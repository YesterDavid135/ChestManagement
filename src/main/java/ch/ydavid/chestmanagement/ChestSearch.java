package ch.ydavid.chestmanagement;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.EulerAngle;

public class ChestSearch  implements CommandExecutor {
    private Plugin plugin;

    public ChestSearch() {
        plugin = JavaPlugin.getPlugin(ChestManagement.class);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (cmd.getName().equalsIgnoreCase("search") && sender instanceof Player) {
            Player player = ((Player) sender).getPlayer();

            chestSearch(player, args);
        }

        return true;
    }

    private void chestSearch(Player player, String[] args) {


        if (args.length == 0) {
            ItemStack item = player.getInventory().getItemInMainHand();

            if (!item.getData().getItemType().isAir()) {
                scanArea(player, item.getType(), 10);
            }
        }


    }

    private void scanArea(Player player, Material item, int radius) {
        Location playerLocation = player.getLocation();
        World world = player.getWorld();
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    Location blockLocation = playerLocation.clone().add(x, y, z);
                    Block block = world.getBlockAt(blockLocation);

                    if (block.getState() instanceof InventoryHolder) {
                        InventoryHolder inventoryHolder = (InventoryHolder) block.getState();
                        if (inventoryHolder.getInventory().contains(item)){
                            spawnMarker(block.getLocation());
                        }
                    }
                }
            }
        }
    }

    private ArmorStand spawnMarker(Location loc) {
        EulerAngle angle = new EulerAngle(Math.toRadians(180), 0, 0);

        ArmorStand stand = (ArmorStand) loc.getWorld().spawnEntity(loc.add(0.5, 0, 0.5), EntityType.ARMOR_STAND);
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

        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            stand.remove();
        }, 10 * 20);
        return stand;
    }
}
