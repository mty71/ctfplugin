package org.mt17.ctfPlugin.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.jspecify.annotations.NullMarked;
import org.mt17.ctfPlugin.CtfPlugin;
import org.mt17.ctfPlugin.game.GameState;
import org.mt17.ctfPlugin.team.GameTeam;
import org.mt17.ctfPlugin.team.TeamType;

@NullMarked
public class ScoreboardManager {

    private final CtfPlugin plugin;

    public ScoreboardManager(CtfPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * オンラインの全プレイヤーのスコアボードを更新する
     */
    public void updateAllScoreboards() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            updateScoreboard(player);
        }
    }

    /**
     * 特定のプレイヤーのスコアボードを更新する
     */
    public void updateScoreboard(Player player) {
        org.bukkit.scoreboard.ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard board = player.getScoreboard();

        // メインスコアボードの場合は新規に個別スコアボードを割り当てる
        if (board.equals(manager.getMainScoreboard())) {
            board = manager.getNewScoreboard();
            player.setScoreboard(board);
        }

        // 既存のサイドバー表示用 Objective をリセット
        Objective obj = board.getObjective("ctf");
        if (obj != null) {
            obj.unregister();
        }

        obj = board.registerNewObjective("ctf", Criteria.DUMMY, "§6§l聖地占領戦");
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);

        GameState state = plugin.getGameManager().getCurrentState();
        GameTeam occupying = plugin.getHolyGroundZone().getOccupyingTeam();
        String occupyingName = (occupying != null) ? occupying.getType().getDisplayName() : "§f未占領";

        int line = 15;
        obj.getScore("§7────────────────").setScore(line--);
        obj.getScore("§fフェーズ: §a" + state.name()).setScore(line--);
        obj.getScore("§f聖地状態: " + occupyingName).setScore(line--);

        // 聖地決戦フェーズかつ聖地が占領されている場合、防衛の残り時間を表示
        if (state == GameState.FINAL_DECISION && occupying != null) {
            int remaining = occupying.getRemainingTimeSeconds();
            obj.getScore("§f防衛残り時間: §e" + (remaining / 60) + "分" + (remaining % 60) + "秒").setScore(line--);
        }

        obj.getScore("§f ").setScore(line--);
        obj.getScore("§e[ チームポイント ]").setScore(line--);

        // 各チームのポイント表示
        for (TeamType type : TeamType.values()) {
            GameTeam team = plugin.getTeamManager().getTeam(type);
            obj.getScore(type.getDisplayName() + " §f: §b" + team.getPoints() + "pt").setScore(line--);
        }

        obj.getScore("§7──────────────── ").setScore(line--);
    }
}