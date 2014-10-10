package com.draznel.bomberboy.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.draznel.bomberboy.Main;
import com.draznel.bomberboy.entities.Player;
import com.draznel.bomberboy.gfx.Font;
import com.draznel.bomberboy.input.LwjglInput;
import com.draznel.bomberboy.level.Level;
import com.draznel.bomberboy.packets.Packet00Login;

public class MainMenu implements TickScreen {

	Main game;
	
	SpriteBatch batch;
	
	LwjglInput input;
	
	Texture bgTexture;
	Sprite bgSprite;
	
	private static String[] str = {"Enter Your Name: ", "Enter IP Address: ", "Enter Port: ", "Connecting...", "Connection failed. Retrying... [1/5]", "Connection Failed."};
	private String enteringStr = "";
	private String redErrorMsg = "";
	
	public static final int STATE_NAME = 0;
	public static final int STATE_IP = 1;
	public static final int STATE_PORT = 2;
	public static final int STATE_CONNECT = 3;
	public static final int STATE_CONNECT_FAILED = 4;
	public static final int STATE_CONNECT_ABORT = 5;
	
	private int currentState = STATE_NAME;
	
	public static final int MAX_NAME_LENGTH = 20;
	public static final int MAX_IP_LENGTH = 15;
	public static final int MAX_PORT_LENGTH = 5;
	
	
	BitmapFont font;
	BitmapFont sFont;
	
	private String ipAddress;
	private int port;
	
	private boolean connected = false;
	
