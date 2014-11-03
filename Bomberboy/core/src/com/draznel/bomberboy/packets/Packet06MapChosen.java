package com.draznel.bomberboy.packets;

import com.draznel.bomberboy.net.GameClient;
import com.draznel.bomberboy.net.GameServer;

public class Packet06MapChosen extends Packet {

	String senderName;
	String mapName;
	int width;
	int height;
	String tiles;
	
	public Packet06MapChosen(byte[] data) {
		super(6);
		String[] dataArray = readData(data).split(",");
		senderName = dataArray[0];
		mapName = dataArray[1];
		width = Integer.parseInt(dataArray[2]);
		height = Integer.parseInt(dataArray[3]);
		if (dataArray.length >= 5) {
			tiles = dataArray[4];
		} else {
			tiles = "";
		}
	}
	
	public Packet06MapChosen(String senderName, String mapName, int width, int height, String tiles) {
		super(6);
		this.senderName = senderName;
		this.mapName = mapName;
		this.width = width;
		this.height = height;
		this.tiles = tiles;
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
		return ("06" + this.senderName + "," + this.mapName + "," + this.width + "," + this.height + "," + this.tiles).getBytes();
	}
	
	public String getSenderName() {
		return senderName;
	}
	
	public String getMapName() {
		return mapName;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public String getTiles() {
		return tiles;
	}
	
}
