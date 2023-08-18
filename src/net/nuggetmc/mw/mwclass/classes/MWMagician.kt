package net.nuggetmc.mw.mwclass.classes

import net.citizensnpcs.api.CitizensAPI
import net.md_5.bungee.api.ChatColor
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
import org.bukkit.*
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

class MWMagician : MWClass() {
    val plugin: MegaWalls = MegaWalls.getInstance()!!
    val energyManager = plugin.energyManager!!
    val usedBluffOut=HashSet<Player> ()
    val bluffOutCache=HashSet<Player> ()
    val bluffOutCooldownCache=HashSet<Player> ()


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
            "After you toggle this ability on you will get a walkspeed boost,and all damage " +
                    "against you will be blocked.This will consume energy." +
                    "When energy is not enough,this won't work." +
                    "When this is on,you cannot attack." +
                    "\n Energy cost upon blocking a hit:15"+
                    "\n how to toggle:Left click with your bow",
            "Bluff Out!",
            "When your health comes to lower than 10,you will immediately create a splitting magic of yourself that lasts for 8s," +
                    "then hide yourself for 5s.In the next 12 seconds,you will be immune to" +
                    "knockback , and heal 50% damage on every hit." +
                    "Then fill your ${ChatColor.BOLD.toString() +ChatColor.RED+"✎Overflow Energy"+ChatColor.RESET}." +
                    "By default,you have only one chance to use it." +
                    "However,if Both your energy and ${ChatColor.BOLD.toString() +ChatColor.RED+"✎Overflow Energy"+ChatColor.RESET}" +
                    " this can be activivited but consumes all your energy ," +
                    "and does not fill your ${ChatColor.BOLD.toString() +ChatColor.RED+"✎Overflow Energy"+ChatColor.RESET}." +
                    "it still has a cooldown of 180 seconds.",
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
            player.walkSpeed=0.3f
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
    @EventHandler
    fun onCreeperDamaged(e: EntityDamageByEntityEvent) {
        if (e.entity !is Creeper) return
        if (!(e.entity as Creeper).isPowered) return
        if (!e.entity.hasMetadata("OwnerName")) return
        if (!(e.damager is Player)) return
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
    }
    @EventHandler
    fun onDamaged(e:EntityDamageEvent){
        if (e.entity !is Player) return
        if (e.isCancelled) return
        val victim=e.entity as Player
        if (manager[victim]==null) return
        if (manager[victim]!=this) return
        if (e is EntityDamageByEntityEvent && inCloakCache.contains(e.damager)){
            return
        }
        if (inCloakCache.contains(victim)){
            if (victim.consumeEnergy(20)){
                e.isCancelled=true
                victim.world.playSound(victim.location,Sound.CREEPER_HISS,1.5f,1f)
                if (e is EntityDamageByEntityEvent){
                    e.damager.sendMessage("Your damage dealt to ${ChatColor.RED.toString()+ChatColor.BOLD+victim.displayName+ChatColor.RESET} was cancelled due to their ${ChatColor.AQUA.toString()+ChatColor.BOLD+"Magical Cloak"+ChatColor.RESET} ability!")
                }
            }else{
                victim.sendMessage(ChatColor.RED.toString() +ChatColor.BOLD+"You didn't block a hit because you don't have enough energy!")
            }
        }
        if(!e.isCancelled&&victim.health-e.damage<10){
            if (!usedBluffOut.contains(victim)){
                usedBluffOut.add(victim)
                bluffOut(victim,true)
            }else if(energyManager[victim]==100&& overflowEnergyMap[victim]!! >=50&&!bluffOutCooldownCache.contains(victim)){
                energyManager.clear(victim)
                overflowEnergyMap[victim] = 0
                bluffOutCooldownCache.add(victim)
                object :BukkitRunnable(){
                    override fun run() {
                        bluffOutCooldownCache.remove(victim)
                    }
                }.runTaskLater(plugin,180*20)
                bluffOut(victim,false)
            }
        }
    }


    override fun assign(player: Player) {
        val items: Map<Int, ItemStack>

        val swordEnch: MutableMap<Enchantment, Int> = HashMap()
        swordEnch[Enchantment.DURABILITY] = 10
        swordEnch[Enchantment.DAMAGE_ALL] = 1


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

    override fun getActionBar(player: Player?): String {
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

    fun veilCreeper(player: Player) {
        val creeper= CitizensAPI.getNPCRegistry().createNPC(EntityType.CREEPER,"Veil Creeper")
        creeper.spawn(player.location)
        creeper.setUseMinecraftAI(false)
        (creeper.entity as Creeper).isPowered=true;
        (creeper.entity as Creeper).addPotionEffect(PotionEffect(PotionEffectType.INVISIBILITY,9999,255))
        creeper.entity.setMetadata("OwnerName",FixedMetadataValue(plugin,player.name))
        object :BukkitRunnable(){
            override fun run() {
                if (!inCloakCache.contains(player)){
                    creeper.destroy()
                    cancel()
                }else {
                    creeper.teleport(player.location, PlayerTeleportEvent.TeleportCause.PLUGIN)
                }
            }
        }.runTaskTimer(plugin,5,5)
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
    fun bluffOut(player: Player,fillOverflow:Boolean){
        player.sendTitle(null,"BLUFF OUT ACTIVATED")
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
