package com.github.lyokofirelyte.Empyreal.Database;

import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import com.github.lyokofirelyte.Empyreal.Empyreal;
import com.github.lyokofirelyte.Empyreal.JSONMap;
import com.github.lyokofirelyte.Empyreal.Modules.AutoRegister;
import com.github.lyokofirelyte.Empyreal.Modules.DefaultPlayer;

public class EmpyrealSQL implements AutoRegister<EmpyrealSQL> {

	@Getter
	private EmpyrealSQL type = this;
	
	private Empyreal main;
	
	@Getter @Setter
	private Connection conn;
	
	public EmpyrealSQL(Empyreal i){
		main = i;
		new File("../db").mkdirs();
		initDatabase("../db/" + i.getServerName() + ".db");
	}
	
	@SneakyThrows
	public void saveMapsToDatabase(List<JSONMap<String, Object>> dpd){
		
		List<JSONMap<String, Object>> dp = new ArrayList<JSONMap<String, Object>>();
		Map<String, PreparedStatement> insertStatements = new HashMap<String, PreparedStatement>();
		Map<String, PreparedStatement> updateStatements = new HashMap<String, PreparedStatement>();
		Map<String, List<String>> tableChecker = new HashMap<String, List<String>>();
		Map<String, List<String>> updatedCols = new HashMap<String, List<String>>();
		
		System.out.println("Initializing database for batch save (" + dpd.size() + ")...");
		
		int amt = 1;
		
		for (JSONMap<String, Object> map : dpd){
			if (!map.containsKey("uuid")){
				map.put("uuid", map.getStr("name"));
			}
			if (map.getStr("uuid").equals("none") && map.getStr("name").equals("none")){
				continue;
			}
			dp.add(map);
		}
		
		for (JSONMap<String, Object> map : dp){
			
			Map<String, String> toChange = new HashMap<String, String>();
			List<String> colNames = new ArrayList<String>();
			String vals = "";
			String val = "";
			String updateVals = "";
			
			for (String key : map.keySet()){
				if (key.equals("table") || (key.equals("name") && map.getStr("table").equals("alliances"))){
					continue;
				}
				if (map.getStr(key).equals("true") || map.getStr(key).equals("false")){
					toChange.put(key, map.getStr(key) + "_BOOLEAN_");
				}
			}
			
			for (String thing : toChange.keySet()){
				map.put(thing, toChange.get(thing));
			}
			
			if (!tableChecker.containsKey(map.getStr("table"))){
				
				ResultSet genCheck = conn.createStatement().executeQuery("select * from " + map.getStr("table") + ";");
				genCheck.next();
				
				ResultSetMetaData genMeta = genCheck.getMetaData();
				
				int colAmt = new Integer(genMeta.getColumnCount());
				
				for (int i = 1; i <= colAmt; i++){
					colNames.add(genMeta.getColumnName(i));
				}
				
				genCheck.close();
			
			} else {
				for (String thing : tableChecker.get(map.getStr("table"))){
					colNames.add(thing);
				}
			}

			ResultSet userCheck = conn.createStatement().executeQuery("select count(*) from " + map.getStr("table") + " where uuid = '" + map.getStr("uuid") + "';");
			userCheck.next();
			
			List<String> newKeys = new ArrayList<String>();
			
			for (String key : map.keySet()){
				if (key.equals("table") || (key.equalsIgnoreCase("name") && map.getStr("table").equals("alliances"))){
					continue;
				}
				if (!colNames.contains(key)){
					newKeys.add(key);
				}
				vals += vals.equals("") ? "?" : ", ?";
			}
			
			if (newKeys.size() > 0){

				for (String key : newKeys){
					conn.createStatement().executeUpdate("alter table " + map.getStr("table") + " add " + key + ";");
					colNames.add(key);
				}

				if (insertStatements.containsKey(map.getStr("table"))){
					insertStatements.get(map.getStr("table")).executeBatch();
					insertStatements.get(map.getStr("table")).close();
					insertStatements.remove(map.getStr("table"));
				}
				
				if (updateStatements.containsKey(map.getStr("table"))){
					updateStatements.get(map.getStr("table")).executeBatch();
					updateStatements.get(map.getStr("table")).close();
					updateStatements.remove(map.getStr("table"));
				}
				
				tableChecker.remove(map.getStr("table"));
			}
			
			List<String> keys = new ArrayList<String>();
			
			for (String col : colNames){
				try {
					if (!map.containsKey(col)){
						vals += ", ?";
						map.set(col, "none");
					}
					keys.add(col);
				} catch (Exception e){}
			}
			
			for (String key : keys){
				val += val.equals("") ? key : ", " + key;
				updateVals += updateVals.equals("") ? key + " = ?" : ", " + key + " = ?";
			}
			
			if (map.getStr("table").equals("alliances")){
				vals += ", ?";
			}
			
			if (userCheck.getInt(1) == 0){
				
				if (!insertStatements.containsKey(map.getStr("table"))){
					insertStatements.put(map.getStr("table"), conn.prepareStatement("insert into " + map.getStr("table") + " (" + val + ") values (" + vals + ");"));
				}
				
				for (int i = 1; i <= keys.size(); i++){
					insertStatements.get(map.getStr("table")).setObject(i, map.get(keys.get(i-1)));
				}
				
				insertStatements.get(map.getStr("table")).addBatch();
				System.out.println("[INSERT] [" + map.getStr("table") + "] " + map.getStr("name") + " (" + amt + "/" + dp.size() + ")");
				
			} else {
				
				if (!updateStatements.containsKey(map.getStr("table"))){
					updateStatements.put(map.getStr("table"), conn.prepareStatement("update " + map.getStr("table") + " set " + updateVals + " where uuid = ?;"));
				}

				for (int i = 1; i <= keys.size(); i++){
					updateStatements.get(map.getStr("table")).setObject(i, map.get(keys.get(i-1)));
				}
				
				updateStatements.get(map.getStr("table")).setObject(keys.size()+1, map.getStr("uuid"));
				
				updateStatements.get(map.getStr("table")).addBatch();
				System.out.println("[UPDATE] [" + map.getStr("table") + "] " + map.getStr("name") + " (" + amt + "/" + dp.size() + ")");
			}
			
			amt++;
			userCheck.close();
			tableChecker.put(map.getStr("table"), colNames);
		}
		
		for (String table : insertStatements.keySet()){
			insertStatements.get(table).executeBatch();
			insertStatements.get(table).close();
		}
		
		for (String table : updateStatements.keySet()){
			updateStatements.get(table).executeBatch();
			updateStatements.get(table).close();
		}
	}
	
