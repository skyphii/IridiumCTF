package dev.skyphi.Listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import dev.skyphi.CTFUtils;
import dev.skyphi.SootCTF;
import dev.skyphi.Models.CTFPlayer;

public class PlayerLeaveListener implements Listener {
    
    @EventHandler
    public void on(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        CTFPlayer ctfp = CTFUtils.getCTFPlayer(player);
        if(ctfp == null) return;

        // reset flag if leaver is carrying
        if(ctfp.hasFlag()) {
            ctfp.setFlag(false);
            ctfp.getEnemyTeam().getFlag().setType(SootCTF.FLAG_TYPE);
            ctfp.getTeam().announce(ChatColor.RED, "The enemy flag was returned to their base!");
            ctfp.getEnemyTeam().announce(ChatColor.GREEN, "Your flag was returned to base!");
        }

        ctfp.getTeam().removePlayer(player);
    }

}
