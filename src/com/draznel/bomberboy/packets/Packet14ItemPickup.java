package com.draznel.bomberboy.packets;

import com.draznel.bomberboy.net.GameClient;
import com.draznel.bomberboy.net.GameServer;

public class Packet14ItemPickup extends Packet {
	
	private String username;
	private int id, x, y;
	
	public Packet14ItemPickup(byte[] data) {
		super(14);
		String[] dataArray = readData(data).split(",");
		this.username = dataArray[0];
		this.id = Integer.parseInt(dataArray[1]);
		this.x = Integer.parseInt(dataArray[2]);
		this.y = Integer.parseInt(dataArray[3]);
	}
	
	public Packet14ItemPickup(String username, int id, int x, int y) {
		super(14);
		this.username = username;
		this.id = id;
		this.x = x;
		this.y = y;
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
		return ("14" + this.username + "," + this.id + "," + this.x + "," + this.y).getBytes();
	}
	
	public String getUsername() {
		return username;
	}
	public int getId() {
		return id;
	}
	public byte getIdByte() {
		return (byte) id;
	}
	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}

}
