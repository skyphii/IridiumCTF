package dev.skyphi.Models;

import static org.holoeasy.builder.HologramBuilder.hologram;
import static org.holoeasy.builder.HologramBuilder.item;

import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.holoeasy.HoloEasy;
import org.holoeasy.config.HologramKey;
import org.holoeasy.hologram.Hologram;
import org.holoeasy.pool.IHologramPool;

import dev.skyphi.CTFUtils;
import dev.skyphi.IridiumCTF;
import dev.skyphi.Models.Pickups.ActivePickup;
import dev.skyphi.Models.Pickups.Pickup;

public class CTFPlayer {

    private static final PotionEffect GLOW = new PotionEffect(PotionEffectType.GLOWING, -1, 0);
    private static final int FLAG_UPDATE_TICK_DELAY = 2;
    private static final IHologramPool HOLO_POOL = HoloEasy.startInteractivePool(IridiumCTF.INSTANCE, 1000, 0.5f, 5f);
    
    private Player player;
    private CTFTeam team;
    private boolean flag;

    private Hologram flagHolo;
    private HologramKey holoKey;
    private BukkitRunnable flagRunnable;

    public CTFPlayer(Player player, CTFTeam team) {
        this.player = player;
        this.team = team;
    }

    // GETTERS/SETTERS

    public Player getPlayer() { return player; }
    public String getPlayerName() { return player.getName(); }

    public UUID getUniqueId() { return player.getUniqueId(); }

    public CTFTeam getEnemyTeam() { return IridiumCTF.TEAM1.equals(team)?IridiumCTF.TEAM2:IridiumCTF.TEAM1; }
    public CTFTeam getTeam() { return team; }
    public void setTeam(CTFTeam team) { this.team = team; }

    public boolean hasFlag() { return flag; }
    public void setFlag(boolean flag) {
        this.flag = flag;

        if(flag) {
            player.addPotionEffect(GLOW);

            // hologram
            Material wool = CTFUtils.getTeamWool(team.equals(IridiumCTF.TEAM1) ? IridiumCTF.TEAM2 : IridiumCTF.TEAM1);
            holoKey = new HologramKey(HOLO_POOL, getPlayerName());
            flagHolo = hologram(holoKey, player.getLocation().add(0, 2, 0), () -> {
                item(new ItemStack(wool));
            });
            
            flagRunnable = new BukkitRunnable() {
                @Override
                public void run() {
                    flagHolo.teleport(player.getLocation().add(0, 2, 0));
                }
            };
            flagRunnable.runTaskTimer(IridiumCTF.INSTANCE, 0, FLAG_UPDATE_TICK_DELAY);
        }else {
            player.removePotionEffect(PotionEffectType.GLOWING);

            // hologram
            flagRunnable.cancel();
            flagRunnable = null;
            HOLO_POOL.remove(holoKey);
        }
    }

    public void removePickups() {
        for(ItemStack item : player.getInventory().getContents()) {
            if(item == null) continue;
            PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();
            if(pdc.has(Pickup.PICKUP_KEY, PersistentDataType.BYTE)) {
                IridiumCTF.PICKUP_MANAGER.removeActivePickup(pdc);
                item.setAmount(0);
            }
        }
    }

    public void addPickup(Pickup pickup) {
        if(pickup instanceof ActivePickup) IridiumCTF.PICKUP_MANAGER.addActivePickup((ActivePickup)pickup);
        player.getInventory().addItem(pickup.getActualItem());
    }

}
