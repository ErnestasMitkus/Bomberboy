package com.draznel.bomberboy.net;

import java.net.InetAddress;

public class PlayerClient {

	private String username;
	private float x, y;
	public InetAddress ipAddress = null;
	public int port = -1;
	private long delta;
	
	public int innerAnimation;
	public int innerFrame;
	
	public int pingTimeout = 0;
	public boolean ready = false;
	
	public boolean alive;
	public boolean spectator = false;
	public int blocksDestroyed;
	
	public PlayerClient(String username, float x, float y, long delta, InetAddress ip, int port) {
		this.username = username;
		this.x = x;
		this.y = y;
		this.delta = delta;
		this.ipAddress = ip;
		this.port = port;
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setX(float x) {
		this.x = x;
	}
	
	public float getX() {
		return x;
	}
	
	public void setY(float y) {
		this.y = y;
	}
	
	public float getY() {
		return y;
	}
	
	public void setDelta(long delta) {
		this.delta = delta;
	}
	public long getDelta() {
		return delta;
	}
	
}
