package com.draznel.bomberboy.packets;

import com.draznel.bomberboy.net.GameClient;
import com.draznel.bomberboy.net.GameServer;

public class Packet12Victory extends Packet {

	String username;
	
	public Packet12Victory(byte[] data) {
		super(12);
		this.username = readData(data);
	}
	
	public Packet12Victory(String username) {
		super(12);
		this.username = username;
	}

	@Override
	public void writeData(GameClient client) {
		client.sendTCPData(getData());
	}

	@Override
	public void writeData(GameServer server) {
		server.sendDataToAllClients(getData());
	}

	@Override
	public byte[] getData() {
		return ("12" + this.username).getBytes();
	}
	
	public String getUsername() {
		return username;
	}
}
