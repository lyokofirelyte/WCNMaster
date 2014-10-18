package com.github.lyokofirelyte.Divinity.Manager;

import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import net.minecraft.util.gnu.trove.map.hash.THashMap;

import org.bukkit.Material;

import com.github.lyokofirelyte.Divinity.API;
import com.github.lyokofirelyte.Divinity.Divinity;
import com.github.lyokofirelyte.Divinity.DivinityUtilsModule;
import com.github.lyokofirelyte.Divinity.Commands.DivCommand;
import com.github.lyokofirelyte.Spectral.DataTypes.DPI;
import com.github.lyokofirelyte.Spectral.DataTypes.ElyChannel;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.spreadsheet.ListEntry;
import com.google.gdata.data.spreadsheet.ListFeed;
import com.google.gdata.data.spreadsheet.SpreadsheetEntry;
import com.google.gdata.data.spreadsheet.SpreadsheetFeed;
import com.google.gdata.data.spreadsheet.WorksheetEntry;

public class SpreadSheetReader {
	
	private API api;
	private SpreadsheetService service;
	private List<SpreadsheetEntry> spreadsheets;
	private Map<Integer, Material> markkitMap;
	private Map<Material, MarkkitFetch> markkits;
	private List<Integer> flagged;
	int fails = 0;
	
	public SpreadSheetReader(API i){
		api = i;
	}
	
	public void auth(final boolean shutdown, final boolean startup){
		
		new Thread(new Runnable(){ public void run(){

			String[] info = api.getDivSystem().getStr(DPI.SPREADSHEET_LOGIN).split(" ");
		    service = new SpreadsheetService("WCNMaster");
		    
		    try {
		    	service.setUserCredentials(info[0], info[1]);
			} catch (Exception e){
				e.printStackTrace();
			}
		    
		    fetch(shutdown, startup);
		    
		}}).start();
	}
	
