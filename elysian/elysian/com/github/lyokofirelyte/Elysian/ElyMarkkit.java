package com.github.lyokofirelyte.Elysian;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import net.minecraft.util.gnu.trove.map.hash.THashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.github.lyokofirelyte.Spectral.DataTypes.DPI;
import com.github.lyokofirelyte.Spectral.Identifiers.AutoRegister;
import com.github.lyokofirelyte.Spectral.StorageSystems.DivinityPlayer;
import com.github.lyokofirelyte.Spectral.StorageSystems.DivinitySystem;

/**
 * 
 * @author msnijder30
 *
 */

public class ElyMarkkit implements Listener, AutoRegister {

	private Elysian main;
	private DivinitySystem system;
	private Map<String, Inventory> inventory = new THashMap<String, Inventory>();
	private Map<String, Inventory> playershop = new THashMap<String, Inventory>();
	private Map<String, String> invName = new THashMap<String, String>();
	private Map<String, Integer> totalPrice = new THashMap<String, Integer>();
	private Map<String, Integer> showPrice = new THashMap<String, Integer>();
	private Map<String, Location> chestLocation = new THashMap<String, Location>();
	public ElyMarkkit(Elysian i){
		main = i;
		system = main.api.getDivSystem();
	}

	@EventHandler
	public void onSignChange(SignChangeEvent e) {
			
		Player p = e.getPlayer();
			
		if (main.api.perms(p, "wa.staff.mod2", true) && e.getLine(0).equalsIgnoreCase("markkit") && e.getLine(1) != null && !e.getLine(1).equals("")){
			e.setLine(0, main.AS("&dWC &5Markkit"));
			e.setLine(1, main.AS("&f" + e.getLine(1)));
		} else if(main.api.perms(p, "wa.staff.intern", true) && e.getLine(0).equalsIgnoreCase("playershop") && e.getLine(1) != null && !e.getLine(1).equals("")){
			org.bukkit.material.Sign sign = (org.bukkit.material.Sign) e.getBlock().getState().getData();
			Block attached = e.getBlock().getRelative(sign.getAttachedFace());
			if(attached.getType() == Material.CHEST) {
				e.setLine(0, main.AS("&3Playershop"));
				e.setLine(1, main.AS("&f" + e.getLine(1)));
			}
		}else if (e.getLine(0).equalsIgnoreCase("markkit")){
			e.setLine(0, main.AS("&4INVALID!"));
			e.setLine(1, main.AS("&cWE DIDN'T"));
			e.setLine(2, main.AS("&cLISTEN! D:"));
		} else {
			for (int x = 0; x < 4; x++){
				if (e.getLine(x) != null){
					e.setLine(x, main.AS(e.getLine(x)));
				}
			}
		}
	}
		
