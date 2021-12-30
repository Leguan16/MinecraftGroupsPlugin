package at.noah.groups;

import at.noah.groups.commands.GroupCommand;
import at.noah.groups.commands.WarpCommand;
import at.noah.groups.managers.GroupManager;
import at.noah.groups.managers.WarpManager;
import at.noah.groups.util.ComponentUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class Groups extends JavaPlugin {

    private final GroupManager groupManager = new GroupManager();
    private final WarpManager warpManager = new WarpManager(groupManager);

    public static final Component PREFIX =
            Component
                    .text("[")
                    .color(ComponentUtil.GRAY)
                    .append(Component
                            .text("Groups")
                            .color(ComponentUtil.GREEN))
                    .append(Component
                            .text("] ")
                            .color(ComponentUtil.GRAY));

    @Override
    public void onEnable() {
        // Plugin startup logic
        initCommands();
        getLog4JLogger().info("Enabled!");
    }

    private void initCommands() {
        Objects.requireNonNull(getCommand("group")).setExecutor(new GroupCommand(groupManager, warpManager));
        Objects.requireNonNull(getCommand("warp")).setExecutor(new WarpCommand(warpManager));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLog4JLogger().info("Disabled!");
    }
}
