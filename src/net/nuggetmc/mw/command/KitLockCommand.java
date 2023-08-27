package net.nuggetmc.mw.command;

import net.nuggetmc.mw.MegaWalls;
import net.nuggetmc.mw.mwclass.MWClass;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class KitLockCommand implements CommandExecutor, TabCompleter {
    List<String> groupnames = new ArrayList<>(MegaWalls.getInstance().getClassManager().getClasses().keySet()).stream().map(String::toLowerCase).collect(Collectors.toList());

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length<1){
            sendHelp(sender);
        }else {
            String name = StringUtils.capitalize(args[0].toLowerCase());
            MWClass mwclass = MegaWalls.getInstance().getClassManager().fetch(name);

            if (mwclass != null) {
                if (!MegaWalls.getInstance().getClassManager().kitLock.contains(mwclass)) {
                    MegaWalls.getInstance().getClassManager().kitLock.add(mwclass);
                    sender.sendMessage("You locked the class "+mwclass.getName());
                }else {
                    MegaWalls.getInstance().getClassManager().kitLock.remove(mwclass);
                    sender.sendMessage("You unlocked the class "+mwclass.getName());
                }
            }else {
                sender.sendMessage("class not found.");
            }
        }
        return true;
    }
    void sendHelp(CommandSender sender){
        sender.sendMessage("usage:/kitlock kit");
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length != 1) return null;


        String arg = args[0];

        if (!isEmptyTab(arg)) {
            return autofill(groupnames, arg);
        }

        return groupnames;
    }

    private boolean isEmptyTab(String s) {
        return s == null || s.equals(" ") || s.isEmpty();
    }

    private List<String> autofill(List<String> groupnames, String input) {
        List<String> list = new ArrayList<>();

        for (String entry : groupnames) {
            if (entry.length() >= input.length()) {
                if (input.equalsIgnoreCase(entry.substring(0, input.length()))) {
                    list.add(entry);
                }
            }
        }

        if (list.isEmpty()) {
            return groupnames;
        }

        return list;
    }
}
