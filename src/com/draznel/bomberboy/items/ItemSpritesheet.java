package com.draznel.bomberboy.items;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.draznel.bomberboy.tiles.Tile;

public class ItemSpritesheet {

	public static final Texture spritesheet = new Texture(Gdx.files.internal("Sprites/items.png"));
	
	public static final Sprite Item_Spread_Sprite = new Sprite(spritesheet, 0, 0, Tile.SIZE, Tile.SIZE);
	public static final Sprite Item_Speed_Sprite = new Sprite(spritesheet, 0, 1 * Tile.SIZE, Tile.SIZE, Tile.SIZE);
	public static final Sprite Item_Bomb_Limit_Sprite = new Sprite(spritesheet, 0, 2 * Tile.SIZE , Tile.SIZE, Tile.SIZE);
	
	private ItemSpritesheet() {
	}
	
	public static final Sprite getSprite(int id, float x, float y) {
		Sprite spr = null;
		
		switch(id) {
		case ItemID.SPREAD:
			spr = Item_Spread_Sprite;
			break;
		case ItemID.SPEED:
			spr = Item_Speed_Sprite;
			break;
		case ItemID.BOMB_LIMIT:
			spr = Item_Bomb_Limit_Sprite;
			break;
		default:
			break;
		}

		
		spr.setPosition(x, y);
		return new Sprite(spr);
	}
}
