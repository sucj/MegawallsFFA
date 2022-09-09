package net.nuggetmc.mw.command;

import net.nuggetmc.mw.MegaWalls;
import net.nuggetmc.mw.mwclass.MWClassManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MWReselCommand implements CommandExecutor {

    private final MWClassManager manager;

    public MWReselCommand() {
        this.manager = MegaWalls.getInstance().getClassManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (!MegaWalls.getInstance().getCombatManager().isInCombat(player)){
                player.sendMessage("LOL BRO YOU DIDNT SELECT A CLASS AT ALL!");
                return true;
            }
            if(args.length==0) {
                MegaWalls.getInstance().getMenu().select(player, manager.get(player));
            } else if (args.length == 1) {
                try {
                    MegaWalls.getInstance().getMenu().select(Bukkit.getPlayer(args[0]), manager.get(Bukkit.getPlayer(args[0])));
                } catch (Exception e) {
                    sender.sendMessage("Player not found!");
                }
            }
            else {
                sender.sendMessage("invaild syntax! usage: /mwresel [player]");
            }

        }

        return true;
    }
}
