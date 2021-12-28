package at.noah.groups.util;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.Optional;

public class PlayerUtil {

    public static Optional<OfflinePlayer> getOfflinePlayer(String name) {
        OfflinePlayer player = Bukkit.getOfflinePlayerIfCached(name);

        if (player == null) {
            return Optional.empty();
        }
        return Optional.of(player);
    }
}
