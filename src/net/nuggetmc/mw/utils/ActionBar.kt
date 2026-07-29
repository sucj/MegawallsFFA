package net.nuggetmc.mw.utils

import net.minecraft.server.v1_8_R3.IChatBaseComponent
import net.minecraft.server.v1_8_R3.PacketPlayOutChat
import net.nuggetmc.mw.MegaWalls
import net.nuggetmc.mw.special.specialItems.MegaBreaker
import org.bukkit.ChatColor
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer
import org.bukkit.entity.Player

object ActionBar {
    var instance: MegaWalls = MegaWalls.getInstance()

    @JvmStatic
    fun send(player: Player, message: String?) {
        val packet =
            PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + message + "\"}"), 2.toByte())
        (player as CraftPlayer).getHandle().playerConnection.sendPacket(packet)
    }

    @JvmStatic
    fun clear(player: Player) {
        send(player, "")
    }

    @JvmStatic
    fun joinActionBar(vararg s: String?): String? {
        if (s.size == 1) {
            return s[0]
        }
        val stringBuilder = StringBuilder()
        for (i in s.indices) {
            stringBuilder.append(s[i])
            if (i + 1 < s.size) {
                stringBuilder.append("      ")
            }
        }
        return stringBuilder.toString()
    }

    @JvmStatic
    fun buildActionBar(player: Player): String? {
        var originalActionBar = instance.getClassManager().get(player).getActionBar(player)
        if (originalActionBar == null) {
            if (MegaBreaker.check(player.itemInHand)) {
                val currentCharges = MegaBreaker.getMegaBreakerCharges(player.itemInHand)
                return ""+ChatColor.GOLD+ ChatColor.BOLD+"${currentCharges}/50 ↖"
            }
        } else {
            if (MegaBreaker.check(player.itemInHand)) {
                val currentCharges = MegaBreaker.getMegaBreakerCharges(player.itemInHand)
                originalActionBar+="      "+ ChatColor.GOLD+ ChatColor.BOLD+"${currentCharges}/50 ↖"
            }




        }
        return originalActionBar
    }
}
