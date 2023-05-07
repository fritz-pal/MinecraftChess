package me.fritzpal.minecraftChess;

import org.bukkit.Bukkit;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main extends JavaPlugin {
    private final List<ActiveGame> activeGames = new ArrayList<>();
    private int highestId = -1;
    Map<String, Long> cooldowns = new HashMap<>();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        getCommand("chess").setExecutor(new Commands(this));
        getServer().getPluginManager().registerEvents(new Events(this), this);

        for(Entity e : Bukkit.getWorld("world").getEntities()) {
            if(e instanceof ArmorStand) {
                if(e.getCustomName() != null && e.getCustomName().matches("[a-h][1-8] [0-9]*")) {
                    e.remove();
                }
            }
        }

        getLogger().info("plugin enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("plugin disabled!");
    }

    public void addActiveGame(ActiveGame activeGame) {
        activeGames.add(activeGame);
    }

    public List<ActiveGame> getActiveGames() {
        return activeGames;
    }

    public int getHighestId() {
        return highestId;
    }

    public void setHighestId(int id) {
        highestId = id;
    }

    public ActiveGame getActiveGame(int id) {
        for (ActiveGame activeGame : activeGames) {
            if (activeGame.getId() == id) return activeGame;
        }
        return null;
    }

    public ActiveGame getActiveGame(Player p){
        for (ActiveGame activeGame : activeGames) {
            if (activeGame.isPlayersGame(p)) return activeGame;
        }
        return null;
    }

    public void removeGame(ActiveGame activeGame) {
        activeGames.remove(activeGame);
    }
}
