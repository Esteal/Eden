package fr.midey.Eden;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import com.elmakers.mine.bukkit.api.magic.Mage;
import com.elmakers.mine.bukkit.api.magic.MagicAPI;
import com.elmakers.mine.bukkit.api.wand.Wand;

import fr.midey.Eden.Shop.WeaponShopItems;
import net.md_5.bungee.api.ChatColor;

public class ShopManager {

	private Eden plugin;
	private Map<Wand, List<String>> basicSpells = new HashMap<>();
	private List<Integer> cmdUpgrade = Arrays.asList(18017, 18187, 18137, 18265, 18381);
	private int statPriceUpgrade;
	private double augmentationOfState;
	
	public ShopManager(Eden plugin) {
		this.plugin = plugin;
        FileConfiguration config = plugin.getConfig();
        if (config.isConfigurationSection("shop")) {
            for (String shopItem : config.getConfigurationSection("shop").getKeys(false)) {
                List<String> spells = config.getStringList("shop." + shopItem);
                basicSpells.put(getWeapon(shopItem), spells);
            }
        }
        
        statPriceUpgrade = config.getInt("statisticPriceUpgrade", 10);
        augmentationOfState = config.getDouble("statisticPriceIncrease", 1);
	}
	
	public Wand getWeapon(String wandKey) {
		return plugin.getMagicAPI().createWand(wandKey);
	}
	
	
	public void openShop(Player player) {
		if(!playerHasWand(player)) {
			openBasicShop(player);
		}
		ItemStack wand = getWandInItemStack(player);
		
		if(wand != null) {
			createShop(player);
		}
	}
	
	private void createShop(Player player) {
	    Wand wand = getWand(player);
	    Collection<String> spellTemplate = wand.getSpells();
	    Inventory inventory = Bukkit.createInventory(player, 45, "Shop de classe");
	    MagicAPI magicAPI = plugin.getMagicAPI();
	    
	    if (spellTemplate.isEmpty()) {
	        addBasicSpellsToInventory(player, wand, inventory, magicAPI);
	    } else {
	        updateSpellsInInventory(player, wand, inventory, magicAPI, spellTemplate);
	    }

	    addMoneyInfo(player, inventory);
	    addPlayerHeadInfo(player, inventory);
	    addStatsUpgrader(player, inventory);
	    player.openInventory(inventory);
	}

	private void addStatsUpgrader(Player player, Inventory inventory) {
		addCustomPlayerHeadsToInventory(player, inventory);
	}

