package ch.ydavid.chestmanagement;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.EulerAngle;

import java.util.ArrayList;
import java.util.List;

public class ChestSearch implements CommandExecutor, TabCompleter {
    private final ChestManagement plugin;

    public ChestSearch() {
        plugin = JavaPlugin.getPlugin(ChestManagement.class);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {
        if (args.length == 1) {

            return getItemList(args[0]);
        }
        return null;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (cmd.getName().equalsIgnoreCase("search") && sender instanceof Player) {
            Player player = ((Player) sender).getPlayer();

            chestSearch(player, args);
        }

        return true;
    }


    private List<String> getItemList(String search) {
        search = search.toLowerCase();
        List<String> items = new ArrayList<>();
        if (Material.matchMaterial(search) != null) {
            NamespacedKey key = Material.matchMaterial(search).getKey();
            String fullItemName = key.getNamespace() + ":" + key.getKey();

            items.add(fullItemName);
            return items;
        }

        for (Material material : Material.values()) {
            String fullItemName = material.getKey().getNamespace() + ":" + material.getKey().getKey();
            if (fullItemName.contains(search)) {
                items.add(fullItemName);
            }
        }
        return items;
    }

    private void chestSearch(Player player, String[] args) {
        int radius = plugin.getConfig().getInt("default-radius");

        if (args.length == 0) { // Search for Item in Players Hand
            ItemStack item = player.getInventory().getItemInMainHand();

            if (item.getData().getItemType().isAir()) {
                String message = plugin.getMessageByKey("emptyhand-message");
                plugin.sendMessage(player, message);
                return;
            }
            scanArea(player, item.getType(), radius);
            return;
        }
        if (args.length == 2) { // Changes Radius if second param is a integer
            try {
                radius = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                String message = plugin.getMessageByKey("parse-error");
                message = message.replace("%text%", args[1]);
                plugin.sendMessage(player, message);
            }
        }

        // Search for the Item given as Parameter
        List<String> itemList = getItemList(args[0]);
        if (itemList.size() == 0) {
            String message = plugin.getMessageByKey("usage-message");
            plugin.sendMessage(player, message);
            return;
        }
        Material item = Material.matchMaterial(itemList.get(0));
        scanArea(player, item, radius);


    }

    private void scanArea(Player player, Material item, int radius) {

        String message = plugin.getMessageByKey("search-message");
        message = message.replace("%item%", item.name());
        plugin.sendMessage(player, message);

        int foundItems = 0;

        Location playerLocation = player.getLocation();
        World world = player.getWorld();
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    Location blockLocation = playerLocation.clone().add(x, y, z);
                    Block block = world.getBlockAt(blockLocation);

                    if (block.getState() instanceof InventoryHolder) {
                        InventoryHolder inventoryHolder = (InventoryHolder) block.getState();
                        if (inventoryHolder.getInventory().contains(item)) {
                            foundItems += countItems(inventoryHolder.getInventory(), item);
                            spawnMarker(block.getLocation());
                        }
                    }
                }
            }
        }

        message = plugin.getMessageByKey("found-message");
        message = message.replace("%item%", item.name());
        message = message.replace("%count%", String.valueOf(foundItems));
        plugin.sendMessage(player, message);
    }

    private int countItems(Inventory inv, Material item) {
        int i = 0;
        for (ItemStack is : inv.getContents()) {
            if (is != null && is.getType().equals(item)) {
                i += is.getAmount();
            }
        }
        return i;
    }

    private ArmorStand spawnMarker(Location loc) {
        EulerAngle angle = new EulerAngle(Math.toRadians(180), 0, 0);

        ArmorStand stand = (ArmorStand) loc.getWorld().spawnEntity(loc.add(0.5, 0, 0.5), EntityType.ARMOR_STAND);
        stand.setCustomName("ChestMarker");
        stand.setGravity(false);
        stand.setGlowing(true);
        stand.setSmall(true);
        stand.setBasePlate(false);
        stand.setRightLegPose(angle);
        stand.setLeftLegPose(angle);
        stand.setHeadPose(angle);
        stand.setMarker(true);
        ItemStack stack = new ItemStack(Material.PLAYER_HEAD, 1);
        stand.setHelmet(stack);

        Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();
        Team team = board.getTeam("blueTeam");

        team.addEntry(stand.getUniqueId().toString());

        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            stand.remove();
        }, 10 * 20);
        return stand;
    }
}
