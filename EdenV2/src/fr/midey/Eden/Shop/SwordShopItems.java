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

public class SwordShopItems {

    private static final List<String> SWORD_KEYS = Arrays.asList(
        "swordtier1", "swordtier1_level2", "swordtier1_level3",
        "swordtier2_dagger", "swordtier2_dagger_level2", "swordtier2_dagger_level3",
        "swordtier2_hammer", "swordtier2_hammer_level2", "swordtier2_hammer_level3",
        "swordtier2_swordmaster", "swordtier2_swordmaster_level2", "swordtier2_swordmaster_level3",
        "swordtier2_fire", "swordtier2_fire_level2", "swordtier2_fire_level3"
    );
    
    private static  HashMap<String, Integer> SWORD_PRICES = new HashMap<>();
    
    private static void initializePrices() {
    	SWORD_PRICES.clear();
    	final List<Integer> prices_Level_1 = Arrays.asList(3, 6, 9, 12);
    	final List<Integer> prices_Level_2 = Arrays.asList(4, 7, 10, 13);
    	final List<Integer> prices_Level_3 = Arrays.asList(5, 8, 11, 14);
        int basePrice = 10; // Prix de base pour wandtier1
        for (int i = 0; i < SWORD_KEYS.size(); i++) {
        	if(i >= 0 && i <= 2) {
        		SWORD_PRICES.put(SWORD_KEYS.get(i), basePrice + (i * 3));
        	} else if (prices_Level_1.contains(i)) {
        		SWORD_PRICES.put(SWORD_KEYS.get(i), 19);
        	} else if (prices_Level_2.contains(i)) {
        		SWORD_PRICES.put(SWORD_KEYS.get(i), 21);
        	} else if (prices_Level_3.contains(i)) {
        		SWORD_PRICES.put(SWORD_KEYS.get(i), 25);
        	}
        }
    }

    public static ItemStack getSwordTier1() {
        return createSwordItem("swordtier1", ChatColor.DARK_AQUA + "Sword Tier 1");
    }

    public static ItemStack getSwordTier1Level2() {
        return createSwordItem("swordtier1_level2", ChatColor.DARK_AQUA + "Sword Tier 1 Level 2");
    }

    public static ItemStack getSwordTier1Level3() {
        return createSwordItem("swordtier1_level3", ChatColor.DARK_AQUA + "Sword Tier 1 Level 3");
    }

    public static ItemStack getSwordTier2Dagger() {
        return createSwordItem("swordtier2_dagger", ChatColor.DARK_AQUA + "Sword Tier 2 Dagger");
    }

    public static ItemStack getSwordTier2DaggerLevel2() {
        return createSwordItem("swordtier2_dagger_level2", ChatColor.DARK_AQUA + "Sword Tier 2 Dagger Level 2");
    }

    public static ItemStack getSwordTier2DaggerLevel3() {
        return createSwordItem("swordtier2_dagger_level3", ChatColor.DARK_AQUA + "Sword Tier 2 Dagger Level 3");
    }

    public static ItemStack getSwordTier2Hammer() {
        return createSwordItem("swordtier2_hammer", ChatColor.DARK_AQUA + "Sword Tier 2 Hammer");
    }

    public static ItemStack getSwordTier2HammerLevel2() {
        return createSwordItem("swordtier2_hammer_level2", ChatColor.DARK_AQUA + "Sword Tier 2 Hammer Level 2");
    }

    public static ItemStack getSwordTier2HammerLevel3() {
        return createSwordItem("swordtier2_hammer_level3", ChatColor.DARK_AQUA + "Sword Tier 2 Hammer Level 3");
    }

    public static ItemStack getSwordTier2Swordmaster() {
        return createSwordItem("swordtier2_swordmaster", ChatColor.DARK_AQUA + "Sword Tier 2 Swordmaster");
    }

    public static ItemStack getSwordTier2SwordmasterLevel2() {
        return createSwordItem("swordtier2_swordmaster_level2", ChatColor.DARK_AQUA + "Sword Tier 2 Swordmaster Level 2");
    }

    public static ItemStack getSwordTier2SwordmasterLevel3() {
        return createSwordItem("swordtier2_swordmaster_level3", ChatColor.DARK_AQUA + "Sword Tier 2 Swordmaster Level 3");
    }

    public static ItemStack getSwordTier2Fire() {
        return createSwordItem("swordtier2_fire", ChatColor.DARK_AQUA + "Sword Tier 2 Fire");
    }

    public static ItemStack getSwordTier2FireLevel2() {
        return createSwordItem("swordtier2_fire_level2", ChatColor.DARK_AQUA + "Sword Tier 2 Fire Level 2");
    }

    public static ItemStack getSwordTier2FireLevel3() {
        return createSwordItem("swordtier2_fire_level3", ChatColor.DARK_AQUA + "Sword Tier 2 Fire Level 3");
    }
    
    private static ItemStack createSwordItem(String wandKey, String displayName) {
        Wand wand = Eden.magicAPI.createWand(wandKey);
        ItemStack wandItem = wand.getItem();
        ItemMeta wandMeta = wandItem.getItemMeta();
        wandMeta.setDisplayName(displayName);
        wandMeta.setLocalizedName(wandKey);
        List<String> lore = wandMeta.hasLore() ? wandMeta.getLore() : new ArrayList<>();
        lore.add(ChatColor.GOLD + "Prix : " + ChatColor.BOLD + getSpecificSwordPrices(wandKey) + " points");
        wandMeta.setLore(lore);              
        wandItem.setItemMeta(wandMeta);
        return wandItem;
    }

    public static String getActualSwordKey(Player player) {
    	MagicAPI magicAPI = Eden.magicAPI;
    	for(ItemStack inventoryItem: player.getInventory()) {
    		if(magicAPI.isWand(inventoryItem)) {
    			String wandKey = magicAPI.getWand(inventoryItem).getItem().getItemMeta().getLocalizedName();
    			if (SWORD_KEYS.contains(wandKey))
    				return wandKey;
    		}
    	}
    	return null;
    }
    
	public static List<String> getSwordKeys() { return SWORD_KEYS; }
	
	public static int getSpecificSwordPrices(String wandKey) {
		initializePrices();
		return SWORD_PRICES.getOrDefault(wandKey, 0);
	}
}
	
	
	
	
	
	