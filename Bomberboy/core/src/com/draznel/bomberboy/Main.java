package com.draznel.bomberboy;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import com.draznel.bomberboy.entities.PlayerMP;
import com.draznel.bomberboy.input.LwjglInput;
import com.draznel.bomberboy.level.Level;
import com.draznel.bomberboy.net.GameClient;
import com.draznel.bomberboy.packets.Packet01Disconnect;
import com.draznel.bomberboy.screens.ScreenManager;

public class Main extends Game {
	
	public static final String TITLE = "BomberBoy beta v1.0";
	public static final int WIDTH = 720;
	public static final int HEIGHT = 405;
	
	public static final boolean EXPORT = true;
//	public static final boolean EXPORT = false;
	
	
//	public static final int WIDTH = 1080;
//	public static final int HEIGHT = 605;
//	public static int WIDTH = 720;
//	public static int HEIGHT = 405;

	//SpriteBatch batch;
	
	boolean running = false;
	private long ping = 0;
//	boolean shouldRender = false;
	
	FPSTickTimer timer;

	ScreenManager screenManager;
	LwjglInput inputProcessor;
	
	private OrthographicCamera cam;
	Rectangle glViewport;
	
	Level level = new Level(this);
	
	PlayerMP player;
	
	GameClient gameClient;
	
	public int camera_offsetX = WIDTH / 2;
	public int camera_offsetY = HEIGHT / 2;
	
	@Override
	public void create () {
		init();
		
//		gameClient = new GameClient(this, "localhost");
//		gameClient = new GameClient(this, "7.12.249.31");
//		gameClient.start();
		
	}

	public void init() {		
//		WIDTH = Gdx.graphics.getDesktopDisplayMode().width;
//		HEIGHT = Gdx.graphics.getDesktopDisplayMode().height;
			
		timer = new FPSTickTimer(60);
		timer.setPrintNumbers(false);
		
		inputProcessor = new LwjglInput(this);
		Gdx.input.setInputProcessor(inputProcessor);
		
		player = new PlayerMP("Main", 0, 0);

		cam = new OrthographicCamera(WIDTH, HEIGHT);
		cam.position.set(camera_offsetX, camera_offsetY, 0);
		glViewport = new Rectangle(0, 0, WIDTH, HEIGHT);
		
		screenManager = new ScreenManager(this);
		setScreen(screenManager.getScreen());
		
		running = true;
	}
	
	public void tick() {
		screenManager.getScreen().tick();
	}
	
	@Override
	public void render() {
		super.render();
		Gdx.gl.glViewport((int) glViewport.x, (int) glViewport.y,
				(int) glViewport.width, (int) glViewport.height);
		
		cam.update();
		
		timer.tick();
		
		if (timer.shouldTick()) {
			tick();
			timer.setTick(false);
		}
	}
	
	@Override
	public void dispose() {
		if (screenManager.getScreen().needToSendDisconnectPacket()) {
			Packet01Disconnect packet = new Packet01Disconnect(player.getUsername(), Packet01Disconnect.REASON_DC);
			packet.writeData(gameClient);
		}
		super.dispose();
	}

	public void setPing(long ping) {
		this.ping = ping;
	}
	
	public long getPing() {
		return ping;
	}

	public LwjglInput getInput() {
		return inputProcessor;
	}
	public ScreenManager getScreenManager() {
		return screenManager;
	}
	
	public OrthographicCamera getCamera() {
		return cam;
	}
	
	public PlayerMP getPlayer() {
		return player;
	}
	
	public Level getLevel() {
		return level;
	}

	public void resetLevel() {
		level = new Level(this);
		player.alive = false;
		player.ready = false;
	}
	
	public void connectToServer(String ipAddress, int port) {
		gameClient = new GameClient(this, ipAddress, port);
		gameClient.start();
	}
	
	public GameClient getGameClient() {
		return gameClient;
	}

	public boolean isRunning() {
		return running;
	}

}
