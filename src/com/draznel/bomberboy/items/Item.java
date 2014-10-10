package com.draznel.bomberboy.items;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.draznel.bomberboy.Main;
import com.draznel.bomberboy.entities.Entity;
import com.draznel.bomberboy.entities.Player;
import com.draznel.bomberboy.level.Level;
import com.draznel.bomberboy.packets.Packet14ItemPickup;
import com.draznel.bomberboy.tiles.Tile;

public class Item extends Entity {

	protected int id;
	protected Rectangle bounds;
	
	public boolean alreadyUsed = false;
	
	private boolean dontCollide = false;
	
	public Item(int id, int x, int y) {
		super(x, y, false);
		this.id = id;
		this.bounds = new Rectangle(x << 5, Main.HEIGHT - (y << 5) - 32, Tile.SIZE, Tile.SIZE);
	}
	
	public void changeID(int newId) {
		this.id = newId;
	}
	
	public int getID() {
		return id;
	}
	
	public Rectangle getBounds() {
		return bounds;
	}
	
	public void render(SpriteBatch batch) {
		if (id == ItemID.NOTHING) return;
		Sprite spr = ItemSpritesheet.getSprite(id, ((int)x) << 5, Main.HEIGHT - (((int)y) << 5) - 32);
		if (spr != null) {
			spr.draw(batch);
		}
	}
	
	public void tick(Level level) {
		if (dontCollide) return;
		
		Player p = level.getPlayer();
		
		
		if (p.getBounds().overlaps(getBounds())) {
			onCollision(p);
			return;
		}
		
		
//		ArrayList<Player> alivePlayers = level.getAlivePlayers();
//		for (int i = 0; i < alivePlayers.size(); i++) {
//			p = alivePlayers.get(i);
//			if (p.getBounds().overlaps(getBounds())) {
//				onCollision(p);
//				return;
//			}
//		}
	}
	
	public void onCollision(Player player) {
		dontCollide = true;
		
		Packet14ItemPickup pickPacket = new Packet14ItemPickup(player.getUsername(), id, (int) x, (int) y);
		pickPacket.writeData(player.getLevel().getGameClient());
	}
	
	public boolean isAlreadyUsed() {
		return alreadyUsed;
	}
	
	public static void awardPlayer(Player player, int id) {
		switch(id) {
		case ItemID.SPREAD:
			Item_Spread.onCollision(player);
			break;
		case ItemID.SPEED:
			Item_Speed.onCollision(player);
			break;
		case ItemID.BOMB_LIMIT:
			Item_Bomb_Limit.onCollision(player);
			break;
		default:
			System.out.println("Unexpected item. ID: " + id);
			return;
		}
	}

	

}
