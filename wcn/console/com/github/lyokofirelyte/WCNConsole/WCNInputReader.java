package com.github.lyokofirelyte.WCNConsole;

import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Files;

import lombok.SneakyThrows;

public class WCNInputReader {

	private WCNConsole main;
	private String recent = "";
	
	public WCNInputReader(WCNConsole i){
		
		main = i;
		
		new Thread(new Runnable(){
			public void run(){
				
				try {
					
					String inText = "";
					
					while ((inText = main.getIn().readLine()) != null){
						eval(inText);
					}
					
				} catch (Exception e){
					main.getLog().log("Connection lost!");
				} finally {
					try {
						main.getIn().close();
						main.getOut().close();
						main.getSocket().close();
						System.exit(0);
					} catch (Exception ee){
						main.getLog().logline("Shit's fucked. Closing this bitch down.");
						System.exit(1);
					}
				}	
			}
		}).start();
	}
	
	@SneakyThrows
	public void eval(String inText){
		
		switch (inText){
		
			case "wcnconsole_invalid_notonline":
				
				main.getLog().logline("&cYou're not authed with WA!");
				main.getLog().logline("&cPlease log in to WA and try logging in again.");
				main.getLog().log("&e< press enter to retry > ");
				main.setProgress(0);
				
			break;

			case "wcnconsole_continue_user":
				
				File file = new File("id.wcn");
				
				if (file.exists()){
					try {
						main.setPass(new String(Files.readAllLines(file.toPath()).get(0)));
					} catch (Exception e){}
				}
				
				main.getLog().log("&eWCN ID CODE: " + main.getPass());
				main.setProgress(main.getProgress() + 1);
				
			break;
			
			case "wcnconsole_accepted_pass":
				
				main.getLog().logline("Verification successful. Welcome!");
				main.getLog().logline("Type 'help' for a list of commands!");
				main.getLog().logline("You can toggle on/off the popup editor by typing 'startx'");
				main.getLog().logline("Without the pop-out editor, if someone talks your typing will be overwritten.");
				main.getLog().logline("");
				main.setProgress(main.getProgress() + 1);
				main.getLog().log("&3" + main.getUser() + "@WCNConsole > ");
				main.popup();
				
				file = new File("id.wcn");
				
				if (!file.exists()){
					file.createNewFile();
					PrintWriter pw = new PrintWriter(file);
					pw.println(main.getPass());
					pw.flush();
					pw.close();
				}
				
			break;
			
			case "wcn_serverlist": case "wcn_say":
				
				main.getLog().log("\r                                                                       \r");
				main.getLog().logline(main.getIn().readLine());
				main.ready();
				
			break;
			
			case "wcnconsole_denied_pass":

				main.getLog().logline("&cInvalid ID code. Please try again.");
				main.getLog().log("&eWCN ID CODE: ");
				
			break;
			
			case "bad_username":
				
				main.getLog().logline("&cYou can't use that username - sorry!");
				main.getLog().log("&e< press enter to retry > ");
				main.setProgress(0);
				
			break;
			
			case "wcn_logger":
				
				if (main.getProgress() == 2){
					
					String log = "";
					main.getLog().log("\r                                                                    \r");
					
					while (true){
						
						log = main.getIn().readLine();
						
						if (log.equals("END")){
							break;
						}
						
						if (log.equals(recent)){
							continue;
						}
						
						recent = new String(log);
						main.getLog().logline(log);
						
					}
					
					main.ready();
				}
				
			break;
			
			case "wcn_no_perms":
				
				main.getLog().logline("&cNo permissions!");
				main.ready();
				
			break;
			
			case "wcnconsole_server_socket_closed":
				
				main.getLog().logline("\r&cThat server is offline.");
				main.ready();
				
			break;
			
			case "wcnconsole_server_not_found":
				
				main.getLog().logline("\r&cThat server does not exist. See serverlist.");
				main.ready();
				
			break;
		}
	}
}