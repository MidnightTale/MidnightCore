package fun.mntale.midnightcore.internal.fakeplayer;

import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

import javax.annotation.Nonnull;

/**
 * A dummy packet listener that does nothing.
 * This is required to handle packets for fake players without any actual network I/O.
 */
public class DummyConnection extends ServerGamePacketListenerImpl {

    public DummyConnection(MinecraftServer server, Connection networkManager, ServerPlayer player, CommonListenerCookie cookie) {
        super(server, networkManager, player, cookie);
    }

    @Override
    public void send(@Nonnull Packet<?> packet) {
        // Do nothing. We don't want to send any packets to our fake player.
    }
}