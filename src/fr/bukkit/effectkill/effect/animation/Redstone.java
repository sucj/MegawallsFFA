package fr.bukkit.effectkill.effect.animation;

import fr.bukkit.effectkill.effect.MainEffectKill;
import fr.bukkit.effectkill.utils.inventory.Heads;
import fr.bukkit.effectkill.utils.maths.MathUtils;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class Redstone extends MainEffectKill {

    public Redstone() {
        super("redstone", Heads.REDSTONE.getTexture());
    }

    @Override
    public void update(Player player) {
        Location loc = player.getLocation();
        for (double height = 0.0; height < 1.0; height += 0.8) {
            player.getWorld().playEffect(loc.clone().add(MathUtils.randomRange(-1.0f, 1.0f), height, MathUtils.randomRange(-1.0f, 1.0f)), Effect.STEP_SOUND, Material.REDSTONE_BLOCK);
            player.getWorld().playEffect(loc.clone().add(MathUtils.randomRange(1.0f, -1.0f), height, MathUtils.randomRange(-1.0f, 1.0f)), Effect.STEP_SOUND, Material.REDSTONE_BLOCK);
        }
    }
}
