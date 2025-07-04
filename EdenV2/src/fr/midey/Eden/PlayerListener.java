package fr.midey.Eden;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class PlayerListener implements Listener {
    private final GameManager gameManager;
    private final List<GameState> exceptRound = List.of(GameState.BEFORE_START, GameState.BEFORE_START_ROUND, GameState.BETWEEN_ROUNDS, GameState.GAME_OVER);
    private final Map<UUID, Long> sneakStartTimes = new HashMap<>();
    private final Map<UUID, BukkitRunnable> regenTasks = new HashMap<>();
    private final Eden plugin;
    
    public PlayerListener(GameManager gameManager, Eden plugin) {
        this.gameManager = gameManager;
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
    	Player player = event.getPlayer();
        player.setGameMode(GameMode.ADVENTURE);
        player.getInventory().setItem(16, gameManager.getReadyItem());
        plugin.getPlayerData(player).setStatu(false);
        for(Player players: Bukkit.getOnlinePlayers()) {
	        gameManager.updateTabList(players);
	        gameManager.updateScoreboard(players);
        }
    }
    
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Player killer = player.getKiller();
        Entity lastKiller = player.getLastDamageCause().getDamageSource().getCausingEntity();
        PlayerData playerData = plugin.getPlayerData(player);
        
        if(playerData.getClasse().equalsIgnoreCase("bretteur_demoniaque")) {
    		int constitutionStars = playerData.getStats().get(4) - 1;
    		float constitution = plugin.playersModifiers.get("CONSTITUTION").get(constitutionStars);
    		player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(constitution);
        }
        
        Location deathLocation = player.getLocation();
        
        if (killer != null) {
            gameManager.addKill(killer);
            String killerClass = plugin.getPlayerData(killer).getClasse();		//Passif du nécromancien
            if(killerClass.equalsIgnoreCase("necromancien")) {
                Bukkit.dispatchCommand(killer, "mcastp "+ killer.getName() + " necromancienPassif");
            }
            
            if(killerClass.equalsIgnoreCase("bretteur_demoniaque")) {
            	killer.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(killer.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue() + 4);
        		killer.setHealth(killer.getHealth() + 4);
            }
        }

        if(lastKiller instanceof Monster) {
        	for(Player players : Bukkit.getOnlinePlayers()) {
        		if(plugin.getPlayerData(players).getClasse().equalsIgnoreCase("necromancien") && players.getLocation().distance(deathLocation) <= 10 && players != player) {
        			gameManager.addKill(players);
                    Bukkit.dispatchCommand(players, "mcastp "+ players.getName() + " necromancienPassif");
        		}
        	}
        }

        // Mettre le joueur en mode spectateur à l'endroit où il est mort
        new BukkitRunnable() {
            @Override
            public void run() {
                player.setGameMode(GameMode.SPECTATOR);
                Bukkit.getScheduler().runTaskLater(plugin, () -> { player.teleport(deathLocation); }, 1);
                
                // Démarrer le compte à rebours pour le respawn
                new BukkitRunnable() {
                    int countdown = 3;

                    @Override
                    public void run() {
                        if (countdown > 0) {
                        	gameManager.sendMessagePlayer(player, ChatColor.RED + "Réapparition dans " + countdown + "...");
                            countdown--;
                        } else {
                            player.setGameMode(GameMode.ADVENTURE);
                            gameManager.teleportPlayerToRandomSpawn(player);
                            cancel();
                        }
                    }
                }.runTaskTimer(plugin, 0L, 20L); // 20L = 1 seconde
            }
        }.runTaskLater(plugin, 1L); // 1 tick de retard pour que le joueur soit bien en spectateur
    }


    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        gameManager.updateScoreboard(player);
        for (Player players : Bukkit.getOnlinePlayers()) {
            gameManager.updateScoreboard(players);
            gameManager.updateTabList(players); // Corrected to update all players
        }
    }
    
    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerTookDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player && exceptRound.contains(gameManager.getGameState())) {
            event.setCancelled(true);
            return;
        }
    }
    
    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
    	
        if (event.getEntity() instanceof Player && exceptRound.contains(gameManager.getGameState())) {
            event.setCancelled(true);
            return;
        }
    	
    	
        if (event.getEntity() instanceof Player) {
        	event.getEntity().sendMessage("Player Event First");
        	handleClass((Player) event.getEntity(), event);
        }
        
        if(event.getDamager() instanceof Player && event.getDamageSource().getDamageType() == DamageType.PLAYER_ATTACK) {
        	event.getDamager().sendMessage("Damager Event Second");
        	handleClass((Player) event.getDamager(), event);
        }
                
        if(event.getDamageSource().getCausingEntity() instanceof Player && event.getDamageSource().getDamageType() == DamageType.INDIRECT_MAGIC)  {
        	event.getDamageSource().getCausingEntity().sendMessage("Damager Event Third");
        	handleClass((Player) event.getDamageSource().getCausingEntity(), event);
        }
    	
    }
    
    public void handleClass(Player player, EntityDamageByEntityEvent event) {
    	
        String playerClass = plugin.getPlayerData(player).getClasse();
        if(playerClass != null) {
            HandleClass.handlePassifs(player, playerClass, event);
        }
    }

    @EventHandler
    public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent event) {
        event.setCancelled(true);
        event.getPlayer().sendMessage(ChatColor.RED + "Swapping items between hands is disabled.");
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();

        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

        String inventoryName = event.getView().getTitle();
        	
        ShopManager shopManager = new ShopManager(plugin);
        //Bukkit.broadcastMessage("" + clickedItem.getItemMeta());
        
        /*if(event.getSlot() == 40) {
        	event.setCancelled(true);
        }*/
        
        // Main Shop Menu
        if (inventoryName.equals("Sectionner une classe")) {
            event.setCancelled(true);
            shopManager.selectClass(event);
        }
        
        if(inventoryName.equalsIgnoreCase("Shop de classe")) {
        	event.setCancelled(true);
        	shopManager.shopClass(event);
        }

        // Prevent dropping the Nether Star
        if (clickedItem.getType() == Material.NETHER_STAR && clickedItem.getItemMeta().getDisplayName().equals(ChatColor.GOLD + "Shop")) {
            event.setCancelled(true);
            if (exceptRound.contains(gameManager.getGameState())) {
            	shopManager.openShop(player);
            }
        }
        
        if(clickedItem.getType() == Material.LIME_WOOL && clickedItem.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Prêt")) {
        	event.setCancelled(true);
        	if(exceptRound.contains(gameManager.getGameState()) && gameManager.getGameState() != GameState.GAME_OVER) {
	        	player.getInventory().remove(clickedItem);
	        	player.getInventory().setItem(16, gameManager.getWaitingItem());
	        	plugin.getPlayerData(player).setStatu(true);
	        	if(checkStatusPlayers()) {
	        		handleStart();
	        		gameManager.setGameState(GameState.BEFORE_START_ROUND);
	        	}
        	}
        }
        if(clickedItem.getType() == Material.RED_WOOL && clickedItem.getItemMeta().getDisplayName().equals(ChatColor.RED + "Attente")) {
        	event.setCancelled(true);
        	if(exceptRound.contains(gameManager.getGameState()) && gameManager.getGameState() != GameState.GAME_OVER) {
		        player.getInventory().remove(clickedItem);
	        	player.getInventory().setItem(16, gameManager.getReadyItem());
	        	plugin.getPlayerData(player).setStatu(false);
        	}
        }

    }
    
    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerOpenInventory(InventoryOpenEvent event) {
        if (event.getPlayer() instanceof Player) {
            Player player = (Player) event.getPlayer();
            player.updateInventory();
        }
    }
    
    private boolean checkStatusPlayers() {
    	for (Player players : Bukkit.getOnlinePlayers()) {
    		boolean statu = plugin.getPlayerData(players).getStatu();
    		if(!statu)
    			return false;
    	}
    	return true;
    }
    
    private void removeStatusPlayers() {
    	for (Player players : Bukkit.getOnlinePlayers()) {
    		players.getInventory().setItem(16, gameManager.getReadyItem());
    		plugin.getPlayerData(players).setStatu(false);
    	}
    }
    
    private void handleStart() {
    	plugin.saveDefaultConfig();
    	new BukkitRunnable() {
    		int countdown = plugin.getConfig().getInt("roundTime", 10);
    		GameState gs = gameManager.getGameState();
    		@Override
    		public void run() {
    			if (countdown > 0) {
    				if (checkStatusPlayers()) {
	    				for(Player players: Bukkit.getOnlinePlayers()) {
	    					players.playSound(players, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
		                }
    					if(gs == GameState.BEFORE_START)
    						gameManager.sendMessagePlayers(ChatColor.GREEN + "La partie commence dans " + ChatColor.RED + countdown);
    					else if (gs == GameState.BETWEEN_ROUNDS)
    						gameManager.sendMessagePlayers(ChatColor.GREEN + "Le prochain round commence dans " + ChatColor.RED + countdown);
	    				countdown--;
    				} else {
    					gameManager.setGameState(gs);
    					cancel();
    				}
    			} else {
    				if(gs == GameState.BEFORE_START)
    					gameManager.startGame();
    				if(gs == GameState.BETWEEN_ROUNDS)
    					gameManager.startRound();
    				removeStatusPlayers();
    				cancel();
    			}
    		}
    	}.runTaskTimer(plugin, 0, 20 * 1);
    }
    
    /*private boolean playerHasItem(Player player, String item) {
    	MagicAPI magicAPI = plugin.getMagicAPI();
    	for(ItemStack inventoryItem: player.getInventory()) {
    		if(magicAPI.isWand(inventoryItem)) {
    			if (magicAPI.getWand(inventoryItem).getItem().getItemMeta().getLocalizedName().equalsIgnoreCase(item))
    				return true;
    		}
    	}
    	
    	return false;
    }*/
    
    private boolean playerHasItem(Player player, ItemStack item) {
    	for(ItemStack inventoryItem: player.getInventory()) {
    		if( inventoryItem != null && inventoryItem.getItemMeta() != null && inventoryItem.getItemMeta().getDisplayName().equalsIgnoreCase(item.getItemMeta().getDisplayName())) {
    			return true;
    		}
    	}
    	
    	return false;
    }
    
    public void onReload() {
        for(Player player: Bukkit.getOnlinePlayers()) {
    		player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);
	        player.setGameMode(GameMode.ADVENTURE);
	        player.getInventory().clear();
	        player.closeInventory();
		    gameManager.updateTabList(player);
		    gameManager.updateScoreboard(player);
	        if(exceptRound.contains(gameManager.getGameState())) {
		        ItemStack ready = gameManager.getReadyItem();
		        
		        if(playerHasItem(player, ready))
		        {
		        	continue;
		        }
		        player.getInventory().setItem(16, ready);
		        plugin.getPlayerData(player).setStatu(false);
	        }
        }
    }
    
    
    @EventHandler
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();
        
        if (event.isSneaking()) {
            // Player started sneaking
        	int timeToSneakBeforeRegen = plugin.getTimeToSneakBeforeRegen();
            sneakStartTimes.put(playerUUID, System.currentTimeMillis());
            BukkitRunnable task = new BukkitRunnable() {
                @Override
                public void run() {
                    if (player.isSneaking()) {
                        long sneakTime = System.currentTimeMillis() - sneakStartTimes.get(playerUUID);
                        int secondsSneaking = (int) (sneakTime / 1000);
                        
                        // Check how long the player has been sneaking
                        if (secondsSneaking < timeToSneakBeforeRegen) {
                            String message = ChatColor.YELLOW + "Vous serez régénéré dans " + (timeToSneakBeforeRegen - secondsSneaking) + " secondes...";
                            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
                        } else {
                            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20*2, 0, true, false)); // Regeneration I for 2 seconds (refreshed every second)
                            String message = ChatColor.GREEN + "Régénération en cours...";
                            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
                            
                        }
                    } else {
                        cancel();
                        player.removePotionEffect(PotionEffectType.REGENERATION);
                        sneakStartTimes.remove(playerUUID);
                        regenTasks.remove(playerUUID);
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.RED + "Vous avez perdu l'effet de régénération en arrêtant de sneak."));
                    }
                }
            };
            task.runTaskTimer(plugin, 0, 20); // Check every second
            regenTasks.put(playerUUID, task);
        } else {
            // Player stopped sneaking
            sneakStartTimes.remove(playerUUID);
            BukkitRunnable task = regenTasks.remove(playerUUID);
            if (task != null) {
                task.cancel();
            }
            player.removePotionEffect(PotionEffectType.REGENERATION);
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.RED + "Vous avez perdu l'effet de régénération en arrêtant de sneak."));
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Player topkiller = gameManager.playerKills.entrySet().stream().max(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse(null);
        if (topkiller != null && topkiller.isOnline() && topkiller.getGameMode() != GameMode.SPECTATOR && gameManager.getGameState() == GameState.IN_ROUND) {
            gameManager.showDirectionArrow(player, topkiller);
        }
    }
}
