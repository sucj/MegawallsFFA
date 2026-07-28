package net.nuggetmc.mw.special.specialItems

import net.md_5.bungee.api.ChatColor
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment

object Terminator : AbstractSpecialItem() {
    override val id: String
        get() = "term"
    override val material: Material
        get() = Material.BOW
    override val enchantments: Map<Enchantment, Int>
        get() = mapOf(Enchantment.DAMAGE_ALL to 1)
    override val displayName: String
        get() = ChatColor.GOLD.toString() + "Spiritual Terminator"
    override val lore: ArrayList<String>
        get() {
            val lore = ArrayList<String>()
            lore.add("")
            lore.add(ChatColor.GOLD.toString() + ChatColor.BOLD + "LEGENDARY BOW")
            return lore
        }
}