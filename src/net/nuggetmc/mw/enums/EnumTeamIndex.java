package net.nuggetmc.mw.enums;

import net.nuggetmc.mw.special.TeamsManager;

public final class EnumTeamIndex {
    public static final int[] EnumTeamIndex;

    static {
        int[] index = new int[TeamsManager.Team.values().length];
        index[TeamsManager.Team.RED.ordinal()] = 1;
        index[TeamsManager.Team.GREEN.ordinal()] = 2;
        index[TeamsManager.Team.BLUE.ordinal()] = 3;
        index[TeamsManager.Team.YELLOW.ordinal()] = 4;
        EnumTeamIndex = index;
    }
}
