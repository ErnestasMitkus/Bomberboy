package com.draznel.bomberboy.packets;

import com.draznel.bomberboy.net.GameClient;
import com.draznel.bomberboy.net.GameServer;

public class Packet11PlayerKilled extends Packet {

	String username;
	
	public Packet11PlayerKilled(byte[] data) {
		super(11);
		username = readData(data);
	}
	
	public Packet11PlayerKilled(String username) {
		super(11);
		this.username = username;
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
		return ("11" + this.username).getBytes();
	}
	
	public String getUsername() {
		return username;
	}
	
}
