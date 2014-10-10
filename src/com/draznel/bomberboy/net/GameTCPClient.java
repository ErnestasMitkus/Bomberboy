package com.draznel.bomberboy.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class GameTCPClient extends Thread {

	GameClient GC = null;
	int port;
	ServerSocket serverSocket;
	
	public GameTCPClient(GameClient GC) {
		this.GC = GC;
		this.port = GC.port;
	}
	
	public void run() {
		try {
			serverSocket = new ServerSocket(this.port);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		while (GC.isRunning()) {
			try {
//				System.out.println("Waiting for server to send a request on " + serverSocket.getInetAddress() + ":" + serverSocket.getLocalPort());
				Socket socketTCP = serverSocket.accept();
//				System.out.println("Got a connection request!!!");
				// Connected!
				
				DataInputStream in = new DataInputStream(socketTCP.getInputStream());
				byte[] buffer = new byte[32768]; //32KB
				in.read(buffer);
				
				GC.parsePacket(buffer, socketTCP.getInetAddress(), socketTCP.getPort());
				
				socketTCP.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		try {
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void sendTCPData(byte[] data) {
		Socket socketTCP;
		try {
			socketTCP = new Socket(GC.ipAddress, GC.SERVER_PORT);

			DataOutputStream out = new DataOutputStream(socketTCP.getOutputStream());
			out.write(data);

			socketTCP.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
