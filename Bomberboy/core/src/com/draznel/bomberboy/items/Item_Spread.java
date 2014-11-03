package com.draznel.bomberboy.items;

import com.draznel.bomberboy.entities.Player;

public class Item_Spread {

	private Item_Spread() {
	}

	public static void onCollision(Player player) {
		player.getPowers().incSpread();
	}
	
	

}
