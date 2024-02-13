package dev.skyphi.Models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import dev.skyphi.CTFUtils;
import dev.skyphi.SootCTF;
import dev.skyphi.Models.Pickups.JumpBoost;

public class PickupManager implements Listener {
    
    private static final int INITIAL_DELAY = 0, SPAWN_PERIOD = 20;
    private static final List<Class<? extends Pickup>> PICKUPS = Arrays.asList(
        JumpBoost.class
        // Barricade.class // currently broken, don't use
    );

    private BukkitRunnable spawnRunnable;

    private List<ItemSpawner> spawners = new ArrayList<>();
    private List<Pickup> spawnedPickups = new ArrayList<>();

    public PickupManager() {}

    public void startSpawning() {
        if(spawnRunnable != null) {
            spawnRunnable.cancel();
        }
        spawnRunnable = new BukkitRunnable() {
            @Override
            public void run() {
                ItemSpawner spawner = spawners.get((int)(Math.random() * spawners.size()));
                Pickup pickup = getRandomPickup();
                if(pickup == null) {
                    SootCTF.INSTANCE.getLogger().log(Level.WARNING, "getRandomPickup() returned null in SpawnerManager.java");
                    return;
                }
                
                spawnedPickups.add(pickup);
                Item spawnedItem = spawner.spawnItem(pickup.getItemStack());
                pickup.setSpawnedItem(spawnedItem);
            }
        };
        spawnRunnable.runTaskTimer(SootCTF.INSTANCE, 20*INITIAL_DELAY, 20*SPAWN_PERIOD);

        SootCTF.INSTANCE.getServer().getPluginManager().registerEvents(this, SootCTF.INSTANCE);
    }

    public void stopSpawning() {
        if(spawnRunnable != null) {
            spawnRunnable.cancel();
            spawnRunnable = null;
        }
        HandlerList.unregisterAll(this);
    }

    public void loadSpawners() {
        FileConfiguration config = SootCTF.INSTANCE.getConfig();
        int index = 1;
        Location loc = config.getLocation("item_spawners.0");
        while(loc != null) {
            spawners.add(new ItemSpawner(loc.getBlock()));
            loc = config.getLocation("item_spawners."+(index++));
        }
    }

    public void saveSpawners() {
        FileConfiguration config = SootCTF.INSTANCE.getConfig();
        for(int i = 0; i < spawners.size(); i++) {
            config.set("item_spawners."+i, spawners.get(i).getBlock().getLocation());
        }
        SootCTF.INSTANCE.saveConfig();
    }

    public void addSpawner(ItemSpawner spawner) {
        spawners.add(spawner);
        saveSpawners();
    }

    private Pickup getRandomPickup() {
        Class<? extends Pickup> pickupClass = PICKUPS.get((int)(Math.random() * PICKUPS.size()));

        Pickup pickup = null;
        try {
            pickup = pickupClass.getDeclaredConstructor().newInstance();
        }catch(Exception e) {
            e.printStackTrace();
        }
        return pickup;
    }

    // EVENT HANDLING

    @EventHandler
    public void on(EntityPickupItemEvent event) {
        if(event.getEntityType() != EntityType.PLAYER) return;
        Pickup pickup = getPickupFromItem(event.getItem());
        if(pickup == null) return;

        Player player = (Player)event.getEntity();
        CTFPlayer ctfp = CTFUtils.getCTFPlayer(player);
        if(ctfp == null) {
            event.setCancelled(true);
            return;
        }

        pickup.setOwner(player);
    }
    private Pickup getPickupFromItem(Item item) {
        for(Pickup pickup : spawnedPickups) {
            if(pickup.getSpawnedItem().equals(item)) return pickup;
        }
        return null;
    }

    @EventHandler
    public void on(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        CTFPlayer ctfp = CTFUtils.getCTFPlayer(player);
        if(ctfp == null) return;

        ItemStack itemStack = event.getItem();
        Pickup pickup = getPickup(itemStack, player);
        if(pickup == null) return;

        pickup.activate();
        spawnedPickups.remove(pickup);
        player.getInventory().remove(itemStack);
    }
    private Pickup getPickup(ItemStack itemStack, Player owner) {
        for(Pickup pickup : spawnedPickups) {
            if(pickup.getItemStack().equals(itemStack) && pickup.getOwner().equals(owner)) return pickup;
        }
        return null;
    }

}
