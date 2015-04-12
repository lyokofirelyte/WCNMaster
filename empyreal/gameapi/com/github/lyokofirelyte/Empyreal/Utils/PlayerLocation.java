package com.github.lyokofirelyte.Empyreal.Utils;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;

import lombok.Getter;

import com.github.lyokofirelyte.Empyreal.Empyreal;
import com.github.lyokofirelyte.Empyreal.Modules.AutoRegister;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.CityResponse;

public class PlayerLocation implements AutoRegister<PlayerLocation> {

	private Empyreal api;
	private File database;
	private DatabaseReader reader;
	
	@Getter
	private PlayerLocation type = this;
	
	public PlayerLocation(Empyreal i){
		api = i;
		database = new File("../wa/plugins/Divinity/GeoLite2-City.mmdb");
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