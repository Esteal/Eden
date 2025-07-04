package fr.midey.Eden;

import java.util.Arrays;
import java.util.List;

public class PlayerData {
	
	private String classe;
	private List<Integer> stats;
	private boolean statu;
	private String name;
	private int points = 0;
	private Long timeToSneak;
	private Integer priceOfStats;

	public PlayerData(String name) {
		this.setName(name);
	}
	public String getClasse() { return classe; }
	public void setClasse(String classe) { 
		setStats(Eden.getClassStats().getOrDefault(classe, Arrays.asList(0, 0, 0, 0)));
		this.classe = classe; 
	}
	
	public int getSpecificStat(String string) {	
		switch(string) {
			case "ATTACK_DAMAGE":
				return getStats().get(0);
			case "ARMOR":
				return getStats().get(1);
			case "MOVEMENT_SPEED":
				return getStats().get(2);
			case "MANA":
				return getStats().get(3);
			case "CONSTITUTION":
				return getStats().get(4);
			default:
				return 0;
		}
	}
	
	public void setSpecificStat(String string, int x) {	
		switch(string) {
			case "ATTACK_DAMAGE":
				 getStats().set(0, x);
				 break;
			case "ARMOR":
				 getStats().set(1, x);
				 break;
			case "MOVEMENT_SPEED":
				 getStats().set(2, x);
				 break;
			case "MANA":
				 getStats().set(3, x);
				 break;
			case "CONSTITUTION":
				 getStats().set(4, x);
				 break;
			default:
				break;
		}
	}
	
	public boolean getStatu() { return statu; }
	public void setStatu(boolean statu) { this.statu = statu; }
	
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	public int getPoints() {  return points; }
	public void setPoints(int points) { this.points = points;}
	public List<Integer> getStats() { return stats; }
	public void setStats(List<Integer> stats) { this.stats = stats; }
	public Long getTimeToSneak() {
		return timeToSneak;
	}
	public void setTimeToSneak(Long timeToSneak) { this.timeToSneak = timeToSneak; }
	public Integer getPriceOfStats() { return priceOfStats; }
	public void setPriceOfStats(Integer priceOfStats) { this.priceOfStats = priceOfStats; }
}
