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
import net.nuggetmc.mw.utils.ItemUtils
import org.bukkit.Bukkit
import org.bukkit.DyeColor
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Arrow
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.scheduler.BukkitRunnable
import kotlin.math.absoluteValue
import kotlin.math.cos
import kotlin.math.sin

class MWAsn :MWClass(){
    var sc=HashMap<Player,Int>()
    var plugin=MegaWalls.getInstance()
    val dmgHandle= HashMap<Player,Double>()
    private val shadowStepCache: HashSet<Player> = HashSet()
    private val maCache: HashSet<Player> = HashSet()
    init {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, { this.tickArrowCatch() }, 0, 1)
        name = arrayOf("刺客", "Assassin", "ASN")
        val its=ItemStack(Material.STAINED_GLASS)
        its.durability= DyeColor.BLACK.data.toShort()
        iconAsItemStack = its
        color = ChatColor.BLACK
        playstyles = arrayOf(
            Playstyle.FIGHTER,
            Playstyle.MOBILITY
        )
        diamonds = arrayOf(
            Diamond.SWORD
        )
        classInfo = MWClassInfo(
            "Shadow Cloak",
            "§7Become invisible and gain Speed I and §7Resistance I for §a10§7 seconds\n§7Attacking an enemy or a wither cancels  §7Shadow Cloak & your Resistance effect, dealing §7extra true damage equaling to §a10%§7 of that§7enemy's missing health, and refunds 4 Energy for §7each second of invisibility that remained",
            "Shadow Step",
            "§7When sneaking and taking melee or ranged, §7damage within §a25§7 blocks, you will teleport §7behind your attacker and not take damage.\n §7This does not trigger while Shadow Cloak is §7active.\n§7Cooldown: §a10s",
            "Master Alchemist",
            "When losing over 10 HP§7 within 1 §7second, you automatically drink a restorative shot §7granting Regeneration §aIII§7 for §a5§7 seconds.\n§7Cooldown: §a12s",
            "§7Arrow Catch",
            " §7You will catch any arrows that are shot in §7the direction you are facing from an opponent, catching 1 arrow adds §a3§7 arrows to §7your inventory if it is not full"
        )
        classInfo.addEnergyGainType("Melee", 10)
        classInfo.addEnergyGainType("Bow", 10)
    }
    fun tickArrowCatch(){
        for (p in plugin.combatManager.inCombatPlayers){
            if (manager[p]==this){
                if (ItemUtils.isFullInventory(p.inventory)){
                    continue
                }
                for (e:Entity in p.getNearbyEntities(1.5,1.5,1.5)){
                    if (e is Arrow&&p.hasLineOfSight(e)){
                        if(e.shooter !is Player || plugin.teamsManager.isOnSameTeam(p,e.shooter as Player)){
                            continue
                        }
                        e.remove()
                        p.inventory.addItem(ItemStack(Material.ARROW,1))
                    }
                }
            }
        }
    }
    @EventHandler
    fun onShadowStep(e:EntityDamageByEntityEvent){
        super.hit(e)
        if (e.isCancelled) return
        val player= energyManager.validate(e) ?: return
        val victim=e.entity as Player
        if (manager[victim] !== this) return
        if (sc.containsKey(victim)) return
        if (shadowStepCache.contains(victim)){
            return
        }
        if (player.location.distance(victim.location)>25){
            return
        }
        if (!victim.isSneaking) return
        e.isCancelled=true
        shadowStepCache.add(victim)
        val loc=player.location.clone()
        val yaw=loc.yaw
        val abs=yaw.absoluteValue
        val amount=2
        when(abs){
            0.toFloat()->loc.x-=amount
            180.toFloat(),-180.toFloat()->loc.x+=amount
            90.toFloat()->loc.z-=amount
            -90.toFloat()->loc.z+=amount
        }
        if (yaw<0&&yaw>-90){
            loc.z -= sin(yaw)
            loc.x-= cos(amount.toDouble())
        }else if(yaw>90&&yaw<180){
            loc.z -= sin(yaw)
            loc.x+= cos(amount.toDouble())
        }else if (yaw>0&&yaw<90){
            loc.z += sin(yaw)
            loc.x -= cos(amount.toDouble())
        }else if (yaw<-90&&yaw>-180){
            loc.z += sin(yaw)
            loc.x += cos(amount.toDouble())
        }
        victim.teleport(loc)
        object:BukkitRunnable(){
            override fun run() {
                shadowStepCache.remove(victim)
            }
        }.runTaskLater(plugin,10*20)
    }
    @EventHandler
    fun onMA(e: EntityDamageEvent){
        if(e.entity !is Player) return
        if (e.isCancelled) return
        val victim=e.entity as Player
        if (manager[victim] !== this) return
        if (maCache.contains(victim)&&!dmgHandle.containsKey(victim)) {
            return
        }
        maCache.add(victim)
        if (dmgHandle.containsKey(victim)){
            dmgHandle[victim] = dmgHandle[victim]!! + e.damage
        }else{
            dmgHandle[victim]=e.damage
        }
        object:BukkitRunnable(){
            override fun run() {
                if (dmgHandle.containsKey(victim)&& dmgHandle[victim]!! >=10){
                    victim.addPotionEffect(PotionEffect(PotionEffectType.REGENERATION,5*20,2))
                    dmgHandle.remove(victim)
                }
            }
        }.runTaskLater(plugin,20)
        object:BukkitRunnable(){
            override fun run() {
                maCache.remove(victim)
            }
        }.runTaskLater(plugin,12*20)
    }

    override fun ability(player: Player) {
        energyManager.clear(player)
        sc.put(player,6)
        object :BukkitRunnable(){
            override fun run() {
                if ((!sc.containsKey(player))|| sc[player]!! <=0){
                    cancel()
                    sc.remove(player)
                    for (p:Player in plugin.combatManager.inCombatPlayers){
                        p.showPlayer(player)
                    }
                    return
                }
                sc[player]=(sc[player] as Int) -1
                if ((!sc.containsKey(player))|| sc[player]!! <=0){
                    cancel()
                    sc.remove(player)
                    for (p:Player in plugin.combatManager.inCombatPlayers){
                        p.showPlayer(player)
                    }
                }
            }
        }.runTaskTimer(plugin,0,20)
        player.addPotionEffect(PotionEffect(PotionEffectType.SPEED,6*20,0))
        player.addPotionEffect(PotionEffect(PotionEffectType.DAMAGE_RESISTANCE,6*20,0))
        player.addPotionEffect(PotionEffect(PotionEffectType.INVISIBILITY,6*20,0))
        for (p:Player in plugin.combatManager.inCombatPlayers){
            p.hidePlayer(player)
        }
    }


    override fun hit(event: EntityDamageByEntityEvent) {
        super.hit(event)
        if (event.isCancelled) return
        val player = energyManager.validate(event) ?: return
        val victim=event.entity as Player
        if (manager[player] !== this) return
        energyManager.add(player, 10)
        if (sc.containsKey(player)){
            player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE)
            player.removePotionEffect(PotionEffectType.INVISIBILITY)
            plugin.energyManager.add(player,sc[player] as Int *4)
            sc.remove(player)
            mwhealth.trueDamage(victim,event.damage/10,player)
            for (p:Player in plugin.combatManager.inCombatPlayers){
                p.showPlayer(player)
            }
        }
    }




    override fun assign(player: Player) {
        val items: Map<Int, ItemStack>

            val swordEnch: MutableMap<Enchantment, Int> = HashMap()
            swordEnch.put(Enchantment.DURABILITY, 10)
            swordEnch.put(Enchantment.DAMAGE_ALL, 1)
            val armorEnch: MutableMap<Enchantment, Int> = HashMap()
            armorEnch.put(Enchantment.PROTECTION_FALL, 2)
            armorEnch.put(Enchantment.PROTECTION_PROJECTILE, 2)
            armorEnch.put(Enchantment.DURABILITY, 10)
            val sword = MWItem.createSword(this, Material.DIAMOND_SWORD, swordEnch,player)
            val bow = MWItem.createBow(this, null)
            val tool = MWItem.createTool(this, Material.DIAMOND_PICKAXE)
            val boots = MWItem.createArmor(this, Material.DIAMOND_BOOTS, armorEnch)
            val potions = ArrayList<ItemStack>()
            potions.add(MWPotions.createAsnPotions(this.color,5))

            items = MWKit.generate(this, sword, bow, tool, null, null, potions, null, null, null, boots, null)

        MWKit.assignItems(player, items)

        shadowStepCache.remove(player)
        maCache.remove(player)
        dmgHandle.remove(player)


    }



}