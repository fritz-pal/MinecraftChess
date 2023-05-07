package me.fritzpal.minecraftChess;

import me.fritzpal.minecraftChess.gameLogic.Board;
import me.fritzpal.minecraftChess.gameLogic.Move;
import me.fritzpal.minecraftChess.gameLogic.Piece;
import me.fritzpal.minecraftChess.gameLogic.Vec2;
import me.fritzpal.minecraftChess.stateMachine.GameEndedState;
import me.fritzpal.minecraftChess.stateMachine.State;
import me.fritzpal.minecraftChess.stateMachine.TurnState;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class ActiveGame {
    private final Location location;
    private final Main plugin;
    private final Board mainBoard;
    private final Player white;
    private final Player black;
    private final List<Tile> tiles = new ArrayList<>();
    private final int id;
    private State state;
    private Vec2 selectedTile;

    public ActiveGame(Main plugin, Location location, Player white, Player black) {
        this.plugin = plugin;
        this.id = plugin.getHighestId() + 1;
        plugin.setHighestId(id);
        this.white = white;
        this.black = black;
        this.location = location;
        this.mainBoard = new Board(this, "");
        placeTiles();
        this.state = new TurnState(this);
        white.getInventory().addItem(MenuInventory.gameMenuItem());
        white.sendMessage("§aGame started. You are playing as §f§lWhite§a!");
        black.getInventory().addItem(MenuInventory.gameMenuItem());
        black.sendMessage("§aGame started. You are playing as §0§lBlack§a!");
    }

    private void placeTiles() {
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                Vec2 current = new Vec2(x, y);
                Piece piece = mainBoard.getPiece(current);
                tiles.add(new Tile(piece, current, this));
            }
        }
    }

    public boolean isInLocation(Location loc) {
        if (loc.getY() != location.getY()) return false;
        if (location.getX() > loc.getX() || location.getX() + 7 < loc.getX()) return false;
        return !(location.getZ() > loc.getZ()) && !(location.getZ() + 7 < loc.getZ());
    }

    public Location getLocation() {
        return location;
    }

    public Board getMainBoard() {
        return mainBoard;
    }

    public void changeState(State state) {
        this.state = state;
        update();
    }

    public void update() {
        List<Vec2> legalMoves = mainBoard.getAllLegalMoves(selectedTile);

        Vec2 checkPos = null;
        if (mainBoard.isWhiteTurn() && mainBoard.isInCheck(true)) checkPos = mainBoard.getKingPos(true);
        else if (!mainBoard.isWhiteTurn() && mainBoard.isInCheck(false)) checkPos = mainBoard.getKingPos(false);

        for (Tile t : tiles) {
            t.update(t.getPos().equals(checkPos), legalMoves.contains(t.getPos()));
        }
    }

    public boolean isPlayersGame(Player p) {
        return p.equals(white) || p.equals(black);
    }

    public boolean isPlayersTurn(Player p) {
        if (mainBoard.isWhiteTurn()) return p.equals(white);
        else return p.equals(black);
    }

    public Vec2 getSelectedTile() {
        return selectedTile;
    }

    public void setSelectedTile(Vec2 pos) {
        if (pos != null) playSound();
        this.selectedTile = pos;
    }

    public void endGame() {
        selectedTile = null;
        changeState(new GameEndedState());

        sendEndMsg(white);
        sendEndMsg(black);

        spawnFireworks();

        new BukkitRunnable() {
            @Override
            public void run() {
                for (Tile t : tiles) {
                    t.setChessPattern();
                    t.remove();
                }
                white.getInventory().remove(MenuInventory.gameMenuItem());
                black.getInventory().remove(MenuInventory.gameMenuItem());
                plugin.removeGame(ActiveGame.this);
            }
        }.runTaskLater(plugin, 20 * 5);
    }

    private void sendEndMsg(Player p) {
        if (p.isOnline()) {
            p.sendMessage("§a§l" + mainBoard.getResult().toString());
            BaseComponent[] message = new ComponentBuilder()
                    .append("§4§lPGN")
                    .event(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, mainBoard.getPgn()))
                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7Click to copy!").create()))
                    .create();
            p.spigot().sendMessage(message);
        }
    }

    public void sendPromotionOption(Vec2 pos) {
        BaseComponent[] msg = new ComponentBuilder()
                .append(new ComponentBuilder()
                        .append("§6§lQueen")
                        .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7Click to promote!").create()))
                        .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/chess promote queen " + pos.getName())).create())
                .append("§r§a│ ")
                .append(new ComponentBuilder()
                        .append("§6§lRook")
                        .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7Click to promote!").create()))
                        .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/chess promote rook " + pos.getName())).create())
                .append("§r§a│ ")
                .append(new ComponentBuilder()
                        .append("§6§lBishop")
                        .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7Click to promote!").create()))
                        .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/chess promote bishop " + pos.getName())).create())
                .append("§r§a│ ")
                .append(new ComponentBuilder()
                        .append("§6§lKnight")
                        .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7Click to promote!").create()))
                        .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/chess promote knight " + pos.getName())).create())
                .create();

        if (mainBoard.isWhiteTurn()) {
            white.spigot().sendMessage(msg);
            white.playSound(white.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1f, 1f);
        } else {
            black.spigot().sendMessage(msg);
            black.playSound(white.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1f, 1f);

        }
    }

    public void playSound(Move move) {
        location.getWorld().playSound(location.clone().add(4, 0, 4), Sound.BLOCK_WOOD_BREAK, 1, 1);
        if (move.isPromotion())
            location.getWorld().playSound(location.clone().add(4, 0, 4), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);

        if (move.isCheck())
            location.getWorld().playSound(location.clone().add(4, 0, 4), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);

        if (move.isCastling())
            location.getWorld().playSound(location.clone().add(4, 0, 4), Sound.BLOCK_PISTON_EXTEND, 1, 1);

        if (move.isCapture())
            location.getWorld().playSound(location.clone().add(4, 0, 4), Sound.ENTITY_PLAYER_ATTACK_CRIT, 1, 1);
    }

    public void playSound() {
        location.getWorld().playSound(location.clone().add(4, 0, 4), Sound.BLOCK_DISPENSER_DISPENSE, 1, 1);
    }

    private void spawnFireworks() {
        for (int i = 0; i < 10; i++) {
            Firework fw = (Firework) location.getWorld().spawnEntity(location.clone().add(Math.random() * 8 + 0.5f, 1, Math.random() * 8 + 0.5f), EntityType.FIREWORK);
            FireworkMeta meta = fw.getFireworkMeta();
            meta.addEffect(FireworkEffect.builder()
                    .withColor(Color.fromRGB(Math.random() < 0.5 ? 0 : 255, Math.random() < 0.5 ? 0 : 255, Math.random() < 0.5 ? 0 : 255))
                    .with(FireworkEffect.Type.BALL_LARGE)
                    .withFlicker()
                    .withFade(Color.LIME)
                    .build()
            );
            meta.setPower(1);
            fw.setFireworkMeta(meta);
        }
    }

    public int getId() {
        return id;
    }

    public State getState() {
        return state;
    }

    public String getName(boolean white) {
        if (white) return this.white.getName();
        else return this.black.getName();
    }
}
