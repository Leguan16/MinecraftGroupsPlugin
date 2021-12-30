package at.noah.groups.features;

import at.noah.groups.domain.Group;
import at.noah.groups.domain.WarpPoint;
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
                        () -> executor.sendMessage("No gorup found with name " + groupName));


    }

    private void createWarpPoint(Player executor, Group group, String warpPointName) {
        if (!group.getMembers().contains(executor)) {
            executor.sendMessage("You need to be part of the group to create a warp point for it");
            return;
        }
        if (!group.getOwner().equals(executor)) {
            executor.sendMessage("Only the group owner can create warp points");
            return;
        }

        var warps = group.getWarpPoints();

        if (warps.containsKey(warpPointName.toLowerCase())) {
            executor.sendMessage("A warp point with that name already exists!");
            return;
        }

        group.addWarpPoint(warpPointName, new WarpPoint(group, executor.getLocation(), warpPointName.toLowerCase()));

        executor.sendMessage("Created warp point with name " + warpPointName + " at your current location");
    }

    public void deleteWarpPoint(Player executor, String groupName, String warpPointName) {
        groupManager.getGroup(groupName)
                .ifPresentOrElse(
                        group -> deleteWarpPoint(executor, group, warpPointName),
                        () -> executor.sendMessage("No gorup found with name " + groupName));
    }

    private void deleteWarpPoint(Player executor, Group group, String warpPointName) {
        if (!group.getMembers().contains(executor)) {
            executor.sendMessage("You need to be part of the group to remove a warp point!");
            return;
        }
        if (!group.getOwner().equals(executor)) {
            executor.sendMessage("Only the group owner can remove warp points!");
            return;
        }

        var warps = group.getWarpPoints();

        if (!warps.containsKey(warpPointName.toLowerCase())) {
            executor.sendMessage("No warp point with the name " + warpPointName + " was found");
            return;
        }

        group.removeWarpPoint(warpPointName);

        executor.sendMessage("removed warp point with name " + warpPointName + " from group " + group.getName());
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
            player.sendMessage("no group fond with name " + groupName);
            return;
        }

        Group group = possibleGroup.get();

        if (!group.getWarpPoints().containsKey(warpPointName.toLowerCase())){
            player.sendMessage("No warp point with name " + warpPointName + " found in group " + group.getName());
            return;
        }

        player.teleport(group.getWarpPoints().get(warpPointName.toLowerCase()).location());

        player.sendMessage("Teleported you to warp point " + warpPointName);
    }

}
