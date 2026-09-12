package org.mt17.ctfPlugin.team;

import org.jspecify.annotations.NullMarked;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@NullMarked
public class GameTeam {

    private final TeamType type;
    private final Set<UUID> members = new HashSet<>();
    private int points = 0;
    private int remainingTimeSeconds = 300;

    public GameTeam(TeamType type) {
        this.type = type;
    }

    public TeamType getType() { return type; }
    public Set<UUID> getMembers() { return members; }

    public void addMember(UUID uuid) { members.add(uuid); }
    public void removeMember(UUID uuid) { members.remove(uuid); }

    public int getPoints() { return points; }
    public void setPoints(int points) { this.points = points; }
    public void addPoint(int amount) { this.points += amount; }

    // ★ 以下の removePoint メソッドを追加 ★
    public void removePoint(int amount) {
        this.points = Math.max(0, this.points - amount); // 0未満にならないよう処理
    }

    public int getRemainingTimeSeconds() { return remainingTimeSeconds; }
    public void setRemainingTimeSeconds(int seconds) { this.remainingTimeSeconds = seconds; }
    public void decrementRemainingTime() {
        if (this.remainingTimeSeconds > 0) {
            this.remainingTimeSeconds--;
        }
    }
}