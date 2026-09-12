package org.mt17.ctfPlugin.mechanics;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class DownedInventoryGUI {

    public static final String GUI_TITLE_PREFIX = "§c奪取: ";

    public static void openTargetInventory(Player stealer, Player target) {
        Inventory customGui = Bukkit.createInventory(null, 36, GUI_TITLE_PREFIX + target.getName());
        customGui.setContents(target.getInventory().getStorageContents());
        stealer.openInventory(customGui);
    }
}