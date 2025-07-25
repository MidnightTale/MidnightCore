package fun.mntale.midnightcore.internal.task.effect;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

public class BlockParticleUtil {

    public static void spawnBlockBreakingParticles(Block block, Player player) {
        try {
            Location particleLoc = getParticleLocation(block, getHitFace(player, block));
            block.getWorld().spawnParticle(
                Particle.BLOCK_CRUMBLE,
                particleLoc,
                10,
                0.1, 0.1, 0.1,
                0.01,
                block.getBlockData()
            );
        } catch (Exception e) {
            // Ignore
        }
    }

    public static void spawnBlockBreakParticles(Block block, Player player) {
        try {
            Location blockCenter = block.getLocation().add(0.5, 0.5, 0.5);
            block.getWorld().spawnParticle(
                Particle.BLOCK,
                blockCenter,
                30,
                0.3, 0.3, 0.3,
                0.1,
                block.getBlockData()
            );
        } catch (Exception e) {
            // Ignore
        }
    }

    private static BlockFace getHitFace(Player player, Block block) {
        var result = player.rayTraceBlocks(player.getAttribute(Attribute.BLOCK_INTERACTION_RANGE).getValue());
        if (result != null && result.getHitBlock() != null && result.getHitBlock().equals(block)) {
            return result.getHitBlockFace();
        }
        return BlockFace.UP; // Default
    }

    private static Location getParticleLocation(Block block, BlockFace face) {
        Location blockCenter = block.getLocation().add(0.5, 0.5, 0.5);
        if (face != null) {
            blockCenter.add(face.getModX() * 0.5, face.getModY() * 0.5, face.getModZ() * 0.5);
        }
        return blockCenter;
    }
}