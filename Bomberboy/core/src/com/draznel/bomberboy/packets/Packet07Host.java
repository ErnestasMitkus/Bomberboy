package com.draznel.bomberboy.packets;

import com.draznel.bomberboy.net.GameClient;
import com.draznel.bomberboy.net.GameServer;

public class Packet07Host extends Packet {

	private String username;
	
	public Packet07Host(byte[] data) {
		super(7);
		this.username = readData(data);
	}
	
	public Packet07Host(String username) {
		super(7);
		this.username = username;
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
		return ("07" + this.username).getBytes();
	}
	
	public String getUsername() {
		return username;
	}
	
}
