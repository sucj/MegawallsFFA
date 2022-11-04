package net.nuggetmc.mw.command;

import net.nuggetmc.mw.MegaWalls;
import net.nuggetmc.mw.mwclass.MWClass;
import net.nuggetmc.mw.mwclass.MWClassManager;
import net.nuggetmc.mw.mwclass.items.MWPotions;
import net.nuggetmc.mw.special.SpecialItemUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class getItemCommand implements CommandExecutor, TabCompleter {
    private final List<String> groupnames = new ArrayList<>();
    private final MWClassManager classManager = MegaWalls.getInstance().getClassManager();
    private final SpecialItemUtils si;

    public getItemCommand() {
        this.groupnames.add("healpot");
        this.groupnames.add("speedpot");
        this.groupnames.add("squpot");
        this.groupnames.add("golempot");
        this.groupnames.add("milk");
        this.si = MegaWalls.getInstance().getSpecialItemUtils();
    }



    public final MWClassManager getClassManager() {
        return this.classManager;
    }



    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player player= ((Player) sender);
        if (!MegaWalls.getInstance().getCombatManager().isInCombat(player)) {
            return true;
        }
        if (args.length == 2 || args.length == 1) {
            int amount;
            if (args.length == 2) {
                try {
                    amount = Integer.parseInt(args[1]);
                } catch (Exception e) {
                    sender.sendMessage("invalid syntax!Correct usage: /mwitem item count");
                    return true;
                }
            } else {
                amount = 1;
            }
            ItemStack itemStack;
            switch (args[0]){
                case "healpot":
                    itemStack=MWPotions.createHealPotions(
                            classManager.get(player).getName(),
                        classManager.get(player).getColor(),
                        amount,
                        10
                );
                     break;
                case "speedpot":
                    itemStack=MWPotions.createSpeedPotions(
                            classManager.get(player).getName(),
                        classManager.get(player).getColor(),
                        amount
                );
                    break;
                case "squpot":
                    itemStack=si.getSquidPot(amount);
                    break;
                case "golempot":
                    itemStack=si.getGolemPot(amount);
                    break;
                case "milk":
                    itemStack=si.getCowBucket(amount);
                    break;
                default:
                    itemStack=null;
            }
            if (itemStack == null) {
                sender.sendMessage("invalid item!Allowed items: "+groupnames);
            } else {
                player.getInventory().addItem(itemStack);
            }
        } else {
            sender.sendMessage("invalid syntax!Correct usage: /mwitem item count");
            return true;
        }
        return true;
    }

    public List<String> onTabComplete(CommandSender sender, Command cmd, String label,String[] args) {
        if (args.length != 1) {
            return null;
        } else {
            String arg = args[0];
            return !this.isEmptyTab(arg) ? this.autofill(this.groupnames, arg) : this.groupnames;
        }
    }

    private List<String> autofill(List<String> groupnames, String input) {
        List<String> list = new ArrayList<>();

        for (String entry : groupnames) {
            if (entry.length() >= input.length()) {
                String name = entry.substring(0, input.length());
                if (Objects.equals(input, name)) {
                    list.add(entry);
                }
            }
        }

        return list.isEmpty() ? groupnames : list;
    }

    private boolean isEmptyTab(String s) {
        return s == null || Objects.equals(s, " ") || ((CharSequence)s).length() == 0;
    }

}
