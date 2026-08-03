package net.nuggetmc.mw.special.entities.wither

import net.citizensnpcs.api.event.NPCDeathEvent
import net.citizensnpcs.api.trait.Trait
import net.citizensnpcs.api.trait.TraitName
import net.minecraft.server.v1_8_R3.NBTTagCompound
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.inventory.ItemStack

/**
 * 监听 NPC 被玩家击杀的 Trait
 */
@TraitName("player_kill_wither_listener") // Trait 的唯一标识名称
class PlayerKillWitherTrait : Trait("player_kill_wither_listener") {

    @EventHandler
    fun onNPCDeath(event: NPCDeathEvent) {
        // 1. 过滤：确保触发死亡事件的 NPC 是挂载了当前 Trait 的 NPC
        if (event.npc != this.npc) return

        // 2. 获取 NPC 对应的 LivingEntity 实体
        val entity = event.npc.entity as? LivingEntity ?: return

        // 3. 获取击杀者（Bukkit 会自动计算最后造成致命伤害的 Player）
        val killer: Player = entity.killer ?: return

        // 4. 执行你自定义的逻辑
        handleNPCKilledByPlayer(killer)
    }

    /**
     * 当 NPC 被玩家击杀时的处理逻辑
     */
    private fun handleNPCKilledByPlayer(killer: Player) {
        convertAllEquipmentAndSwordsToDiamond(killer)
    }
    fun convertAllEquipmentAndSwordsToDiamond(player: Player) {
        var updated = false

        // ================= 1. 处理身上的盔甲槽位 =================
        val armorContents: Array<ItemStack?> = player.inventory.armorContents
        val diamondArmors = arrayOf(
            Material.DIAMOND_BOOTS,      // Index 0: 靴子
            Material.DIAMOND_LEGGINGS,   // Index 1: 护腿
            Material.DIAMOND_CHESTPLATE, // Index 2: 胸甲
            Material.DIAMOND_HELMET      // Index 3: 头盔
        )
        var invokedTimes = 0
        for (i in armorContents.indices) {
            if (invokedTimes>=5){
                break
            }
            val item = armorContents[i] ?: continue
            if (item.type == Material.AIR) continue

            // 转换材质并注入 NBT
            armorContents[i] = applyDiamondAndNBT(item, diamondArmors[i])
            invokedTimes++
            updated = true
        }

        if (updated) {
            player.inventory.armorContents = armorContents
        }

        // ================= 2. 处理背包中的所有物品（快捷栏 + 主背包） =================
        val contents: Array<ItemStack?> = player.inventory.contents

        for (i in contents.indices) {
            val item = contents[i] ?: continue
            if (item.type == Material.AIR) continue

            // 如果是任意材质的剑（木/石/铁/金/钻石），都转换为钻石剑并添加 Tag
            if (isSword(item.type)) {
                contents[i] = applyDiamondAndNBT(item, Material.DIAMOND_SWORD)
                updated = true
            }
        }

        if (updated) {
            player.inventory.contents = contents
        }

        // ================= 3. 同步至客户端渲染 =================
        if (updated) {
            player.updateInventory()
        }
    }

    /**
     * 通用核心逻辑：修改物品材质，并注入自定义 NBTTagCompound
     */
    private fun applyDiamondAndNBT(item: ItemStack, targetMaterial: Material): ItemStack {
        // 1. 直接修改 Bukkit ItemStack 的材质（保持 ItemMeta 不变）
        item.type = targetMaterial

        // 2. 转为 NMS 物品以操作深层 NBT
        val nmsItem = CraftItemStack.asNMSCopy(item)
        val tag = nmsItem.tag ?: NBTTagCompound()

        // ------------------ 在这里写入你的自定义 NBT 数据 ------------------
        tag.setBoolean("megaWalls", true)
        // ------------------------------------------------------------------

        nmsItem.tag = tag

        // 3. 转换回 Bukkit ItemStack 并返回
        return CraftItemStack.asBukkitCopy(nmsItem)
    }

    /**
     * 判断 Material 是否为任意材质的剑
     */
    private fun isSword(material: Material): Boolean {
        return when (material) {
            Material.WOOD_SWORD,
            Material.STONE_SWORD,
            Material.IRON_SWORD,
            Material.GOLD_SWORD,
            Material.DIAMOND_SWORD -> true
            else -> false
        }
    }
}