package net.nuggetmc.mw.utils

import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.entity.EntityDamageByEntityEvent

object EventDumper {
    fun dumpDamager(event: EntityDamageByEntityEvent): Entity? {

        val damager: Entity?

        if (event.damager is Projectile) {
            val projectile = event.damager as Projectile
            damager = projectile.shooter as Entity?
        } else {
            damager=event.damager
        }

        if (event.damage == 0.0 || event.isCancelled) return null

        return damager
    }
    @JvmOverloads
    fun dumpDamagerPlayer(event: EntityDamageByEntityEvent,requireVictimAlsoPlayer: Boolean =true): Player?{
        if (dumpDamager(event) !is Player){
            return null
        }else{
            if (requireVictimAlsoPlayer&& event.entity !is Player){
                return null
            }

            return dumpDamager(event) as? Player
        }
    }
}