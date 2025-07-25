package fun.mntale.midnightcore.api.fakeplayer;

import com.mojang.authlib.GameProfile;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Specifies the properties for creating a new fake player.
 *
 * @param name                The name of the fake player.
 * @param uuid                The UUID of the fake player.
 * @param location            The location where the fake player will spawn.
 * @param skinProfile         The game profile to use for the player's skin (optional).
 * @param copyStateFromPlayer If true, copies the state from the {@code stateSource} player.
 * @param stateSource         The player to copy the state from (optional).
 */
public record FakePlayerSpec(
    String name,
    UUID uuid,
    Location location,
    GameProfile skinProfile,
    boolean copyStateFromPlayer,
    Player stateSource
) {
}