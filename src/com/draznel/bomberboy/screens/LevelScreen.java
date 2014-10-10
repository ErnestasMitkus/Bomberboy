package com.draznel.bomberboy.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.draznel.bomberboy.Main;
import com.draznel.bomberboy.bombs.Bomb;
import com.draznel.bomberboy.bombs.ClassicBomb;
import com.draznel.bomberboy.entities.PlayerMP;
import com.draznel.bomberboy.gfx.CallBack;
import com.draznel.bomberboy.gfx.ChatBox;
import com.draznel.bomberboy.gfx.Font;
import com.draznel.bomberboy.hud.HUD;
import com.draznel.bomberboy.input.LwjglInput;
import com.draznel.bomberboy.level.Level;
import com.draznel.bomberboy.net.BombClient;
import com.draznel.bomberboy.packets.Packet03ChatMessage;
import com.draznel.bomberboy.tiles.Tile;

public class LevelScreen implements TickScreen {

	Main game;
	Level level;
	
	SpriteBatch batch;
	LwjglInput inputProcessor;
	
	ShapeRenderer shapeRenderer;
	
	PlayerMP player;
	ChatBox chatBox;
	
	Font font = new Font();
	BitmapFont font0;
	BitmapFont playerNameFont;
	
	Vector2 camOffset;
	
	OrthographicCamera camera;
	
	HUD hud;
	
	public static final int MAX_MESSAGE_LENGTH = 70;
	public static final int CHAT_HISTORY_DURATION = 10 * 60;
	private int ticksHistoryChatOpen = 0;
	private boolean chatHistoryEnabled = false;
	private int upperIndex = 0;
	
	private boolean enteringText = false;
	private String chatMessage = "";
	
	public LevelScreen(Main game, PlayerMP player) {
		this.game = game;
		this.level = game.getLevel();
		this.player = player;
		camera = game.getCamera();
		camera.translate(-camera.position.x + (level.width * Tile.SIZE) / 2, -camera.position.y - (level.height * Tile.SIZE) / 2);
		
		font0 = font.getFontBySize(12);
		playerNameFont = font.getFontBySize(16);
		
		batch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();
		
		hud = new HUD(game, player);
	}
	public boolean needToSendDisconnectPacket() { return true; }
	
