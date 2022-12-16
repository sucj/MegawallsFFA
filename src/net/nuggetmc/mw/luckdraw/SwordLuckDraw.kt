package net.nuggetmc.mw.luckdraw

import net.nuggetmc.mw.MegaWalls
import org.bukkit.configuration.file.FileConfiguration
import java.lang.Double.sum
import kotlin.random.Random

class SwordLuckDraw {
    companion object {
        val names = MegaWalls.getInstance().swordNameMap
        val total = SwordNameRarity.values().sumOf { it.probabilityPercent }

        fun luckDraw(): String {

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
    }
}