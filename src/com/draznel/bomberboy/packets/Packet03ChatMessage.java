package com.draznel.bomberboy.packets;

import com.draznel.bomberboy.net.GameClient;
import com.draznel.bomberboy.net.GameServer;

public class Packet03ChatMessage extends Packet {

	private String message;
	private String username;
	
	public Packet03ChatMessage(byte[] data) {
		super(3);
		String dataString = new String(data).trim();	
		this.username = dataString.substring(2, dataString.indexOf(","));
		this.message = dataString.substring(dataString.indexOf(",") + 1);
	}
	
	public Packet03ChatMessage(String username, String message) {
		super(3);
		this.username = username;
		this.message = message;
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
		return ("03" + username + "," + message).getBytes();
		
	}
	
	public String getUsername() {
		return username;
	}
	
	public String getMessage() {
		return message;
	}
	
}
