package com.draznel.bomberboy.bombs;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.draznel.bomberboy.entities.Entity;
import com.draznel.bomberboy.entities.Player;


public abstract class Bomb extends Entity {

	public static final int CLASSIC_BOMB_POWER = 1;
	
	public static final int TYPE_CLASSIC = 0;
	
	protected String placedBy;
	
	boolean exploded = false;
	
	public Bomb(String placedBy, float x, float y, boolean solid) {
		super(x, y, solid);
		this.placedBy = placedBy;
	}
	
	public String getPlacedBy() {
		return placedBy;
	}
	
	public boolean exploded() {
		return exploded;
	}
	
	public abstract void explode();
	public abstract void tick();
	public abstract void render(SpriteBatch batch);
	
}
