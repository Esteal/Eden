package fr.midey.Eden.Shop;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.elmakers.mine.bukkit.api.wand.Wand;

import fr.midey.Eden.Eden;

public class WeaponShopItems {
	
	public final static List<Integer> basicList = Arrays.asList(0, 0, 0, 0, 0);

    public static ItemStack getDagger() {
        return createMagicItem("dagger", ChatColor.DARK_AQUA + "Assassin", genererStats(Eden.getClassStats().get("assassin")) ," ","§6Maître de l'évasion §7(Passif)§6 : §d20§e% §fde chance d'§desquiver un coup ", "§fet d'obtenir §dinvisibilité §fpendant §d4§es");
    }
    
    public static ItemStack getHammer() {
        return createMagicItem("hammer", ChatColor.DARK_AQUA + "Berserker", genererStats(Eden.getClassStats().getOrDefault("berserker", basicList)), " ","§6Rage §7(Passif)§6 : §faugmente les §ddégâts de mêlée§f de §d70§e%§f lorsque la §dsanté","§fest inférieur à §d30§e%");
    }
    
    public static ItemStack getMasterSword() {
        return createMagicItem("swordmaster", ChatColor.AQUA + "Maître épéiste", genererStats(Eden.getClassStats().getOrDefault("maitre_epeiste", basicList)), " ","§6Maîtrise de l'épée §7(Passif)§6 : §d5§e% §fde chance de §ddoubler §fles dégâts d'un coup d'épée");
    }
    
    public static ItemStack getSamouraiSword() {
        return createMagicItem("samouraisword", ChatColor.AQUA + "Samouraï", genererStats(Eden.getClassStats().getOrDefault("samourai", basicList)), " ","§6Contre attaque §7(Passif)§6 : §d15§e% §fde chance de §drenvoyer §fles dégâts d'un coup d'épée");
    }
    
    public static ItemStack getDemonSword() {
        return createMagicItem("demonsword", ChatColor.DARK_RED + "Bretteur démoniaque", genererStats(Eden.getClassStats().getOrDefault("bretteur_demoniaque", basicList)), " ","§6Sacrifice §7(Passif)§6 : §fGagne §d2 §ecoeurs§f supplémentaire à chaque §ckill §7(disparaît à la mort)");
    }
    
    public static ItemStack getPaladinSword() {
        return createMagicItem("paladinsword", ChatColor.AQUA + "Paladin", genererStats(Eden.getClassStats().getOrDefault("paladin", basicList)), " ","§6Aura divine §7(Passif)§6 : §d5§e% §fde chance d'obtenir §drégénération §eIII §fpendant §d5§es","§florsqu'un dégât est subi");
    }
    
    public static ItemStack getElementalWand() {
        return createMagicItem("elementalwand", ChatColor.DARK_AQUA + "Mage élémentaire", genererStats(Eden.getClassStats().getOrDefault("mage_elementaire", basicList)), " ","§6Bénédiction du mana §7(Passif)§6 :§f vos sorts passent à travers la résistance des armures");
    }
    
    public static ItemStack getBlackWand() {
        return createMagicItem("blackwand", ChatColor.DARK_RED + "Mage NOIR", genererStats(Eden.getClassStats().getOrDefault("mage_noir", basicList)), " ","§6Énergie obscure §7(Passif)§6 : §d15§e% §fde chance d'infliger §cwither poison§f à la cible");
    }
    
    public static ItemStack getDeathWand() {
        return createMagicItem("deathwand", ChatColor.DARK_RED + "Nécromancien", genererStats(Eden.getClassStats().getOrDefault("necromancien", basicList)), " ","§6Maître des morts §7(Passif)§6 : §ffait apparaître une §dhorde de morts§f lorsqu'un ","§fjoueur §cmeurt§f à moins de §d10 §emètres");
    }
    
    public static ItemStack getStick() {
        return createMagicItem("stick", ChatColor.AQUA + "Moine shaoline", genererStats(Eden.getClassStats().getOrDefault("moine", basicList)), " ","§6Équilibre intérieur §7(Passif)§6 : §d35§e% §fde chance d'obtenir §drapidité §eII §fpendant §d5§es","§florsqu'un dégât est subi");
    }
    
    public static ItemStack getGauntlet() {
        return createMagicItem("gauntlet", ChatColor.DARK_AQUA + "Artiste martial", genererStats(Eden.getClassStats().getOrDefault("artiste_martial", basicList)), " ","§6Étourdissement §7(Passif)§6 : §d5§e% §fde chance d'étourdir l'ennemi pendant §d2 §esecondes");
    }
    
    public static ItemStack getLeftGauntlet() {
        return createMagicItem("gauntletleft", ChatColor.DARK_AQUA + "Artiste martial", genererStats(Eden.getClassStats().getOrDefault("artiste_martial", basicList)), " ","§6Étourdissement §7(Passif)§6 : §d5§e% §fde chance d'étourdir l'ennemi pendant §d2 §esecondes");
    }

    public static String[] genererStats(List<Integer> stats) {
        return new String[]{
            "§cForce : §6" + getStars(stats.get(0), "ATTACK_DAMAGE"),
            "§aDéfense : §6" + getStars(stats.get(1), "ARMOR"),
            "§9Mobilité : §6" + getStars(stats.get(2), "MOVEMENT_SPEED"),
            "§eMana : §6" + getStars(stats.get(3), "MANA"),
            "§dConstitution : §6" + getStars(stats.get(4), "CONSTITUTION"),
            "    ",
            "§fTotal : §6" + (stats.get(4) + stats.get(2) + stats.get(3) + stats.get(1) + stats.get(0)) + " ★"
        };
    }

    public static String getStars(int rating, String maxRatingString) {
        StringBuilder stars = new StringBuilder();
        int size = Eden.MAIN.playersModifiers.get(maxRatingString).size() - 1;
        int number = 0;
        while(rating >= 5) {
        	stars.append("§6✪");
        	rating-=5;
        	number+=5;
        }
        if(!stars.isEmpty())
        	stars.append(" ");
        for (int i = 0; i < rating; i++) {
            stars.append("§6★");
            number++;
        }
        for(int i = rating; i < 5; i++) {
            if(number >= size )
            	break;
        	stars.append("§6☆");
        }
        return stars.toString();
    }
    
    private static ItemStack createMagicItem(String wandKey, String displayName, String[] stats, String... description) {
        Wand wand = Eden.magicAPI.createWand(wandKey);
        ItemStack wandItem = wand.getItem();
        ItemMeta wandMeta = wandItem.getItemMeta();
        wandMeta.setDisplayName(displayName);
        wandMeta.setLocalizedName(wandKey);
        List<String> lore = wandMeta.hasLore() ? wandMeta.getLore() : new ArrayList<>();
        for(String stringStats : stats)
        	lore.add(stringStats);
        for(String stringDesc : description) 
        	lore.add(stringDesc);
        wandMeta.setLore(lore);              
        wandItem.setItemMeta(wandMeta);
        return wandItem;
    }

	public static ItemStack getShield() {
		return createMagicItem("bouclier", "bouclier", new String[] {}, "");
	}
}
