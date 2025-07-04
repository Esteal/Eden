package fr.midey.Eden;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.file.FileConfiguration;

public class StatsOfClass {
	
	//Damage, Defense, Mobility, Stamina
	private Map<String, List<Integer>> classStats = new HashMap<>();

	public StatsOfClass(Eden plugin) {
        FileConfiguration config = plugin.getConfig();
        if (config.isConfigurationSection("classes")) {
            for (String className : config.getConfigurationSection("classes").getKeys(false)) {
                List<Integer> stats = config.getIntegerList("classes." + className);
                classStats.put(className, stats);
            }
        }
	}
	
	public Map<String, List<Integer>> getClassStats() {
		return classStats;
	}
}
