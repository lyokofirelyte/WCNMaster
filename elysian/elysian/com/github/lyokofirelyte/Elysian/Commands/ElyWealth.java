package com.github.lyokofirelyte.Elysian.Commands;

import com.github.lyokofirelyte.Elysian.Elysian;
import com.github.lyokofirelyte.Spectral.Identifiers.AutoRegister;

public class ElyWealth implements AutoRegister {

	private Elysian main;
	
	public ElyWealth(Elysian i){
		main = i;
	}
	
	/*@DivCommand(aliases = {"wealth"}, help = "/wealth <item>, /wealth total, /wealth top", desc = "Elysian Wealth Evaluator", player = true, perm = "wa.rank.dweller", min = 1)
	public void onWealth(final Player p, final String[] args){
		
		final DivinityPlayer dp = main.api.getDivPlayer(p);
		
		if (args[0].equals("top") && main.api.perms(p, "wa.staff.mod", false)){
			top(dp);
			return;
		}
		
		new Thread(
			new Runnable(){
				public void run(){
					List<String> chestLocs = dp.getList(DPI.OWNED_CHESTS);
					String lookup = args[0].equals("this") && !p.getItemInHand().getType().equals(Material.AIR) ? p.getItemInHand().getType().toString() : args[0];
					boolean all = args[0].equals("total");
					
					if (chestLocs.size() <= 0){
						dp.err("You don't have any owned chests!");
					} else {
						
						double totalAmount = 0D;
						double price = 0D;
						
						for (Material m : Material.values()){
							if (m.toString().equalsIgnoreCase(lookup) || all){
								
								for (String c : chestLocs){
									
									String[] loc = c.split(" ");
									Location chestLoc = new Location(Bukkit.getWorld(loc[0]), d(loc[1]), d(loc[2]), d(loc[3]));
									boolean cont = false;
									double amount = 0D;
									
									if (chestLoc.getBlock().getState() instanceof Chest){
										
										Chest chest = (Chest) chestLoc.getBlock().getState();
										
										if (chest.getInventory().getHolder() instanceof DoubleChest == false){
										
											for (ItemStack i : chest.getInventory().getContents()){
												if (i != null && i.getType().equals(m)){
													amount += i.getAmount();
													cont = true;
												}
											}
											
										} else if (chest.getInventory().getHolder() instanceof DoubleChest){
											
											for (ItemStack i : chest.getInventory().getContents()){
												if (i != null && i.getType().equals(m)){
													amount += (i.getAmount()/2.0);
													cont = true;
												}
											}
										}
										
									}
									
									try {
									
										if (amount > 0 && cont){
											for (String path : main.api.getDivSystem().getMarkkit().getConfigurationSection("Items").getKeys(false)){
												if (main.api.getDivSystem().getMarkkit().contains("Items." + path)){
													if (main.api.getDivSystem().getMarkkit().getInt("Items." + path + ".ID") == m.getId()){
														double init = main.api.getDivSystem().getMarkkit().getDouble("Items." + path + ".64.sellprice")/64.0D;
														price += init*amount;
														break;
													}
												}
											}
										}
									
									} catch (Exception e){}
									
									totalAmount += amount;
								}
								
								if (!all){
									break;
								}
							}
						}
						
						dp.s("We found &6" + totalAmount + " &bwhich is worth about &6" + price + "&b!");
					}
				}
			}
		).start();
	}
	
	private void top(final DivinityPlayer dp){
		
		if (dp.getLong(DPI.WEALTH_LOOKUP) <= System.currentTimeMillis()){
			
			dp.set(DPI.WEALTH_LOOKUP, System.currentTimeMillis() + 10000L);
			
			for (Player p : Bukkit.getOnlinePlayers()){
				p.closeInventory();
			}

			Map<Double, DivinityStorage> maps = new THashMap<>();
			List<Double> toRank = new ArrayList<>();
			
			try {
						
				for (DivinityStorage d : main.api.getAllPlayers()){
								
					double wealth = d.getInt(DPI.BALANCE) + 0F;
								
					for (String c : d.getList(DPI.OWNED_CHESTS)){
									
						String[] loc = c.split(" ");
						Location chestLoc = new Location(Bukkit.getWorld(loc[0]), d(loc[1]), d(loc[2]), d(loc[3]));
	
						if (chestLoc.getBlock().getState() instanceof Chest){
										
							Chest chest = (Chest) chestLoc.getBlock().getState();
									
							for (ItemStack i : chest.getInventory().getContents()){
								if (i != null && !i.getType().toString().contains("EMERALD")){
									for (String path : main.api.getDivSystem().getMarkkit().getConfigurationSection("Items").getKeys(false)){
										if (main.api.getDivSystem().getMarkkit().getInt("Items." + path + ".ID") == i.getType().getId()){
											if (main.api.getDivSystem().getMarkkit().contains("Items." + path + ".64.sellprice")){
												if (main.api.getDivSystem().getMarkkit().getDouble("Items." + path + ".64.sellprice") < 200000){
													double init = main.api.getDivSystem().getMarkkit().getDouble("Items." + path + ".64.sellprice")/64.0D;
													wealth += chest.getInventory().getHolder() instanceof DoubleChest ? init*(i.getAmount()/2) : init*i.getAmount();
												}
											}
											break;
										}
									}
								}
							}
						}
					}
							
					maps.put(wealth, d);
					toRank.add(wealth);
				}
				
			} catch (Exception e){}
						
			Collections.sort(toRank);
			Collections.reverse(toRank);
						
			double total = 0;
						
			for (double i : toRank){
				total+= i;
			}
						
			dp.s("&3Top 10 Wealthiest Players (items + shinies)");
			dp.s("&6Server Total&f: &6" + total);
						
			for (int i = 0; i < (toRank.size() >= 10 ? 10 : toRank.size()); i++){
				dp.s(maps.get(toRank.get(i)).getStr(DPI.DISPLAY_NAME) + "&f: &6" + toRank.get(i));
			}
			
		} else {
			dp.err("This command is on cooldown (it is very server intensive)");
		}
	}
	
	private double d(String s){
		return Double.parseDouble(s);
	}*/
}