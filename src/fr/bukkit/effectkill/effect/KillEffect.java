package fr.bukkit.effectkill.effect;

import fr.bukkit.effectkill.utils.ItemsUtils;
import net.nuggetmc.mw.MegaWalls;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;


public abstract class KillEffect {


    public static MegaWalls plugin = MegaWalls.getInstance();
    public static KillEffect effectKill;
    public ArrayList<ArmorStand> as = new ArrayList<>();
    public ItemStack itemStack;
    protected String name;


    public KillEffect(String name, String texture) {
        effectKill = this;
        ItemStack head = ItemsUtils.getSkull(texture);
        itemStack = ItemsUtils.create(head, name, null);
        this.name = name;


    }

    public abstract void update(Player player);

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}