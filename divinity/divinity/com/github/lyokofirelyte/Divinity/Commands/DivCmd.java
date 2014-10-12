package com.github.lyokofirelyte.Divinity.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class DivCmd extends Command {

    public CommandExecutor exe = null;
    
    public DivCmd(String name) {
        super(name);
    }

    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if(exe != null){
            exe.onCommand(sender, this, commandLabel,args);
        }
        return false;
    }
    
    public void setExecutor(CommandExecutor exe){
        this.exe = exe;
    }
}
