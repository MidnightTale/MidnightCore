package fun.mntale.midnightcore.internal.fakeplayer;

import io.netty.channel.local.LocalAddress;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.PacketFlow;

import java.net.SocketAddress;

/**
 * A dummy network connection that does nothing.
 * This is used to trick the server into thinking a real player is connected.
 */
public class DummyNetwork extends Connection {

    public DummyNetwork() {
        super(PacketFlow.CLIENTBOUND);
    }

    @Override
    public SocketAddress getRemoteAddress() {
        return new LocalAddress("dummy");
    }

    // We can override other methods to be no-ops if needed,
    // but for now, this is sufficient for basic fake players.
}