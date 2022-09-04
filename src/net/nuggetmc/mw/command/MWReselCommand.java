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
            MegaWalls.getInstance().getMenu().select(player,manager.get(player));

        }

        return true;
    }
}
