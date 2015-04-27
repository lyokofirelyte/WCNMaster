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
	
	/**
	 * Saves an list of JSONMaps to the database very quickly. <br />
	 * Automatically adjusts for new rows and incomplete data sets. <br />
	 * This groups prep statements by tables & batches them at the end.
	 */
	@SneakyThrows
	public void saveMapsToDatabase(List<JSONMap<String, Object>> dpd){
		
		List<JSONMap<String, Object>> dp = new ArrayList<JSONMap<String, Object>>();
		Map<String, PreparedStatement> insertStatements = new HashMap<String, PreparedStatement>();
		Map<String, PreparedStatement> updateStatements = new HashMap<String, PreparedStatement>();
		Map<String, List<String>> tableChecker = new HashMap<String, List<String>>();
		Map<String, List<String>> updatedCols = new HashMap<String, List<String>>();
		
		System.out.println("Initializing database for batch save (" + dpd.size() + ")...");
		
		int amt = 1;
		
		/**
		 * Insuring the "uuid" value is present. If not, we will take "name". <br />
		 * If no uuid and no name, we skip the map - it won't work with the database.
		 */
		for (JSONMap<String, Object> map : dpd){
			if (!map.containsKey("uuid")){
				map.put("uuid", map.getStr("name"));
			}
			if (map.getStr("uuid").equals("none") && map.getStr("name").equals("none")){
				continue;
			}
			dp.add(map);
		}
		
		/**
		 * Iterate through each map in the list. Each map is an individual entry filled with values.
		 */
		for (JSONMap<String, Object> map : dp){
			
			Map<String, String> toChange = new HashMap<String, String>();
			List<String> colNames = new ArrayList<String>();
			String vals = "";
			String val = "";
			String updateVals = "";
			
			/**
			 * SQLite does not support booleans - so we need to make them obvious strings first.
			 */
			for (String key : map.keySet()){
				if (key.equals("table") || (key.equals("name") && map.getStr("table").equals("alliances"))){
					continue;
				}
				if (map.getStr(key).equals("true") || map.getStr(key).equals("false")){
					toChange.put(key, map.getStr(key) + "_BOOLEAN_");
				}
			}
			
			/**
			 * We change the booleans here because of concurrent modification exceptions.
			 */
			for (String thing : toChange.keySet()){
				map.put(thing, toChange.get(thing));
			}
			
			/**
			 * The table checker holds the column names so we only have to ask the database <br />
			 * how many columns there are ONCE per table. This saves time - about 3 seconds per entry...
			 */
			if (!tableChecker.containsKey(map.getStr("table"))){
				
				ResultSet genCheck = conn.createStatement().executeQuery("select * from " + map.getStr("table") + ";");
				genCheck.next();
				
				ResultSetMetaData genMeta = genCheck.getMetaData();
				
				int colAmt = new Integer(genMeta.getColumnCount());
				
				for (int i = 1; i <= colAmt; i++){
					colNames.add(genMeta.getColumnName(i));
				}
				
				genCheck.close();
			
			} else { // Otherwise, we'll just use what we already have.
				for (String thing : tableChecker.get(map.getStr("table"))){
					colNames.add(thing);
				}
			}

			/** This is used to see if we should use INSERT or UPDATE */
			ResultSet userCheck = conn.createStatement().executeQuery("select count(*) from " + map.getStr("table") + " where uuid = '" + map.getStr("uuid") + "';");
			userCheck.next();
			
			List<String> newKeys = new ArrayList<String>();
			
			for (String key : map.keySet()){
				if (key.equals("table") || (key.equalsIgnoreCase("name") && map.getStr("table").equals("alliances"))){
					continue;
				}
				// The column names we added earlier are what's in the table - so if this is true, then it's a NEW key.
				if (!colNames.contains(key)){
					newKeys.add(key);
				}
				vals += vals.equals("") ? "?" : ", ?";
			}
			
			/**
			 * We need to add all of the new keys manually now - since the database has no columns for them.
			 */
			if (newKeys.size() > 0){

				for (String key : newKeys){
					conn.createStatement().executeUpdate("alter table " + map.getStr("table") + " add " + key + ";");
					colNames.add(key);
				}

				/**
				 * Now the value amounts won't match the prep statements we made before.
				 * I guess we'll have to execute them and wipe them!
				 */
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
				
				/**
				 * Since we executed our prep statements, we'll need to make new ones for the rest of the entries.
				 * This will make the next rotation parse the columns again and re-fill our colNames.
				 */
				tableChecker.remove(map.getStr("table"));
			}
			
			List<String> keys = new ArrayList<String>();
			
			/**
			 * Sometimes we will get a bad value or a NULL, so we'll try/catch
			 * and see if all of the columns are safe. Then, transfer the safe ones
			 * to the keys list.
			 */
			for (String col : colNames){
				try {
					if (!map.containsKey(col)){
						vals += ", ?";
						map.set(col, "none");
					}
					keys.add(col);
				} catch (Exception e){}
			}
			
			/**
			 * Here we create our set values and the placeholders for the UPDATE statement.
			 */
			for (String key : keys){
				val += val.equals("") ? key : ", " + key;
				updateVals += updateVals.equals("") ? key + " = ?" : ", " + key + " = ?";
			}
			
			/**
			 * Fuck the alliances table. >.>
			 */
			if (map.getStr("table").equals("alliances")){
				vals += ", ?";
			}
			
			/**
			 * This will be true if the user does not exist yet. We will INSERT.
			 */
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
		
		
		/**
		 * Now, all of the above work didn't actually touch the database unless new keys were added
		 * This will execute each table's batch - so if we have 5 tables it'll hit the database
		 * 5 times... instead of hitting it once per user/alliance/etc.
		 * We save like 8 minutes doing this.
		 */
		for (String table : insertStatements.keySet()){
			insertStatements.get(table).executeBatch();
			insertStatements.get(table).close(); // Close out of memory right away.
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
	
	/**
	 * Touches the database each time. Use very rarely.
	 * Alternative: Prep statements w/ batch updates.
	 */
	public void write(String data){
		try {
			Statement stat = conn.createStatement();
			stat.executeUpdate(data);
		} catch (Exception e){
			e.printStackTrace();
		}
	}
}