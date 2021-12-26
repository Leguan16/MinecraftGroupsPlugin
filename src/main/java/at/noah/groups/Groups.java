package at.noah.groups;

import org.bukkit.plugin.java.JavaPlugin;

public final class Groups extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        getLog4JLogger().info("Enabled!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLog4JLogger().info("Disabled!");
    }
}
