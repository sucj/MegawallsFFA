package net.nuggetmc.mw.command;

import net.md_5.bungee.api.ChatColor;
import net.nuggetmc.mw.MegaWalls;
import net.nuggetmc.mw.mwclass.MWClassManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class SetMWSpawnCommand implements CommandExecutor {
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
}
