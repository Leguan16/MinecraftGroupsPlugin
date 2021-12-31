package at.noah.groups.commands;

import at.noah.groups.Groups;
import at.noah.groups.domain.Group;
import at.noah.groups.domain.WarpPoint;
import at.noah.groups.managers.GroupManager;
import at.noah.groups.managers.WarpManager;
import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public record GroupCommand(GroupManager groupManager, WarpManager warpManager) implements TabExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (sender instanceof Player player) {

            if (!player.hasPermission("group.command")) {
                player.sendMessage(Groups.PREFIX.append(Component.text("You dont have permissions for that command")));
                return true;
            }

            if (args.length < 1) {
                player.sendMessage(Groups.PREFIX.append(Component.text("Usage: /group help")));
                return true;
            }

            if ("help".equalsIgnoreCase(args[0])) {
                help(player, args);
                return true;
            }

            if (args.length == 1) {
                if ("list".equalsIgnoreCase(args[0])) {
                    groupManager.listGroupsOfPlayer(player);
                } else {
                    help(player, args);
                }
                return true;
            }
            if (args.length == 2) {
                switch (args[0].toLowerCase()) {
                    case "create" -> groupManager.createGroup(player, args[1]);
                    case "delete" -> groupManager.deleteGroup(player, args[1]);
                    default -> help(player, args);
                }
                return true;
            }

            if (args.length == 3) {
                if ("warppoint".equalsIgnoreCase(args[0])) {
                    if ("list".equalsIgnoreCase(args[1])) {
                        warpManager.listWarpPointOfGroup(player, args[2]);
                    }
                } else if ("member".equalsIgnoreCase(args[0])) {
                    if ("list".equalsIgnoreCase(args[1])) {
                        groupManager.listPlayersOfGroup(player, args[2]);
                    }
                } else {
                    help(player, args);
                }
                return true;
            }

            if (args.length == 4) {
                if ("warppoint".equalsIgnoreCase(args[0])) {
                    switch (args[1].toLowerCase()) {
                        case "create" -> warpManager.createWarpPoint(player, args[2], args[3]);
                        case "delete" -> warpManager.deleteWarpPoint(player, args[2], args[3]);
                    }
                } else if ("member".equalsIgnoreCase(args[0])) {
                    switch (args[1]) {
                        case "add" -> groupManager.addMemberToGroup(player, args[2], args[3]);
                        case "remove" -> groupManager.removeMemberFromGroup(player, args[2], args[3]);
                    }
                } else {
                    help(player, args);
                }
                return true;
            }

            help(player, args);
        } else {
            sender.sendMessage(Groups.PREFIX.append(Component.text("This command can only be executed by a Player")));
            return false;
        }

        return true;
    }

    private void help(Player player, String[] args) {

        String helpMessage = """
                List of possible commands:\s
                /group help
                /group create [name]
                /group delete [name]
                /group warppoint
                /group member""";

        if (args.length == 2) {
            if ("help".equalsIgnoreCase(args[0])) {
                switch (args[1].toLowerCase()) {
                    case "warppoints" -> helpMessage = """
                            Help for warp points:
                            /group warppoint create [group] [name]
                            /group warppoint delete [group] [name]
                            /group warppoint list [group]""";

                    case "member" -> helpMessage = """
                            Help for members:
                            /group member add [group] [player]
                            /group member remove [group] [player]
                            /group member list [group]""";
                }
            }
        } else if (args.length >= 2) {
            switch (args[0].toLowerCase()) {
                case "warppoints" -> {
                    switch (args[1]) {
                        case "create" -> helpMessage = "Usage: /group warppoint create [group] [name]";
                        case "delete" -> helpMessage = "Usage: /group warppoint delete [group] [name]";
                        case "list" -> helpMessage = "Usage: /group warppoint list [group]";
                    }
                }
                case "member" -> {
                    switch (args[1]) {
                        case "add" -> helpMessage = "Usage: /group member add [group] [player]";
                        case "remove" -> helpMessage = "Usage: /group member remove [group] [player]";
                        case "list" -> helpMessage = "Usage: /group member list [group]";
                    }
                }
            }
        }

        player.sendMessage(Groups.PREFIX.append(Component.text(helpMessage)));
    }

    @Override
    public @NotNull List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {

        if (sender instanceof Player player) {
            if (args.length == 1) {
                return List.of("help", "create", "list", "delete", "warppoint", "member");
            }
            if (args.length == 2) {
                if ("warppoint".equalsIgnoreCase(args[0])) {
                    return List.of("create", "delete", "list");
                }
                if ("member".equalsIgnoreCase(args[0])) {
                    return List.of("add", "remove", "list");
                }
                if ("help".equalsIgnoreCase(args[0])) {
                    return List.of("member", "warppoints");
                }
            }
            //group warppoint remove groupName warpName
            if (args.length == 3) {
                if ("warppoint".equalsIgnoreCase(args[0])) {
                    if ("delete".equalsIgnoreCase(args[1]) || "create".equalsIgnoreCase(args[1])) {
                        var warpPointsofPlayer = warpManager.getPossibleWarpPointsOfPlayer(player);
                        return List.of(warpPointsofPlayer.keySet().toArray(new String[0]));
                    }
                }
                if ("member".equalsIgnoreCase(args[0])) {
                    if ("remove".equalsIgnoreCase(args[1]) || "add".equalsIgnoreCase(args[1])) {
                        return groupManager.getGroupsOfMember(player).stream().map(Group::getName).toList();
                    }
                }
            }

            if (args.length == 4) {
                if ("warppoint".equalsIgnoreCase(args[0])) {
                    if ("delete".equalsIgnoreCase(args[1])) {
                        var warpPointsOfPlayer = warpManager.getPossibleWarpPointsOfPlayer(player);
                        if (warpPointsOfPlayer.containsKey(args[2].toLowerCase())) {
                            return warpPointsOfPlayer.get(args[2].toLowerCase()).stream().map(WarpPoint::name).toList();
                        }
                    }
                }
                if ("member".equalsIgnoreCase(args[0])) {
                    if ("remove".equalsIgnoreCase(args[1])) {
                        var possibleGroup = groupManager
                                .getGroupsOfMember(player)
                                .stream()
                                .filter(group -> group.getName().equals(args[2]))
                                .findFirst();

                        if (possibleGroup.isPresent()) {
                            return possibleGroup
                                    .get()
                                    .getMembers()
                                    .stream()
                                    .map(OfflinePlayer::getName)
                                    .toList();
                        }
                    }
                }
            }
        }
        return Collections.emptyList();
    }
}
