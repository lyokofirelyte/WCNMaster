package com.github.lyokofirelyte.Divinity.JSON;

import net.minecraft.server.v1_8_R1.ChatSerializer;
import net.minecraft.server.v1_8_R1.PacketPlayOutChat;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class JSONChatMessage {
	
    private JSONObject chatObject;

    public JSONChatMessage(String text, Object nothing1, Object nothing2) {
    	
        chatObject = new JSONObject();
        chatObject.put("text", text);
    }
    
    public JSONChatMessage(String text) {
    	
        chatObject = new JSONObject();
        chatObject.put("text", text);
    }

    public void addExtra(JSONChatExtra extraObject) {
    	
        if (!chatObject.containsKey("extra")) {
            chatObject.put("extra", new JSONArray());
        }
        
        JSONArray extra = (JSONArray) chatObject.get("extra");
        extra.add(extraObject.toJSON());
        chatObject.put("extra", extra);
    }

    public void sendToPlayer(Player player) {  
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutChat(ChatSerializer.a(chatObject.toJSONString())));
    }
    
    public void sendToAllPlayers(){
    	for (Player player : Bukkit.getOnlinePlayers()){
    		((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutChat(ChatSerializer.a(chatObject.toJSONString())));
    	}
    }
    
    public String toString() {
        return chatObject.toJSONString();
    }
}