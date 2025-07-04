package fr.midey.Eden;


import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;

public class StartCommand implements CommandExecutor {
    //private final GameManager gameManager;
	private Eden plugin;

    public StartCommand(GameManager gameManager, Eden plugin) {
        //this.gameManager = gameManager;
       this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length == 1 && args[0].equalsIgnoreCase("weapon")) {
                openWeaponModelInventory(player, 18001, 18054);
                return true;
            } else if (args.length == 1 && args[0].equalsIgnoreCase("weapon2")) {
                openWeaponModelInventory(player, 18055, 18109);
                return true;
            } else if (command.getName().equalsIgnoreCase("focusreload")) {
                reloadPlugin();
                return true;
            } else {
            	if(command.getName().equalsIgnoreCase("model")) {
            		player.sendMessage(ChatColor.RED + "Usage: /model weapon");
                	player.sendMessage(ChatColor.RED + "Usage: /model weapon2");
                } else {
                	player.sendMessage(ChatColor.RED + "Usage: /focusreload");
                }
            	return false;
            }
        } else {
            sender.sendMessage("This command can only be used by a player.");
            return false;
        }
    }

    public void reloadPlugin() {
		PluginManager pluginManager = plugin.getServer().getPluginManager();
		
		pluginManager.disablePlugin(plugin);
		pluginManager.enablePlugin(plugin);
		Bukkit.broadcastMessage(ChatColor.GREEN + "Plugin rechargé");
	}

	private void openWeaponModelInventory(Player player, int start, int end) {
        Inventory inventory = Bukkit.createInventory(player, 54, "Select a Weapon Model");

        for (int i = start; i <= end; i++) {
            ItemStack item = new ItemStack(Material.NETHERITE_SWORD);
            ItemMeta meta = item.getItemMeta();
            meta.setCustomModelData(i);
            meta.setDisplayName(ChatColor.RESET + "Netherite Sword " + (i - 18000));
            item.setItemMeta(meta);
            inventory.addItem(item);
        }

        player.openInventory(inventory);
    }
}
