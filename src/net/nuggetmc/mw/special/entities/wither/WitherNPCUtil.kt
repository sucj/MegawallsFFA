package net.nuggetmc.mw.special.entities.wither

import fr.bukkit.effectkill.effect.KillEffect.plugin
import net.citizensnpcs.api.CitizensAPI
import net.citizensnpcs.api.npc.NPC
import net.citizensnpcs.api.trait.TraitInfo
import net.citizensnpcs.trait.HologramTrait
import net.minecraft.server.v1_8_R3.GenericAttributes
import net.minecraft.server.v1_8_R3.NBTTagCompound
import org.bukkit.Location
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftLivingEntity
import org.bukkit.entity.EntityType
import org.bukkit.entity.Wither
import org.bukkit.entity.WitherSkull
import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.util.Vector


object WitherNPCUtil {
    init {
        CitizensAPI.getTraitFactory().registerTrait(
            TraitInfo.create(PlayerKillWitherTrait::class.java)
        )
        CitizensAPI.getTraitFactory().registerTrait(
            TraitInfo.create(PlayerHitWitherTrait::class.java)
        )
    }

    /**
     * 在指定位置生成无击退的 Wither NPC
     *
     * @param location 生成位置
     * @param name 自定义名称，默认为 "Wither"
     * @return 创建好的 Citizens NPC 对象
     */
    fun spawnKnockbackResistantWither(location: Location, name: String = "Wither"): NPC {
        // 1. 获取 Citizens NPC 注册器并创建 NPC
        val npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.WITHER, name)
        npc.isProtected = false
        npc.data().setPersistent(NPC.Metadata.NAMEPLATE_VISIBLE, false)
        // 2. 在指定位置生成 NPC
        npc.spawn(location)


        // 3. 通过安全转换并调用 NMS 设置 100% 击退抗性
        (npc.entity as? CraftLivingEntity)?.handle?.let { nmsEntity ->
            // GenericAttributes.c 对应 1.8.8 R3 中的 generic.knockbackResistance
            nmsEntity.getAttributeInstance(GenericAttributes.c).value = 1.0
            val nbt = NBTTagCompound()

            // 将当前实体的 NBT 存入 compound 中
            nmsEntity.c(nbt)

            // 写入 NoAI 标签 (1b 表示禁用 AI)
            nbt.setInt("NoAI", 1)
            nmsEntity.f(nbt)
        }
        (npc.entity as CraftLivingEntity).maxHealth= 800.0
        (npc.entity as CraftLivingEntity).health=800.0
        (npc.entity as CraftLivingEntity).customName = name
        (npc.entity as CraftLivingEntity).isCustomNameVisible = true
        (npc.entity as CraftLivingEntity).maximumNoDamageTicks = 5
        addHologram(npc,name)
        npc.addTrait(PlayerKillWitherTrait::class.java)
        npc.addTrait(PlayerHitWitherTrait::class.java)
        return npc
    }
    fun addHologram(npc: NPC, name: String) {
        // 获取或添加 HologramTrait（如果没有会自动创建）
        val hologram = npc.getOrAddTrait(HologramTrait::class.java)

        // 清空现有全息文字（可选）
        hologram.clear()

        // 添加永久显示的行（会保存）
        hologram.addLine(name)

        // 设置行间距（可选，单位是方块高度）
        hologram.lineHeight = 0.28

        // 添加临时全息文字（不会保存，ticks 后消失）
        // hologram.addTemporaryLine("§c临时提示", 100) // 显示 5 秒
    }
    fun shootCustomSkull(npc: NPC?, targetLocation: Location) {
        if (npc == null || !npc.isSpawned || (npc.entity !is Wither)) {
            return
        }

        val wither = npc.entity as Wither


        // 计算骷髅头生成点（凋灵头部稍微靠前的位置）
        val spawnLoc = wither.eyeLocation.add(wither.location.direction.multiply(1.2))

        // 计算朝向目标位置的方向向量
        val direction: Vector = targetLocation.toVector().subtract(spawnLoc.toVector()).normalize()

        // 在指定位置生成骷髅头
        val skull = wither.world.spawn(spawnLoc, WitherSkull::class.java)


        // 设置发射源（确保伤害和杀手统计归属于该凋灵）
        skull.shooter = wither


        // 设置方向与速度
        skull.direction = direction
        skull.velocity = direction.multiply(1.2)
        skull.setMetadata("no_terrain_damage", FixedMetadataValue(plugin, true))

        // 是否为蓝色骷髅头 (Charged)
        skull.isCharged = true
    }
}