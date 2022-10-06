package net.nuggetmc.mw.special;

import net.nuggetmc.mw.MegaWalls;
import net.nuggetmc.mw.enums.EnumTeamIndex;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public final class CompassManager {
    private Map<Player, TeamsManager.Team> compassTargetMap = (Map)(new HashMap());
    private MegaWalls plugin = MegaWalls.getInstance();
    private TeamsManager tm;

    public CompassManager() {
        this.tm = this.plugin.getTeamsManager();
    }

    public final Map<Player, TeamsManager.Team> getCompassTargetMap() {
        return this.compassTargetMap;
    }

    public final void setCompassTargetMap(Map<Player, TeamsManager.Team> var1) {
        this.compassTargetMap = var1;
    }

    public final MegaWalls getPlugin() {
        return this.plugin;
    }

    public final void setPlugin(MegaWalls var1) {
        this.plugin = var1;
    }

    public final TeamsManager getTm() {
        return this.tm;
    }

    public final void setTm(TeamsManager var1) {
        this.tm = var1;
    }

    public final void changeTrackingTarget(Player player) {
        Map var2 = this.compassTargetMap;
        TeamsManager.Team var10000;
        TeamsManager.Team var3;
        if (this.compassTargetMap.get(player) == null) {
            var10000 = this.tm.getTeamOfPlayer(player);
        } else {
            var3 = (TeamsManager.Team)this.compassTargetMap.get(player);
            if (var3 != null) {
                var10000 = this.nextTeam(var3);
            } else {
                var10000 = null;
            }
        }

        var3 = var10000;
        var2.put(player, var3);
        var10000 = (TeamsManager.Team)this.compassTargetMap.get(player);
        boolean var7;
        if (var10000 != null) {
            TeamsManager.Team it = var10000;
            var7 = this.plugin.getTeamsManager().getTeamMembers(it).isEmpty();
        } else {
            var7 = false;
        }

        if (var7) {
            this.changeTrackingTarget(player);
        }

    }

    public final String getCompassActionBarOfPlayer(Player player) {
        Map it = this.compassTargetMap;
        if (it.get(player) == null) {
            it.put(player, this.tm.getTeamOfPlayer(player));
        }

        StringBuilder var10000 = (new StringBuilder()).append("Tracking ");
        TeamsManager.Team var10001 = (TeamsManager.Team)this.compassTargetMap.get(player);
        String var8;
        if (var10001 != null) {
            TeamsManager.Team it2 = var10001;
            StringBuilder var6 = var10000;
            var8 = this.tm.getColorOfTeam(it2);
            var10000 = var6;
        } else {
            var8 = null;
        }

        var10000 = var10000.append(var8).append(ChatColor.BOLD);
        TeamsManager.Team var2 = (TeamsManager.Team)this.compassTargetMap.get(player);
        return var10000.append(var2 != null ? var2.name() : null).append(ChatColor.RESET).append(ChatColor.BOLD).append("        Distance: ").append((new BigDecimal(player.getLocation().distance(player.getCompassTarget()))).setScale(1, 4).doubleValue()).append(" m").toString();
    }

    private final TeamsManager.Team nextTeam(TeamsManager.Team team) {
        TeamsManager.Team var10000;
        switch(EnumTeamIndex.EnumTeamIndex[team.ordinal()]) {
            case 1:
                var10000 = TeamsManager.Team.GREEN;
                break;
            case 2:
                var10000 = TeamsManager.Team.BLUE;
                break;
            case 3:
                var10000 = TeamsManager.Team.YELLOW;
                break;
            case 4:
                var10000 = TeamsManager.Team.RED;
                break;
            default:
                throw new RuntimeException("Employee");
        }

        return var10000;
    }

    // $FF: synthetic class

}
