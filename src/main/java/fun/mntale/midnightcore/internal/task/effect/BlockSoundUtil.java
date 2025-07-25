package fun.mntale.midnightcore.internal.task.effect;

import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class BlockSoundUtil {

    public static void playBreakingProgressSound(Block block, Player player) {
        try {
            Sound sound = block.getBlockData().getSoundGroup().getHitSound();
            player.getWorld().playSound(block.getLocation(), sound, SoundCategory.BLOCKS, 0.5f, 1.0f);
        } catch (Exception e) {
            // Ignore
        }
    }

    public static void playBlockBreakSound(Block block, Player player) {
        try {
            Sound sound = block.getBlockData().getSoundGroup().getBreakSound();
            player.getWorld().playSound(block.getLocation(), sound, SoundCategory.BLOCKS, 1.0f, 1.0f);
        } catch (Exception e) {
            // Ignore
        }
    }

    public static void playBlockPlaceSound(Block block, Player player) {
        try {
            Sound sound = block.getBlockData().getSoundGroup().getPlaceSound();
            player.getWorld().playSound(block.getLocation(), sound, SoundCategory.BLOCKS, 0.5f, 1.0f);
        } catch (Exception e) {
            // Ignore
        }
    }

    public static void playItemUseSound(Player player) {
        try {
            ItemStack item = player.getInventory().getItemInMainHand();
            if (item.getType().isBlock()) {
                Sound sound = item.getType().createBlockData().getSoundGroup().getPlaceSound();
                player.getWorld().playSound(player.getLocation(), sound, SoundCategory.BLOCKS, 0.5f, 1.0f);
            }
        } catch (Exception e) {
            // Ignore
        }
    }
}