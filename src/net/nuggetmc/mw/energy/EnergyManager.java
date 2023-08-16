package net.nuggetmc.mw.energy;

import net.nuggetmc.mw.MegaWalls;
import net.nuggetmc.mw.mwclass.MWClass;
import net.nuggetmc.mw.mwclass.MWClassManager;
import net.nuggetmc.mw.mwclass.classes.MWDriver;
import net.nuggetmc.mw.mwclass.classes.MWGoldenDragon;
import net.nuggetmc.mw.mwclass.classes.MWMagician;
import net.nuggetmc.mw.mwclass.classes.MWMole;
import net.nuggetmc.mw.utils.ActionBar;
import net.nuggetmc.mw.utils.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class EnergyManager implements Listener {

    private final MegaWalls plugin;

    private final MWClassManager manager;
    private final Map<Player, Integer> playerData = new HashMap<>();

    public EnergyManager() {
        this.plugin = MegaWalls.getInstance();
        this.manager = plugin.getClassManager();

        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this::tick, 20, 20);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, plugin::tickBlockReset, 20, 20L * plugin.breakResetTime);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this::tickActionBar, 10, 10);
    }

    public void tick() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            MWClass mwclass = manager.get(player);

            if (mwclass == null) continue;

            switch (mwclass.getShortName().toLowerCase()) {
                default:
                    break;

                case "spi":
                    add(player, 6);
                    break;
                case "asn":
                    add(player, 2);
                    break;
                case "god":
                case "mag":
                    add(player, 1);
                    break;
                case "mol":
                    add(player, 5);
                    break;
            }
        }
    }

    public void tickActionBar() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            MWClass mwclass = manager.get(player);

            if (mwclass == null) continue;
            if (player.getItemInHand().getType().equals(Material.COMPASS) && ItemUtils.isKitItem(player.getItemInHand())) {
                ActionBar.send(player, ChatColor.BOLD + plugin.getCompassManager().getCompassActionBarOfPlayer(player));
                return;
            }
            if (manager.get(player).getActionBar(player) != null) {
                ActionBar.send(player, manager.get(player).getActionBar(player));
            }
        }
    }

    public Player validate(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) return null;
        if (!(event.getDamager() instanceof Player) && !(event.getDamager() instanceof Arrow)) return null;

        Player player;

        if (event.getDamager() instanceof Arrow) {
            Arrow arrow = (Arrow) event.getDamager();

            if (arrow.getShooter() instanceof Player) {
                player = (Player) arrow.getShooter();

                if (player == event.getEntity()) return null;
            } else {
                return null;
            }

        } else {
            player = (Player) event.getDamager();
        }

        if (((Player) event.getEntity()).getNoDamageTicks() >= 12) return null;
        if (event.getDamage() == 0 || event.isCancelled()) return null;

        if (manager.isMW(player)) {
            return player;
        }

        return null;
    }

    public Player validate(PlayerDeathEvent event) {
        if (!(event.getEntity() instanceof Player)) return null;
        if (!(event.getEntity().getKiller() instanceof Player) && !(event.getEntity().getKiller() instanceof Arrow))
            return null;

        Player player;

        if (event.getEntity().getKiller() instanceof Arrow) {
            Arrow arrow = (Arrow) event.getEntity().getKiller();

            if (arrow.getShooter() instanceof Player) {
                player = (Player) arrow.getShooter();

                if (player == event.getEntity()) return null;
            } else {
                return null;
            }

        } else {
            player = event.getEntity().getKiller();
        }


        if (manager.isMW(player)) {
            return player;
        }

        return null;
    }

    @EventHandler
    public void onExpSpawn(EntitySpawnEvent event) {
        if (event.getEntity() instanceof ExperienceOrb) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onAbility(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        String action = event.getAction().name();

        checkActions(player, action);
    }

    @EventHandler
    public void onAbility2(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        if (!(event.getDamager() instanceof Player)) return;
        if (event.getDamage() == 0 || event.isCancelled()) return;

        checkActions((Player) event.getDamager(), "LEFT_CLICK");
    }

    private void checkActions(Player player, String action) {
        ItemStack item = player.getInventory().getItemInHand();
        if (item == null) return;

        Material type = player.getInventory().getItemInHand().getType();
        if (manager.get(player) instanceof MWGoldenDragon) {
            if (type == Material.BOW && action.contains("LEFT_CLICK")) {
                ((MWGoldenDragon) manager.get(player)).callEcho(player);
            } else if (type.name().contains("SWORD") && action.contains("RIGHT_CLICK")) {
                ((MWGoldenDragon) manager.get(player)).callHeal(player);
            }
            return;
        }

        if (type == Material.BOW && action.contains("LEFT_CLICK") && (!(manager.get(player) instanceof MWDriver))) {
            callAbility(player);
        }

        if (type.name().contains("SWORD") && action.contains("RIGHT_CLICK")&&(!(manager.get(player) instanceof MWMagician))) {
            callAbility(player);
        }
        if (type.name().contains("DIAMOND_SPADE") && action.contains("RIGHT_CLICK") && manager.get(player) instanceof MWMole) {
            callAbility(player);
        }
    }

    private void callAbility(Player player) {
        if (!manager.isMW(player)) return;
        if (fetch(player) < 100&&(!(manager.get(player) instanceof MWMagician))) return;

        manager.get(player).ability(player);
    }

    public int fetch(Player player) {
        return playerData.getOrDefault(player, 0);
    }

    public void add(Player player, int amount) {
        if (!playerData.containsKey(player)) {
            set(player, amount);
            return;
        }

        int current = playerData.get(player);
        int updated = current + amount;

        if (updated > 100) {
            updated = 100;
        }

        set(player, updated);
    }

    public void set(Player player, int amount) {
        playerData.put(player, amount);

       /* plugin.getConfig().set("energy." + player.getName(), amount);
        plugin.saveConfig();*/

        float bar = (float) (amount / 100.0);

        player.setLevel(amount);
        player.setExp(bar);
    }

    public int get(Player player) {
        if (playerData.containsKey(player)) {
            return playerData.get(player);
        }

        return 0;
    }

    public void clear(Player player) {
        set(player, 0);
    }

    public void flash() {
        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            playerData.keySet().forEach(p -> {
                int level = p.getLevel();
                float bar = p.getExp();

                if (level == 100 && bar == 1) {
                    p.setExp(0);
                } else if (level == 100 && bar == 0) {
                    p.setExp(1);
                }
            });
        }, 6, 6);
    }
}
