package fr.bukkit.effectkill.effect.animation;

import fr.bukkit.effectkill.effect.MainEffectKill;
import fr.bukkit.effectkill.utils.Particle;
import fr.bukkit.effectkill.utils.inventory.Heads;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class FrostFlame extends MainEffectKill {

    public FrostFlame() {
        super("frostflame", Heads.FLAME.getTexture());
    }

    @Override
    public void update(Player player) {
        Location loc = player.getLocation();
        new BukkitRunnable() {
            double t = 0.0D;

            public void run() {
                t += 0.3;
                for (double phi = 0.0D; phi <= 6; phi += 1.5) {
                    double x = 0.11D * (12.5 - t) * Math.cos(t + phi);
                    double y = 0.23D * t;
                    double z = 0.11D * (12.5 - t) * Math.sin(t + phi);
                    loc.add(x, y, z);
                    Particle.play(loc, Effect.FLAME);
                    loc.subtract(x, y, z);

                    if (t >= 12.5) {
                        loc.add(x, y, z);
                        if (phi > Math.PI) {
                            cancel();
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 1L, 1L);
    }
}
