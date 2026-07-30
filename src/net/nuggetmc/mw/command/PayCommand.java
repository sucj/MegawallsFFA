package net.nuggetmc.mw.command;

import net.nuggetmc.mw.MegaWalls;
import net.nuggetmc.mw.mwclass.MWClassManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static org.bukkit.Bukkit.getServer;

public class PayCommand implements CommandExecutor {

    private final MWClassManager manager;

    public PayCommand() {
        this.manager = MegaWalls.getInstance().getClassManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (args.length == 2) {


                    Player target=null;
                    target=Bukkit.getPlayerExact(args[0]);
                    if (target==null){
                        sender.sendMessage("Player not found!");
                        return true;
                    }
                    int amount=-1;
                    try {
                        amount=Integer.parseInt(args[1]);
                    } catch (NumberFormatException e) {
                        sender.sendMessage("Cannot read number!It must be a integer bigger than 0.");
                        return true;
                    }
                    if (amount==-1){
                        sender.sendMessage("Cannot read number!It must be a integer bigger than 0.");
                        return true;
                    }
                    if (MegaWalls.getInstance().getCoinsManager().get(player)<amount){
                        sender.sendMessage("You do not have that many coins.");
                        return true;
                    }
                    if (target.getUniqueId().equals(player.getUniqueId())){
                        sender.sendMessage("Why would you pay yourself?.");
                        return true;
                    }
                    MegaWalls.getInstance().getCoinsManager().add(player,-amount);
                    MegaWalls.getInstance().getCoinsManager().add(target,amount);
                    player.sendMessage(ChatColor.YELLOW+"You paid "+amount+" coins to "+target.getDisplayName()+".");
                    target.sendMessage(ChatColor.YELLOW+"You received "+amount+" coins from "+player.getDisplayName()+".");
                    //player.openInventory(Bukkit.getPlayerExact(args[0]).getEnderChest());
                    return true;


            }else {
                player.sendMessage("Wrong usage!/pay <player> <amount>");
            }


            return true;
        }
        sender.sendMessage("this can only be used by player!");
        return true;

    }
}
