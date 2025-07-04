package fr.midey.Eden;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class HandleClass {

	public static void handlePassifs(Player player, String playerClass, EntityDamageByEntityEvent event) {
		switch(playerClass) {
			case "assassin":
				handleAssassinPassif(player, event);
				break;
			case "paladin":
				handlePaladinPassif(player, event);
				break;
			case "samourai":
				handleSamouraiPassif(player, event);
				break;
			case "moine":
				handleMoinePassif(player, event);
				break;
			case "maitre_epeiste":
				handleMaitreEpeistePassif(player, event);
				break;
			case "mage_noir":
				handleMageNoirPassif(player, event);
				break;
			case "artiste_martial":
				handleArtistMartialePassif(player, event);
				break;
			case "berserker":
				handleBerserkerPassif(player, event);
				break;
			default:
				break;
		}
		
	}

	private static void handleBerserkerPassif(Player damager, EntityDamageByEntityEvent event) {


		if(isDamagerToDamager(damager, event)) {
			return;
		}

		double maxHealth = damager.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();
		if( damager.getHealth() <= (maxHealth * 0.3)) {
			event.setDamage(event.getDamage() * 1.7);
		}
		
	}

	private static void handleArtistMartialePassif(Player damager, EntityDamageByEntityEvent event) {
		if(isDamagerToDamager(damager, event)) return;
		if (new Random().nextInt(100) < 5) {
            damager.sendMessage(ChatColor.GREEN + "Étourdissement activé !");
            damager.playSound(damager, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
            if(event.getEntity() instanceof LivingEntity) {
                ((LivingEntity)event.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 2 * 20, 10));
                ((LivingEntity)event.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 2 * 20, 10));
                ((LivingEntity)event.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 2 * 20, 10));
            }
		}
	}

	private static void handleMageNoirPassif(Player damager, EntityDamageByEntityEvent event) {
		if(isDamagerToDamager(damager, event)) return;
		if (new Random().nextInt(100) < 15) {
            if(event.getEntity() instanceof LivingEntity) {
                ((LivingEntity)event.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.POISON, 5 * 20, 2));
            }
            damager.sendMessage(ChatColor.GREEN + "Énergie obscure activé !");
            damager.playSound(damager, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
		}
	}

	private static void handleMaitreEpeistePassif(Player damager, EntityDamageByEntityEvent event) {
		if(isDamagerToDamager(damager, event)) return;
		if (new Random().nextInt(100) < 5) {
			event.setDamage(event.getDamage()*2);
            damager.sendMessage(ChatColor.GREEN + "Maîtrise de l'épée activé !");
            damager.playSound(damager, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
		}
	}

	private static void handleSamouraiPassif(Player player, EntityDamageByEntityEvent event) {
		if(isPlayerToPlayer(player, event) || isPlayerToCausingEntity(player, event)) return;
        if(event.getDamager() instanceof LivingEntity) {
			if (new Random().nextInt(100) < 15) {
	            player.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
	            player.sendMessage(ChatColor.GREEN + "Contre attaque activé !");
	            LivingEntity livingEntityDamager = (LivingEntity) event.getDamager();
	            livingEntityDamager.damage(event.getDamage());
	            event.setCancelled(true);
	            if(livingEntityDamager instanceof Player)
	            	((Player) livingEntityDamager).sendMessage(ChatColor.RED + "Vous venez de vous manger la contre attaque de " + ChatColor.GOLD + player.getName());
			}
		}
	}

	private static void handlePaladinPassif(Player player, EntityDamageByEntityEvent event) {
		if(isPlayerToPlayer(player, event) || isPlayerToCausingEntity(player, event)) return;
        if (new Random().nextInt(100) < 5) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 5 * 20, 2));
            player.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
            player.sendMessage(ChatColor.GREEN + "Aura divine activé !");
        }
	}
	
	private static void handleMoinePassif(Player player, EntityDamageByEntityEvent event) {
		if(isPlayerToPlayer(player, event) || isPlayerToCausingEntity(player, event)) return;
        if (new Random().nextInt(100) < 5) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 5 * 20, 2));
            player.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
            player.sendMessage(ChatColor.GREEN + "Équilibre intérieur activé !");
        }
	}

	private static void handleAssassinPassif(Player player, EntityDamageByEntityEvent event) {
		if(isPlayerToPlayer(player, event) || isPlayerToCausingEntity(player, event)) return;
		if(player.hasPotionEffect(PotionEffectType.INVISIBILITY)) return;
        if (new Random().nextInt(100) < 20) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 4 * 20, 250));
            event.setCancelled(true);
            vanishPlayer(player, 4);
            player.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
            player.sendMessage(ChatColor.GREEN + "Maître de l'évasion activé !");
        }
	}
	
	 private static void vanishPlayer(Player player, int seconds) {
	        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
	            if (!onlinePlayer.equals(player)) {
	                onlinePlayer.hidePlayer(Eden.MAIN, player);
	            }
	        }
	        Bukkit.getScheduler().runTaskLater(Eden.MAIN, () -> {
	            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
	                if (!onlinePlayer.equals(player)) {
	                    onlinePlayer.showPlayer(Eden.MAIN, player);
	                }
	            }
	            player.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
	            player.sendMessage(ChatColor.GREEN + "Vous êtes réapparu !");
	        }, seconds * 20L);
	    }
	
	 private static boolean isDamagerToDamager(Player damager, EntityDamageByEntityEvent event) {
	    	if(event.getEntity() instanceof Player) {
	    		if(event.getEntity() == damager)
	    		{
	    			return true;
	    		}
	    	}
			return false;
	 }
	 
	 private static boolean isPlayerToPlayer(Player player, EntityDamageByEntityEvent event) {
	    	if(event.getDamager() instanceof Player) {
	    		if(event.getDamager() == player)
	    		{
	    			return true;
	    		}
	    	}
			return false;
	 }
	 
	 private static boolean isPlayerToCausingEntity(Player player, EntityDamageByEntityEvent event) {
		 if(event.getDamageSource().getCausingEntity() instanceof Player) {
			 if (event.getDamageSource().getCausingEntity() == player) {
				 return true;
			 }
		 }
		 return false;
	 }
}
