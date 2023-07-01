package net.nuggetmc.mw.luckdraw

import net.md_5.bungee.api.ChatColor
import net.nuggetmc.mw.MegaWalls
import org.bukkit.entity.Player
import java.lang.Double.sum
import kotlin.random.Random

object SwordLuckDraw {

    val names = MegaWalls.getInstance().swordNameMap
    val total = SwordNameRarity.values().sumOf { it.probabilityPercent }
    var swordNameManager: SwordNameManager? = null


    private fun luckDraw(): LuckDrawData {
        if (swordNameManager == null) {
            swordNameManager = MegaWalls.getInstance().swordNameManager
        }


        val rarity = when ((Math.random() * total)) {
            in 0.0..SwordNameRarity.LEGENDARY.probabilityPercent -> SwordNameRarity.LEGENDARY
            in SwordNameRarity.LEGENDARY.probabilityPercent..(sum(
                SwordNameRarity.LEGENDARY.probabilityPercent,
                SwordNameRarity.EPIC.probabilityPercent
            )) -> SwordNameRarity.EPIC

            in sum(
                SwordNameRarity.LEGENDARY.probabilityPercent,
                SwordNameRarity.EPIC.probabilityPercent
            )..(SwordNameRarity.LEGENDARY.probabilityPercent + SwordNameRarity.EPIC.probabilityPercent + SwordNameRarity.RARE.probabilityPercent) -> SwordNameRarity.RARE

            else -> SwordNameRarity.UNCOMMON
        }
        return LuckDrawData(names[rarity]!![Random.nextInt(names[rarity]!!.size)], rarity)
    }

    fun doLuckDraw(player: Player, times: Int = 1) {
        repeat(times) {
            val a = luckDraw()
            val color = when (a.rarity) {
                SwordNameRarity.UNCOMMON -> ChatColor.GREEN
                SwordNameRarity.RARE -> ChatColor.BLUE
                SwordNameRarity.EPIC -> ChatColor.DARK_PURPLE
                SwordNameRarity.LEGENDARY -> ChatColor.GOLD
            }
            swordNameManager!!.select(player, a.result)
            player.sendMessage("GG!You found a ${color.toString() + a.result}")
        }
    }

    class LuckDrawData(val result: String, val rarity: SwordNameRarity)

}