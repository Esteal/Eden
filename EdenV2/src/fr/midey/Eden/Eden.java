package fr.midey.Eden;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.elmakers.mine.bukkit.api.magic.MagicAPI;

public class Eden extends JavaPlugin {

    private GameManager gameManager;
    private PlayerListener playerListener;
    private static StatsOfClass statsOfClass;
    public static Eden MAIN;
    public static MagicAPI magicAPI;
    private int timeToSneakBeforeRegen;
    private Map<Player, PlayerData> playersData = new HashMap<>();
    public Map<String, List<Float>> playersModifiers = new HashMap<>();

    @Override
    public void onEnable() {
    	Bukkit.broadcastMessage("ayaaaaaaaa");
        magicAPI = (MagicAPI) getServer().getPluginManager().getPlugin("Magic");
        if (magicAPI == null) {
            getLogger().severe("Magic plugin not found! Disabling plugin.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        MAIN = this;
        gameManager = new GameManager(this);
        statsOfClass = new StatsOfClass(this);
        playerListener = new PlayerListener(gameManager, this);
        getServer().getPluginManager().registerEvents(playerListener, this);
        getCommand("model").setExecutor(new StartCommand(gameManager, this));
        getCommand("focusreload").setExecutor(new StartCommand(gameManager, this));
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Focus Plugin has been enabled!");
        
        playerListener.onReload();
        gameManager.startDirectionTask();
        loadConfig();
    	new BukkitRunnable() {

			@Override
			public void run() {
				for(Player player: Bukkit.getOnlinePlayers()) 
					if(player.getGameMode() != GameMode.CREATIVE) player.updateInventory();
			}
    		
    	}.runTaskTimer(this, 0, 20);
    }

    private void loadConfig() {
    	loadModifiers();
    	loadTimeToSneakBeforeRegen();
	}
    
    private void loadTimeToSneakBeforeRegen() {
        FileConfiguration config = getConfig();
		timeToSneakBeforeRegen = config.getInt("timeToSneakBeforeRegen");
	}

	private void loadModifiers() {
        FileConfiguration config = getConfig();
        playersModifiers.put("MOVEMENT_SPEED", getFloatList(config, "modifier.MOVEMENT_SPEED"));
        playersModifiers.put("CONSTITUTION", getFloatList(config, "modifier.CONSTITUTION"));
        playersModifiers.put("ATTACK_DAMAGE", getFloatList(config, "modifier.ATTACK_DAMAGE"));
        playersModifiers.put("ARMOR", getFloatList(config, "modifier.ARMOR"));
        playersModifiers.put("MANA", getFloatList(config, "modifier.MANA"));
    }

    private List<Float> getFloatList(FileConfiguration config, String path) {
        List<Double> doubleList = config.getDoubleList(path);
        return doubleList.stream().map(Double::floatValue).toList();
    }

	@Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Focus Plugin has been disabled.");
    }

    public GameManager getGameManager() { return gameManager; }

	public MagicAPI getMagicAPI() { return magicAPI; }

	public Map<Player, PlayerData> getPlayersData() { return playersData; }

	public void setPlayersData(Map<Player, PlayerData> playersData) { this.playersData = playersData; }
	
	public PlayerData getPlayerData(Player player) {
		playersData.put(player, playersData.getOrDefault(player, new PlayerData(player.getName())));
		return this.playersData.getOrDefault(player, new PlayerData(player.getName()));
	}

	public StatsOfClass getStatsOfClass() { return statsOfClass; }

	public static Map<String, List<Integer>> getClassStats() {
		return statsOfClass.getClassStats();
	}

	public int getTimeToSneakBeforeRegen() {
		return timeToSneakBeforeRegen;
	}
}
