package net.nuggetmc.mw.special.entities;

import net.citizensnpcs.api.event.NPCDeathEvent;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;
import net.nuggetmc.mw.MegaWalls;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

@TraitName("removeondeath")
public class RemoveOnDeathTrait extends Trait {

    public RemoveOnDeathTrait() {
        super("removeondeath");
    }

    @EventHandler
    public void onDeath(NPCDeathEvent event) {
        // 确保监听到的死亡事件属于当前这个 NPC
        if (event.getNPC() == this.getNPC()) {
            // 延迟2个Tick移除，避免与当前的死亡事件处理产生线程冲突
            Bukkit.getScheduler().runTaskLater(MegaWalls.getInstance(), () -> {
                            getNPC().destroy();
                        }, 2);
        }
    }
}