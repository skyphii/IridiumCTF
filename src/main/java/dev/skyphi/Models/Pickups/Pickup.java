package dev.skyphi.Models.Pickups;

import java.util.Arrays;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import dev.skyphi.SootCTF;

public abstract class Pickup {
    
    public static final NamespacedKey PICKUP_KEY = new NamespacedKey(SootCTF.INSTANCE, "Pickup");

    protected ItemStack itemStack, actualItem;
    protected String name, description;
    protected Player owner;

    protected Item spawnedItem;

    protected void init() {
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(name);
        itemMeta.setLore(Arrays.asList(description));
        itemMeta.getPersistentDataContainer().set(PICKUP_KEY, PersistentDataType.BYTE, (byte)1);
        itemStack.setItemMeta(itemMeta);

        if(actualItem != null) {
            ItemMeta meta = actualItem.getItemMeta();
            meta.getPersistentDataContainer().set(PICKUP_KEY, PersistentDataType.BYTE, (byte)1);
            actualItem.setItemMeta(meta);
        }
    }

    // GETTERS/SETTERS

    public ItemStack getItemStack() { return itemStack; }

    public Player getOwner() { return owner; }
    public void setOwner(Player owner) { this.owner = owner; }

    public Item getSpawnedItem() { return spawnedItem; }
    public void setSpawnedItem(Item spawnedItem) { this.spawnedItem = spawnedItem; }

    public ItemStack getActualItem() { return actualItem; }

}
