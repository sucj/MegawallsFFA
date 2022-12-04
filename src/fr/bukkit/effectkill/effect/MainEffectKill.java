package fr.bukkit.effectkill.effect;

import fr.bukkit.effectkill.utils.ItemsUtils;
import net.nuggetmc.mw.MegaWalls;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;


public abstract class MainEffectKill{


	public static MegaWalls plugin=MegaWalls.getInstance();
	public static MainEffectKill effectKill;
	public ArrayList<ArmorStand> as = new ArrayList<>();
	public ItemStack itemStack;
	protected String name;
	protected String displayName;


	public MainEffectKill(String name, String texture) {
		effectKill = this;
		ItemStack head = ItemsUtils.getSkull(texture);
		itemStack = ItemsUtils.create(head, displayName,null);
		this.name = name;

		this.displayName = name;


	}
	public abstract void update(Player player);
	public String getName() {
		return name;
	}

	public String getDisplayName() {
		return displayName; 
	}
	public ItemStack getItemStack() {
		return itemStack;
	}
}