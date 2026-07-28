package net.nuggetmc.mw.special

import net.minecraft.server.v1_8_R3.EntityPig
import net.nuggetmc.mw.MegaWalls
import net.nuggetmc.mw.utils.PlayerUtils.getNearbyEnemies
import org.bukkit.Material
import org.bukkit.entity.Entity
import org.bukkit.entity.Fireball
import org.bukkit.entity.Pig
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.Vector
import java.util.ArrayList


class MK2 : Listener {
    val onPig : Map<Player, Pig> = HashMap()
    fun launchPig(player: Player){
        val pig = PigManager.spawnInvinciblePig(player.location)
        pig.passenger = player
        object : BukkitRunnable() {
            var ticks = 0
            var damaged: MutableList<Player> = ArrayList()
            override fun run() {
                if (pig.isDead || player.isDead || pig.passenger==null || ticks >= 600
                ) {
                    pig.remove()
                    pig.velocity = Vector(0.0, 0.0, 0.0)
                    cancel()
                    return
                }
                pig.velocity = player.eyeLocation.direction.multiply(0.5)
                for (nearby in getNearbyEnemies(player, 5.toDouble())!!) {
                    if (damaged.contains(nearby)) {
                        continue
                    }
                    nearby.damage(6.0, player as Entity)
                    damaged.add(nearby)
                }
                ++ticks
            }
        }.runTaskTimer(MegaWalls.getInstance(), 0L, 1L)
    }

    @EventHandler
    fun onRC(e: PlayerInteractEvent){
        val player = e.getPlayer()
        if (player.itemInHand == null || player.itemInHand.getType() == Material.AIR) return
        if (player.itemInHand.type.equals(Material.CARROT_STICK)){
            //launchGhastFireball(p)
            launchPig(player)
        }
    }

    fun launchGhastFireball(player: Player) {
        // 1. 让玩家生成并发射一个大火球（Fireball 代表恶魂火球，SmallFireball 代表烈焰人小火球）
        val fireball = player.launchProjectile(Fireball::class.java)


        // 2. 设置火球的生成位置略高于玩家头部，避免一生成就撞到玩家自身
        //fireball.teleport(player.getEyeLocation().add(player.getLocation().getDirection().multiply(1.5)))


        // 3. 设置火球的飞行速度和方向（朝向玩家视线方向）
        val direction: Vector = player.getLocation().getDirection()
        fireball.velocity = direction.multiply(1.5) // 1.5 为速度系数，可自由调整


        // 4. 设置恶魂火球特有的属性：方向向量与爆炸威力
        fireball.setDirection(direction)
        fireball.setYield(1.0f) // 爆炸威力（恶魂默认值为 1.0）
        fireball.setIsIncendiary(true) // 是否产生火焰（设置为 true 会在爆炸处着火）
    }
}