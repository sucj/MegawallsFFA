package net.nuggetmc.mw.command;

import net.nuggetmc.mw.events.PsychopathManager;
import net.nuggetmc.mw.special.entities.wither.WitherNPCUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MWDebugCommand implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (!sender.hasPermission("mw.admin")){
                return true;
            }
            PsychopathManager.INSTANCE.addPsychopath(player);
            return true;
        }
        sender.sendMessage("this can only be used by player!");
        return true;

    }
}
