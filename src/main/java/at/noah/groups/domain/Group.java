package at.noah.groups.domain;

import org.bukkit.OfflinePlayer;

import java.util.*;

public class Group {

    private final OfflinePlayer owner;
    private String name;

    private final List<OfflinePlayer> members = new ArrayList<>();
    private final Map<String, WarpPoint> warpPoints = new HashMap<>();

    public Group(OfflinePlayer owner, String name) {
        this(owner, name, Collections.emptyList());
    }

    public Group(OfflinePlayer owner, String name, Collection<OfflinePlayer> members) {
        this.owner = owner;
        this.name = name;

        this.members.add(owner);
        this.members.addAll(members);
    }

    public Group(OfflinePlayer owner, String name, Collection<OfflinePlayer> members, Map<String, WarpPoint> warpPoints) {
        this.owner = owner;
        this.name = name;

        this.members.add(owner);
        this.members.addAll(members);

        this.warpPoints.putAll(warpPoints);
    }

    public OfflinePlayer getOwner() {
        return owner;
    }

    public String getName() {
        return name;
    }

    public List<OfflinePlayer> getMembers() {
        return List.copyOf(members);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addMember(OfflinePlayer player) {
        members.add(player);
    }

    public void removeMember(OfflinePlayer player) {
        members.remove(player);
    }

    public Map<String, WarpPoint> getWarpPoints() {
        return Map.copyOf(warpPoints);
    }

    public void addWarpPoint(String name, WarpPoint warpPoint) {
        warpPoints.put(name.toLowerCase(), warpPoint);
    }

    public void removeWarpPoint(String name) {
        warpPoints.remove(name.toLowerCase());
    }
}
