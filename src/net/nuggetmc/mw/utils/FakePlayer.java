package net.nuggetmc.mw.utils;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Equipment;
import net.citizensnpcs.trait.SkinTrait;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class FakePlayer
{

    private Player thePlayer;
    private Location location;
    private NPC npc;

    public FakePlayer(final Player player) {
        this.thePlayer = player;
        this.location = player.getLocation().clone();
        this.npc=CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER,player.getPlayerListName(),player.getLocation());
        SkinTrait skinTrait=npc.getOrAddTrait(SkinTrait.class);
        skinTrait.setSkinName(player.getName());
        this.npc.spawn(this.location);
        final Equipment trait = (Equipment)this.npc.getTrait((Class)Equipment.class);
        trait.set(Equipment.EquipmentSlot.HELMET, player.getInventory().getHelmet());
        trait.set(Equipment.EquipmentSlot.CHESTPLATE, player.getInventory().getChestplate());
        trait.set(Equipment.EquipmentSlot.LEGGINGS, player.getInventory().getLeggings());
        trait.set(Equipment.EquipmentSlot.BOOTS, player.getInventory().getBoots());
        trait.set(Equipment.EquipmentSlot.HAND, player.getItemInHand());
    }

    public void teleport(final Location location) {
        this.npc.teleport(location, PlayerTeleportEvent.TeleportCause.PLUGIN);
        this.location = location;
    }

    public void delete() {
        this.npc.destroy();
    }

    public Player getThePlayer() {
        return this.thePlayer;
    }

    public Location getLocation() {
        return this.location;
    }


    public NPC getNpc() {
        return this.npc;
    }

}
