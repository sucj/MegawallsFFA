package net.nuggetmc.mw.utils

import org.bukkit.entity.Arrow
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageByEntityEvent

object EventDumper {
    fun dumpDamager(event: EntityDamageByEntityEvent): Entity? {

        val damager: Entity?

        if (event.damager is Arrow) {
            val arrow = event.damager as Arrow
            damager = arrow.shooter as Entity?
        } else {
            damager=event.damager
        }

        if (event.damage == 0.0 || event.isCancelled) return null

        return damager
    }
    fun dumpDamagerPlayer(event: EntityDamageByEntityEvent): Player?{
        if (dumpDamager(event) !is Player){
            return null
        }else{
            return dumpDamager(event) as Player
        }
    }
}