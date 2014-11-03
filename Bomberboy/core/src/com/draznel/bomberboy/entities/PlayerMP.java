package com.draznel.bomberboy.entities;

import java.net.InetAddress;

import com.draznel.bomberboy.Main;

public class PlayerMP extends Player {

	public InetAddress ipAddress = null;
	public int port = -1;
	
	public PlayerMP(String username, float x, float y, InetAddress ip, int port) {
		super(username, x, y);
		this.ipAddress = ip;
		this.port = port;
	}
	
	public PlayerMP(String username, float x, float y) {
		super(username, x, y);
	}

	@Override
	public void tick() {
		super.tick();
	}

}
