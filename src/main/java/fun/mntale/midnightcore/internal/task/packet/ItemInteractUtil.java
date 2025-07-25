package fun.mntale.midnightcore.internal.task.packet;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

/**
 * Utility for simulating item interactions using direct NMS packets.
 */
public class ItemInteractUtil {

    /**
     * Simulates a right-click in the air (use item).
     *
     * @param bukkitPlayer The player.
     * @param mainHand     True for main hand, false for off hand.
     */
    public static void sendNMSUseItem(Player bukkitPlayer, boolean mainHand) {
        try {
            ServerPlayer nmsPlayer = ((CraftPlayer) bukkitPlayer).getHandle();
            InteractionHand hand = mainHand ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
            int stateId = nmsPlayer.containerMenu.getStateId();
            float yRot = bukkitPlayer.getLocation().getYaw();
            float xRot = bukkitPlayer.getLocation().getPitch();
            ServerboundUseItemPacket useItemPacket = new ServerboundUseItemPacket(hand, stateId, yRot, xRot);
            nmsPlayer.connection.handleUseItem(useItemPacket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Simulates a right-click on a block (place block or use item on block).
     *
     * @param bukkitPlayer The player.
     * @param block        The block being targeted.
     * @param face         The face being targeted.
     * @param mainHand     True for main hand, false for off hand.
     */
    public static void sendNMSUseItemOn(Player bukkitPlayer, Block block, BlockFace face, boolean mainHand) {
        try {
            ServerPlayer nmsPlayer = ((CraftPlayer) bukkitPlayer).getHandle();
            InteractionHand hand = mainHand ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
            BlockPos pos = new BlockPos(block.getX(), block.getY(), block.getZ());
            BlockHitResult hitResult = new BlockHitResult(
                new Vec3(block.getX() + 0.5, block.getY() + 0.5, block.getZ() + 0.5),
                toNMSDirection(face), pos, false
            );
            int stateId = nmsPlayer.containerMenu.getStateId();
            ServerboundUseItemOnPacket packet = new ServerboundUseItemOnPacket(hand, hitResult, stateId);
            nmsPlayer.connection.handleUseItemOn(packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Simulates starting to break a block (left-click and hold).
     *
     * @param bukkitPlayer The player.
     * @param block        The block being targeted.
     * @param face         The face being targeted.
     */
    public static void sendNMSStartBreak(Player bukkitPlayer, Block block, BlockFace face) {
        sendPlayerAction(bukkitPlayer, block, face, ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK);
    }

    /**
     * Simulates stopping to break a block (release left-click).
     *
     * @param bukkitPlayer The player.
     * @param block        The block being targeted.
     * @param face         The face being targeted.
     */
    public static void sendNMSStopBreak(Player bukkitPlayer, Block block, BlockFace face) {
        sendPlayerAction(bukkitPlayer, block, face, ServerboundPlayerActionPacket.Action.STOP_DESTROY_BLOCK);
    }

    /**
     * Simulates aborting breaking a block.
     *
     * @param bukkitPlayer The player.
     * @param block        The block being targeted.
     * @param face         The face being targeted.
     */
    public static void sendNMSAbortBreak(Player bukkitPlayer, Block block, BlockFace face) {
        sendPlayerAction(bukkitPlayer, block, face, ServerboundPlayerActionPacket.Action.ABORT_DESTROY_BLOCK);
    }

    private static void sendPlayerAction(Player bukkitPlayer, Block block, BlockFace face, ServerboundPlayerActionPacket.Action action) {
        try {
            ServerPlayer nmsPlayer = ((CraftPlayer) bukkitPlayer).getHandle();
            BlockPos pos = new BlockPos(block.getX(), block.getY(), block.getZ());
            int stateId = nmsPlayer.containerMenu.getStateId();
            ServerboundPlayerActionPacket packet = new ServerboundPlayerActionPacket(
                action, pos, toNMSDirection(face), stateId
            );
            nmsPlayer.connection.handlePlayerAction(packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Direction toNMSDirection(BlockFace face) {
        return switch (face) {
            case DOWN -> Direction.DOWN;
            case UP -> Direction.UP;
            case NORTH -> Direction.NORTH;
            case SOUTH -> Direction.SOUTH;
            case WEST -> Direction.WEST;
            case EAST -> Direction.EAST;
            default -> Direction.UP;
        };
    }
}