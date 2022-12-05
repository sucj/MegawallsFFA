package fr.bukkit.effectkill.effect.animation;

import fr.bukkit.effectkill.effect.KillEffect;
import fr.bukkit.effectkill.utils.Particle;
import fr.bukkit.effectkill.utils.inventory.Heads;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;

public class HeadExplode extends KillEffect {

    public HeadExplode() {
        super("headexplode", Heads.ANGRY.getTexture());
    }

    @Override
    public void update(Player player) {
        Location loc = player.getLocation();
        ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
        skullMeta.setOwner(player.getName());
        skull.setItemMeta(skullMeta);
        ArmorStand armor = (ArmorStand) loc.getWorld().spawnEntity(loc.add(0, -1, 0), EntityType.ARMOR_STAND);
        armor.setVisible(false);
        armor.setCustomName("" + player.getName());
        armor.setCustomNameVisible(true);
        armor.setHelmet(skull);
        armor.setGravity(false);
        as.add(armor);
        new BukkitRunnable() {
            int i = 0;

            @Override
            public void run() {
                i++;
                armor.teleport(armor.getLocation().add(0, 0.5, 0));
                armor.setHeadPose(armor.getHeadPose().add(0.0, 1, 0.0));
                Particle.play(armor.getLocation().add(0.0, -0.2, 0.0), Effect.FLAME);
                if (i == 20) {
                    as.remove(armor);
                    armor.remove();
                    Particle.play(armor.getLocation().add(0.0, 0.5, 0.0), Effect.EXPLOSION_HUGE, 1);
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 1, 0);
    }
}
