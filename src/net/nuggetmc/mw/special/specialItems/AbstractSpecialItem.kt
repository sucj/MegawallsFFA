package net.nuggetmc.mw.special.specialItems

import net.minecraft.server.v1_8_R3.NBTTagCompound
import net.nuggetmc.mw.utils.ItemUtils
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack

abstract class AbstractSpecialItem {
    abstract val id: String
    abstract val material: Material
    abstract val enchantments:Map<Enchantment, Int>
    abstract val displayName: String
    abstract val lore: ArrayList<String>
    open val unbreakable = true
    open fun check(itemStack: ItemStack): Boolean{
        val nmsItem = CraftItemStack.asNMSCopy(itemStack)
        if (nmsItem == null) return false

        val compound = if (nmsItem.hasTag()) nmsItem.getTag() else NBTTagCompound()
        return compound.getBoolean(id)
    }
    open fun buildItem(): ItemStack {
        val item = ItemStack(material)

        item.addUnsafeEnchantments(enchantments)


        val meta = item.getItemMeta()
        meta.setDisplayName(displayName)



        meta.setLore(lore)
        meta.spigot().setUnbreakable(unbreakable)

        item.setItemMeta(meta)
        val nmsItem = CraftItemStack.asNMSCopy(item)
        val compound = if (nmsItem.hasTag()) nmsItem.getTag() else NBTTagCompound()

        compound.setBoolean(id, true)
        nmsItem.setTag(compound)

        return ItemUtils.toMWItem(CraftItemStack.asBukkitCopy(nmsItem))
    }
}