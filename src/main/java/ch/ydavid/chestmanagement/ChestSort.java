package ch.ydavid.chestmanagement;

import net.md_5.bungee.api.chat.hover.content.Item;
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
import java.util.Arrays;
import java.util.List;

public class ChestSort implements CommandExecutor, TabCompleter {
    private final ChestManagement plugin;

    public ChestSort() {
        plugin = JavaPlugin.getPlugin(ChestManagement.class);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {

        return null;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {

        if (sender instanceof Player) {
            Player player = (Player) sender;
            Block target = player.getTargetBlock(null, 5);
            if (target.getState() instanceof InventoryHolder) {
                Inventory inv = ((InventoryHolder) target.getState()).getInventory();
                inv.setContents(sort(inv));
            }
            player.getInventory().setContents(
                    sort(((Player) sender).getInventory()));
        }
        return true;
    }

    private ItemStack[] sort(Inventory inv) {
        ItemStack[] content = inv.getContents();
        Arrays.sort(content, (item1, item2) -> {
            if (item1 == null && item2 == null) {
                return 0;
            }
            if (item1 == null) {
                return -1;
            }
            if (item2 == null) {
                return 1;
            }

            String item1Name = item1.getType().getKey().getKey();
            String item2Name = item2.getType().getKey().getKey();
            return item1Name.compareToIgnoreCase(item2Name);
        });
        return content;
    }


}
