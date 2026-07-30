package net.nuggetmc.mw.guis

import net.md_5.bungee.api.ChatColor
import net.nuggetmc.mw.mwclass.MWClassMenu
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

class GUIPresets : Listener {
    val menuTitle= "Edit Presets Diamond"
    fun openGUI(player: Player) {
        /*val inv = Bukkit.createInventory(null, 54, menuTitle)

        var n = 1

        for (entry in manager.getClasses().entries) {
            val item: ItemStack? = generateClassInfo(entry.value)
            inv.setItem(n + 9 + 2 * ((n - 1) / 7), item)

            n++
        }

        inv.setItem(49, createClose())
        player.openInventory(inv)*/
    }

    @EventHandler
    fun click(event: InventoryClickEvent) {
        val inv = event.clickedInventory ?: return

        val invName = inv.name
        if (invName != menuTitle) return

        event.isCancelled = true

        val item = event.currentItem ?: return

        val meta = item.itemMeta ?: return

        val name = meta.displayName ?: return

        val player = event.whoClicked as Player

        //select(player, ChatColor.stripColor(name))
    }


}