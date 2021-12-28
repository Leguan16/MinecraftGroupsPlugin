package at.noah.groups.domain;

import org.bukkit.Location;

public record WarpPoint(Group group, Location location, String name) {
}
