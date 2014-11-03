package com.draznel.bomberboy.items;

import com.draznel.bomberboy.entities.Player;

public class Item_Bomb_Limit {

	private Item_Bomb_Limit() {
	}
	
	public static void onCollision(Player player) {
		player.getPowers().incMaxBombs();
	}
	
}
