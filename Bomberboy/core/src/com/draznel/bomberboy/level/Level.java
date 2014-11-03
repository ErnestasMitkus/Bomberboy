package com.draznel.bomberboy.level;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.draznel.bomberboy.Main;
import com.draznel.bomberboy.bombs.Bomb;
import com.draznel.bomberboy.bombs.ClassicBomb;
import com.draznel.bomberboy.bombs.Explosion;
import com.draznel.bomberboy.entities.Entity;
import com.draznel.bomberboy.entities.Player;
import com.draznel.bomberboy.entities.PlayerMP;
import com.draznel.bomberboy.gfx.IntIntByte;
import com.draznel.bomberboy.gfx.Map;
import com.draznel.bomberboy.gfx.MapGenerator;
import com.draznel.bomberboy.gfx.PlayerNameTag;
import com.draznel.bomberboy.items.Item;
import com.draznel.bomberboy.items.ItemID;
import com.draznel.bomberboy.items.ItemSpritesheet;
import com.draznel.bomberboy.net.BombClient;
import com.draznel.bomberboy.net.GameClient;
import com.draznel.bomberboy.packets.Packet01Disconnect;
import com.draznel.bomberboy.packets.Packet06MapChosen;
import com.draznel.bomberboy.packets.Packet07Host;
import com.draznel.bomberboy.packets.Packet10BombPlaced;
import com.draznel.bomberboy.screens.LevelScreen;
import com.draznel.bomberboy.screens.LobbyScreen;
import com.draznel.bomberboy.tiles.Tile;

public class Level {

	private Main game;
	
	public boolean beenKicked = false;
	
	public String mapName = "Classic";
	private byte[] tiles;
//	private byte[] itemBytes;
	public int width;
	public int height;
	private String imagePath;
	Texture image;
	Pixmap pixels;
	
	private List<Item> items = new ArrayList<Item>();
	
	private List<Entity> playersOnline = new ArrayList<Entity>();
	private List<Bomb> bombs = new ArrayList<Bomb>();
	
	private List<BombClient> bombsToAdd = new ArrayList<BombClient>();
	private Queue<IntIntByte> alterTileQueue = new LinkedList<IntIntByte>();
	
	private List<PlayerNameTag> playerNameTags = new ArrayList<PlayerNameTag>();
	
	private int[] mapColors;
	
	Map map;
	
	public Level(Main game, String imagePath) {
		this.game = game;
		readyExplosions();
		if (imagePath != null){
			this.imagePath = imagePath;
			loadLevelFromFile();
		} else {
			generateLevel();
		}
	}
	
	public Level(Main game) {
		this.game = game;
	}

	private void readyExplosions() {
		Texture sheet = new Texture("Sprites/tiles.png");
		Sprite[] sprites = new Sprite[3];
		for (int i = 0; i < 3; i++) {
			sprites[i] = new Sprite(sheet, i * Tile.SIZE, 6 * Tile.SIZE, Tile.SIZE, Tile.SIZE);
		}
		Explosion.setExplosionSprites(sprites);
		Explosion.setLevel(this);
	}
	
	private void loadLevelFromFile() {
		image = new Texture(imagePath);
		pixels = new Pixmap(Gdx.files.internal(imagePath));

		width = image.getWidth();
		height = image.getHeight();
		mapColors = new int[width * height];

		tiles = new byte[width * height];
		loadTiles();
		
		map = new Map(width, height);
		map.setTiles(tiles);
		map.setMapColors(mapColors);
		setFromMap(map);
	}
	
	public void loadLevel(String mapName, Texture texture, Pixmap pixels, boolean sendToServer) {
		readyExplosions();
		game.getPlayer().cancelStartImageFailedToLoad = false;
		
		this.mapName = mapName;
		image = texture;
		this.pixels = pixels;
		width = image.getWidth();
		height = image.getHeight();
		mapColors = new int[width * height];

		tiles = new byte[width * height];
		loadTiles();
		
		if (sendToServer) {
			String tilesToString = "";
			for (int i = 0; i < tiles.length; i++) {
				char a = (char) (tiles[i] + 'A');
//				System.out.println("Bytes[" + tiles[i] + "] to char[" + a + "]");
				tilesToString += a;
			}
			Packet06MapChosen mapPacket = new Packet06MapChosen(game.getPlayer().getUsername(), mapName, width, height, tilesToString);
			mapPacket.writeData(game.getGameClient());
		}
		
		map = new Map(width, height);
		map.setTiles(tiles);
		map.setMapColors(mapColors);
		setFromMap(map);
	}
	
