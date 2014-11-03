package com.draznel.bomberboy.gfx;

public class Map {

	private int width, height;
	private byte[] tiles;
	private int[] mapColors;
	
	public boolean ready = false;
	
	private boolean tilesSet = false;
	private boolean mapColorsSet = false;
	
	public Map(int width, int height) {
		this.width = width;
		this.height = height;
		this.tiles = new byte[width * height];
		this.mapColors = new int[width * height];
		ready = false;
		tilesSet = false;
		mapColorsSet = false;
	}
	
	public void setTiles(byte[] tiles) {
		this.tiles = tiles;
		tilesSet = true;
		if (tilesSet && mapColorsSet) {
			ready = true;
		}
	}
	public void setMapColors(int[] mapColors) {
		this.mapColors = mapColors;
		mapColorsSet = true;
		if (tilesSet && mapColorsSet) {
			ready = true;
		}
	}
	
	public int getWidth() {
		return width;
	}
	public int getHeight() {
		return height;
	}
	public byte[] getTiles() {
		return tiles;
	}
	public int[] getMapColors() {
		return mapColors;
	}
}
