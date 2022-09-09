package net.nuggetmc.mw.command;

import net.nuggetmc.mw.MegaWalls;
import net.nuggetmc.mw.mwclass.MWClassManager;
import net.nuggetmc.mw.special.TeamsManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class DebugCommand implements CommandExecutor {

    private final MWClassManager manager;

    public DebugCommand() {
        this.manager = MegaWalls.getInstance().getClassManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            ArrayList<TeamsManager.Team> list = new ArrayList<TeamsManager.Team>(Arrays.asList(TeamsManager.Team.values()));
            list.sort(new Comparator<TeamsManager.Team>() {
                @Override
                public int compare(TeamsManager.Team team, TeamsManager.Team t1) {
                    if (MegaWalls.getInstance().getTeamsManager().getTeamMembers(team).size()<MegaWalls.getInstance().getTeamsManager().getTeamMembers(t1).size()){
                        return -1;
                    }else if (MegaWalls.getInstance().getTeamsManager().getTeamMembers(team).size()>MegaWalls.getInstance().getTeamsManager().getTeamMembers(t1).size()){
                        return 1;
                    }
                    return 0;
                }
            });
            System.out.println(list);
        }

        /*if (manager.getKitLock()) {
            manager.setKitLock(false);
            Bukkit.broadcastMessage("Kit items are now " + ChatColor.BLUE + "STACKABLE" + ChatColor.RESET + ".");
        } else {
            manager.setKitLock(true);
            Bukkit.broadcastMessage("Kit items are no longer " + ChatColor.BLUE + "STACKABLE" + ChatColor.RESET + ".");
        }*/
        return true;
    }
}
