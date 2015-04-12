package com.github.lyokofirelyte.Empyreal.Listener;

import com.github.lyokofirelyte.Empyreal.Listener.SocketMessageListener.Handler;

import lombok.Getter;
import lombok.Setter;

public class SocketObject {

	@Getter @Setter
	private Object obj;
	
	@Getter @Setter
	private Class<?> clazz;
	
	@Getter @Setter
	private Handler reason;
	
	@Getter @Setter
	private String fromServer;
	
	public SocketObject(Object obj, Class<?> clazz, Handler reason, String fromServer){
		setObj(obj);
		setClazz(clazz);
		setReason(reason);
		setFromServer(fromServer);
	}
}