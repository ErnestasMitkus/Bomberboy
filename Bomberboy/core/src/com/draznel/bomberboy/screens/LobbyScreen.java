package com.draznel.bomberboy.screens;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.draznel.bomberboy.Main;
import com.draznel.bomberboy.entities.Player;
import com.draznel.bomberboy.entities.PlayerMP;
import com.draznel.bomberboy.gfx.CallBack;
import com.draznel.bomberboy.gfx.ChatBox;
import com.draznel.bomberboy.gfx.Font;
import com.draznel.bomberboy.input.LwjglInput;
import com.draznel.bomberboy.input.LwjglInput.MouseEvent;
import com.draznel.bomberboy.level.Level;
import com.draznel.bomberboy.packets.Packet03ChatMessage;
import com.draznel.bomberboy.packets.Packet05ReadyState;
import com.draznel.bomberboy.packets.Packet06MapChosen;
import com.draznel.bomberboy.packets.Packet07Host;
import com.draznel.bomberboy.packets.Packet08GameStart;

public class LobbyScreen implements TickScreen {

	Main game;
	ChatBox chatBox;
	String chatMessage = "";
	
	Level level;
	
	Font font = new Font();
	BitmapFont fontMapName;
	BitmapFont fontChat;
	
	BitmapFont fontOnlinePlayers;
	Rectangle onlinePlayersBox;
	Color onlinePlayersBoxColor;
	
	Sprite readyButtonUpSpr, readyButtonDownSpr;
	Sprite startButtonUp, startButtonDown, startButtonDisabled, startButtonAlone;
	Rectangle readyButtonRect;
	boolean readyButtonDown = false;
	
	Sprite navButtonLeftUp, navButtonRightUp, selectMapButtonUp;
	Sprite navButtonLeftDown, navButtonRightDown, selectMapButtonDown;
	boolean navLeftPressed = false, navRightPressed = false, selectMapPressed = false;
	Rectangle navLeftRect, navRightRect, selectMapRect;
		
	public static final int MAX_MESSAGE_LENGTH = 120;
	private int upperIndex = 0;
	
	LwjglInput input;
	
	SpriteBatch batch;
	ShapeRenderer shapeRenderer;
	
	PlayerMP player;
	
	public List<String> mapNames = new ArrayList<String>();
	List<Texture> mapTextures = new ArrayList<Texture>();
	int currentMapIndex = 0;
	String currentMapName = "";
	String nextMap = "";
	
	Sprite currentMap;
	int currentMapMaxPlayers = 8;
	
	Sprite border;
	Sprite borderOther;
	Vector2 borderSize = new Vector2(256f, 256f);
	
	public boolean canLoadGame = false;
	public boolean needToSendDefaultMap = false;
	
	public LobbyScreen(Main game) {
		this.game = game;
		this.input = game.getInput();
		level = game.getLevel();
	}
	
