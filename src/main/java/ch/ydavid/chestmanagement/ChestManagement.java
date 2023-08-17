package ch.ydavid.chestmanagement;

import org.bukkit.plugin.java.JavaPlugin;

public final class ChestManagement extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        this.getCommand("search").setExecutor(new ChestSearch());

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
