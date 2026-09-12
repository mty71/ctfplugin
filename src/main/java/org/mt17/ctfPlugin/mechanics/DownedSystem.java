package org.mt17.ctfPlugin.mechanics;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.mt17.ctfPlugin.CtfPlugin;
import org.mt17.ctfPlugin.player.PlayerData;
import org.mt17.ctfPlugin.team.GameTeam;

public class DownedSystem {

    private final CtfPlugin plugin;

    public DownedSystem(CtfPlugin plugin) {
        this.plugin = plugin;
    }

    public void enterDownedState(Player player) {
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        if (data.isDowned()) return;

        data.setDowned(true);
        data.setDownedTimer(30); // 30秒カウントダウン

        player.setHealth(1.0);
        // SLOW から SLOWNESS に変更
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 20 * 35, 255, false, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 35, 0, false, false));

        data.setDownedTask(new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline() || !data.isDowned()) {
                    cancel();
                    return;
                }

                int timer = data.getDownedTimer();
                if (timer <= 0) {
                    // 時間切れ：ランダム3個ドロップして拠点へ復帰
                    executeTimeoutRespawn(player);
                    cancel();
                    return;
                }

                player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                        new TextComponent("§c【ダウン中】 残り時間: " + timer + "秒 (スニークで即リス)"));

                data.setDownedTimer(timer - 1);
            }
        }.runTaskTimer(plugin, 0L, 20L));
    }

    public void revivePlayer(Player target, Player rescuer) {
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(target);
        if (!data.isDowned()) return;

        data.cancelDownedTask();
        data.setDowned(false);

        // SLOW から SLOWNESS に変更
        target.removePotionEffect(PotionEffectType.SLOWNESS);
        target.removePotionEffect(PotionEffectType.BLINDNESS);
        target.setHealth(6.0); // 救助後ハート3個で復帰

        target.sendMessage("§a" + rescuer.getName() + " に救助されました！");
        rescuer.sendMessage("§a" + target.getName() + " を救助しました！");
    }

    public void executeInstantRespawn(Player player) {
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        if (!data.isDowned()) return;

        data.cancelDownedTask();
        data.setDowned(false);

        // 即リス時：5個ドロップ
        plugin.getItemDropManager().dropRandomItems(player, 5);
        teleportToTeamSpawn(player);
    }

    public void executeTimeoutRespawn(Player player) {
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        data.cancelDownedTask();
        data.setDowned(false);

        // 時間切れ：3個ドロップ
        plugin.getItemDropManager().dropRandomItems(player, 3);
        teleportToTeamSpawn(player);
    }

    public void teleportToTeamSpawn(Player player) {
        player.removePotionEffect(PotionEffectType.SLOWNESS);
        player.removePotionEffect(PotionEffectType.BLINDNESS);

        // 1.20.5 以降は Attribute.MAX_HEALTH を使用
        var maxHealthAttr = player.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH);
        if (maxHealthAttr != null) {
            player.setHealth(maxHealthAttr.getValue());
        } else {
            player.setHealth(20.0);
        }

        GameTeam team = plugin.getTeamManager().getPlayerTeam(player);
        if (team != null) {
            Location spawn = plugin.getTeamConfig().getSpawn(team.getType());
            if (spawn != null) {
                player.teleport(spawn);
                return;
            }
        }
        player.teleport(player.getWorld().getSpawnLocation());
    }
}