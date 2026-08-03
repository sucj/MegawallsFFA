package net.nuggetmc.mw.mwclass.classes

import net.citizensnpcs.api.CitizensAPI
import net.citizensnpcs.api.npc.NPC
import net.citizensnpcs.trait.Gravity
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.TextComponent
import net.minecraft.server.v1_8_R3.EnumParticle
import net.nuggetmc.mw.MegaWalls
import net.nuggetmc.mw.mwclass.MWClass
import net.nuggetmc.mw.mwclass.info.Diamond
import net.nuggetmc.mw.mwclass.info.MWClassInfo
import net.nuggetmc.mw.mwclass.info.Playstyle
import net.nuggetmc.mw.mwclass.items.MWItem
import net.nuggetmc.mw.mwclass.items.MWKit
import net.nuggetmc.mw.mwclass.items.MWPotions
import net.nuggetmc.mw.utils.ActionBar
import net.nuggetmc.mw.utils.FakePlayer
import net.nuggetmc.mw.utils.ParticleUtils
import net.nuggetmc.mw.utils.TitleUtils
import org.bukkit.*
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Creeper
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerKickEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerTeleportEvent
import org.bukkit.event.player.PlayerVelocityEvent
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.Vector
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class MWMagician : MWClass() {
    val plugin: MegaWalls = MegaWalls.getInstance()!!
    val energyManager = plugin.energyManager!!
    val usedBluffOut=HashSet<Player> ()
    val bluffOutCache=HashSet<Player> ()
    val bluffOutCooldownCache=HashSet<Player> ()
    val energyCostExemption = HashSet<Player>()


    init {
        name = arrayOf("Magician", "MAG")
        val its = ItemStack(Material.STICK)
        its.addUnsafeEnchantment(Enchantment.PROTECTION_PROJECTILE,1)
        its.itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS)
        iconAsItemStack=its
        color = ChatColor.AQUA
        playstyles = arrayOf(
            Playstyle.SUPPORT,
            Playstyle.RUSHER
        )
        diamonds = arrayOf(Diamond.SWORD)
        classInfo = MWClassInfo(
            "Magical Cloak",
            "After you toggle this ability on you will get a walk speed boost,and all damage " +
                    "against you will be blocked.This will consume energy." +
                    "When energy is not enough,this won't work and multiply your damage by 1.25x" +
                    "When this is on,you cannot attack." +
                    "\n Energy cost upon blocking a hit:45"+
                    "\n how to toggle:Left click with your bow",
            "Bluff Out!",
            "When your health comes to lower than 10,you can${ChatColor.RED.toString()+ ChatColor.BOLD} Right Click${ChatColor.RESET} your sword to create a splitting magic of yourself that lasts for 8s," +
                    "then hide yourself for 5s.In the next 12 seconds,you will be immune to" +
                    "knockback , and heal 50% damage on every hit." +
                    "Then fill your ${ChatColor.BOLD.toString() +ChatColor.RED+"✎Overflow Energy"+ChatColor.RESET}." +
                    "By default,you have only one chance to use it." +
                    "However,if Both your energy and ${ChatColor.BOLD.toString() +ChatColor.RED+"✎Overflow Energy"+ChatColor.RESET}" +
                    " this can be activivited but consumes all your energy ," +
                    "and does not fill your ${ChatColor.BOLD.toString() +ChatColor.RED+"✎Overflow Energy"+ChatColor.RESET}." +
                    "it still has a cooldown of 90 seconds.",
            "Overflow Energy",
            "Hitting an enemy gives you ${ChatColor.BOLD.toString() +ChatColor.RED+"✎Overflow Energy"} instead of energy." +
                    "${ChatColor.BOLD.toString() +ChatColor.RED+"✎Overflow Energy"} will be used as energy as well." +
                    "\n However,they have a cap at 50." +
                    "\n Only if you reached your ${ChatColor.BOLD.toString() +ChatColor.RED+"✎Overflow Energy"} cap," +
                    "${ChatColor.BOLD.toString() +ChatColor.RED+"✎Overflow Energy"} you gain will be divided by 2 and then" +
                    "added into your basic energy.",
            "Nothing",
            "Nothing here."
        )
        classInfo.addEnergyGainType("Melee", 6)
        classInfo.addEnergyGainType("Bow", 6)
        classInfo.addEnergyGainType("Per second", 1)
    }

    override fun ability(player: Player) {
        toggle(player)
    }
    fun toggle(player: Player){
        if (inCloakCache.contains(player)){
            inCloakCache.remove(player)
            player.walkSpeed=0.2f
            player.sendMessage(ChatColor.RED.toString()+ChatColor.BOLD+"You deactivated your ${ChatColor.YELLOW.toString()+"Magical Cloak"+ChatColor.RED.toString()+ChatColor.BOLD} ability!")
        }else{
            veilCreeper(player)
            inCloakCache.add(player)
            player.walkSpeed=0.45f
            player.sendMessage(ChatColor.GREEN.toString()+ChatColor.BOLD+"You activated your ${ChatColor.RESET.toString()+ ChatColor.YELLOW+"Magical Cloak"+ChatColor.GREEN.toString()+ChatColor.BOLD} ability!")
        }
    }
    @EventHandler
    fun onVelocity(e:PlayerVelocityEvent){
        if(bluffOutCache.contains(e.player)){
            e.isCancelled=true
        }
    }


    override fun hit(event: EntityDamageByEntityEvent) {
        super.hit(event)
        if (event.isCancelled) return
        val player = energyManager.validate(event) ?: return
        if (manager[player] == this) {
            if (inCloakCache.contains(player)){
                event.isCancelled=true
                return
            }
            if (bluffOutCache.contains(player)) {
                mwhealth.heal(player, 0.5 * event.damage)
            }
            if (overflowEnergyMap[player]!! >=50) {
                energyManager.add(player, 3)
            }else{
                overflowEnergyMap[player] = overflowEnergyMap[player]!! + 6
            }
        }
    }
    /*@EventHandler
    fun onCreeperDamaged(e: EntityDamageByEntityEvent) {
        if (e.entity !is Creeper) return
        if (!(e.entity as Creeper).isPowered) return
        if (!e.entity.hasMetadata("OwnerName")) return
        if (e.damager !is Player) return
        val owner=Bukkit.getPlayerExact(e.entity.getMetadata("OwnerName")[0].asString()) ?: return
        if (owner.uniqueId.equals(e.damager.uniqueId)) return
        if ((e.damager as Player).itemInHand.type.equals(Material.BOW)&&manager[e.damager as Player]==this){
            toggle(e.damager as Player)
        }
        if (inCloakCache.contains(owner)){
            if (owner.consumeEnergy(20)){
                e.isCancelled=true
                owner.world.playSound(owner.location,Sound.CREEPER_HISS,1.5f,1f)
                e.damager.sendMessage("Your damage dealt to ${ChatColor.RED.toString()+ChatColor.BOLD+owner.displayName+ChatColor.RESET} was cancelled due to their ${ChatColor.AQUA.toString()+ChatColor.BOLD+"Magical Cloak"+ChatColor.RESET} ability!")
            }else{
                owner.sendMessage(ChatColor.RED.toString() +ChatColor.BOLD+"You didn't block a hit because you don't have enough energy!")
            }
        }
    }*/
    @EventHandler
    fun onDamaged(e:EntityDamageEvent){
        if (e.entity !is Player) return
        if (e.isCancelled) return
        val victim=e.entity as Player
        if (manager[victim]==null) return
        if (manager[victim]!=this) return
        if (e.cause== EntityDamageEvent.DamageCause.SUICIDE) return
        if (e is EntityDamageByEntityEvent && inCloakCache.contains(e.damager)){
            return
        }
        if (e is EntityDamageByEntityEvent && e.damager is Player && e.damager.uniqueId==victim.uniqueId){
            return
        }
        if (inCloakCache.contains(victim)){
            if (energyCostExemption.contains(victim)){
                e.isCancelled=true
                victim.world.playSound(victim.location,Sound.CREEPER_HISS,1.5f,1f)
                if (e is EntityDamageByEntityEvent){
                    e.damager.sendMessage("Your damage dealt to ${ChatColor.RED.toString()+ChatColor.BOLD+victim.displayName+ChatColor.RESET} was cancelled due to their ${ChatColor.AQUA.toString()+ChatColor.BOLD+"Magical Cloak"+ChatColor.RESET} ability!")
                }
            }else if (victim.consumeEnergy(45)){
                e.isCancelled=true
                victim.world.playSound(victim.location,Sound.CREEPER_HISS,1.5f,1f)
                energyCostExemption.add(victim)
                object : BukkitRunnable(){
                    override fun run() {
                        energyCostExemption.remove(victim)
                    }

                }.runTaskLater(plugin,10)
                if (e is EntityDamageByEntityEvent){
                    e.damager.sendMessage("Your damage dealt to ${ChatColor.RED.toString()+ChatColor.BOLD+victim.displayName+ChatColor.RESET} was cancelled due to their ${ChatColor.AQUA.toString()+ChatColor.BOLD+"Magical Cloak"+ChatColor.RESET} ability!")
                }
            }else{
                victim.sendMessage(ChatColor.RED.toString() +ChatColor.BOLD+"You didn't block a hit because you don't have enough energy!Multiplied your damage taken by 1.25x.")
                e.damage*=1.25
            }
        }
        if(!e.isCancelled&&victim.health-e.damage<10){
            if (!usedBluffOut.contains(victim)){
                usedBluffOut.add(victim)
                bluffOut(victim,true,e)
            }else if(energyManager[victim]==100&& overflowEnergyMap[victim]!! >=50&&!bluffOutCooldownCache.contains(victim)){
                energyManager.clear(victim)
                overflowEnergyMap[victim] = 0
                bluffOutCooldownCache.add(victim)
                object :BukkitRunnable(){
                    override fun run() {
                        bluffOutCooldownCache.remove(victim)
                    }
                }.runTaskLater(plugin,90*20)
                bluffOut(victim,false,e)
            }
        }
    }


    override fun assign(player: Player) {
        val items: Map<Int, ItemStack>

        val swordEnch: MutableMap<Enchantment, Int> = HashMap()
        swordEnch[Enchantment.DURABILITY] = 10
        swordEnch[Enchantment.DAMAGE_ALL] = 2


        val helmetench: MutableMap<Enchantment, Int> = HashMap()
        helmetench[Enchantment.PROTECTION_PROJECTILE] = 1
        helmetench[Enchantment.PROTECTION_ENVIRONMENTAL] = 2
        helmetench[Enchantment.DURABILITY] = 10

        val sword = MWItem.createSword(this, Material.DIAMOND_SWORD, swordEnch, player)
        val tool = MWItem.createTool(this, Material.DIAMOND_PICKAXE)
        val helmet = MWItem.createArmor(this, Material.DIAMOND_HELMET, helmetench)
        val potions = MWPotions.createBasic(this, 2, 8, 2)

        items = MWKit.generate(this, sword, null, tool, null, potions, helmet, null, null, null, null)

        MWKit.assignItems(player, items)
        overflowEnergyMap[player] = 0
        if (usedBluffOut.contains(player)){
            usedBluffOut.remove(player)
        }
        if (bluffOutCache.contains(player)){
            bluffOutCache.remove(player)
        }
        if (bluffOutCooldownCache.contains(player)){
            bluffOutCooldownCache.remove(player)
        }
    }

    override fun getActionBar(player: Player?): String? {
        val echo = this.color.toString() + ChatColor.BOLD.toString() + "Magical Cloak ${
            if (inCloakCache.contains(player)) ChatColor.GREEN.toString() + ChatColor.BOLD.toString() + "ENABLED" else ChatColor.RED.toString() + ChatColor.BOLD.toString() + "DISABLED"
        }"
        val bluffOut = this.color.toString() + ChatColor.BOLD.toString() + "Bluff Out ${
            if ((!usedBluffOut.contains(player))||(energyManager[player]==100&& overflowEnergyMap[player]!! >=50&&!bluffOutCooldownCache.contains(player))) ChatColor.GREEN.toString() + ChatColor.BOLD.toString() + "✔" else ChatColor.RED.toString() + ChatColor.BOLD.toString() + "✖"
        }"
        val overflow =
            this.color.toString() + ChatColor.BOLD.toString() + "✎Overflow Energy ${overflowEnergyMap[player]}"
        return ActionBar.joinActionBar(echo,bluffOut,overflow)
    }
    companion object {
        val inCloakCache = HashSet<Player>()
        val overflowEnergyMap = HashMap<Player,Int>()
        @JvmStatic
        fun Player.consumeEnergy (amount:Int) :Boolean {
            if (overflowEnergyMap[player]!!>=amount){
                overflowEnergyMap[player] = overflowEnergyMap[player]!! - amount
            }else if (overflowEnergyMap[player]!!+MegaWalls.getInstance().energyManager[player]>=amount){
                var toConsume=amount
                toConsume-=overflowEnergyMap[player]!!
                overflowEnergyMap[player] = 0
                MegaWalls.getInstance().energyManager[player] -= toConsume
            }else{
                return false
            }
            return true
        }
    }

    /**
     * 计算在指定位置周围，以给定半径均匀分布的位置集合
     * 所有位置到中心的距离都严格等于 radius，相邻位置之间的弧长相等
     *
     * @param location   中心位置
     * @param radius     圆的半径（单位：方块）
     * @param amount     位置数量
     * @param startAngle 起始角度（弧度），默认 0.0（正东方向）
     * @return 均匀分布在圆上的 Location 列表
     */
    fun getLocationsOnCircle(
        location: Location,
        radius: Double,
        amount: Int,
        startAngle: Double = 0.0
    ): List<Location> {
        if (amount <= 0) return emptyList()
        val world = location.world ?: return emptyList()

        val angleStep = 2 * PI / amount
        val result = mutableListOf<Location>()

        for (i in 0 until amount) {
            val angle = startAngle + i * angleStep

            val x = location.x + radius * cos(angle)
            val z = location.z + radius * sin(angle)
            val y = location.y

            result.add(Location(world, x, y, z))
        }

        return result
    }
    fun veilCreeper(player: Player) {
        val radius = 3.5
        val amount = 6
        fun createVeilCreeper(location: Location): NPC {
            val creeper = CitizensAPI.getNPCRegistry().createNPC(EntityType.CREEPER, "Veil Creeper")
            creeper.spawn(location)
            creeper.setUseMinecraftAI(false)
            (creeper.entity as Creeper).isPowered = true;
            (creeper.entity as Creeper).addPotionEffect(PotionEffect(PotionEffectType.INVISIBILITY, 9999, 255))
            val gravityTrait = creeper.getOrAddTrait(Gravity::class.java)
            gravityTrait.toggle()
            creeper.entity.setMetadata("OwnerName", FixedMetadataValue(plugin, player.name))
            return creeper
        }
        val creeperLocations = getLocationsOnCircle(player.location,radius,amount)
        val creepers = ArrayList<NPC>()
        for (loc in creeperLocations){
            creepers.add(createVeilCreeper(loc))
        }
        object :BukkitRunnable(){
            override fun run() {

                if (manager[player]!=this@MWMagician){
                    inCloakCache.remove(player)
                }

                if (!inCloakCache.contains(player)){
                    creepers.forEach { it.destroy() }
                    cancel()
                }else {
                    creepers.zip(getLocationsOnCircle(player.location,radius,amount)).forEach { (creeper,location)->
                        run {
                            creeper.teleport(
                                location,
                                PlayerTeleportEvent.TeleportCause.PLUGIN
                            )
                            creeper.entity.velocity = Vector(0, 0, 0)
                        }
                    }
                }
            }
        }.runTaskTimer(plugin,0,0)
    }
    @EventHandler
    fun onDeath(e:PlayerDeathEvent){
        if (inCloakCache.contains(e.entity)) {
            inCloakCache.remove(e.entity)
        }
        if (bluffOutCache.contains(e.entity)){
            bluffOutCache.remove(e.entity)
        }
        e.entity.walkSpeed=0.2f
    }
    @EventHandler
    fun onDisconnect(e:PlayerQuitEvent){
        if (inCloakCache.contains(e.player)) {
            inCloakCache.remove(e.player)
        }
        if (bluffOutCache.contains(e.player)){
            bluffOutCache.remove(e.player)
        }
    }
    @EventHandler
    fun onKick(e:PlayerKickEvent){
        if (inCloakCache.contains(e.player)) {
            inCloakCache.remove(e.player)
        }
        if (bluffOutCache.contains(e.player)){
            bluffOutCache.remove(e.player)
        }
    }
    fun bluffOut(player: Player,fillOverflow:Boolean,event: EntityDamageEvent){
        (player as CraftPlayer).sendTitle("", ChatColor.RED.toString()+"Bluff Out activated")
        val location=player.location
        val fakePlayer= FakePlayer(player)
        bluffOutCache.add(player)
        object : BukkitRunnable() {
            override fun run() {
                bluffOutCache.remove(player)
            }
        }.runTaskLater(plugin,12*20)
        for (p: Player in plugin.combatManager.inCombatPlayers) {
            p.hidePlayer(player)
        }
        object : BukkitRunnable() {
            override fun run() {
                for (p: Player in plugin.combatManager.inCombatPlayers) {
                    p.showPlayer(player)
                }
            }
        }.runTaskLater(plugin,5*20)

        if (event is EntityDamageByEntityEvent && event.damager is Player) {
            fakePlayer.npc.navigator.setTarget(event.damager, false)
        }
        object : BukkitRunnable() {
            override fun run() {
                fakePlayer.delete()
            }
        }.runTaskLater(plugin,8*20)
        if (fillOverflow) {
            overflowEnergyMap[player] = 54
        }
        ParticleUtils.play(EnumParticle.SMOKE_LARGE,location, 0.1, 0.1, 0.1, 0.0, 3)
    }

}
