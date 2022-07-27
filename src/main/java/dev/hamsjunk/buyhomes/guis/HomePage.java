package dev.hamsjunk.buyhomes.guis;

import dev.hamsjunk.buyhomes.events.HomePageEvent;
import dev.hamsjunk.buyhomes.misc.PlayerHomes;
import dev.hamsjunk.buyhomes.events.SetHomeEvent;
import dev.hamsjunk.buyhomes.misc.TakenIndexes;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
        inv.setItem(8, createGuiItem(Material.PAPER,  ChatColor.GREEN + "New home", "§7Creates a new home at your current location!"));
        inv.setItem(17, createGuiItem(Material.NAME_TAG, ChatColor.YELLOW + "Rename home", "§7Rename a home from your list of homes!"));
        inv.setItem(26, createGuiItem(Material.COMPASS, ChatColor.LIGHT_PURPLE + "Relocate home", "§7Changes a location of a home to your current location!"));
        inv.setItem(35, createGuiItem(Material.ITEM_FRAME, ChatColor.AQUA + "Change block", "§7Changes a home's item in the menu!"));
        inv.setItem(44, createGuiItem(Material.SHEARS, ChatColor.GOLD + "Delete home", "§7Removes a home from your list!", "§7This does not forfeit any money you put in, as it provides you with a free token for later"));
        inv.setItem(53, createGuiItem(Material.BARRIER, ChatColor.RED + "Close menu"));
        inv.setItem(7, createGuiItem(Material.BLACK_STAINED_GLASS_PANE, "§r"));
        inv.setItem(16, createGuiItem(Material.BLACK_STAINED_GLASS_PANE, "§r"));
        inv.setItem(25, createGuiItem(Material.BLACK_STAINED_GLASS_PANE, "§r"));
        inv.setItem(34, createGuiItem(Material.BLACK_STAINED_GLASS_PANE, "§r"));
        inv.setItem(43, createGuiItem(Material.BLACK_STAINED_GLASS_PANE, "§r"));
        inv.setItem(52, createGuiItem(Material.BLACK_STAINED_GLASS_PANE, "§r"));

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
    public void openGuiEvent(HomePageEvent p) throws URISyntaxException {
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
            case 8:
                Bukkit.getServer().getPluginManager().callEvent(new SetHomeEvent(p));

                return;

            case 53:
                p.closeInventory();

                return;

            case 7:
            case 16:
            case 25:
            case 34:
            case 43:
            case 52: return;

            default:
                FileConfiguration homesData = PlayerHomes.get();
                List<String> homes = homesData.getStringList("Homes." + p.getUniqueId() + "._homes");
                List<Integer> takenIndexes = homesData.getIntegerList("Homes." + p.getUniqueId() + "._takenIndexes");

                if (!takenIndexes.contains(e.getSlot())) {
                    return;
                }

                String home = homes.get(e.getSlot());

                String world = homesData.getString("Homes." + p.getUniqueId() + "." + home + ".coords.world");
                double x = homesData.getDouble("Homes." + p.getUniqueId() + "." + home + ".coords.x");
                double y = homesData.getDouble("Homes." + p.getUniqueId() + "." + home + ".coords.y");
                double z = homesData.getDouble("Homes." + p.getUniqueId() + "." + home + ".coords.z");
                float yaw = (float) homesData.getDouble("Homes." + p.getUniqueId() + "." + home + ".coords.yaw");
                float pitch = (float) homesData.getDouble("Homes." + p.getUniqueId() + "." + home + ".coords.pitch");

                p.teleport(new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch));
                p.sendMessage(ChatColor.GREEN + "Teleported you to " + ChatColor.WHITE + homesData.getString("Homes." + p.getUniqueId() + "." + home + ".name"));
        }
    }
}

