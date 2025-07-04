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

public class ArmorShopItems {

    private static final List<String> ARMOR_KEYS = Arrays.asList(
            "leatherhelmet", "leatherchestplate", "leatherleggings", "leatherboots",
            "leatherhelmet_tier2", "leatherchestplate_tier2", "leatherleggings_tier2", "leatherboots_tier2",
            "leatherhelmet_tier3", "leatherchestplate_tier3", "leatherleggings_tier3", "leatherboots_tier3",
            "chainmailhelmet", "chainmailchestplate", "chainmailleggings", "chainmailboots",
            "chainmailhelmet_tier2", "chainmailchestplate_tier2", "chainmailleggings_tier2", "chainmailboots_tier2",
            "chainmailhelmet_tier3", "chainmailchestplate_tier3", "chainmailleggings_tier3", "chainmailboots_tier3",
            "ironhelmet", "ironchestplate", "ironleggings", "ironboots",
            "ironhelmet_tier2", "ironchestplate_tier2", "ironleggings_tier2", "ironboots_tier2",
            "ironhelmet_tier3", "ironchestplate_tier3", "ironleggings_tier3", "ironboots_tier3"
        );


    private static final List<String> HELMET_KEYS = Arrays.asList(
        "leatherhelmet", "leatherhelmet_tier2", "leatherhelmet_tier3",
        "chainmailhelmet", "chainmailhelmet_tier2", "chainmailhelmet_tier3",
        "ironhelmet", "ironhelmet_tier2", "ironhelmet_tier3"
    );

    private static final List<String> CHESTPLATE_KEYS = Arrays.asList(
        "leatherchestplate", "leatherchestplate_tier2", "leatherchestplate_tier3",
        "chainmailchestplate", "chainmailchestplate_tier2", "chainmailchestplate_tier3",
        "ironchestplate", "ironchestplate_tier2", "ironchestplate_tier3"
    );

    private static final List<String> LEGGINGS_KEYS = Arrays.asList(
        "leatherleggings", "leatherleggings_tier2", "leatherleggings_tier3",
        "chainmailleggings", "chainmailleggings_tier2", "chainmailleggings_tier3",
        "ironleggings", "ironleggings_tier2", "ironleggings_tier3"
    );

    private static final List<String> BOOTS_KEYS = Arrays.asList(
        "leatherboots", "leatherboots_tier2", "leatherboots_tier3",
        "chainmailboots", "chainmailboots_tier2", "chainmailboots_tier3",
        "ironboots", "ironboots_tier2", "ironboots_tier3"
    );
    
    private static  HashMap<String, Integer> ARMOR_PRICES = new HashMap<>();

    private static void initializePrices() {
    	ARMOR_PRICES.clear();
        // Leather armor prices T1
        ARMOR_PRICES.put("leatherhelmet", 5);
        ARMOR_PRICES.put("leatherchestplate", 8);
        ARMOR_PRICES.put("leatherleggings", 7);
        ARMOR_PRICES.put("leatherboots", 5);
        //T2
        ARMOR_PRICES.put("leatherhelmet_tier2", 8);
        ARMOR_PRICES.put("leatherchestplate_tier2", 12);
        ARMOR_PRICES.put("leatherleggings_tier2", 11);
        ARMOR_PRICES.put("leatherboots_tier2", 8);
        //T3
        ARMOR_PRICES.put("leatherhelmet_tier3", 12);
        ARMOR_PRICES.put("leatherchestplate_tier3", 16);
        ARMOR_PRICES.put("leatherleggings_tier3", 15);
        ARMOR_PRICES.put("leatherboots_tier3", 12);

        // Chainmail armor prices T1
        ARMOR_PRICES.put("chainmailhelmet", 5);
        ARMOR_PRICES.put("chainmailchestplate", 8);
        ARMOR_PRICES.put("chainmailleggings", 7);
        ARMOR_PRICES.put("chainmailboots", 5);
        //T2
        ARMOR_PRICES.put("chainmailhelmet_tier2", 8);
        ARMOR_PRICES.put("chainmailchestplate_tier2", 12);
        ARMOR_PRICES.put("chainmailleggings_tier2", 11);
        ARMOR_PRICES.put("chainmailboots_tier2", 8);
        //T3
        ARMOR_PRICES.put("chainmailhelmet_tier3", 12);
        ARMOR_PRICES.put("chainmailchestplate_tier3", 16);
        ARMOR_PRICES.put("chainmailleggings_tier3", 15);
        ARMOR_PRICES.put("chainmailboots_tier3", 12);

        // Iron armor prices T1
        ARMOR_PRICES.put("ironhelmet", 5);
        ARMOR_PRICES.put("ironchestplate", 8);
        ARMOR_PRICES.put("ironleggings", 7);
        ARMOR_PRICES.put("ironboots", 5);
        //T2
        ARMOR_PRICES.put("ironhelmet_tier2", 8);
        ARMOR_PRICES.put("ironchestplate_tier2", 12);
        ARMOR_PRICES.put("ironleggings_tier2", 11);
        ARMOR_PRICES.put("ironboots_tier2", 8);
        //T3
        ARMOR_PRICES.put("ironhelmet_tier3", 12);
        ARMOR_PRICES.put("ironchestplate_tier3", 16);
        ARMOR_PRICES.put("ironleggings_tier3", 15);
        ARMOR_PRICES.put("ironboots_tier3", 12);
    }
    
