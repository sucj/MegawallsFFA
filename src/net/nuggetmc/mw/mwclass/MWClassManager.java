package net.nuggetmc.mw.mwclass;

import com.earth2me.essentials.Essentials;
import io.isles.nametagapi.NametagAPI;
import net.md_5.bungee.api.ChatColor;
import net.nuggetmc.mw.MegaWalls;
import net.nuggetmc.mw.special.SpecialItemUtils;
import net.nuggetmc.mw.special.TeamsManager;
import net.nuggetmc.mw.utils.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import java.util.*;
import java.util.stream.Collectors;


public class MWClassManager implements Listener {

    private final MegaWalls plugin;

    private final Map<String, MWClass> classes;
    private final Map<Player, MWClass> active;

    public Set<MWClass> kitLock=new HashSet<>();

    public MWClassManager(MegaWalls instance) {
        this.plugin = instance;
        this.classes = new HashMap<>();
        this.active = new HashMap<>();
    }


    public void register(MWClass... mwclasses) {
        Arrays.stream(mwclasses).forEach(m -> classes.put(m.getName(), m));
    }

    public Map<String, MWClass> getClasses() {
        return classes;
    }

    public MWClass fetch(String name) {
        return classes.getOrDefault(name, null);
    }

    public boolean isMW(Player player) {
        return active.containsKey(player);
    }

    public MWClass get(Player player) {
        return active.get(player);
    }

    public Map<Player, MWClass> getActive() {
        return active;
    }

    public void assign(Player player, MWClass mwclass, TeamsManager.Team team) {
        PlayerInventory inventory = player.getInventory();
        inventory.clear();


        player.setMaxHealth(40);
        player.setHealth(40);
        player.setFoodLevel(20);
        player.setSaturation(20);
        player.setWalkSpeed(0.2f);

        if (team != null) {
            List<ItemStack> contents = ItemUtils.getAllContents(inventory).stream().filter(i -> !ItemUtils.isKitItem(i)).collect(Collectors.toList());

            inventory.clear();

            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                contents.forEach(i -> ItemUtils.givePlayerItemStack(player, i));
            }, 1);

            mwclass.assign(player);
        }

        active.put(player, mwclass);
      /*  plugin.getConfig().set("active_classes." + player.getName(), mwclass.getName());
        plugin.saveConfig();*/

        player.getPlayer().setGameMode(GameMode.SURVIVAL);

        String str = ((plugin.getTeamsManager().getSymbolOfTeam(team)));
        plugin.getCombatManager().addInCombat(player);
        if (plugin.getCombatManager().isInCombat(player)) {
            player.setPlayerListName(player.getName());
            player.setDisplayName(player.getName());
            NametagAPI.resetNametag(player.getName());
        }
        if (Bukkit.getPluginManager().getPlugin("Essentials") != null && !Objects.equals(ChatColor.stripColor(((Essentials) Bukkit.getPluginManager().getPlugin("Essentials")).getUser(player).getNick()), player.getName())) {
            //player nicked
            String nickname = ChatColor.stripColor(((Essentials) Bukkit.getPluginManager().getPlugin("Essentials")).getUser(player).getNick());
            String prefix = str + " ";
            String suffix = ChatColor.GRAY + " [" + plugin.getClassManager().get(player).getShortName() + "]";
            player.setPlayerListName(prefix + nickname + suffix);
            player.setDisplayName((plugin.getTeamsManager().getColorOfTeam(team) + "[" + plugin.getTeamsManager().getTeamOfPlayer(player).name()) + "] " + nickname + ChatColor.RESET);
            NametagAPI.setNametagHard(nickname, prefix + ChatColor.MAGIC, ChatColor.RESET + suffix);
        } else {
            String prefix = str + " ";
            String suffix = ChatColor.GRAY + " [" + plugin.getClassManager().get(player).getShortName() + "]";
            player.setPlayerListName(prefix + player.getDisplayName() + suffix);
            player.setDisplayName((plugin.getTeamsManager().getColorOfTeam(team) + "[" + plugin.getTeamsManager().getTeamOfPlayer(player).name()) + "] " + player.getDisplayName() + ChatColor.RESET);
            NametagAPI.setNametagHard(player.getName(), prefix, suffix);
        }
        player.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD+ "[Tip]"+ChatColor.RESET+ChatColor.YELLOW+ "Feeling poor?Mine some cobblestone and sell them at /mwsell.Use /mwshop to buy things.");
        if (ItemUtils.haveCowBucketInEnderChest(player)){
            ItemUtils.refundCowBucket(player);
        }
        ItemUtils.clearUnlegitDiamonds(player.getEnderChest());
        if (mwclass.getShortName().equals("ZOM")) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 9999 * 20, 2));
        }

    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        check(event, event.getPlayer(), event.getItemDrop().getItemStack());
    }

    @EventHandler
    public void onPickUp(PlayerPickupItemEvent event) {
        check(event, event.getPlayer(), event.getItem().getItemStack());
    }

    @EventHandler(
            priority = EventPriority.HIGHEST,
            ignoreCancelled = true
    )
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        List<ItemStack> drops = event.getDrops();

        if (drops != null) {
            drops.removeIf(ItemUtils::isKitItem);
        }

        if (active.containsKey(player)) {
            active.remove(player);
            plugin.getEnergyManager().clear(player);

            event.setDroppedExp(0);
        }

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (player != null && player.isDead()) {
                player.spigot().respawn();
            }
        }, 12);
        event.getEntity().getPlayer().getInventory().clear();

    }

    @EventHandler
    public void onItemTransfer(InventoryClickEvent event) {
        InventoryType type = event.getInventory().getType();

        if (type == InventoryType.PLAYER || type == InventoryType.CRAFTING) {
            return;
        }
        if (plugin.getSpecialItemUtils().isCowBucket(event.getCurrentItem())){
            event.setCancelled(true);
            return;
        }

        check(event, (Player) event.getWhoClicked(), event.getCurrentItem());
    }

    private void check(Cancellable event, Player player, ItemStack item) {
        if (ItemUtils.isKitItem(item) && player.getGameMode() == GameMode.SURVIVAL) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (player.isOnline()) {
                player.sendMessage(ChatColor.GREEN + "Do " + ChatColor.YELLOW + "/mw" + ChatColor.GREEN + " to select a class!");
            }
        }, 10);

        event.getPlayer().setGameMode(GameMode.ADVENTURE);

        player.setHealth(0);
    }

    @EventHandler
    public void onPreJoin(PlayerSpawnLocationEvent event) {
        // event.setSpawnLocation(WorldUtils.nearby(event.getSpawnLocation()));
    }
}
