package net.nuggetmc.mw.special

import net.md_5.bungee.api.ChatColor
import net.nuggetmc.mw.MegaWalls
import net.nuggetmc.mw.utils.MathUtils
import net.nuggetmc.mw.utils.PlayerSafeSet
import net.nuggetmc.mw.utils.PlayerUtils.getNearbyEnemies
import net.nuggetmc.mw.utils.PlayerUtils.getNearbyMobs
import org.bukkit.Material
import org.bukkit.entity.*
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.Vector


class MK2 : Listener {
    val onPig : HashMap<Player, Horse> = HashMap()
    val inCD =PlayerSafeSet()
    val inAccelerate= PlayerSafeSet()
    val accelerateCD= HashMap<Player, Long>()
    fun launchPig(player: Player){
        val pig = PigManager.spawnInvinciblePig(player.location)
        pig.passenger = player
        onPig[player] = pig
        object : BukkitRunnable() {
            var ticks = 0
            var damaged: MutableList<Player> = ArrayList()
            var damagedMobs: MutableList<LivingEntity> = ArrayList()
            override fun run() {
                if (pig.isDead || player.isDead || pig.passenger==null||(!player.isOnline) /*|| ticks >= 600*/
                ) {
                    //player.inventory.forEachIndexed { index, stack -> if (MK2Stick.check(stack)) player.inventory.clear(index) }
                    pig.remove()
                    pig.velocity = Vector(0.0, 0.0, 0.0)
                    onPig.remove(player)
                    cancel()
                    return
                }
                pig.velocity = player.eyeLocation.direction.multiply(if(inAccelerate.contains(player))1.5 else 1.0)
                for (nearby in getNearbyEnemies(player, 5.toDouble())!!) {
                    if (damaged.contains(nearby)) {
                        continue
                    }
                    MegaWalls.getInstance().mwHealth.trueDamage(nearby,7.0,player)
                    damaged.add(nearby)
                }
                for (nearby in getNearbyMobs(player.location, 5.toDouble())!!) {
                    if (damagedMobs.contains(nearby)) {
                        continue
                    }
                    if (nearby.uniqueId.equals(pig.uniqueId)){
                        continue
                    }
                    nearby.damage(6.0, player as Entity)
                    //damagedMobs.add(nearby)
                    //Bu
                }
                ++ticks
            }
        }.runTaskTimer(MegaWalls.getInstance(), 0L, 1L)
    }

    @EventHandler
    fun onRC(e: PlayerInteractEvent){
        val player = e.getPlayer()
        if (!MegaWalls.getInstance().classManager.isMW(player)) {
            return
        }
        if (inCD.contains(player)){
            return
        }
        if (onPig.containsKey(player)){
            launchGhastFireball(player)
            inCD.add(player)
            object : BukkitRunnable() {
                override fun run() {
                    inCD.remove(player)
                }
            }.runTaskLater(MegaWalls.getInstance(), 5)
        }
    }
    @EventHandler
    fun antiHitSelfPig(e: EntityDamageByEntityEvent){
        if (e.damager is Fireball&&(e.damager as Fireball).shooter is Player){
            if (e.entity.type== EntityType.HORSE){
                val uuid = ((e.damager as Fireball).shooter as Player).uniqueId
                if (e.entity?.passenger?.uniqueId?.equals(uuid) == true){
                    e.isCancelled = true
                }
            }
        }
    }
    fun launchGhastFireball(player: Player) {
        // 1. 让玩家生成并发射一个大火球（Fireball 代表恶魂火球，SmallFireball 代表烈焰人小火球）
        val fireball = player.launchProjectile(Fireball::class.java)


        // 2. 设置火球的生成位置略高于玩家头部，避免一生成就撞到玩家自身
        fireball.teleport(player.eyeLocation.add(player.location.direction.multiply(1.5)))


        // 3. 设置火球的飞行速度和方向（朝向玩家视线方向）
        val direction: Vector = player.location.direction
        fireball.velocity = direction.multiply(if(inAccelerate.contains(player)) 1.5 else 1.1) // 1.5 为速度系数，可自由调整


        // 4. 设置恶魂火球特有的属性：方向向量与爆炸威力
        fireball.direction = direction
        fireball.yield = 2.5f // 爆炸威力（恶魂默认值为 1.0）
        fireball.setIsIncendiary(true) // 是否产生火焰（设置为 true 会在爆炸处着火）
    }
    @EventHandler
    fun onDrop(e: PlayerDropItemEvent){
        val player = e.getPlayer()
        if (!MegaWalls.getInstance().classManager.isMW(player)) {
            return
        }
        if (onPig.containsKey(player)){
            e.isCancelled=true

            if (accelerateCD.containsKey(player)){
                player.sendMessage("This is still in a cooldown of ${MathUtils.getCooldownNumber(20000,accelerateCD[player]!!,1)} seconds.")
                return
            }
            if (inAccelerate.contains(player)){
                return
            }
            accelerateCD[player]= System.currentTimeMillis()
            object : BukkitRunnable(){
                override fun run() {
                    accelerateCD.remove(player)
                }
            }.runTaskLater(MegaWalls.getInstance(),20*20)
            player.sendMessage("You accelerated your horse!")
            inAccelerate.add(player)
            object : BukkitRunnable(){
                override fun run() {
                    inAccelerate.remove(player)
                }
            }.runTaskLater(MegaWalls.getInstance(),5*20)

        }
    }
}