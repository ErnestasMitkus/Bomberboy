package com.draznel.bomberboy.entities;

public class Entity {

	protected float x = 0f;
	protected float y = 0f;
	protected boolean solid = false;
	
	public Entity(float x, float y, boolean isSolid) {
		this.x = x;
		this.y = y;
		solid = isSolid;
	}
	
	public void setX(float x) {
		this.x = x;
	}
	
	public void setY(float y) {
		this.y = y;
	}
	
	public float getX() {
		return x;
	}
	
	public float getY() {
		return y;
	}
	
	public void setSolid(boolean solid) {
		this.solid = solid;
	}
	public boolean isSolid() {
		return solid;
	}

	public void tick() {}
	
}
