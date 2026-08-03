package net.nuggetmc.mw.events

import net.minecraft.server.v1_8_R3.EnumParticle
import net.nuggetmc.mw.MegaWalls
import net.nuggetmc.mw.utils.ParticleUtils
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer
import org.bukkit.entity.EntityType
import org.bukkit.entity.Item
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityExplodeEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.Vector
import java.util.*
import kotlin.random.Random


object PsychopathManager : Listener {
    private val psychopathSet = HashSet<UUID>()
    @EventHandler
    fun onHit(e: EntityDamageByEntityEvent){
        if (e.damager is Player&&psychopathSet.contains(e.damager.uniqueId)&&e.entity is Player){
            e.damage *= 1.05
            playBloodHitEffect(e.entity as Player)
        }
    }
    @EventHandler
    fun onDeath(e: PlayerDeathEvent){
        val killer: Player? = MegaWalls.getInstance().energyManager.validate(e)
        val victim: Player = e.getEntity()
        if (isPsychopath(victim)){
            removePsychopath(victim)
            victim.sendMessage("You are no longer a psychopath because of your death!")
            if (killer != null&&killer.uniqueId != victim.uniqueId) {
                MegaWalls.getInstance().coinsManager.add(killer,500)
                (killer as CraftPlayer).sendTitle(ChatColor.GOLD.toString()+"Killed a psychopath", ChatColor.GRAY.toString()+"You got extra ${ChatColor.RED.toString()+ ChatColor.BOLD.toString()}500${ChatColor.RESET.toString()+ ChatColor.GRAY} coins!")
                killer.sendMessage(net.md_5.bungee.api.ChatColor.YELLOW.toString() + "+ 500 Coins (Killed Psychopath)!")
                Bukkit.broadcastMessage(ChatColor.AQUA.toString()+ net.md_5.bungee.api.ChatColor.BOLD.toString()+"Psychopath ${victim.name} was terminated by ${killer.name}")
            }
        }

    }
    fun playBloodHitEffect(victim: Player?) {
        if (victim == null || !victim.isOnline) return

        // 取得受害者的胸口位置（加高 1.0 格，避免粒子发在脚底）
        val hitLoc: Location? = victim.location.clone().add(0.0, 1.0, 0.0)

        // 1.8 中 BLOCK_CRACK 格式: BlockID + (Data << 12)
        // 152 是红石块(Redstone Block)的 ID
        val redstoneBlockData = 152


        // 参数：
        // offset(X,Y,Z): 0.25 - 随机飞溅范围
        // speed: 0.1 - 飞溅速度
        // count: 25 - 粒子数量
        ParticleUtils.play(
            EnumParticle.BLOCK_CRACK,
            hitLoc,
            0.25, 0.25, 0.25,
            0.1,
            25,
            redstoneBlockData
        )
    }
    fun addPsychopath(player: Player){
        psychopathSet.add(player.uniqueId)
        object : BukkitRunnable() {

            override fun run() {
                if (!player.isOnline||!psychopathSet.contains(player.uniqueId)) {
                    this.cancel()
                    return
                }
                val itemSet = HashSet<Item>()
                repeat(5){
                    val item2 =player.world.dropItem(player.location.add(0.0, 1.5+Random.nextDouble()*1, 0.0), ItemStack(Material.GOLD_INGOT))
                    item2.setMetadata(MegaWalls.getMetadataValue(), MegaWalls.getFixedMetadataValue())
                    val vector = Vector(
                        (MegaWalls.getRandom().nextDouble() - 0.5) / 1.7,
                        0.35,
                        (MegaWalls.getRandom().nextDouble() - 0.5) / 1.7
                    )
                    item2.velocity = vector
                    itemSet.add(item2)
                }
                object : BukkitRunnable(){
                    override fun run() {
                        itemSet.forEach { it.remove() }
                    }

                }.runTaskLater(MegaWalls.getInstance(),20*5)
            }
        }.runTaskTimer(MegaWalls.getInstance(), 5L, 5L)
    }
    fun removePsychopath(player: Player){
        psychopathSet.remove(player.uniqueId)
    }
    fun isPsychopath(player: Player): Boolean{
        return psychopathSet.contains(player.uniqueId)
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onCustomSkullExplode(event: EntityExplodeEvent) {
        if (event.entityType == EntityType.WITHER_SKULL) {
            // 检查该骷髅头是否带有我们标记的元数据
            if (event.getEntity().hasMetadata("no_terrain_damage")) {
                event.blockList().clear() // 仅禁止带有该标记的骷髅头破坏地形
            }
        }
    }
}