package org.mt17.ctfPlugin.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;
import org.mt17.ctfPlugin.CtfPlugin;
import org.mt17.ctfPlugin.team.TeamType;

import java.util.ArrayList;
import java.util.List;

@NullMarked
public class TeamCommand implements CommandExecutor, TabCompleter {

    private final CtfPlugin plugin;

    public TeamCommand(CtfPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cこのコマンドはプレイヤーのみ実行可能です。");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage("§e/team join <RED|BLUE|GREEN|YELLOW> §7- チームに参加");
            player.sendMessage("§e/team leave §7- チームから離脱");
            return true;
        }

        if (args[0].equalsIgnoreCase("join") && args.length >= 2) {
            try {
                TeamType type = TeamType.valueOf(args[1].toUpperCase());
                plugin.getTeamManager().setPlayerTeam(player, type);
                player.sendMessage("§a" + type.getDisplayName() + " チームに参加しました。");
            } catch (IllegalArgumentException e) {
                player.sendMessage("§c無効なチーム名です。");
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("leave")) {
            plugin.getTeamManager().leaveTeam(player);
            player.sendMessage("§aチームから離脱しました。");
            return true;
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            completions.add("join");
            completions.add("leave");
        } else if (args.length == 2 && args[0].equalsIgnoreCase("join")) {
            for (TeamType type : TeamType.values()) {
                completions.add(type.name());
            }
        }
        return completions;
    }
}