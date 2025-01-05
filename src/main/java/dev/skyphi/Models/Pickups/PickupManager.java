package dev.skyphi.Models.Pickups;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
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
import dev.skyphi.IridiumCTF;
import dev.skyphi.Models.CTFConfig;
import dev.skyphi.Models.CTFPlayer;
import dev.skyphi.Models.Pickups.Active.GoatRam;
import dev.skyphi.Models.Pickups.Active.GoldenArrow;
import dev.skyphi.Models.Pickups.Active.JumpBoost;
import dev.skyphi.Models.Pickups.Active.SpeedBoost;
import dev.skyphi.Models.Pickups.Active.ThrowTnt;
import dev.skyphi.Models.Pickups.Simple.Freezeball;
import dev.skyphi.Models.Pickups.Simple.GoldenApple;
import dev.skyphi.Models.Pickups.Simple.MobEgg;
import dev.skyphi.Models.Pickups.Simple.SlownessArrows;

public class PickupManager implements Listener {
    
    private static final int INITIAL_DELAY = 0; // in seconds
    private static int SPAWN_PERIOD = 20;       // in seconds

    private static final List<Class<? extends Pickup>> PICKUPS_COMMON = Arrays.asList(
        JumpBoost.class, ThrowTnt.class, SpeedBoost.class, GoldenApple.class
    );

    private static final List<Class<? extends Pickup>> PICKUPS_RARE = Arrays.asList(
        SlownessArrows.class, Freezeball.class
        // Barricade.class // currently broken, don't use
    );

    private static final List<Class<? extends Pickup>> PICKUPS_MYTHIC = Arrays.asList(
        GoldenArrow.class, MobEgg.class, GoatRam.class
    );

    private BukkitRunnable spawnRunnable, particleRunnable;

    private List<ItemSpawner> spawners = new ArrayList<>();
    private List<Pickup> spawnedPickups = new ArrayList<>();
    private List<ActivePickup> activePickups = new ArrayList<>();

    public PickupManager() {}

    public void startSpawning() {
        if(spawners.size() == 0) {
            IridiumCTF.INSTANCE.getLogger().log(Level.WARNING, "No item spawners set! There will be no item spawning for this game.");
            return;
        }
        if(spawnRunnable != null) spawnRunnable.cancel();
        if(particleRunnable != null) particleRunnable.cancel();
        spawnedPickups.clear();
        activePickups.clear();
        spawnRunnable = new BukkitRunnable() {
            @Override
            public void run() {
                spawners.forEach(spawner -> {
                    Pickup pickup = getRandomPickup();
                    if(pickup == null) {
                        IridiumCTF.INSTANCE.getLogger().log(Level.WARNING, "getRandomPickup() returned null in SpawnerManager.java");
                        return;
                    }
                    
                    spawnedPickups.add(pickup);
                    Item spawnedItem = spawner.spawnItem(pickup.getItemStack());
                    spawnedItem.setGravity(false);
                    spawnedItem.setInvulnerable(true);
                    pickup.setSpawnedItem(spawnedItem);
                    spawner.getBlock().getWorld().spawnParticle(Particle.HEART, spawner.getBlock().getLocation().add(0.5, 2, 0.5), 0, 0d, 0.4d, 0d);
                });
            }
        };
        spawnRunnable.runTaskTimer(IridiumCTF.INSTANCE, 20*INITIAL_DELAY, 20*SPAWN_PERIOD);

        IridiumCTF.INSTANCE.getServer().getPluginManager().registerEvents(this, IridiumCTF.INSTANCE);

        particleRunnable = new BukkitRunnable() {
            @Override
            public void run() {
                spawners.forEach(spawner -> {
                    spawner.getBlock().getWorld().spawnParticle(Particle.SOUL, spawner.getBlock().getLocation().add(0.5, 0.5, 0.5), 0, 0d, 0.08d, 0d);
                });
            }
        };
        particleRunnable.runTaskTimer(IridiumCTF.INSTANCE, 20*INITIAL_DELAY, 10);
    }

    public void stopSpawning() {
        if(spawnRunnable != null) {
            spawnRunnable.cancel();
            spawnRunnable = null;
        }
        if(particleRunnable != null) {
            particleRunnable.cancel();
            particleRunnable = null;
        }
        HandlerList.unregisterAll(this);
        clearSpawnedPickups();
    }

    public void loadSpawners() {
        FileConfiguration config = IridiumCTF.INSTANCE.getConfig();
        int index = 1;
        Location loc = config.getLocation("item_spawners.0");
        while(loc != null) {
            spawners.add(new ItemSpawner(loc.getBlock()));
            loc = config.getLocation("item_spawners."+(index++));
        }
    }

    public void saveSpawners() {
        CTFConfig.saveItemSpawners(spawners);
    }

    public void addSpawner(ItemSpawner spawner) {
        spawners.add(spawner);
        saveSpawners();
    }

    public boolean removeSpawner(Block block) {
        return spawners.removeIf(spawner -> spawner.getBlock().getLocation().equals(block.getLocation()));
    }

    public void setItemSpawnRate(int seconds) {
        SPAWN_PERIOD = seconds;
        CTFConfig.ITEM_SPAWN_RATE = seconds;
        CTFConfig.save();
    }

    public void addActivePickup(ActivePickup activePickup) {
        for(int i = 0; i < activePickup.getActualItem().getAmount(); i++) this.activePickups.add(activePickup);
    }

    private Pickup getRandomPickup() {
        Class<? extends Pickup> pickupClass = null;

        int roll = (int)(Math.random() * 100);
        if (roll <= 10)
            pickupClass = PICKUPS_MYTHIC.get((int)(Math.random() * PICKUPS_MYTHIC.size()));
        else if (roll <= 40)
            pickupClass = PICKUPS_RARE.get((int)(Math.random() * PICKUPS_RARE.size()));
        else
            pickupClass = PICKUPS_COMMON.get((int)(Math.random() * PICKUPS_COMMON.size()));

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

        // for items where the dropped item should look different than the *actual* item that gets picked up
        ItemStack actualItem = pickup.getActualItem();
        if(actualItem != null) event.getItem().setItemStack(actualItem);

        if(pickup instanceof ActivePickup) {
            ActivePickup activePickup = (ActivePickup)pickup;
            for(int i = 0; i < event.getItem().getItemStack().getAmount(); i++) activePickups.add(activePickup);
        }
    }
    private Pickup getPickupFromItem(Item item) {
        Pickup pickup = null;
        for(Pickup p : spawnedPickups) {
            if(p.getSpawnedItem().equals(item)) {
                pickup = p;
                break;
            }
        }
        if(pickup != null) {
            for(int i = 0; i < item.getItemStack().getAmount(); i++) spawnedPickups.remove(pickup);
        }
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

    public void removeActivePickup(PersistentDataContainer pdc) {
        for(ActivePickup ap : activePickups) {
            if(pdc.getKeys().contains(ap.getKey())) {
                activePickups.remove(ap);
                return;
            }
        }
    }

    public void clearSpawnedPickups() {
        for(Pickup pickup : spawnedPickups) {
            pickup.getSpawnedItem().remove();
        }
        spawnedPickups.clear();
    }

}
