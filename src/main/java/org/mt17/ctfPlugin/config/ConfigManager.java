package org.mt17.ctfPlugin.config;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.mt17.ctfPlugin.CtfPlugin;

public class ConfigManager {

    private final CtfPlugin plugin;

    public ConfigManager(CtfPlugin plugin) {
        this.plugin = plugin;
    }

    @SuppressWarnings("unused") // リロードコマンド等で拡張利用するため保持
    public void reload() {
        plugin.reloadConfig();
    }

    public Location getHolyGroundLocation() {
        FileConfiguration config = plugin.getConfig();
        String worldName = config.getString("holy_ground.world", "world");
        World world = Bukkit.getWorld(worldName);
        if (world == null) return null;

        double x = config.getDouble("holy_ground.x", 0.5);
        double y = config.getDouble("holy_ground.y", 64.0);
        double z = config.getDouble("holy_ground.z", 0.5);
        return new Location(world, x, y, z);
    }

    public void setHolyGroundLocation(Location loc) {
        if (loc == null || loc.getWorld() == null) return; // ヌルチェックの追加

        FileConfiguration config = plugin.getConfig();
        config.set("holy_ground.world", loc.getWorld().getName());
        config.set("holy_ground.x", loc.getBlockX() + 0.5);
        config.set("holy_ground.y", loc.getBlockY());
        config.set("holy_ground.z", loc.getBlockZ() + 0.5);
        plugin.saveConfig();
    }
}