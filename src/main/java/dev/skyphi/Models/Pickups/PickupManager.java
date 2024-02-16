package dev.skyphi.Models.Pickups;

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
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.scheduler.BukkitRunnable;

import dev.skyphi.CTFUtils;
import dev.skyphi.SootCTF;
import dev.skyphi.Models.CTFPlayer;
import dev.skyphi.Models.Pickups.Active.JumpBoost;
import dev.skyphi.Models.Pickups.Simple.SlownessArrows;
import dev.skyphi.Models.Pickups.Simple.Freezeball;
import dev.skyphi.Models.Pickups.Simple.GoldenApple;

public class PickupManager implements Listener {
    
    private static final int INITIAL_DELAY = 0, SPAWN_PERIOD = 20;
    private static final List<Class<? extends Pickup>> PICKUPS = Arrays.asList(
        JumpBoost.class,
        GoldenApple.class, SlownessArrows.class, Freezeball.class
        // Barricade.class // currently broken, don't use
    );

    private BukkitRunnable spawnRunnable;

    private List<ItemSpawner> spawners = new ArrayList<>();
    private List<Pickup> spawnedPickups = new ArrayList<>();
    private List<ActivePickup> activePickups = new ArrayList<>();

    public PickupManager() {}

    public void startSpawning() {
        if(spawners.size() == 0) {
            SootCTF.INSTANCE.getLogger().log(Level.WARNING, "No item spawners set! There will be no item spawning for this game.");
            return;
        }
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
        if(pickup instanceof ActivePickup) activePickups.add((ActivePickup)pickup);
    }
    private Pickup getPickupFromItem(Item item) {
        Pickup pickup = null;
        for(Pickup p : spawnedPickups) {
            if(p.getSpawnedItem().equals(item)) {
                pickup = p;
                break;
            }
        }
        if(pickup != null) spawnedPickups.remove(pickup);
        return pickup;
    }

    @EventHandler
    public void on(PlayerInteractEvent event) {
        if(event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Player player = event.getPlayer();
        CTFPlayer ctfp = CTFUtils.getCTFPlayer(player);
        if(ctfp == null) return;

        ItemStack itemStack = event.getItem();
        if(itemStack == null) return;

        ActivePickup activePickup = getActivePickup(itemStack.getItemMeta().getPersistentDataContainer());
        if(activePickup == null) return;

        event.setCancelled(true);

        activePickup.activate();
        itemStack.setAmount(itemStack.getAmount()-1);
    }
    private ActivePickup getActivePickup(PersistentDataContainer pdc) {
        ActivePickup activePickup = null;
        for(ActivePickup ap : activePickups) {
            if(pdc.getKeys().contains(ap.getKey())) {
                activePickup = ap;
                break;
            }
        }
        if(activePickup != null) activePickups.remove(activePickup);
        return activePickup;
    }

}
