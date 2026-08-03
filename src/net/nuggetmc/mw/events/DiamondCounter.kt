package net.nuggetmc.mw.events

import net.nuggetmc.mw.MegaWalls
import net.nuggetmc.mw.special.entities.wither.WitherNPCUtil
import net.nuggetmc.mw.utils.PlayerUtils
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.scheduler.BukkitRunnable

object DiamondCounter {
    var diamondCount = 0;


    fun breakDiamond(player: Player,location: Location) {
        object : BukkitRunnable(){
            override fun run() {
                location.world.getBlockAt(location).type = Material.DIAMOND_ORE
            }

        }.runTaskLater(MegaWalls.getInstance(),180*20)

        val i = MegaWalls.getRandom().nextInt(6)
        var pet: PotionEffectType? = null
        when (i) {
            0 -> pet = PotionEffectType.JUMP
            1 -> pet = PotionEffectType.DAMAGE_RESISTANCE
            2 -> pet = PotionEffectType.SPEED
            3 -> pet = PotionEffectType.REGENERATION
            4 -> pet = PotionEffectType.INCREASE_DAMAGE
            5 -> pet = PotionEffectType.HEALTH_BOOST
        }
        player.addPotionEffect(PotionEffect(pet, 120 * 20, 1))
        player.sendMessage("You were given " + pet!!.name + " for breaking a diamond ore!")
        diamondCount+=1;
        val untilNow = diamondCount%3
        if (untilNow==0){
            WitherNPCUtil.spawnKnockbackResistantWither(location, ChatColor.RED.toString()+ ChatColor.BOLD+"Diamond Defender")
            Bukkit.broadcastMessage(ChatColor.RED.toString()+ ChatColor.BOLD.toString()+"A wither has just spawned at ${location.x}.${location.y},${location.z}")
            for (player in PlayerUtils.getNearbyPlayers(location,30.0)){
                (player as CraftPlayer).sendTitle(ChatColor.GOLD.toString()+ ChatColor.BOLD+"A wither has spawned nearby!", ChatColor.GRAY.toString()+"Kill it to become a Psychopath")
            }
        }else {
            player.sendMessage("Diamond count: ${untilNow}/3 until a wither summons")
        }
    }
}