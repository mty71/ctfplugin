package org.mt17.ctfPlugin.game;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.mt17.ctfPlugin.CtfPlugin;
import org.mt17.ctfPlugin.team.GameTeam;

public class HolyGroundZone {

    private final CtfPlugin plugin;
    private GameTeam occupyingTeam = null;

    public HolyGroundZone(CtfPlugin plugin) {
        this.plugin = plugin;
    }

    public GameTeam getOccupyingTeam() {
        return occupyingTeam;
    }

    public void setOccupyingTeam(GameTeam occupyingTeam) {
        this.occupyingTeam = occupyingTeam;
    }

    public boolean isFlagLocation(Location location) {
        Location holyLoc = plugin.getConfigManager().getHolyGroundLocation();

        // holyLoc やワールドが null の場合を考慮してチェックを追加
        if (holyLoc == null || location == null || holyLoc.getWorld() == null || location.getWorld() == null) {
            return false;
        }

        return holyLoc.getWorld().equals(location.getWorld())
                && holyLoc.getBlockX() == location.getBlockX()
                && holyLoc.getBlockY() == location.getBlockY()
                && holyLoc.getBlockZ() == location.getBlockZ();
    }

    public void resetToWhiteBanner() {
        Location holyLoc = plugin.getConfigManager().getHolyGroundLocation();
        if (holyLoc == null || holyLoc.getWorld() == null) return;

        Block block = holyLoc.getBlock();
        block.setType(Material.WHITE_BANNER);
        this.occupyingTeam = null;
    }
}