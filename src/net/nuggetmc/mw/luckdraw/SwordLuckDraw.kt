package net.nuggetmc.mw.luckdraw

import net.nuggetmc.mw.MegaWalls
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player
import java.lang.Double.sum
import kotlin.random.Random

object SwordLuckDraw {

        val names = MegaWalls.getInstance().swordNameMap
        val total = SwordNameRarity.values().sumOf { it.probabilityPercent }
        var swordNameManager: SwordNameManager? =null

       private fun luckDraw(): String {
           if (swordNameManager==null){
               swordNameManager=MegaWalls.getInstance().swordNameManager
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
            return names[rarity]!![Random.nextInt(names[rarity]!!.size)]
        }
    fun doLuckDraw(player:Player,times:Int=1){
        repeat(times) {
            val a = luckDraw()
            swordNameManager!!.select(player, a)
            player.sendMessage("GG!You found a $a")
        }
    }

}