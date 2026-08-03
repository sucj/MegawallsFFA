package net.nuggetmc.mw.combat;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CombatManager {
    public List<Player> getInCombatPlayers() {
        return inCombatPlayers;
    }

    public static List<Player> inCombatPlayers = new ArrayList<>();

    public void addInCombat(Player player) {
        inCombatPlayers.add(player);
    }

    public boolean isInCombat(Player player) {
        return false;
        //return inCombatPlayers.contains(player);
    }

    public void removeInCombat(Player player) {
        if (isInCombat(player)) {
            inCombatPlayers.remove(player);
        }
    }

}
