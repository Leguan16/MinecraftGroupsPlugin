package at.noah.groups.managers;

import at.noah.groups.Groups;
import at.noah.groups.domain.Group;
import at.noah.groups.domain.WarpPoint;
import at.noah.groups.util.ComponentUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.*;

public class WarpManager {

    private final GroupManager groupManager;

    public WarpManager(GroupManager groupManager) {
        this.groupManager = groupManager;
    }

    public void createWarpPoint(Player executor, String groupName, String warpPointName) {
        groupManager.getGroup(groupName)
                .ifPresentOrElse(
                        group -> createWarpPoint(executor, group, warpPointName),
                        () -> executor.sendMessage(Groups.PREFIX
                                .append(Component.text("No gorup found with name "))
                                .append(Component.text(groupName).color(ComponentUtil.GOLD))));

    }

    private void createWarpPoint(Player executor, Group group, String warpPointName) {
        if (!group.getMembers().contains(executor)) {
            executor.sendMessage(Groups.PREFIX.append(Component.text("You need to be part of the group to create a warp point for it")));
            return;
        }
        if (!group.getOwner().equals(executor)) {
            executor.sendMessage(Groups.PREFIX.append(Component.text("Only the group owner can create warp points")));
            return;
        }

        var warps = group.getWarpPoints();

        if (warps.containsKey(warpPointName.toLowerCase())) {
            executor.sendMessage(Groups.PREFIX.append(Component.text("A warp point with that name already exists!")));
            return;
        }

        group.addWarpPoint(warpPointName, new WarpPoint(group, executor.getLocation(), warpPointName.toLowerCase()));

        executor.sendMessage(Groups.PREFIX
                .append(Component.text("Created warp point with name "))
                .append(Component.text(warpPointName).color(ComponentUtil.GOLD))
                .append(Component.text(" at your current location")));
    }

    public void deleteWarpPoint(Player executor, String groupName, String warpPointName) {
        groupManager.getGroup(groupName)
                .ifPresentOrElse(
                        group -> deleteWarpPoint(executor, group, warpPointName),
                        () -> executor.sendMessage(Groups.PREFIX
                                .append(Component.text("No gorup found with name "))
                                .append(Component.text(groupName).color(ComponentUtil.GOLD))));
    }

    private void deleteWarpPoint(Player executor, Group group, String warpPointName) {
        if (!group.getMembers().contains(executor)) {
            executor.sendMessage(Groups.PREFIX.append(Component.text("You need to be part of the group to create a warp point for it")));

            return;
        }
        if (!group.getOwner().equals(executor)) {
            executor.sendMessage(Groups.PREFIX.append(Component.text("Only the group owner can create warp points")));
            return;
        }

        var warps = group.getWarpPoints();

        if (!warps.containsKey(warpPointName.toLowerCase())) {
            executor.sendMessage(Groups.PREFIX.append(Component
                    .text("No warp point with the name "))
                    .append(Component.text(warpPointName).color(ComponentUtil.GOLD))
                    .append(Component.text(" was found")));
            return;
        }

        group.removeWarpPoint(warpPointName);

        executor.sendMessage(Groups.PREFIX
                .append(Component.text("removed warp point with name "))
                .append(Component.text(warpPointName).color(ComponentUtil.GOLD))
                .append(Component.text(" from group " ))
                .append(Component.text(group.getName()).color(ComponentUtil.GOLD)));
    }

    public Map<String, List<WarpPoint>> getPossibleWarpPointsOfPlayer(Player player) {
        var groups = groupManager.getGroupsOfMember(player);

        Map<String, List<WarpPoint>> warps = new HashMap<>();

        for (Group group : groups) {
            var warpPointsOfGroup = group.getWarpPoints();

            var warpList = new ArrayList<>(warpPointsOfGroup.values());

            warps.put(group.getName(), warpList);
        }

        return warps;
    }

    public void warpPlayer(Player player, String groupName, String warpPointName) {
        Optional<Group> possibleGroup = groupManager.getGroup(groupName);

        if (possibleGroup.isEmpty()) {
            player.sendMessage(Groups.PREFIX.append(Component.text("no group fond with name ")).append(Component.text(groupName).color(ComponentUtil.GOLD)));
            return;
        }

        Group group = possibleGroup.get();

        if (!group.getWarpPoints().containsKey(warpPointName.toLowerCase())) {
            player.sendMessage(Groups.PREFIX
                    .append(Component.text("No warp point with the name "))
                    .append(Component.text(warpPointName).color(ComponentUtil.GOLD))
                    .append(Component.text(" found in group"))
                    .append(Component.text(group.getName()).color(ComponentUtil.GOLD)));
            return;
        }

        player.teleport(group.getWarpPoints().get(warpPointName.toLowerCase()).location());

        player.sendMessage(Groups.PREFIX
                .append(Component.text("Teleported you to warp point "))
                .append(Component.text(warpPointName).color(ComponentUtil.GOLD)));
    }

}
