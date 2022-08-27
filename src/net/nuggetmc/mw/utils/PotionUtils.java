package net.nuggetmc.mw.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PotionUtils {

    /*
     * The reason why I am applying effects via command and not via player.addPotionEffect() is because the command version already has built-in features
     * to compensate for players who already have the effect (for certain time invervals), and would adjust the time as necessary. In the future, I will make my own
     * that can do just that, and maybe even ADD to the current effect time ;)
     */

    public static void effect(Player player, PotionEffectType potionEffectType, int time) {
        effect(player, potionEffectType, time, 0);
    }

    public static void effect(Player player, PotionEffectType potionEffectType, int time, int amplifier) {
        player.addPotionEffect(new PotionEffect(potionEffectType,time*20,amplifier),true);
    }
}
