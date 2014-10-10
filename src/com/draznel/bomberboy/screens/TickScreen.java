package com.draznel.bomberboy.screens;

import com.badlogic.gdx.Screen;
import com.draznel.bomberboy.gfx.ChatBox;
import com.draznel.bomberboy.level.Level;

public interface TickScreen extends Screen {
	
	public void tick();
	public Level getLevel();
	public boolean needToSendDisconnectPacket();
	
	public void printMessage(String message);
	
}