	public MainMenu(Main game) {
		this.game = game;
		batch = new SpriteBatch();
		
		Font fontFactory = new Font();
		
		font = fontFactory.getFontBySize(24);
		sFont = fontFactory.getFontBySize(16);
		game.resetLevel();
	}
	public boolean needToSendDisconnectPacket() { return true; }
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		batch.draw(bgTexture, 0, 0);
		if (redErrorMsg.length() > 0) {
			sFont.setColor(1.0f, 0.0f, 0.0f, 1.0f);
			sFont.draw(batch, redErrorMsg, (Main.WIDTH - sFont.getBounds(redErrorMsg).width ) / 2, Main.HEIGHT - 50);
		}
		font.setColor(1.0f, 1.0f, 1.0f, 1.0f);
		font.draw(batch, str[currentState], (Main.WIDTH - font.getBounds(str[currentState]).width ) / 2, Main.HEIGHT - 100);
		font.draw(batch, enteringStr, (Main.WIDTH - font.getBounds(enteringStr).width) / 2, Main.HEIGHT - 150);
		batch.end();
	}

	private void throwRedError(String msg) {
		redErrorMsg = msg;
	}
	
	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void show() {
		showBackground();
		
		input = game.getInput();
		input.autoReset = false;
	}
	
	private void showBackground() {
		bgTexture = new Texture("Images/background.png");
		bgTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
	}

	@Override
	public void hide() {
		dispose();
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
	}

	@Override
	public void tick() {
		switch(currentState) {
		case STATE_NAME:
			tickName();
			break;
		case STATE_IP:
			tickIP();
			break;
		case STATE_PORT:
			tickPort();
			break;
		case STATE_CONNECT:
		case STATE_CONNECT_FAILED:
			tickConnect();
			break;
		case STATE_CONNECT_ABORT:
			tickConnectAbort();
		default:
			break;
		}
		
		if (enteringStr.length() > 0) {
			redErrorMsg = "";
		}
		
	}
	
	private void tickName() {
		if (input != null) {
			
			if (input.isPressed(Keys.ENTER)) {
				if (enteringStr.length() < 3) {
					enteringStr = "";
					input.resetKey(Keys.ENTER);
					throwRedError("Name must be equal or longer than 3 letters");
				} else {
					game.getPlayer().setName(enteringStr);
					currentState = STATE_IP;
					enteringStr = "";
//					currentState = STATE_CONNECT;
//					connect();
					input.resetKey(Keys.ENTER);
				}
			}
			if (input.isPressed(Keys.BACKSPACE)) {
				if (enteringStr.length() > 0) {
					enteringStr = enteringStr.substring(0, enteringStr.length() - 1);					
				}
				input.resetKey(Keys.BACKSPACE);
			}
			
			for (int i = Keys.A; i <= Keys.Z; i++) {
				if (input.isPressed(i)) {
					if (enteringStr.length() < MAX_NAME_LENGTH) {
						if (input.isPressed(Keys.SHIFT_LEFT) || input.isPressed(Keys.SHIFT_RIGHT)) {
							enteringStr += Keys.toString(i);						
						} else {
							enteringStr += Keys.toString(i).toLowerCase();
						}
					}
					input.resetKey(i);
				}
			}
			for (int i = Keys.NUM_0; i <= Keys.NUM_9; i++) {
				if (input.isPressed(i)) {
					if (enteringStr.length() < MAX_NAME_LENGTH) {
						enteringStr += Keys.toString(i);
					}
					input.resetKey(i);
				}
			}
			for (int i = Keys.NUMPAD_0; i <= Keys.NUMPAD_9; i++) {
				if (input.isPressed(i)) {
					String temp = Keys.toString(i);
					if (enteringStr.length() < MAX_NAME_LENGTH) {
						enteringStr += temp.substring(temp.length() - 1, temp.length());
					}
					input.resetKey(i);
				}
			}
		}
	}

	private void tickIP() {
		// Ctrl + V??
		if (game.getInput() != null) {
			LwjglInput input = game.getInput();
			
			if (input.isPressed(Keys.ENTER)) {
				System.out.println("IP: " + enteringStr);
				ipAddress = enteringStr;
				enteringStr = "";
				currentState = STATE_PORT;
				input.resetKey(Keys.ENTER);
			}
			if (input.isPressed(Keys.BACKSPACE)) {
				if (enteringStr.length() > 0) {
					enteringStr = enteringStr.substring(0, enteringStr.length() - 1);					
				}
				input.resetKey(Keys.BACKSPACE);
			}
			for (int i = Keys.A; i <= Keys.Z; i++) {
				if (input.isPressed(i)) {
					if (enteringStr.length() < MAX_NAME_LENGTH) {
						if (input.isPressed(Keys.SHIFT_LEFT) || input.isPressed(Keys.SHIFT_RIGHT)) {
							enteringStr += Keys.toString(i);						
						} else {
							enteringStr += Keys.toString(i).toLowerCase();
						}
					}
					input.resetKey(i);
				}
			}
			for (int i = Keys.NUM_0; i <= Keys.NUM_9; i++) {
				if (input.isPressed(i)) {
					if (enteringStr.length() < MAX_IP_LENGTH) {
						enteringStr += Keys.toString(i);
					}
					input.resetKey(i);
				}
			}
			for (int i = Keys.NUMPAD_0; i <= Keys.NUMPAD_9; i++) {
				if (input.isPressed(i)) {
					String temp = Keys.toString(i);
					if (enteringStr.length() < MAX_IP_LENGTH) {
						enteringStr += temp.substring(temp.length() - 1, temp.length());
					}
					input.resetKey(i);
				}
			}
			if (input.isPressed(Keys.valueOf("."))) {
				if (enteringStr.length() < MAX_IP_LENGTH) {
					enteringStr += ".";
				}
				input.resetKey(Keys.valueOf("."));
			}
		}
	}
	
	private void tickPort() {
		if (game.getInput() != null) {
			LwjglInput input = game.getInput();
			
			if (input.isPressed(Keys.ENTER)) {
				System.out.println("PORT: " + enteringStr);
				port = Integer.parseInt(enteringStr);
				currentState = STATE_CONNECT;
				connect();
				input.resetKey(Keys.ENTER);
			}
			if (input.isPressed(Keys.BACKSPACE)) {
				if (enteringStr.length() > 0) {
					enteringStr = enteringStr.substring(0, enteringStr.length() - 1);					
				}
				input.resetKey(Keys.BACKSPACE);
			}
			

			for (int i = Keys.NUM_0; i <= Keys.NUM_9; i++) {
				if (input.isPressed(i)) {
					if (enteringStr.length() < MAX_PORT_LENGTH) {
						enteringStr += Keys.toString(i);
					}
					input.resetKey(i);
				}
			}
			for (int i = Keys.NUMPAD_0; i <= Keys.NUMPAD_9; i++) {
				if (input.isPressed(i)) {
					String temp = Keys.toString(i);
					if (enteringStr.length() < MAX_PORT_LENGTH) {
						enteringStr += temp.substring(temp.length() - 1, temp.length());
					}
					input.resetKey(i);
				}
			}
		}
	}
	
	private void tickConnect() {
		enteringStr = "";
		if (connected) {
			nextScreen();
		}
	}
	
	private void tickConnectAbort() {
		enteringStr = "";
		if (input.isPressed(Keys.ENTER)) {
			currentState = STATE_NAME;
			input.resetAll();
		}
	}
	
	private void connect() {
		game.connectToServer(ipAddress, port);
		Thread thread = new Thread("connecting..."){
		Player player = game.getPlayer();	
			public void run() {
				int sleepTimer = 3000;
				int tries = 0;
				
		 mainCycle: while (!(game.getGameClient().connected && game.getGameClient().nameAvailable)) {		
					Packet00Login loginPacket = new Packet00Login(player.getUsername(), 0, 0, System.currentTimeMillis(), true);
					loginPacket.writeData(game.getGameClient());	
					
//					currentState = STATE_CONNECT;
					
					try {
						Thread.sleep(500);

						if (game.getGameClient().connected) {
							break mainCycle;
						}
						
// 						printMessage("Failed to connect. Retrying in " + sleepTimer / 1000 + " seconds.");
						tries++;
						currentState = STATE_CONNECT_FAILED;
						str[STATE_CONNECT_FAILED] = "Connection failed. Retrying... [" + tries + "/5]";
						if (tries > 5) {
							currentState = STATE_CONNECT_ABORT;
							return;
						}
						Thread.sleep(sleepTimer);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				connected = true;
				if (!game.getGameClient().nameAvailable) {
					nameNotAvailable();
				}
			}
		};
		thread.start();
	}
	
	private void nameNotAvailable() {
		connected = false;
		redErrorMsg = "Name already taken. Try something else!";
		currentState = STATE_NAME;
	}
	
	// After entering ports
	private void nextScreen() {
		input.resetAll();
		game.getScreenManager().loadLobby(game);
	}
	
	public Level getLevel() {
		return game.getLevel();
	}
	
	public void gotKicked() {
		redErrorMsg = "You have been kicked!";
	}
	
	public void printMessage(String message) {}
	
}
