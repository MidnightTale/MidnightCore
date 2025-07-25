package fun.mntale.midnightcore.api.fakeplayer;

import com.mojang.authlib.GameProfile;
import fun.mntale.midnightcore.internal.fakeplayer.DummyConnection;
import fun.mntale.midnightcore.internal.fakeplayer.DummyNetwork;
import fun.mntale.midnightcore.internal.fakeplayer.FakePlayerBroadcaster;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * A factory for creating and managing fake players.
 * This API allows other plugins to easily spawn and despawn fake players.
 */
public class FakePlayerFactory {

    private static final Map<String, ServerPlayer> FAKE_PLAYERS = new ConcurrentHashMap<>();
    private static final Map<String, Plugin> FAKE_PLAYER_OWNERS = new ConcurrentHashMap<>();

    /**
     * Creates a new fake player based on the given specification.
     *
     * @param owner The plugin that owns this fake player.
     * @param spec  The specification for the fake player.
     */
    public static void create(Plugin owner, FakePlayerSpec spec) {
        MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
        ServerLevel world = ((CraftWorld) spec.location().getWorld()).getHandle();
        GameProfile profile = (spec.skinProfile() != null) ? spec.skinProfile() : new GameProfile(spec.uuid(), spec.name());

        ServerPlayer fakePlayer = new ServerPlayer(server, world, profile, ClientInformation.createDefault());
        fakePlayer.connection = new DummyConnection(server, new DummyNetwork(), fakePlayer, CommonListenerCookie.createInitial(profile, false));
        fakePlayer.setPos(spec.location().getX(), spec.location().getY(), spec.location().getZ());

        world.addFreshEntity(fakePlayer);
        FakePlayerBroadcaster.broadcast(fakePlayer);
        FAKE_PLAYERS.put(spec.name(), fakePlayer);
        FAKE_PLAYER_OWNERS.put(spec.name(), owner);
    }

    /**
     * Creates a new fake player with a default appearance.
     *
     * @param owner    The plugin that owns this fake player.
     * @param location The location to spawn the player at.
     * @param name     The name of the player.
     */
    public static void create(Plugin owner, Location location, String name) {
        if (FAKE_PLAYERS.containsKey(name)) {
            FAKE_PLAYERS.get(name).setPos(location.getX(), location.getY(), location.getZ());
            return;
        }
        UUID fakeUuid = UUID.nameUUIDFromBytes(("FAKE_PLAYER_" + name).getBytes(StandardCharsets.UTF_8));
        FakePlayerSpec spec = new FakePlayerSpec(name, fakeUuid, location, null, false, null);
        create(owner, spec);
    }

    /**
     * Removes a fake player by name.
     *
     * @param name The name of the fake player to remove.
     * @return True if the player was removed, false otherwise.
     */
    public static boolean remove(String name) {
        ServerPlayer fakePlayer = FAKE_PLAYERS.remove(name);
        FAKE_PLAYER_OWNERS.remove(name);
        if (fakePlayer == null) {
            return false;
        }
        fakePlayer.remove(net.minecraft.world.entity.Entity.RemovalReason.DISCARDED);
        ClientboundPlayerInfoRemovePacket removePacket = new ClientboundPlayerInfoRemovePacket(List.of(fakePlayer.getUUID()));
        for (Player online : Bukkit.getOnlinePlayers()) {
            ((CraftPlayer) online).getHandle().connection.send(removePacket);
        }
        return true;
    }

    /**
     * Gets a fake player by name.
     *
     * @param name The name of the fake player.
     * @return The fake player, or null if not found.
     */
    public static Player get(String name) {
        ServerPlayer fakePlayer = FAKE_PLAYERS.get(name);
        return (fakePlayer != null) ? fakePlayer.getBukkitEntity() : null;
    }

    /**
     * Gets a collection of all fake player names.
     *
     * @return A collection of fake player names.
     */
    public static Collection<String> getNames() {
        return FAKE_PLAYERS.keySet();
    }

    /**
     * Gets a collection of all fake player names created by a specific plugin.
     *
     * @param owner The plugin to get the fake players for.
     * @return A collection of fake player names.
     */
    public static Collection<String> getNames(Plugin owner) {
        return FAKE_PLAYER_OWNERS.entrySet().stream()
            .filter(entry -> entry.getValue().equals(owner))
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
    }

    /**
     * Teleports a fake player to a new location asynchronously.
     *
     * @param name     The name of the fake player to teleport.
     * @param location The new location for the fake player.
     * @return A future that completes when the teleport is done.
     */
    public static CompletableFuture<Boolean> teleportAsync(String name, Location location) {
        ServerPlayer fakePlayer = FAKE_PLAYERS.get(name);
        if (fakePlayer == null) {
            return CompletableFuture.completedFuture(false);
        }
        return fakePlayer.getBukkitEntity().teleportAsync(location);
    }
}