	@Override
	public void render(float delta) {
		camOffset = new Vector2(camera.position.x, camera.position.y);
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.setProjectionMatrix(game.getCamera().combined);
		batch.begin();
		level.render(batch);
		level.renderPlayers(batch);
		if (player.alive) {
			player.render(batch);
		}
		batch.end();
		
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		shapeRenderer.setProjectionMatrix(game.getCamera().combined);
		shapeRenderer.begin(ShapeType.Filled);
		
		// Name box
		
		level.renderNameTags(shapeRenderer);
		if(player.alive) {
			player.getPlayerNameTag().renderNameTag(shapeRenderer, player.getX(), player.getY());
		}
		shapeRenderer.end();
		Gdx.gl.glDisable(GL20.GL_BLEND);
		
	
		// Name
		batch.begin();
		level.renderPlayerNames(batch, Color.WHITE);
		if(player.alive) {
			player.getPlayerNameTag().renderName(batch, player.getX(), player.getY(), Color.WHITE);
		}
//		playerNameFont.draw(batch, player.getUsername(), player.getNameOffset().x, player.getNameOffset().y + 2);
		batch.end();
		
		Gdx.gl.glEnable(GL20.GL_BLEND);
	    Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
	    shapeRenderer.setProjectionMatrix(game.getCamera().combined);
	    shapeRenderer.begin(ShapeType.Filled);
	    
	    // Chat Box
	    if (chatHistoryEnabled) {
	    	Rectangle rect = chatBox.getHistoryField();
	    	
	    	shapeRenderer.setColor(chatBox.getHistoryFieldColor());
	    	shapeRenderer.rect(rect.x - Main.WIDTH / 2 + camOffset.x, rect.y - Main.HEIGHT / 2 + camOffset.y, rect.width, rect.height);
	    }
	    
	    if (enteringText) {
	    	Rectangle rect = chatBox.getInputField();
	    	shapeRenderer.setColor(chatBox.getInputFieldColor());
	    	shapeRenderer.rect(rect.x - Main.WIDTH / 2 + camOffset.x, rect.y - Main.HEIGHT / 2 + camOffset.y, rect.width, rect.height);
	    	
	    }

	    // Chat Text
		shapeRenderer.end();
		Gdx.gl.glDisable(GL20.GL_BLEND);
		batch.begin();
		if (enteringText) {
			font0.setColor(1.0f, 1.0f, 1.0f, 1.0f);
			int length = chatMessage.length() < chatBox.getMaxCharactersInLine() ? chatMessage.length() : chatBox.getMaxCharactersInLine();
			Rectangle inputRect = chatBox.getInputField();
			
			font0.draw(batch, chatMessage.substring(chatMessage.length() -length, chatMessage.length()),
					inputRect.x - Main.WIDTH / 2 + camOffset.x + 2, inputRect.y - Main.HEIGHT / 2 + (int) camOffset.y + inputRect.height / 2 + 2);
		}
		if (chatHistoryEnabled) {
			font0.setColor(1.0f, 1.0f, 1.0f, 1.0f);
			Rectangle historyRect = chatBox.getHistoryField();
			float textX = historyRect.x - Main.WIDTH / 2 + camOffset.x + 2;
			float textY = historyRect.y - Main.HEIGHT / 2 + (int) camOffset.y + historyRect.height - 4;
			float height = font0.getBounds("W").height + chatBox.getLineSpacing();

			String[] history = chatBox.getHistoryMessages(upperIndex);
			for (int i = 0; i < history.length; i++) {
				font0.draw(batch, history[i], 
						textX, textY - height * i);				
			}
		}
		
		font0.setColor(Color.GREEN);
		font0.draw(batch, "Ping: " + game.getPing() + "ms", 2f - Main.WIDTH / 2 + camera.position.x , -2f + Main.HEIGHT / 2 + camera.position.y);			
		
		// Player above everything else
		
		hud.render(batch);
		batch.end();
		
		//CHECK BOMBS COLLISION BOXES
//		shapeRenderer.begin(ShapeType.Line);
//	    {
//	    	shapeRenderer.setColor(Color.WHITE);
//	    	Rectangle rect;
//	    	for (int i = 0; i < level.getBombs().size(); i++) {
//	    		rect = ((ClassicBomb) level.getBombs().get(i)).getBounds();
//	    		shapeRenderer.rect(rect.x, rect.y, rect.width, rect.height);
//	    	}
	    	
//	    	Tile tile;
//	    	for (int y = 0; y < level.height; y++) {
//	    		for (int x = 0; x < level.width; x++) {
//	    			tile = level.getTile(x, y);
//	    			if (tile.isSolid() && tile.isBreakable()) {
//	    				rect = level.getTileRect(x, y);
//	    				shapeRenderer.rect(rect.x, rect.y, rect.width, rect.height);	    				
//	    			}
//	    		}
//	    	}
	    	
//	    	rect = player.getBounds();
//	    	shapeRenderer.setColor(Color.RED);
//	    	shapeRenderer.rect(rect.x, rect.y, rect.width, rect.height);
//	    	
//	    }
//	    shapeRenderer.end();
		
//		//CHECK ITEMS COLLISION BOXES
//		shapeRenderer.begin(ShapeType.Line);
//	    {
//	    	shapeRenderer.setColor(Color.WHITE);
//	    	Rectangle rect;
//	    	for (int i = 0; i < level.getItems().size(); i++) {
//	    		rect = level.getItemAt(i).getBounds();
//	    		shapeRenderer.rect(rect.x, rect.y, rect.width, rect.height);
//	    	}
//	    	rect = player.getBounds();
//	    	shapeRenderer.setColor(Color.RED);
//	    	shapeRenderer.rect(rect.x, rect.y, rect.width, rect.height);
//	    	
//	    }
//	    shapeRenderer.end();
		
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub
		inputProcessor = game.getInput();
		inputProcessor.autoReset = true;
		
		Rectangle historyFieldRect = new Rectangle(5.0f, 27.0f, 300.0f, 183.0f);
		Rectangle inputFieldRect = new Rectangle(5.0f, 5.0f, 300.0f, 20.0f);
		chatBox = new ChatBox(historyFieldRect, inputFieldRect, font0);
		
		player = game.getPlayer();
//		player.spawnPlayer(level, 96.0f, 405 - 96.0f);
		
//		level.addPlayerToGame((PlayerMP) player);
		
	
		camOffset = new Vector2(game.getCamera().position.x, game.getCamera().position.y);
		game.getCamera().translate(100.0f, -50.0f);
		game.getCamera().update();
				
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
	public void tick() {
		if (level.beenKicked) {
			game.getScreenManager().newKickedScreen(new MainMenu(game));
		}
		
		if (game.getScreenManager().shouldDoVictory) {
			game.getScreenManager().doVictory();
		}
		
		// TODO Auto-generated method stub
		checkInput();
		level.tick();
		if (player.alive) {
			player.tick();
			cameraToPlayer();
//			camOffset = new Vector2(game.getCamera().position.x, game.getCamera().position.y);
		}
		
		if (chatHistoryEnabled && !enteringText) {
			ticksHistoryChatOpen++;
			if (ticksHistoryChatOpen >= CHAT_HISTORY_DURATION) {
				chatHistoryEnabled = false;
			}
		}
//		System.out.println(player.getX() + " " + player.getY());
	}
	
	private void checkInput() {
		if (inputProcessor == null) {
			return;
		}
		
		if (enteringText) {
			checkTextEntering();
		}

		float vx = 0;
		float vy = 0;
		float movSpeed = player.getMovementSpeed();
		
		if (player.alive) {
			
			if (inputProcessor.isPressed(Keys.A)) {
				vx -= movSpeed;
			}
			
			if (inputProcessor.isPressed(Keys.D)) {
				vx += movSpeed;
			}
	
			if (inputProcessor.isPressed(Keys.W)) {
				vy += movSpeed;
			}
			
			if (inputProcessor.isPressed(Keys.S)) {
				vy -= movSpeed;
			}
			
			// Circle movement speed
			if (vx != 0 && vy != 0) {
				vx /= Math.sqrt(2);
				vy /= Math.sqrt(2);
			}
			
			if (inputProcessor.isPressed(Keys.SPACE)) {
				int bombsPlaced = 0;
				for (int i = 0; i < level.getBombs().size(); i++) {
					bombsPlaced += level.getBombs().get(i).getPlacedBy().equals(player.getUsername()) ? 1 : 0;
				}
				if (bombsPlaced < player.getPowers().getMaxBombs()) {
					
	//				float normalY = game.getCamera().viewportHeight - player.getY();
					int bombX =  Math.round(player.getX() / Tile.SIZE);
					int bombY = Math.round((player.getY() + (player.getBounds().height / 2)) / Tile.SIZE);
					
//					System.out.println("Player: " + player.getX() + " " + player.getY());
//					System.out.println("Bomb: " + bombX + " " + bombY);
					
					level.createBomb(new BombClient(Bomb.TYPE_CLASSIC, player.getUsername(), bombX, bombY, ClassicBomb.DEFAULT_EXPLODE_TIMER, System.currentTimeMillis(), 0));
				}
				inputProcessor.resetKey(Keys.SPACE);
			}
		
		} else {
			movSpeed = 4.0f;
			
			if (inputProcessor.isPressed(Keys.A)) {
				vx -= movSpeed;
			}
			
			if (inputProcessor.isPressed(Keys.D)) {
				vx += movSpeed;
			}
	
			if (inputProcessor.isPressed(Keys.W)) {
				vy += movSpeed;
			}
			
			if (inputProcessor.isPressed(Keys.S)) {
				vy -= movSpeed;
			}

			moveCamera(vx, vy);
		}
		
		if (inputProcessor.isPressed(Keys.ENTER)) {
			enteringText = true;
			inputProcessor.autoReset = false;
			inputProcessor.resetKey(Keys.ENTER);
			chatMessage = "";
			chatHistoryEnabled = true;
			ticksHistoryChatOpen = 0;
			inputProcessor.scrollAmount = 0;
			
			
			upperIndex = chatBox.getMessageHistory().size() - chatBox.getMaxLines();
			if (chatBox.getMessageHistory().size() <= chatBox.getMaxLines()) {
				upperIndex = 0;
			}
			
		}
		
		if (player.alive) {
			player.move(vx, vy);
		}
		
		
	}
	
	private void moveCamera(float dx, float dy) {
		if (player.alive) return;
		OrthographicCamera camera = game.getCamera();
		
		if (camera.position.x + dx - game.camera_offsetX < 0) {
			dx = -(camera.position.x - game.camera_offsetX);
		} else if (-camera.position.x - dx + (level.width * Tile.SIZE) - game.camera_offsetX < 0) {
			dx = -camera.position.x + (level.width * Tile.SIZE) - game.camera_offsetX;

		}
		if (camera.position.y + dy - game.camera_offsetY > -0.5f) {
			dy = -(camera.position.y - game.camera_offsetY) + 0.5f;
		} else if (camera.position.y + dy + (level.height * Tile.SIZE) - Main.HEIGHT - game.camera_offsetY < 0.5f) {
			dy = -(camera.position.y + (level.height * Tile.SIZE) - Main.HEIGHT - game.camera_offsetY) - 0.5f;
			
		}
				
		if(Math.sqrt(Math.pow(dx - player.getX(), 2) + Math.pow(dy - player.getY(), 2)) > 100) {
			dx *= 2;
			dy *= 2;
		}
		camera.translate(dx, dy);
		camera.update();
	}
	
	private void cameraToPlayer() {
		OrthographicCamera camera = game.getCamera();
		float movSpeed = player.getMovementSpeed();
		
		float camMovSpeed = 3.0f; // minimum speed the camera moves
		camMovSpeed = camMovSpeed < movSpeed ? movSpeed : camMovSpeed;
		
		float distanceToJumpToPlayer = 1.0f;
		
		float dx = player.getX() - camera.position.x + hud.getWidth() / 2;
		float dy = player.getY() - camera.position.y - 0.5f;

		if (dx != 0) {
			if (Math.abs(dx) < distanceToJumpToPlayer) {
				// Leave the same
			} else {
				dx /= camMovSpeed * 10;
				if (Math.abs(dx) > camMovSpeed) {
					dx = dx > 0 ? camMovSpeed : -camMovSpeed;
				}
			}
		}
		if (dy != 0) {
			if (Math.abs(dy) < distanceToJumpToPlayer) {
				// Leave the same
			} else {
				dy /= camMovSpeed * 10;
				if (Math.abs(dy) > camMovSpeed) {
					dy = dy > 0 ? camMovSpeed : -camMovSpeed;					
				}
			}
		}

		// Check camera if not going out of bounds
		if (camera.position.x + dx - game.camera_offsetX < 0) {
			dx = -(camera.position.x - game.camera_offsetX);
		} else if (-camera.position.x - dx + (level.width * Tile.SIZE) - game.camera_offsetX + hud.getWidth() < 0) {
			dx = -camera.position.x + (level.width * Tile.SIZE) - game.camera_offsetX + hud.getWidth();

		}
		if (camera.position.y + dy - game.camera_offsetY > -0.5f) {
			dy = -(camera.position.y - game.camera_offsetY) + 0.5f;
		} else if (camera.position.y + dy + (level.height * Tile.SIZE) - Main.HEIGHT - game.camera_offsetY < 0.5f) {
			dy = -(camera.position.y + (level.height * Tile.SIZE) - Main.HEIGHT - game.camera_offsetY) - 0.5f;
			
		}
		
//		if (dx == 0 && dy == 0) {
//			float diffX = (float) -(camera.position.x - Math.floor(camera.position.x));
//			float diffY = (float) -(camera.position.y - Math.floor(camera.position.y));
//			
//			System.out.println("x: " + diffX);
//			System.out.println("y: " + diffY);
//			
//			if (diffX != 0) {
//				dx = diffX;
//			}
//			if (diffY != 0) {
//				dy = diffY;
//			}
//		}
		
		camera.translate(dx, dy);
		camera.update();
		
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
				inputProcessor.resetKey(keycode);				
			}
			
			private void appendChar(char c) {
				if (chatMessage.length() < MAX_MESSAGE_LENGTH) {
					chatMessage += c;
				}
			}
		}
		