    // Leather Armor Getters
    public static ItemStack getLeatherHelmet() {
        return createArmorItem("leatherhelmet", ChatColor.DARK_AQUA + "Leather Helmet");
    }

    public static ItemStack getLeatherChestplate() {
        return createArmorItem("leatherchestplate", ChatColor.DARK_AQUA + "Leather Chestplate");
    }

    public static ItemStack getLeatherLeggings() {
        return createArmorItem("leatherleggings", ChatColor.DARK_AQUA + "Leather Leggings");
    }

    public static ItemStack getLeatherBoots() {
        return createArmorItem("leatherboots", ChatColor.DARK_AQUA + "Leather Boots");
    }

    public static ItemStack getLeatherHelmetTier2() {
        return createArmorItem("leatherhelmet_tier2", ChatColor.DARK_AQUA + "Leather Helmet Tier 2");
    }

    public static ItemStack getLeatherChestplateTier2() {
        return createArmorItem("leatherchestplate_tier2", ChatColor.DARK_AQUA + "Leather Chestplate Tier 2");
    }

    public static ItemStack getLeatherLeggingsTier2() {
        return createArmorItem("leatherleggings_tier2", ChatColor.DARK_AQUA + "Leather Leggings Tier 2");
    }

    public static ItemStack getLeatherBootsTier2() {
        return createArmorItem("leatherboots_tier2", ChatColor.DARK_AQUA + "Leather Boots Tier 2");
    }

    public static ItemStack getLeatherHelmetTier3() {
        return createArmorItem("leatherhelmet_tier3", ChatColor.DARK_AQUA + "Leather Helmet Tier 3");
    }

    public static ItemStack getLeatherChestplateTier3() {
        return createArmorItem("leatherchestplate_tier3", ChatColor.DARK_AQUA + "Leather Chestplate Tier 3");
    }

    public static ItemStack getLeatherLeggingsTier3() {
        return createArmorItem("leatherleggings_tier3", ChatColor.DARK_AQUA + "Leather Leggings Tier 3");
    }

    public static ItemStack getLeatherBootsTier3() {
        return createArmorItem("leatherboots_tier3", ChatColor.DARK_AQUA + "Leather Boots Tier 3");
    }

    // Chainmail Armor Getters
    public static ItemStack getChainmailHelmet() {
        return createArmorItem("chainmailhelmet", ChatColor.DARK_AQUA + "Chainmail Helmet");
    }

    public static ItemStack getChainmailChestplate() {
        return createArmorItem("chainmailchestplate", ChatColor.DARK_AQUA + "Chainmail Chestplate");
    }

    public static ItemStack getChainmailLeggings() {
        return createArmorItem("chainmailleggings", ChatColor.DARK_AQUA + "Chainmail Leggings");
    }

    public static ItemStack getChainmailBoots() {
        return createArmorItem("chainmailboots", ChatColor.DARK_AQUA + "Chainmail Boots");
    }

    public static ItemStack getChainmailHelmetTier2() {
        return createArmorItem("chainmailhelmet_tier2", ChatColor.DARK_AQUA + "Chainmail Helmet Tier 2");
    }

    public static ItemStack getChainmailChestplateTier2() {
        return createArmorItem("chainmailchestplate_tier2", ChatColor.DARK_AQUA + "Chainmail Chestplate Tier 2");
    }

    public static ItemStack getChainmailLeggingsTier2() {
        return createArmorItem("chainmailleggings_tier2", ChatColor.DARK_AQUA + "Chainmail Leggings Tier 2");
    }

    public static ItemStack getChainmailBootsTier2() {
        return createArmorItem("chainmailboots_tier2", ChatColor.DARK_AQUA + "Chainmail Boots Tier 2");
    }

    public static ItemStack getChainmailHelmetTier3() {
        return createArmorItem("chainmailhelmet_tier3", ChatColor.DARK_AQUA + "Chainmail Helmet Tier 3");
    }

