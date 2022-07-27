package dev.hamsjunk.buyhomes.guis;

import dev.hamsjunk.buyhomes.Main;
import dev.hamsjunk.buyhomes.events.SetHomeEvent;
import dev.hamsjunk.buyhomes.misc.PlayerHomes;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
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
import java.util.concurrent.atomic.AtomicBoolean;

public class SetHome implements Listener {
    private Inventory inv;

    public void openNewGui(Player p) throws URISyntaxException {
        inv = Bukkit.createInventory(null, 27, "Create a new home? This will cost you ");

        initializeItems(p);

        p.openInventory(inv);
    }

    public void initializeItems(Player p) throws URISyntaxException {
        // static items
        inv.setItem(11, createGuiItem(Material.RED_CONCRETE, ChatColor.RED + "Cancel"));
        inv.setItem(15, createGuiItem(Material.LIME_CONCRETE, ChatColor.GREEN + "Confirm"));
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
    public void openGuiEvent(SetHomeEvent p) throws URISyntaxException {
        openNewGui(p.getPlayer());
    }

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent e) {
        if (!e.getInventory().equals(inv)) return;

        e.setCancelled(true); // prevents moving of items in the inventories
        final Player p = (Player) e.getWhoClicked(); // get the player who clicked

        AtomicBoolean prematureClose = new AtomicBoolean(true);

        if (e.getSlot() == 11) {
            p.closeInventory();
            p.sendMessage(ChatColor.RED + "Canceled creation of a new home!");
        } else if (e.getSlot() == 15) {
            new AnvilGUI.Builder()
                .onClose(player -> {
                    if (prematureClose.get()) p.sendMessage(ChatColor.RED + "Canceled creation of a new home!");
                }).onComplete((player, text) -> {
                    if (String.valueOf(text.charAt(0)).equals("_")) {
                        p.sendMessage(ChatColor.RED + "Home names cannot start with \"_\"!");
                        return AnvilGUI.Response.close();
                    }

                    // get lists
                    List<String> homes = PlayerHomes.get().getStringList("Homes." + p.getUniqueId() + "._homes");
                    List<Integer> ti = PlayerHomes.get().getIntegerList("Homes." + p.getUniqueId() + "._takenIndexes");

                    if (homes.contains(text)) {
                        p.sendMessage(ChatColor.RED + "Home names cannot be duplicates!");
                        return AnvilGUI.Response.close();
                    }

                    // make the new home's index
                    Integer newIndex = 0;
                    while (ti.contains(newIndex)) {
                        newIndex++;
                    }

                    // reassign the lists
                    homes.add(text);
                    ti.add(newIndex);

                    if (newIndex % 9 == 6) {
                        homes.add("_DONT REMOVE");
                        homes.add("_DONT REMOVE");
                        ti.add(newIndex + 1);
                        ti.add(newIndex + 2);
                    }

                    // reassign the values
                    PlayerHomes.get().set("Homes." + p.getUniqueId() + "._homes", homes);
                    PlayerHomes.get().set("Homes." + p.getUniqueId() + "._takenIndexes", ti);
                    PlayerHomes.get().set("Homes." + p.getUniqueId() + "." + text + ".name", text);
                    PlayerHomes.get().set("Homes." + p.getUniqueId() + "." + text + ".index", newIndex);
                    PlayerHomes.get().set("Homes." + p.getUniqueId() + "." + text + ".item", "PAPER");
                    PlayerHomes.get().set("Homes." + p.getUniqueId() + "." + text + ".coords.world", p.getWorld().getName());
                    PlayerHomes.get().set("Homes." + p.getUniqueId() + "." + text + ".coords.x", p.getLocation().getX());
                    PlayerHomes.get().set("Homes." + p.getUniqueId() + "." + text + ".coords.y", p.getLocation().getY());
                    PlayerHomes.get().set("Homes." + p.getUniqueId() + "." + text + ".coords.z", p.getLocation().getZ());
                    PlayerHomes.get().set("Homes." + p.getUniqueId() + "." + text + ".coords.yaw", p.getLocation().getYaw());
                    PlayerHomes.get().set("Homes." + p.getUniqueId() + "." + text + ".coords.pitch", p.getLocation().getPitch());

                    PlayerHomes.save();

                    p.sendMessage(ChatColor.GREEN + "Successfully created the home " + ChatColor.WHITE + text);

                    prematureClose.set(false);
                    return AnvilGUI.Response.close();
                }).text("").title("Input a name").plugin(Main.getPlugin()).open(p);
        }
    }
}