	@SneakyThrows
	public JSONMap<String, Object> getMapFromDatabase(String table, String uuid){
		
		JSONMap<String, Object> map = new JSONMap<String, Object>();
		ResultSet rs = getResult(table, "*", "uuid = '" + uuid + "'");
		ResultSetMetaData rsm = rs.getMetaData();
		rs.next();
		
		Object obj = null;
		
		for (int i = 1; i <= rsm.getColumnCount(); i++){
			obj = rs.getObject(i);
			if (obj != null && obj.toString().endsWith("_BOOLEAN_")){
				map.set(rsm.getColumnLabel(i), obj != null ? obj.toString().replace("_BOOLEAN_", "") : "none");
			} else {
				map.set(rsm.getColumnLabel(i), obj != null ? obj : "none");
			}
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
		return set;
	}
	
	@SneakyThrows
	public void initDatabase(String pathToDatabase){
		
		if (conn == null){
	        Class.forName("org.sqlite.JDBC");
	        conn = DriverManager.getConnection("jdbc:sqlite:" + pathToDatabase);
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
		if (!hasTable(tableName)){
			write("create table if not exists " + tableName + " (name VARCHAR(1));");
		}
		if (!hasColumn(tableName, column)){
			write("alter table " + tableName + " add " + column + " VARCHAR(255);");
		} else {
			write("update " + tableName + " set " + column + "=" + data + " where " + where + ";");
		}
	}
	
	public boolean hasTable(String tableName){
		try {
			DatabaseMetaData md = conn.getMetaData();
			ResultSet rs = md.getColumns(null, null, tableName, null);
			return rs.next();
		} catch (Exception e){
			return false;
		}
	}
	
	public boolean hasColumn(String tableName, String rowName){
		try {
			DatabaseMetaData md = conn.getMetaData();
			ResultSet rs = md.getColumns(null, null, tableName, rowName);
			return rs.next();
		} catch (Exception e){
			return false;
		}
	}
	
	public void write(String data){
		try {
			Statement stat = conn.createStatement();
			stat.executeUpdate(data);
		} catch (Exception e){
			e.printStackTrace();
		}
	}
}