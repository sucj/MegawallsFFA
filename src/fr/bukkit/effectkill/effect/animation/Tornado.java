package fr.bukkit.effectkill.effect.animation;

import fr.bukkit.effectkill.effect.MainEffectKill;
import fr.bukkit.effectkill.utils.Particle;
import fr.bukkit.effectkill.utils.inventory.Heads;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class Tornado extends MainEffectKill {

    public Tornado() {
        super("tornado", Heads.TORNADO.getTexture());
    }

    @Override
    public void update(Player player) {
        Location location = player.getLocation();
        new BukkitRunnable() {
            int angle = 0;

            @Override
            public void run() {
                int max_height = 7;
                double max_radius = 5;
                int lines = 5;
                double height_increasement = 0.25;
                double radius_increasement = max_radius / max_height;
                for (int l = 0; l < lines; l++) {
                    for (double y = 0; y < max_height; y += height_increasement) {
                        double radius = y * radius_increasement;
                        double x = Math.cos(Math.toRadians(360 / lines * l + y * 30 - angle)) * radius;
                        double z = Math.sin(Math.toRadians(360 / lines * l + y * 30 - angle)) * radius;
                        Particle.play(location.clone().add(x, y, z), Effect.CLOUD);
                    }
                }
                angle++;
                if (angle == 70) {
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 2, 0);
    }
}
