package fun.mntale.midnightcore.internal.task;

import com.tcoded.folialib.wrapper.task.WrappedTask;
import fun.mntale.midnightcore.MidnightCore;
import fun.mntale.midnightcore.internal.task.packet.EntityInteractUtil;
import fun.mntale.midnightcore.internal.task.tool.EntityTargetingUtil;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AttackTaskManager {
    private static final Map<Player, WrappedTask> attackTasks = new ConcurrentHashMap<>();

    public static boolean isAttackTaskRunning(Player player) {
        return attackTasks.containsKey(player);
    }

    public static void startAttackTask(Player player, int interval) {
        if (attackTasks.containsKey(player)) return;

        WrappedTask task = MidnightCore.getInstance().getFoliaLib().getScheduler().runAtEntityTimer(player, () -> {
            player.swingMainHand();
            double range = player.getAttribute(Attribute.ENTITY_INTERACTION_RANGE).getValue();
            Entity target = EntityTargetingUtil.getTargetEntityDoubleRange(player, range);
            if (target != null) {
                EntityInteractUtil.sendNMSAttack(player, target);
            }
        }, 0L, interval);

        attackTasks.put(player, task);
    }

    public static void stopAttackTask(Player player) {
        WrappedTask task = attackTasks.remove(player);
        if (task != null) {
            task.cancel();
        }
    }
}