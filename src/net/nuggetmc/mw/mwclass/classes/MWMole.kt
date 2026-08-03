package net.nuggetmc.mw.mwclass.classes

import net.md_5.bungee.api.ChatColor
import net.minecraft.server.v1_8_R3.BlockPosition
import net.nuggetmc.mw.events.DiamondCounter
import net.nuggetmc.mw.mwclass.MWClass
import net.nuggetmc.mw.mwclass.info.Diamond
import net.nuggetmc.mw.mwclass.info.MWClassInfo
import net.nuggetmc.mw.mwclass.info.Playstyle
import net.nuggetmc.mw.mwclass.items.MWItem
import net.nuggetmc.mw.mwclass.items.MWKit
import net.nuggetmc.mw.mwclass.items.MWPotions
import net.nuggetmc.mw.special.SpecialEventsManager
import net.nuggetmc.mw.utils.ActionBar
import net.nuggetmc.mw.utils.ItemUtils
import net.nuggetmc.mw.utils.LocationUtils
import net.nuggetmc.mw.utils.PlayerUtils
import org.bukkit.Bukkit
import org.bukkit.Effect
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.Vector
import java.util.*


class MWMole : MWClass() {
    var shortCut: HashMap<Player, Int> = HashMap()
    var junkApple: HashMap<Player, Int> = HashMap()


    init {
        name = arrayOf("Mole", "MOL")
        icon = Material.GOLD_SPADE
        color = ChatColor.YELLOW
        playstyles = arrayOf(
            Playstyle.DAMAGE,
            Playstyle.MOBILITY
        )
        diamonds = arrayOf(
            Diamond.LEGGINGS
        )
        classInfo = MWClassInfo(
            "Dig",
            "We are too lazy to write this.",
            "Shortcut",
            "After digging 3 shovel related blocks.gives Speed II and Haste II for 4 seconds.",
            "Junk food",
            "for every 100 blocks dug,gives a junk apple.",
            "NO GATHERING",
            "Nothing here."
        )
        classInfo.addEnergyGainType("Melee", 10)
        classInfo.addEnergyGainType("Bow", 10)
        classInfo.addEnergyGainType("Per second", 5)
    }

