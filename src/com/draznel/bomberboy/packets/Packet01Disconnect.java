package com.draznel.bomberboy.packets;

import com.draznel.bomberboy.net.GameClient;
import com.draznel.bomberboy.net.GameServer;

public class Packet01Disconnect extends Packet {

	public static final int REASON_DC = 0;
	public static final int REASON_TIMED_OUT = 1;
	public static final int REASON_KICKED = 2;
	
	private String username;
	private int reason;
	
	public Packet01Disconnect(byte[] data) {
		super(1);
		String[] dataArray = readData(data).split(",");
		this.username = dataArray[0];
		this.reason = Integer.parseInt(dataArray[1]);
	}
	
	public Packet01Disconnect(String username, int reason) {
		super(1);
		this.username = username;
		this.reason = reason;
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
		return ("01" + this.username + "," + this.reason).getBytes();
	}
	
	public String getUsername() {
		return username;
	}
	
	public int getReason() {
		return reason;
	}
	
}
