package com.draznel.bomberboy.tiles;


import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public abstract class Tile {

	byte id;
	boolean solid = false;
	boolean breakable = false;
	
	public static final int SIZE = 32;
	
	public static final int ID_VOID = 0;
	public static final int ID_GRASS = 1;
	public static final int ID_STONE = 2;
	public static final int ID_OBSIDIAN = 3;
	public static final int ID_SPAWN = 4;
	// Add ID to Lobby screen's "getColorFromId()"
	
	public static final Tile[] tiles = new Tile[256];
	public static final Tile VOID = new BasicSolidTile(ID_VOID, 0, 7, false, 0xFF010101);
	public static final Tile GRASS = new BasicTile(ID_GRASS, 1, 7, 0xFF00FF00);
	public static final Tile STONE = new BasicSolidTile(ID_STONE, 2, 7, true, 0xFF999999);
	public static final Tile OBSIDIAN = new BasicSolidTile(ID_OBSIDIAN, 3, 7, false, 0xFF000000);
	public static final Tile SPAWN_POINT = new BasicTile(ID_SPAWN, 1, 7, 0xFFFF0000);
	
	private int levelColor;
	private Sprite sprite;
	
	public static final String tilePath = "Sprites/tiles.png";
	
	protected int x;
	protected int y;
	
	public Tile(int id, boolean isSolid, int levelColor) {
		this.id = (byte) id;
		if (tiles[id] != null) throw new RuntimeException("Duplicate tile id on " + id);
		this.solid = isSolid;
		this.levelColor = levelColor;
		tiles[id] = this;
	}
	
	public byte getId() {
		return id;
	}
	
	public boolean isSolid() {
		return solid;
	}
	
	public boolean isBreakable() {
		return breakable;
	}

	public int getLevelColor() {
		return levelColor;
	}
	
	public Sprite getSprite() {
		return sprite;
	}
	
	public void setSprite(Sprite sprite) {
		this.sprite = sprite;
	}
	
	public static Tile getById(int id) {
		switch (id) {
		case ID_GRASS:
			return GRASS;
		case ID_STONE:
			return STONE;
		case ID_OBSIDIAN:
			return OBSIDIAN;
		case ID_VOID:
		default:
			return VOID;
		}
	}
	
	public static int getColorFromId(int id) {
		switch(id) {
		case ID_GRASS:
			return 0x00FF00;
		case ID_STONE:
			return 0x999999;
		case ID_OBSIDIAN:
			return 0x000000;
		case ID_SPAWN:
			return 0xFF0000;
		case ID_VOID:
			return 0xFF010101;
		default:
			return 0x000000;
		}
	}
	
	public abstract void tick();
	
	public abstract void render(SpriteBatch batch);
}