    public static ItemStack getChainmailChestplateTier3() {
        return createArmorItem("chainmailchestplate_tier3", ChatColor.DARK_AQUA + "Chainmail Chestplate Tier 3");
    }

    public static ItemStack getChainmailLeggingsTier3() {
        return createArmorItem("chainmailleggings_tier3", ChatColor.DARK_AQUA + "Chainmail Leggings Tier 3");
    }

    public static ItemStack getChainmailBootsTier3() {
        return createArmorItem("chainmailboots_tier3", ChatColor.DARK_AQUA + "Chainmail Boots Tier 3");
    }

    // Iron Armor Getters
    public static ItemStack getIronHelmet() {
        return createArmorItem("ironhelmet", ChatColor.DARK_AQUA + "Iron Helmet");
    }

    public static ItemStack getIronChestplate() {
        return createArmorItem("ironchestplate", ChatColor.DARK_AQUA + "Iron Chestplate");
    }

    public static ItemStack getIronLeggings() {
        return createArmorItem("ironleggings", ChatColor.DARK_AQUA + "Iron Leggings");
    }

    public static ItemStack getIronBoots() {
        return createArmorItem("ironboots", ChatColor.DARK_AQUA + "Iron Boots");
    }

    public static ItemStack getIronHelmetTier2() {
        return createArmorItem("ironhelmet_tier2", ChatColor.DARK_AQUA + "Iron Helmet Tier 2");
    }

    public static ItemStack getIronChestplateTier2() {
        return createArmorItem("ironchestplate_tier2", ChatColor.DARK_AQUA + "Iron Chestplate Tier 2");
    }

    public static ItemStack getIronLeggingsTier2() {
        return createArmorItem("ironleggings_tier2", ChatColor.DARK_AQUA + "Iron Leggings Tier 2");
    }

    public static ItemStack getIronBootsTier2() {
        return createArmorItem("ironboots_tier2", ChatColor.DARK_AQUA + "Iron Boots Tier 2");
    }

    public static ItemStack getIronHelmetTier3() {
        return createArmorItem("ironhelmet_tier3", ChatColor.DARK_AQUA + "Iron Helmet Tier 3");
    }

    public static ItemStack getIronChestplateTier3() {
        return createArmorItem("ironchestplate_tier3", ChatColor.DARK_AQUA + "Iron Chestplate Tier 3");
    }

    public static ItemStack getIronLeggingsTier3() {
        return createArmorItem("ironleggings_tier3", ChatColor.DARK_AQUA + "Iron Leggings Tier 3");
    }

    public static ItemStack getIronBootsTier3() {
        return createArmorItem("ironboots_tier3", ChatColor.DARK_AQUA + "Iron Boots Tier 3");
    }
    
    private static ItemStack createArmorItem(String wandKey, String displayName) {
        Wand wand = Eden.magicAPI.createWand(wandKey);
        ItemStack wandItem = wand.getItem();
        ItemMeta wandMeta = wandItem.getItemMeta();
        
        wandMeta.setDisplayName(displayName);
        wandMeta.setLocalizedName(wandKey);

        List<String> lore = wandMeta.hasLore() ? wandMeta.getLore() : new ArrayList<>();
        lore.add(ChatColor.GOLD + "Prix : " + ChatColor.BOLD + getSpecificArmorPrices(wandKey) + " points");
        wandMeta.setLore(lore);        
        
        wandItem.setItemMeta(wandMeta);
        return wandItem;
    }

    public static String getActualArmorKey(Player player) {
    	MagicAPI magicAPI = Eden.magicAPI;
    	for(ItemStack inventoryItem: player.getInventory()) {
    		if(magicAPI.isWand(inventoryItem)) {
    			String wandKey = magicAPI.getWand(inventoryItem).getItem().getItemMeta().getLocalizedName();
    			if (ARMOR_KEYS.contains(wandKey))
    				return wandKey;
    		}
    	}
    	return null;
    }
    
	public static List<String> getArmorKeys() { return ARMOR_KEYS; }
	
	public static int getSpecificArmorPrices(String wandKey) {
		initializePrices();
		return ARMOR_PRICES.getOrDefault(wandKey, 0);
	}

	public static List<String> getHelmetKeys() {
		return HELMET_KEYS;
	}

	public static List<String> getChestplateKeys() {
		return CHESTPLATE_KEYS;
	}

	public static List<String> getLeggingsKeys() {
		return LEGGINGS_KEYS;
	}

	public static List<String> getBootsKeys() {
		return BOOTS_KEYS;
	}
}
