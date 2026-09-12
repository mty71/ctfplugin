package org.mt17.ctfPlugin.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.mt17.ctfPlugin.CtfPlugin;
import org.mt17.ctfPlugin.event.HolyGroundOccupationEvent;
import org.mt17.ctfPlugin.team.GameTeam;

public class FlagBreakListener implements Listener {

    private final CtfPlugin plugin;
    private final HolyGroundOccupationEvent occupationEvent;

    public FlagBreakListener(CtfPlugin plugin) {
        this.plugin = plugin;
        this.occupationEvent = new HolyGroundOccupationEvent(plugin);
    }

    @EventHandler
    public void onFlagBreak(BlockBreakEvent event) {
        if (!plugin.getHolyGroundZone().isFlagLocation(event.getBlock().getLocation())) return;

        event.setCancelled(true); // ブロックドロップ防止

        Player player = event.getPlayer();
        GameTeam playerTeam = plugin.getTeamManager().getPlayerTeam(player);
        GameTeam occupyingTeam = plugin.getHolyGroundZone().getOccupyingTeam();

        // 条件検知：占領チームが存在し、破壊者が敵チームであるか
        if (occupyingTeam != null && playerTeam != null && playerTeam != occupyingTeam) {
            // 処理は event 層へ引き渡す
            occupationEvent.executeNeutralize(player, playerTeam);
        }
    }
}