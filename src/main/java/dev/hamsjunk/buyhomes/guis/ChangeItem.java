package dev.hamsjunk.buyhomes.guis;

import dev.hamsjunk.buyhomes.events.ChangeBlockEvent;
import dev.hamsjunk.buyhomes.misc.PlayerHomes;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

public class ChangeBlock implements Listener {
    private Inventory inv;

    public void openNewGui(Player p) throws URISyntaxException {
        inv = Bukkit.createInventory(null, 54, "Select the block");

        initializeItems(p);

        p.openInventory(inv);
    }

    public void initializeItems(Player p) throws URISyntaxException {
        // static items
        inv.setItem(48, createGuiItem(Material.ITEM_FRAME, ChatColor.AQUA + "Change block", "§7Changes a home's item in the menu!"));
        inv.setItem(52, createGuiItem(Material.BARRIER, ChatColor.RED + "Close menu"));
        inv.setItem(36, createGuiItem(Material.BLACK_STAINED_GLASS_PANE, "§r"));
        inv.setItem(37, createGuiItem(Material.BLACK_STAINED_GLASS_PANE, "§r"));
        inv.setItem(38, createGuiItem(Material.BLACK_STAINED_GLASS_PANE, "§r"));
        inv.setItem(39, createGuiItem(Material.BLACK_STAINED_GLASS_PANE, "§r"));
        inv.setItem(40, createGuiItem(Material.BLACK_STAINED_GLASS_PANE, "§r"));
        inv.setItem(41, createGuiItem(Material.BLACK_STAINED_GLASS_PANE, "§r"));
        inv.setItem(42, createGuiItem(Material.BLACK_STAINED_GLASS_PANE, "§r"));
        inv.setItem(43, createGuiItem(Material.BLACK_STAINED_GLASS_PANE, "§r"));
        inv.setItem(44, createGuiItem(Material.BLACK_STAINED_GLASS_PANE, "§r"));

        // items
        inv.setItem(0, createGuiItem(Material.PAPER, ChatColor.WHITE + "Paper"));
        inv.setItem(1, createGuiItem(Material.STONE, ChatColor.WHITE + "Stone"));
        inv.setItem(2, createGuiItem(Material.GRASS_BLOCK, ChatColor.WHITE + "Grass Block"));
        inv.setItem(3, createGuiItem(Material.OAK_SAPLING, ChatColor.WHITE + "Oak Sapling"));
        inv.setItem(4, createGuiItem(Material.APPLE, ChatColor.WHITE + "Apple"));
        inv.setItem(5, createGuiItem(Material.COOKED_BEEF, ChatColor.WHITE + "Cooked Beef"));
        inv.setItem(6, createGuiItem(Material.WHEAT, ChatColor.WHITE + "Wheat"));
        inv.setItem(7, createGuiItem(Material.HAY_BLOCK, ChatColor.WHITE + "Hay Block"));
        inv.setItem(8, createGuiItem(Material.SPAWNER, ChatColor.WHITE + "Spawner"));
        inv.setItem(9, createGuiItem(Material.IRON_SWORD, ChatColor.WHITE + "Iron Sword"));
        inv.setItem(10, createGuiItem(Material.IRON_PICKAXE, ChatColor.WHITE + "Iron Pickaxe"));
        inv.setItem(11, createGuiItem(Material.IRON_CHESTPLATE, ChatColor.WHITE + "Iron Chestplate"));
        inv.setItem(12, createGuiItem(Material.REDSTONE, ChatColor.WHITE + "Redstone"));
        inv.setItem(13, createGuiItem(Material.PISTON, ChatColor.WHITE + "Piston"));
        inv.setItem(14, createGuiItem(Material.DRAGON_HEAD, ChatColor.WHITE + "Dragon Head"));
        inv.setItem(15, createGuiItem(Material.END_PORTAL_FRAME, ChatColor.WHITE + "End Portal Frame"));
        inv.setItem(16, createGuiItem(Material.SHULKER_BOX, ChatColor.WHITE + "Shulker Box"));
        inv.setItem(17, createGuiItem(Material.ENDER_PEARL, ChatColor.WHITE + "Ender Pearl"));
        inv.setItem(18, createGuiItem(Material.OBSIDIAN, ChatColor.WHITE + "Obsidian"));
        inv.setItem(19, createGuiItem(Material.LAVA_BUCKET, ChatColor.WHITE + "Lava Bucket"));
        inv.setItem(20, createGuiItem(Material.WATER_BUCKET, ChatColor.WHITE + "Water Bucket"));
        inv.setItem(21, createGuiItem(Material.SNOWBALL, ChatColor.WHITE + "Snowball"));
        inv.setItem(22, createGuiItem(Material.IRON_BARS, ChatColor.WHITE + "Iron Bars"));
        inv.setItem(23, createGuiItem(Material.RAIL, ChatColor.WHITE + "Rail"));
        inv.setItem(24, createGuiItem(Material.MINECART, ChatColor.WHITE + "Minecart"));
        inv.setItem(25, createGuiItem(Material.ITEM_FRAME, ChatColor.WHITE + "Item Frame"));
        inv.setItem(26, createGuiItem(Material.PAINTING, ChatColor.WHITE + "Painting"));
        inv.setItem(27, createGuiItem(Material.CHEST, ChatColor.WHITE + "Chest"));
        inv.setItem(28, createGuiItem(Material.ENDER_CHEST, ChatColor.WHITE + "Ender Chest"));
        inv.setItem(29, createGuiItem(Material.BREWING_STAND, ChatColor.WHITE + "Brewing Stand"));
        inv.setItem(30, createGuiItem(Material.POTION, ChatColor.WHITE + "Potion"));
        inv.setItem(31, createGuiItem(Material.DIAMOND, ChatColor.WHITE + "Diamond"));
        inv.setItem(32, createGuiItem(Material.IRON_INGOT, ChatColor.WHITE + "Iron"));
        inv.setItem(33, createGuiItem(Material.OAK_WOOD, ChatColor.WHITE + "Oak Planks"));
        inv.setItem(34, createGuiItem(Material.OAK_LOG, ChatColor.WHITE + "Oak Log"));
        inv.setItem(35, createGuiItem(Material.ENCHANTING_TABLE, ChatColor.WHITE + "Enchanting Table"));
        inv.setItem(36, createGuiItem(Material.EXPERIENCE_BOTTLE, ChatColor.WHITE + "XP Bottle"));

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
    public void openGuiEvent(ChangeBlockEvent p) throws URISyntaxException {
        PlayerHomes.get().set("Homes." + p.getPlayer().getUniqueId() + ".__name", p.getPlayer().getName());
        PlayerHomes.save();

        openNewGui(p.getPlayer());
    }

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent e) {
        if (!e.getInventory().equals(inv)) return;
        if (e.getSlot() >= 37 && e.getSlot() <= 53) return;

        e.setCancelled(true); // prevents moving of items in the inventories
        final Player p = (Player) e.getWhoClicked(); // get the player who clicked

        List<String> homes = PlayerHomes.get().getStringList("Homes." + p.getUniqueId() + "._homes");

        String home = null;
        for (String h : homes) {
            if (PlayerHomes.get().getBoolean("Homes." + p.getUniqueId() + "." + h + ".flags.itemChanging")) {
                home = h;

                break;
            }
        }

        String item = e.getCurrentItem().getType().toString();

        PlayerHomes.get().set("Homes." + p.getUniqueId() + "." + home + ".flags.itemChanging", false);
        PlayerHomes.get().set("Homes." + p.getUniqueId() + "." + home + ".item", item);

        p.closeInventory();
        p.sendMessage(ChatColor.GREEN + "Successfully changed the item for " + ChatColor.WHITE + home + ChatColor.GREEN + " to " + ChatColor.WHITE + item + ChatColor.GREEN + "!");
    }

    @EventHandler
    public void onInventoryClose(final InventoryCloseEvent e) {
        if (!e.getInventory().equals(inv)) return;

        final Player p = (Player) e.getPlayer(); // get the player who clicked

        List<String> homes = PlayerHomes.get().getStringList("Homes." + p.getUniqueId() + "._homes");

        String home = null;
        for (String h : homes) {
            if (PlayerHomes.get().getBoolean("Homes." + p.getUniqueId() + "." + h + ".flags.itemChanging")) {
                home = h;

                break;
            }
        }

        if (home == null) return;

        p.sendMessage(ChatColor.RED + "Canceled changing of the block for the home!");
        PlayerHomes.get().set("Homes." + p.getUniqueId() + "." + home + ".flags.itemChanging", false);
    }
}