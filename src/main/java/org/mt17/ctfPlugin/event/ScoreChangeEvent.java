package org.mt17.ctfPlugin.event;

import org.mt17.ctfPlugin.CtfPlugin;
import org.mt17.ctfPlugin.team.GameTeam;

public class ScoreChangeEvent {

    private final CtfPlugin plugin;

    public ScoreChangeEvent(CtfPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * キルによるポイント強奪処理
     */
    public void transferKillPoint(GameTeam victimTeam, GameTeam killerTeam) {
        if (victimTeam.getPoints() > 0) {
            victimTeam.removePoint(1);
            killerTeam.addPoint(1);
        }
    }
}