package fun.mntale.midnightcore.command;

import fun.mntale.midnightcore.api.task.PlayerTaskApi;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class TaskCommand implements BasicCommand {

    @Override
    public void execute(@NotNull CommandSourceStack stack, @NotNull String[] args) {
        if (!(stack.getSender() instanceof Player player)) {
            stack.getSender().sendMessage("This command can only be used by a player.");
            return;
        }

        if (args.length < 2) {
            sendUsage(stack.getSender());
            return;
        }

        String taskType = args[0].toLowerCase();
        String action = args[1].toLowerCase();

        switch (taskType) {
            case "attack" -> handleTask(player, "Attack", action, args, PlayerTaskApi::isAttackTaskRunning, PlayerTaskApi::startAttackTask, PlayerTaskApi::stopAttackTask);
            case "interact" -> handleTask(player, "Interact", action, args, PlayerTaskApi::isInteractTaskRunning, PlayerTaskApi::startInteractTask, PlayerTaskApi::stopInteractTask);
            case "use" -> handleTask(player, "Use", action, args, PlayerTaskApi::isUseTaskRunning, PlayerTaskApi::startUseTask, PlayerTaskApi::stopUseTask);
            case "place" -> handleTask(player, "Place", action, args, PlayerTaskApi::isPlaceTaskRunning, PlayerTaskApi::startPlaceTask, PlayerTaskApi::stopPlaceTask);
            case "break" -> handleTask(player, "Break", action, args, PlayerTaskApi::isBreakTaskRunning, PlayerTaskApi::startBreakTask, PlayerTaskApi::stopBreakTask);
            default -> sendUsage(stack.getSender());
        }
    }

    private void handleTask(Player player, String taskName, String action, String[] args, Predicate<Player> isRunning, BiConsumer<Player, Integer> start, Consumer<Player> stop) {
        if (action.equals("start")) {
            if (args.length < 3) {
                player.sendMessage("Usage: /task " + taskName.toLowerCase() + " start <interval>");
                return;
            }
            int interval;
            try {
                interval = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                player.sendMessage("Interval must be a number (ticks).");
                return;
            }
            if (isRunning.test(player)) {
                player.sendMessage(taskName + " task is already running. Use /task " + taskName.toLowerCase() + " stop first.");
                return;
            }
            start.accept(player, interval);
            player.sendMessage("Started " + taskName.toLowerCase() + " task every " + interval + " ticks.");
        } else if (action.equals("stop")) {
            if (isRunning.test(player)) {
                stop.accept(player);
                player.sendMessage("Stopped " + taskName.toLowerCase() + " task.");
            } else {
                player.sendMessage("No " + taskName.toLowerCase() + " task is running.");
            }
        } else {
            sendUsage(player);
        }
    }

    private void sendUsage(CommandSender sender) {
        sender.sendMessage("Usage: /task <attack|interact|use|place|break> <start <interval>|stop>");
    }

    @Override
    public Collection<String> suggest(CommandSourceStack stack, String[] args) {
        List<String> suggestions = new ArrayList<>();
        if (args.length == 1) {
            suggestions.addAll(List.of("attack", "interact", "use", "place", "break"));
        } else if (args.length == 2) {
            suggestions.addAll(List.of("start", "stop"));
        } else if (args.length == 3 && args[1].equalsIgnoreCase("start")) {
            suggestions.addAll(List.of("5", "10", "20"));
        }
        return suggestions;
    }
}