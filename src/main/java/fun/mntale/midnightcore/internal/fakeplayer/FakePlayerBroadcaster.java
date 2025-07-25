package fun.mntale.midnightcore.internal.fakeplayer;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Handles broadcasting fake player packets to all online players.
 * This makes the fake players visible in the world and in the tab list.
 */
public class FakePlayerBroadcaster {

    /**
     * Broadcasts the fake player to all online players.
     *
     * @param fakePlayer The fake player to broadcast.
     */
    public static void broadcast(ServerPlayer fakePlayer) {
        ClientboundPlayerInfoUpdatePacket addPlayerInfo = ClientboundPlayerInfoUpdatePacket.createPlayerInitializing(List.of(fakePlayer));
        ServerEntity tracker = createTracker(fakePlayer);
        ClientboundAddEntityPacket addEntityPacket = new ClientboundAddEntityPacket(fakePlayer, tracker);

        for (Player online : Bukkit.getOnlinePlayers()) {
            sendPackets(online, addPlayerInfo, addEntityPacket);
        }
    }

    /**
     * Broadcasts the fake player to a single online player.
     *
     * @param fakePlayer The fake player to broadcast.
     * @param online     The player to broadcast to.
     */
    public static void broadcastToPlayer(ServerPlayer fakePlayer, Player online) {
        ClientboundPlayerInfoUpdatePacket addPlayerInfo = ClientboundPlayerInfoUpdatePacket.createPlayerInitializing(List.of(fakePlayer));
        ServerEntity tracker = createTracker(fakePlayer);
        ClientboundAddEntityPacket addEntityPacket = new ClientboundAddEntityPacket(fakePlayer, tracker);

        sendPackets(online, addPlayerInfo, addEntityPacket);
    }

    private static ServerEntity createTracker(ServerPlayer fakePlayer) {
        return new ServerEntity(
            (net.minecraft.server.level.ServerLevel) fakePlayer.level(),
            fakePlayer,
            1200, // Update interval
            true, // Track delta
            (packet) -> {}, // No-op broadcast
            (packet, uuids) -> {}, // No-op broadcast with ignore
            ConcurrentHashMap.newKeySet() // Thread-safe tracked players
        );
    }

    private static void sendPackets(Player player, Packet... packets) {
        ServerPlayer nmsPlayer = ((CraftServer) Bukkit.getServer()).getServer().getPlayerList().getPlayer(player.getUniqueId());
        if (nmsPlayer != null) {
            for (Packet packet : packets) {
                nmsPlayer.connection.send(packet);
            }
        }
    }
}