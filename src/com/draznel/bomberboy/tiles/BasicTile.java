package com.draznel.bomberboy.tiles;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class BasicTile extends Tile {

	protected int tileId;
	
	public BasicTile(int id, int x, int y, int levelColor) {
		super(id, false, levelColor);
		Texture texture = new Texture(tilePath);
		setSprite(new Sprite(texture, x * Tile.SIZE, y * Tile.SIZE, Tile.SIZE, Tile.SIZE));
		//this.tileId = x + y * 32;
	}

	public void tick(){
		
	}

	@Override
	public void render(SpriteBatch batch) {
		// TODO Auto-generated method stub
		getSprite().draw(batch);
	}

}