	private void loadTiles() {
//		mapColors = this.image.getRGB(0, 0, width, height, null, 0, width);
		Color color = new Color();

		for (int x=0; x<pixels.getWidth(); x++) {
		    for (int y=0; y<pixels.getHeight(); y++) {
		        int val = pixels.getPixel(x, y);
		        Color.rgba8888ToColor(color, val);
		        int R = (int)(color.r * 255f);
		        int G = (int)(color.g * 255f);
		        int B = (int)(color.b * 255f);
		        int A = (int)(color.a * 255f);
		        mapColors[x + y * width] = A << 24 | R << 16 | G << 8 | B;
		    }
		}
		
		for (int y = 0; y < height; y++){
			for (int x = 0; x < width; x++){
				tileCheck: for (Tile t : Tile.tiles){
					if (t != null && t.getLevelColor() == mapColors[x + y * width]){
						this.tiles[x + y * width] = t.getId();
						// Do something
						break tileCheck;
					}
				}
//				System.out.println("Tile[" + x + ";" + y + "] = " + tiles[x + y * width]);
			}
		}
	}
	
	private void generateLevel() {
		// Random levels
		int[] chances = {0, 0, 700, 300};
		map = MapGenerator.generateMap(64, 64, chances, 4, 0);
		setFromMap(map);
	}
	
	private void setFromMap(Map map) {
		this.width = map.getWidth();
		this.height = map.getHeight();
		this.tiles = map.getTiles();
		this.mapColors = map.getMapColors();
	}
	

	
	public Tile getTile(int x, int y) {
		if (x < 0 || x >= width || y < 0 || y >= height) return Tile.VOID;
		return Tile.tiles[tiles[x+y*width]];
	}
	
	public void alterTile(int x, int y, Tile newTile) {
		if (x < 0 || x >= width || y < 0 || y >= height) return;
		this.tiles[x + y * width] = newTile.getId();
//		pixels.setRGB(x, y, newTile.getLevelColor());
		mapColors[x + y * width] = newTile.getLevelColor();
				
	}
	
	public void alterTileQueue(int x, int y, byte id) {
		alterTileQueue.add(new IntIntByte(x, y, id));
	}
	
	public void tick(){
		tickBombsToAdd();
		while (alterTileQueue.size() != 0) {
			IntIntByte alteration = alterTileQueue.poll();
			alterTile(alteration.x, alteration.y, Tile.getById(alteration.b));
		}
		
		for (Entity e: getPlayers()) {
			e.tick();
		}
		
		// Check if any non-solid bomb should become solid
		for (int i = 0; i < bombs.size(); i++) {
			if (!bombs.get(i).isSolid()) {
				if (bombs.get(i) instanceof ClassicBomb) {
					ClassicBomb bomb = (ClassicBomb) bombs.get(i);
					Rectangle bounds = bomb.getBounds();
					boolean shouldBecomeSolid = true;
					// Is it me who overlaps?
					if (bounds.overlaps(game.getPlayer().getBounds())) {
						shouldBecomeSolid = false;
					}
					// Is it anyone else?
					ArrayList<Player> alivePlayers = game.getLevel().getAlivePlayers();
					for (int j = 0; j < alivePlayers.size(); j++) {
						Rectangle pBounds = ((Player) alivePlayers.get(j)).getBounds();
						if (pBounds.overlaps(bounds)) {
							shouldBecomeSolid = false;
						}
					}
					if (shouldBecomeSolid) {
						bomb.setSolid(true);
					}
				}
			}
		}
		
		for (int i = 0; i < bombs.size(); i++) {
			Bomb bomb = bombs.get(i);
			bomb.tick();
			if (bomb.exploded()) {
				bombs.remove(bomb);
				--i;
			}
		}
		
		Item item;
		for (int i = 0; i < items.size(); i++) {
			item = items.get(i);
			item.tick(this);
			if (item.isAlreadyUsed()) {
				items.remove(i);
				--i;
			}
		}
		
		Explosion.tick();
	
//		for (Tile t: Tile.tiles) {
//			if (t == null) {
//				break;
//			}
//			t.tick();
//		}
	}
	
	public void tickBombsToAdd() {
		while (bombsToAdd.size() != 0) {
			BombClient BC = bombsToAdd.get(0);
			Player p = (Player) getPlayerByIndex(getPlayerMPIndex(BC.placedBy));
			
			ClassicBomb bomb = new ClassicBomb(BC.placedBy, p.getPowers().getSpread(), BC.x, BC.y, BC.explodeTimer, BC.userTimePlaced);
			bombs.add(bomb);
			
			bombsToAdd.remove(0);
		}
	}
	
