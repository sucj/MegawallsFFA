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

    public final List<String> getGroupnames() {
        return this.groupnames;
    }

    public final MWClassManager getClassManager() {
        return this.classManager;
    }

    public final SpecialItemUtils getSi() {
        return this.si;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        } else {
            Player player = (Player)sender;
            if (!MegaWalls.getInstance().getCombatManager().isInCombat(player)) {
                return true;
            } else if (!(args != null ? args.length == 2 : false) && !(args != null ? args.length == 1 : false)) {
                ((Player)sender).sendMessage("invalid syntax!Correct usage: /mwitem item count");
                return true;
            } else {
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

                ItemStack stack;
                cfrIsTheBestDecompiler: {
                    String itemName = args[0];
                    if (itemName != null) {
                        MWClassManager classManager1;
                        MWClass bzd;
                        String var14;
                        switch(itemName.hashCode()) {
                            case -2131167058:
                                if (itemName.equals("speedpot")) {
                                    label72: {
                                        classManager1 = this.classManager;
                                        if (classManager1 != null) {
                                            bzd = classManager1.get(player);
                                            if (bzd != null) {
                                                var14 = bzd.getName();
                                                break label72;
                                            }
                                        }

                                        var14 = null;
                                    }

                                    stack = MWPotions.createSpeedPotions(var14, this.classManager.get(player).getColor(), amount);
                                    break cfrIsTheBestDecompiler;
                                }
                                break;
                            case -894660322:
                                if (itemName.equals("squpot")) {
                                    stack = this.si.getSquidPot(amount);
                                    break cfrIsTheBestDecompiler;
                                }
                                break;
                            case 3351579:
                                if (itemName.equals("milk")) {
                                    stack = this.si.getCowBucket(amount);
                                    break cfrIsTheBestDecompiler;
                                }
                                break;
                            case 795556717:
                                if (itemName.equals("healpot")) {
                                    label77: {
                                        classManager1 = this.classManager;
                                        if (classManager1 != null) {
                                            bzd = classManager1.get(player);
                                            if (bzd != null) {
                                                var14 = bzd.getName();
                                                break label77;
                                            }
                                        }

                                        var14 = null;
                                    }

                                    stack = MWPotions.createHealPotions(var14, this.classManager.get(player).getColor(), amount, 10);
                                    break cfrIsTheBestDecompiler;
                                }
                                break;
                            case 2038081193:
                                if (itemName.equals("golempot")) {
                                    stack = this.si.getGolemPot(amount);
                                    break cfrIsTheBestDecompiler;
                                }
                        }
                    }

                    stack = (ItemStack)null;
                }

                ItemStack itemStack = stack;
                if (itemStack == null) {
                    ((Player)sender).sendMessage("invalid item!Allowed items:" + this.groupnames);
                } else {
                    PlayerInventory var15 = player.getInventory();
                    ItemStack[] var11 = new ItemStack[]{itemStack};
                    var15.addItem(var11);
                }

                return true;
            }
        }
    }

    public List<String> onTabComplete(CommandSender sender, Command cmd, String label,String[] args) {
        if (args.length != 1) {
            return null;
        } else {
            String arg = args[0];
            return !this.isEmptyTab(arg) ? this.autofill(this.groupnames, arg) : this.groupnames;
        }
    }

    private final List<String> autofill(List<String> groupnames, String input) {
        List<String> list = new ArrayList<String>();

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

    private final boolean isEmptyTab(String s) {
        return s == null || Objects.equals(s, " ") || ((CharSequence)s).length() == 0;
    }

}
