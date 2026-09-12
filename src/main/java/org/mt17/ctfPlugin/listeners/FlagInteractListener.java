package org.mt17.ctfPlugin.listeners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.mt17.ctfPlugin.CtfPlugin;
import org.mt17.ctfPlugin.event.HolyGroundOccupationEvent;
import org.mt17.ctfPlugin.team.GameTeam;

public class FlagInteractListener implements Listener {

    private final CtfPlugin plugin;
    private final HolyGroundOccupationEvent occupationEvent;

    public FlagInteractListener(CtfPlugin plugin) {
        this.plugin = plugin;
        this.occupationEvent = new HolyGroundOccupationEvent(plugin);
    }

    @EventHandler
    public void onFlagInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Block block = event.getClickedBlock();
        if (block == null) return;

        // 条件検知：クリックされた場所が聖地の旗かつ「白旗」であるか
        if (plugin.getHolyGroundZone().isFlagLocation(block.getLocation())) {
            Player player = event.getPlayer();
            GameTeam team = plugin.getTeamManager().getPlayerTeam(player);

            if (team == null) {
                player.sendMessage("§cチームに所属していません。");
                return;
            }

            if (block.getType() == Material.WHITE_BANNER) {
                // 処理は event 層へ引き渡す
                occupationEvent.executeOccupy(player, block, team);
            }
        }
    }
}