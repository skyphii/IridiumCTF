package dev.skyphi.Models;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import dev.skyphi.CTFUtils;
import dev.skyphi.IridiumCTF;
import dev.skyphi.Models.Pickups.ActivePickup;
import dev.skyphi.Models.Pickups.Pickup;

public class CTFPlayer {

    private static final PotionEffect GLOW = new PotionEffect(PotionEffectType.GLOWING, -1, 0);
    
    private Player player;
    private CTFTeam team;
    private boolean flag;

    private FlagCarryEffect flagEffect;

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

            Material wool = CTFUtils.getTeamWool(team.equals(IridiumCTF.TEAM1) ? IridiumCTF.TEAM2 : IridiumCTF.TEAM1);
            
            flagEffect = new FlagCarryEffect(player, wool);

        }else {
            player.removePotionEffect(PotionEffectType.GLOWING);

            if (flagEffect != null) {
                flagEffect.stop();
                flagEffect = null;
            }
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
