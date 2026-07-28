package net.nuggetmc.mw.special.specialItems

import fr.bukkit.effectkill.utils.Particle
import net.md_5.bungee.api.ChatColor
import net.nuggetmc.mw.MegaWalls
import net.nuggetmc.mw.utils.PlayerUtils
import org.bukkit.Effect
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

object Hyperion :AbstractSpecialItem(){
    override val id: String
        get() = "hyperion"
    override val material: Material
        get() = Material.IRON_SWORD
    override val enchantments: Map<Enchantment, Int>
        get() = mapOf(Enchantment.DAMAGE_ALL to 1)
    override val displayName: String
        get() = ChatColor.GOLD.toString() + "Withered Hyperion"
    override val lore: ArrayList<String>
        get() = arrayListOf("", ChatColor.GOLD.toString() + ChatColor.BOLD + "LEGENDARY DUNGEON SWORD")

    //Hype
    @EventHandler
    fun onHyperion(e: PlayerInteractEvent) {
        val p = e.getPlayer()
        if (!e.getAction().name.contains("RIGHT")) return
        if (p.getItemInHand() == null || p.getItemInHand().getType() == Material.AIR) return
        if (!check(e.getItem())) return
        triggerHypeAbility(p)
    }
    fun triggerHypeAbility(player: Player) {
        player.setVelocity(player.getVelocity().setY(0))
        if (player.getEyeLocation().add(0.0, 1.0, 0.0).getBlock().isEmpty() || player.getEyeLocation()
                .add(0.0, 1.0, 0.0).getBlock().isLiquid()
        ) {
            player.teleport(player.getLocation().add(0.0, 1.0, 0.0))
        }
        PlayerUtils.teleport(player)
        player.addPotionEffect(PotionEffect(PotionEffectType.ABSORPTION, 60, 0), true)
        player.getWorld().playSound(player.getLocation(), Sound.ZOMBIE_REMEDY, 0.2f, 2f)
        Particle.play(player.getLocation().add(0.0, 0.5, 0.0), Effect.EXPLOSION_LARGE)
        val close = PlayerUtils.getNearbyEnemies(player, 6.0)
        close.remove(player)
        for (target in close) {
            val velocity = target.getVelocity()
            MegaWalls.getInstance().getMWHealth().trueDamage(target, 2.0, player)
            target.setVelocity(velocity) //so that using this ability won't change the targets' velocity
        }
        if (close.size != 0) {
            player.sendMessage("Your " + ChatColor.RED + "Implosion " + ChatColor.RESET + "hit " + ChatColor.RED + close.size + ChatColor.RESET + " enemy.")
        }
        player.getWorld().playSound(player.getLocation(), Sound.EXPLODE, 0.4f, 1.2f)
    }
}