    override fun ability(player: Player) {
        energyManager.clear(player)
        player.addPotionEffect(PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 2 * 20, 0))
        val from: Location = player.location.clone()
        object : BukkitRunnable() {
            var ticks = 0
            var damaged: MutableList<Player> = ArrayList()
            override fun run() {
                if (player.isDead || player.location.distance(from) > 8 || ticks >= 60
                ) {
                    player.velocity = Vector(0.0, 0.0, 0.0)
                    cancel()
                    return
                }
                player.velocity = player.eyeLocation.direction.multiply(0.8)
                for (block1 in LocationUtils.getSphere(player.location, 2)) {
                    if (block1.type === Material.BEDROCK) {
                        continue
                    }
                    if (block1.type === Material.BARRIER) {
                        continue
                    }
                    if (SpecialEventsManager.rp != null && SpecialEventsManager.rp.getAPI(plugin).getRegion("spawn")
                            .isLocationInRegion(block1.location)
                    ) {
                        continue
                    }
                    val material: Material = block1.type
                    if (!material.name.lowercase(Locale.getDefault()).contains("diamond")) {
                        Bukkit.getScheduler()
                            .runTaskLater(plugin, { block1.type = material }, plugin.breakResetTime * 20L)
                    }
                    block1.breakNaturally()
                    if (material != Material.DIAMOND_ORE) {
                        if (!plugin.resetMap.containsKey(block1)) {
                            plugin.resetMap[block1] = material
                        }
                    } else {
                        DiamondCounter.breakDiamond(player,block1.location)
                    }
                }
                for (nearby in getNearbyEnemies(player,  5.toDouble())) {
                    if (damaged.contains(nearby)) {
                        continue
                    }
                    mwhealth.trueDamage(nearby,6.0,player)
                    damaged.add(nearby)
                }
                ++ticks
            }
        }.runTaskTimer(plugin, 0L, 1L)
    }

    private fun getNearbyEnemies(player: Player, radius: Double): List<Player> {
        val players: MutableList<Player> = ArrayList()
        for (other in PlayerUtils.getNearbyPlayers(player.location, radius)) {
            if (plugin.combatManager.isInCombat(other)) {
                if (plugin.teamsManager.isOnSameTeam(player, other)) {
                    continue
                }
                players.add(other)
            }
        }
        return players
    }


    override fun hit(event: EntityDamageByEntityEvent) {
        super.hit(event)
        if (event.isCancelled) return
        val player = event.damager as? Player ?: return
        if (manager[player] !== this) return
        energyManager.add(player, 10)
    }


    override fun assign(player: Player) {
        val items: Map<Int, ItemStack>

        val swordEnch: MutableMap<Enchantment, Int> = HashMap()
        swordEnch[Enchantment.DURABILITY] = 10
        swordEnch[Enchantment.DIG_SPEED] = 10
        swordEnch[Enchantment.DAMAGE_ALL] = 2
        val legginsEnch: MutableMap<Enchantment, Int> = HashMap()
        legginsEnch[Enchantment.PROTECTION_ENVIRONMENTAL] = 3
        legginsEnch[Enchantment.DURABILITY] = 10
        val helmetEnch: MutableMap<Enchantment, Int> = HashMap()
        helmetEnch[Enchantment.DURABILITY] = 10
        helmetEnch[Enchantment.PROTECTION_ENVIRONMENTAL] = 1
        val sword = MWItem.createSword(this, Material.DIAMOND_SPADE, swordEnch, player)
        val bow = MWItem.createBow(this, null)
        val tool = MWItem.createTool(this, Material.DIAMOND_PICKAXE)
        val leggings = MWItem.createArmor(this, Material.DIAMOND_LEGGINGS, legginsEnch)
        val helmet = MWItem.createArmor(this, Material.GOLD_HELMET, helmetEnch)
        val potions = MWPotions.createBasic(this, 2, 8)
        val junkApples = plugin.specialItemUtils.getJunkApple(5)

        items = MWKit.generate(
            this,
            sword,
            bow,
            tool,
            null,
            potions,
            helmet,
            null,
            leggings,
            null,
            Collections.singletonList(junkApples)
        )

        MWKit.assignItems(player, items)
        shortCut[player] = 0
        junkApple[player] = 0

    }

    @EventHandler
    fun shortCut(event: BlockBreakEvent) {
        val player = event.player
        if (manager[player] === this) {
            when (event.block.type) {
                Material.SNOW_BLOCK, Material.SNOW, Material.GRASS, Material.DIRT, Material.SAND, Material.GRAVEL,Material.NETHERRACK
                -> {
                    if (!shortCut.containsKey(player)) {
                        shortCut.put(player, 1)
                    } else {
                        when (shortCut[player]) {
                            0, 1 -> shortCut[player] = shortCut[player]!! + 1
                            2 -> shortCut[player] = 0
                        }
                    }
                    if (shortCut[player] == 0) {
                        player.addPotionEffect(PotionEffect(PotionEffectType.SPEED, 4 * 20, 1))
                        player.addPotionEffect(PotionEffect(PotionEffectType.FAST_DIGGING, 4 * 20, 1))
                    }


                }

                else -> {
                    return
                }
            }

        }
    }

    @EventHandler
    fun JunkApple(e: BlockBreakEvent) {
        val player = e.player
        if (manager[player] === this) {
            if (!junkApple.containsKey(player)) {
                junkApple.put(player, 1)
            } else {
                when (junkApple[player]) {
                    in 0..98 -> junkApple[player] = junkApple[player]!! + 1
                    99 -> junkApple[player] = 0
                }
            }
            if (junkApple[player] == 0) {
                val slot = ItemUtils.findJunkApple(player)
                if (slot != -1 && player.inventory.getItem(slot).amount <= 63) {
                    val amount = player.inventory.getItem(slot).amount
                    val itemStack = player.inventory.getItem(slot).clone()
                    itemStack.amount = amount + 1
                    player.inventory.setItem(slot, itemStack)
                } else {
                    player.inventory.addItem(plugin.specialItemUtils.getJunkApple(1))
                }
            }


        }
    }

    @EventHandler
    fun onLeftClick(e: PlayerInteractEvent) {
        val p = e.player
        if (!e.action.name.contains("LEFT")) return
        if (p.itemInHand == null) return
        if (!plugin.specialItemUtils.isJunkApple(p.itemInHand)) return
        if (manager[p] !== this) return
        if (p.foodLevel == 20) {
            p.foodLevel = 19
        }
    }


    override fun getActionBar(player: Player?): String? {
        return ActionBar.joinActionBar(
            this.color.toString() + ChatColor.BOLD + "ShortCut " + ChatColor.RESET + ChatColor.RESET + shortCut[player] + ChatColor.RESET,
            this.color.toString() + ChatColor.BOLD + "Junk Apple " + ChatColor.RESET + ChatColor.RESET + junkApple[player] + "/100" + ChatColor.RESET
        )
    }
}
