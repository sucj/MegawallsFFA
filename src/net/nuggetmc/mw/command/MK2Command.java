package net.nuggetmc.mw.command;

import net.nuggetmc.mw.MegaWalls;
import net.nuggetmc.mw.special.MK2;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static org.bukkit.Bukkit.getServer;

public class MK2Command implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (!MegaWalls.getInstance().getClassManager().isMW(player)){
                return true;
            }
            if (MegaWalls.getInstance().mk2.getOnPig().containsKey(player)){
                return true;
            }
            if (!sender.hasPermission("mw.admin")){
                sender.sendMessage("Please use /mwshop to buy and use it.");
                return true;
            }
            //player.setItemInHand(MK2Stick.INSTANCE.buildItem());
            MegaWalls.getInstance().mk2.launchPig(player);
            return true;
        }
        sender.sendMessage("this can pnly be used by player!");
        return true;

    }
}
