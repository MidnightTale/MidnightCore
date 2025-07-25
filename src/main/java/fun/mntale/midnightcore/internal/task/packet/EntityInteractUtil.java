package fun.mntale.midnightcore.internal.task.packet;

import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.Vec3;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

/**
 * Utility for simulating player interactions with entities using direct NMS packets.
 * Includes attack (left-click), interact (right-click), and interact at location.
 */
public class EntityInteractUtil {

    /**
     * Simulates a left-click (attack) on a target entity.
     *
     * @param bukkitPlayer The player performing the attack.
     * @param bukkitTarget The entity to be attacked.
     */
    public static void sendNMSAttack(Player bukkitPlayer, org.bukkit.entity.Entity bukkitTarget) {
        try {
            ServerPlayer nmsPlayer = ((CraftPlayer) bukkitPlayer).getHandle();
            net.minecraft.world.entity.Entity nmsTarget = ((org.bukkit.craftbukkit.entity.CraftEntity) bukkitTarget).getHandle();
            ServerboundInteractPacket attackPacket = ServerboundInteractPacket.createAttackPacket(nmsTarget, bukkitPlayer.isSneaking());
            nmsPlayer.connection.handleInteract(attackPacket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Simulates a right-click (interact) on a target entity.
     *
     * @param bukkitPlayer The player performing the interaction.
     * @param bukkitTarget The entity to interact with.
     * @param mainHand     True for main hand, false for off hand.
     */
    public static void sendNMSInteract(Player bukkitPlayer, org.bukkit.entity.Entity bukkitTarget, boolean mainHand) {
        try {
            ServerPlayer nmsPlayer = ((CraftPlayer) bukkitPlayer).getHandle();
            net.minecraft.world.entity.Entity nmsTarget = ((org.bukkit.craftbukkit.entity.CraftEntity) bukkitTarget).getHandle();
            InteractionHand hand = mainHand ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
            ServerboundInteractPacket interactPacket = ServerboundInteractPacket.createInteractionPacket(nmsTarget, bukkitPlayer.isSneaking(), hand);
            nmsPlayer.connection.handleInteract(interactPacket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Simulates a right-click (interact at location) on a target entity at a specific point.
     *
     * @param bukkitPlayer The player performing the interaction.
     * @param bukkitTarget The entity to interact with.
     * @param mainHand     True for main hand, false for off hand.
     * @param x            X offset relative to the entity's position.
     * @param y            Y offset relative to the entity's position.
     * @param z            Z offset relative to the entity's position.
     */
    public static void sendNMSInteractAt(Player bukkitPlayer, org.bukkit.entity.Entity bukkitTarget, boolean mainHand, double x, double y, double z) {
        try {
            ServerPlayer nmsPlayer = ((CraftPlayer) bukkitPlayer).getHandle();
            net.minecraft.world.entity.Entity nmsTarget = ((org.bukkit.craftbukkit.entity.CraftEntity) bukkitTarget).getHandle();
            InteractionHand hand = mainHand ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
            Vec3 location = new Vec3(x, y, z);
            ServerboundInteractPacket interactAtPacket = ServerboundInteractPacket.createInteractionPacket(nmsTarget, bukkitPlayer.isSneaking(), hand, location);
            nmsPlayer.connection.handleInteract(interactAtPacket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}