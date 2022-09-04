package net.nuggetmc.mw.command;

import net.md_5.bungee.api.ChatColor;
import net.nuggetmc.mw.MegaWalls;
import net.nuggetmc.mw.mwclass.MWClassManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SetMWSpawnCommand implements CommandExecutor, TabCompleter {
    private final MWClassManager manager;
    private final MegaWalls  plugin=MegaWalls.getInstance();

    public SetMWSpawnCommand() {
        this.manager = MegaWalls.getInstance().getClassManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player=((Player) sender).getPlayer();
            if (args.length!=1){
                sender.sendMessage("Invalid Syntax! Usage: /mwspawn [RED,GREEN,BLUE,YELLOW]");
            }else {
                double[] doubles =new double[] {player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ()};
                switch (args[0].toLowerCase()){
                    case "red":
                        plugin.getConfig().set("spawnloc.red", doubles);
                        sender.sendMessage("success!");
                        break;
                    case "green":
                        plugin.getConfig().set("spawnloc.green",doubles);
                        sender.sendMessage("success!");
                        break;
                    case "blue":
                        plugin.getConfig().set("spawnloc.blue",doubles);
                        sender.sendMessage("success!");
                        break;
                    case "yellow":
                        plugin.getConfig().set("spawnloc.yellow",doubles);
                        sender.sendMessage("success!");
                        break;
                    default:
                        sender.sendMessage("Invalid Syntax! Usage: /mwspawn [RED,GREEN,BLUE,YELLOW]");
                }
                plugin.saveConfig();
            }
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

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {


            List<String> groupnames = new ArrayList<>();
            groupnames.add("red");
            groupnames.add("green");
            groupnames.add("blue");
            groupnames.add("yellow");
            String arg = args[0];

            if (!isEmptyTab(arg)) {
                return autofill(groupnames, arg);
            }

            return groupnames;

    }
    private boolean isEmptyTab(String s) {
        return s == null || s.equals(" ") || s.isEmpty();
    }
    private List<String> autofill(List<String> groupnames, String input) {
        List<String> list = new ArrayList<>();

        for (String entry : groupnames) {
            if (entry.length() >= input.length()) {
                if (input.equalsIgnoreCase(entry.substring(0, input.length()))) {
                    list.add(entry);
                }
            }
        }

        if (list.isEmpty()) {
            return groupnames;
        }

        return list;
    }
}
