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
	public void saveMapToDatabase(String table, JSONMap<String, Object> dp){
		
		if (!dp.containsKey("uuid")){
			dp.set("uuid", dp.getStr("name"));
		}
		
		if (dp.getStr("uuid").equals("none") && dp.getStr("name").equals("none")){
			return;
		}

		String vals = "";
		String val = "";
		
		Map<String, String> toChange = new HashMap<String, String>();
		
		for (String key : dp.keySet()){
			if (!hasColumn(table, key)){
				conn.createStatement().executeUpdate("alter table " + table + " add " + key + ";");
			}
			vals += vals.equals("") ? "?" : ", ?";
			if (dp.getStr(key).equals("true") || dp.getStr(key).equals("false")){
				toChange.put(key, dp.getStr(key) + "_BOOLEAN_");
			}
		}
		
		for (String thing : toChange.keySet()){
			dp.set(thing, toChange.get(thing));
		}
		
		ResultSet userCheck = conn.createStatement().executeQuery("select count(*) from " + table + " where uuid = '" + dp.getStr("uuid") + "';");
		userCheck.next();
		
		ResultSet genCheck = conn.createStatement().executeQuery("select * from " + table + ";");
		genCheck.next();
		
		ResultSetMetaData genMeta = genCheck.getMetaData();
		int colAmt = new Integer(genMeta.getColumnCount());
		List<String> colNames = new ArrayList<String>();
		
		for (int i = 1; i <= colAmt; i++){
			colNames.add(genMeta.getColumnName(i));
		}
		
		if (userCheck.getInt(1) == 0){
			
			List<String> keys = new ArrayList<String>();
			
			for (String col : colNames){
				try {
					if (!dp.containsKey(col)){
						vals += ", ?";
						dp.set(col, "none");
					}
					keys.add(col);
				} catch (Exception e){
					e.printStackTrace();
				}
			}
			
			for (String key : keys){
				val += val.equals("") ? key : ", " + key;
			}
			
			PreparedStatement pst = conn.prepareStatement("insert into " + table + " (" + val + ") values (" + vals + ");");
			
			for (int i = 1; i <= keys.size(); i++){
				pst.setObject(i, dp.get(keys.get(i-1)));
			}
			
			System.out.println(pst.executeUpdate() + " @ " + dp.get("name"));
			pst.close();
			
		} else {
			
			vals = "";
			
			for (String col : colNames){
				vals += vals.equals("") ? col + " = ?" : ", " + col + " = ?";
			}
			
			PreparedStatement pst = conn.prepareStatement("update " + table + " set " + vals + " where uuid = '" + dp.getStr("uuid") + "';");
			
			for (int i = 1; i <= colNames.size(); i++){
				pst.setObject(i, dp.get(colNames.get(i-1)));
			}
			
			pst.close();
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
			if (obj instanceof String){
				if (((String) obj).endsWith("_BOOLEAN_")){
					System.out.println(((String) obj).replace("_BOOLEAN_", ""));
					map.set(rsm.getColumnLabel(i), obj != null ? ((String) obj).replace("_BOOLEAN_", "") : "none");
				} else {
					map.set(rsm.getColumnLabel(i), obj != null ? obj : "none");
				}
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