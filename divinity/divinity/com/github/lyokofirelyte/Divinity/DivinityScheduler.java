package com.github.lyokofirelyte.Divinity;

import java.lang.reflect.Method;

public class DivinityScheduler implements Runnable {
	
	private Object c;
	private Method m;
	private Object[] a;
	private boolean b;
	
	public DivinityScheduler(Object clazz, Method method, Object... args){
		c = clazz;
		m = method;
		a = args;
		b = true;
	}
	
	public DivinityScheduler(Object clazz, Method method){
		c = clazz;
		m = method;
		b = false;
	}
	
	@Override
	public void run(){

		try {
			if (b){
				m.invoke(c, a);
			} else {
				m.invoke(c);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}