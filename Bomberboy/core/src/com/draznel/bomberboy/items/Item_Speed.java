package com.draznel.bomberboy.items;

import com.draznel.bomberboy.entities.Player;

public class Item_Speed {

	private Item_Speed() {
	}
	
	public static void onCollision(Player player) {
		player.getPowers().incMovSpeed(0.2f);
	}
	
}