    public ItemStack createCustomPlayerHead(Player player, String name, int customModelData, String statKey) {
        ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) playerHead.getItemMeta();
        if (meta != null) {
            meta.setOwningPlayer(Bukkit.getOfflinePlayer(player.getUniqueId()));
            meta.setDisplayName(name);
            meta.setLocalizedName(statKey);
            meta.setCustomModelData(customModelData);
            
            int currentStat = plugin.getPlayerData(player).getSpecificStat(statKey);
            int newStat = currentStat + 1; // Assuming the improvement increases the stat by 1
            
            int size = plugin.playersModifiers.get(statKey).size();
            
            String currentStars = WeaponShopItems.getStars(currentStat, statKey);
            String newStars = WeaponShopItems.getStars(newStat, statKey);
            Integer price = (plugin.getPlayerData(player).getPriceOfStats() == null) ? statPriceUpgrade : plugin.getPlayerData(player).getPriceOfStats();
            String priceString = "§6Prix : §l"+ price + " points";
            
            if(newStat > size - 1) {
            	priceString = "§6Prix : §k" + price + " §r§6points";
            	newStars = ChatColor.RED + "✖";
            }
            List<String> loreList = Arrays.asList(
                currentStars + ChatColor.GRAY + " → " + newStars, " ", priceString
            );
            meta.setLore(loreList);
            playerHead.setItemMeta(meta);
        }
        return playerHead;
    }

    public void addCustomPlayerHeadsToInventory(Player player, Inventory inventory) {
        inventory.setItem(37, createCustomPlayerHead(player, "§aAmélioration de la défense", cmdUpgrade.get(0), "ARMOR"));
        inventory.setItem(36, createCustomPlayerHead(player, "§cAmélioration de la force", cmdUpgrade.get(1), "ATTACK_DAMAGE"));
        inventory.setItem(40, createCustomPlayerHead(player, "§dAmélioration de la Constitution", cmdUpgrade.get(2), "CONSTITUTION"));
        inventory.setItem(38, createCustomPlayerHead(player, "§bAmélioration de la Vitesse de déplacement", cmdUpgrade.get(3), "MOVEMENT_SPEED"));
        inventory.setItem(39, createCustomPlayerHead(player, "§eAmélioration du Mana", cmdUpgrade.get(4), "MANA"));
    }
	
	private void addPlayerHeadInfo(Player player, Inventory inventory) {
		inventory.setItem(43, getPlayerHead(player));
	}

	private void addMoneyInfo(Player player, Inventory inventory) {
		ItemStack gold = new ItemStack(Material.GOLD_INGOT);
		ItemMeta goldMeta = gold.getItemMeta();
		goldMeta.setDisplayName(ChatColor.GOLD + "Points : " + ChatColor.BOLD + plugin.getPlayerData(player).getPoints());
		gold.setItemMeta(goldMeta);
		inventory.setItem(44, gold);
	}

	private void addBasicSpellsToInventory(Player player, Wand wand, Inventory inventory, MagicAPI magicAPI) {
	    for (Wand wands : basicSpells.keySet()) {
	        if (wands.getName().equalsIgnoreCase(wand.getName())) {
	            for (String spells : basicSpells.get(wands)) {
	    	    	String price = ChatColor.GOLD + "Prix : " + ChatColor.BOLD + getSpellPrice(spells)+ " points";
	                addItemToInventory(magicAPI, inventory, spells, price);
	            }
	        }
	    }
	}

	private void updateSpellsInInventory(Player player, Wand wand, Inventory inventory, MagicAPI magicAPI, Collection<String> spellTemplate) {
	    for (Wand wands : basicSpells.keySet()) {
	        if (wands.getName().equalsIgnoreCase(wand.getName())) {
	            List<String> copyBasicSpells = new ArrayList<>(basicSpells.get(wands));
	            for (String spells : basicSpells.get(wands)) {
	                for (String spellInWand : spellTemplate) {
	                    String spellUp = getUpdatedSpell(spells, spellInWand, copyBasicSpells);
	                    if (spellUp == null || itemAlreadyExist(spellUp, inventory)) continue;
	        	    	String price = ChatColor.GOLD + "Prix : " + ChatColor.BOLD + getSpellPrice(spellUp) + " points";
	                    addItemToInventory(magicAPI, inventory, spellUp, price);
	                }
	            }
	            addRemainingSpellsToInventory(magicAPI, inventory, copyBasicSpells);
	        }
	    }
	}

	private String getUpdatedSpell(String spells, String spellInWand, List<String> copyBasicSpells) {
	    String spellUp = null;
	    if (spellInWand.startsWith(spells)) {
	        if (spellInWand.equalsIgnoreCase(spells)) {
	            spellUp = spells + "|2";
	        } else if (spellInWand.endsWith("|2")) {
	            spellUp = spells + "|3";
	        }
	        copyBasicSpells.remove(spells);
	    }
	    return spellUp;
	}

	private void addItemToInventory(MagicAPI magicAPI, Inventory inventory, String spellName, String lore) {
	    ItemStack it = magicAPI.createSpellItem(spellName);
	    ItemMeta itM = it.getItemMeta();
	    itM.setLocalizedName(spellName);
        List<String> lores = itM.hasLore() ? itM.getLore() : new ArrayList<>();
        lores.add("  ");
        lores.add(lore);
        itM.setLore(lores);  
	    it.setItemMeta(itM);
	    inventory.addItem(it);
	}

	private void addRemainingSpellsToInventory(MagicAPI magicAPI, Inventory inventory, List<String> copyBasicSpells) {
	    for (String spellsRemaining : copyBasicSpells) {
	    	String price = ChatColor.GOLD + "Prix : " + ChatColor.BOLD + getSpellPrice(spellsRemaining) + " points";
	        addItemToInventory(magicAPI, inventory, spellsRemaining, price);
	    }
	}
	
	private int getSpellPrice(String spell) {
		int price = 10;
		
		if(spell.endsWith("|2"))
			price = 15;
		else if(spell.endsWith("|3"))
			price = 20;
		
		return price;
	}


	private boolean itemAlreadyExist(String spellUp, Inventory inventory) {
		for(ItemStack it : inventory) {
			if(it != null && it.hasItemMeta() && it.getItemMeta().hasLocalizedName() && it.getItemMeta().getDisplayName().equalsIgnoreCase(spellUp))
				return true;
		}
		return false;
	}

	private void openBasicShop(Player player) {
		Inventory inv = Bukkit.createInventory(player, 45, "Sectionner une classe");
		
	    inv.setItem(10, WeaponShopItems.getDagger());            // Première ligne
	    inv.setItem(12, WeaponShopItems.getLeftGauntlet());
	    inv.setItem(14, WeaponShopItems.getHammer());
	    inv.setItem(16, WeaponShopItems.getElementalWand());
	    
	    inv.setItem(20, WeaponShopItems.getDeathWand());		// Deuxième ligne
	    inv.setItem(22, WeaponShopItems.getDemonSword());
	    inv.setItem(24, WeaponShopItems.getBlackWand());
	    
	    inv.setItem(28, WeaponShopItems.getMasterSword()); 		 // Troisième ligne
	    inv.setItem(30, WeaponShopItems.getSamouraiSword());
	    inv.setItem(32, WeaponShopItems.getStick());
	    inv.setItem(34, WeaponShopItems.getPaladinSword());

	    // Ajouter des vitres teintées pour une esthétique plus propre
	    ItemStack glassPane = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
	    ItemMeta meta = glassPane.getItemMeta();
	    meta.setDisplayName(" ");
	    meta.setLocalizedName(" ");
	    glassPane.setItemMeta(meta);

	    for (int i = 0; i < inv.getSize(); i++) {
	        if (inv.getItem(i) == null) {
	            inv.setItem(i, glassPane);
	        }
	    }
		
		player.openInventory(inv);
	}
	
	public boolean playerHasWand(Player player) {
		MagicAPI magicAPI = plugin.getMagicAPI();
		for(ItemStack it : player.getInventory()) {
			if(magicAPI.isWand(it))
				return true;
		}
		return false;
	}

	public ItemStack getWandInItemStack(Player player) {
		MagicAPI magicAPI = plugin.getMagicAPI();
		for(ItemStack it : player.getInventory()) {
			if(magicAPI.isWand(it))
				return it;
		}
		return null;
	}
	
	public Wand getWand(Player player) {
		MagicAPI magicAPI = plugin.getMagicAPI();
		for(ItemStack it : player.getInventory()) {
			if(magicAPI.isWand(it))
				return magicAPI.getWand(it);
		}
		return null;
	}
	
	public int removePlayerWand(Player player) {
		MagicAPI magicAPI = plugin.getMagicAPI();
		int i = 0;
		for(ItemStack it : player.getInventory()) {
			if(magicAPI.isWand(it)) {
				player.getInventory().remove(it);
				return i;
			}
			i++;
		}
		return 0;
	}
	
    public ItemStack getPlayerHead(Player player) {
        ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) playerHead.getItemMeta();
        if (meta != null) {
            meta.setOwningPlayer(Bukkit.getOfflinePlayer(player.getUniqueId()));
            meta.setDisplayName("§fStatistiques :");
            List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
            for(String stats : WeaponShopItems.genererStats(plugin.getPlayerData(player).getStats()))
            	lore.add(stats);
            meta.setLore(lore);
            playerHead.setItemMeta(meta);
        }
        return playerHead;
    }
	
	public void selectClass(InventoryClickEvent event) {
		
		ItemStack it = event.getCurrentItem();
		Player player = (Player) event.getWhoClicked();
		if (it == null || it.getType() == Material.AIR || it.getType() == Material.BLACK_STAINED_GLASS_PANE) return;
		
		String displayName = it.getItemMeta().getLocalizedName();
		
		if(displayName == null) return;
		
		player.sendMessage(displayName);
		PlayerData playerData = plugin.getPlayerData(player);

		switch(displayName) {
			case "dagger":
				playerData.setClasse("assassin");
				break;
			case "hammer":
				playerData.setClasse("berserker");
				break;
			case "demonsword":
				playerData.setClasse("bretteur_demoniaque");
				break;
			case "swordmaster":
				playerData.setClasse("maitre_epeiste");
				break;
			case "paladinsword":
				player.getInventory().setItemInOffHand(WeaponShopItems.getShield());
				playerData.setClasse("paladin");
				break;
			case "deathwand":
				playerData.setClasse("necromancien");
				break;
			case "samouraisword":
				playerData.setClasse("samourai");
				break;
			case "elementalwand":
				playerData.setClasse("mage_elementaire");
				break;
			case "gauntletleft":
				player.getInventory().setItemInOffHand(WeaponShopItems.getLeftGauntlet());
				playerData.setClasse("artiste_martial");
				break;
			case "stick":
				playerData.setClasse("moine");
				break;
			case "blackwand":
				playerData.setClasse("mage_noir");
				break;
			case " ":
				player.sendMessage(ChatColor.RED + "La classe séléctionné n'est pas disponible, veuillez en choisir une autre.");
				playerData.setClasse(null);
				return;
			default:
				playerData.setClasse(null);
				player.sendMessage(ChatColor.RED + "La classe séléctionné n'est pas disponible, veuillez en choisir une autre.");
				break;
		}
		
		plugin.getMagicAPI().giveItemToPlayer(player, it);
		
		for(String modifier : plugin.playersModifiers.keySet()) {
			player.sendMessage("modif " + modifier);
			reloadStats(player, modifier);
		}
		player.sendMessage("pass");
		player.closeInventory();
		openShop(player);
	}
	
	@SuppressWarnings("deprecation")
	private void updateManaPlayer(Player player) {
		MagicAPI mAPI = plugin.getMagicAPI();
		Mage magePlayer = mAPI.getMage(player);
		Wand activeWand = magePlayer.getActiveWand();
		activeWand.activate(magePlayer);
		int manaStars = plugin.getPlayerData(player).getStats().get(3);
		float mana = Eden.MAIN.playersModifiers.get("MANA").get(manaStars);
		activeWand.setManaMax(mana);
		activeWand.setMana(mana);		
	}

	@SuppressWarnings("deprecation")
	public void shopClass(InventoryClickEvent event) {
		ItemStack it = event.getCurrentItem();
		Player player = (Player) event.getWhoClicked();
		Mage mage = plugin.getMagicAPI().getMage(player);
		
		if (it == null || it.getType() == Material.AIR) return;
		
		if(it.getItemMeta().hasCustomModelData() && cmdUpgrade.contains(it.getItemMeta().getCustomModelData())) {
		    purchaseUpgrade(player, it); 
			return; 
		}
		
		if(!plugin.getMagicAPI().isSpell(it)) return;
		
		String localizedName = it.getItemMeta().getLocalizedName();

		int price = getSpellPrice(localizedName);
		int points = plugin.getPlayerData(player).getPoints();
		
		Wand activeWand = mage.getActiveWand();
		
		if(activeWand == null) return;
		
		if(localizedName == null) return;
		
		if(price > points) {
			player.sendMessage(ChatColor.RED + "Points insuffisants pour l'achat de cet article !");
			player.playSound(player, Sound.ENTITY_VILLAGER_NO, 1f, 1f);
			return;
		}
		
		plugin.getPlayerData(player).setPoints(points - price);
		plugin.getGameManager().updateAllScoreboards();
		
		activeWand.addSpell(localizedName);
		player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 0.5f);
		openShop(player);
	}

	private void purchaseUpgrade(Player player, ItemStack it) {
		String stat = it.getItemMeta().getLocalizedName();
		PlayerData playerData = plugin.getPlayerData(player);
		int newStar = playerData.getSpecificStat(stat) + 1;
		
		if(newStar > plugin.playersModifiers.get(stat).size() - 1) {
			player.sendMessage(ChatColor.RED + "Statistique déjà au niveau le plus élevé");
			player.playSound(player, Sound.ENTITY_VILLAGER_NO, 1f, 1f);
			openShop(player);
			return;
		}
		
		int points = plugin.getPlayerData(player).getPoints();
        Integer price = (plugin.getPlayerData(player).getPriceOfStats() == null) ? statPriceUpgrade : plugin.getPlayerData(player).getPriceOfStats();
		
		if(price > points) {
			player.sendMessage(ChatColor.RED + "Points insuffisants pour l'achat de cet article !");
			player.playSound(player, Sound.ENTITY_VILLAGER_NO, 1f, 1f);
			return;
		}
		plugin.getPlayerData(player).setPriceOfStats((int) (price * augmentationOfState));
		plugin.getPlayerData(player).setPoints(points - price);
		plugin.getGameManager().updateAllScoreboards();
		player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 0.5f);
		playerData.setSpecificStat(stat, newStar);
		reloadStats(player, stat);
		openShop(player);
	}

	private void reloadStats(Player player, String StatToUpdate) {
		switch(StatToUpdate) {
			case "MOVEMENT_SPEED":
				int speedStars = plugin.getPlayerData(player).getSpecificStat("MOVEMENT_SPEED");
				float speedValue = plugin.playersModifiers.get("MOVEMENT_SPEED").get(speedStars);
				player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(speedValue);
				break;
			case "MANA":
				updateManaPlayer(player);
				break;
			case "CONSTITUTION":
				int healthStars = plugin.getPlayerData(player).getSpecificStat("CONSTITUTION");
				float healthValue = plugin.playersModifiers.get("CONSTITUTION").get(healthStars);
				if(!(healthValue == player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue())) {
					player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(healthValue);
					if(player.getHealth() < player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue())
						player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
				}
				break;
			case "ATTACK_DAMAGE":
				int attackStars = plugin.getPlayerData(player).getSpecificStat("ATTACK_DAMAGE");
				float attackValue = plugin.playersModifiers.get("ATTACK_DAMAGE").get(attackStars);
				player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(attackValue);
				break;
			case "ARMOR":
				player.sendMessage("hear 1");
				int armorStars = plugin.getPlayerData(player).getSpecificStat("ARMOR");
				player.sendMessage("hear 2");
				float armorValue = plugin.playersModifiers.get("ARMOR").get(armorStars);
				player.sendMessage("hear 3");
				player.getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(armorValue);
				player.sendMessage("hear 4");
				break;
			default:
				break;
		}
	}

	
	
	
	
	
	
	
	
	
	
}
