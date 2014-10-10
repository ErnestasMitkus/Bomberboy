package com.draznel.bomberboy.bombs;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.draznel.bomberboy.Main;
import com.draznel.bomberboy.level.Level;
import com.draznel.bomberboy.tiles.Tile;

public class Explosion {

	public static final int EXPLOSION = 0;
	public static final int EXPLOSION_H = 1;
	public static final int EXPLOSION_V = 2;
	
	public static final int EXPLOSION_SIZE = 32;
	public static final int EXPLOSION_WIDTH = 24;
	public static final int EXPLOSION_DURATION = 60;
	
	public static Sprite[] explosionSprite;
	
	public static Level level;
	
	public static List<ExplodeEvent> explosionEvents = new ArrayList<ExplodeEvent>();
	
	private Explosion() {
	}
	
	public static void setExplosionSprites(Sprite[] newExplosionSprite) {
		Explosion.explosionSprite = new Sprite[newExplosionSprite.length];
		for (int i = 0; i < 3 && i < newExplosionSprite.length; i++) {
			Explosion.explosionSprite[i] = new Sprite(newExplosionSprite[i]);
		}
	}
	
	public static void setExplosionSprites(Sprite newExplosion, Sprite newExplosionH, Sprite newExplosionV) {
		Explosion.explosionSprite = new Sprite[3];
		Explosion.explosionSprite[0] = new Sprite(newExplosion);
		Explosion.explosionSprite[1] = new Sprite(newExplosionH);
		Explosion.explosionSprite[2] = new Sprite(newExplosionV);
	}
	
	public static Sprite getExplosion(int ID) {
		return Explosion.explosionSprite[ID];
	}
	
	public static void setLevel(Level level) {		
		Explosion.level = level;
	}
	
	public static void explode(int x, int y, int spread, int power) {
//		System.out.println(spread);
		List<Bomb> bombs = level.getBombs();
		int bx, by;
		
		iteration: for (Bomb bomb : bombs) {
			if (!bomb.exploded()) {
				bx = (int) bomb.getX();
				by = (int) bomb.getY();
				if (by == y) {
					forYCheck: for (int dx = x - spread; dx <= x + spread; dx++) {
						if (bx == dx) {
							bomb.explode();
							continue iteration;
						} else if (level.getTile(dx, (Main.HEIGHT / Tile.SIZE) - y).isSolid()) {
							break forYCheck;
						}
					}
				}
				if (bx == x) {
					forXCheck: for (int dy = y - spread; dy <= y + spread; dy++) {
						if (by == dy) {
							bomb.explode();
							continue iteration;
						} else if (level.getTile(x, (Main.HEIGHT / Tile.SIZE) - dy).isSolid()) {
							break forXCheck;
						}
					}
				}
			}
		}
		
		int dx = x;
//		int dy = (int) (Math.floor((Main.HEIGHT / Tile.SIZE)) - y);
		int dy = (Main.HEIGHT / Tile.SIZE) - y;
		

//		System.out.println("Up: " + level.getTile(dx, dy - 1).getId());
//		System.out.println("Right: " + level.getTile(dx + 1, dy).getId());
//		System.out.println("Down: " + level.getTile(dx, dy + 1).getId());
//		System.out.println("Left: " + level.getTile(dx - 1, dy).getId());
//		System.out.println("[" + dx + ";" + dy + "]");
		
		explosionEvents.add(new ExplodeEvent(level, x, y, EXPLOSION, EXPLOSION_DURATION));
		boolean[] dirAvailable = {true, true, true, true};
		for (int i = 1; i <= spread; i++) {
			// UP
			if (dirAvailable[0]) {
				if (!level.getTile(dx, dy - i).isSolid() || level.getTile(dx, dy - i).isBreakable()) {
					explosionEvents.add(new ExplodeEvent(level, x, y + i, EXPLOSION_V, EXPLOSION_DURATION));
					if (level.getTile(dx, dy - i).getId() != Tile.ID_GRASS) {
						level.alterTile(dx, dy - i, Tile.GRASS);
						dirAvailable[0] = false;
					}
				} else {
					dirAvailable[0] = false;
				}
			}
			
			// RIGHT
			if (dirAvailable[1]) {
				if (!level.getTile(dx + i, dy).isSolid() || level.getTile(dx + i, dy).isBreakable()) {
					explosionEvents.add(new ExplodeEvent(level, x + i, y, EXPLOSION_H, EXPLOSION_DURATION));								
					if (level.getTile(dx + i, dy).getId() != Tile.ID_GRASS) {
						level.alterTile(dx + i, dy, Tile.GRASS);
						dirAvailable[1] = false;
					}
				} else {
					dirAvailable[1] = false;
				}
			}

			// BOTTOM
			if (dirAvailable[2]) {
				if (!level.getTile(dx, dy + i).isSolid() || level.getTile(dx, dy + i).isBreakable()) {
					explosionEvents.add(new ExplodeEvent(level, x, y - i, EXPLOSION_V, EXPLOSION_DURATION));								
					if (level.getTile(dx, dy + i).getId() != Tile.ID_GRASS) {
						level.alterTile(dx, dy + i, Tile.GRASS);
						dirAvailable[2] = false;
					}
				} else {
					dirAvailable[2] = false;
				}
			}
	
			// LEFT
			if (dirAvailable[3]) {
				if (!level.getTile(dx - i, dy).isSolid() || level.getTile(dx - i, dy).isBreakable()) {
					explosionEvents.add(new ExplodeEvent(level, x - i, y, EXPLOSION_H, EXPLOSION_DURATION));								
					if (level.getTile(dx - i, dy).getId() != Tile.ID_GRASS) {
						level.alterTile(dx - i, dy, Tile.GRASS);
						dirAvailable[3] = false;
					}
				} else {
					dirAvailable[3] = false;
				}
			}

			
		}
		
		
	}
	
	public static void tick() {
//		if (explosionEvents.size() > 0) {
//			System.out.println("Explosion Events size: " + explosionEvents.size());
//		}
		for (ExplodeEvent ee : explosionEvents) {
			ee.tick();
		}
		for (int i = 0; i < explosionEvents.size(); i++) {
			ExplodeEvent ee = explosionEvents.get(i);
			if (ee.needsDestroy) {
				explosionEvents.remove(ee);
				i--;
			}
		}
	}
	
	public static void render(Main game, SpriteBatch batch) {
		for (ExplodeEvent ee : explosionEvents) {
			ee.render(game, batch);
		}
	}

	public static void clear() {
		explosionEvents.clear();
	}
	
}
