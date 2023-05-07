package me.fritzpal.minecraftChess;

import me.fritzpal.minecraftChess.gameLogic.Vec2;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

public class Events implements Listener {
    private final Main plugin;

    public Events(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof ArmorStand) {
            if (e.getEntity().getCustomName() == null) return;
            if (e.getEntity().getCustomName().matches("[a-h][1-8] [0-9]*") && e.getDamager() instanceof Player) {
                Player p = (Player) e.getDamager();
                e.setCancelled(true);
                ActiveGame game = plugin.getActiveGame(Integer.parseInt(e.getEntity().getCustomName().split(" ")[1]));
                if (game == null) return;
                clickedTile(new Vec2(e.getEntity().getCustomName().split(" ")[0]), p, game);
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractAtEntityEvent e) {
        if (e.getRightClicked() instanceof ArmorStand) {
            if (e.getRightClicked().getCustomName() == null) return;
            if (e.getRightClicked().getCustomName().matches("[a-h][1-8] [0-9]*")) {
                e.setCancelled(true);
                ActiveGame game = plugin.getActiveGame(Integer.parseInt(e.getRightClicked().getCustomName().split(" ")[1]));
                if (game == null) return;
                clickedTile(new Vec2(e.getRightClicked().getCustomName().split(" ")[0]), e.getPlayer(), game);
            }
        }
    }

    private void clickedTile(Vec2 pos, Player p, ActiveGame game) {
        String uuid = p.getUniqueId().toString();
        if (plugin.cooldowns.containsKey(uuid)) {
            if (System.currentTimeMillis() - plugin.cooldowns.get(uuid) < 100) {
                return;
            }
        }
        plugin.cooldowns.put(uuid, System.currentTimeMillis());
        if (!game.isPlayersGame(p)) {
            p.sendMessage("§cThis is not your game!");
            return;
        }
        if (!game.isPlayersTurn(p)) {
            p.sendMessage("§cIt's not your turn!");
            return;
        }
        game.getState().onTileClick(pos);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR) {
            ItemStack item = e.getItem();
            if (item != null
                    && item.hasItemMeta()
                    && item.getItemMeta().hasDisplayName()
                    && item.getItemMeta().getDisplayName().equals(MenuInventory.gameMenuItem().getItemMeta().getDisplayName())) {
                p.performCommand("chess menu");
                e.setCancelled(true);
                return;
            }
        }

        Block block = e.getClickedBlock();
        if (block == null) return;

        for (ActiveGame game : plugin.getActiveGames()) {
            if (game.isInLocation(block.getLocation())) {
                Vec2 pos = new Vec2(block.getLocation().getBlockZ() - game.getLocation().getBlockZ(), 7 - (block.getLocation().getBlockX() - game.getLocation().getBlockX()));
                clickedTile(pos, p, game);
                e.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent e) {
        for (ActiveGame game : plugin.getActiveGames()) {
            if (game.isPlayersGame(e.getPlayer())) {
                game.endGame();
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        for (ActiveGame game : plugin.getActiveGames()) {
            if (game.isPlayersGame(e.getPlayer())) {
                game.endGame();
            }
        }
    }

    @EventHandler
    public void onInvClick(InventoryClickEvent event) {
        Player p = (Player) event.getWhoClicked();
        if (event.getClickedInventory() != null && event.getClickedInventory().getHolder() instanceof MenuInventory) {
            event.setCancelled(true);
            ItemStack item = event.getCurrentItem();
            if (item == null) return;
            if (!item.hasItemMeta()) return;
            if (!item.getItemMeta().hasDisplayName()) return;
            switch (item.getItemMeta().getDisplayName()) {
                case "§c§lResign":
                    //TODO
                    break;
                case "§aOffer Draw":
                    //TODO
                    break;
                    //TODO
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        e.getPlayer().getInventory().remove(MenuInventory.gameMenuItem());
    }
}
