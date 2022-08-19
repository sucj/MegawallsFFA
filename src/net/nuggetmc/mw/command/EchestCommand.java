package net.nuggetmc.mw.command;

import jdk.internal.org.objectweb.asm.commons.Method;
import net.minecraft.server.v1_8_R3.ChatClickable;
import net.nuggetmc.mw.MegaWalls;
import net.nuggetmc.mw.mwclass.MWClassManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static org.bukkit.Bukkit.getServer;

public class EchestCommand implements CommandExecutor {

    private final MWClassManager manager;

    public EchestCommand() {
        this.manager = MegaWalls.getInstance().getClassManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player){
            Player player = (Player)sender;

            if(args.length < 1) {
                    if (MegaWalls.getInstance().getCombatManager().isInCombat(player)||player.isOp()||player.hasPermission("mw.admin")){
                    player.openInventory(player.getEnderChest());


            }else {
                        sender.sendMessage("You must be in combat to do that!");
                    }
                return true;
            }
                else if (args.length==1||args.length==2){
                if(player.isOp()||player.hasPermission("mw.admin")) {

                    String targetplayername = null;
                    Player targetplayer = null;
                    try {
                        targetplayername = args[0];
                    } catch (ArrayIndexOutOfBoundsException e) {
                        getServer().getLogger().warning("Missing argument on enderchestcommand... " + e);
                    }
                    if (targetplayername != null) {
                        targetplayer = Bukkit.getPlayerExact(targetplayername);

                        // Exceptions vermeiden if possible...

                        if (targetplayer == null) {
                            sender.sendMessage("Player is null!");
                            return true;
                        }
                    }
                    assert targetplayer != null;
                    if (args.length == 1) {
                        player.openInventory(targetplayer.getEnderChest());
                    } else if (args.length == 2 && args[1] == "clear") {
                        targetplayer.getEnderChest().clear();
                    }
                    //player.openInventory(Bukkit.getPlayerExact(args[0]).getEnderChest());
                    return true;
                }
                }




            return true;
        }
            sender.sendMessage("this can pnly be used by player!");
            return true;
    }
}
