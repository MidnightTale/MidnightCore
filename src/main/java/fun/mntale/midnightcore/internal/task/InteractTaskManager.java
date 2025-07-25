package fun.mntale.midnightcore.internal.task;

import com.tcoded.folialib.wrapper.task.WrappedTask;
import fun.mntale.midnightcore.MidnightCore;
import fun.mntale.midnightcore.internal.task.packet.ItemInteractUtil;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InteractTaskManager {
    private static final Map<Player, WrappedTask> interactTasks = new ConcurrentHashMap<>();

    public static boolean isInteractTaskRunning(Player player) {
        return interactTasks.containsKey(player);
    }

    public static void startInteractTask(Player player, int interval) {
        if (interactTasks.containsKey(player)) return;

        WrappedTask task = MidnightCore.getInstance().getFoliaLib().getScheduler().runAtEntityTimer(player, () -> {
            player.swingMainHand();
            ItemInteractUtil.sendNMSUseItem(player, true);
        }, 0L, interval);

        interactTasks.put(player, task);
    }

    public static void stopInteractTask(Player player) {
        WrappedTask task = interactTasks.remove(player);
        if (task != null) {
            task.cancel();
        }
    }
}