	@EventHandler
	public void onClick(final InventoryClickEvent e){
		
		Player p = (Player) e.getWhoClicked();

		List<Integer> sellCart = Arrays.asList(0, 1, 2, 9, 10, 11, 18, 19, 20, 27, 28, 29, 36, 37, 38);
		List<Integer> buyCart = Arrays.asList(6, 7, 8, 15, 16, 17, 24, 25, 26, 33, 34, 35, 42, 43, 44);
		List<Integer> itemSlot = Arrays.asList(4, 13, 22, 31, 40, 49);
		List<Integer> buyCartPlayerShop = Arrays.asList(36, 37, 38, 39, 40, 41, 42, 43);
		List<Integer> forSale = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26);
		
		
		if(e.getInventory().getName().contains("Playershop")){
			e.setCancelled(true);
			String clicker = e.getWhoClicked().getName();
			
			if(forSale.contains(e.getRawSlot())){
				for(int i : buyCartPlayerShop){
					if(e.getInventory().getItem(i) == null){
						ItemStack clicked = new ItemStack(e.getCurrentItem().getType());
						clicked.setAmount(1);
						e.getInventory().setItem(i, clicked);
						return;
					}else if(e.getInventory().getItem(i).getType() == e.getCurrentItem().getType() && e.getInventory().getItem(i).getAmount() == 64 && i + 1 < 44){
						ItemStack clicked = new ItemStack(e.getCurrentItem().getType());
						clicked.setAmount(1);
						e.getInventory().setItem(i + 1, clicked);
						return;
					}else if(e.getInventory().getItem(i).getType() == e.getCurrentItem().getType()){
					
						ItemStack clicked = new ItemStack(e.getCurrentItem().getType());
						clicked.setAmount(e.getInventory().getItem(i).getAmount() + 1);
						e.getInventory().setItem(i, clicked);
						return;
					}
				}
			}else if(e.getRawSlot() == 44){
				int total = 0;
				for(int i : buyCartPlayerShop){
					if(e.getInventory().getItem(i) != null && e.getInventory().getItem(i).getType() != Material.AIR){
						int money = system.getMarkkit().getInt("playershop." + e.getInventory().getItem(44).getItemMeta().getDisplayName().split(" ")[0] + "." + e.getInventory().getItem(i).getTypeId() + "." + e.getInventory().getItem(i).getDurability());
						total = total + money;
						Chest c = (Chest) e.getWhoClicked().getLocation().getWorld().getBlockAt(chestLocation.get(e.getInventory().getItem(44).getItemMeta().getDisplayName().split(" ")[0])).getState();
						c.getInventory().removeItem(new ItemStack(e.getInventory().getItem(i).getType(), 1));
						
					}
				}
				//check if he has enough money todo!
				DivinityPlayer dp = main.api.getDivPlayer(p);
				dp.set(DPI.BALANCE, dp.getInt(DPI.BALANCE) - total);
				
				if(main.api.doesPartialPlayerExist(e.getInventory().getItem(44).getItemMeta().getDisplayName().split(" ")[0])){
					DivinityPlayer dp2 = main.api.getDivPlayer(e.getInventory().getItem(44).getItemMeta().getDisplayName().split(" ")[0]);
					dp.set(DPI.BALANCE, dp.getInt(DPI.BALANCE) + total);
				}
				
				main.s(p, total + " shinies were taken from your account!");
				
			}else if(e.getInventory().getItem(e.getRawSlot()) != null && buyCartPlayerShop.contains(e.getRawSlot())){
				e.getInventory().setItem(e.getRawSlot(), new ItemStack(Material.AIR));
			}
		}else if(e.getInventory().getName().contains("items stocked") || e.getInventory().getName().contains("Double price!")){
			e.setCancelled(true);
			String name = invName.get(e.getWhoClicked().getName());
			ElyMarkkitItem mi = new ElyMarkkitItem(main, e.getInventory().getItem(4).getType(), e.getInventory().getItem(4).getDurability());

			if(e.getCurrentItem() != null){
				if(!sellCart.contains(e.getRawSlot()) && !buyCart.contains(e.getRawSlot()) &&!itemSlot.contains(e.getRawSlot()) && e.getCurrentItem().getTypeId() == mi.getMaterialID() && e.getCurrentItem().getDurability() == mi.getDurability()){
					ItemStack clicked = e.getCurrentItem();
					for (Integer i : sellCart){
						if(!(Arrays.asList(47, 51, 46, 52, 53, 45, 27, 28, 29, 30, 31, 32, 33, 34, 35).contains(e.getRawSlot()))){
							if(e.getInventory().getItem(i) == null){
								e.getInventory().setItem(i, clicked);
								p.getInventory().setItem(e.getSlot(), new ItemStack(Material.AIR));
								p.updateInventory();
								break;
							} else if(e.getInventory().getItem(i).getAmount() <= 64 - clicked.getAmount()){
								ItemStack calculated = new ItemStack(clicked.getType(), e.getInventory().getItem(i).getAmount() + clicked.getAmount(), (short) clicked.getDurability());
								e.getInventory().setItem(i, calculated);
								p.getInventory().setItem(e.getSlot(), new ItemStack(Material.AIR));
								p.updateInventory();
								break;
							}
						}
					}
				}
			}
				
			switch(e.getRawSlot()){
				
				//the buying area aka shoppig cart
			case 6: case 7: case 8: case 15: case 16: case 17: case 24: case 25: case 26: case 33: case 34: case 35: case 42: case 43: case 44:
		
				if(e.getInventory().getItem(e.getRawSlot()) != null){
					e.getInventory().setItem(e.getRawSlot(), new ItemStack(Material.AIR));
				}
				
			break;
				
			case 53:
					
				showPrice.put(e.getWhoClicked().getName(), 0);

				for (Integer i : buyCart){
					if(e.getInventory().getItem(i) != null){
						if(mi.isSellDoubled()){
							int fullPrice = mi.getStackBuyPrice();
							int currentPrice = mi.getBuyPrice(e.getInventory().getItem(i).getAmount());
							showPrice.put(e.getWhoClicked().getName(), showPrice.get(e.getWhoClicked().getName()) + (currentPrice*2));
						}else{
							int fullPrice = mi.getStackBuyPrice();
							int currentPrice = mi.getBuyPrice(e.getInventory().getItem(i).getAmount());
							showPrice.put(e.getWhoClicked().getName(), showPrice.get(e.getWhoClicked().getName()) + currentPrice);
						}
					}
				}
					
				ItemStack calculateRight = new ItemStack(Material.MUSHROOM_SOUP, 1);
				ItemMeta rightMeta = calculateRight.getItemMeta();
				rightMeta.setDisplayName(ChatColor.RED + "Click here to calculate the price!");
				rightMeta.setLore(Arrays.asList(ChatColor.GREEN + "Price: " + showPrice.get(e.getWhoClicked().getName())));
				calculateRight.setItemMeta(rightMeta);

				e.getInventory().setItem(53, calculateRight);
					
				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(main, new Runnable(){ public void run(){
							
					ItemStack calculateRight = new ItemStack(Material.BOWL, 1);
					ItemMeta rightMeta = calculateRight.getItemMeta();
					rightMeta.setDisplayName(ChatColor.RED + "Click here to calculate the price!");
					rightMeta.setLore(Arrays.asList(ChatColor.GREEN + "Price: "));
					calculateRight.setItemMeta(rightMeta);
					e.getInventory().setItem(53, calculateRight);
					
				}}, 100L);
				
			break;
				
				
			case 45:
				
				showPrice.put(e.getWhoClicked().getName(), 0);

				for(Integer i : sellCart){
					if(e.getInventory().getItem(i) != null){
						int fullPrice = mi.getStackBuyPrice();
						int currentPrice = mi.getBuyPrice(e.getInventory().getItem(i).getAmount());
						showPrice.put(e.getWhoClicked().getName(), showPrice.get(e.getWhoClicked().getName()) + currentPrice);
					}
				}
					
				ItemStack calculateLeft = new ItemStack(Material.MUSHROOM_SOUP, 1);
				ItemMeta leftMeta = calculateLeft.getItemMeta();
				leftMeta.setDisplayName(ChatColor.RED + "Click here to calculate the price!");
				leftMeta.setLore(Arrays.asList(ChatColor.GREEN + "Price: " + showPrice.get(e.getWhoClicked().getName())/2));
				calculateLeft.setItemMeta(leftMeta);
					
				e.getInventory().setItem(45, calculateLeft);
					
				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(main, new Runnable(){ public void run(){
							
					ItemStack calculateLeft = new ItemStack(Material.BOWL, 1);
					ItemMeta leftMeta = calculateLeft.getItemMeta();
					leftMeta.setDisplayName(ChatColor.RED + "Click here to calculate the price!");
					leftMeta.setLore(Arrays.asList(ChatColor.GREEN + "Price: "));
					calculateLeft.setItemMeta(leftMeta);
					e.getInventory().setItem(45, calculateLeft);
					
				}}, 100L);
				
			break;
					
			case 46:
					//sell button
				int itemCount = 0;
				int totalAmount = 0;
				Material mat = Material.AIR;
				for(Integer i : sellCart){
					if(e.getInventory().getItem(i) != null){
						itemCount = itemCount + e.getInventory().getItem(i).getAmount();
						int fullPrice = mi.getStackSellPrice();
						int currentPrice = mi.getSellPrice(e.getInventory().getItem(i).getAmount());
						if(totalPrice.get(e.getWhoClicked().getName()) == null){
							totalPrice.put(e.getWhoClicked().getName(), 0);
						}
						
						totalAmount = totalAmount + e.getInventory().getItem(i).getAmount();
						mat = e.getInventory().getItem(i).getType();
						
						totalPrice.put(e.getWhoClicked().getName(), totalPrice.get(e.getWhoClicked().getName()) + currentPrice);
						e.getInventory().setItem(i, new ItemStack(Material.AIR));
					}
				}
				
				DivinityPlayer dp = main.api.getDivPlayer(p);
				
				if(totalAmount > 0){
					Date d = new Date();
					dp.getList(DPI.MARKKIT_LOG).add(d.getDate() + "/" + d.getMonth() + " " + d.getHours() + ":" + d.getMinutes() + " &bSold &6" + totalAmount + "&b of &6" + mat.name() + " &bworth &6" + totalPrice.get(e.getWhoClicked().getName()) + " &bshinies");
				}
				
				dp.set(DPI.BALANCE, dp.getInt(DPI.BALANCE) + totalPrice.get(e.getWhoClicked().getName()));
				main.s((Player)e.getWhoClicked(), totalPrice.get(e.getWhoClicked().getName()) + " was added to your account!");
				totalPrice.put(e.getWhoClicked().getName(), 0);
				mi.setInStock(mi.getInStock() + itemCount);
				if(mi.getInStock() <= 0){
					mi.setSellDoubled(true);
				}else{
					mi.setSellDoubled(false);
				}

				loadMarkkitInventory((Player)e.getWhoClicked(), name);
			break;
					
			case 47:
				
				for(Integer i : sellCart){
					if(e.getInventory().getItem(i) != null){
						p.getLocation().getWorld().dropItemNaturally(p.getLocation(), e.getInventory().getItem(i));
					}
				e.getInventory().setItem(i, new ItemStack(Material.AIR));
			}
				
			break;
					
			case 51:
					
				for (int i : buyCart){
						e.getInventory().setItem(i, new ItemStack(Material.AIR));
				}
					
				break;
					
				case 52:
					//buy button
					int itemC = 0;
					for(int i : buyCart){
							if(e.getInventory().getItem(i) != null){
								itemC = itemC + e.getInventory().getItem(i).getAmount();
								int price = mi.getBuyPrice(e.getInventory().getItem(i).getAmount());
								if(totalPrice.get(e.getWhoClicked().getName()) == null){
									totalPrice.put(e.getWhoClicked().getName(), 0);
								}
								totalPrice.put(e.getWhoClicked().getName(), totalPrice.get(e.getWhoClicked().getName()) + price);
							}
							if(i == 44){
								dp = main.api.getDivPlayer(p);
								if (dp.getInt(DPI.BALANCE) >= totalPrice.get(e.getWhoClicked().getName())){
									if(mi.isSellDoubled()){
										if(dp.getInt(DPI.BALANCE) >= totalPrice.get(e.getWhoClicked().getName())*2){
											dp.set(DPI.BALANCE, dp.getInt(DPI.BALANCE) - totalPrice.get(e.getWhoClicked().getName())*2);
											main.s((Player)e.getWhoClicked(), totalPrice.get(e.getWhoClicked().getName())*2 + " was taken from your account!");
										}else{
											main.s((Player)e.getWhoClicked(), "You do not have enough money");
										}

									}else{
										dp.set(DPI.BALANCE, dp.getInt(DPI.BALANCE) - totalPrice.get(e.getWhoClicked().getName()));
										main.s((Player)e.getWhoClicked(), totalPrice.get(e.getWhoClicked().getName()) + " was taken from your account!");
									}
									totalPrice.put(e.getWhoClicked().getName(), 0);
									for(int slot : new int[]{6, 7, 8, 15, 16, 17, 24, 25, 26, 33, 34, 35, 42, 43, 44}){
											if(e.getInventory().getItem(slot) != null){
												ItemStack item = new ItemStack(e.getInventory().getItem(slot).getType());
												item.setAmount(e.getInventory().getItem(slot).getAmount());
												item.setDurability(e.getInventory().getItem(slot).getDurability());
												if(p.getInventory().firstEmpty() == -1){
													p.getWorld().dropItemNaturally(p.getLocation(), item);
												}else{
													p.getInventory().addItem(item);
												}	
											}
											e.getInventory().setItem(slot, new ItemStack(Material.AIR));
										}
									mi.setInStock(mi.getInStock() - itemC);
									if(mi.getInStock() <= 0){
										mi.setSellDoubled(true);
									}else{
										mi.setSellDoubled(false);
									}
									loadMarkkitInventory((Player)e.getWhoClicked(), name);
								} else {
									main.s(p, "You do not have enough money!");
									totalPrice.put(e.getWhoClicked().getName(), 0);
								}
						}
					}
				break;
					
				//the item you have to click
				case 4: case 13: case 22: case 31: case 40: case 49:
					ItemStack clicked = e.getInventory().getItem(e.getRawSlot());
					int count = 0;
					for (Integer i : buyCart){
						if(e.getInventory().getItem(i) != null){
							count = count + e.getInventory().getItem(i).getAmount();
						}
					}
			
					for (Integer i : buyCart){
						if (e.getInventory().getItem(i) == null){
							if(mi.isSellDoubled()){
								e.getInventory().setItem(i, clicked);
							}else{
								if(mi.getInStock() > count + e.getCurrentItem().getAmount() - 1){
									e.getInventory().setItem(i, clicked);
								}else{
									main.s(p, "There is no more playerstock!, please buy this and re-open to buy moar.");
								}
							}
							p.updateInventory();
							break;
						}
					}
					break;
				}
			}
		}
		@EventHandler
		public void onClickyTheSign(PlayerInteractEvent e) {
			

			if (e.getAction() == Action.RIGHT_CLICK_BLOCK){
				
				if (e.getClickedBlock().getState() instanceof Sign){
					Sign sign = (Sign) e.getClickedBlock().getState();
					
					if (sign.getLine(0).equals(main.AS("&dWC &5Markkit")) || sign.getLine(0).equals(main.AS("&bEly &3Markkit"))){
						if(sign.getLine(0).equals("§dWC §5Markkit")){
							sign.setLine(0, main.AS("§bEly §3Markkit"));
							sign.update();
						}
						
						if (e.getPlayer().getItemInHand() != null && e.getPlayer().getItemInHand().getType() != Material.AIR){
							main.s(e.getPlayer(), "You must use your hand to activate the sign.");
							return;
						}
						String name = sign.getLine(1).replace("§f", "");
						invName.put(e.getPlayer().getName(), name);
						loadMarkkitInventory(e.getPlayer(), name);
					}else if(sign.getLine(0).equals(main.AS("&3Playershop"))){
						
						Block attached = e.getClickedBlock().getRelative(e.getBlockFace().getOppositeFace());
						if(attached.getType() == Material.CHEST) {
							if (e.getPlayer().getItemInHand() != null && e.getPlayer().getItemInHand().getType() != Material.AIR){
								main.s(e.getPlayer(), "You must use your hand to activate the sign.");
								return;
							}
							Chest c = (Chest) attached.getState();
							String name = sign.getLine(1).replace("§f", "");
							loadPlayerShopInventory(e.getPlayer(), c.getInventory(), name);
							chestLocation.put(name, new Location(c.getWorld(), c.getLocation().getBlockX(), c.getLocation().getBlockY(), c.getLocation().getBlockZ()));
						}
					}
				}
			}
		}
		
		@EventHandler
		public void onClose(InventoryCloseEvent e){
			Player p = (Player) e.getPlayer();
			List<Integer> sellCart = Arrays.asList(0, 1, 2, 9, 10, 11, 18, 19, 20, 27, 28, 29, 36, 37, 38);
			if(e.getInventory().getName().contains("Double price!") || e.getInventory().getName().contains("items stocked")){
				for(Integer i : sellCart){
					if(e.getInventory().getItem(i) != null){
						p.getLocation().getWorld().dropItemNaturally(p.getLocation(), e.getInventory().getItem(i));
					}
					e.getInventory().setItem(i, new ItemStack(Material.AIR));
				}
			}
		}

		public void loadPlayerShopInventory(Player buyer, Inventory passed, String owner){
			Inventory temp = Bukkit.createInventory(null, 45, main.AS("&3Playershop"));
			List<Integer> divider = Arrays.asList(27, 28, 29, 30, 31, 32, 33, 34, 35);
			for(ItemStack istack : passed.getContents()){
				if(istack != null && istack.getType() != Material.AIR){
					if(!temp.contains(new ItemStack(istack.getType(), 1, (short)istack.getDurability()))){
						ItemStack itemstack = new ItemStack(istack.getType());
						itemstack.setDurability(istack.getDurability());
						ItemMeta i = itemstack.getItemMeta();
						i.setDisplayName(itemstack.getType().name().toLowerCase());
						
						if(system.getMarkkit().getString("playershop." + owner + "." + itemstack.getTypeId() + "." + itemstack.getDurability()) != null){
							i.setLore(Arrays.asList("Price: " + system.getMarkkit().getString("playershop." + owner + "." + itemstack.getTypeId() + "." + itemstack.getDurability())));
						}else{
							i.setLore(Arrays.asList("Price not found"));
						}
						itemstack.setItemMeta(i);
						temp.addItem(itemstack);
					}
				}
			}
			for(int i : divider){
				ItemStack divide = new ItemStack(Material.THIN_GLASS);
				ItemMeta divMeta = divide.getItemMeta();
				divMeta.setDisplayName(main.AS("&5Separator"));
				divide.setItemMeta(divMeta);
				temp.setItem(i, divide);
			}
			ItemStack buy = new ItemStack(Material.WOOL);
			buy.setDurability((short)5);
			ItemMeta buyMeta = buy.getItemMeta();
			buyMeta.setDisplayName(owner + " 's shop");
			buyMeta.setLore(Arrays.asList("Click to buy the items in your cart!"));
			buy.setItemMeta(buyMeta);
			temp.setItem(44, buy);
			playershop.put(owner, temp);
			buyer.openInventory(playershop.get(owner));
		}
		
		public void loadMarkkitInventory(Player p, String name){

			if(system.getMarkkit().get("Items." + name) == null){
				main.s(p, "Cannot find this markkit, please contact staff.");
				return;
			}
			
			ElyMarkkitItem im = new ElyMarkkitItem(main, name);
			Material mat = im.getMaterial();
			short damage = (short) im.getDurability();
			
			if(system.getMarkkit().get("Items." + name + ".inStock") == null){
				im.setInStock(192);
			}
			if(im.getInStock() < 0){
				im.setInStock(0);
			}
			Inventory inv;
			if(im.isSellDoubled()){
				inv = Bukkit.createInventory(null, 54, main.AS("&40 stocked. Double price!"));
			}else{
				inv = Bukkit.createInventory(null, 54, main.AS("&6" + im.getInStock() + "&b items stocked."));
			}
			
			
			for(int i : new int[]{3, 12, 21, 30, 39, 48, 5, 14, 23, 32, 41, 50}){
					ItemStack divider = new ItemStack(Material.THIN_GLASS);
					ItemMeta divide = divider.getItemMeta();
					divide.setDisplayName(ChatColor.DARK_PURPLE + "" + ChatColor.ITALIC + "Separator");
					divider.setItemMeta(divide);
					inv.setItem(i, divider);
			}

			ItemStack redCancel = new ItemStack(Material.WOOL, 1, (short) 14);
			ItemStack greenAccept = new ItemStack(Material.WOOL, 1, (short) 5);
			ItemMeta cancel = redCancel.getItemMeta();
			ItemMeta accept = greenAccept.getItemMeta();
			cancel.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "Cancel!");
			redCancel.setItemMeta(cancel);
			accept.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Accept!");
			greenAccept.setItemMeta(accept);
			
			ItemStack calculateRight = new ItemStack(Material.BOWL, 1);
			ItemMeta rightMeta = calculateRight.getItemMeta();
			rightMeta.setDisplayName(ChatColor.RED + "Click here to calculate the price!");
			rightMeta.setLore(Arrays.asList(ChatColor.GREEN + "Price: "));
			calculateRight.setItemMeta(rightMeta);
			
			ItemStack calculateLeft = new ItemStack(Material.BOWL, 1);
			ItemMeta leftMeta = calculateLeft.getItemMeta();
			leftMeta.setDisplayName(ChatColor.RED + "Click here to calculate the price!");
			leftMeta.setLore(Arrays.asList(ChatColor.GREEN + "Price: "));
			calculateLeft.setItemMeta(leftMeta);

			inv.setItem(47, redCancel);
			inv.setItem(51, redCancel);
			inv.setItem(46, greenAccept);
			inv.setItem(52, greenAccept);
			inv.setItem(53, calculateRight);
			inv.setItem(45, calculateLeft);

			for(int i : ((system.getMarkkit().get("Items." + name + ".64") == null) ? new int[]{1} : new int[]{64, 32, 16, 8, 1})){
				if(system.getMarkkit().contains("Items." + name + "." + i)){
					if(im.getBuyPrice(i) > 0){
						ItemStack item = new ItemStack(mat, i, damage);
						ItemMeta itemMeta = item.getItemMeta();
						if(im.isSellDoubled()){
							itemMeta.setLore(Arrays.asList((ChatColor.GREEN + "Buy"), (im.getBuyPrice(i)*2) + "", (ChatColor.RED + "Sell"), im.getSellPrice(i) + ""));
						}else{
							itemMeta.setLore(Arrays.asList((ChatColor.GREEN + "Buy"), (im.getBuyPrice(i)) + "", (ChatColor.RED + "Sell"), im.getSellPrice(i) + ""));
						}
						item.setItemMeta(itemMeta);
						for(int slot : new int[]{4, 13, 22, 31, 40}){
							if(inv.getItem(slot) == null || inv.getItem(slot).getType() == Material.AIR){
								inv.setItem(slot, item);
								break;
							}
						}
					}
				}
			}
			
			inventory.put(name, inv);
			p.openInventory(inventory.get(name));
		}
}