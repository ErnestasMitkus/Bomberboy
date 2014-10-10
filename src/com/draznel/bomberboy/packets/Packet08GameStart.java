package com.draznel.bomberboy.packets;

import com.draznel.bomberboy.net.GameClient;
import com.draznel.bomberboy.net.GameServer;

public class Packet08GameStart extends Packet {

	private String username;
	private float x, y;
	private boolean alive;
	
	public Packet08GameStart(byte[] data) {
		super(8);
		String[] dataArray = readData(data).split(",");
		this.username = dataArray[0];
		this.x = Float.parseFloat(dataArray[1]);
		this.y = Float.parseFloat(dataArray[2]);
		this.alive = Boolean.parseBoolean(dataArray[3]);
	}
	
	public Packet08GameStart(String username, float x, float y, boolean alive) {
		super(8);
		this.username = username;
		this.x = x;
		this.y = y;
		this.alive = alive;
	}

	@Override
	public void writeData(GameClient client) {
		client.sendTCPData(getData());
	}

	@Override
	public void writeData(GameServer server) {
		server.sendTCPDataToAllClients(getData());
	}
	

	@Override
	public byte[] getData() {
		return ("08" + this.username + "," + this.x + "," + this.y + "," + this.alive).getBytes();
	}
	
	public String getUsername() {
		return username;
	}
	public float getX() {
		return x;
	}
	public float getY() {
		return y;
	}
	public boolean getAlive() {
		return alive;
	}
}
