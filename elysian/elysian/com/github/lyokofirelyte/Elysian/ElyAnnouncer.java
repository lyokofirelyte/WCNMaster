package com.github.lyokofirelyte.Elysian;

import java.util.List;

import org.bukkit.command.CommandSender;
import com.github.lyokofirelyte.Divinity.DivinityUtilsModule;
import com.github.lyokofirelyte.Divinity.Commands.DivCommand;
import com.github.lyokofirelyte.Spectral.DataTypes.DPI;
import com.github.lyokofirelyte.Spectral.DataTypes.ElyTask;
import com.github.lyokofirelyte.Spectral.Identifiers.AutoRegister;
import com.github.lyokofirelyte.Spectral.StorageSystems.DivinitySystem;

public class ElyAnnouncer implements Runnable, AutoRegister {
	
	private Elysian main;
	
	public ElyAnnouncer(Elysian i){
		main = i;
	}

	@Override
	public void run(){
		
		DivinitySystem dp = main.api.getDivSystem();
		List<String> messages = dp.getList(DPI.ANNOUNCER);
		int index = dp.getInt(DPI.ANNOUNCER_INDEX);
		
		if (messages.size() > index){
			DivinityUtilsModule.bc(messages.get(index));
			dp.set(DPI.ANNOUNCER_INDEX, index + 1);
		} else if (messages.size() > 0){
			DivinityUtilsModule.bc(messages.get(0));
			dp.set(DPI.ANNOUNCER_INDEX, 1);
		}
	}
	
	public void stop(){
		main.cancelTask(ElyTask.ANNOUNCER);
	}
	
	@DivCommand(aliases= {"announcer"}, perm = "wa.staff.mod2", help = "/announcer <add, remove, view> <message>", player = false, min = 1)
	public void onAnnounceCommand(CommandSender cs, String[] args){
		
		List<String> messages = main.api.getDivSystem().getList(DPI.ANNOUNCER);
		String msg = args.length > 1 ? new String(args[1]) : "";
		
		for (int x = 2; x < args.length; x++){
			msg = msg + " " + args[x];
		}
		
		switch (args[0]){
		
			case "add":
				
				if (!messages.contains(msg)){
					messages.add(msg);
					main.s(cs, "none", "Message added!");
				} else {
					main.s(cs, "none", "&c&oMessage already present.");
				}
				
			break;
			
			case "remove":
				
				if (messages.contains(msg)){
					messages.remove(msg);
					main.s(cs, "none", "Message removed!");
				} else {
					main.s(cs, "none", "&c&oMessage not present.");
				}
				
			break;
			
			case "view":
				
				for (String s : messages){
					main.s(cs, "none", s);
				}
				
			break;
		}
	}
}