package net.nuggetmc.mw.special;

import net.nuggetmc.mw.MegaWalls;
import net.nuggetmc.mw.enums.EnumTeamIndex;
import net.nuggetmc.mw.utils.MathUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.*;

public class TeamsManager implements Listener {
    private HashMap<Player, Team> teamsMap = new HashMap();
    private MegaWalls plugin = MegaWalls.getInstance();

    @EventHandler
    public final void onRespawn(PlayerRespawnEvent e) {
        Player var10001 = e.getPlayer();
        this.removePlayerTeam(var10001);
    }

    @EventHandler
    public final void onLeave(PlayerQuitEvent e) {
        Player var10001 = e.getPlayer();
        this.removePlayerTeam(var10001);
    }

    public final List<Double> getSpawnLocOfPlayer(Player player) {
        TeamsManager.Team var10000 = this.getTeamOfPlayer(player);
        List var2;
        switch(var10000 == null ? -1 : EnumTeamIndex.EnumTeamIndex[var10000.ordinal()]) {
            case -1:
                throw new RuntimeException("WOCENIMA!!!!");
            case 0:
            default:
                throw new RuntimeException("Meta");
            case 1:
                var2 = this.plugin.redspawn;
                //Intrinsics.checkNotNullExpressionValue(var2, "plugin.redspawn");
                break;
            case 2:
                var2 = this.plugin.greenspawn;
                //Intrinsics.checkNotNullExpressionValue(var2, "plugin.greenspawn");
                break;
            case 3:
                var2 = this.plugin.bluespawn;
                //Intrinsics.checkNotNullExpressionValue(var2, "plugin.bluespawn");
                break;
            case 4:
                var2 = this.plugin.yellowspawn;
                //Intrinsics.checkNotNullExpressionValue(var2, "plugin.yellowspawn");
        }

        return var2;
    }
    
    public final String getSymbolOfTeam(TeamsManager.Team team) {
        //Intrinsics.checkNotNullParameter(team, "team");
        String var10000;
        switch(EnumTeamIndex.EnumTeamIndex[team.ordinal()]) {
            case 1:
                var10000 = ChatColor.RED.toString() + "[R]" + ChatColor.RESET;
                break;
            case 2:
                var10000 = ChatColor.GREEN.toString() + "[G]" + ChatColor.RESET;
                break;
            case 3:
                var10000 = ChatColor.BLUE.toString() + "[B]" + ChatColor.RESET;
                break;
            case 4:
                var10000 = ChatColor.YELLOW.toString() + "[Y]" + ChatColor.RESET;
                break;
            default:
                throw new RuntimeException("List");
        }

        return var10000;
    }
    
    public final String getSymbolOfTeamRaw(TeamsManager.Team team) {
        //Intrinsics.checkNotNullParameter(team, "team");
        String var10000;
        switch(EnumTeamIndex.EnumTeamIndex[team.ordinal()]) {
            case 1:
                var10000 = "[R]";
                break;
            case 2:
                var10000 = "[G]";
                break;
            case 3:
                var10000 = "[B]";
                break;
            case 4:
                var10000 = "[Y]";
                break;
            default:
                throw new RuntimeException("Reborn");
        }

        return var10000;
    }
    
    public final String getColorOfTeam(TeamsManager.Team team) {
        //Intrinsics.checkNotNullParameter(team, "team");
        String var10000;
        switch(EnumTeamIndex.EnumTeamIndex[team.ordinal()]) {
            case 1:
                var10000 = ChatColor.RED.toString();
                //Intrinsics.checkNotNullExpressionValue(var10000, "RED.toString()");
                break;
            case 2:
                var10000 = ChatColor.GREEN.toString();
                //Intrinsics.checkNotNullExpressionValue(var10000, "GREEN.toString()");
                break;
            case 3:
                var10000 = ChatColor.BLUE.toString();
                //Intrinsics.checkNotNullExpressionValue(var10000, "BLUE.toString()");
                break;
            case 4:
                var10000 = ChatColor.YELLOW.toString();
                //Intrinsics.checkNotNullExpressionValue(var10000, "YELLOW.toString()");
                break;
            default:
                throw new RuntimeException("FileManager");
        }

        return var10000;
    }

    public final boolean isOnSameTeam(Player player, Player player1) {
        //Intrinsics.checkNotNullParameter(player, "player");
        //Intrinsics.checkNotNullParameter(player1, "player1");
        if (this.plugin.getCombatManager().isInCombat(player) && this.plugin.getCombatManager().isInCombat(player1)) {
            return this.getTeamOfPlayer(player) == this.getTeamOfPlayer(player1);
        } else {
            return false;
        }
    }

