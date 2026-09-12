package org.mt17.ctfPlugin.player;

import org.bukkit.scheduler.BukkitTask;

import java.util.UUID;

public class PlayerData {

    private final UUID uuid;
    private boolean isDowned = false;
    private int downedTimer = 30; // 自動リスポーンまでのカウントダウン（秒）
    private BukkitTask downedTask = null;

    public PlayerData(UUID uuid) {
        this.uuid = uuid;
    }

    @SuppressWarnings("unused")
    public UUID getUuid() { return uuid; }

    public boolean isDowned() { return isDowned; }
    public void setDowned(boolean downed) { isDowned = downed; }

    public int getDownedTimer() { return downedTimer; }
    public void setDownedTimer(int downedTimer) { this.downedTimer = downedTimer; }

    @SuppressWarnings("unused")
    public BukkitTask getDownedTask() { return downedTask; }
    public void setDownedTask(BukkitTask downedTask) { this.downedTask = downedTask; }

    public void cancelDownedTask() {
        if (downedTask != null && !downedTask.isCancelled()) {
            downedTask.cancel();
            downedTask = null;
        }
    }
}