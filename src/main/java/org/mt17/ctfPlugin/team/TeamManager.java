package org.mt17.ctfPlugin.team;

import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;
import org.mt17.ctfPlugin.CtfPlugin;
import org.mt17.ctfPlugin.team.GameTeam;
import org.mt17.ctfPlugin.team.TeamType;

import java.util.*;

@NullMarked
public class TeamManager {

    private final CtfPlugin plugin;
    private final Map<TeamType, GameTeam> teams = new EnumMap<>(TeamType.class);

    public TeamManager(CtfPlugin plugin) {
        this.plugin = plugin;
        for (TeamType type : TeamType.values()) {
            teams.put(type, new GameTeam(type));
        }
    }

    public GameTeam getTeam(TeamType type) { return teams.get(type); }
    public Collection<GameTeam> getAllTeams() { return teams.values(); }

    public GameTeam getPlayerTeam(Player player) {
        for (GameTeam team : teams.values()) {
            if (team.getMembers().contains(player.getUniqueId())) {
                return team;
            }
        }
        return null;
    }

    public void setPlayerTeam(Player player, TeamType type) {
        leaveTeam(player);
        GameTeam targetTeam = teams.get(type);
        if (targetTeam != null) {
            targetTeam.addMember(player.getUniqueId());
        }
    }

    public void leaveTeam(Player player) {
        for (GameTeam team : teams.values()) {
            team.removeMember(player.getUniqueId());
        }
    }

    /**
     * 所属チームがないプレイヤーを、現在の人数が最も少ないチームへ均等に振り分ける
     */
    public void autoBalance(Collection<? extends Player> players) {
        List<Player> unassigned = new ArrayList<>();
        for (Player p : players) {
            if (getPlayerTeam(p) == null) {
                unassigned.add(p);
            }
        }

        // シャッフルしてランダム性を確保
        Collections.shuffle(unassigned);

        for (Player p : unassigned) {
            GameTeam smallestTeam = teams.values().stream()
                    .min(Comparator.comparingInt(t -> t.getMembers().size()))
                    .orElse(teams.get(TeamType.RED));

            setPlayerTeam(p, smallestTeam.getType());
            p.sendMessage("§a[自動チーム分け] " + smallestTeam.getType().getDisplayName() + " チームに振り分けられました。");
        }
    }

    public GameTeam getTopTeam() {
        return teams.values().stream()
                .max(Comparator.comparingInt(GameTeam::getPoints))
                .orElse(null);
    }
}