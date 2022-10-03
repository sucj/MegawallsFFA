package net.nuggetmc.mw.special

import net.nuggetmc.mw.MegaWalls
import net.nuggetmc.mw.special.TeamsManager.Team.*
import org.bukkit.entity.Player
import java.math.BigDecimal

class CompassManager {
    var compassTargetMap: MutableMap<Player, TeamsManager.Team?> = HashMap()
    var plugin = MegaWalls.getInstance()
    var tm = plugin.teamsManager
    fun changeTrackingTarget(player: Player) {
        compassTargetMap[player] = if (compassTargetMap[player] == null) tm.getTeamOfPlayer(player) else compassTargetMap.get(player)
            ?.let { nextTeam(it) }
        if(compassTargetMap[player]?.let { plugin.teamsManager.getTeamMembers(it).isEmpty() } == true){
            changeTrackingTarget(player)
        }
    }
    fun getCompassActionBarOfPlayer(player: Player):String{
        compassTargetMap.let { if (it.get(player)==null) {
            it.put(player,tm.getTeamOfPlayer(player))
        } }
           return "Tracking ${compassTargetMap.get(player)?.name}        Distance: ${
               BigDecimal(
                   player.location.distance(
                       player.compassTarget
                   )
               ).setScale(1, BigDecimal.ROUND_HALF_UP).toDouble()} m"
    }

    private fun nextTeam(team: TeamsManager.Team) :TeamsManager.Team {
        return when(team){
            RED -> GREEN
            GREEN -> BLUE
            BLUE -> YELLOW
            YELLOW -> RED
        }
    }
}