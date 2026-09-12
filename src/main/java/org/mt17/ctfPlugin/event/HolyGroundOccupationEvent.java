package org.mt17.ctfPlugin.event;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.mt17.ctfPlugin.CtfPlugin;
import org.mt17.ctfPlugin.team.GameTeam;

public class HolyGroundOccupationEvent {

    private final CtfPlugin plugin;

    public HolyGroundOccupationEvent(CtfPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * 白旗から自チームの旗へ変更して占領を開始する処理
     */
    public void executeOccupy(Player player, Block bannerBlock, GameTeam team) {
        bannerBlock.setType(team.getType().getBannerMaterial());
        plugin.getHolyGroundZone().setOccupyingTeam(team);

        Bukkit.broadcastMessage("§a[聖地] " + team.getType().getDisplayName() + " チームが聖地を占領しました！ (" + player.getName() + ")");
    }

    /**
     * 占領中の旗を破壊して白旗（中立）に戻す処理
     */
    public void executeNeutralize(Player player, GameTeam playerTeam) {
        plugin.getHolyGroundZone().resetToWhiteBanner();

        Bukkit.broadcastMessage("§e[聖地] " + playerTeam.getType().getDisplayName() + " チームにより聖地の占領が解除されました！ (" + player.getName() + ")");
    }
}