package org.mt17.ctfPlugin.player;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerDataManager {

    private final Map<UUID, PlayerData> playerDataMap = new HashMap<>();

    public PlayerData getPlayerData(Player player) {
        return playerDataMap.computeIfAbsent(player.getUniqueId(), PlayerData::new);
    }

    @SuppressWarnings("unused")
    public PlayerData getPlayerData(UUID uuid) {
        return playerDataMap.computeIfAbsent(uuid, PlayerData::new);
    }

    @SuppressWarnings("unused")
    public void removePlayerData(UUID uuid) {
        PlayerData data = playerDataMap.remove(uuid);
        if (data != null) {
            data.cancelDownedTask();
        }
    }
}