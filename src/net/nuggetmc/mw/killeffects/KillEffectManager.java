package net.nuggetmc.mw.killeffects;

import fr.bukkit.effectkill.effect.KillEffect;
import me.kaaseigenaar.scoreboard.ScoreboardBuilder;
import net.md_5.bungee.api.ChatColor;
import net.nuggetmc.mw.MegaWalls;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.*;

public class KillEffectManager implements Listener {
    private Map<Player, List<KillEffect>> data = new HashMap<>();
    private final MegaWalls plugin = MegaWalls.getInstance();







    public void save(Player player) {
        if (data.get(player)==null){
            return;
        }
        List<String> list=new ArrayList<>();
        for (KillEffect ke:data.get(player)){
            list.add(ke.getName());
        }
        plugin.getConfig().set("owneffects." + player.getName(), list);
        plugin.saveConfig();
    }

    public List<KillEffect> get(Player player) {
        return data.get(player);
    }

    public void give(Player player, KillEffect ke) {
        if (data.get(player)==null){
            data.put(player, Collections.singletonList(ke));
        }else {
            data.get(player).add(ke);
        }
        save(player);
    }
    public boolean playerHasEffect(Player player,KillEffect ke){
        if (data.get(player)==null||data.get(player).isEmpty()){
            return false;
        }else {
            return data.get(player).contains(ke);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("owneffects");

        if (section == null) return;
        List<String> effects;
        try {
            effects = plugin.getConfig().getStringList("owneffects." + e.getPlayer().getName());
        } catch (Exception exception) {
            return;
        }
        for (String str:effects){
            KillEffect ke=plugin.getKeMenu().getKEByName(str);
            if (ke!=null){
                if (data.get(e.getPlayer())==null){
                    data.put(e.getPlayer(), Collections.singletonList(ke));
                }else {
                    data.get(e.getPlayer()).add(ke);
                }
            }
        }
        save(e.getPlayer());
        plugin.saveConfig();
    }



}
