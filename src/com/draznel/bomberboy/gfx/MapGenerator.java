package com.draznel.bomberboy.gfx;

import java.util.Random;

import com.draznel.bomberboy.tiles.Tile;

public class MapGenerator {

	private MapGenerator() {
	}
	
	public static Map generateMap(int width, int height, int[] chances, int maxPlayers, long seed) {
		Map map = new Map(width, height);
		tiles = new byte[width * height];
		mapColors = new int[width * height];
		
		int allChances = 0;
		for (int i = 0; i < chances.length; i++) {
			allChances += chances[i];
		}
		
		int[] blocks = new int[allChances];
		int counter = 0;
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < chances[i]; j++) {
				blocks[counter++] = i;
			}
		}

		Random rand;
		if (seed != 0) {
			rand = new Random(seed);
		} else {
			rand = new Random();
		}
		
		for (int y = 0; y < height; y++){
			for (int x = 0; x < width; x++){
				int tileId = blocks[(int) Math.round((rand.nextFloat() * (allChances - 1)))];
				generateTile(x, y, width, Tile.getById(tileId));
			}
		}
		
		for (int x = 0; x < width; x++) {
			generateTile(x, 0, width, Tile.OBSIDIAN);
			generateTile(x, height - 1, width, Tile.OBSIDIAN);
		}
		for (int y = 0; y < height; y++) {
			generateTile(0, y, width, Tile.OBSIDIAN);
			generateTile(width - 1, y, width, Tile.OBSIDIAN);
		}
		
		map.setTiles(tiles);
		map.setMapColors(mapColors);
//		fixMap(map, maxPlayers);
		
		return map;
	}
	
	private static void fixMap(Map map, int maxPlayers) {
		// Every breakable block is reachable
//		int width = map.getWidth();
//		int height = map.getHeight();
//		byte[] tiles = map.getTiles();
//		int[] mapColors = map.getMapColors();
//		
//		boolean[] been = new boolean[width * height];
//		for (boolean b : been) b = false;
//		
//		int[] a = {0, 1, 0, -1};
//		int[] b = {1, 0, -1, 0};
//
//		for (int x = 0; x < width; x++) {
//			for (int y = 0; y < height; y++) {
//				int index = x + y * width;
//				if (!been[index] && Tile.getById(tiles[index]).isBreakable()) {
//					
//					been[index] = true;
//					
//					for (int k = 0; k < 4; k++) {
//						int dx = x + a[k];
//						int dy = y + b[k];
//						if (dx >= 0 && dx < width && dy >= 0 && dy < height) {
//							int ind = 
//							
//						}
//					}
//				}
//			}
//		}
		
		
		// Make spawn points
		
		
		map.setTiles(tiles);
		map.setMapColors(mapColors);
	}
	
	private static byte[] tiles;
	private static int[] mapColors;
	
	private static void generateTile(int x, int y, int width, Tile tile) {
		tiles[x + y * width] = tile.getId();
		mapColors[x + y * width] = tile.getLevelColor();
	}
}
