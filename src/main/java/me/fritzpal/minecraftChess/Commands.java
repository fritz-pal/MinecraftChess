package me.fritzpal.minecraftChess;

import me.fritzpal.minecraftChess.gameLogic.Board;
import me.fritzpal.minecraftChess.gameLogic.PieceType;
import me.fritzpal.minecraftChess.gameLogic.Result;
import me.fritzpal.minecraftChess.gameLogic.Vec2;
import me.fritzpal.minecraftChess.stateMachine.TurnState;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Commands implements CommandExecutor {
    Main plugin;

    public Commands(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (command.getName().equalsIgnoreCase("chess")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("Only players can perform that command.");
                return true;
            }
            Player p = (Player) sender;

            if (args.length > 0) {
                if (args[0].equals("menu")) {
                    if (plugin.getActiveGame(p) == null) {
                        p.sendMessage("§cYou are not in a game!");
                        p.getInventory().remove(MenuInventory.gameMenuItem());
                        return true;
                    }
                    p.openInventory(new MenuInventory(p).getInventory());
                    return true;
                }

                if(args[0].equals("acceptdraw")){
                    ActiveGame game = plugin.getActiveGame(p);
                    if(game == null) return true;
                    boolean isWhite = game.isWhite(p);
                    if(game.getMainBoard().hasOfferedDraw(!isWhite)){
                        game.getMainBoard().offerDraw(isWhite);
                    }
                    return true;
                }
            }

            if (args.length < 2) {
                p.sendMessage("§cUsage:" + command.getUsage());
                return true;
            }

            if (args[0].equals("promote")) {
                PieceType promotion = PieceType.fromString(args[1]);
                if (promotion == null) return true;
                if (args.length < 3) return true;
                if (!args[2].matches("[a-h][1-8]")) return true;
                Vec2 pos = new Vec2(args[2]);
                ActiveGame game = plugin.getActiveGame(p);
                if (game == null) return true;
                if (!game.isPlayersTurn(p)) return true;
                Board board = game.getMainBoard();
                if (!board.isLegalMove(game.getSelectedTile(), pos)) return true;
                board.move(game.getSelectedTile(), pos, true, promotion);
                if (board.getResult() != Result.NOTFINISHED) {
                    game.endGame();
                } else {
                    game.setSelectedTile(null);
                    game.changeState(new TurnState(game));
                }
                return true;
            }

            Player white = Bukkit.getPlayer(args[0]);
            Player black = Bukkit.getPlayer(args[1]);
            if (white == null) {
                p.sendMessage("§cPlayer " + args[0] + " not found!");
                return true;
            }
            if (black == null) {
                p.sendMessage("§cPlayer " + args[1] + " not found!");
                return true;
            }
            Location pos = p.getLocation().clone().add(0f, -1f, 0f).getBlock().getLocation();

            plugin.addActiveGame((new ActiveGame(plugin, pos, white, black)));
            return true;
        }
        return false;
    }
}
