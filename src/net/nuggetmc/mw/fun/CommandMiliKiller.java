package net.nuggetmc.mw.fun;

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

public class CommandMiliKiller implements CommandExecutor {

    private final MegaWalls plugin;

    public CommandMiliKiller() {
        this.plugin = MegaWalls.getInstance();
        MegaWalls plugin = MegaWalls.getInstance();

    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            execute(sender);
        });

        return true;
    }

    private void execute(CommandSender sender) {
        plugin.miliKiller.toggle(sender);
    }
}
