package com.github.lyokofirelyte.Empyreal.Database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import com.github.lyokofirelyte.Empyreal.Empyreal;
import com.github.lyokofirelyte.Empyreal.JSONMap;
import com.github.lyokofirelyte.Empyreal.Listener.SocketMessageListener.Handler;
import com.github.lyokofirelyte.Empyreal.Listener.SocketObject;
import com.github.lyokofirelyte.Empyreal.Modules.DefaultPlayer;

public class EmpyrealSQL {

	private Empyreal main;
	
	@Getter @Setter
	private Connection conn;
	
	static final String WRITE_OBJECT_SQL = "INSERT INTO java_objects(uuid, object_value) VALUES (?, ?)";
	static final String READ_OBJECT_SQL = "SELECT object_value FROM java_objects WHERE id = ?";
	
	public EmpyrealSQL(Empyreal i){
		main = i;
		initDatabase("../wa.db");
	}
	
	@SneakyThrows
	public void saveMapToDatabase(String table, JSONMap<String, Object> dp){
		for (Object thing : dp.keySet()){
			write((String) thing);
		}
	}
	
	@SneakyThrows
	public JSONMap<String, Object> getMapFromDatabase(String table, String uuid){
		
		JSONMap map = new JSONMap<String, Object>();
		ResultSet rs = getResult(table, "*", "uuid = '" + uuid + "'");
		ResultSetMetaData rsm = rs.getMetaData();
		int i = 1;
		
		while (rs.next()){
			map.set(rsm.getColumnName(i), rs.getObject(i));
			i++;
		}
		
		return map;
	}
	
	@SneakyThrows
	public DefaultPlayer createPlayerFromDatabase(String uuid){

		DefaultPlayer dms = new DefaultPlayer(UUID.fromString(uuid), main);
		ResultSet rs = getResult("users", "*", "uuid = '" + uuid + "'");
		ResultSetMetaData rsm = rs.getMetaData();
		int i = 1;
			
		while (rs.next()){
			dms.set(rsm.getColumnName(i), rs.getString(i));
			i++;
		}
		
		return dms;
	}
	
	@SneakyThrows
	public ResultSet getResult(String table, String value, String where){
		Statement stat = conn.createStatement();
		ResultSet set = where.equals("ALL") ? stat.executeQuery("select " + value + " from " + table + ";") : stat.executeQuery("select " + value + " from " + table + " where " + where + ";");
		stat.close();
		return set;
	}
	
	@SneakyThrows
	public void initDatabase(String pathToDatabase){
		
		if (conn == null){
	        Class.forName("org.sqlite.JDBC");
	        conn = DriverManager.getConnection("jdbc:sqlite:" + pathToDatabase);
		}
		
		if (main.getServerName().equals("GameServer")){
	        write("create table if not exists users (uuid VARCHAR(255), name VARCHAR(255));");
	        injectEnum(DPI.class, "users");
	        injectEnum(DAI.class, "alliances", "NAME");
	        injectEnum(Arrays.asList(DRI.class, DRF.class), "regions", "NAME");
		}
	}
	
	@SneakyThrows
	public void addTable(String tableName, String initData){
		write("create table if not exists " + tableName + " (" + initData + " VARCHAR(255));");
	}
	
	@SneakyThrows
	public void addColumn(String tableName, String columnName){
		
		write("create table if not exists " + tableName + " (" + columnName + " VARCHAR(255));");
		
		if (!hasColumn(tableName, columnName)){
			write("alter table " + tableName + " add " + columnName + " VARCHAR(255);");
		}
	}
	
	public void injectEnum(Class<? extends Enum<?>> e, String tableName, String... extras){
		injectEnum((List) Arrays.asList(e), tableName, extras);
	}
	
	@SneakyThrows
	public void injectEnum(List<Class<? extends Enum<?>>> e, String tableName, String... extras){
		
		write("create table if not exists " + tableName + " (TEMP VARCHAR(1));");

        for (String extra : extras){
        	if (!hasColumn(tableName, extra)){
        		addColumn(tableName, extra);
        	}
        }
        
        for (Class<? extends Enum<?>> clazz : e){
	        for (Enum<?> i : clazz.getEnumConstants()){
	            if (!hasColumn(tableName, i.toString())){
	            	addColumn(tableName, i.toString());
	            }
	        }
        }
	}
	
	@SneakyThrows
	public void injectData(String tableName, String column, String data, String where){
		write("update " + tableName + " set " + column + "=" + data + " where " + where + ";");
	}
	
	@SneakyThrows
	public boolean hasTable(String tableName){
		DatabaseMetaData md = conn.getMetaData();
		ResultSet rs = md.getColumns(null, null, tableName, null);
		return rs.next();
	}
	
	@SneakyThrows
	public boolean hasColumn(String tableName, String rowName){
		DatabaseMetaData md = conn.getMetaData();
		ResultSet rs = md.getColumns(null, null, tableName, rowName);
		return rs.next();
	}
	
	private boolean isGameServer(){
		return main.getServerName().equals("GameServer");
	}
	
	@SneakyThrows
	public void write(String data){
		
		Statement stat = conn.createStatement();
		
		if (!isGameServer()){
			main.sendToSocket("GameServer", Handler.SQL_WRITE, data);
		} else {
			stat = stat.isClosed() ? conn.createStatement() : stat;
			stat.executeUpdate(data);
		}
	}
}