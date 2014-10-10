package com.draznel.bomberboy.bombs;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.draznel.bomberboy.Main;
import com.draznel.bomberboy.entities.Player;
import com.draznel.bomberboy.level.Level;
import com.draznel.bomberboy.packets.Packet11PlayerKilled;
import com.draznel.bomberboy.tiles.Tile;

public class ExplodeEvent {

	public int ID;
	public int x, y;
	
	public Sprite sprite;
	public Rectangle fireBounds;
	
	public int ticksLeft;
	
	Level level = null;
	
	public boolean needsDestroy = false;
	
	public ExplodeEvent(Level level, int x, int y, int ID, int ticksTillDissapear) {
		this.level = level;
//		System.out.println("[" + x + ";" + ((Main.HEIGHT / Tile.SIZE) - y) + "] ID: " + ID);

		this.x = x;
		this.y = (Main.HEIGHT / Tile.SIZE) - y;
		this.ID = ID;
		this.ticksLeft = ticksTillDissapear;
		
		sprite = new Sprite(Explosion.getExplosion(ID));
		sprite.setPosition(x << 5, (y << 5) - 12);
		if (!level.getTile(x, y).isSolid() || level.getTile(x, y).isBreakable()) {
			level.alterTile(x, (Main.HEIGHT / Tile.SIZE) - y, Tile.GRASS);			
		}
		
		createRect();
	}
	
	private void createRect() {
		float dx = x * Tile.SIZE;
		float dy = y * Tile.SIZE;
		if (ID == Explosion.EXPLOSION) {
			fireBounds = new Rectangle(dx, dy, Explosion.EXPLOSION_SIZE, Explosion.EXPLOSION_SIZE);
		} else if (ID == Explosion.EXPLOSION_H) {
			fireBounds = new Rectangle(dx, dy + ((Explosion.EXPLOSION_SIZE - Explosion.EXPLOSION_WIDTH) / 2), Explosion.EXPLOSION_SIZE, Explosion.EXPLOSION_WIDTH);
		} else {
			fireBounds = new Rectangle(dx + ((Explosion.EXPLOSION_SIZE - Explosion.EXPLOSION_WIDTH) / 2), dy, Explosion.EXPLOSION_WIDTH, Explosion.EXPLOSION_SIZE);
		}
	}
	
	public Rectangle getRect() {
		return fireBounds;
	}
	
	public void tick() {
		if (--ticksLeft <= 0) {
			needsDestroy = true;
			return;
		}
		
		Rectangle fireBounds = getRect();
		fireBounds.y = -(fireBounds.y - Main.HEIGHT) - Tile.SIZE + 3f;
		
		for (int i = 0; i < level.getOnlinePlayersCount(); i++) {
			Player p = (Player) level.getPlayerByIndex(i);
			if (!p.alive) continue;
			
			if (p.getBounds().overlaps(fireBounds)) {
				p.alive = false;
//				if (level.getPlayer().getUsername().equals(p.getUsername())) {					
//					p.sendDeathPacket();
				level.getPlayer().sendDeathPacket(p.getUsername());
//				}
				// Packet that player died.
			}
		}

	}
	
	public void render(Main game, SpriteBatch batch) {
	
		sprite.draw(batch);
		
	}
	
	
	
}
