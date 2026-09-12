package org.mt17.ctfPlugin.config;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.mt17.ctfPlugin.CtfPlugin;
import org.mt17.ctfPlugin.team.TeamType;

import java.io.File;
import java.io.IOException;

public class TeamConfig {

    private final CtfPlugin plugin;
    private final File file;
    private YamlConfiguration config;

    public TeamConfig(CtfPlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "teams.yml");
        if (!file.exists()) {
            try {
                plugin.getDataFolder().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.config = YamlConfiguration.loadConfiguration(file);
    }

    public void setSpawn(TeamType type, Location loc) {
        String path = "spawns." + type.name();
        config.set(path + ".world", loc.getWorld().getName());
        config.set(path + ".x", loc.getX());
        config.set(path + ".y", loc.getY());
        config.set(path + ".z", loc.getZ());
        config.set(path + ".yaw", loc.getYaw());
        config.set(path + ".pitch", loc.getPitch());
        save();
    }

    public Location getSpawn(TeamType type) {
        String path = "spawns." + type.name();
        if (!config.contains(path + ".world")) return null;

        World world = Bukkit.getWorld(config.getString(path + ".world"));
        if (world == null) return null;

        double x = config.getDouble(path + ".x");
        double y = config.getDouble(path + ".y");
        double z = config.getDouble(path + ".z");
        float yaw = (float) config.getDouble(path + ".yaw");
        float pitch = (float) config.getDouble(path + ".pitch");

        return new Location(world, x, y, z, yaw, pitch);
    }

    public void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}