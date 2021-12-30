package at.noah.groups.managers;

import at.noah.groups.Groups;
import at.noah.groups.domain.Group;
import at.noah.groups.util.ComponentUtil;
import at.noah.groups.util.PlayerUtil;
import net.kyori.adventure.text.Component;
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

            owner.sendMessage(Groups.PREFIX
                    .append(Component.text("Group with name "))
                    .append(Component.text(name).color(ComponentUtil.GOLD))
                    .append(Component.text(" already exists")));

            return;
        }

        if (!owner.hasPermission("group.create")) {
            owner.sendMessage(Groups.PREFIX.append(Component.text("You dont have enough permissions to create a group")));
            return;
        }

        groups.put(name, new Group(owner, name));

        owner.sendMessage(Groups.PREFIX
                .append(Component.text("Created group with name "))
                .append(Component.text(name).color(ComponentUtil.GOLD))
                .append(Component.text("!")));
    }

    public void deleteGroup(Player player, String name) {


        if (!player.hasPermission("group.delete")) {
            player.sendMessage(Groups.PREFIX.append(Component.text("You dont have enough permissions to delete a group")));
            return;
        }

        if (!groups.get(name).getOwner().equals(player)) {
            player.sendMessage(Groups.PREFIX.append(Component.text("Only the owner can delete their groups")));
            return;
        }

        groups.remove(name);

        player.sendMessage(Groups.PREFIX
                .append(Component.text("Deleted group with name "))
                .append(Component.text(name).color(ComponentUtil.GOLD))
                .append(Component.text("!")));

    }

    // TODO: 26/12/2021 make this work with multiple names?

    public void addMemberToGroup(Player executor, String groupName, String toAddName) {
        if (!groups.containsKey(groupName)) {
            executor.sendMessage(Groups.PREFIX
                    .append(Component.text("Group with name "))
                    .append(Component.text(groupName).color(ComponentUtil.GOLD))
                    .append(Component.text(" does not exist!")));
            return;
        }

//        if (!executor.hasPermission("group.addMembers")) {
//            executor.sendMessage("You dont have enough permissions to create a group");
//            return;
//        }

        Group group = groups.get(groupName);

        if (!group.getOwner().equals(executor)) {
            executor.sendMessage(Groups.PREFIX.append(Component.text("Only the owner can add members to the group")));
            return;
        }

        var offlinePlayer = PlayerUtil.getOfflinePlayer(toAddName);

        if (offlinePlayer.isEmpty()) {
            executor.sendMessage(Groups.PREFIX
                    .append(Component.text("No player found with name "))
                    .append(Component.text(toAddName).color(ComponentUtil.GOLD)));
            return;
        }

        OfflinePlayer toAdd = offlinePlayer.get();

        toAddName = toAdd.getName();


        assert toAddName != null;

        if (group.getMembers().contains(executor)) {
            executor.sendMessage(Groups.PREFIX
                    .append(Component.text("Player "))
                    .append(Component.text(toAddName).color(ComponentUtil.GOLD))
                    .append(Component.text(" is already in your group!")));
            return;
        }

        groups.get(groupName).addMember(toAdd);

        executor.sendMessage(Groups.PREFIX
                .append(Component.text("Added "))
                .append(Component.text(toAdd.getName()).color(ComponentUtil.GOLD))
                .append(Component.text("to group "))
                .append(Component.text(groupName).color(ComponentUtil.GOLD)));
    }

    public void removeMemberFromGroup(Player executor, String groupName, String toRemoveName) {
        if (!groups.containsKey(groupName)) {
            executor.sendMessage(Groups.PREFIX
                    .append(Component.text("Group with name "))
                    .append(Component.text(groupName).color(ComponentUtil.GOLD))
                    .append(Component.text(" does not exist!")));
            return;
        }

//        if (!executor.hasPermission("group.addMembers")) {
//            player.sendMessage("You dont have enough permissions to create a group");
//            return;
//        }

        Group group = groups.get(groupName);

        if (!group.getOwner().equals(executor)) {
            executor.sendMessage(Groups.PREFIX.append(Component.text("Only the owner can remove members to the group")));
            return;
        }

        var offlinePlayer = PlayerUtil.getOfflinePlayer(toRemoveName);

        if (offlinePlayer.isEmpty()) {
            executor.sendMessage(Groups.PREFIX
                    .append(Component.text("No player found with name "))
                    .append(Component.text(toRemoveName).color(ComponentUtil.GOLD)));
            return;
        }

        OfflinePlayer toRemove = offlinePlayer.get();

        toRemoveName = toRemove.getName();

        assert toRemoveName != null;
        
        if (!group.getMembers().contains(executor)) {
            executor.sendMessage(Groups.PREFIX
                    .append(Component.text("Player "))
                    .append(Component.text(toRemove.getName()).color(ComponentUtil.GOLD))
                    .append(Component.text(" is not in your group!")));
            return;
        }

        group.removeMember(toRemove);

        executor.sendMessage(Groups.PREFIX
                .append(Component.text("Added "))
                .append(Component.text(toRemove.getName()).color(ComponentUtil.GOLD))
                .append(Component.text(" from group "))
                .append(Component.text(groupName).color(ComponentUtil.GOLD)));
    }

    public void listGroupsOfPlayer(Player player) {
        List<Group> groupsContainingPlayer = getGroupsOfMember(player);

        groupsContainingPlayer.forEach(group -> System.out.println(group.getName()));

        StringBuilder message = new StringBuilder("Your Groups:\n");

        for (Group group : groupsContainingPlayer) {
            message.append(group.getName()).append("\n");
        }

        player.sendMessage(Groups.PREFIX.append(Component.text(message.toString())));
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
