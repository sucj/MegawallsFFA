package net.nuggetmc.mw.special.entities.wither

import net.citizensnpcs.api.event.NPCDamageByEntityEvent
import net.citizensnpcs.api.event.NPCDeathEvent
import net.citizensnpcs.api.npc.NPC
import net.citizensnpcs.api.trait.Trait
import net.citizensnpcs.api.trait.TraitName
import net.minecraft.server.v1_8_R3.NBTTagCompound
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.entity.Wither
import org.bukkit.event.EventHandler
import org.bukkit.inventory.ItemStack
import kotlin.random.Random

@TraitName("player_hit_wither_listener") // Trait 的唯一标识名称
class PlayerHitWitherTrait : Trait("player_hit_wither_listener") {

    @EventHandler
    fun onNPCDamaged(event: NPCDamageByEntityEvent) {
        if (event.npc != this.npc) return
        if (event.npc.entity !is Wither ) return

        val entity = event.npc.entity as? LivingEntity ?: return

        val damager: Player = event.damager as? Player ?: return

        handleNPCDamagedByPlayer(damager,event.npc)
    }
    fun handleNPCDamagedByPlayer(player:Player,npc: NPC){
        if (Random.nextDouble()<0.3){
            WitherNPCUtil.shootCustomSkull(npc,player.location)
        }
    }
}