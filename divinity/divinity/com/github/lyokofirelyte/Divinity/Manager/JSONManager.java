package com.github.lyokofirelyte.Divinity.Manager;

import com.github.lyokofirelyte.Divinity.API;
import com.github.lyokofirelyte.Divinity.JSON.JSONChatClickEventType;
import com.github.lyokofirelyte.Divinity.JSON.JSONChatExtra;
import com.github.lyokofirelyte.Divinity.JSON.JSONChatHoverEventType;
import com.github.lyokofirelyte.Divinity.JSON.JSONChatMessage;
import com.google.common.collect.ImmutableMap;

public class JSONManager {

	private API api;
	
	public JSONManager(API i){
		api = i;
	}
	
	public enum JSONClickType {
		
		CLICK_RUN("CLICK_RUN"),
		CLICK_SUGGEST("CLICK_SUGGEST"),
		CLICK_URL("CLICK_URL"),
		NONE("NONE");
		
		JSONClickType(String type){
			this.type = type;
		}
		
		String type;
	}
	
	public JSONChatMessage create(String message, ImmutableMap<String, ImmutableMap<JSONClickType, String[]>> jsonValue){
		
		JSONChatMessage msg = new JSONChatMessage(api.AS(message));
		
		for (String extra : jsonValue.keySet()){
			JSONChatExtra e = new JSONChatExtra(api.AS(extra));
			for (JSONClickType type : jsonValue.get(extra).keySet()){
				switch (type){
					case CLICK_RUN:
						e.setClickEvent(JSONChatClickEventType.RUN_COMMAND, jsonValue.get(extra).get(type)[0]);
					break;
					case CLICK_SUGGEST:
						e.setClickEvent(JSONChatClickEventType.SUGGEST_COMMAND, jsonValue.get(extra).get(type)[0]);
					break;
					case CLICK_URL:
						e.setClickEvent(JSONChatClickEventType.OPEN_URL, jsonValue.get(extra).get(type)[0]);
					break;
					default: break;
				}
				if (jsonValue.get(extra).get(type).length > 1){
					e.setHoverEvent(JSONChatHoverEventType.SHOW_TEXT, api.AS(jsonValue.get(extra).get(type)[1]));
				} else if (type.equals(JSONClickType.NONE)){
					e.setHoverEvent(JSONChatHoverEventType.SHOW_TEXT, api.AS(jsonValue.get(extra).get(type)[0]));
				}
			}
			msg.addExtra(e);
		}
		
		return msg;
	}
}