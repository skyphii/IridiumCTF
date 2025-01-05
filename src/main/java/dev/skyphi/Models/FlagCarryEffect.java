package dev.skyphi.Models;

import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import dev.skyphi.IridiumCTF;

public class FlagCarryEffect {
    
    private static final int FLAG_UPDATE_TICK_DELAY = 4;

    private Player player;
    private Material itemMat;

    private BukkitRunnable flagRunnable;
    private Item[] items = new Item[10];
    private int counter;

    public FlagCarryEffect(Player player, Material itemMat) {
        this.player = player;
        this.itemMat = itemMat;

        start();
    }

    private void start() {
        // for (int i = 0; i < items.length; i++) {
        //     items[i] = player.getWorld().dropItem(player.getLocation(), new ItemStack(itemMat));
        //     items[i].setUnlimitedLifetime(true);
        //     items[i].setOwner(new UUID(0, 0));  // set owner to invalid UUID so nobody can pick it up
        //     // items[i].setGravity(false);
        // }

        flagRunnable = new BukkitRunnable() {
            @Override
            public void run() {
                // Vector velocity = player.getEyeLocation().getDirection().normalize().multiply(-1);
                // velocity.setY(0.2);
                // items[counter].teleport(player.getEyeLocation());
                // items[counter].setVelocity(velocity);

                // if (++counter == items.length) counter = 0;
                if (items[counter] != null) 
                    items[counter].remove();

                items[counter] = player.getWorld().dropItemNaturally(player.getEyeLocation().add(0, 0.5, 0), new ItemStack(itemMat));
                items[counter].setOwner(new UUID(0, 0));  // set owner to invalid UUID so nobody can pick it up

                if (++counter == items.length) counter = 0;
            }
        };
        flagRunnable.runTaskTimer(IridiumCTF.INSTANCE, 0, FLAG_UPDATE_TICK_DELAY);
    }

    public void stop() {
        flagRunnable.cancel();
        flagRunnable = null;

        for (int i = 0; i < items.length; i++) {
            items[i].remove();
        }
    }

}
