package net.nuggetmc.mw.special.entities;

import net.citizensnpcs.api.event.NPCDeathEvent;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;
import org.bukkit.event.EventHandler;

@TraitName("removeondeath")
public class RemoveOnDeathTrait extends Trait {

    public RemoveOnDeathTrait() {
        super("removeondeath");
    }

    @EventHandler
    public void onDeath(NPCDeathEvent event) {
        // 确保监听到的死亡事件属于当前这个 NPC
        if (event.getNPC() == this.getNPC()) {
            // 延迟1个Tick移除，避免与当前的死亡事件处理产生线程冲突
            getNPC().destroy();
        }
    }
}