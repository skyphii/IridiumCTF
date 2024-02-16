package dev.skyphi.Models.Pickups;

import org.bukkit.NamespacedKey;
import org.bukkit.event.Listener;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public abstract class ActivePickup extends Pickup implements Listener {
    
    protected NamespacedKey key;

    @Override
    public void init() {
        super.init();
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.getPersistentDataContainer().set(key, PersistentDataType.BYTE, (byte)1);
        itemStack.setItemMeta(itemMeta);
    }

    public NamespacedKey getKey() { return key; }

    public abstract void activate();

}
