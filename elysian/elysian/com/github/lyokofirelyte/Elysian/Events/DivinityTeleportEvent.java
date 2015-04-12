package com.github.lyokofirelyte.Elysian.Events;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class DivinityTeleportEvent extends Event implements Cancellable {
	

    private static final HandlerList handlers = new HandlerList();
    private Player player;
    private boolean cancelled;
    private World w;
    private double x;
    private double y;
    private double z;
    private float pitch;
    private float yaw;
    private Location from;
    private Location to;

    public DivinityTeleportEvent(Player p, String world, String xx, String yy, String zz) {
    	player = p;
    	w = Bukkit.getWorld(world);
    	x = Double.parseDouble(xx);
    	y = Double.parseDouble(yy);
    	z = Double.parseDouble(zz);
    	pitch = p.getLocation().getPitch();
    	yaw = p.getLocation().getYaw();
    	from = p.getLocation();
    	to = new Location(w, x, y, z, yaw, pitch);
    }
    
    public DivinityTeleportEvent(Player p, String world, String xx, String yy, String zz, String yaww, String pitchh) {
    	player = p;
    	w = Bukkit.getWorld(world);
    	x = Double.parseDouble(xx);
    	y = Double.parseDouble(yy);
    	z = Double.parseDouble(zz);
    	pitch = Float.parseFloat(pitchh);
    	yaw = Float.parseFloat(yaww);
    	from = p.getLocation();
    	to = new Location(w, x, y, z, yaw, pitch);
    }
    
    public DivinityTeleportEvent(Player p, Location l) {
    	player = p;
    	w = l.getWorld();
    	x = l.toVector().getBlockX();
    	y = l.toVector().getBlockY();
    	z = l.toVector().getBlockZ();
    	pitch = p.getLocation().getPitch();
    	yaw = p.getLocation().getYaw();
    	from = p.getLocation();
    	to = new Location(w, x, y, z, yaw, pitch);
    }
    
    public DivinityTeleportEvent(Player p, Location l, String yaww, String pitchh) {
    	player = p;
    	w = l.getWorld();
    	x = l.toVector().getBlockX();
    	y = l.toVector().getBlockY();
    	z = l.toVector().getBlockZ();
    	pitch = Float.parseFloat(pitchh);
    	yaw = Float.parseFloat(yaww);
    	from = p.getLocation();
    	to = new Location(w, x, y, z, yaw, pitch);
    }
    
    public void setPlayer(Player p){
    	player = p;
    }
 
    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }
    
    public boolean isCancelled() {
        return cancelled;
    }

    public Player getPlayer(){
    	return player;
    }
    
    public double getX(){
    	return x;
    }
    
    public double getY(){
    	return y;
    }
    
    public double getZ(){
    	return z;
    }
    
    public float getPitch(){
    	return pitch;
    }
    
    public float getYaw(){
    	return yaw;
    }
    
    public Location getTo(){
    	return to;
    }
    
    public Location getFrom(){
    	return from;
    }
    
    public void setX(String xx){
    	x = Double.parseDouble(xx);
    }
    
    public void setY(String yy){
    	y = Double.parseDouble(yy);
    }
    
    public void setZ(String zz){
    	z = Double.parseDouble(zz);
    }
    
    public void setPitch(String pitchh){
    	pitch = Float.parseFloat(pitchh);
    }
    
    public void setYaw(String yaww){
    	yaw = Float.parseFloat(yaww);
    }
    
    public void setTo(Location l){
    	to = l;
    }
    
    public void setFrom(Location l){
    	from = l;
    }
 
    public HandlerList getHandlers() {
        return handlers;
    }
 
    public static HandlerList getHandlerList() {
        return handlers;
    }
}