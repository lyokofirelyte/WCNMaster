package com.github.lyokofirelyte.Divinity.Manager;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.minecraft.util.gnu.trove.map.hash.THashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.github.lyokofirelyte.Divinity.API;
import com.github.lyokofirelyte.Divinity.JSON.JSONChatClickEventType;
import com.github.lyokofirelyte.Divinity.JSON.JSONChatExtra;
import com.github.lyokofirelyte.Divinity.JSON.JSONChatHoverEventType;
import com.github.lyokofirelyte.Divinity.JSON.JSONChatMessage;

public class WebsiteManager implements Runnable {
	
	API api;
	public List<String> messages = new ArrayList<String>();
	public List<String> onlinePlayers = new ArrayList<String>();
	private boolean failed = false;
	
	public WebsiteManager(API i){
		api = i;
	}
	
	@Override
	public void run(){
		
		try {
		
			onlinePlayers = new ArrayList<String>();
			
			for (Player p : Bukkit.getOnlinePlayers()){
				onlinePlayers.add(p.getName());
			}
			
			Map<String, Object> map = new THashMap<>();
			map.put("type", "minecraft_refresh");
			map.put("players", onlinePlayers);
			
			JSONObject obj = sendPost("/api/chat", map);
			List<String> message = (List<String>) obj.get("message");
			String type = (String) obj.get("type");
			
			if (type.equals("send")){
				for (String s : message){
					if (!messages.contains(s)){
						messages.add(s);
						String noTimeStamp = s.substring(s.indexOf("] ")+1);
						String user = ("&7&o" + noTimeStamp.split(":")[0]).replace(" ", "");
						String send = "&6W &8\u2744&7&o " + user + "&f:" + s.substring(s.indexOf(user) + user.length() + 9);
						JSONChatMessage msg  = new JSONChatMessage("");
						
						for (String m : send.split(" ")){
							JSONChatExtra extra = null;
							if (m.startsWith("http")){
								extra = new JSONChatExtra(api.AS("&6&o" + api.title.getPageTitle(m) + " "));
								extra.setClickEvent(JSONChatClickEventType.OPEN_URL, m);
								extra.setHoverEvent(JSONChatHoverEventType.SHOW_TEXT, api.AS("&7Open url..."));
							} else {
								extra = new JSONChatExtra(api.AS(m + " "));
								extra.setHoverEvent(JSONChatHoverEventType.SHOW_TEXT, api.AS("&7&oThis user is using our web chat!"));
							}
							msg.addExtra(extra);
						}
						msg.sendToAllPlayers();
					}
				}
			}
			
		} catch (Exception e){
			if (!failed){
				failed = true;
				System.out.println("Error connecting to the website. There will be no further error messages.");
			}
		}
	}

	public JSONObject sendGet(final String folder){
		
		JSONObject result = null;

		try {
			
			String url = "http://worldsapart.no-ip.org:9090" + folder;
			 
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			
			con.setRequestMethod("GET");
			con.setRequestProperty("User-Agent", "");
			con.setRequestProperty("Content-Type", "application/json");
			
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
	 
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			
			in.close();
	 
			result = (JSONObject) new JSONParser().parse(response.toString());
			
		} catch (Exception e){}
		
		return result;
	}
 
	public JSONObject sendPost(final String folder, final Map<String, Object> map){
		
		JSONObject result = null;
		
		try {
			
			
			JSONObject json = new JSONObject();
			
			for (String key : map.keySet()){
				json.put(key, map.get(key));
			}
			
			 
			String url = "http://worldsapart.no-ip.org:9090" + folder;
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("POST");
			con.setRequestProperty("User-Agent", "");
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
			con.setDoOutput(true);
			
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(json.toJSONString());
			wr.flush();
			wr.close();
	 
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
	 
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			
			in.close();
			
			result = (JSONObject) new JSONParser().parse(response.toString());
			
		} catch (Exception e){}
		
		return result;
	}
}