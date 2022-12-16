package net.nuggetmc.mw.command;

import com.google.common.collect.Sets;
import net.md_5.bungee.api.ChatColor;
import net.nuggetmc.mw.MegaWalls;
import net.nuggetmc.mw.mwclass.MWClass;
import net.nuggetmc.mw.mwclass.MWClassManager;
import net.nuggetmc.mw.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class WalkSpeedCommand implements CommandExecutor {


    public WalkSpeedCommand() {


    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)){
            sender.sendMessage("THIS CAN ONLY USED BY A PLAYER!");
        } else if (args.length != 1&&args.length!=2) {
            sender.sendMessage("WRONG USAGE!CORRECT USAGE : /walkspeed <speed> [player]");
        }else {
            float speed;
            try {
                speed=Float.parseFloat(args[0]);
            }catch (Exception e){
                sender.sendMessage("INVALID NUMBER ! NEED A FLOAT!");
                return true;
            }
            if (speed>1.0){
                sender.sendMessage("NUMBER MUST BE SMALLER THAN 1.0 !");
                return true;
            }

            if (args.length==1){
                ((Player) sender).setWalkSpeed(speed);
                sender.sendMessage("SUCCESS");
            }else {
                Player targetPlayer;
                try {
                    targetPlayer=Bukkit.getPlayer(args[1]);
                } catch (Exception e) {
                    sender.sendMessage("CANNOT FIND THE PLAYER!");
                    return true;
                }
                targetPlayer.setWalkSpeed(speed);
                sender.sendMessage("SUCCESS");
            }
        }

        return true;
    }
}