	public void fetch(final boolean shutdown, final boolean startup){
		
		if (!api.getDivSystem().getBool(DPI.ENABLE_SPREADSHEET)){
			return;
		}
		
		new Thread(new Runnable(){ public void run(){

			markkitMap = new THashMap<Integer, Material>();
			markkits = new THashMap<Material, MarkkitFetch>();
			flagged = new ArrayList<Integer>();
			
			try {
				URL spreadURL = new URL("https://spreadsheets.google.com/feeds/spreadsheets/private/full");
				SpreadsheetFeed feed = service.getFeed(spreadURL, SpreadsheetFeed.class);
				spreadsheets = feed.getEntries();
				fails = 0;
			} catch (Exception e){
				fails++;
				if (fails > 3){
					System.out.println("Locked the spreadsheet due to connection issues!");
					return;
				}
				auth(shutdown, startup);
				return;
			}
			
		    for (SpreadsheetEntry spread : spreadsheets){
		    	if (spread.getTitle().getPlainText().equals("system")){
		    		
			    	List<WorksheetEntry> worksheets = null;
			    	
					try {
						worksheets = spread.getWorksheets();
					} catch (Exception e) {
						e.printStackTrace();
					}
					
			        for (WorksheetEntry worksheet : worksheets){
			        	if (worksheet.getTitle().getPlainText().equals("markkit")){
			        		
			        	    ListFeed feed = null;
			        	    boolean cont = true;
			        	    
							try {
								feed = service.getFeed(worksheet.getListFeedUrl(), ListFeed.class);
							} catch (Exception e) {
								e.printStackTrace();
								cont = false;
							}
							
							if (cont){
							
								int rowNum = 1;
								int colNum = 0;
		
				        	    for (ListEntry row : feed.getEntries()){
				        	    	for (String tag : row.getCustomElements().getTags()){
		        	    				if (rowNum >= 2){
		        	    					
		        	    					String val = row.getCustomElements().getValue(tag);
		        	    					boolean update = false;
		        	    					
					        	    		switch (colNum){
					        	    		
					        	    			case 0:
					        	    					
					        	    				try {
					        	    						
					        	    					Material m = Material.valueOf(val);
				        	    						markkitMap.put(rowNum, m);
					        	    						
					        	    					if (!markkits.containsKey(m)){
					        	    						markkits.put(m, new MarkkitFetch(m));
					        	    					}
					        	    						
					        	    				} catch (Exception e){
					        	    					//e.printStackTrace();
					        	    					flagged.add(rowNum);
					        	    				}
		
					        	    			break;
					        	    			
					        	    			case 1:
					        	    				
					        	    				if (markkitMap.containsKey(rowNum) && !flagged.contains(rowNum)){
					        	    					MarkkitFetch fetch = markkits.get(markkitMap.get(rowNum));
					        	    					
					        	    					try {
					        	    						fetch.getBuyPrices().put(Byte.parseByte(val), new ArrayList<Integer>());
					        	    						fetch.getSellPrices().put(Byte.parseByte(val), new ArrayList<Integer>());
					        	    						fetch.getRowSorter().put(rowNum, Byte.parseByte(val));
					        	    					} catch (Exception e){
						        	    					//e.printStackTrace();
					        	    						flagged.add(rowNum);
					        	    					}
					        	    				}
					        	    				
					        	    			break;
					        	    			
					        	    			case 2: case 3:
					        	    				
					        	    				if (markkitMap.containsKey(rowNum) && !flagged.contains(rowNum)){
					        	    					
					        	    					try {
					        	    						
						        	    					MarkkitFetch fetch = markkits.get(markkitMap.get(rowNum));
						        	    					int currPrice = Integer.parseInt(val);
						        	    					Integer[] stacks = new Integer[]{ 1, 2, 3, 4, 64 };
						        	    					
						        	    					for (int i = 0; i < 5; i++){
						        	    						if (fetch.getBuyPrices().get(fetch.getRowSorter().get(rowNum)).size() < i+1){
						        	    							fetch.getBuyPrices().get(fetch.getRowSorter().get(rowNum)).add(0);
						        	    						}
						        	    						if (fetch.getSellPrices().get(fetch.getRowSorter().get(rowNum)).size() < i+1){
						        	    							fetch.getSellPrices().get(fetch.getRowSorter().get(rowNum)).add(0);
						        	    						}
						        	    					}
		
						        	    					for (int i = 0; i < 5; i++){
							        	    					if (colNum == 2 && fetch.getRowSorter().containsKey(rowNum)){
							        	    						fetch.getBuyPrices().get(fetch.getRowSorter().get(rowNum)).set(i, currPrice/stacks[i]);
							        	    					} else if (fetch.getRowSorter().containsKey(rowNum)){
							        	    						fetch.getSellPrices().get(fetch.getRowSorter().get(rowNum)).set(i, currPrice/stacks[i]);
							        	    					}
						        	    					}
		
					        	    					} catch (Exception e){
						        	    					//e.printStackTrace();
					        	    						flagged.add(rowNum);
					        	    					}
					        	    				}
					        	    				
					        	    			break;
					        	    			
					        	    			case 4:
					        	    				
					        	    				if (markkitMap.containsKey(rowNum) && !flagged.contains(rowNum)){
					        	    					
					        	    					try {
					        	    						
						        	    					MarkkitFetch fetch = markkits.get(markkitMap.get(rowNum));
						        	    					
						        	    					if (!val.equals("ALL")){
						        	    						List<String> allowedStacks = Arrays.asList(val.split(", "));
						        	    						if (allowedStacks.size() == 0){
						        	    							allowedStacks.add(val);
						        	    						}
						        	    						for (String s : new String[]{ "64", "32", "16", "8", "1" }){
						        	    							if (!allowedStacks.contains(s)){
						        	    								fetch.getBuyPrices().get(fetch.getRowSorter().get(rowNum)).set(fetch.getIndex(Integer.parseInt(s)), 0);
						        	    								fetch.getSellPrices().get(fetch.getRowSorter().get(rowNum)).set(fetch.getIndex(Integer.parseInt(s)), 0);
						        	    							}
						        	    						}
						        	    					}
						        	    					
					        	    					} catch (Exception e){
						        	    					//e.printStackTrace();
					        	    						flagged.add(rowNum);
					        	    					}
					        	    				}
					        	    				
					        	    			break;
					        	    			
					        	    			case 5:
					        	    				
					        	    				if (!val.equals("0") && markkitMap.containsKey(rowNum) && !flagged.contains(rowNum)){
					        	    					
					        	    					try {
					        	    						
					        	    						MarkkitFetch fetch = markkits.get(markkitMap.get(rowNum));
					        	    						String[] customs = val.split(", ");
					        	    						
					        	    						if (customs.length == 0){
					        	    							customs = new String[]{ val };
					        	    						}
					        	    						
					        	    						for (String custom : customs){
					        	    							String stackAmt = custom.split("\\[")[0];
					        	    							String buy = custom.split("\\[")[1].split("\\:")[0];
					        	    							String sell = custom.split("\\[")[1].split("\\:")[1].replace("]", "");
				        	    								fetch.getBuyPrices().get(fetch.getRowSorter().get(rowNum)).set(fetch.getIndex(Integer.parseInt(stackAmt)), Integer.parseInt(buy));
				        	    								fetch.getSellPrices().get(fetch.getRowSorter().get(rowNum)).set(fetch.getIndex(Integer.parseInt(stackAmt)), Integer.parseInt(sell));
					        	    						}
					        	    						
					        	    					} catch (Exception e){
						        	    					//e.printStackTrace();
					        	    						flagged.add(rowNum);
					        	    					}
					        	    				}
					        	    				
					        	    			break;
					        	    			
					        	    			case 6:
					        	    				
					        	    				if (markkitMap.containsKey(rowNum) && !flagged.contains(rowNum)){
					        	    					
					        	    					ElyMarkkitItem item = new ElyMarkkitItem(api, markkitMap.get(rowNum), markkits.get(markkitMap.get(rowNum)).getRowSorter().get(rowNum));
					        	    					row.getCustomElements().setValueLocal(tag, item.getInStock() + "");
														update = true;
					        	    				}
					        	    				
					        	    			break;
					        	    			
					        	    			case 7:
					        	    				
					        	    				if (markkitMap.containsKey(rowNum) && !flagged.contains(rowNum)){
					        	    					try {
						        	    					List<String> items = api.getDivSystem().getList(DPI.LAST_ACTION);
						        	    					for (String item : items){
						        	    						String[] itemSplit = item.split(" ");
						        	    						if (itemSplit[0].equals(markkitMap.get(rowNum).getId() + ":" + markkits.get(markkitMap.get(rowNum)).getRowSorter().get(rowNum))){
						        	    							row.getCustomElements().setValueLocal(tag, DivinityUtilsModule.createString(itemSplit, 1));
						        	    							break;
						        	    						}
						        	    					}
					        	    					} catch (Exception e){
						        	    					//e.printStackTrace();
					        	    						row.getCustomElements().setValueLocal(tag, "0");
															update = true;
					        	    					}
					        	    				}
					        	    				
					        	    			break;
					        	    			
					        	    			case 9:
					        	    				
					        	    				if (!shutdown){
					        	    					row.getCustomElements().setValueLocal(tag, !flagged.contains(rowNum) ? "0" : "INVALID");
					        	    				} else {
					        	    					row.getCustomElements().setValueLocal(tag, "OFFLINE");
					        	    				}
					        	    				
					        	    				update = true;
					        	    				
					        	    			break;
					        	    		}
					        	    		
					        	    		if (update){
					        	    			try {
					        	    				row.update();
					        	    			} catch (Exception e){}
					        	    		}
		        	    				}
		        	    				colNum++;
				        	    	}
				        	    	rowNum++;
				        	    	colNum = 0;
				        	    }
				        	    if (!shutdown){
				        	    	finish();
				        	    }
				        	}
				        } else if (worksheet.getTitle().getPlainText().equals("commands") && startup){
				        	
				        	Map<String, String[]> info = new THashMap<String, String[]>();
				        	
				    		for (Object o : api.main.commandMap.values()){
				    			for (Method m : o.getClass().getMethods()){
				    				if (m.getAnnotation(DivCommand.class) != null){
				    					DivCommand anno = m.getAnnotation(DivCommand.class);
				    					String name = anno.aliases()[0];
				    					for (int i = 1; i < anno.aliases().length; i++){
				    						name = anno.aliases().length > i ? name + ", " + anno.aliases()[i] : name;
				    					}
				    					String[] perm = anno.perm().split("\\.");
				    					String permFinal = perm[perm.length-1].substring(0, 1).toUpperCase() + perm[perm.length-1].substring(1) + "+";
				    					info.put(name, new String[]{ name, anno.desc(), anno.help(), permFinal, (anno.player() + "").replace("true", "no").replace("false", "yes"), o.getClass().getName().replace(".class", "") });
				    				}
				    			}
				    		}
				    		
				    		List<String> names = new ArrayList<String>(info.keySet());
				    		Collections.sort(names);
				    		
			        	    ListFeed feed = null;
			        	    boolean cont = true;
			        	    
							try {
								feed = service.getFeed(worksheet.getListFeedUrl(), ListFeed.class);
							} catch (Exception e) {
								e.printStackTrace();
								cont = false;
							}
							
							if (cont){
							
								int rowNum = 1;
								int colNum = 0;
		
				        	    for (ListEntry row : feed.getEntries()){
				        	    	for (String tag : row.getCustomElements().getTags()){
		        	    				if (rowNum >= 1){
		        	    					row.getCustomElements().setValueLocal(tag, info.get(names.get(rowNum))[colNum]);
		        	    				}
		        	    				colNum++;
				        	    	}
				        	    	try {
				        	    		row.update();
				        	    	} catch (Exception e){}
				        	    	colNum = 0;
				        	    	rowNum++;
				        	    	if (rowNum >= names.size()){
				        	    		break;
				        	    	}
				        	    }
							}
				        }
			    	}
		    	}
		    }
		}}).start();
	}
	
