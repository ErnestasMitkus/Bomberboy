package com.draznel.bomberboy.packets;

import com.draznel.bomberboy.net.GameClient;
import com.draznel.bomberboy.net.GameServer;

public class Packet00Login extends Packet {
	
	private String username;
	private float x, y;
	private long timeMillis;
	private boolean nameAvailable;

	public Packet00Login(byte[] data) {
		super(0);
		String[] dataArray = readData(data).split(",");
		this.username = dataArray[0];
		this.x = Float.parseFloat(dataArray[1]);
		this.y = Float.parseFloat(dataArray[2]);
		this.timeMillis = Long.parseLong(dataArray[3]);
		this.nameAvailable = Boolean.parseBoolean(dataArray[4]);
	}
	
	public Packet00Login(String username, float x, float y, long timeMillis, boolean nameAvailable) {
		super(0);
		this.username = username;
		this.x = x;
		this.y = y;
		this.timeMillis = timeMillis;
		this.nameAvailable = nameAvailable;
	}

	@Override
	public void writeData(GameClient client) {
		client.sendData(getData());
	}

	@Override
	public void writeData(GameServer server) {
		server.sendDataToAllClients(getData());
	}

	@Override
	public byte[] getData() {
		return ("00" + this.username + "," + this.x + "," + this.y + "," + this.timeMillis + "," + this.nameAvailable).getBytes();
	}
	
	public String getUsername() {
		return username;
	}
	
	public float getX(){
		return x;
	}

	public float getY(){
		return y;
	}
	
	public long getTimeMillis() {
		return timeMillis;
	}
	
	public boolean getNameAvailable() {
		return nameAvailable;
	}
}
