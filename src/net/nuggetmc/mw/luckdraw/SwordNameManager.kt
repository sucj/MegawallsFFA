package net.nuggetmc.mw.luckdraw

import net.nuggetmc.mw.MegaWalls
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class SwordNameManager : Listener {
    private val data: MutableMap<Player, String?> = HashMap()
    private val plugin = MegaWalls.getInstance()
    fun save(player: Player) {
        if (data[player] == null) {
            return
        }
        plugin.config["useSwordName." + player.name] = data[player]
        plugin.saveConfig()
    }

    operator fun get(player: Player): String? {
        return data[player]
    }

    fun select(player: Player, name: String?) {
        data[player] = name
        save(player)
    }

    @EventHandler
    fun onJoin(e: PlayerJoinEvent) {
        plugin.config.getConfigurationSection("useSwordName") ?: return
        val name: String
        name = try {
            plugin.config.getString("useSwordName." + e.player.name)
        } catch (exception: Exception) {
            return
        }
        data[e.player] = name
        save(e.player)
        plugin.saveConfig()
    }
}