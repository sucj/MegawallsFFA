package net.nuggetmc.mw.special.specialItems

import net.md_5.bungee.api.ChatColor
import net.minecraft.server.v1_8_R3.BlockPosition
import net.minecraft.server.v1_8_R3.NBTTagCompound
import net.nuggetmc.mw.utils.ItemUtils
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack
import org.bukkit.enchantments.Enchantment
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack

object MegaBreaker : AbstractSpecialItem() {
    override val id: String
        get() = "mega_breaker"
    override val material: Material
        get() = Material.DIAMOND_PICKAXE
    override val enchantments: Map<Enchantment, Int>
        get() = mapOf(Enchantment.DAMAGE_ALL to 1)
    override val displayName: String
        get() = ChatColor.GOLD.toString() + "Mega Breaker"
    override val lore: ArrayList<String>
        get() = arrayListOf("大姑奶奶干饭王")
    //this won't be used, actually.

    override fun buildItem(): ItemStack {
        error("请调用buildMegaBreaker方法")
    }
    fun buildMegaBreaker(charge : Int): ItemStack{
        val item = ItemStack(material)
        val lor: MutableList<String?> = java.util.ArrayList<String?>()


        item.addUnsafeEnchantments(enchantments)

        val meta = item.getItemMeta()
        meta.setDisplayName(displayName)


        lor.add(ChatColor.GRAY.toString() + "Charges : " + charge + "/50")
        lor.add("")
        lor.add(ChatColor.GOLD.toString() + ChatColor.BOLD + "LEGENDARY PICKAXE")


        meta.setLore(lor)
        meta.spigot().setUnbreakable(unbreakable)

        item.setItemMeta(meta)
        val nmsItem = CraftItemStack.asNMSCopy(item)
        val compound = if (nmsItem.hasTag()) nmsItem.getTag() else NBTTagCompound()

        compound.setBoolean(id, true)
        compound.setInt("charges", charge)
        nmsItem.setTag(compound)

        return ItemUtils.toMWItem(CraftItemStack.asBukkitCopy(nmsItem))
    }

    @EventHandler
    fun onMegaBreaker(e: PlayerInteractEvent) {
        val p = e.getPlayer()

        if (!e.getAction().name.contains("LEFT")) return
        if (p.getItemInHand() == null) return
        if (!check(p.itemInHand)) return

        if (getMegaBreakerCharges(p.getItemInHand()) <= 0) return

        val clickedBlock = e.getClickedBlock()
        if (clickedBlock == null || clickedBlock.getType() == Material.AIR) return
        if (clickedBlock.getType() == Material.BEDROCK) return
        if (clickedBlock.getType() == Material.BARRIER) return
        (p as CraftPlayer).getHandle().playerInteractManager.breakBlock(
            BlockPosition(clickedBlock.getX(), clickedBlock.getY(), clickedBlock.getZ())
        )
        p.itemInHand = addMegaBreakerCharges(p.getItemInHand(), -1)
    }
    fun getMegaBreakerCharges(itemStack: ItemStack): Int {
        val nmsItem = CraftItemStack.asNMSCopy(itemStack)
        val compound = if (nmsItem.hasTag()) nmsItem.getTag() else NBTTagCompound()
        if (!check(itemStack)) {
            return 0
        }
        return compound.getInt("charges")
    }

    fun addMegaBreakerCharges(itemStack: ItemStack, amount: Int): ItemStack? {
        if (!check(itemStack)) {
            return null
        }
        val charge = getMegaBreakerCharges(itemStack)
        return buildMegaBreaker(charge + amount)
    }
}