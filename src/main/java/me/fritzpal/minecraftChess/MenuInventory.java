package me.fritzpal.minecraftChess;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class MenuInventory implements InventoryHolder {
    private final Player p;
    private final Inventory inv;

    public MenuInventory(Player p) {
        this.p = p;

        inv = Bukkit.createInventory(this, 9, "Chess Menu");
        inv.setItem(1, resignItem());
        inv.setItem(8, exitItem());
        inv.setItem(0, offerDrawItem());
        inv.setItem(7, pgnItem());
        inv.setItem(6, fenItem());
    }

    private static ItemStack makeItem(Material material, boolean glow, String name, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(lore));
        if (glow) meta.addEnchant(Enchantment.DURABILITY, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack gameMenuItem() {
        return makeItem(Material.COOKIE, true, "§a§lChess Menu", "§7Right-click to open the chess menu.");
    }

    public ItemStack resignItem() {
        return makeItem(Material.RED_WOOL, false, "§c§lResign", "§7Click to resign.");
    }

    public ItemStack exitItem() {
        return makeItem(Material.BARRIER, false, "§cClose Game", "§7Click to reset the game.");
    }

    public ItemStack offerDrawItem() {
        return makeItem(Material.WHITE_WOOL, false, "§aOffer Draw", "§7Click to offer and accept a draw.");
    }

    public ItemStack pgnItem() {
        return makeItem(Material.BOOK, false, "§4§lExport PGN", "§7Click to get the PGN of the game.");
    }

    public ItemStack fenItem() {
        return makeItem(Material.PAPER, false, "§a§lExport FEN", "§7Click to get the FEN of the current position.", "§8This can be used to continue the game later.");
    }

    @Override
    public Inventory getInventory() {
        return inv;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof MenuInventory && ((MenuInventory) obj).p.equals(p) && ((MenuInventory) obj).inv.equals(inv);
    }
}
