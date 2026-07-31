package net.nuggetmc.mw.special.specialItems

import net.md_5.bungee.api.ChatColor
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment

object MK2Stick : AbstractSpecialItem() {
    override val id: String
        get() = "mk2stick"
    override val material: Material
        get() = Material.CARROT_STICK
    override val enchantments: Map<Enchantment, Int>
        get() = mapOf(Enchantment.DURABILITY to 10)
    override val displayName: String
        get() = ChatColor.DARK_PURPLE.toString() + "Nothing on a stick"
    override val lore: ArrayList<String>
        get() = arrayListOf("", ChatColor.DARK_PURPLE.toString() + ChatColor.BOLD + "EPIC", ChatColor.GRAY.toString()+"Press Drop to accelerate your pig.","Cooldown:20s.")
}