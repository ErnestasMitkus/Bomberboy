package com.draznel.bomberboy.packets;

import com.draznel.bomberboy.net.GameClient;
import com.draznel.bomberboy.net.GameServer;

public class Packet09TileChanged extends Packet {

	private int x, y;
	private byte id;
	
	public Packet09TileChanged(byte[] data) {
		super(9);
		String[] dataArray = readData(data).split(",");
		this.x = Integer.parseInt(dataArray[0]);
		this.y = Integer.parseInt(dataArray[1]);
		this.id = Byte.parseByte(dataArray[2]);
	}
	
	public Packet09TileChanged(int x, int y, byte id) {
		super(9);
		this.x = x;
		this.y = y;
		this.id = id;
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
		return ("09" + this.x + "," + this.y + "," + this.id).getBytes();
	}
	
	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	public byte getId() {
		return id;
	}

}
