package net.nuggetmc.mw.killeffects;

import fr.bukkit.effectkill.effect.KillEffect;
import net.nuggetmc.mw.MegaWalls;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.HashMap;
import java.util.Map;

public class KillEffectManager implements Listener {
    private final Map<Player, KillEffect> data = new HashMap<>();
    private final MegaWalls plugin = MegaWalls.getInstance();


    public void save(Player player) {
        if (data.get(player) == null) {
            return;
        }
        plugin.getConfig().set("useeffect." + player.getName(), data.get(player).getName());
        plugin.saveConfig();
    }

    public KillEffect get(Player player) {
        return data.get(player);
    }

    public void select(Player player, KillEffect ke) {
        data.put(player, ke);
        save(player);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("useeffect");

        if (section == null) return;
        String effect;
        KillEffect ke;
        try {
            effect = plugin.getConfig().getString("useeffect." + e.getPlayer().getName());
        } catch (Exception exception) {
            return;
        }

        ke = plugin.getKeMenu().getKEByName(effect);
        if (ke == null) {
            return;
        }
        data.put(e.getPlayer(), ke);

        save(e.getPlayer());
        plugin.saveConfig();
    }


}
