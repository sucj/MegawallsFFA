package net.nuggetmc.mw.command

import net.nuggetmc.mw.MegaWalls
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class killEffectCommand : CommandExecutor{

    val plugin = MegaWalls.getInstance()

    override fun onCommand(sender: CommandSender?, cmd: Command?, label: String?, args: Array<String?>?): Boolean {
        if (!(sender is Player)) return true
        val player = sender as Player
        plugin.keMenu.openGUI(player)
        return true;
    }

}