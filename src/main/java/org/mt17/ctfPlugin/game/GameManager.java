package org.mt17.ctfPlugin.game;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.mt17.ctfPlugin.CtfPlugin;
import org.mt17.ctfPlugin.team.GameTeam;

import java.time.Duration;
import java.time.LocalDateTime;

@NullMarked
public class GameManager {

    private final CtfPlugin plugin;
    private GameState currentState = GameState.IDLE;
    private @Nullable BukkitTask mainTask;
    private @Nullable LocalDateTime eventStartTime = null;

    public GameManager(CtfPlugin plugin) {
        this.plugin = plugin;
    }

    public GameState getCurrentState() { return currentState; }
    public void setCurrentState(GameState state) { this.currentState = state; }

    public void startEvent() {
        this.eventStartTime = LocalDateTime.now();
        this.currentState = GameState.GAME_RUNNING;
        Bukkit.broadcastMessage("§a[聖地占領戦] イベントが開始されました！（Day 1）");
    }

    public void stopEvent() {
        this.currentState = GameState.IDLE;
        plugin.getHolyGroundZone().resetToWhiteBanner();
        Bukkit.broadcastMessage("§c[聖地占領戦] イベントが停止されました。");
    }

    public void startScheduleTask() {
        if (mainTask != null && !mainTask.isCancelled()) {
            mainTask.cancel();
        }
        this.mainTask = new BukkitRunnable() {
            @Override
            public void run() {
                checkTimeAndPhase();
                processOccupationPoints();
                plugin.getScoreboardManager().updateAllScoreboards();
            }
        }.runTaskTimer(plugin, 20L, 20L);
    }

    public void stopScheduleTask() {
        if (mainTask != null && !mainTask.isCancelled()) {
            mainTask.cancel();
        }
    }

    private void checkTimeAndPhase() {
        if (eventStartTime == null || currentState == GameState.IDLE) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        long elapsedDays = Duration.between(eventStartTime, now).toDays();
        int hour = now.getHour();

        if (elapsedDays >= 2) {
            if (hour >= 22) {
                currentState = GameState.FINAL_DECISION;
            } else if (hour >= 17) {
                currentState = GameState.GAME_RUNNING;
            }
            return;
        }

        if (hour == 23) {
            currentState = GameState.OCCUPATION_TIME;
        } else if (hour >= 18) {
            currentState = GameState.GAME_RUNNING;
        }
    }

    private void processOccupationPoints() {
        GameTeam occupying = plugin.getHolyGroundZone().getOccupyingTeam();
        if (occupying == null) return;

        if (currentState == GameState.OCCUPATION_TIME) {
            occupying.addPoint(1);
        } else if (currentState == GameState.FINAL_DECISION) {
            GameTeam topTeam = plugin.getTeamManager().getTopTeam();
            if (topTeam != null && occupying.getPoints() >= topTeam.getPoints()) {
                occupying.decrementRemainingTime();

                if (occupying.getRemainingTimeSeconds() <= 0) {
                    Bukkit.broadcastMessage("§a[聖地決戦] " + occupying.getType().getDisplayName() + " チームが防衛に成功し、完全勝利を収めました！");
                    setCurrentState(GameState.IDLE);
                }
            }
        }
    }
}