package com.draznel.bomberboy.net;

import com.draznel.bomberboy.packets.Packet10BombPlaced;

public class BombClient {

	public int type;
	public String placedBy;
	public int x, y;	
	public long explodeTimer;
	public long userTimePlaced;
	
	public long delta;
	
	public BombClient(int type, String placedBy, int x, int y, long explodeTimer, long timePlaced, long delta) {
		this.type = type;
		this.placedBy = placedBy;
		this.x = x;
		this.y = y;
		this.explodeTimer = explodeTimer;
		this.userTimePlaced = timePlaced;
		this.delta = delta;
	}
	
	public BombClient(Packet10BombPlaced packet, long delta) {
		this.type = packet.getType();
		this.placedBy = packet.getPlacedBy();
		this.x = packet.getX();
		this.y = packet.getY();
		this.explodeTimer = packet.getExplodeTimer();
		this.userTimePlaced = packet.getTimePlaced();
		this.delta = delta;
	}
	
	public boolean shouldExplode() {
		long currentTime = System.currentTimeMillis();
		if (userTimePlaced + delta <= currentTime) {
			return true;
		} else {
			return false;
		}
	}
}
