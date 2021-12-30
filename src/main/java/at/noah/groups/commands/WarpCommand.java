package at.noah.groups.commands;

import at.noah.groups.domain.WarpPoint;
import at.noah.groups.features.WarpManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public record WarpCommand(WarpManager warpManager) implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (sender instanceof Player player) {
            if (args.length < 1 || args.length > 2) {
                player.sendMessage("Usage: /warp groupName warpName");
            } else {
                warpManager.warpPlayer(player, args[0], args[1]);
            }
        } else {
            sender.sendMessage("This command can only be executed by a player!");
        }
        return true;
    }

    @Override
    public @NotNull List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {

        if (sender instanceof Player player) {
            if (args.length == 1) {
                var warpPointsOfPlayer = warpManager.getPossibleWarpPointsOfPlayer(player);
                return List.of(warpPointsOfPlayer.keySet().toArray(new String[0]));
            }
            if (args.length == 2) {
                var warpPointsOfPlayer = warpManager.getPossibleWarpPointsOfPlayer(player);
                if (warpPointsOfPlayer.containsKey(args[0].toLowerCase())) {
                    return warpPointsOfPlayer.get(args[0].toLowerCase()).stream().map(WarpPoint::name).toList();
                }
            }
        }

        return Collections.emptyList();
    }
}
