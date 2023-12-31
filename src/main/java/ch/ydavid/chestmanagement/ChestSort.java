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
        Arrays.sort(content, (item1, item2) -> { // Sort by Item Name
            if (item1 == null && item2 == null) {
                return 0;
            }
            if (item1 == null) {
                return 1;
            }
            if (item2 == null) {
                return -1;
            }
            String item1Name = item1.getType().getKey().getKey();
            String item2Name = item2.getType().getKey().getKey();
            return item2Name.compareToIgnoreCase(item1Name);
        });

        for (int i = 0; i < content.length - 1; i++) { //Stack items together
            ItemStack currentItem = content[i];
            if (currentItem == null) {
                continue;
            }

            for (int j = i + 1; j < content.length; j++) {
                ItemStack nextItem = content[j];
                if (nextItem == null) {
                    continue;
                }

                if (currentItem.isSimilar(nextItem) && currentItem.getAmount() + nextItem.getAmount() <= currentItem.getMaxStackSize()) {
                    currentItem.setAmount(currentItem.getAmount() + nextItem.getAmount());
                    content[j] = null;
                }
            }
        }
        // Remove Empty Slots between the items
        for (int i = 0; i < content.length; i++) {
            if (content[i] == null) {
                for (int j = i + 1; j < content.length; j++) {
                    if (content[j] != null) {
                        content[i] = content[j];
                        content[j] = null;
                    }
                }
                break;
            }
        }

        return content;
    }


}
