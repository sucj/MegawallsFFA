package net.nuggetmc.mw.special;

import net.nuggetmc.mw.MegaWalls;
import net.nuggetmc.mw.enums.EnumTeamIndex;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

public final class CompassManager {
    private Map<Player, TeamsManager.Team> compassTargetMap = new HashMap<>();
    private MegaWalls plugin = MegaWalls.getInstance();
    private TeamsManager tm;

    public CompassManager() {
        this.tm = this.plugin.getTeamsManager();
    }

    public Map<Player, TeamsManager.Team> getCompassTargetMap() {
        return this.compassTargetMap;
    }

    public void setCompassTargetMap(Map<Player, TeamsManager.Team> var1) {
        this.compassTargetMap = var1;
    }

    public MegaWalls getPlugin() {
        return this.plugin;
    }

    public void setPlugin(MegaWalls var1) {
        this.plugin = var1;
    }

    public TeamsManager getTeam() {
        return this.tm;
    }

    public void setTeam(TeamsManager var1) {
        this.tm = var1;
    }

    public void changeTrackingTarget(Player player) {
        Map<Player, TeamsManager.Team> targetMap = this.compassTargetMap;
        TeamsManager.Team team;
        TeamsManager.Team target;
        if (this.compassTargetMap.get(player) == null) {
            team = this.tm.getTeamOfPlayer(player);
        } else {
            target = this.compassTargetMap.get(player);
            if (target != null) {
                team = this.nextTeam(target);
            } else {
                team = null;
            }
        }

        target = team;
        targetMap.put(player, target);
        team = this.compassTargetMap.get(player);
        boolean empty;
        if (team != null) {
            empty = this.plugin.getTeamsManager().getTeamMembers(team).isEmpty();
        } else {
            empty = false;
        }

        if (empty) {
            this.changeTrackingTarget(player);
        }

    }

    public String getCompassActionBarOfPlayer(Player player) {
        Map<Player, TeamsManager.Team> it = this.compassTargetMap;
        if (it.get(player) == null) {
            it.put(player, this.tm.getTeamOfPlayer(player));
        }

        StringBuilder czf = (new StringBuilder()).append("Tracking ");
        TeamsManager.Team team = this.compassTargetMap.get(player);
        String str;
        if (team != null) {
            str = this.tm.getColorOfTeam(team);
        } else {
            str = null;
        }

        czf.append(str).append(ChatColor.BOLD);
        TeamsManager.Team targetTeam = this.compassTargetMap.get(player);
        return czf.append(targetTeam != null ? targetTeam.name() : null).append(ChatColor.RESET).append(ChatColor.BOLD).append("        Distance: ").append((BigDecimal.valueOf(player.getLocation().distance(player.getCompassTarget()))).setScale(1, RoundingMode.HALF_UP).doubleValue()).append(" m").toString();
    }

    private TeamsManager.Team nextTeam(TeamsManager.Team team) {
        TeamsManager.Team colorTeam;
        switch(EnumTeamIndex.EnumTeamIndex[team.ordinal()]) {
            case 1:
                colorTeam = TeamsManager.Team.GREEN;
                break;
            case 2:
                colorTeam = TeamsManager.Team.BLUE;
                break;
            case 3:
                colorTeam = TeamsManager.Team.YELLOW;
                break;
            case 4:
                colorTeam = TeamsManager.Team.RED;
                break;
            default:
                throw new RuntimeException("Employee");
        }

        return colorTeam;
    }
}
