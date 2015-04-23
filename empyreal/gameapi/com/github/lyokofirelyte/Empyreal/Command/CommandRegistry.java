package com.github.lyokofirelyte.Empyreal.Command;

import java.io.FileInputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.SimplePluginManager;

import com.github.lyokofirelyte.Empyreal.Empyreal;
import com.github.lyokofirelyte.Empyreal.Elysian.DivinityPlayer;
import com.github.lyokofirelyte.Empyreal.Elysian.DivinityStorageModule;
import com.github.lyokofirelyte.Empyreal.Modules.AutoRegister;
import com.github.lyokofirelyte.Empyreal.Modules.GamePlayer;

public class CommandRegistry implements CommandExecutor {
	
	public Empyreal main;
	
	public CommandRegistry(Empyreal i){
		main = i;
	}
	
	public void registerCommands(Object... classes){
                
		Field f = null;
		CommandMap scm = null;
	                
		try {
			f = SimplePluginManager.class.getDeclaredField("commandMap");
			f.setAccessible(true);
			scm = (CommandMap) f.get(Bukkit.getPluginManager());
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	                
		for (Object obj : classes){
			for (Method method : obj.getClass().getMethods()) {
				if (method.getAnnotation(GameCommand.class) != null){
					GameCommand anno = method.getAnnotation(GameCommand.class);
					try {
						Cmd command = new Cmd(anno.aliases()[0]);
						command.setUsage(anno.help());
						command.setAliases(Arrays.asList(anno.aliases()));
						command.setDescription(anno.desc());
						scm.register("emp", command);
						command.setExecutor(this);
						main.commandMap.put(Arrays.asList(anno.aliases()), obj);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
    
    @Override // so this is a hot mess...
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    	
    	GamePlayer<?> gp = null;
    	
    	if (sender instanceof Player){
	    	if (main.getPlayers().containsKey(((Player) sender).getUniqueId())){
	    		gp = main.getGamePlayer(((Player) sender).getUniqueId());
	    	} else if (main.getOnlineModules().containsKey(((Player) sender).getUniqueId().toString())){
	    		gp = (DivinityPlayer) main.getOnlineModules().get(((Player) sender).getUniqueId().toString());
	    	}
    	} else {
    		gp = main.getConsolePlayer();
    	}
    	
    	for (List<String> cmdList : main.commandMap.keySet()){
    		if (cmdList.contains(label)){
    			for (String command : cmdList){
    				if (command.equals(label)){
    					Object obj = main.commandMap.get(cmdList);
    					for (Method m : obj.getClass().getMethods()){
    						if (m.getAnnotation(GameCommand.class) != null && Arrays.asList(m.getAnnotation(GameCommand.class).aliases()).contains(command)){
    							try {
    								GameCommand anno = m.getAnnotation(GameCommand.class);
    								if ((sender instanceof Player && gp.getPerms().contains(anno.perm())) || sender instanceof Player == false || sender.isOp() || anno.perm().equals("emp.member") || anno.perm().equals("wa.guest")){
    									if (args.length > anno.max() || args.length < anno.min()){
    										s(sender, anno.help());
    										return true;
    									}
    									if (sender instanceof Player == false && anno.player()){
    										s(sender, "&4Console can't run this!");
    									} else { // epilepsy warning
    										if (anno.name().equals("none")){
	    										if (m.getParameterTypes()[0].equals(Player.class)){
	    											if (m.getParameterTypes()[1].equals(GamePlayer.class)){
	    												m.invoke(obj, (Player) sender, gp, args);
	    											} else {
	    												m.invoke(obj, (Player) sender, args);
	    											}
	    										} else if (m.getParameterTypes()[0].equals(CommandSender.class)){
	    											if (m.getParameterTypes()[1].equals(GamePlayer.class)){
	    												m.invoke(obj, sender, gp, args);
	    											} else {
	    												m.invoke(obj, sender, args);
	    											}
	    										}
    										} else {
	    										if (m.getParameterTypes()[0].equals(Player.class)){
	    											if (m.getParameterTypes()[1].equals(GamePlayer.class)){
	    												m.invoke(obj, (Player) sender, gp, args, label);
	    											} else {
	    												m.invoke(obj, (Player) sender, args, label);
	    											}
	    										} else if (m.getParameterTypes()[0].equals(CommandSender.class)){
	    											if (m.getParameterTypes()[1].equals(GamePlayer.class)){
	    												m.invoke(obj, sender, gp, args, label);
	    											} else {
	    												m.invoke(obj, sender, args, label);
	    											}
	    										}
    										}
    									}
    								} else if (!sender.hasPermission(anno.perm())){
    									s(sender, "&4No permission!");
    								}
    							} catch (Exception e) {
    								e.printStackTrace();
    							}
    						}
    					}
    				}
    			}
    		}
    	}
    	return true;
    }
    
	public void registerAll(Object mainClassInstance, String packageMainDirectory, String jarName){
		  
        List<Class<?>> allClasses = new ArrayList<Class<?>>();
        
        try {
        
	        List<String> classNames = new ArrayList<String>();
	        ZipInputStream zip = new ZipInputStream(new FileInputStream("./plugins/" + jarName.replace(".jar", "") + ".jar"));
	        boolean look = false;
	        
	        for (ZipEntry entry = zip.getNextEntry(); entry != null; entry = zip.getNextEntry()){
	        	
	        	if (entry.isDirectory()){
	        		look = entry.getName().contains(packageMainDirectory);
	        	}
	        	
	            if (entry.getName().endsWith(".class") && !entry.isDirectory() && look) {
	            	
	                StringBuilder className = new StringBuilder();
	                
	                for (String part : entry.getName().split("/")) {
	                	
	                    if (className.length() != 0){
	                        className.append(".");
	                    }
	                    
	                    className.append(part);
	                    
	                    if (part.endsWith(".class")){
	                        className.setLength(className.length()-".class".length());
	                    }
	                }
	                
	                classNames.add(className.toString());
	            }
	        }
	        
	        for (String clazz : classNames){
	        	allClasses.add(Class.forName(clazz));
	        }
	        
        } catch (Exception e){
        	e.printStackTrace();
        }
        
        List<AutoRegister<?>> curr = new ArrayList<AutoRegister<?>>();
        
		for (Class<?> clazz : allClasses){
			
			Object obj = null;
			
			for (int i = 0; i < clazz.getConstructors().length; i++){
				try {
					Constructor<?> con = clazz.getConstructors()[i];
					con.setAccessible(true);
					obj = con.newInstance(mainClassInstance);
					break;
				} catch (Exception e){}
			}
			
			if (obj instanceof AutoRegister && !clazz.toString().contains("\\$") && !main.getClazzez().containsKey(clazz.toString())){
				main.getClazzez().put(clazz.toString(), (AutoRegister<?>) obj);
				curr.add((AutoRegister<?>) obj);
			}
		}

		for (Object obj : curr){
			
			if (obj instanceof Listener){
				Bukkit.getPluginManager().registerEvents((Listener) obj, (Plugin) mainClassInstance);
			}
			
			registerCommands(obj);
		}
	}
    
    private void s(CommandSender sender, String msg){
    	sender.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
    }
}