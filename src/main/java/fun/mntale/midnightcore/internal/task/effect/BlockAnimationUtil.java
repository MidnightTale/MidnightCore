package fun.mntale.midnightcore.internal.task.effect;

import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundBlockDestructionPacket;
import net.minecraft.server.level.ServerLevel;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftWorld;

/**
 * Utility for sending block-related animations to players.
 */
public class BlockAnimationUtil {

    /**
     * Sends a block crack animation to all players in the world.
     *
     * @param block    The block to send the animation for.
     * @param entityId The entity ID of the player breaking the block.
     * @param progress The progress of the animation (0-9).
     */
    public static void sendBlockCrackAnimation(Block block, int entityId, int progress) {
        try {
            if (block == null) {
                return;
            }
            ServerLevel serverLevel = ((CraftWorld) block.getWorld()).getHandle();
            BlockPos blockPos = new BlockPos(block.getX(), block.getY(), block.getZ());
            progress = Math.max(-1, Math.min(10, progress));
            serverLevel.destroyBlockProgress(entityId, blockPos, progress);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends a block destruction packet to all players.
     *
     * @param entityId The entity ID of the player breaking the block.
     * @param blockPos The position of the block.
     * @param progress The progress of the animation (0-9).
     */
    public static void sendBlockDestructionPacket(int entityId, BlockPos blockPos, int progress) {
        try {
            ClientboundBlockDestructionPacket packet = new ClientboundBlockDestructionPacket(entityId, blockPos, progress);
            org.bukkit.Bukkit.getOnlinePlayers().forEach(player -> {
                ((org.bukkit.craftbukkit.entity.CraftPlayer) player).getHandle().connection.send(packet);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}