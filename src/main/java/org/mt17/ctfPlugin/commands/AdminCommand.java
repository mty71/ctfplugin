package org.mt17.ctfPlugin.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;
import org.mt17.ctfPlugin.CtfPlugin;
import org.mt17.ctfPlugin.game.GameState;
import org.mt17.ctfPlugin.team.GameTeam;
import org.mt17.ctfPlugin.team.TeamType;
import org.mt17.ctfPlugin.scoreboard.ScoreboardManager;

import java.util.ArrayList;
import java.util.List;

@NullMarked
public class AdminCommand implements CommandExecutor, TabCompleter {

    private final CtfPlugin plugin;

    public AdminCommand(CtfPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("ctf.admin")) {
            sender.sendMessage("§c権限がありません。");
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage("§e/admin start §7- イベントを開始");
            sender.sendMessage("§e/admin stop §7- イベントを停止");
            sender.sendMessage("§e/admin autobalance §7- 未所属プレイヤーを自動で均等振り分け");
            sender.sendMessage("§e/admin join <MCID> <チーム名> §7- プレイヤーを強制チーム加入");
            sender.sendMessage("§e/admin leave <MCID> §7- プレイヤーを強制チーム離脱");
            sender.sendMessage("§e/admin setspawn <チーム名> §7- 拠点設定");
            sender.sendMessage("§e/admin setholyground §7- 聖地の座標設定");
            sender.sendMessage("§e/admin setphase <フェーズ> §7- フェーズ手動変更");
            sender.sendMessage("§e/admin setpoint <チーム> <数値> §7- ポイント手動調整");
            return true;
        }

        if (args[0].equalsIgnoreCase("start")) {
            plugin.getGameManager().startEvent();
            sender.sendMessage("§aイベントを開始しました。");
            return true;
        }

        if (args[0].equalsIgnoreCase("stop")) {
            plugin.getGameManager().stopEvent();
            sender.sendMessage("§aイベントを停止しました。");
            return true;
        }

        if (args[0].equalsIgnoreCase("autobalance")) {
            plugin.getTeamManager().autoBalance(Bukkit.getOnlinePlayers());
            sender.sendMessage("§a未所属プレイヤーをチームに均等振り分けしました。");
            return true;
        }

        if (args[0].equalsIgnoreCase("join") && args.length >= 3) {
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage("§c指定されたプレイヤーが見つかりません。");
                return true;
            }
            try {
                TeamType type = TeamType.valueOf(args[2].toUpperCase());
                plugin.getTeamManager().setPlayerTeam(target, type);
                sender.sendMessage("§a" + target.getName() + " を " + type.getDisplayName() + " チームに追加しました。");
                target.sendMessage("§a運営によって " + type.getDisplayName() + " チームに追加されました。");
            } catch (IllegalArgumentException e) {
                sender.sendMessage("§c無効なチーム名です。");
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("leave") && args.length >= 2) {
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage("§c指定されたプレイヤーが見つかりません。");
                return true;
            }
            plugin.getTeamManager().leaveTeam(target);
            sender.sendMessage("§a" + target.getName() + " をチームから離脱させました。");
            target.sendMessage("§c運営によってチームから離脱されました。");
            return true;
        }

        if (args[0].equalsIgnoreCase("setspawn") && sender instanceof Player player && args.length >= 2) {
            try {
                TeamType type = TeamType.valueOf(args[1].toUpperCase());
                plugin.getTeamConfig().setSpawn(type, player.getLocation());
                player.sendMessage("§a" + type.getDisplayName() + " の拠点スポーンを設定しました。");
            } catch (IllegalArgumentException e) {
                player.sendMessage("§c無効なチーム名です。");
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("setholyground") && sender instanceof Player player) {
            plugin.getConfigManager().setHolyGroundLocation(player.getLocation().getBlock().getLocation());
            plugin.getHolyGroundZone().resetToWhiteBanner();
            player.sendMessage("§a現在地を聖地の設置場所に設定しました。");
            return true;
        }

        if (args[0].equalsIgnoreCase("setphase") && args.length >= 2) {
            try {
                GameState state = GameState.valueOf(args[1].toUpperCase());
                plugin.getGameManager().setCurrentState(state);
                sender.sendMessage("§aゲームフェーズを " + state.name() + " に変更しました。");
            } catch (IllegalArgumentException e) {
                sender.sendMessage("§c無効なフェーズ名です。");
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("setpoint") && args.length >= 3) {
            try {
                TeamType type = TeamType.valueOf(args[1].toUpperCase());
                int points = Integer.parseInt(args[2]);
                GameTeam team = plugin.getTeamManager().getTeam(type);
                if (team != null) {
                    team.setPoints(points);
                    sender.sendMessage("§a" + type.getDisplayName() + " チームのポイントを " + points + " に設定しました。");
                }
            } catch (Exception e) {
                sender.sendMessage("§c引数が不正です。");
            }
            return true;
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            completions.add("start");
            completions.add("stop");
            completions.add("autobalance");
            completions.add("join");
            completions.add("leave");
            completions.add("setspawn");
            completions.add("setholyground");
            completions.add("setphase");
            completions.add("setpoint");
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("setspawn") || args[0].equalsIgnoreCase("setpoint")) {
                for (TeamType type : TeamType.values()) completions.add(type.name());
            } else if (args[0].equalsIgnoreCase("setphase")) {
                for (GameState state : GameState.values()) completions.add(state.name());
            } else if (args[0].equalsIgnoreCase("join") || args[0].equalsIgnoreCase("leave")) {
                for (Player p : Bukkit.getOnlinePlayers()) completions.add(p.getName());
            }
        } else if (args.length == 3 && args[0].equalsIgnoreCase("join")) {
            for (TeamType type : TeamType.values()) completions.add(type.name());
        }
        return completions;
    }
}