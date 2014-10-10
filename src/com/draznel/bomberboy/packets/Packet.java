package com.draznel.bomberboy.packets;

import com.draznel.bomberboy.net.GameClient;
import com.draznel.bomberboy.net.GameServer;

public abstract class Packet {

	public static enum PacketTypes {
		INVALID(-1), LOGIN(0), DISCONNECT(1), MOVE(2), CHATMESSAGE(3), PING(4),
		READYSTATE(5), MAPCHOSEN(6), HOST(7), GAMESTART(8), TILECHANGED(9), BOMBPLACED(10),
		PLAYERKILLED(11), VICTORY(12), ITEMS(13), ITEMPICKUP(14);
		
		private int packetId;
		private PacketTypes(int packetId){
			this.packetId = packetId;
		}
		
		public int getId() {
			return packetId;
		}
	}
	
	public byte packetId;
	
	public Packet(int packetId) {
		this.packetId = (byte) packetId;
	}

	public abstract void writeData(GameClient client);
	
	public abstract void writeData(GameServer server);
	
	public String readData(byte[] data){
		String message = new String(data).trim();
		return message.substring(2);
	}
	
	public abstract byte[] getData();
	
	public static PacketTypes lookupPacket(String packetId){
		try {
			return lookupPacket(Integer.parseInt(packetId));
		} catch (NumberFormatException e) {
			return PacketTypes.INVALID;
		}
	}
	
	
	public static PacketTypes lookupPacket(int id){
		for (PacketTypes p : PacketTypes.values()){
			if (p.getId() == id) {
				return p;
			}
		}
		return PacketTypes.INVALID;
	}
	
}
