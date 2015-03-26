package com.github.lyokofirelyte.WCNConsole;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.Console;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.Socket;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JPanel;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.SneakyThrows;

public class WCNConsole {
	
	@Getter
	private FancyLogging log = new FancyLogging();
	
	@Getter
	private Socket socket;
	
	private int conTries = 0;
	
	@Getter @Setter
	private int progress = 0;
	
	@Getter
	private String user = "";
	
	@Getter @Setter
	private String pass = "";
	
	@Getter
	private PrintWriter out;
	
	@Getter
	private BufferedReader in;
	
	private boolean startX = false;
	
	private JFrame frame;
	
	private String[] help = new String[]{
		"",
		"&acmd <server> <command>",
		"  &f> &3Send a command to a server.",
		"  &f> &3cmd Creative tp Hugh_Jasses TaylorSealy",
		"",
		"&aserverlist",
		"  &f> &3View all online servers.",
		"",
		"&aperms",
		"   &f> &3View your permission level.",
		"",
		"&asay <server> <message>",
		"   &f> &3Chat to a server. Use ALL for all servers.",
		"   &f> &3say wa Hey guys!",
		"",
		"&aonlineplayers",
		"   &f> &3View a list of all online players.",
		"",
		"&astartx",
		"   &f> &3Toggle a seperate command editor window.",
		"",
		"&aexit",
		"   &f> &3Disconnect safely.",
		""
	};

	public WCNConsole(){
		start();
	}
	
	public static void main(String[] args){
		new WCNConsole();
	}
	
	@SneakyThrows
	public void start(){
		
		try {
			
			socket = new Socket("144.76.184.51", 24500);
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			new WCNInputReader(this);
			
			log.log("&eWCN USERNAME: ");
			
			new Thread(new Runnable(){
				public void run(){
					read(new Scanner(System.in));
				}
			}).start();
			
		} catch (Exception e){
			if (conTries < 3){
				conTries++;
				System.out.println("Could not connect to WCN... trying again in 5 seconds...");
				Thread.sleep(5000);
				start();
			} else {
				System.out.println("Connection failed 3 times, is the server offline?");
			}
		}
	}
	
	public void read(@NonNull Scanner scanner){
		
		String inText = "";
		
		while ((inText = scanner.nextLine()) != null){
			
			if (progress == 0){
				
				user = user.equals("") ? inText : user;
				out.println("wcn_login");
				out.println(user);
				log.logline("&aConnecting...");
				
			} else if (progress == 1){
				
				pass = pass.equals("") ? inText : pass;
				log.logline("&aVerifying...");
				out.println("wcn_pass");
				out.println(pass);

			} else if (progress == 2){
				eval(inText);
			}
		}
	}
	
	public void eval(String inText){
		
		String[] args = inText.split(" ");
		
		switch (args[0].toLowerCase()){
		
			case "serverlist":
				
				out.println("wcn_serverlist");
				
			break;
		
			case "help":
				
				for (String thing : help){
					log.logline(thing);
				}
				
			break;
			
			default:
				
				log.logline("&cI'm afraid I can't do that, " + user + ". Unknown command.");
				
			break;
			
			case "say":
				
				if (args.length > 2){
					out.println("wcn_say");
					printAll(1, args);
					out.println("END");
				} else {
					log.logline("&cSyntax: say <server> <message>");
				}
				
			return;
			
			case "cmd":
				
				if (args.length > 2){
					out.println("wcn_cmd");
					printAll(1, args);
				} else {
					log.logline("&cSyntax: cmd <server> <command>");
				}
				
				out.println("END");
				
			break;
			
			case "exit":
				log.logline("&eI don't want to go!");
				System.exit(0);
			break;
			
			case "perms":
				
				
			break;
			
			case "startx":
				
				popup();
				
			break;
		}
		
		ready();
	}
	
	public void popup(){
		
		if (!startX){
			
			startX = true;
			frame = new JFrame();
			JPanel panel = new JPanel();
			final TextField text = new TextField();
			text.setPreferredSize(new Dimension(500, 30));
			frame.setLayout(new FlowLayout(0, 0, 0));
			panel.add(text);
			frame.add(panel);
			frame.setTitle("WCNConsole Command Input");
			frame.setVisible(true);
			frame.setLocationRelativeTo(null);
			frame.pack();
			frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			frame.setAlwaysOnTop(true);
			
			text.addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(ActionEvent e) {
					eval(text.getText());
					text.setText("");
				}
				
			});
			
		} else {
			frame.dispose();
			startX = false;
		}
	}
	
	@SneakyThrows
	public void ready(){
		getLog().log("\r&3" + getUser() + "@WCNConsole > ");
	}
	
	public void printAll(int startAt, String... args){
		for (int i = startAt; i < args.length; i++){
			out.println(args[i]);
		}
	}
}