	public void render(SpriteBatch batch) {
//		for (Entity e: getEntities()) {
//			e.render(batch);
//		}
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				renderNonStoneTile(batch, mapColors[x + y * width], x, y);
			}
		}
	
		for (int i = 0; i < items.size(); i++) {
			items.get(i).render(batch);
		}

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				renderStoneTile(batch, mapColors[x + y * width], x, y);
			}
		}

		
		for (Bomb bomb: bombs) {
			bomb.render(batch);	
		}
		
		Explosion.render(game, batch);
		
	}

	public void renderNonStoneTile(SpriteBatch batch, int color, int x, int y) {
		for (Tile t : Tile.tiles) {
			if (t != null && color == t.getLevelColor()) {
				if (t.getId() == Tile.ID_STONE) continue;
				Sprite spr = new Sprite(t.getSprite());
				spr.setX(x << 5);
				// WHY 309
				spr.setY(Main.HEIGHT - (y << 5) - 32);
//				spr.setY(y << 5);
				spr.draw(batch);
			}
		}
	}
	
	public void renderStoneTile(SpriteBatch batch, int color, int x, int y) {
		for (Tile t : Tile.tiles) {
			if (t != null && color == t.getLevelColor()) {
				if (t.getId() != Tile.ID_STONE) continue;
				Sprite spr = new Sprite(t.getSprite());
				spr.setX(x << 5);
				// WHY 309
				spr.setY(Main.HEIGHT - (y << 5) - 32);
//				spr.setY(y << 5);
				spr.draw(batch);
			}
		}
	}
	
	public void renderBounds(ShapeRenderer SR) {
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				renderTileBounds(SR, mapColors[x + y * width], x, y);
			}
		}
	}
	
	public void renderTileBounds(ShapeRenderer SR, int color, int x, int y) {
		for (Tile t : Tile.tiles) {
			if (t != null && color == t.getLevelColor() && t.isSolid()) {
				Rectangle rect = getTileRect(x, y);
				SR.rect(rect.x, rect.y, rect.width, rect.height);
			}
		}
	}
	
	public boolean checkCollision(Rectangle bounds) {
		if (bounds == null) return true;
		
		bounds.x += 3f;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (isCollidingWithTile(bounds, mapColors[x + y * width], x, y)) {
					return true;
				}
			}
		}
		bounds.x -= 3f;

		if (isCollidingWithBombs(bounds)) {
			return true;
		}
		return false;
	}
	
	public boolean isCollidingWithTile(Rectangle bounds, int color, int x, int y) {
		for (Tile t : Tile.tiles) {
			if (t != null && color == t.getLevelColor() && t.isSolid()) {
				Rectangle rect = getTileRect(x, y);
				return bounds.overlaps(rect);
			}
		}
		
		return false;
	}
	
	public boolean isCollidingWithBombs(Rectangle bounds) {
		Rectangle bombRect;
		for (int i = 0; i < bombs.size(); i++) {
			bombRect = ((ClassicBomb) bombs.get(i)).getBounds();
			if (bombs.get(i).isSolid()) {
				if (bounds.overlaps(bombRect)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public void addPlayerMP(PlayerMP player) {
		playersOnline.add(player);
		playerNameTags.add(new PlayerNameTag(player.getUsername(), PlayerNameTag.FOCUS_NAME_TAG_COLOR, Color.WHITE));
		if (!player.getUsername().equals(game.getPlayer().getUsername())) {
			if (game.getScreenManager().getScreen() instanceof LevelScreen) {
				LevelScreen temp = (LevelScreen) game.getScreenManager().getScreen();
				temp.printMessage(player.getUsername() + " has joined the game.");
			}
		}
	}
	
	public void addPlayerToGame(PlayerMP player) {
		playersOnline.add(player);
		playerNameTags.add(new PlayerNameTag(player.getUsername(), PlayerNameTag.FOCUS_NAME_TAG_COLOR, Color.WHITE));
	}
	
	public void removeAdditionalPlayer(PlayerMP player) {
		playersOnline.remove(player);
	}
	
	public void removePlayerMP(String username, int timedOut) {
		if (username.equalsIgnoreCase(game.getPlayer().getUsername())) {
			beenKicked = true;
		}
		Entity e;
		for (int iterator = 0; iterator < playersOnline.size(); iterator++) {
			e = playersOnline.get(iterator);
			if (e instanceof Player) {
				Player p = (Player) e;
				if (p.getUsername().equals(username)) {
					playerNameTags.remove(playersOnline.indexOf(e));
					playersOnline.remove(e);
					if (game.getScreenManager().getScreen() instanceof LevelScreen) {
						LevelScreen temp = (LevelScreen) game.getScreenManager().getScreen();
						if (timedOut == Packet01Disconnect.REASON_TIMED_OUT) {						
							temp.printMessage(p.getUsername() + " has timed out.");
						} else if (timedOut == Packet01Disconnect.REASON_DC) {							
							temp.printMessage(p.getUsername() + " has left the game.");
						} else if (timedOut == Packet01Disconnect.REASON_KICKED) {
							temp.printMessage(p.getUsername() + " has been kicked.");							
						}
					}
				}
			}
		}
	}
	
	public PlayerMP getPlayerByIndex(int index) {
		if (index == -1) return game.getPlayer();
		
		if (index >= 0 && index < playersOnline.size()) {
			return (PlayerMP) playersOnline.get(index);
		}
		return null;
	}
	
	public int getPlayerMPIndex(String username) {
		if (game.getPlayer().getUsername().equals(username)) {
			return -1;
		}
		int index = 0;
		Entity e;
		for (int iterator = 0; iterator < playersOnline.size(); iterator++) {
			e = playersOnline.get(iterator);
			if (e instanceof PlayerMP && ((PlayerMP)e).getUsername().equals(username)){
				return index;
			}
			index++;
		}
		return -99;
	}
	
	public Rectangle getTileRect(int x, int y) {
		return new Rectangle(x << 5, Main.HEIGHT - (y << 5) - 32, Tile.SIZE, Tile.SIZE);
	}
	
	public List<Entity> getPlayers() {
		return playersOnline;
	}
	
	public void movePlayer(String username, float x, float y, int currentAnimation, int currentFrame) {
		if (username.equals(game.getPlayer().getUsername())) {
			return;
		}
		try {
			
			int index = getPlayerMPIndex(username);
			if (index >= playersOnline.size()) return;
			
			Player player = (Player) getPlayers().get(index);
			
			player.setX(x);
			player.setY(y);
//			System.out.println("x: " + x + " y: " + y);
			player.innerAnimation = currentAnimation;
			player.innerFrame = currentFrame;
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	public void renderPlayers(SpriteBatch batch) {
		Player p;
		for (int iterator = 0; iterator < playersOnline.size(); iterator++) {
			p = (Player) playersOnline.get(iterator);
			if (p.alive) {
				p.render(batch);
			}
		}
		
	}

	public void renderNameTags(ShapeRenderer shapeRenderer) {
		Player player;
		PlayerNameTag playerNameTag;
		
		for (int iterator = 0; iterator < playersOnline.size(); iterator++) {
			playerNameTag = playerNameTags.get(iterator);
			int index = playerNameTags.indexOf(playerNameTag);
			player = (Player) playersOnline.get(index);
			
			if (player.alive) {
				playerNameTag.renderNameTag(shapeRenderer, player.getX(), player.getY());
			}
		}
	}

	public void renderPlayerNames(SpriteBatch batch, Color color) {
		Player player;
		PlayerNameTag playerNameTag;
		for (int iterator = 0; iterator < playersOnline.size(); iterator++) {
			playerNameTag = playerNameTags.get(iterator);
			int index = playerNameTags.indexOf(playerNameTag);
			player = (Player) playersOnline.get(index);
			
			if (player.alive) {
				playerNameTag.renderName(batch, player.getX(), player.getY(), color);
			}
		}
	}

	public GameClient getGameClient() {
		return game.getGameClient();
	}
	
	public void createBomb(BombClient bomb) {
		Packet10BombPlaced bombPlacedPacket = new Packet10BombPlaced(bomb.type, bomb.placedBy, bomb.x, bomb.y, bomb.explodeTimer, bomb.userTimePlaced);
		bombPlacedPacket.writeData(game.getGameClient());
//		if (bomb instanceof ClassicBomb) {
//			((ClassicBomb) bomb).setFuse(System.currentTimeMillis() + ClassicBomb.EXPLODE_TIMER, game);
//		}
//		bombs.add(bomb);
		// Send packet
	}
	
	public void addBomb(int type, String placedBy, int x, int y, long explodeTimer, long timePlaced) {
		bombsToAdd.add(new BombClient(type, placedBy, x, y, explodeTimer, timePlaced, 0));
	}
	
	public List<Bomb> getBombs() {
		return bombs;
	}
	
	public int getOnlinePlayersCount() {
		return playersOnline.size();
	}
	
	public void changeMap(String name, int width, int height, String tilesString) {
		this.width = width;
		this.height = height;
		byte[] tiles = new byte[width * height];
		int maxIndex = tilesString.length();
		for (int i = 0; i < maxIndex; i++) {
			tiles[i] = (byte) (tilesString.charAt(i) - 'a');
		}
		
		breakPoint: if (game.getScreenManager().getScreen() instanceof LobbyScreen) {
			LobbyScreen LS = (LobbyScreen) game.getScreenManager().getScreen();
			for (int i = 0; i < LS.mapNames.size(); i++) {
				if (LS.mapNames.get(i).equals(name)) {
					LS.setCurrentMap(i);
					game.getPlayer().cancelStartImageFailedToLoad = false;
					break breakPoint;
				}
			}
			LS.createMap(name, width, height, tiles);
		} else {
			System.err.println("Not an instance of Lobby Screen.");
			System.err.println("Change map or you may get an error.");
		}
		mapName = name;
		if (game.getScreenManager().getScreen() instanceof LobbyScreen) {		
			((LobbyScreen) game.getScreenManager().getScreen()).fixCurrentMapIndex();
		}
	}

	public void setHost(Packet07Host packet) {
		Player p = game.getPlayer();
		if (p.getUsername().equals(packet.getUsername())) {
			p.host = true;
		} else {
			p.host = false;
		}
		p.ready = false;
		
		for (int i = 0; i < getPlayers().size(); i++) {
			p = (Player) playersOnline.get(i);
			if (p.getUsername().equals(packet.getUsername())) {
				p.host = true;
			} else {
				p.host = false;
			}
			p.ready = false;
		}
	}

	public void spawnPlayer(String username, float x, float y, boolean alive) {
		for (int i = 0; i < playersOnline.size(); i++) {
			Player p = (Player) playersOnline.get(i);
			if (p.getUsername().equals(username)) {
				p.spawnPlayer(this, x, y, alive);
			}
		}
	}
	
	public String getMapName() {
		return mapName;
	}
	public PlayerMP getPlayer() {
		return game.getPlayer();
	}

	public void resetEntities() {
		bombs.clear();
		bombsToAdd.clear();
		alterTileQueue.clear();
		items.clear();
		
		Explosion.clear();
		
		game.getPlayer().getPowers().reset();
	
	}

	public void makeItems(String itemString) {
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int id = (itemString.charAt(x + y * width) - 'A');
				if (id == ItemID.NOTHING) continue;
				Item item = new Item(id, x, y);
				items.add(item);		
			}
		}
	}
	
	public void editItem(int x, int y, byte newId) {
		Item item = null;
		for (int i = 0; i < items.size(); i++) {
			item = items.get(i);
			if (item.getX() == x && item.getY() == y) {
				item.changeID(newId);
			}
		}
	}

	public int getAlivePlayersCount() {
		int counter = 0;
		
		if (getPlayer().alive) {
			counter++;
		}
		
		for (int i = 0; i < playersOnline.size(); i++) {
			if (((Player) playersOnline.get(i)).alive) {
				counter++;
			}
		}
		
		return counter;
	}
	
	public ArrayList<Player> getAlivePlayers() {
		ArrayList<Player> alivePlayers = new ArrayList<Player>();
		
		if (getPlayer().alive) {
			alivePlayers.add(getPlayer());
		}
		
		for (int i = 0; i < playersOnline.size(); i++) {
			if (((Player) playersOnline.get(i)).alive) {
				alivePlayers.add((Player) playersOnline.get(i));
			}
		}
		
		return alivePlayers;
	}
	
	public List<Item> getItems() {
		return items;
	}
	
	public Item getItemAt(int index) {
		if (index < 0 || index >= items.size()) return null;
		return items.get(index);
	}
	
	public Item getItemAt(int x, int y) {
		Item item;
		for (int i = 0; i < items.size(); i++) {
			item = items.get(i);
			if (item.getX() == x && item.getY() == y) {
				return item;
			}
		}
		return null;
	}

	public void handleItemPickup(String username, int id, int x, int y) {
		Player p = null;
		if (game.getPlayer().getUsername().equals(username)) {
			p = game.getPlayer();
		} else {
			for (int i = 0; i < getAlivePlayersCount(); i++) {
				if (getAlivePlayers().get(i).getUsername().equals(username)) {
					p = getAlivePlayers().get(i);
					break;
				}
			}
		}
		if (p != null) {
			if (!getItemAt(x, y).isAlreadyUsed()) {
				Item.awardPlayer(p, id);
			}
		}
		
		getItemAt(x, y).alreadyUsed = true;
	}
	
	
}
