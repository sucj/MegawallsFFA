package net.nuggetmc.mw.command

import net.nuggetmc.mw.MegaWalls
import net.nuggetmc.mw.luckdraw.SwordLuckDraw
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class LuckDrawCommand : CommandExecutor {

    val plugin = MegaWalls.getInstance()
    val coinsMgr = plugin.coinsManager
    var luckDrawPrice: Int? = null

    override fun onCommand(sender: CommandSender?, cmd: Command?, label: String?, args: Array<String?>?): Boolean {
        if (luckDrawPrice == null) {
            luckDrawPrice = MegaWalls.getInstance().swordLuckDrawPrice
        }
        if (sender !is Player) return true
        if (args!!.isNotEmpty()) {
            var times = 0
            try {
                times = Integer.parseInt(args[0])
            } catch (e: Exception) {
                //
            }
            if (times == 0) {
                sender.sendMessage("YOUR NUMBER MUST BE A VALID INTEGER AND BIGGER THAN 0!")
                return true
            }
            if (coinsMgr.get(sender) < luckDrawPrice!!) {
                sender.sendMessage("Not enough coins ! You need $luckDrawPrice to do this!")
                return true
            } else {
                SwordLuckDraw.doLuckDraw(sender, Integer.parseInt(args[0]))
            }
        } else {
            SwordLuckDraw.doLuckDraw(sender)
        }
        return true
    }

}