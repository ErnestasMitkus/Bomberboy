package com.draznel.bomberboy.screens;

import java.util.ArrayList;
import java.util.List;

import com.draznel.bomberboy.Main;
import com.draznel.bomberboy.entities.PlayerMP;
import com.draznel.bomberboy.level.Level;


public class ScreenManager {
	
	public final static int screenAmount = 1;
	
	public final static int MAINMENU = 0;
	public final static int LOBBYSCREEN = 1;
	public final static int LEVELSCREEN = 2;
	
	List<TickScreen> screens = new ArrayList<TickScreen>();
	
	TickScreen currentScreen;
	
	Main game;
	
	public ScreenManager(Main game) {
		this.game = game;
		
		screens.add(new MainMenu(game));
//		screens.add(new LobbyScreen(game));
//		screens.add(new LevelScreen(game, "Levels/classic.png"));
		
		currentScreen = screens.get(MAINMENU);
	}
	
	
	/**
	 * If for some reason programmer wants to put a random screen
	 * @param screen
	 */
	public void setScreen(TickScreen screen) {
		currentScreen = screen;
		game.setScreen(currentScreen);
	}
	
	public void setScreen(int screenID) {
		currentScreen.dispose();
		currentScreen = screens.get(screenID);
		game.setScreen(currentScreen);
	}
	
	public void newScreen(TickScreen screen) {
		currentScreen.dispose();
		currentScreen = screen;
		game.setScreen(screen);
	}
	
	public void newKickedScreen(TickScreen screen) {
		currentScreen.dispose();
		currentScreen = screen;
		game.setScreen(screen);
		((MainMenu) currentScreen).gotKicked();
	}
	
	
	public TickScreen getScreen() { return currentScreen; }
	
	public void nextScreen() {
		if (screens.contains(currentScreen)) {
			int nextIndex = screens.indexOf(currentScreen) + 1;
			if (nextIndex > screens.size() - 1) 
				nextIndex = 0;
			setScreen(nextIndex);
		}
	}
	
	public void loadLobby(Main game) {
		currentScreen.dispose();
		setScreen(new LobbyScreen(game));
	}
	
	public void loadGame(Main game, PlayerMP player) {
		currentScreen.dispose();
		setScreen(new LevelScreen(game, player));
	}

	public boolean shouldDoVictory = false;
	public String winner = "";
	
	public void victory(String username) {
		winner = username;
		shouldDoVictory = true;
	}
	
	public void doVictory() {
		shouldDoVictory = false;
		currentScreen.dispose();
//		game.resetLevel();
//		for (int i = 0; i < game.getLevel().getOnlinePlayersCount(); i++) {
//			System.out.println(i + ") " + game.getLevel().getPlayerByIndex(i).getUsername());
//		}
//		int index = game.getLevel().getPlayerMPIndex(game.getPlayer().getUsername());
//		if (index >= 0) {
//			game.getLevel().getPlayers().remove(index);
//		}
		setScreen(new LobbyScreen(game));
		((LobbyScreen) currentScreen).victory(winner);
		winner = "";
	}
	
}
