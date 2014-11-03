package com.draznel.bomberboy.gfx;

public class Powers {

	public static final int MAX_SPREAD = -1;
	public static final int MAX_MOVSPEED = 5;
	public static final int MAX_BOMBS = 10;
	
	int spread;
	float movSpeed;
	int maxBombs;
	
	public Powers() {
		reset();
	}
	
	@SuppressWarnings("unused")
	public void incSpread() {
		this.spread++;
		if (MAX_SPREAD > 0 && spread > MAX_SPREAD) {
			this.spread = MAX_SPREAD;
		}
	}
	@SuppressWarnings("unused")
	public void setSpread(int spread) {
		this.spread = spread;
		if (MAX_SPREAD > 0 && spread > MAX_SPREAD) {
			this.spread = MAX_SPREAD;
		}
	}
	public int getSpread() {
		return spread;
	}
	
	public void incMovSpeed(float movSpeed) {
		this.movSpeed += movSpeed;
		if (MAX_MOVSPEED > 0 && this.movSpeed > MAX_MOVSPEED) {
			this.movSpeed = MAX_MOVSPEED;
		}
	}
	public void setMovSpeed(float movSpeed) {
		this.movSpeed = movSpeed;
		if (MAX_MOVSPEED > 0 && this.movSpeed > MAX_MOVSPEED) {
			this.movSpeed = MAX_MOVSPEED;
		}
	}
	public float getMovSpeed() {
		return movSpeed;
	}
	
	
	public void incMaxBombs() {
		maxBombs++;
		if (MAX_BOMBS > 0 && this.maxBombs > MAX_BOMBS) {
			this.maxBombs = MAX_BOMBS;
		}
	}
	public void setMaxBombs(int maxBombs) {
		this.maxBombs = maxBombs;
		if (MAX_BOMBS > 0 && this.maxBombs > MAX_BOMBS) {
			this.maxBombs = MAX_BOMBS;
		}
	}
	public int getMaxBombs() {
		return maxBombs;
	}

	public void reset() {
		spread = 1;
		movSpeed = 3.0f;
		maxBombs = 1;
	}
	
}
