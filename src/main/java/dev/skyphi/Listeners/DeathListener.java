package dev.skyphi.Listeners;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;

import dev.skyphi.CTFUtils;
import dev.skyphi.IridiumCTF;
import dev.skyphi.Models.CTFConfig;
import dev.skyphi.Models.CTFPlayer;
import dev.skyphi.Models.Pickups.Simple.Arrows;

public class DeathListener implements Listener {
    
    @EventHandler
    public void on(EntityDamageEvent event) {
        if(event.getEntityType() != EntityType.PLAYER) return;
        final Player player = (Player)event.getEntity();
        final CTFPlayer ctfp = CTFUtils.getCTFPlayer(player);
        if(ctfp == null) return;
        if(player.getHealth() - event.getFinalDamage() > 0) return;

        event.setCancelled(true);
        GameMode gm = player.getGameMode();
        player.setGameMode(GameMode.SPECTATOR);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_WITHER_SHOOT, SoundCategory.PLAYERS, 1.0f, 1.0f);
        player.getWorld().spawnParticle(Particle.LARGE_SMOKE, player.getLocation().add(0, 1, 0), 100);
        player.sendTitle(ChatColor.RED+""+ChatColor.BOLD+"You died!", ChatColor.GRAY+"You will respawn shortly...", -1, -1, -1);
        // clear titles on bedrock clients
        new BukkitRunnable() {
            @Override
            public void run() {
                player.sendTitle("", "", -1, -1, -1);
            }
        }.runTaskLater(IridiumCTF.INSTANCE, 80);
        announceDeath(ctfp, event);

        // replace flag
        if(ctfp.hasFlag()) {
            ctfp.setFlag(false);
            ctfp.getEnemyTeam().getFlag().setType(CTFConfig.FLAG_TYPE);
            ctfp.getTeam().announce(ChatColor.RED, "The enemy flag was returned to their base!");
            ctfp.getEnemyTeam().announce(ChatColor.GREEN, "Your flag was returned to base!");
        }

        ctfp.removePickups();

        new BukkitRunnable() {
            @Override
            public void run() {
                player.setHealth(20);
                player.setFoodLevel(20);
                player.setSaturation(20);
                player.teleport(ctfp.getTeam().getFlag().getLocation());
                player.setGameMode(gm);
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 1);
                ctfp.addPickup(new Arrows());
            }
        }.runTaskLater(IridiumCTF.INSTANCE, 20*CTFConfig.RESPAWN_TIMER);
    }

    private void announceDeath(CTFPlayer deadPlayer, EntityDamageEvent edEvent) {
        EntityDamageByEntityEvent edbeEvent = null;
        String nameKiller = "????";
        if(edEvent instanceof EntityDamageByEntityEvent) {
            edbeEvent = (EntityDamageByEntityEvent)edEvent;
            Entity damager = edbeEvent.getDamager();
            if(damager instanceof Projectile) {
                ProjectileSource shooter = ((Projectile)damager).getShooter();
                if(shooter instanceof Entity) damager = (Entity)shooter;
            }
            ChatColor colour = ChatColor.GRAY;
            CTFPlayer ctfpDamager = (damager instanceof Player) ? CTFUtils.getCTFPlayer((Player)damager) : null;
            if(ctfpDamager != null) colour = CTFUtils.getTeamChatColour(ctfpDamager.getTeam());
            nameKiller = colour+""+ChatColor.BOLD + damager.getName();
        }

        String nameDead = CTFUtils.getTeamChatColour(deadPlayer.getTeam())+""+ChatColor.BOLD + deadPlayer.getPlayerName();

        String deathMsg = "";
        switch(edEvent.getCause()) {
            default:
                deathMsg = nameDead + ChatColor.GRAY + " died.";
                break;
            case ENTITY_ATTACK:
                deathMsg = nameDead + ChatColor.GRAY + " was " + KILL_SYNONYMS[(int)(Math.random()*KILL_SYNONYMS.length)] 
                    + " by " + nameKiller;
                break;
            case BLOCK_EXPLOSION:
                deathMsg = nameDead + ChatColor.RED + " was blown up by " + nameKiller;
                break;
            case PROJECTILE:
                deathMsg = nameDead + ChatColor.GRAY+""+ChatColor.ITALIC + " was shot by " + nameKiller;
                break;
        }

        CTFUtils.broadcast(String.format(deathMsg, deadPlayer.getPlayerName()), false);
    }

    private final String[] KILL_SYNONYMS = {
        "bonked", "bopped", "sent back to spawn", "stabbed", "slammed", "poofed away",
        "blasted", "slapped", "stomped on", "hit too much", "lightly salted", "struck down",
        "crushed", "halted"
    };

}
