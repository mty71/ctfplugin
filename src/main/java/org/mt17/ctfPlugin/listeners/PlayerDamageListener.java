package org.mt17.ctfPlugin.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.mt17.ctfPlugin.CtfPlugin;
import org.mt17.ctfPlugin.event.PlayerDownedEvent;
import org.mt17.ctfPlugin.player.PlayerData;

public class PlayerDamageListener implements Listener {

    private final CtfPlugin plugin;
    private final PlayerDownedEvent downedEvent;

    public PlayerDamageListener(CtfPlugin plugin) {
        this.plugin = plugin;
        this.downedEvent = new PlayerDownedEvent(plugin);
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);

        if (data.isDowned()) {
            event.setCancelled(true);
            return;
        }

        // 条件検知：ダメージ適用後にHPが0以下になるか
        if (player.getHealth() - event.getFinalDamage() <= 0) {
            event.setCancelled(true);
            // 処理は event 層へ引き渡す
            downedEvent.triggerDown(player);
        }
    }
}