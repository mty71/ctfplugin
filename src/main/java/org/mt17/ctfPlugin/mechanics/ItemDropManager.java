package org.mt17.ctfPlugin.mechanics;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ItemDropManager {

    public void dropRandomItems(Player player, int count) {
        if (player == null) return;

        List<ItemStack> candidates = new ArrayList<>();
        ItemStack[] contents = player.getInventory().getContents();

        for (ItemStack item : contents) {
            if (item != null && !item.getType().isAir()) {
                candidates.add(item);
            }
        }

        if (candidates.isEmpty()) return;

        Collections.shuffle(candidates);
        int dropAmount = Math.min(count, candidates.size());

        Location dropLoc = player.getLocation();
        World world = dropLoc.getWorld();
        if (world == null) return; // ヌルチェックを追加

        for (int i = 0; i < dropAmount; i++) {
            ItemStack target = candidates.get(i);
            world.dropItemNaturally(dropLoc, target.clone());
            player.getInventory().removeItem(target);
        }
    }
}