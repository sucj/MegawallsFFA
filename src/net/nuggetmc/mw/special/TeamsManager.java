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

@SuppressWarnings("unused")
public class TeamsManager implements Listener {
    private final HashMap<Player, Team> teamsMap = new HashMap<>();
    private final MegaWalls plugin = MegaWalls.getInstance();

    @EventHandler
    public final void onRespawn(PlayerRespawnEvent e) {
        this.removePlayerTeam(e.getPlayer());
    }

    @EventHandler
    public final void onLeave(PlayerQuitEvent e) {
        this.removePlayerTeam(e.getPlayer());
    }

    public final List<Double> getSpawnLocOfPlayer(Player player) {
        Team team = this.getTeamOfPlayer(player);
        List<Double> spawn;
        switch(team == null ? -1 : EnumTeamIndex.EnumTeamIndex[team.ordinal()]) {
            case -1:
                throw new RuntimeException("WOCENIMA!!!!");
            case 0:
            default:
                throw new RuntimeException("Meta");
            case 1:
                spawn = this.plugin.redspawn;
                break;
            case 2:
                spawn = this.plugin.greenspawn;
                break;
            case 3:
                spawn = this.plugin.bluespawn;
                break;
            case 4:
                spawn = this.plugin.yellowspawn;
        }

        return spawn;
    }
    
    public final String getSymbolOfTeam(Team team) {
        String ColorSuffix;
        switch(EnumTeamIndex.EnumTeamIndex[team.ordinal()]) {
            case 1:
                ColorSuffix = ChatColor.RED + "[R]" + ChatColor.RESET;
                break;
            case 2:
                ColorSuffix = ChatColor.GREEN + "[G]" + ChatColor.RESET;
                break;
            case 3:
                ColorSuffix = ChatColor.BLUE + "[B]" + ChatColor.RESET;
                break;
            case 4:
                ColorSuffix = ChatColor.YELLOW + "[Y]" + ChatColor.RESET;
                break;
            default:
                throw new RuntimeException("List");
        }

        return ColorSuffix;
    }


    public final String getSymbolOfTeamRaw(Team team) {
        String suffix;
        switch(EnumTeamIndex.EnumTeamIndex[team.ordinal()]) {
            case 1:
                suffix = "[R]";
                break;
            case 2:
                suffix = "[G]";
                break;
            case 3:
                suffix = "[B]";
                break;
            case 4:
                suffix = "[Y]";
                break;
            default:
                throw new RuntimeException("Reborn");
        }

        return suffix;
    }
    
    public final String getColorOfTeam(Team team) {
        String colorCode;
        switch(EnumTeamIndex.EnumTeamIndex[team.ordinal()]) {
            case 1:
                colorCode = ChatColor.RED.toString();
                break;
            case 2:
                colorCode = ChatColor.GREEN.toString();
                break;
            case 3:
                colorCode = ChatColor.BLUE.toString();
                break;
            case 4:
                colorCode = ChatColor.YELLOW.toString();
                break;
            default:
                throw new RuntimeException("FileManager");
        }

        return colorCode;
    }

    public final boolean isOnSameTeam(Player player, Player player1) {
        if (player.getUniqueId()==player1.getUniqueId()){
            return true;
        }
        if (this.plugin.getCombatManager().isInCombat(player) && this.plugin.getCombatManager().isInCombat(player1)) {
            return this.getTeamOfPlayer(player) == this.getTeamOfPlayer(player1);
        } else {
            return false;
        }
    }

    public final boolean isOnSameTeam(Player[] array) {
        if (array.length == 1) {
            return true;
        } else {
            boolean allSameTeam = false;
            int index = 0;
            int lengths = array.length;
            while(true) {
                if (this.isOnSameTeam(array[index], array[index + 1])) {
                    allSameTeam = true;
                    break;
                }

                if (index == lengths) {
                    break;
                }

                ++index;
            }

            return allSameTeam;
        }
    }

    public final void movePlayerToTeam(Player player, Team team) {
        this.removePlayerTeam(player);
        this.addTeam(player, team);
    }
    
    @Deprecated
    public final Team randomTeam(Player player) {
        int num = MathUtils.randomIntInRange(0, 3);
        Team teamColor;
        switch(num) {
            case 0:
                teamColor = Team.RED;
                break;
            case 1:
                teamColor = Team.GREEN;
                break;
            case 2:
                teamColor = Team.BLUE;
                break;
            case 3:
                teamColor = Team.YELLOW;
                break;
            default:
                teamColor = null;
        }

        Team team = teamColor;
        if (team != null) {
            this.addTeam(player, team);
        }

        return team;
    }
    
    public final Team putTeam(Player player) {
        ArrayList<Team> list = new ArrayList<>();
        Collections.addAll(list, Team.values());
        Team team = list.get(0);
        this.addTeam(player, team);
        return team;
    }

    public final void removePlayerTeam(Player player) {
        this.teamsMap.remove(player);
    }

    public final void addTeam(Player player, Team team) {
        this.teamsMap.put(player, team);
    }
    
    public final Team getTeamOfPlayer(Player player) {
        return this.teamsMap.get(player);
    }
    
    public final HashSet<Player> getTeamMembers(Team team) {
        HashSet<Player> result = new HashSet<>();

        for (Player player : this.teamsMap.keySet()) {
            if (this.teamsMap.get(player) == team) {
                result.add(player);
            }
        }

        return result;
    }

    public enum Team {
        RED,
        GREEN,
        BLUE,
        YELLOW;
    }
}
