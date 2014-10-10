package com.draznel.bomberboy.packets;

import com.draznel.bomberboy.net.GameClient;
import com.draznel.bomberboy.net.GameServer;

public class Packet13Items extends Packet {

	String items;
	
	public Packet13Items(byte[] data) {
		super(13);
		items = readData(data);
	}
	
	public Packet13Items(String items) {
		super(13);
		this.items = items;
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
		return ("13" + this.items).getBytes();
	}
	
	public String getItems() {
		return items;
	}
	
}
