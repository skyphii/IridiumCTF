package dev.skyphi.Models;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class CTFPlayer {

    private static final PotionEffect GLOW = new PotionEffect(PotionEffectType.GLOWING, -1, 0);
    
    private Player player;
    private CTFTeam team;
    private boolean flag;

    public CTFPlayer(Player player, CTFTeam team) {
        this.player = player;
        this.team = team;
    }

    // GETTERS/SETTERS

    public Player getPlayer() { return player; }
    public String getPlayerName() { return player.getDisplayName(); }

    public UUID getUniqueId() { return player.getUniqueId(); }

    public CTFTeam getTeam() { return team; }
    public void setTeam(CTFTeam team) { this.team = team; }

    public boolean hasFlag() { return flag; }
    public void setFlag(boolean flag) {
        this.flag = flag;

        if(flag) {
            player.addPotionEffect(GLOW);
        }else {
            player.removePotionEffect(PotionEffectType.GLOWING);
        }
    }

}
