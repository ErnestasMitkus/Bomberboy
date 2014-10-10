package com.draznel.bomberboy.packets;

import com.draznel.bomberboy.net.GameClient;
import com.draznel.bomberboy.net.GameServer;

public class Packet10BombPlaced extends Packet {

	private int type;
	private String placedBy;
	private int x, y;
	private long explodeTimer;
	private long timePlaced;
	
	public Packet10BombPlaced(byte[] data) {
		super(10);
		String[] dataArray = readData(data).split(",");
		this.type = Integer.parseInt(dataArray[0]);
		this.placedBy = dataArray[1];
		this.x = Integer.parseInt(dataArray[2]);
		this.y = Integer.parseInt(dataArray[3]);
		this.explodeTimer = Long.parseLong(dataArray[4]);
		this.timePlaced = Long.parseLong(dataArray[5]);
	}
	
	public Packet10BombPlaced(int type, String placedBy, int x, int y, long explodeTimer, long timePlaced) {
		super(10);
		this.type = type;
		this.placedBy = placedBy;
		this.x = x;
		this.y = y;
		this.explodeTimer = explodeTimer;
		this.timePlaced = timePlaced;
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
		return ("10" + this.type + "," + this.placedBy + "," + this.x + "," + this.y + "," + this.explodeTimer + "," + this.timePlaced).getBytes();
	}
	
	public int getType() {
		return type;
	}
	public String getPlacedBy() {
		return placedBy;
	}
	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	public long getExplodeTimer() {
		return explodeTimer;
	}
	public long getTimePlaced() {
		return timePlaced;
	}
	
}