    public final boolean isOnSameTeam(Player[] array) {
        //Intrinsics.checkNotNullParameter(array, "array");
        if (array.length == 1) {
            return true;
        } else {
            boolean allSameTeam = false;
            int index = 0;
            int var4 = array.length;
            if (index <= var4) {
                while(true) {
                    if (this.isOnSameTeam(array[index], array[index + 1])) {
                        allSameTeam = true;
                        break;
                    }

                    if (index == var4) {
                        break;
                    }

                    ++index;
                }
            }

            return allSameTeam;
        }
    }

    public final void movePlayerToTeam(Player player, TeamsManager.Team team) {
        //Intrinsics.checkNotNullParameter(player, "player");
        //Intrinsics.checkNotNullParameter(team, "team");
        this.removePlayerTeam(player);
        this.addTeam(player, team);
    }
    
    @Deprecated
    public final TeamsManager.Team randomTeam(Player player) {
        //Intrinsics.checkNotNullParameter(player, "player");
        int num = MathUtils.randomIntInRange(0, 3);
        TeamsManager.Team var10000;
        switch(num) {
            case 0:
                var10000 = TeamsManager.Team.RED;
                break;
            case 1:
                var10000 = TeamsManager.Team.GREEN;
                break;
            case 2:
                var10000 = TeamsManager.Team.BLUE;
                break;
            case 3:
                var10000 = TeamsManager.Team.YELLOW;
                break;
            default:
                var10000 = (TeamsManager.Team)null;
        }

        TeamsManager.Team team = var10000;
        if (team != null) {
            this.addTeam(player, team);
        }

        return team;
    }
    
    public final TeamsManager.Team putTeam(Player player) {
        //Intrinsics.checkNotNullParameter(player, "player");
        ArrayList list = new ArrayList();
        Collections.addAll((Collection)list, TeamsManager.Team.values());
        Object var10002 = list.get(0);
        //Intrinsics.checkNotNullExpressionValue(var10002, "list[0]");
        this.addTeam(player, (TeamsManager.Team)var10002);
        Object var10000 = list.get(0);
        //Intrinsics.checkNotNullExpressionValue(var10000, "list[0]");
        return (TeamsManager.Team)var10000;
    }

    public final void removePlayerTeam(Player player) {
        //Intrinsics.checkNotNullParameter(player, "player");
        if (this.teamsMap.containsKey(player)) {
            this.teamsMap.remove(player);
        }

    }

    public final void addTeam(Player player, TeamsManager.Team team) {
        //Intrinsics.checkNotNullParameter(player, "player");
        //Intrinsics.checkNotNullParameter(team, "team");
        this.teamsMap.put(player, team);
    }
    
    public final TeamsManager.Team getTeamOfPlayer(Player player) {
        //Intrinsics.checkNotNullParameter(player, "player");
        return (TeamsManager.Team)this.teamsMap.get(player);
    }
    
    public final HashSet<Player> getTeamMembers(TeamsManager.Team team) {
        //Intrinsics.checkNotNullParameter(team, "team");
        HashSet result = new HashSet();
        Iterator var3 = this.teamsMap.keySet().iterator();

        while(var3.hasNext()) {
            Object var10000 = var3.next();
            //Intrinsics.checkNotNullExpressionValue(var10000, "teamsMap.keys");
            Player player = (Player)var10000;
            if (this.teamsMap.get(player) == team) {
                result.add(player);
            }
        }

        return result;
    }

    private static final int putTeam$lambda_1/* $FF was: putTeam$lambda-1*/(TeamsManager.Team team, TeamsManager.Team t1) {
        TeamsManager var10000 = MegaWalls.getInstance().getTeamsManager();
        //Intrinsics.checkNotNull(team);
        int var2 = var10000.getTeamMembers(team).size();
        TeamsManager var10001 = MegaWalls.getInstance().getTeamsManager();
        //Intrinsics.checkNotNull(t1);
        if (var2 < var10001.getTeamMembers(t1).size()) {
            return -1;
        } else {
            return MegaWalls.getInstance().getTeamsManager().getTeamMembers(team).size() > MegaWalls.getInstance().getTeamsManager().getTeamMembers(t1).size() ? 1 : 0;
        }
    }




    public enum Team {
        RED,
        GREEN,
        BLUE,
        YELLOW;
    }
}
