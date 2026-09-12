package org.mt17.ctfPlugin.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.mt17.ctfPlugin.CtfPlugin;
import org.mt17.ctfPlugin.event.PlayerDownedEvent;
import org.mt17.ctfPlugin.mechanics.DownedInventoryGUI;
import org.mt17.ctfPlugin.player.PlayerData;
import org.mt17.ctfPlugin.team.GameTeam;

public class PlayerInteractListener implements Listener {

    private final CtfPlugin plugin;
    private final PlayerDownedEvent downedEvent;

    public PlayerInteractListener(CtfPlugin plugin) {
        this.plugin = plugin;
        this.downedEvent = new PlayerDownedEvent(plugin);
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof Player target)) return;

        Player clicker = event.getPlayer();
        PlayerData targetData = plugin.getPlayerDataManager().getPlayerData(target);

        if (!targetData.isDowned()) return;

        GameTeam clickerTeam = plugin.getTeamManager().getPlayerTeam(clicker);
        GameTeam targetTeam = plugin.getTeamManager().getPlayerTeam(target);

        // 条件分岐検知：味方か敵か
        if (clickerTeam != null && clickerTeam == targetTeam) {
            downedEvent.triggerRevive(target, clicker);
        } else {
            DownedInventoryGUI.openTargetInventory(clicker, target);
        }
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent event) {
        if (!event.isSneaking()) return;

        Player player = event.getPlayer();
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);

        if (data.isDowned()) {
            downedEvent.triggerInstantRespawn(player);
        }
    }
}