	@Override
	public boolean needToSendDisconnectPacket() { return true; }

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void show() {
		game.getCamera().position.set(game.camera_offsetX, game.camera_offsetY, 0);
		
		batch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();

		player = (PlayerMP) game.getPlayer();
		
		// Chat box
		Rectangle historyField = new Rectangle(5f, 27f, 380f, 130f);
		Rectangle inputField = new Rectangle(5f, 5f, 380f, 20f);
		chatBox = new ChatBox(historyField, inputField, font.getFontBySize(12));
		
		onlinePlayersBox = new Rectangle(5f, 165f, 380f, Main.HEIGHT - 165f - 5f - 20f);
		onlinePlayersBoxColor = new Color(0.1f, 0.1f, 0.11f, 0.7f);
		
		// Buttons
		readyButtonUpSpr = new Sprite(new Texture("Sprites/Buttons/readyButtonUp.png"));
		readyButtonDownSpr = new Sprite(new Texture("Sprites/Buttons/readyButtonDown.png"));
		startButtonUp = new Sprite(new Texture("Sprites/Buttons/startButtonUp.png"));
		startButtonDown = new Sprite(new Texture("Sprites/Buttons/startButtonDown.png"));
		startButtonDisabled = new Sprite(new Texture("Sprites/Buttons/startButtonDisabled.png"));
		startButtonAlone = new Sprite(new Texture("Sprites/Buttons/startButtonAlone.png"));

		readyButtonRect = new Rectangle(430f, 10f, readyButtonUpSpr.getWidth(), readyButtonUpSpr.getHeight());

		readyButtonUpSpr.setPosition(readyButtonRect.x, readyButtonRect.y);
		readyButtonDownSpr.setPosition(readyButtonRect.x, readyButtonRect.y);
		startButtonUp.setPosition(readyButtonRect.x, readyButtonRect.y);
		startButtonDown.setPosition(readyButtonRect.x, readyButtonRect.y);
		startButtonDisabled.setPosition(readyButtonRect.x, readyButtonRect.y);
		startButtonAlone.setPosition(readyButtonRect.x, readyButtonRect.y);
		
		// Map select buttons
		navButtonLeftUp = new Sprite(new Texture("Sprites/Buttons/navLeftUp.png"));
		navButtonLeftDown = new Sprite(new Texture("Sprites/Buttons/navLeftDown.png"));
		navButtonRightUp = new Sprite(new Texture("Sprites/Buttons/navRightUp.png"));
		navButtonRightDown = new Sprite(new Texture("Sprites/Buttons/navRightDown.png"));
		selectMapButtonUp = new Sprite(new Texture("Sprites/Buttons/selectMapUp.png"));
		selectMapButtonDown = new Sprite(new Texture("Sprites/Buttons/selectMapDown.png"));

		navLeftRect = new Rectangle(430f, 88f, navButtonLeftUp.getWidth(), navButtonLeftUp.getHeight());
		navRightRect = new Rectangle(664f, 88f, navButtonRightUp.getWidth(), navButtonRightUp.getHeight());
		selectMapRect = new Rectangle(460f, 88f, selectMapButtonUp.getWidth(), selectMapButtonUp.getHeight());
		
		navButtonLeftUp.setPosition(navLeftRect.x, navLeftRect.y);
		navButtonLeftDown.setPosition(navLeftRect.x, navLeftRect.y);
		navButtonRightUp.setPosition(navRightRect.x, navRightRect.y);
		navButtonRightDown.setPosition(navRightRect.x, navRightRect.y);
		selectMapButtonUp.setPosition(selectMapRect.x, selectMapRect.y);
		selectMapButtonDown.setPosition(selectMapRect.x, selectMapRect.y);
		
		// Map border
		border = new Sprite(new Texture("Sprites/Border.png"));
		borderOther = new Sprite(new Texture("Sprites/BorderOther.png"));
		border.setPosition(430f, 110f);
		borderOther.setPosition(430f, 110f);
		
		// Load maps
		loadMaps();
		if (mapTextures.size() <= 0) {
			try {
//				throw new Exception("No maps found in \"Levels/\" directory.");
				System.out.println("No maps found in \"Levels/\" directory.");
				System.out.println("Creating default maps failed.");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		setCurrentMap(0);
		
		// Get current map
		Packet06MapChosen requestMap = new Packet06MapChosen(player.getUsername(), "", 0, 0, "");
		requestMap.writeData(game.getGameClient());
		
		// Fonts
		fontMapName = font.getFontBySize(16);
		fontMapName.setColor(0.9f, 0.9f, 0.3f, 1.0f);
		fontChat = font.getFontBySize(12);
		fontChat.setColor(Color.WHITE);
		fontOnlinePlayers = font.getFontBySize(16);
		fontOnlinePlayers.setColor(Color.WHITE);
		
		
	}

	private void loadMaps() {
		if (Main.EXPORT) {
			String path = "Levels/";
			
			boolean directoryExists = new File(path).exists();
			if (!directoryExists) {
				new File(path).mkdir();
			}
			
			File[] files = new File(path).listFiles();
			//If this pathname does not denote a directory, then listFiles() returns null. 
	
			if (files.length == 0) {
				System.out.println("Creating deafult maps...");				
				try {
					BufferedImage bc = ImageIO.read(LobbyScreen.class.getResource("/Levels/Classic.png"));
					BufferedImage bf = ImageIO.read(LobbyScreen.class.getResource("/Levels/Four_Way.png"));
					
					ImageIO.write(bc, "PNG", new File(path + "Classic.png"));
					ImageIO.write(bf, "PNG", new File(path + "Four_Way.png"));
					
					files = new File(path).listFiles();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			for (File file : files) {
			    if (file.isFile()) {
			    	if (file.getName().endsWith(".png") || file.getName().endsWith(".PNG")) {
			    		mapNames.add(file.getName().substring(0, file.getName().length() - 4));
			    		mapTextures.add(new Texture(new FileHandle(path + file.getName())));
			    	}
			    }
			}
			
		} else {
			mapTextures.add(new Texture("Levels/Classic.png"));
			mapTextures.add(new Texture("Levels/Four_Way.png"));	
			mapNames.add("Classic");
			mapNames.add("Four_Way");
		}
		
	}
	
	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 120/255f, 50/255f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.setProjectionMatrix(game.getCamera().combined);
		
		batch.begin();
		
		if (!player.host) {
			border.draw(batch);
		} else {
			if (level.getMapName().equals(mapNames.get(currentMapIndex))) {
				border.draw(batch);
			} else {
				borderOther.draw(batch);
			}
		}
		currentMap.draw(batch);
		fontMapName.draw(batch, currentMapName, 560f - (fontMapName.getBounds(currentMapName).width / 2), 395f);
		batch.end();
		
		Gdx.gl.glEnable(GL20.GL_BLEND);
	    Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
	    shapeRenderer.setProjectionMatrix(game.getCamera().combined);
	    shapeRenderer.begin(ShapeType.Filled);
	    
	    Rectangle rect;
	 // Online Players Box
	    rect = onlinePlayersBox;
	    shapeRenderer.setColor(onlinePlayersBoxColor);
	    shapeRenderer.rect(rect.x, rect.y, rect.width, rect.height);
	    
	 // Chat Box
    	rect = chatBox.getHistoryField();
    	
    	shapeRenderer.setColor(chatBox.getHistoryFieldColor());
    	shapeRenderer.rect(rect.x, rect.y, rect.width, rect.height);
	    
	    rect = chatBox.getInputField();
	    shapeRenderer.setColor(chatBox.getInputFieldColor());
	    shapeRenderer.rect(rect.x, rect.y, rect.width, rect.height);

	    // Chat Text
		shapeRenderer.end();
		Gdx.gl.glDisable(GL20.GL_BLEND);
		batch.begin();
		fontChat.setColor(1.0f, 1.0f, 1.0f, 1.0f);
		int length = chatMessage.length() < chatBox.getMaxCharactersInLine() ? chatMessage.length() : chatBox.getMaxCharactersInLine();
		Rectangle inputRect = chatBox.getInputField();
		
		fontChat.draw(batch, chatMessage.substring(chatMessage.length() -length, chatMessage.length()),
				inputRect.x + 2, inputRect.y + inputRect.height / 2 + 4);
		
		fontChat.setColor(1.0f, 1.0f, 1.0f, 1.0f);
		Rectangle historyRect = chatBox.getHistoryField();
		float textX = historyRect.x;
		float textY = historyRect.y + historyRect.height; // - 4;
		float height = fontChat.getBounds("W").height + chatBox.getLineSpacing();

		String[] history = chatBox.getHistoryMessages(upperIndex);
		for (int i = 0; i < history.length; i++) {
			fontChat.draw(batch, history[i], 
					textX, textY - height * i);				
		}
		
		// Online Players
		int count = level.getOnlinePlayersCount();
		fontMapName.draw(batch, "Online Players (" + (count + 1) + "):", 8f, 395f);
		
		// Player
		fontOnlinePlayers.draw(batch, player.getUsername(), 20f, 375f);
		player.getReadyBoxSprite(7f, 375f - 11f).draw(batch);
		
		// Other players
		height = fontOnlinePlayers.getBounds("WieMEWQO").height + 2f;
		for (int i = 0; i < level.getOnlinePlayersCount(); i++) {
			PlayerMP p = level.getPlayerByIndex(i);
			String username = p.getUsername();
			p.getReadyBoxSprite(7f, 375f - (height * (i + 1)) - 11f).draw(batch);
			fontOnlinePlayers.draw(batch, username, 20f, 375f - (height * (i + 1)));
		}
		
		// Map select button
		if (player.host) {
			if (navLeftPressed) {
				navButtonLeftDown.draw(batch);
			} else {
				navButtonLeftUp.draw(batch);				
			}
			if (navRightPressed) {
				navButtonRightDown.draw(batch);
			} else {
				navButtonRightUp.draw(batch);				
			}
			if (selectMapPressed) {
				selectMapButtonDown.draw(batch);
			} else {
				selectMapButtonUp.draw(batch);				
			}
		}
		
		// Ready button
		getReadyButton().draw(batch);
		
		batch.end();
	}
	
	@Override
	public void tick() {
		if (needToSendDefaultMap) {
			sendDefaultMap();
			needToSendDefaultMap = false;
		}
		
		checkNextMap();
		
		if (level.beenKicked) {
			game.getScreenManager().newKickedScreen(new MainMenu(game));
		}
		
		if (canLoadGame) {
			loadGame();
			canLoadGame = false;
		}
		
		checkTextEntering();
		
		// check Mouse readying
		while (input.getQueueSize() > 0) {
			MouseEvent evt = input.getMouseEvent();
			energizeReadyButton(evt);
			if (player.host) {
				energizeNavLeftButton(evt);
				energizeNavRightButton(evt);
				energizeSelectMapButton(evt);

				if (evt.button == Input.Buttons.LEFT) {
					checkHostTransfer(evt);				
				}
			}
		}
		
	}
	
	private void checkHostTransfer(MouseEvent evt) {
		float heightTemp = fontOnlinePlayers.getBounds("WieMEWQO").height + 2f;

		for (int i = 0; i < level.getOnlinePlayersCount(); i++) {
			Player p = (Player) level.getPlayerByIndex(i);
			Rectangle readyRect = new Rectangle(7f, 375f - (heightTemp * (i + 1)) - 11f, 11f, 11f);
			if (readyRect.contains(evt.screenX, Main.HEIGHT - evt.screenY)) {
				Packet07Host hostTransferPacket = new Packet07Host(p.getUsername());
				hostTransferPacket.writeData(game.getGameClient());
				return;
			}
		}
	}
	
	private boolean energizeReadyButton(MouseEvent evt) {
		boolean canContinueIteration = false;
		if (evt.button == Input.Buttons.LEFT) {
			if (evt.event == MouseEvent.EVENT_TOUCH_DOWN) {
				if (readyButtonRect.contains(evt.screenX, Main.HEIGHT - evt.screenY)) {
					readyButtonDown = true;
					canContinueIteration = true;
				}
			} else if (evt.event == MouseEvent.EVENT_TOUCH_UP) {
				if (readyButtonDown) {
					if (!player.host) {
						togglePlayerReady();
					} else {
						boolean everyoneReady = true;
						readyCheck: for (int i = 0; i < level.getOnlinePlayersCount(); i++) {
							if (!level.getPlayerByIndex(i).ready) {
								everyoneReady = false;
								break readyCheck;
							}
						}
						if (everyoneReady) {
							startGame();
						}
					}
					canContinueIteration = true;
				}
				readyButtonDown = false;
			}
		} else if (evt.button == Input.Buttons.RIGHT){
			readyButtonDown = false;
		}
		return canContinueIteration;
	}
	
	private boolean energizeNavLeftButton(MouseEvent evt) {
		boolean canContinueIteration = false;
		if (evt.button == Input.Buttons.LEFT) {
			if (evt.event == MouseEvent.EVENT_TOUCH_DOWN) {
				if (navLeftRect.contains(evt.screenX, Main.HEIGHT - evt.screenY)) {
					navLeftPressed = true;
					canContinueIteration = true;
				}
			} else if (evt.event == MouseEvent.EVENT_TOUCH_UP) {
				if (navLeftPressed) {
					currentMapIndex = --currentMapIndex < 0 ? mapTextures.size() - 1 : currentMapIndex;
					setCurrentMap(currentMapIndex);
					canContinueIteration = true;
				}
				navLeftPressed = false;
			}
		} else if (evt.button == Input.Buttons.RIGHT){
			navLeftPressed = false;
		}
		return canContinueIteration;
	}
	
	private boolean energizeNavRightButton(MouseEvent evt) {
		boolean canContinueIteration = false;
		if (evt.button == Input.Buttons.LEFT) {
			if (evt.event == MouseEvent.EVENT_TOUCH_DOWN) {
				if (navRightRect.contains(evt.screenX, Main.HEIGHT - evt.screenY)) {
					navRightPressed = true;
					canContinueIteration = true;
				}
			} else if (evt.event == MouseEvent.EVENT_TOUCH_UP) {
				if (navRightPressed) {
					currentMapIndex = ++currentMapIndex >= mapTextures.size() ? 0 : currentMapIndex;
					setCurrentMap(currentMapIndex);
					canContinueIteration = true;
				}
				navRightPressed = false;
			}
		} else if (evt.button == Input.Buttons.RIGHT){
			navRightPressed = false;
		}
		return canContinueIteration;
	}
	private boolean energizeSelectMapButton(MouseEvent evt) {
		boolean canContinueIteration = false;
		if (evt.button == Input.Buttons.LEFT) {
			if (evt.event == MouseEvent.EVENT_TOUCH_DOWN) {
				if (selectMapRect.contains(evt.screenX, Main.HEIGHT - evt.screenY)) {
					selectMapPressed = true;
					canContinueIteration = true;
				}
			} else if (evt.event == MouseEvent.EVENT_TOUCH_UP) {
				if (selectMapPressed) {
					mapTextures.get(currentMapIndex).getTextureData().prepare();
					Pixmap pixels = mapTextures.get(currentMapIndex).getTextureData().consumePixmap();
					level.loadLevel(mapNames.get(currentMapIndex), mapTextures.get(currentMapIndex), pixels, true);	
					canContinueIteration = true;
				}
				selectMapPressed = false;
			}
		} else if (evt.button == Input.Buttons.RIGHT){
			selectMapPressed = false;
		}
		return canContinueIteration;
	}
	
	public void setCurrentMap(int mapIndex) {
		currentMapIndex = mapIndex;
		mapTextures.get(currentMapIndex).getTextureData().prepare();
		Pixmap pixels = mapTextures.get(currentMapIndex).getTextureData().consumePixmap();

		// Count max players in the map
		currentMapMaxPlayers = 0;
		for (int y = 0; y < pixels.getHeight(); y++) {
			for (int x = 0; x < pixels.getWidth(); x++) {
			        int val = pixels.getPixel(x, y);
			        Color color = Color.BLACK;
			        Color.rgba8888ToColor(color, val);
			        int R = (int)(color.r * 255f);
			        int G = (int)(color.g * 255f);
			        int B = (int)(color.b * 255f);
			        int A = (int)(color.a * 255f);
			        if (R == 255 && G == 0 && B == 0 && A == 255) {
			        	currentMapMaxPlayers++;
			        }
			}
		}
		
		currentMap = new Sprite(mapTextures.get(currentMapIndex));
		currentMap.setPosition(border.getX() + (border.getWidth() - borderSize.x) / 2, border.getY() + (border.getHeight() - borderSize.y) / 2);
		currentMap.setSize(borderSize.x, borderSize.y);
		
		currentMapName = mapNames.get(currentMapIndex).replaceAll("_", " ") + " (" + currentMapMaxPlayers + ")";
	}

	@Override
	public Level getLevel() {
		return game.getLevel();
	}
	
	public void loadGame() {
		// Send to server the selected map (pixels), players
		// Get everyone's spawn points.
		// Spawn accordingly.
		
		
		mapTextures.get(currentMapIndex).getTextureData().prepare();
		Pixmap pixels = mapTextures.get(currentMapIndex).getTextureData().consumePixmap();
		level.loadLevel(mapNames.get(currentMapIndex), mapTextures.get(currentMapIndex), pixels, false);
		
		
//		game.getPlayer().spawnPlayer(level, 96.0f, 405 - 96.0f);
		
		game.getScreenManager().loadGame(game, player);
	}
	
private void checkTextEntering() {
		
		class InputCallBack implements CallBack {
			
			public InputCallBack() {
			}
			
			@Override
			public void methodToCallBack(int keycode, boolean shiftPressed) {
				switch(keycode) {
				case Keys.ENTER:
				case Keys.ESCAPE:
				case Keys.BACKSPACE:
				case Keys.SHIFT_LEFT:
				case Keys.SHIFT_RIGHT:
					return;
				default:
					break;
				}
//				System.out.println(shiftPressed);
				String temp = Keys.toString(keycode);
				char c = 'a'; // will be overwritten by any other thing
				boolean failure = false;
				if (temp.length() == 1) {
					if (!shiftPressed) {
						temp = temp.toLowerCase();
					}
					c = temp.charAt(0);
				} else {
					if (temp.equalsIgnoreCase(Keys.toString(Keys.SPACE))) {
						c = ' ';
					} else if(temp.contains("Numpad")) {
						c = temp.charAt(temp.length() - 1);
					} else if (temp.equalsIgnoreCase("plus")) {
						c = '+';
					} else {
						failure = true;
						System.out.println("FAILURE: " + Keys.toString(keycode));
					}
				}
				// Shifting values
				if (shiftPressed) {
					switch(c) {
					case '/':
						c = '?';
						break;
					case '.':
						c = '>';
						break;
					case ',':
						c = '<';
						break;
					case ';':
						c = ':';
						break;
					case 39:
						c = '"';
						break;
					case '\\':
						c = '|';
						break;
					case '-':
						c = '_';
						break;
					case '=':
						c = '+';
						break;
					default:
						break;
					}
					
					if (temp.length() == 1) {
						switch(c) {
						case '1':
							c = '!';
							break;
						case '2':
							c = '@';
							break;
						case '3':
							c = '#';
							break;
						case '4':
							c = '$';
							break;
						case '5':
							c = '%';
							break;
						case '6':
							c = '^';
							break;
						case '7':
							c = '&';
							break;
						case '8':
							c = '*';
							break;
						case '9':
							c = '(';
							break;
						case '0':
							c = ')';
							break;
						default:
							break;
						}
					}
				}
				if (!failure) {
					appendChar(c);
				}
				input.resetKey(keycode);				
			}
			
			private void appendChar(char c) {
				if (chatMessage.length() < MAX_MESSAGE_LENGTH) {
					chatMessage += c;
				}
			}
		}
		
		InputCallBack ICB = new InputCallBack();
		for (int keycode = 0; keycode < 256; keycode++) {
			input.ifPressed(keycode, ICB);			
		}
		
		if (input.isPressed(Keys.ENTER)) {
			if (chatMessage.trim().length() > 0) {
				broadcastMessage(chatMessage);		
				chatMessage = "";
				upperIndex = chatBox.getMessageHistory().size() - chatBox.getMaxLines();
				if (chatBox.getMessageHistory().size() <= chatBox.getMaxLines()) {
					upperIndex = 0;
				}
			}
			input.resetKey(Keys.ENTER);
//			input.autoReset = true;
		}
		if (input.isPressed(Keys.ESCAPE)) {
			chatMessage = "";
			input.resetKey(Keys.ESCAPE);
//			input.autoReset = true;
		}
		if (input.isPressed(Keys.BACKSPACE)) {
			if (chatMessage.length() > 0) {
				chatMessage = chatMessage.substring(0, chatMessage.length() - 1);					
			}
			input.resetKey(Keys.BACKSPACE);
		}
		if (input.scrollAmount != 0) {
			upperIndex += input.scrollAmount;
			input.scrollAmount = 0;
			
			if (upperIndex < 0) {
				upperIndex = 0;
			} else if (upperIndex + chatBox.getMaxLines() - 1 >= chatBox.getMessageHistory().size()) {
				upperIndex = chatBox.getMessageHistory().size() - chatBox.getMaxLines();
				if (chatBox.getMessageHistory().size() <= chatBox.getMaxLines()) {
					upperIndex = 0;
				}
			}
		}
	}
	
	private void broadcastMessage(String message) {
		Packet03ChatMessage messagePacket = new Packet03ChatMessage(player.getUsername(), player.getUsername() + ": " + message);
		messagePacket.writeData(game.getGameClient());
	}

	private void togglePlayerReady() {
		Packet05ReadyState readyPacket = new Packet05ReadyState(player.getUsername(), !player.ready);
		readyPacket.writeData(game.getGameClient());
	}
	
	@Override
	public void printMessage(String message) {
		chatBox.append(message);			
	}
	
	public void createMap(String name, int width, int height, byte[] tiles) {
		String path = "Levels/";
		
		System.out.println("Tiles byte array length: " + tiles.length);
		
		boolean directoryExists = new File(path).exists();
		if (!directoryExists) {
			new File(path).mkdir();
		}
		
		System.out.println("Creating map named: " + name);
		try {
			BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					bi.setRGB(x, y, getColorFromId(tiles[x + y * width]));
				}
			}
//			System.out.println("x:" + 1 + " y:" + 1 + " tileId["+ tiles[1] +"] BIid:" + Integer.toHexString(bi.getRGB(1, 1)));//tiles[x+y*width]);
			String callName = name.replace(" ", "_");
			ImageIO.write(bi, "PNG", new File(path + callName + ".png"));
			System.out.println("New map created successfully.");
			nextMap = name;
		} catch (Exception e) {
			System.err.println("Failed to create image.");
			player.cancelStartImageFailedToLoad = true;
			e.printStackTrace();
		}
	}
	
	public static int getColorFromId(byte byteId) {
		final int ID_VOID = 0;
		final int ID_GRASS = 1;
		final int ID_STONE = 2;
		final int ID_OBSIDIAN = 3;
		final int ID_SPAWN = 4;
		
		int id = (int) byteId;
		
		switch(id) {
		case ID_GRASS:
			return 0x00FF00;
		case ID_STONE:
			return 0x999999;
		case ID_OBSIDIAN:
			return 0x000000;
		case ID_SPAWN:
			return 0xFF0000;
		case ID_VOID:
			return 0xFF010101;
		default:
			return 0x000000;
		}
	}
	
	private void checkNextMap() {
		if (nextMap.length() > 0) {
			mapNames.add(nextMap);
			mapTextures.add(new Texture(new FileHandle("Levels/" + nextMap + ".png")));
			setCurrentMap(mapTextures.size() - 1);
			nextMap = "";
			level.mapName = mapNames.get(currentMapIndex);
		}
	}
	
	private Sprite getReadyButton() {
		Sprite spr = null;
		boolean host = game.getPlayer().host;
		
		
		if (host) {
			if (level.getOnlinePlayersCount() == 0) {
				spr = startButtonAlone;
				return spr;
			}
			boolean everyoneReady = true;
			readyCheck: for (int i = 0; i < level.getOnlinePlayersCount(); i++) {
				if (!level.getPlayerByIndex(i).ready) {
					everyoneReady = false;
					break readyCheck;
				}
			}
			if (!everyoneReady) {
				spr = startButtonDisabled;
			} else {
				if (readyButtonDown) {
					spr = startButtonDown;
				} else {
					spr = startButtonUp;
				}
			}
			
		} else {
			if (readyButtonDown) {
				spr = readyButtonDownSpr;
			} else {
				spr = readyButtonUpSpr;
			}
		}
		return spr;
	}

	private void startGame() {
		Packet08GameStart initGameStartPacket = new Packet08GameStart(player.getUsername(), 0f, 0f, false);
		initGameStartPacket.writeData(game.getGameClient());
	}

	public void victory(String username) {
		level.removeAdditionalPlayer(game.getPlayer());
		level.resetEntities();
		if (username.equals("@Server")) {			
			printMessage("No one won last match");
		} else {			
			printMessage("The winner of the match is: " + username);
		}
		
		
	}

	public void sendDefaultMap() {
		mapTextures.get(currentMapIndex).getTextureData().prepare();
		Pixmap pixels = mapTextures.get(currentMapIndex).getTextureData().consumePixmap();
		level.loadLevel(mapNames.get(0), mapTextures.get(0), pixels, true);
	}

	public void fixCurrentMapIndex() {
		String newName = level.mapName;
		for (int i = 0; i < mapNames.size(); i++) {
			if (mapNames.get(i).equals(newName)) {
				currentMapIndex = i;
				System.out.println(mapNames.get(currentMapIndex) + " = " + currentMapIndex);
				break;
			}
		}
		
	}
	
}
