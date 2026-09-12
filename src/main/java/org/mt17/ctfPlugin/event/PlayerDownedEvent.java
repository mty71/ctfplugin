package org.mt17.ctfPlugin.event;

import org.bukkit.entity.Player;
import org.mt17.ctfPlugin.CtfPlugin;

public class PlayerDownedEvent {

    private final CtfPlugin plugin;

    public PlayerDownedEvent(CtfPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * 致死ダメージを受けたプレイヤーをダウン状態へ移行させる処理
     */
    public void triggerDown(Player victim) {
        plugin.getDownedSystem().enterDownedState(victim);
    }

    /**
     * 味方プレイヤーによる救助処理
     */
    public void triggerRevive(Player target, Player rescuer) {
        plugin.getDownedSystem().revivePlayer(target, rescuer);
    }

    /**
     * ダウン中プレイヤーの即時リスポーン処理
     */
    public void triggerInstantRespawn(Player player) {
        plugin.getDownedSystem().executeInstantRespawn(player);
    }
}