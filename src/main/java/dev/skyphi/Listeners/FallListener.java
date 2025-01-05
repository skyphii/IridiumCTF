package dev.skyphi.Listeners;

import java.util.ArrayList;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.scheduler.BukkitRunnable;

import dev.skyphi.IridiumCTF;

public class FallListener implements Listener {
    
    private static ArrayList<Player> players = new ArrayList<>();

    @EventHandler
    public void on(EntityDamageEvent event) {
        if(event.getCause() != DamageCause.FALL) return;
        if(event.getEntityType() != EntityType.PLAYER) return;
        final Player player = (Player)event.getEntity();
        if(!players.remove(player)) return;
        
        event.setCancelled(true);
    }

    public static void addPlayer(Player player) {
        if(players.contains(player)) return;
        players.add(player);

        new BukkitRunnable() {
            @Override
            public void run() {
                players.remove(player);
            }
        }.runTaskLater(IridiumCTF.INSTANCE, 20*4);
    }

}
