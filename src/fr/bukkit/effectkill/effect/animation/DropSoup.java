package fr.bukkit.effectkill.effect.animation;

import fr.bukkit.effectkill.effect.MainEffectKill;
import fr.bukkit.effectkill.utils.ItemFactory;
import fr.bukkit.effectkill.utils.Particle;
import fr.bukkit.effectkill.utils.inventory.Heads;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

public class DropSoup extends MainEffectKill {

    Random r = new Random();
    ArrayList<Item> items = new ArrayList<Item>();

    public DropSoup() {
        super("dropsoup", Heads.SOUP.getTexture());
    }

    @Override
    public void update(Player player) {
        for (int i = 0; i < 30; i++) {
            Item ITEM = player.getWorld().dropItem(player.getLocation(), ItemFactory.create(Material.MUSHROOM_SOUP, (byte) 0, UUID.randomUUID().toString()));
            ITEM.setPickupDelay(300);
            items.add(ITEM);
            ITEM.setVelocity(new Vector(r.nextDouble() - 0.5D, r.nextDouble() / 2.0D, r.nextDouble() - 0.5D));
        }
        Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
            public void run() {
                for (Item i : items) {
                    Particle.play(i.getLocation(), Effect.COLOURED_DUST);
                    i.remove();
                }
            }
        }, 50L);
    }
}
