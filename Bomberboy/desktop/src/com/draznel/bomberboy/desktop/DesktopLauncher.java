package com.draznel.bomberboy.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.draznel.bomberboy.Main;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.resizable = false;
		config.title = Main.TITLE;
		config.width = Main.WIDTH;
		config.height = Main.HEIGHT;
		
		config.backgroundFPS = 1000;
		config.foregroundFPS = 1000;
		
//		config.fullscreen = true;
//		config.width = config.getDesktopDisplayMode().width;
//		config.height = config.getDesktopDisplayMode().height;
		
//		System.out.println();
		
		new LwjglApplication(new Main(), config);
	}
}
