package org.mt17.ctfPlugin.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.mt17.ctfPlugin.CtfPlugin;
import org.mt17.ctfPlugin.event.ScoreChangeEvent;
import org.mt17.ctfPlugin.game.GameState;
import org.mt17.ctfPlugin.team.GameTeam;

public class PlayerPvPListener implements Listener {

    private final CtfPlugin plugin;
    private final ScoreChangeEvent scoreChangeEvent;

    public PlayerPvPListener(CtfPlugin plugin) {
        this.plugin = plugin;
        this.scoreChangeEvent = new ScoreChangeEvent(plugin);
    }

    @EventHandler
    public void onPvP(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player victim) || !(event.getDamager() instanceof Player killer)) return;

        // 条件検知：致死攻撃かつフェーズが占領戦/聖地決戦であるか
        if (victim.getHealth() - event.getFinalDamage() <= 0) {
            GameState state = plugin.getGameManager().getCurrentState();
            if (state == GameState.OCCUPATION_TIME || state == GameState.FINAL_DECISION) {
                GameTeam victimTeam = plugin.getTeamManager().getPlayerTeam(victim);
                GameTeam killerTeam = plugin.getTeamManager().getPlayerTeam(killer);

                if (victimTeam != null && killerTeam != null && victimTeam != killerTeam) {
                    // 処理は event 層へ引き渡す
                    scoreChangeEvent.transferKillPoint(victimTeam, killerTeam);
                    killer.sendMessage("§a敵をキルし、1ポイント奪取しました！");
                }
            }
        }
    }
}