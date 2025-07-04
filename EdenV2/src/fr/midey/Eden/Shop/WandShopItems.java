package fr.midey.Eden.Shop;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.elmakers.mine.bukkit.api.magic.MagicAPI;
import com.elmakers.mine.bukkit.api.wand.Wand;

import fr.midey.Eden.Eden;

public class WandShopItems {

    private static final List<String> WAND_KEYS = Arrays.asList(
            "wandtier1", "wandtier1_level2", "wandtier1_level3",
            "wandtier2_water", "wandtier2_water_level2", "wandtier2_water_level3",
            "wandtier2_earth", "wandtier2_earth_level2", "wandtier2_earth_level3",
            "wandtier2_fire", "wandtier2_fire_level2", "wandtier2_fire_level3",
            "wandtier2_wind", "wandtier2_wind_level2", "wandtier2_wind_level3"
    );
	
    private static  HashMap<String, Integer> WAND_PRICES = new HashMap<>();
    
    private static void initializePrices() {
    	WAND_PRICES.clear();
    	final List<Integer> prices_Level_1 = Arrays.asList(3, 6, 9, 12);
    	final List<Integer> prices_Level_2 = Arrays.asList(4, 7, 10, 13);
    	final List<Integer> prices_Level_3 = Arrays.asList(5, 8, 11, 14);
        int basePrice = 10; // Prix de base pour wandtier1
        for (int i = 0; i < WAND_KEYS.size(); i++) {
        	if(i >= 0 && i <= 2) {
        		WAND_PRICES.put(WAND_KEYS.get(i), basePrice + (i * 3));
        	} else if (prices_Level_1.contains(i)) {
        		WAND_PRICES.put(WAND_KEYS.get(i), 19);
        	} else if (prices_Level_2.contains(i)) {
        		WAND_PRICES.put(WAND_KEYS.get(i), 21);
        	} else if (prices_Level_3.contains(i)) {
        		WAND_PRICES.put(WAND_KEYS.get(i), 25);
        	}
        }
    }
    
    public static ItemStack getWandTier1() {
        return createWandItem("wandtier1", ChatColor.DARK_AQUA + "Wand Tier 1");
    }

    public static ItemStack getWandTier1Level2() {
        return createWandItem("wandtier1_level2", ChatColor.DARK_AQUA + "Wand Tier 1 Level 2");
    }

    public static ItemStack getWandTier1Level3() {
        return createWandItem("wandtier1_level3", ChatColor.DARK_AQUA + "Wand Tier 1 Level 3");
    }

    public static ItemStack getWandTier2Water() {
        return createWandItem("wandtier2_water", ChatColor.DARK_AQUA + "Wand Tier 2 Water");
    }

    public static ItemStack getWandTier2WaterLevel2() {
        return createWandItem("wandtier2_water_level2", ChatColor.DARK_AQUA + "Wand Tier 2 Water Level 2");
    }

    public static ItemStack getWandTier2WaterLevel3() {
        return createWandItem("wandtier2_water_level3", ChatColor.DARK_AQUA + "Wand Tier 2 Water Level 3");
    }

    public static ItemStack getWandTier2Earth() {
        return createWandItem("wandtier2_earth", ChatColor.DARK_AQUA + "Wand Tier 2 Earth");
    }

    public static ItemStack getWandTier2EarthLevel2() {
        return createWandItem("wandtier2_earth_level2", ChatColor.DARK_AQUA + "Wand Tier 2 Earth Level 2");
    }

    public static ItemStack getWandTier2EarthLevel3() {
        return createWandItem("wandtier2_earth_level3", ChatColor.DARK_AQUA + "Wand Tier 2 Earth Level 3");
    }

    public static ItemStack getWandTier2Fire() {
        return createWandItem("wandtier2_fire", ChatColor.DARK_AQUA + "Wand Tier 2 Fire");
    }

    public static ItemStack getWandTier2FireLevel2() {
        return createWandItem("wandtier2_fire_level2", ChatColor.DARK_AQUA + "Wand Tier 2 Fire Level 2");
    }

    public static ItemStack getWandTier2FireLevel3() {
        return createWandItem("wandtier2_fire_level3", ChatColor.DARK_AQUA + "Wand Tier 2 Fire Level 3");
    }

    public static ItemStack getWandTier2Wind() {
        return createWandItem("wandtier2_wind", ChatColor.DARK_AQUA + "Wand Tier 2 Wind");
    }

    public static ItemStack getWandTier2WindLevel2() {
        return createWandItem("wandtier2_wind_level2", ChatColor.DARK_AQUA + "Wand Tier 2 Wind Level 2");
    }

    public static ItemStack getWandTier2WindLevel3() {
        return createWandItem("wandtier2_wind_level3", ChatColor.DARK_AQUA + "Wand Tier 2 Wind Level 3");
    }

    private static ItemStack createWandItem(String wandKey, String displayName) {
        Wand wand = Eden.magicAPI.createWand(wandKey);
        ItemStack wandItem = wand.getItem();
        ItemMeta wandMeta = wandItem.getItemMeta();
        wandMeta.setDisplayName(displayName);
        wandMeta.setLocalizedName(wandKey);
        List<String> lore = wandMeta.hasLore() ? wandMeta.getLore() : new ArrayList<>();
        lore.add(ChatColor.GOLD + "Prix : " + ChatColor.BOLD + getSpecificWandPrices(wandKey) + " points");
        wandMeta.setLore(lore);              
        wandItem.setItemMeta(wandMeta);
        return wandItem;
    }

    public static String getActualWandKey(Player player) {
    	MagicAPI magicAPI = Eden.magicAPI;
    	for(ItemStack inventoryItem: player.getInventory()) {
    		if(magicAPI.isWand(inventoryItem)) {
    			String wandKey = magicAPI.getWand(inventoryItem).getItem().getItemMeta().getLocalizedName();
    			if (WAND_KEYS.contains(wandKey))
    				return wandKey;
    		}
    	}
    	return null;
    }
    
	public static List<String> getWandKeys() { return WAND_KEYS; }
	
	public static HashMap<String, Integer> getWandPrices() {
		initializePrices();
		return WAND_PRICES;
	}
	
	public static int getSpecificWandPrices(String wandKey) {
		initializePrices();
		return WAND_PRICES.getOrDefault(wandKey, 0);
	}
}
