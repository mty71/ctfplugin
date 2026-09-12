package org.mt17.ctfPlugin.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.mt17.ctfPlugin.CtfPlugin;

public class PlayerQuitListener implements Listener {

    private final CtfPlugin plugin;

    public PlayerQuitListener(CtfPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        plugin.getPlayerDataManager().removePlayerData(event.getPlayer().getUniqueId());
    }
}