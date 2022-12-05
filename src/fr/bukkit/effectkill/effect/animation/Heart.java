package fr.bukkit.effectkill.effect.animation;

import fr.bukkit.effectkill.effect.KillEffect;
import fr.bukkit.effectkill.utils.Particle;
import fr.bukkit.effectkill.utils.inventory.Heads;
import fr.bukkit.effectkill.utils.maths.MathUtils;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Heart extends KillEffect {

    public Heart() {
        super("love", Heads.HEART.getTexture());
    }

    @Override
    public void update(Player player) {
        Location loc = player.getLocation();
        for (double height = 0.0; height < 1.0; height += 0.2) {
            Particle.play(loc.clone().add(MathUtils.randomRange(-1.0f, 1.0f), height, MathUtils.randomRange(-1.0f, 1.0f)), Effect.HEART);
        }
    }
}
