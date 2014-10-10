package com.draznel.bomberboy.tiles;


public class BasicSolidTile extends BasicTile {

	public BasicSolidTile(int id, int x, int y, boolean breakable, int levelColor) {
		super(id, x, y, levelColor);
		this.solid = true;
		this.breakable = breakable;
		
		//System.out.println("ID: " + id + " x: " + x + " y: " + y);
	}
}
