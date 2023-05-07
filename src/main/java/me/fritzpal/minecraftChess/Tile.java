package me.fritzpal.minecraftChess;

import me.fritzpal.minecraftChess.gameLogic.Piece;
import me.fritzpal.minecraftChess.gameLogic.Vec2;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;


public class Tile {
    private final Vec2 pos;
    private final ActiveGame game;
    Location loc;
    private ArmorStand armorStand;
    private Piece piece;
    private boolean inCheck = false;
    private boolean isLegalMove = false;
    private int taskID = -1;

    public Tile(Piece piece, Vec2 pos, ActiveGame game) {
        this.pos = pos;
        this.piece = piece;
        this.game = game;
        this.loc = game.getLocation().clone().add(7 - pos.getY() + 0.5f, 0, pos.getX() + 0.5f);

        setTileBlock();
        makeArmorStand();
        particles();
    }

    public Vec2 getPos() {
        return pos;
    }

    private void setTileBlock() {
        if (inCheck) {
            loc.getBlock().setType(Material.RED_CONCRETE);
        } else {
            setChessPattern();
        }
    }

    public void setChessPattern() {
        if ((pos.getX() + pos.getY()) % 2 == 0) {
            if (loc.getBlock().getType() != Material.DARK_OAK_PLANKS) loc.getBlock().setType(Material.DARK_OAK_PLANKS);
        } else {
            if (loc.getBlock().getType() != Material.BIRCH_PLANKS) loc.getBlock().setType(Material.BIRCH_PLANKS);
        }
    }

    private void particles() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (inCheck) {
                    armorStand.getWorld().spawnParticle(Particle.PORTAL, loc.clone().add(0f, 1.5f, 0f), 30, 0.2f, 0.2f, 0.2f, 0.2f);
                }
                if (isLegalMove) {
                    armorStand.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, loc.clone().add(0f, 1.2f, 0f), 5, 0.1f, 0.1f, 0.1f, 0f);
                }
                taskID = this.getTaskId();
            }
        }.runTaskTimer(Main.getPlugin(Main.class), 0, 20);
    }

    private void makeArmorStand() {
        armorStand = (ArmorStand) game.getLocation().getWorld().spawnEntity(loc.clone().add(0f, -0.35f, 0f), EntityType.ARMOR_STAND);
        armorStand.setGravity(false);
        armorStand.setInvisible(true);
        armorStand.setCustomName(pos.getName() + " " + game.getId());
        armorStand.setCustomNameVisible(false);
        armorStand.setHelmet(getTexture());
    }

    private ItemStack getTexture() {
        if (piece == null) return null;
        ItemStack copper = new ItemStack(Material.FLINT);
        ItemMeta meta = copper.getItemMeta();
        meta.setCustomModelData(piece.type().getCustomModelData(piece.isWhite()));
        copper.setItemMeta(meta);
        return copper;
    }

    public void setPiece(Piece piece) {
        this.piece = piece;
        armorStand.setHelmet(getTexture());
    }

    public void update(boolean inCheck, boolean legalMove) {
        if (armorStand.isDead()) {
            makeArmorStand();
        }
        this.isLegalMove = legalMove;
        this.inCheck = inCheck;
        setTileBlock();
        setPiece(game.getMainBoard().getPiece(pos));
        if (pos.equals(game.getSelectedTile()))
            armorStand.teleport(loc.clone().add(0f, -0.2f, 0f));
        else {
            if (armorStand.getLocation().getY() != loc.getY() - 0.35f)
                armorStand.teleport(loc.clone().add(0f, -0.35f, 0f));
        }
    }

    public void remove() {
        armorStand.remove();
        if (taskID != -1) {
            Bukkit.getScheduler().cancelTask(taskID);
        }
    }
}
