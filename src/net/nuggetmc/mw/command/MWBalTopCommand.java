package net.nuggetmc.mw.command;

import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_8_R3.ChatClickable;
import net.nuggetmc.mw.MegaWalls;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;

import java.util.Map;

public class MWBalTopCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length ==0){
            if (sender instanceof Player){
                ((Player) sender).spigot().sendMessage(MegaWalls.getInstance().getCoinsManager().getBalTopPage(1));
            }else {
                sender.sendMessage(MegaWalls.getInstance().getCoinsManager().getBalTopPageCONSOLE(1));
            }
            return true;
        }else {
            int page;
            try {
                page=Integer.parseInt(args[0]);
            } catch (Exception e) {
                sender.sendMessage("That seems like you entered a invalid number.Please input a Integer.");
                return true;
            }
            if (Integer.parseInt(args[0]) <= 0) {
                sender.sendMessage("Fuck you! Do you have a page number that is smaller than 1 when you read books?");
                return true;
            } else {
                if (sender instanceof Player){
                    ((Player) sender).spigot().sendMessage(MegaWalls.getInstance().getCoinsManager().getBalTopPage(page));
                }else {
                    sender.sendMessage(MegaWalls.getInstance().getCoinsManager().getBalTopPageCONSOLE(page));
                }
            }
        }
        return true;
    }
}
