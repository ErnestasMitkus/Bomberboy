package com.draznel.bomberboy.packets;

import com.draznel.bomberboy.net.GameClient;
import com.draznel.bomberboy.net.GameServer;

public class Packet05ReadyState extends Packet {

	private String username;
	private boolean ready;
	
	public Packet05ReadyState(byte[] data) {
		super(5);
		String[] dataArray = readData(data).split(",");
		this.username = dataArray[0];
		this.ready = Boolean.parseBoolean(dataArray[1]);
	}
	
	public Packet05ReadyState(String username, boolean ready) {
		super(5);
		this.username = username;
		this.ready = ready;
	}
	
	public String getUsername() {
		return username;
	}
	
	public boolean getReady() {
		return ready;
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
		return ("05" + this.username + "," + this.ready).getBytes();
	}
	
}
