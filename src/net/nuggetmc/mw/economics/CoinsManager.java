package net.nuggetmc.mw.economics;

import me.kaaseigenaar.scoreboard.ScoreboardBuilder;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.nuggetmc.mw.MegaWalls;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CoinsManager implements Listener {
    private final Map<Player, Integer> coinsData = new HashMap<>();
    private final MegaWalls plugin = MegaWalls.getInstance();
    private final boolean sbExists = (Bukkit.getPluginManager().getPlugin("ScoreboardPlus") != null);


    public CoinsManager() {

    }

    public void set(Player player, int amount) {
        coinsData.put(player, amount);

        saveCoins(player, amount);
        if (sbExists) {
            ScoreboardBuilder.buildScoreboard(player);
        }
    }

    public void saveCoins(Player player, int amount) {
        plugin.getConfig().set("coins." + player.getName(), amount);
        plugin.saveConfig();
    }

    public int get(Player player) {
        if (coinsData.containsKey(player)) {
            return coinsData.get(player);
        }

        return 0;
    }

    public void add(Player player, int amount) {
        if (!coinsData.containsKey(player)) {
            set(player, amount);
            return;
        }

        int current = coinsData.get(player);
        int updated = current + amount;


        set(player, updated);
    }

    @EventHandler
    public void onKill(PlayerDeathEvent e) {
        if (plugin.getEnergyManager().validate(e) == null) {
            return;
        }
        Player killer = plugin.getEnergyManager().validate(e);
        //
        plugin.getCoinsManager().add(killer, 200);
        killer.sendMessage(ChatColor.YELLOW + "+ 200 Coins (Kill Player)!");
    }

    public void clear(Player player) {
        set(player, 0);
    }

    public TextComponent[] getBalTopPage(int page) {
        TextComponent textComponent = new TextComponent();
        TextComponent[] list = new TextComponent[4];
        StringBuilder result = new StringBuilder();
        result.append(ChatColor.AQUA + "------------BalTop------------\n");
        Map<String, Integer> data = new HashMap<>();
        ArrayList<String> mapArrayList = new ArrayList<>();
        for (String playername : plugin.getConfig().getConfigurationSection("coins").getKeys(false)) {
            data.put(playername, (plugin.getOrDefaultFromConfig("coins." + playername, 0)));
        }
        for (int i = 0; i < data.keySet().toArray().length; i++) {
            mapArrayList.add((String) data.keySet().toArray()[i]);
        }
        mapArrayList.sort((s, t1) -> {
            if (data.get(s) < data.get(t1)) {
                return 1;
            } else {
                return -1;
            }
        });
        int startsAt = ((page - 1) * 10);
        int endsAt = page * 10 - 1;
        for (int i = startsAt; i < mapArrayList.size() && i <= endsAt; i++) {
            result.append(getColorOfGrade(i + 1) + "[" + (i + 1) + "] " + mapArrayList.get(i) + " : " + data.get(mapArrayList.get(i)) + "\n");
        }
        result.append(ChatColor.AQUA + "------------------------------\n");
        result.append(ChatColor.YELLOW + ChatColor.BOLD.toString() + "this is page " + page + " .\n");
        TextComponent lastPage = new TextComponent(ChatColor.RED + ChatColor.BOLD.toString() + "[<<<PREVIOUS PAGE]");
        lastPage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mwbaltop " + (page - 1)));
        lastPage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[]{new TextComponent("CLICK TO GO TO THE PREVIOUS PAGE!")}));
        TextComponent t1 = new TextComponent("        ");
        TextComponent nextPage = new TextComponent(ChatColor.GREEN + ChatColor.BOLD.toString() + "[>>>NEXT PAGE]");
        nextPage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mwbaltop " + (page + 1)));
        nextPage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[]{new TextComponent("CLICK TO GO TO THE NEXT PAGE!")}));
        textComponent.setText(result.toString());
        list[0] = textComponent;
        list[1] = lastPage;
        list[2] = t1;
        list[3] = nextPage;
        return list;
    }

    public String getBalTopPageCONSOLE(int page) {
        StringBuilder result = new StringBuilder();
        result.append(ChatColor.AQUA + "------------BalTop------------\n");
        Map<String, Integer> data = new HashMap<>();
        ArrayList<String> mapArrayList = new ArrayList<>();
        for (String playername : plugin.getConfig().getConfigurationSection("coins").getKeys(false)) {
            data.put(playername, (plugin.getOrDefaultFromConfig("coins." + playername, 0)));
        }
        for (int i = 0; i < data.keySet().toArray().length; i++) {
            mapArrayList.add((String) data.keySet().toArray()[i]);
        }
        mapArrayList.sort((s, t1) -> {
            if (data.get(s) < data.get(t1)) {
                return 1;
            } else {
                return -1;
            }
        });
        int startsAt = ((page - 1) * 10);
        int endsAt = page * 10 - 1;
        for (int i = startsAt; i < mapArrayList.size() && i <= endsAt; i++) {
            result.append(getColorOfGrade(i + 1) + "[" + (i + 1) + "] " + mapArrayList.get(i) + " : " + data.get(mapArrayList.get(i)) + "\n");
        }
        result.append(ChatColor.AQUA + "------------------------------\n");
        return result.toString();
    }


    private ChatColor getColorOfGrade(int input) {
        switch (input) {
            case 1:
                return ChatColor.GOLD;
            case 2:
                return ChatColor.AQUA;
            case 3:
                return ChatColor.GREEN;
            default:
                return ChatColor.RESET;
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        ConfigurationSection sectionCoins = plugin.getConfig().getConfigurationSection("coins");

        if (sectionCoins == null) return;
        int coinamount;
        try {
            coinamount = (int) plugin.getConfig().get("coins." + e.getPlayer().getName());
        } catch (Exception exception) {
            return;
        }
        plugin.getCoinsManager().set(e.getPlayer(), coinamount);
        plugin.saveConfig();
    }


}
