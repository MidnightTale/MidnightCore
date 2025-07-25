package fun.mntale.midnightcore.command;

import fun.mntale.midnightcore.MidnightCore;
import fun.mntale.midnightcore.api.fakeplayer.FakePlayerFactory;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PlayerCommand implements BasicCommand {

    @Override
    public void execute(@NotNull CommandSourceStack commandSourceStack, @NotNull String[] args) {
        CommandSender sender = commandSourceStack.getSender();
        if (args.length < 1) {
            sender.sendMessage("Usage: /player <spawn|remove|teleport|list> ...");
            return;
        }

        String sub = args[0].toLowerCase();

        switch (sub) {
            case "spawn" -> {
                if (args.length < 2) {
                    sender.sendMessage("Usage: /player spawn <name>");
                    return;
                }
                if (!(sender instanceof Player player)) {
                    sender.sendMessage("This command can only be used by a player.");
                    return;
                }
                FakePlayerFactory.create(MidnightCore.instance, player.getLocation(), args[1]);
                sender.sendMessage("Spawned fake player: " + args[1]);
            }
            case "remove" -> {
                if (args.length < 2) {
                    sender.sendMessage("Usage: /player remove <name>");
                    return;
                }
                boolean removed = FakePlayerFactory.remove(args[1]);
                if (removed) {
                    sender.sendMessage("Removed fake player: " + args[1]);
                } else {
                    sender.sendMessage("No fake player found with name: " + args[1]);
                }
            }
            case "teleport" -> {
                if (args.length < 2) {
                    sender.sendMessage("Usage: /player teleport <name> [x y z]");
                    return;
                }
                if (!(sender instanceof Player player)) {
                    sender.sendMessage("This command can only be used by a player.");
                    return;
                }
                org.bukkit.Location targetLocation;
                if (args.length >= 5) {
                    try {
                        double x = Double.parseDouble(args[2]);
                        double y = Double.parseDouble(args[3]);
                        double z = Double.parseDouble(args[4]);
                        targetLocation = new org.bukkit.Location(player.getWorld(), x, y, z);
                    } catch (NumberFormatException e) {
                        sender.sendMessage("Invalid coordinates.");
                        return;
                    }
                } else {
                    targetLocation = player.getLocation();
                }
                FakePlayerFactory.teleportAsync(args[1], targetLocation).thenAccept(teleported -> {
                    if (teleported) {
                        sender.sendMessage("Teleported fake player: " + args[1]);
                    } else {
                        sender.sendMessage("No fake player found with name: " + args[1]);
                    }
                });
            }
            case "list" -> {
                Collection<String> names = FakePlayerFactory.getNames(MidnightCore.instance);
                if (names.isEmpty()) {
                    sender.sendMessage("No fake players found for this plugin.");
                } else {
                    sender.sendMessage("Fake players for " + MidnightCore.instance.getName() + ": " + String.join(", ", names));
                }
            }
            default -> sender.sendMessage("Usage: /player <spawn|remove|teleport|list> ...");
        }
    }

    @Override
    public @Nullable String permission() {
        return "midnightcore.player";
    }

    @Override
    public Collection<String> suggest(CommandSourceStack commandSourceStack, String[] args) {
        List<String> suggestions = new ArrayList<>();
        if (args.length == 1) {
            if ("spawn".startsWith(args[0].toLowerCase())) suggestions.add("spawn");
            if ("remove".startsWith(args[0].toLowerCase())) suggestions.add("remove");
            if ("teleport".startsWith(args[0].toLowerCase())) suggestions.add("teleport");
            if ("list".startsWith(args[0].toLowerCase())) suggestions.add("list");
        } else if (args.length == 2 && (args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("teleport"))) {
            for (String name : FakePlayerFactory.getNames(MidnightCore.instance)) {
                if (name.toLowerCase().startsWith(args[1].toLowerCase())) {
                    suggestions.add(name);
                }
            }
        }
        return suggestions;
    }
}