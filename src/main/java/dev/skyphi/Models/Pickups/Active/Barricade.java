package dev.skyphi.Models.Pickups.Active;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import dev.skyphi.SootCTF;
import dev.skyphi.Models.Pair;
import dev.skyphi.Models.Pickups.ActivePickup;
import net.md_5.bungee.api.ChatColor;

public class Barricade extends ActivePickup {

    private static final int WIDTH = 3, HEIGHT = 4;
    private static final int TIME = 6; // in seconds
    private static final Material BLOCK_TYPE = Material.DEEPSLATE_TILES;

    public Barricade() {
        name = ChatColor.DARK_PURPLE+""+ChatColor.BOLD+"Barricade";
        description = ChatColor.LIGHT_PURPLE+"Right click to spawn a wall in front of you!";
        itemStack = new ItemStack(Material.SHIELD);
        key = new NamespacedKey(SootCTF.INSTANCE, "Barricade");
        init();
    }

    @Override
    public void activate() {
        Location origin = owner.getEyeLocation().getBlock().getLocation();
        
        // effects
        World world = origin.getWorld();
        world.playSound(origin, Sound.BLOCK_ANVIL_DESTROY, 1, 1);
        world.spawnParticle(Particle.GLOW_SQUID_INK, origin, 10);

        // spawn wall
        Vector dir = owner.getEyeLocation().getDirection().multiply(2);
        Location center = origin.add(dir);

        Location temp = origin.clone().setDirection(dir);
        temp.setPitch(0);
        temp.setYaw(temp.getYaw() - 90);
        Vector rotation = temp.getDirection();
        Vector initialRotation = rotation.clone().multiply(HEIGHT/2);
        
        Location blockLoc = center.clone().add(initialRotation).subtract(0, HEIGHT/2-1, 0).getBlock().getLocation();
        rotation.multiply(-1);
        
        int initialX = blockLoc.getBlockX();
        int initialZ = blockLoc.getBlockZ();

        List<Pair<Block, Material>> oldBlocks = new ArrayList<>();
        for(int y = 0; y < HEIGHT; y++) {
            for(int x = 0; x < WIDTH; x++) {
                Block block = blockLoc.getBlock();
                oldBlocks.add(new Pair<>(block, block.getType()));
                block.setType(BLOCK_TYPE);

                blockLoc.add(rotation);
            }
            blockLoc.add(0, 1, 0);
            blockLoc.setX(initialX);
            blockLoc.setZ(initialZ);
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                oldBlocks.forEach(pair -> {
                    pair.getFirst().setType(pair.getSecond());
                });
            }
        }.runTaskLater(SootCTF.INSTANCE, 20*TIME);
    }
    
}
