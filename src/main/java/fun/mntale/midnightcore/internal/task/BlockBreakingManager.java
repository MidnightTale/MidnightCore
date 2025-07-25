package fun.mntale.midnightcore.internal.task;

import com.tcoded.folialib.wrapper.task.WrappedTask;
import fun.mntale.midnightcore.MidnightCore;
import fun.mntale.midnightcore.internal.task.effect.BlockAnimationUtil;
import fun.mntale.midnightcore.internal.task.effect.BlockBreakingUtil;
import fun.mntale.midnightcore.internal.task.effect.BlockParticleUtil;
import fun.mntale.midnightcore.internal.task.effect.BlockSoundUtil;
import fun.mntale.midnightcore.internal.task.tool.BlockTargetingUtil;
import fun.mntale.midnightcore.internal.task.tool.BlockValidationUtil;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BlockBreakingManager {
    private static final Map<Player, WrappedTask> breakTasks = new ConcurrentHashMap<>();
    private static final Map<Player, Map<Block, WrappedTask>> activeBreakingTasks = new ConcurrentHashMap<>();

    public static boolean isBreakTaskRunning(Player player) {
        return breakTasks.containsKey(player);
    }

    public static void startBreakTask(Player player, int interval) {
        if (breakTasks.containsKey(player)) return;

        Map<Block, WrappedTask> playerActiveTasks = new ConcurrentHashMap<>();
        activeBreakingTasks.put(player, playerActiveTasks);

        WrappedTask outerTask = MidnightCore.getInstance().getFoliaLib().getScheduler().runAtEntityTimer(player, () -> {
            Block targetBlock = BlockTargetingUtil.getPlayerTargetBlock(player);

            if (targetBlock == null || !BlockValidationUtil.isBreakableBlock(targetBlock) || playerActiveTasks.containsKey(targetBlock)) {
                return;
            }

            int breakTime = BlockBreakingUtil.estimateBreakTime(player, targetBlock);
            startBlockBreakingAnimation(player, targetBlock, breakTime, playerActiveTasks);

        }, 0L, interval);

        breakTasks.put(player, outerTask);
    }

    public static void stopBreakTask(Player player) {
        WrappedTask task = breakTasks.remove(player);
        if (task != null) {
            task.cancel();
        }

        Map<Block, WrappedTask> playerTasks = activeBreakingTasks.remove(player);
        if (playerTasks != null) {
            playerTasks.values().forEach(WrappedTask::cancel);
        }

        Block targetBlock = BlockTargetingUtil.getPlayerTargetBlock(player);
        if (targetBlock != null) {
            BlockAnimationUtil.sendBlockCrackAnimation(targetBlock, player.getEntityId(), -1);
        }
    }

    private static void startBlockBreakingAnimation(Player player, Block block, int breakTime, Map<Block, WrappedTask> activeTasks) {
        final int entityId = player.getEntityId();
        final net.minecraft.core.BlockPos blockPos = new net.minecraft.core.BlockPos(block.getX(), block.getY(), block.getZ());
        final int delay = Math.max(1, breakTime / 10);

        final int[] progress = {0};
        final WrappedTask[] taskRef = new WrappedTask[1];

        taskRef[0] = MidnightCore.getInstance().getFoliaLib().getScheduler().runAtLocationTimer(
            block.getLocation(),
            () -> {
                Block currentTargetBlock = BlockTargetingUtil.getPlayerTargetBlock(player);
                if (currentTargetBlock == null || !currentTargetBlock.getLocation().equals(block.getLocation()) || block.getType().isAir()) {
                    BlockAnimationUtil.sendBlockDestructionPacket(entityId, blockPos, -1);
                    activeTasks.remove(block);
                    taskRef[0].cancel();
                    return;
                }

                player.swingMainHand();
                BlockParticleUtil.spawnBlockBreakingParticles(block, player);
                BlockSoundUtil.playBreakingProgressSound(block, player);
                BlockAnimationUtil.sendBlockDestructionPacket(entityId, blockPos, progress[0]);

                if (progress[0] >= 9) {
                    BlockAnimationUtil.sendBlockDestructionPacket(entityId, blockPos, -1);
                    BlockSoundUtil.playBlockBreakSound(block, player);
                    BlockParticleUtil.spawnBlockBreakParticles(block, player);
                    block.breakNaturally(player.getInventory().getItemInMainHand());
                    activeTasks.remove(block);
                    taskRef[0].cancel();
                    return;
                }

                progress[0]++;
            },
            0L,
            delay
        );

        activeTasks.put(block, taskRef[0]);
    }
}