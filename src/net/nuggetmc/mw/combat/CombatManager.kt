package net.nuggetmc.mw.combat

import net.nuggetmc.mw.MegaWalls
import net.nuggetmc.mw.utils.PlayerSafeSet
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerJoinEvent

object CombatManager : Listener {
    init {

    }

    fun addInCombat(player: Player) {
        inCombatPlayers.add(player)
    }

    fun isInCombat(player: Player?): Boolean {
        return MegaWalls.getInstance().getClassManager().isMW(player)
    }

    fun removeInCombat(player: Player?) {
        if (isInCombat(player)) {
            inCombatPlayers.remove(player)
        }
    }

    @EventHandler
    fun onDeath(e: PlayerDeathEvent) {
        removeInCombat(e.getEntity())
    }
    @EventHandler
    fun onJoin(e: PlayerJoinEvent){
        removeInCombat(e.player)
    }
    var inCombatPlayers = PlayerSafeSet()
}
