package at.noah.groups.features;

import at.noah.groups.domain.Group;
import at.noah.groups.util.PlayerUtil;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class GroupManager {

    private final Map<String, Group> groups = new HashMap<>();

    public void createGroup(Player owner, String name) {
        if (groups.containsKey(name)) {
            owner.sendMessage("Group with name " + name + " already exists");
            return;
        }

        if (!owner.hasPermission("group.create")) {
            owner.sendMessage("You dont have enough permissions to create a group");
            return;
        }

        groups.put(name, new Group(owner, name));

        owner.sendMessage("Created group with name " + name + "!");
    }

    public void deleteGroup(Player player, String name) {
        if (!groups.containsKey(name)) {
            player.sendMessage("Group with name " + name + " does not exist");
            return;
        }

        if (!player.hasPermission("group.delete")) {
            player.sendMessage("You dont have enough permissions to create a group");
            return;
        }

        if (!groups.get(name).getOwner().equals(player)) {
            player.sendMessage("Only the owner can delete their groups");
            return;
        }

        groups.remove(name);

        player.sendMessage("Deleted group with name " + name + "!");
    }

    // TODO: 26/12/2021 make this work with multiple names?
    public void addMemberToGroup(Player executor, String groupName, String toAddName) {
        if (!groups.containsKey(groupName)) {
            executor.sendMessage("Group with name " + groupName + " does not exist");
            return;
        }

//        if (!executor.hasPermission("group.addMembers")) {
//            executor.sendMessage("You dont have enough permissions to create a group");
//            return;
//        }

        Group group = groups.get(groupName);

        if (!group.getOwner().equals(executor)) {
            executor.sendMessage("Only the owner can add members to the group");
            return;
        }

        var offlinePlayer = PlayerUtil.getOfflinePlayer(toAddName);

        if (offlinePlayer.isEmpty()) {
            executor.sendMessage("No player found with name " + toAddName);
            return;
        }

        OfflinePlayer toAdd = offlinePlayer.get();

        if (group.getMembers().contains(executor)) {
            executor.sendMessage("Player " + toAdd.getName() + " is already in your group!");
            return;
        }

        groups.get(groupName).addMember(toAdd);

        executor.sendMessage("Added " + toAdd.getName() + "to group " + groupName);
    }

    public void removeMemberFromGroup(Player executor, String groupName, String toRemoveName) {
        if (!groups.containsKey(groupName)) {
            executor.sendMessage("Group with name " + groupName + " does not exist");
            return;
        }

//        if (!executor.hasPermission("group.addMembers")) {
//            player.sendMessage("You dont have enough permissions to create a group");
//            return;
//        }

        Group group = groups.get(groupName);

        if (!group.getOwner().equals(executor)) {
            executor.sendMessage("Only the owner can remove members from the group");
            return;
        }

        var offlinePlayer = PlayerUtil.getOfflinePlayer(toRemoveName);

        if (offlinePlayer.isEmpty()) {
            executor.sendMessage("No player found with name " + toRemoveName);
            return;
        }

        OfflinePlayer toRemove = offlinePlayer.get();

        if (!group.getMembers().contains(executor)) {
            executor.sendMessage("Player " + toRemove.getName() + " is not in your group!");
            return;
        }

        group.removeMember(toRemove);

        executor.sendMessage("Removed " + toRemove.getName() + " from group " + groupName);
    }

    public void listGroupsOfPlayer(Player player) {
        List<Group> groupsContainingPlayer = getGroupsOfMember(player);

        System.out.println("groups");
        groupsContainingPlayer.forEach(group -> System.out.println(group.getName()));

        StringBuilder message = new StringBuilder("Your Groups:\n");

        for (Group group : groupsContainingPlayer) {
            message.append(group.getName()).append("\n");
        }

        String finalMessage = message.toString();
        System.out.println(finalMessage);
        player.sendMessage(finalMessage);
    }

    public Optional<Group> getGroup(String groupName) {
        if (groups.containsKey(groupName)) {
            Group group = groups.get(groupName);

            return Optional.of(group);
        }

        return Optional.empty();
    }

    public List<Group> getGroupsOfMember(Player player) {
        return groups
                .values()
                .stream()
                .filter(group -> group.getMembers().contains(player))
                .toList();
    }
}
