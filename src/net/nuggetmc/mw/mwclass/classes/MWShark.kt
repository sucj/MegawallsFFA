package net.nuggetmc.mw.mwclass.classes

import net.md_5.bungee.api.ChatColor
import net.nuggetmc.mw.MegaWalls
import net.nuggetmc.mw.mwclass.MWClass
import net.nuggetmc.mw.mwclass.info.Diamond
import net.nuggetmc.mw.mwclass.info.MWClassInfo
import net.nuggetmc.mw.mwclass.info.Playstyle
import net.nuggetmc.mw.mwclass.items.MWItem
import net.nuggetmc.mw.mwclass.items.MWKit
import net.nuggetmc.mw.mwclass.items.MWPotions
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockPhysicsEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

class MWShark : MWClass() {
    private var waterMap: HashMap<Player, HashSet<Block>> = HashMap()

    init {
        name = arrayOf("鲨鱼", "Shark", "SRK")
        icon = Material.WATER_BUCKET
        color = ChatColor.DARK_AQUA
        playstyles = arrayOf(
            Playstyle.FIGHTER,
            Playstyle.SUPPORT
        )
        diamonds = arrayOf(
            Diamond.BOOTS
        )
        classInfo = MWClassInfo(
            "From the Depths",
            "§7Create a 7§7 block square area of water §7around you for §a5§7 seconds§7§8 ? §7" +
                    "You also receive Regeneration I for the §7duration of your ability§7§8 ? §7" +
                    "If an enemy interracts with your pool of §7water, " +
                    "they will receive Slowness I until the §7water pool disappears§7§8 ? §7§7",
            "Blood Rage",
            "§7§8 ? §7§7If you or an enemy within a 9 block §7radius is under 15 HP," +
                    " you deal +§a21.5%§7 damage\n§7§8 ? §7§7This considers up to a maximum of 5 §7players," +
                    " or +§a107.5%§7 damage§7§8 ? §7§7You and nearby allies deal +§a0.75§7 §7" +
                    "extra damage when standing or attacking enemies in" +
                    " §7the water that comes from your ability.§7§8 ? §7This has a maximum of +1.5 damage.",
            "Food Hunt",
            "§7§8 ? §7§7Kills grant Regeneration III for §8 §8§a4§7 seconds and replenishes 4 hunger.\n If there is allies in a 4 block range,you will heal for 3.5 HP.",
            "Sea Treasure",
            "Nothing here."
        )
        classInfo.addEnergyGainType("Melee", 20)
        classInfo.addEnergyGainType("Bow", 20)
    }

    override fun ability(player: Player) {
        energyManager.clear(player)
        if (!waterMap.containsKey(player)) {
            waterMap[player] = emptySet<Block>().toHashSet()
        }
        player.addPotionEffect(PotionEffect(PotionEffectType.REGENERATION, 5 * 20, 0))
        val expand = 3
        val posY = player.location.blockY
        val location = player.location
        val world = player.world
        val set = HashSet<Block>()
        for (i in -expand until expand + 1) {
            val block = world.getBlockAt(location.blockX + i, posY, location.blockZ)
            if (block.canBeReplaced()) {


                block.setType(Material.STATIONARY_WATER, false)
                waterMap[player]?.add(block)
                if (!set.contains(block)) {
                    set.add(block)
                }
                for (j in -expand until expand + 1) {
                    val block1 = world.getBlockAt(location.blockX + i, posY, location.blockZ + j)
                    if (block1.canBeReplaced()) {

                        block1.setType(Material.STATIONARY_WATER, false)
                        waterMap[player]?.add(block1)
                        if (!set.contains(block1)) {
                            set.add(block1)
                        }
                    }
                }
            }
        }
        if (set.isNotEmpty()) {
            Bukkit.getScheduler().runTaskLater(
                MegaWalls.getInstance(),
                {
                    for (i in 0 until set.size) {
                        val block = set.toArray()[i] as Block
                        block.type = Material.AIR
                        waterMap[player]?.remove(block)
                    }
                }, 5 * 20
            )
        }
    }


    override fun hit(event: EntityDamageByEntityEvent) {
        super.hit(event)
        if (event.isCancelled) return
        val player = energyManager.validate(event) ?: return
        if (manager[player] !== this) return
        energyManager.add(player, 20)
    }

    @EventHandler
    fun onDamage(e: EntityDamageEvent) {
        if (e.isCancelled) return
        if (e.entity !is Player) return
        val victim = e.entity as Player
        if (manager[victim] !== this) {
            return
        }
        //suffer from dmg
    }


