package org.mt17.ctfPlugin.team;

import org.bukkit.ChatColor;
import org.bukkit.Material;

public enum TeamType {
    RED("レッド", ChatColor.RED, Material.RED_BANNER),
    BLUE("ブルー", ChatColor.BLUE, Material.BLUE_BANNER),
    GREEN("グリーン", ChatColor.GREEN, Material.GREEN_BANNER),
    YELLOW("イエロー", ChatColor.YELLOW, Material.YELLOW_BANNER);

    private final String displayName;
    private final ChatColor color;
    private final Material bannerMaterial;

    TeamType(String displayName, ChatColor color, Material bannerMaterial) {
        this.displayName = displayName;
        this.color = color;
        this.bannerMaterial = bannerMaterial;
    }

    public String getDisplayName() { return displayName; }
    public ChatColor getColor() { return color; }
    public Material getBannerMaterial() { return bannerMaterial; }
}