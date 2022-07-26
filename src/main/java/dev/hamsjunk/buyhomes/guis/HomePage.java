package dev.hamsjunk.buyhomes.guis;

import dev.hamsjunk.buyhomes.events.Gui;
import dev.hamsjunk.buyhomes.misc.PlayerHomes;
import org.bukkit.Bukkit;
import org.bukkit.Location;
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

public class HomePage implements Listener {
    private Inventory inv;

    public void openNewGui(Player p) throws URISyntaxException {
        inv = Bukkit.createInventory(null, 54, "BuyHomes 1.0-DEV");

        initializeItems(p);

        p.openInventory(inv);
    }

    public void initializeItems(Player p) throws URISyntaxException {
        // static items
        inv.setItem(8, createGuiItem(Material.PAPER, "§aNew home", "§7Creates a new home at your current location!"));
        inv.setItem(17, createGuiItem(Material.NAME_TAG, "§eRename home", "§7Rename a home from your list of homes!"));
        inv.setItem(26, createGuiItem(Material.COMPASS, "§dRelocate home", "§7Changes a location of a home to your current location!"));
        inv.setItem(35, createGuiItem(Material.ITEM_FRAME, "§bChange block", "§7Changes a home's item in the menu!"));
        inv.setItem(44, createGuiItem(Material.SHEARS, "§6Delete home", "§7Removes a home from your list!", "§7This does not forfeit any money you put in, as it provides you with a free token for later"));
        inv.setItem(53, createGuiItem(Material.BARRIER, "§cClose menu"));
        inv.setItem(7, createGuiItem(Material.BLACK_STAINED_GLASS_PANE, "§r"));
        inv.setItem(16, createGuiItem(Material.BLACK_STAINED_GLASS_PANE, "§r"));
        inv.setItem(25, createGuiItem(Material.BLACK_STAINED_GLASS_PANE, "§r"));
        inv.setItem(34, createGuiItem(Material.BLACK_STAINED_GLASS_PANE, "§r"));
        inv.setItem(43, createGuiItem(Material.BLACK_STAINED_GLASS_PANE, "§r"));
        inv.setItem(52, createGuiItem(Material.BLACK_STAINED_GLASS_PANE, "§r"));

        // find homes from config.yml
        FileConfiguration homesData = PlayerHomes.get();
        List<String> homes = homesData.getStringList("Homes." + p.getName() + "._homes");

        // set the homes for the player
        for (String home : homes) {
            Integer index = homesData.getInt("Homes." + p.getName() + "." + home + ".index");
            String item = homesData.getString("Homes." + p.getName() + "." + home + ".item");
            String name = homesData.getString("Homes." + p.getName() + "." + home + ".name");

            inv.setItem(index, createGuiItem(Material.valueOf(item), "§8" + name));
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
    public void openGuiEvent(Gui p) throws URISyntaxException {
        openNewGui(p.getPlayer());
    }

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent e) {
        if (!e.getInventory().equals(inv)) return;

        e.setCancelled(true); // prevents moving of items in the inventories
        final ItemStack clickedItem = e.getCurrentItem(); // get the item that was clicked
        final Player p = (Player) e.getWhoClicked(); // get the player who clicked

        FileConfiguration homesData = PlayerHomes.get();
        List<String> homes = homesData.getStringList("Homes." + p.getName() + "._homes");

        for (String home : homes) {
            if (e.getSlot() != homesData.getInt("Homes." + p.getName() + home + ".index")) return;

            String world = homesData.getString("Homes." + p.getName() + "." + home + ".coords.world");
            double x = homesData.getDouble("Homes." + p.getName() + "." + home + ".coords.x");
            double y = homesData.getDouble("Homes." + p.getName() + "." + home + ".coords.y");
            double z = homesData.getDouble("Homes." + p.getName() + "." + home + ".coords.z");
            float yaw = (float) homesData.getDouble("Homes." + p.getName() + "." + home + ".coords.yaw");
            float pitch = (float) homesData.getDouble("Homes." + p.getName() + "." + home + ".coords.pitch");

            p.teleport(new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch));
        }
    }
}
