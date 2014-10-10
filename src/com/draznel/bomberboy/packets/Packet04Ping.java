package com.draznel.bomberboy.packets;

import com.draznel.bomberboy.net.GameClient;
import com.draznel.bomberboy.net.GameServer;

public class Packet04Ping extends Packet {

	private String username;
	private long timeSent;
	private long delta;
	
	public Packet04Ping(byte[] data) {
		super(4);
		String[] dataArray = readData(data).split(",");
		username = dataArray[0];
		timeSent = Long.parseLong(dataArray[1]);
		delta = Long.parseLong(dataArray[2]);
	}
	
	public Packet04Ping(String username, long timeSent, long delta) {
		super(4);
		this.username = username;
		this.timeSent = timeSent;
		this.delta = delta;
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
		return ("04" + this.username + "," + this.timeSent + "," + this.delta).getBytes();
	}
	
	public String getUsername() {
		return username;
	}
	
	public long getTimeSent() {
		return timeSent;
	}

	public long getDelta() {
		return delta;
	}
}
