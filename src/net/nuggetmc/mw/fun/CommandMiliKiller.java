package net.nuggetmc.mw.fun;

import net.nuggetmc.mw.MegaWalls;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

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
