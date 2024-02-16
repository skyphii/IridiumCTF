package dev.skyphi.Models.Pickups;

import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class ItemSpawner {
    
    private Block block;

    public ItemSpawner(Block block) {
        this.block = block;
    }

    public Block getBlock() { return block; }

    public Item spawnItem(ItemStack itemStack) {
        Item item = block.getWorld().dropItem(block.getLocation().add(0.5, 1, 0.5), itemStack);
        item.setVelocity(new Vector(0, 0.4, 0));
        return item;
    }

}
