package fun.mntale.midnightcore.internal.task;

import com.tcoded.folialib.wrapper.task.WrappedTask;
import fun.mntale.midnightcore.MidnightCore;
import fun.mntale.midnightcore.internal.task.packet.ItemInteractUtil;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UseTaskManager {
    private static final Map<Player, WrappedTask> useTasks = new ConcurrentHashMap<>();

    public static boolean isUseTaskRunning(Player player) {
        return useTasks.containsKey(player);
    }

    public static void startUseTask(Player player, int interval) {
        if (useTasks.containsKey(player)) return;

        WrappedTask task = MidnightCore.getInstance().getFoliaLib().getScheduler().runAtEntityTimer(player, () -> {
            player.swingMainHand();
            ItemInteractUtil.sendNMSUseItem(player, false);
        }, 0L, interval);

        useTasks.put(player, task);
    }

    public static void stopUseTask(Player player) {
        WrappedTask task = useTasks.remove(player);
        if (task != null) {
            task.cancel();
        }
    }
}