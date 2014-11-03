package com.draznel.bomberboy.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

import com.draznel.bomberboy.packets.Packet;
import com.draznel.bomberboy.packets.Packet.PacketTypes;
import com.draznel.bomberboy.packets.Packet06MapChosen;

public class GameTCPServer extends Thread {
	GameServer GS = null;
	int port;
	public ServerSocket serverSocket;
	
	public GameTCPServer(GameServer GS) {
		this.GS = GS;
		this.port = GS.SERVER_PORT;
	}
	
	public void run() {
		try {
			serverSocket = new ServerSocket(this.port);
			serverSocket.setSoTimeout(10000);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Socket socketTCP = null;
		while (GS.running) {
			try {
			socketTCP = serverSocket.accept();
			// Connected!
			DataInputStream in = new DataInputStream(socketTCP.getInputStream());
			byte[] buffer = new byte[32768]; //32KB
			in.read(buffer);
			
			GS.parsePacket(buffer, socketTCP.getInetAddress(), socketTCP.getPort());
			
			socketTCP.close();
			
			} catch (SocketTimeoutException s) {
				System.out.println("Socket has timed out");
//				break;
			} catch (Exception e) {
				e.printStackTrace();
				break;
			}
		}
		try {
			socketTCP.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
//	private void handleMapChosen(Packet06MapChosen packet) {
//		if (true || packet.getSenderName().equalsIgnoreCase(GS.currentHost)) {
//			if (!GS.inGame) {
//				if (packet.getMapName().length() == 0) {
//					// Requesting current map
//					PlayerClient PC;
//					for (int i = 0; i < GS.connectedPlayers.size(); i++) {
//						PC = GS.connectedPlayers.get(i);
//						if (PC.getUsername().equalsIgnoreCase(packet.getSenderName())) {
//							String tilesToString = "";
//							for (int i1 = 0; i1 < GS.map.length; i1++) {
//								char a = (char) (GS.map[i1] + 'a');
//								tilesToString += a;
//							}
//							Packet06MapChosen returnPacket = new Packet06MapChosen("System", GS.mapName, GS.mapWidth, GS.mapHeight, tilesToString);
//							sendTCPData(returnPacket.getData(), PC.ipAddress, PC.port);
////							sendData(returnPacket.getData(), PC.ipAddress, PC.port);
//						}
//					}
//					
//				} else {
//					System.out.println("Map changed to " + GS.mapName);
//					GS.mapName = packet.getMapName();
//					GS.mapWidth = packet.getWidth();
//					GS.mapHeight = packet.getHeight();
//					
//					GS.map = new byte[GS.mapWidth * GS.mapHeight];
//					String tilesString = packet.getTiles();
//					int maxIndex = tilesString.length();
//					for (int i = 0; i < maxIndex; i++) {
////							System.ouErt.println(tilesString.length() + " : " + tilesString.charAt(0) + " " + tilesString.charAt(tilesString.length() - 2) + " " + tilesString.charAt(tilesString.length() - 1));
////							System.out.println("map-length: " + map.length + " tiles-length: " + tilesString.length() + "  with current i: " + i);
//						GS.map[i] = (byte) (tilesString.charAt(i) - 'a');
//					}
//					
////						System.out.println("Tiles byte array length: " + map.length);
////						System.out.println("Packet string data: " + packet.getTiles());
//					
////						packet.writeData(this);
//					sendTCPDataToAll(packet.getData());
//				}
//			}
//		}
//	}
	
	public void sendTCPData(byte[] data, InetAddress ip, int port) {
		try {
//			System.out.println("Requesting connection on " + ip + ":" + port);
			Socket socket = new Socket(ip, port);
//			System.out.println("Connected!");
			// Connected!
			
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			out.write(data);
			
			socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void sendTCPDataToAll(byte[] data) {
		PlayerClient PC;
		for (int i = 0; i < GS.connectedPlayers.size(); i++) {
			PC = GS.connectedPlayers.get(i);
			
			try {
				Socket socket = new Socket(PC.ipAddress, PC.port);
				// Connected!
				
				DataOutputStream out = new DataOutputStream(socket.getOutputStream());
				out.write(data);
				
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	// END OF TCP
}
