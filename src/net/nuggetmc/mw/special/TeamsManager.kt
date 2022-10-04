package net.nuggetmc.mw.special

import net.nuggetmc.mw.MegaWalls
import net.nuggetmc.mw.special.TeamsManager.Team.*
import net.nuggetmc.mw.utils.MathUtils
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerRespawnEvent

class TeamsManager :Listener {
    private var teamsMap= HashMap<Player, Team>();
    private var plugin=MegaWalls.getInstance()
    /**
     * clear a player's team on respawn.
     */
    @EventHandler
    fun onRespawn(e:PlayerRespawnEvent){
        removePlayerTeam(e.player)
    }
    @EventHandler
    fun onLeave(e:PlayerQuitEvent){
        removePlayerTeam(e.player)
    }
    fun getSpawnLocOfPlayer(player: Player):List<Double>{
        return when(getTeamOfPlayer(player)){
            RED ->plugin.redspawn
            GREEN ->plugin.greenspawn
            BLUE ->plugin.bluespawn
            YELLOW ->plugin.yellowspawn
            null -> throw RuntimeException("WOCENIMA!!!!")
        }
    }
    fun getSymbolOfTeam(team: Team):String{
        return when(team){
            RED ->  (ChatColor.RED.toString()+"[R]"+ChatColor.RESET.toString())
            GREEN ->(ChatColor.GREEN.toString()+"[G]"+ChatColor.RESET.toString())
            BLUE ->(ChatColor.BLUE.toString()+"[B]"+ChatColor.RESET.toString())
            YELLOW ->(ChatColor.YELLOW.toString()+"[Y]"+ChatColor.RESET.toString())
        }
    }
    fun getSymbolOfTeamRaw(team: Team):String{
        return when(team){
            RED ->  ("[R]")
            GREEN ->("[G]")
            BLUE ->("[B]")
            YELLOW ->("[Y]")
        }
    }
    fun getColorOfTeam(team: Team):String{
        return when(team){
            RED ->  (ChatColor.RED.toString())
            GREEN ->(ChatColor.GREEN.toString())
            BLUE ->(ChatColor.BLUE.toString())
            YELLOW ->(ChatColor.YELLOW.toString())
        }
    }
    /**
     * tell if players are in the same team.
     */
    fun isOnSameTeam(player: Player,player1: Player):Boolean{
        if((!plugin.combatManager.isInCombat(player))||(!plugin.combatManager.isInCombat(player1))) return false;
        return getTeamOfPlayer(player)==getTeamOfPlayer(player1)
    }
    fun isOnSameTeam(array:Array<Player>):Boolean{
        if (array.size==1){
            return true;
        }
        var allSameTeam=false;
        for(index in 0..array.size){
            if (isOnSameTeam(array.get(index),array.get(index+1))) {
                allSameTeam = true;
                break
            }
        }
        return allSameTeam


    }

    /**
     * move player to a team.
     */
    fun movePlayerToTeam(player: Player, team: Team){
        removePlayerTeam(player)
        addTeam(player,team)
    }
    /**
     * randomly put a player in a team.
     */
    @Deprecated("no longer using this in selecting team")
    fun randomTeam(player: Player): Team? {
       val num= MathUtils.randomIntInRange(0,3)
        val team=when(num){
            0-> RED
            1-> GREEN
            2-> BLUE
            3-> YELLOW
            else -> {null}
        }
        team?.let { addTeam(player, it) }
        return team
    }
    fun putTeam(player: Player): Team {
       var list= ArrayList<Team>()
        list.addAll(Team.values())
        list.sortWith(java.util.Comparator { team, t1 ->
            if (MegaWalls.getInstance().teamsManager.getTeamMembers(team!!).size < MegaWalls.getInstance().teamsManager.getTeamMembers(
                    t1!!
                ).size
            ) {
                return@Comparator -1
            } else if (MegaWalls.getInstance().teamsManager.getTeamMembers(team).size > MegaWalls.getInstance().teamsManager.getTeamMembers(
                    t1
                ).size
            ) {
                return@Comparator 1
            }
            0
        })
       addTeam(player, list[0])
        return list[0]
    }
    /**
     * clear a player's team
     */
    fun removePlayerTeam(player: Player) {
        if (teamsMap.containsKey(player)){
            teamsMap.remove(player)
        }
    }

    /**
     * add a player in a team directly.
     */
    fun addTeam(player: Player,team:Team) {
        teamsMap.put(player,team)
    }

    /**
     * get team of player
     */
    fun getTeamOfPlayer(player: Player): Team? {
        return teamsMap.get(player);
    }

    /**
     * get all the members of a team.
     */
    fun getTeamMembers(team:Team):HashSet<Player>{
        var result= HashSet<Player>();
        for (player:Player in teamsMap.keys){
            if (teamsMap.get(player)==team) result.add(player)
        }
        return result;
    }
    enum class Team{
        RED,
        GREEN,
        BLUE,
        YELLOW;
    }
}