package ch.ydavid.chestmanagement;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class ChestManagement extends JavaPlugin {
    private FileConfiguration messagesConfig;

    @Override
    public void onEnable() {
        // Plugin startup logic

        saveDefaultConfig();
        saveConfig();
        messagesConfig = getConfig();
        this.getCommand("search").setExecutor(new ChestSearch());
        this.getCommand("search").setTabCompleter(new ChestSearch());

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic

        for (World w : Bukkit.getServer().getWorlds()){ //Deletes all leftover ChestMarkers
            for (Entity e : w.getEntities()){
                if (e.getName().equalsIgnoreCase("ChestMarker")){
                    e.remove();
                }
            }
        }
    }

    public String getMessageByKey(String key){
        return messagesConfig.getString(key);
    }

    public void sendMessage(Player target, String message){
        message = ChatColor.translateAlternateColorCodes('&', message);
        target.sendMessage(message);
    }
}
