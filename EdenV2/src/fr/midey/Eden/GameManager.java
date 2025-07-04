package fr.midey.Eden;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import com.elmakers.mine.bukkit.api.magic.MagicAPI;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class GameManager {
    private int requiredKills;
    private int requiredRounds;
    private int killPoints;
    private int roundsPoints;
    private int pointsAtTheStart;
    private final Random random = new Random();
    
    private final Eden plugin;
	
    public final Map<Player, Integer> playerKills = new HashMap<>();
    private final Map<Player, Integer> playerRoundsWon = new HashMap<>();
    
    private final Map<String, List<Location>> spawnPoint = new HashMap<>();
    private List<Location> currentSpawnPoint = new ArrayList<>();
    

    private GameState gameState = GameState.BEFORE_START;
    
    public GameManager(Eden plugin) {
        this.plugin = plugin;
        loadConfig();
        initializeSpawnPoints();
    }

    private void loadConfig() {
        plugin.saveDefaultConfig();
        FileConfiguration config = plugin.getConfig();
        requiredKills = config.getInt("requiredKills", 10);
        requiredRounds = config.getInt("requiredRounds", 5);
        killPoints = config.getInt("pointPerKill", 3);
        roundsPoints = config.getInt("pointPerRound", 10);
        pointsAtTheStart = config.getInt("pointsAtTheStart", 20);
    }

    private void initializeSpawnPoints() {
        FileConfiguration config = plugin.getConfig();
        World world = Bukkit.getWorld("world");

        for (String mapName : config.getConfigurationSection("maps").getKeys(false)) {
            List<Location> spawnLocations = new ArrayList<>();
            List<?> spawns = config.getList("maps." + mapName + ".spawnpoints");

            for (Object spawn : spawns) {
                @SuppressWarnings("unchecked")
				List<Integer> coords = (List<Integer>) spawn;
                spawnLocations.add(new Location(world, coords.get(0), coords.get(1), coords.get(2)));
            }

            spawnPoint.put(mapName, spawnLocations);
        }
    }
    
    public void startGame() {
        for(Player players: Bukkit.getOnlinePlayers()) {
        	giveShopItem(players);
        	players.playSound(players, Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 0.5f);
    		players.sendTitle("", ChatColor.WHITE + "Regarde le §6shop§f dans ton §einventaire !", 0, 40, 20);
    		int points = plugin.getPlayerData(players).getPoints() + pointsAtTheStart;
    		plugin.getPlayerData(players).setPoints(points);
    		updateTabList(players);
        }
        gameState = GameState.BETWEEN_ROUNDS;
    }

	public void startRound() {
        for (Player player : Bukkit.getOnlinePlayers()) {
        	player.playSound(player, Sound.ENTITY_ENDER_DRAGON_AMBIENT, 0.5f, 0.5f);
            player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
            giveShopItem(player);
            updateTabList(player);
        }
        sendMessagePlayers(ChatColor.WHITE + "Un nouveau round a "+ ChatColor.GOLD + "commencé");
        gameState = GameState.IN_ROUND;
        playerKills.clear();
        definePlayedMap();
    }

    protected void definePlayedMap() {
        String playedMap = selectRandomMap();
        currentSpawnPoint.clear();
        currentSpawnPoint = new ArrayList<>(spawnPoint.get(playedMap)); // Copie la liste des points de spawn
        List<Location> tempSpawnPoint = new ArrayList<>(currentSpawnPoint); // Utilise une copie pour le traitement temporaire
        Bukkit.broadcastMessage(ChatColor.GREEN + "Map choisie pour ce round: §e" + playedMap);
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(ChatColor.GREEN + "Téléportation effectuée avec succès");
            if (tempSpawnPoint.isEmpty()) {
            	tempSpawnPoint = new ArrayList<>(currentSpawnPoint);
                player.sendMessage(ChatColor.RED + "Erreur: Pas assez de points de spawn disponibles!");
            }
            int index = this.random.nextInt(tempSpawnPoint.size());
            Location teleportLocation = tempSpawnPoint.get(index);
            player.teleport(teleportLocation);
            tempSpawnPoint.remove(index);
            giveShopItem(player);
            updateScoreboard(player);
        }
    }


    private String selectRandomMap() {
        int index = this.random.nextInt(spawnPoint.size());
        return (String) spawnPoint.keySet().toArray()[index];
    }
    
	public void addKill(Player player) {
        if (gameState != GameState.IN_ROUND) return;

        int kills = playerKills.getOrDefault(player, 0) + 1;
        playerKills.put(player, kills);

        int points = plugin.getPlayerData(player).getPoints() + killPoints;
        plugin.getPlayerData(player).setPoints(points);

        updateScoreboard(player);
        updateTabList(player);

        if (kills >= requiredKills) {
        	
            sendMessagePlayers(player.getName() + " a gagné le round !");
            
            int roundPointWin = plugin.getPlayerData(player).getPoints() + roundsPoints/2;
            plugin.getPlayerData(player).setPoints(roundPointWin);

            int roundsWon = playerRoundsWon.getOrDefault(player, 0) + 1;
            playerRoundsWon.put(player, roundsWon);
            
            Player topKiller = playerKills.entrySet().stream().max(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse(null);
            int multiplier = playerRoundsWon.getOrDefault(topKiller, 0) / 10 + 1;
            
            for(Player players: Bukkit.getOnlinePlayers()) {
        		int point = plugin.getPlayerData(players).getPoints() + roundsPoints*multiplier;
        		plugin.getPlayerData(players).setPoints(point);
                players.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
        		Bukkit.getScheduler().runTaskLater(plugin, () -> {
        			players.teleport(new Location(players.getWorld(), 0 , -50, 0));
        		}, 20*4);
            }
            updateAllScoreboards();

            gameState = GameState.BETWEEN_ROUNDS;

            if (roundsWon >= requiredRounds) {
                sendMessagePlayers(player.getName() + " a gagné la partie !");

                endGame();
            } else {
                //startRound();
            }
        }
    }

    public void endGame() {
        gameState = GameState.GAME_OVER;
        new BukkitRunnable() {
            @Override
            public void run() {
                resetGame();
            }
        }.runTaskLater(plugin, 20 * 10); // 10 seconds delay before reset
    }

    public void resetGame() {
        playerKills.clear();
        playerRoundsWon.clear();
        plugin.getPlayersData().clear();

        for (Player player : Bukkit.getOnlinePlayers()) {
        	clearSpell(player);
            player.getInventory().clear();
    		player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                Bukkit.dispatchCommand(player, "kill @e");
            	player.getInventory().clear();
            	player.getInventory().setItem(16, getReadyItem());
    	        plugin.getPlayersData().replace(player, new PlayerData(player.getName()));
    	        plugin.getPlayerData(player).setStatu(false);
            	updateTabList(player);
            	player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard()); // Clear the scoreboard
            }, 5);
	        player.setGameMode(GameMode.ADVENTURE);
        }
        sendMessagePlayers("La partie a été réinitialisé.");
        gameState = GameState.BEFORE_START;
        
    }
    
    public void clearSpell(Player player) {
        for (ItemStack item : player.getInventory()) {
        	MagicAPI magicAPI = plugin.getMagicAPI();
            if (item != null && magicAPI.isWand(item)) {
                player.getInventory().setItemInMainHand(item);
                Bukkit.dispatchCommand(player, "wand configure spells []");
                Bukkit.dispatchCommand(player, "wand configure reset: true");
            }
        }
    }
    
    public void updateAllScoreboards() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            updateScoreboard(player);
    		updateTabList(player);
        }
    }

    public void updateScoreboard(Player player) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard board = manager.getNewScoreboard();

        @SuppressWarnings("deprecation")
        Objective objective = board.registerNewObjective("Focus", "dummy", ChatColor.GOLD + "Focus - RPG");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        Score killScore = objective.getScore(ChatColor.GREEN + "Kills: " + ChatColor.WHITE + playerKills.getOrDefault(player, 0) + "/" + requiredKills);
        killScore.setScore(4);

        Score roundWinScore = objective.getScore(ChatColor.BLUE + "Rounds Won: " + ChatColor.WHITE + playerRoundsWon.getOrDefault(player, 0) + "/" + requiredRounds);
        roundWinScore.setScore(3);

        Player topRoundWinner = playerRoundsWon.entrySet().stream().max(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse(null);
        Score topRoundWinnerScore = objective.getScore(ChatColor.RED + "Top Round Winner: " + ChatColor.WHITE + (topRoundWinner != null ? topRoundWinner.getName() : "N/A"));
        topRoundWinnerScore.setScore(2);

        Player topKiller = playerKills.entrySet().stream().max(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse(null);
        Score topKillerScore = objective.getScore(ChatColor.YELLOW + "Top Killer: " + ChatColor.WHITE + (topKiller != null ? topKiller.getName() : "N/A"));
        topKillerScore.setScore(1);

        player.setScoreboard(board);
    }

    public void updateTabList(Player player) {
        int points = plugin.getPlayerData(player).getPoints();
        int kills = playerKills.getOrDefault(player, 0);
        int rounds = playerRoundsWon.getOrDefault(player, 0);
        player.setPlayerListName(player.getName() + ChatColor.GOLD + " [" + points + "/" + kills + "/" + rounds + "]");
        player.setPlayerListHeaderFooter("§eFOCUS RPG", "§4Points/Kills/Rounds");
    }

    public GameState getGameState() {
        return this.gameState;
    }

    public void setGameState(GameState state) {
    	this.gameState = state;
    }
    private void giveShopItem(Player player) {
        ItemStack shopItem = new ItemStack(Material.NETHER_STAR);
        ItemMeta meta = shopItem.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.GOLD + "Shop");
            shopItem.setItemMeta(meta);
        }
        player.getInventory().setItem(17, shopItem); // Top right slot in inventory
    }
	
	public void teleportPlayerToRandomSpawn(Player player) {
	    if (currentSpawnPoint.isEmpty()) {
	        player.sendMessage(ChatColor.RED + "Erreur: Pas de points de spawn disponibles !");
	        return;
	    }
	    int index = random.nextInt(currentSpawnPoint.size());
	    Location spawnLocation = currentSpawnPoint.get(index);
	    player.teleport(spawnLocation);
	}
	
	
	public ItemStack getReadyItem() {
		ItemStack it = new ItemStack(Material.LIME_WOOL);
		ItemMeta im = it.getItemMeta();
		im.setDisplayName(ChatColor.GREEN + "Prêt");
		it.setItemMeta(im);
		return it;
	}

	public ItemStack getWaitingItem() {
		ItemStack it = new ItemStack(Material.RED_WOOL);
		ItemMeta im = it.getItemMeta();
		im.setDisplayName(ChatColor.RED + "Attente");
		it.setItemMeta(im);
		return it;
	}
	
	public void sendMessagePlayers(String subtitle) {
		for(Player players: Bukkit.getOnlinePlayers()) {
            players.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(subtitle));
		}
	}
	
	public void sendMessagePlayer(Player player, String subtitle) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(subtitle));
	}
	
    public void startDirectionTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                Player player = playerKills.entrySet().stream().max(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse(null);
                if (player != null && player.isOnline() && player.getGameMode() != GameMode.SPECTATOR && gameState == GameState.IN_ROUND) {
                    for (Player players : Bukkit.getOnlinePlayers()) {
                        if (!players.equals(player)) {
                            showDirectionArrow(players, player);
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 0, 20); // Run task every second
    }

    public void showDirectionArrow(Player player, Player target) {
        Location playerLoc = player.getLocation();
        Location targetLoc = target.getLocation();

        double dx = targetLoc.getX() - playerLoc.getX();
        double dz = targetLoc.getZ() - playerLoc.getZ();

        double angle = Math.toDegrees(Math.atan2(dz, dx)) - playerLoc.getYaw();
        angle = (angle + 360) % 360;

        String direction;
        if (angle >= 337.5 || angle < 22.5) {
            direction = "←";  // E
        } else if (angle >= 22.5 && angle < 67.5) {
            direction = "⬉";  // NE
        } else if (angle >= 67.5 && angle < 112.5) {
            direction = "↑";  // N
        } else if (angle >= 112.5 && angle < 157.5) {
            direction = "⬈";  // NW
        } else if (angle >= 157.5 && angle < 202.5) {
            direction = "→";  // W
        } else if (angle >= 202.5 && angle < 247.5) {
            direction = "⬋";  // SW
        } else if (angle >= 247.5 && angle < 292.5) {
            direction = "↓";  // S
        } else {
            direction = "⬊";  // SE
        }

        String message = ChatColor.YELLOW + target.getName() + " " + direction;
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
    }
	
	
	
	
	
	
	
	
	
	
	
	
}