    override fun assign(player: Player) {
        val items: Map<Int, ItemStack>
        if (MWKit.contains(this)) {
            items = MWKit.fetch(this)
        } else {
            val swordEnch: MutableMap<Enchantment, Int> = HashMap()
            swordEnch[Enchantment.DURABILITY] = 10
            val armorEnch: MutableMap<Enchantment, Int> = HashMap()
            armorEnch[Enchantment.DEPTH_STRIDER] = 3
            armorEnch[Enchantment.PROTECTION_ENVIRONMENTAL] = 2
            armorEnch[Enchantment.DURABILITY] = 10
            val sword = MWItem.createSword(this, Material.DIAMOND_SWORD, swordEnch)
            val bow = MWItem.createBow(this, null)
            val tool = MWItem.createTool(this, Material.DIAMOND_PICKAXE)
            val boots = MWItem.createArmor(this, Material.DIAMOND_BOOTS, armorEnch)
            val potions = MWPotions.createBasic(this, 2, 8, 2)

            items = MWKit.generate(this, sword, bow, tool, null, null, potions, null, null, null, boots, null)
        }
        MWKit.assignItems(player, items)


    }

    @EventHandler
    fun onPhysics(e: BlockPhysicsEvent) {
        for (player: Player in waterMap.keys) {
            for (block: Block in waterMap[player]!!) {
                if (e.block == block) {
                    e.isCancelled = true
                    return
                }
            }
        }
    }

    @EventHandler
    fun onMove(e: PlayerMoveEvent) {
        if (e.player.location.block.type == Material.WATER || e.player.location.block.type == Material.STATIONARY_WATER) {
            val b = forBlock(e) ?: return
            if (plugin.teamsManager.isOnSameTeam(e.player, b.get(1) as Player)) {
                return
            }
            e.player.addPotionEffect(PotionEffect(PotionEffectType.SLOW, 1 * 20, 0))
        }
    }

    @EventHandler
    fun onBloodRageAllies(event: PlayerMoveEvent) {
        val player = event.player
        if (isInAlliesPool(player)) {
            plugin.bloodRageList.add(player)
        } else {
            if (plugin.bloodRageList.contains(player)) {
                plugin.bloodRageList.remove(player)
            }
        }

    }

    @EventHandler
    fun onBloodRageSelf(event: EntityDamageByEntityEvent) {
        super.hit(event)
        if (event.isCancelled) return
        val player = energyManager.validate(event) ?: return
        if (manager[player] !== this) return
        var increaceAmount = 0.0
        if (player.health < 15) increaceAmount += 0.215
        for (p in Bukkit.getOnlinePlayers()) {
            if (p.location.distance(player.location) > 9) {
                continue
                //too far
            }
            if (plugin.teamsManager.isOnSameTeam(player, p)) {
                continue
                //we need enemies
            }
            if (!plugin.combatManager.isInCombat(p)) {
                continue
            }
            if (player.world !== p.world) continue
            if (p.isDead) continue
            increaceAmount += 0.215
        }
        if (increaceAmount > 1.075) {
            increaceAmount = 1.075
        }
        event.damage = event.damage + event.damage * increaceAmount
    }

    @EventHandler
    fun onKill(event: PlayerDeathEvent) {
        val victim = event.entity
        val player = victim.killer
        if (player == null || victim === player) return
        if (manager[player] === this) {
            player.addPotionEffect(PotionEffect(PotionEffectType.REGENERATION, 4 * 20, 2))
            if (player.foodLevel + 4 > 20) {
                player.foodLevel = 20
            } else {
                player.foodLevel += 4
            }
            var b = false
            for (p in Bukkit.getOnlinePlayers()) {
                if (p.location.distance(player.location) > 4) {
                    continue
                    //too far
                }
                if (!plugin.teamsManager.isOnSameTeam(player, p)) {
                    continue
                    //we need allies
                }
                if (!plugin.combatManager.isInCombat(p)) {
                    continue
                }
                if (player.world !== p.world) continue
                if (p.isDead) continue
                b = true
                break
            }
            if (b) {
                if (player.health + 3.5 > player.maxHealth) {
                    player.health = player.maxHealth
                } else {
                    player.health += 3.5
                }
            }
        }
    }

    private fun forBlock(e: PlayerMoveEvent): Array<Any>? {
        for (player: Player in waterMap.keys) {
            for (block: Block in waterMap.get(player)!!) {
                if (block == e.player.location.block) {
                    return arrayOf(block, player)
                }
            }
        }
        return null
    }

    private fun isInAlliesPool(plr: Player): Boolean {
        for (player: Player in waterMap.keys) {
            if (!plugin.teamsManager.isOnSameTeam(plr, player)) {
                continue
            }
            for (block: Block in waterMap.get(player)!!) {
                if (block == plr.location.block) {
                    return true
                }
            }
        }
        return false
    }

    private fun Block.canBeReplaced(): Boolean {
        return when (this.type) {
            Material.AIR, Material.LONG_GRASS, Material.YELLOW_FLOWER, Material.DOUBLE_PLANT -> true
            else -> false
        }
    }
}