		InputCallBack ICB = new InputCallBack();
		for (int keycode = 0; keycode < 256; keycode++) {
			inputProcessor.ifPressed(keycode, ICB);			
		}
		
		if (inputProcessor.isPressed(Keys.ENTER)) {
			enteringText = false;
			if (chatMessage.trim().length() > 0) {
				broadcastMessage(chatMessage);				
			}
			inputProcessor.resetKey(Keys.ENTER);
			inputProcessor.autoReset = true;
		}
		if (inputProcessor.isPressed(Keys.ESCAPE)) {
			enteringText = false;
			inputProcessor.resetKey(Keys.ESCAPE);
			inputProcessor.autoReset = true;
		}
		if (inputProcessor.isPressed(Keys.BACKSPACE)) {
			if (chatMessage.length() > 0) {
				chatMessage = chatMessage.substring(0, chatMessage.length() - 1);					
			}
			inputProcessor.resetKey(Keys.BACKSPACE);
		}
		if (inputProcessor.scrollAmount != 0) {
			upperIndex += inputProcessor.scrollAmount;
			inputProcessor.scrollAmount = 0;
			
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
	
	public void printMessage(String message) {
		chatBox.append(message);			
		ticksHistoryChatOpen = 0;
		chatHistoryEnabled = true;
	}
	
	public Level getLevel() {
		return game.getLevel();
	}

}
