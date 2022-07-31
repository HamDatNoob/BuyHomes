package dev.hamsjunk.buyhomes.guis.selectors;

import dev.hamsjunk.buyhomes.events.ChangeBlockEvent;
import dev.hamsjunk.buyhomes.events.HomePageEvent;
import dev.hamsjunk.buyhomes.events.selectors.ChangeBlockSelectorEvent;
import dev.hamsjunk.buyhomes.misc.PlayerHomes;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

public class ChangeBlockSelector implements Listener {
    private Inventory inv;

    public void openNewGui(Player p) throws URISyntaxException {
        inv = Bukkit.createInventory(null, 54, "Select a home to change the block of");

        initializeItems(p);

        p.openInventory(inv);
    }

    public void initializeItems(Player p) throws URISyntaxException {
        // static items
        inv.setItem(48, createGuiItem(Material.ITEM_FRAME, ChatColor.AQUA + "Change block", "§7Changes a home's item in the menu!"));
        inv.setItem(51, createGuiItem(Material.ARROW, ChatColor.GRAY + "Previous page"));
        inv.setItem(52, createGuiItem(Material.BARRIER, ChatColor.RED + "Cancel"));
        inv.setItem(53, createGuiItem(Material.ARROW, ChatColor.GRAY + "Next page"));
        inv.setItem(36, createGuiItem(Material.BLACK_STAINED_GLASS_PANE, "§r"));
        inv.setItem(37, createGuiItem(Material.BLACK_STAINED_GLASS_PANE, "§r"));
        inv.setItem(38, createGuiItem(Material.BLACK_STAINED_GLASS_PANE, "§r"));
        inv.setItem(39, createGuiItem(Material.BLACK_STAINED_GLASS_PANE, "§r"));
        inv.setItem(40, createGuiItem(Material.BLACK_STAINED_GLASS_PANE, "§r"));
        inv.setItem(41, createGuiItem(Material.BLACK_STAINED_GLASS_PANE, "§r"));
        inv.setItem(42, createGuiItem(Material.BLACK_STAINED_GLASS_PANE, "§r"));
        inv.setItem(43, createGuiItem(Material.BLACK_STAINED_GLASS_PANE, "§r"));
        inv.setItem(44, createGuiItem(Material.BLACK_STAINED_GLASS_PANE, "§r"));

        // find homes from config.yml
        FileConfiguration homesData = PlayerHomes.get();
        List<String> homes = homesData.getStringList("Homes." + p.getUniqueId() + "._homes");

        // set the homes for the player
        for (String home : homes) {
            if (String.valueOf(home.charAt(0)).equals("_")) {
                continue;
            }

            int index = homesData.getInt("Homes." + p.getUniqueId() + "." + home + ".index");
            String item = homesData.getString("Homes." + p.getUniqueId() + "." + home + ".item");
            String name = homesData.getString("Homes." + p.getUniqueId() + "." + home + ".name");

            inv.setItem(index, createGuiItem(Material.valueOf(item), ChatColor.WHITE + name));
        }
    }

    protected ItemStack createGuiItem(final Material material, final String name, final String... lore) {
        final ItemStack item = new ItemStack(material, 1);
        final ItemMeta meta = item.getItemMeta();

        assert meta != null;
        meta.setDisplayName(name); // set a display name
        meta.setLore(Arrays.asList(lore)); // set the item lore

        item.setItemMeta(meta); // set the metadata

        return item;
    }

    @EventHandler
    public void openGuiEvent(ChangeBlockSelectorEvent p) throws URISyntaxException {
        PlayerHomes.get().set("Homes." + p.getPlayer().getUniqueId() + ".__name", p.getPlayer().getName());
        PlayerHomes.save();

        openNewGui(p.getPlayer());
    }

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent e) {
        if (!e.getInventory().equals(inv)) return;

        e.setCancelled(true); // prevents moving of items in the inventories
        final Player p = (Player) e.getWhoClicked(); // get the player who clicked

        switch (e.getSlot()) {
            case 52:
                Bukkit.getServer().getPluginManager().callEvent(new HomePageEvent(p));
                p.sendMessage(ChatColor.RED + "Canceled changing of the block for the home!");

                return;

            case 36:
            case 37:
            case 38:
            case 39:
            case 40:
            case 41:
            case 42:
            case 43:
            case 44:
            case 50: return;

            default:
                if (e.getCurrentItem().getType() == Material.AIR) return;

                List<String> homes = PlayerHomes.get().getStringList("Homes." + p.getUniqueId() + "._homes");
                PlayerHomes.get().set("Homes." + p.getUniqueId() + "." + homes.get(e.getSlot()) + ".flags.blockChanging", true);

                PlayerHomes.save();

                Bukkit.getServer().getPluginManager().callEvent(new ChangeBlockEvent(p));
        }
    }
}
