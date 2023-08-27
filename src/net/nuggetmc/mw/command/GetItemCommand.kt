package net.nuggetmc.mw.command

import net.nuggetmc.mw.MegaWalls
import net.nuggetmc.mw.mwclass.items.MWPotions
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class GetItemCommand : CommandExecutor, TabCompleter {
    val groupnames: MutableList<String> = ArrayList()
    val classManager = MegaWalls.getInstance().classManager

    init {
        groupnames.add("healpot")
        groupnames.add("speedpot")
        groupnames.add("squpot")
        groupnames.add("golempot")
        groupnames.add("milk")
        groupnames.add("aotv")
        groupnames.add("hype")
        groupnames.add("term")
    }

    val si = MegaWalls.getInstance().specialItemUtils
    override fun onCommand(sender: CommandSender?, cmd: Command?, label: String?, args: Array<String?>?): Boolean {
        if (!(sender is Player)) return true
        val player = sender
        if (!MegaWalls.getInstance().combatManager.isInCombat(player)) {
            return true
        }
        if (args?.size == 2 || args?.size == 1) {
            val amount: Int
            if (args.size == 2) {
                try {
                    amount = Integer.parseInt(args[1])
                } catch (e: Exception) {
                    sender.sendMessage("invalid syntax!Correct usage: /mwitem item count")
                    return true
                }
            } else {
                amount = 1
            }
            var itemStack = when (args[0]) {
                "healpot" -> MWPotions.createHealPotions(
                    classManager?.get(player)?.name,
                    classManager.get(player).color,
                    amount,
                    10
                )

                "speedpot" -> MWPotions.createSpeedPotions(
                    classManager?.get(player)?.name,
                    classManager.get(player).color,
                    amount
                )

                "squpot" -> si.getSquidPot(amount)
                "golempot" -> si.getGolemPot(amount)
                "milk" -> si.getCowBucket(amount)
                "aotv" -> si.aotv
                "hype" -> si.hyperion
                "term" -> si.term
                else -> {
                    null
                }
            }
            if (itemStack == null) {
                sender.sendMessage("invalid item!Allowed items:$groupnames")
            } else {
                player.inventory.addItem(itemStack)
            }
        } else {
            sender.sendMessage("invalid syntax!Correct usage: /mwitem item count")
            return true
        }
        return true
    }

    override fun onTabComplete(
        sender: CommandSender?,
        cmd: Command?,
        label: String?,
        args: Array<String>
    ): List<String>? {
        if (args.size != 1) return null

        val arg = args[0]
        return if (!isEmptyTab(arg)) {
            autofill(groupnames, arg)
        } else groupnames
    }


    private fun autofill(groupnames: List<String>, input: String): List<String> {
        val list: MutableList<String> = ArrayList()
        for (entry in groupnames) {
            if (entry.length >= input.length) {
                if (input.equals(entry.substring(0, input.length), ignoreCase = true)) {
                    list.add(entry)
                }
            }
        }
        return if (list.isEmpty()) {
            groupnames
        } else list
    }

    private fun isEmptyTab(s: String?): Boolean {
        return s == null || s == " " || s.isEmpty()
    }
}