	private void finish(){
		
		Integer[] stacks = new Integer[]{ 64, 32, 16, 8, 1 };
		List<Byte> curr;
		
		for (MarkkitFetch markkit : markkits.values()){
			curr = new ArrayList<Byte>(markkit.getBuyPrices().keySet());
			for (Byte b : curr){
				ElyMarkkitItem item = new ElyMarkkitItem(api, markkit.getMat(), b);
				for (int i = 0; i < markkit.getBuyPrices().size(); i++){
					int price = markkit.getBuyPrices().get(b).get(markkit.getIndex(stacks[i]));
					if (price != item.getBuyPrice(stacks[i])){
						ElyChannel.STAFF.send("&6System", "Markkit buy price change: &6" + markkit.getMat() + "x" + stacks[i] + " &c(" + item.getBuyPrice(stacks[i]) + " -> " + price + ")", api);
						api.getDivSystem().getMarkkit().set("Items." + item.getSignName() + "." + stacks[i] + ".buyprice", price);
					}
					i++;
				}
			}
			curr = new ArrayList<Byte>(markkit.getSellPrices().keySet());
			for (Byte b : curr){
				ElyMarkkitItem item = new ElyMarkkitItem(api, markkit.getMat(), b);
				for (int i = 0; i < markkit.getSellPrices().size(); i++){
					int price = markkit.getSellPrices().get(b).get(markkit.getIndex(stacks[i]));
					if (price != item.getSellPrice(stacks[i])){
						ElyChannel.STAFF.send("&6System", "Markkit sell price change: &6" + markkit.getMat() + "x" + stacks[i] + " &c(" + item.getSellPrice(stacks[i]) + " -> " + price + ")", api);
						api.getDivSystem().getMarkkit().set("Items." + item.getSignName() + "." + stacks[i] + ".sellprice", price);
					}
					i++;
				}
			}
		}
	}
	
	private class MarkkitFetch {
		
		private Material mat;
		private Map<Byte, List<Integer>> buyPrices = new THashMap<Byte, List<Integer>>();
		private Map<Byte, List<Integer>> sellPrices = new THashMap<Byte, List<Integer>>();
		private Map<Integer, Byte> rowSorter = new THashMap<Integer, Byte>();
		
		MarkkitFetch(Material m){
			mat = m;
		}
		
		public Map<Byte, List<Integer>> getBuyPrices(){
			return buyPrices;
		}
		
		public Map<Byte, List<Integer>> getSellPrices(){
			return sellPrices;
		}
		
		public Map<Integer, Byte> getRowSorter(){
			return rowSorter;
		}
		
		public Material getMat(){
			return mat;
		}
		
		private int getIndex(int stackAmount){
			switch (stackAmount){
				case 1: return 4;
				case 8: return 3;
				case 16: return 2;
				case 32: return 1;
				default: return 0;
			}
		}
		
	}
}