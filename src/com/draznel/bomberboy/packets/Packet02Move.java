package com.draznel.bomberboy.packets;

import com.draznel.bomberboy.net.GameClient;
import com.draznel.bomberboy.net.GameServer;

public class Packet02Move extends Packet {

	private String username;
	private float x, y;
	private int currentAnimation;
	private int currentFrame;
	
	public Packet02Move(byte[] data) {
		super(2);
		String[] dataArray = readData(data).split(",");
		this.username = dataArray[0];
		this.x = Float.parseFloat(dataArray[1]);
		this.y = Float.parseFloat(dataArray[2]);
		this.currentAnimation = Integer.parseInt(dataArray[3]);
		this.currentFrame = Integer.parseInt(dataArray[4]);
	}
	
	public Packet02Move(String username, float moveX, float moveY, int currentAnimation, int currentFrame) {
		super(2);
		this.username = username;
		this.x = moveX;
		this.y = moveY;
		this.currentAnimation = currentAnimation;
		this.currentFrame = currentFrame;
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
		return ("02" + this.username + "," + this.x + "," + this.y + "," + currentAnimation +
											"," + currentFrame).getBytes();
		
	}
	
	public String getUsername() {
		return username;
	}
	
	public float getX(){
		return this.x;
	}
	
	public float getY(){
		return this.y;
	}
	
	public int getCurrentAnimation() {
		return currentAnimation;
	}
	
	public int getCurrentFrame() {
		return currentFrame;
	}


	
	
}
