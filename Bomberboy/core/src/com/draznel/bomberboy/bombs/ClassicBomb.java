package com.draznel.bomberboy.bombs;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.draznel.bomberboy.Main;
import com.draznel.bomberboy.entities.Player;
import com.draznel.bomberboy.gfx.AnimatedEntity;
import com.draznel.bomberboy.packets.Packet10BombPlaced;
import com.draznel.bomberboy.tiles.Tile;

public class ClassicBomb extends Bomb {

	public static final int DEFAULT_EXPLODE_TIMER = 2000; // 2s

	AnimatedEntity animations;
	
	int currentAnimation;
	long fuseTimer;
	
	int spread = 1;
	
	long EXPLODE_TIMER = DEFAULT_EXPLODE_TIMER;
	long timePlaced;
	
	boolean initiated = false;
	
	float changeEveryXMilliSeconds = 428f; // default. will change accordingly

	Rectangle rect;
	
	public ClassicBomb(Player player) {
		super(player.getUsername(), 0, 0f, false);
		
		Rectangle playerBounds = player.getBounds();
		float dx = playerBounds.x + playerBounds.width / 2;
		float dy = playerBounds.y + playerBounds.height;
		this.x = (int) (dx / Tile.SIZE);
		this.y = (int) (dy / Tile.SIZE);
		
		this.spread = player.getPowers().getSpread();
		timePlaced = System.currentTimeMillis();
		
		animations = new AnimatedEntity("Sprites/tiles.png", 0, 0, 32, 8);
		currentAnimation = 0;
		changeEveryXMilliSeconds = EXPLODE_TIMER / (animations.getAnimationCount() - 1);
		
		rect = new Rectangle(dx, dy - 12, 32, 32);
	}
	
	public ClassicBomb(String username, int spread, int x, int y, long explodeTimer, long timePlaced) {
		super(username, x, y, false);
		this.x = x;
		this.y = y;
		this.spread = spread;
		
		EXPLODE_TIMER = explodeTimer;
		this.timePlaced = timePlaced;
		
		
		animations = new AnimatedEntity("Sprites/tiles.png", 0, 0, 32, 8);
		currentAnimation = 0;
		changeEveryXMilliSeconds = EXPLODE_TIMER / (animations.getAnimationCount() - 1);
		setFuse(EXPLODE_TIMER + timePlaced);
		
		rect = new Rectangle(x * Tile.SIZE, y * Tile.SIZE - 12, 32, 32);
	}
	
	public void setFuse(long fuseTimer) {
		this.fuseTimer = fuseTimer;
		initiated = true;
	}
	
	public Sprite getAnimation() {
		return animations.getAnimationAt(currentAnimation);
	}
	
	
	public long getFuseTimer() {
		return fuseTimer;
	}
	
	public long getTimeLeft() {
		return fuseTimer - System.currentTimeMillis();
	}

	
	@Override
	public void tick() {
		if (!initiated) { 
			// Send packet to initiate?
			return;
		}
		if (getTimeLeft() <= 0) {
			explode();
			return;
		}
		
		currentAnimation = (int) ((EXPLODE_TIMER - getTimeLeft()) / changeEveryXMilliSeconds) + 1;
//		currentAnimation = currentAnimation > 
//		currentAnimation = (animations.getAnimationCount() - 1) - currentAnimation;
		if (currentAnimation < 0) currentAnimation = 0;
		if (currentAnimation >= animations.getAnimationCount()) currentAnimation = animations.getAnimationCount() - 1;
	}

	@Override
	public void render(SpriteBatch batch) {
//		System.out.println(animations.getAnimationAt(0) == null);
		
		Sprite spr = animations.getAnimationAt(currentAnimation);
		
		spr.setPosition(x * Tile.SIZE, y * Tile.SIZE - 12);
		spr.draw(batch);
	}
	
	public void explode() {
		exploded = true;
		Explosion.explode(Math.round(x), Math.round(y), spread, CLASSIC_BOMB_POWER);
//		System.out.println("Explode at: " + x + " " + y);
	}

	public Rectangle getBounds() {
		return rect;
	}
	
	
}
