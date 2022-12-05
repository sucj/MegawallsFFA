package fr.bukkit.effectkill.effect.animation;


import fr.bukkit.effectkill.effect.KillEffect;
import fr.bukkit.effectkill.utils.Particle;
import fr.bukkit.effectkill.utils.inventory.Heads;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class Wave extends KillEffect {

    public Wave() {
        super("wave", Heads.WAVE.getTexture());
    }

    @Override
    public void update(Player player) {
        Location loc = player.getLocation();
        new BukkitRunnable() {
            double t = Math.PI / 4;

            public void run() {
                t = t + 0.1 * Math.PI;
                for (double theta = 0; theta <= 2 * Math.PI; theta = theta + Math.PI / 32) {
                    double x = t * Math.cos(theta);
                    double y = 2 * Math.exp(-0.1 * t) * Math.sin(t) + 1.5;
                    double z = t * Math.sin(theta);
                    loc.add(x, y, z);
                    Particle.play(loc, Effect.WATERDRIP);
                    Particle.play(loc, Effect.SNOW_SHOVEL);
                    loc.subtract(x, y, z);
                    theta = theta + Math.PI / 64;
                }
                if (t > 8) {
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 4, 0);
    }
}
