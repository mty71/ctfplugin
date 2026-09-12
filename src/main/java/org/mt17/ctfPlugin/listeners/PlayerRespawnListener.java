package org.mt17.ctfPlugin.listeners;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.mt17.ctfPlugin.CtfPlugin;
import org.mt17.ctfPlugin.team.GameTeam;

public class PlayerRespawnListener implements Listener {

    private final CtfPlugin plugin;

    public PlayerRespawnListener(CtfPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        GameTeam team = plugin.getTeamManager().getPlayerTeam(event.getPlayer());
        if (team != null) {
            Location spawn = plugin.getTeamConfig().getSpawn(team.getType());
            if (spawn != null) {
                event.setRespawnLocation(spawn);
            }
        }
    }
}