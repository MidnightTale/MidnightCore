package fun.mntale.midnightcore.internal.task.packet;

import net.minecraft.network.protocol.game.ServerboundSwingPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

/**
 * Utility for sending miscellaneous player packets.
 */
public class PacketUtil {

    /**
     * Sends a swing arm packet for the player.
     *
     * @param player The player to send the packet for.
     * @param hand   The hand to swing (0 for main hand, 1 for off hand).
     */
    public static void sendSwingArmPacket(Player player, int hand) {
        try {
            ServerPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
            InteractionHand interactionHand = (hand == 0) ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
            ServerboundSwingPacket swingPacket = new ServerboundSwingPacket(interactionHand);
            nmsPlayer.connection.handleAnimate(swingPacket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}