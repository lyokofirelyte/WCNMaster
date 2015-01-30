package com.github.lyokofirelyte.Divinity.Manager;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;

import org.bukkit.entity.Player;

import com.github.lyokofirelyte.Divinity.API;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.CityResponse;

public class PlayerLocation {

	private API api;
	private File database;
	private DatabaseReader reader;
	
	public PlayerLocation(API i){
		api = i;
		database = new File(DivinityManager.sysDir + "GeoLite2-City.mmdb");
	}
	
	public String getCountry(String ip){
		return response(ip).getCountry().getName();
	}
	
	public String getCity(String ip){
		return response(ip).getCity().getName();
	}
	
	public String getPostal(String ip){
		return response(ip).getPostal().getCode();
	}
	
	private CityResponse response(String ip){
		
		try {
			reader = new DatabaseReader.Builder(database).build();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		CityResponse response = null;
		
		try {
			response = reader.city(InetAddress.getByName(ip));
			reader.close();
		} catch (Exception e){
			e.printStackTrace();
		}
		
		return response;
	}
}