package com.draznel.bomberboy.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.draznel.bomberboy.gfx.AnimatedPlayer;
import com.draznel.bomberboy.gfx.Font;
import com.draznel.bomberboy.gfx.PlayerNameTag;
import com.draznel.bomberboy.gfx.Powers;
import com.draznel.bomberboy.level.Level;
import com.draznel.bomberboy.packets.Packet02Move;
import com.draznel.bomberboy.packets.Packet11PlayerKilled;
	
public class Player extends Entity {
	
	private String username;
	private AnimatedPlayer animation;
	
	private PlayerNameTag PNT;
	private Powers powers;
	
	Level level;
	 
	private int playerBoundsWidth = 26;
	private int playerBoundsHeight = 20;
	private int playerBoundsOffsetX = 3;
	private int playerBoundsOffsetY = 0;
	 
	public static final int DIRECTION_NORTH = 0;
	public static final int DIRECTION_EAST = 1;
	public static final int DIRECTION_SOUTH = 2;
	public static final int DIRECTION_WEST = 3;
	 
	 int direction = DIRECTION_SOUTH;
	 
	 public boolean alive = false;
	 boolean initialized = false;
	 public boolean cancelStartImageFailedToLoad = false;
	 
	 public Vector2 nameOffset = new Vector2(0, 0);
	 public Vector2 boxOffset = new Vector2(8.0f, 4.0f);
	 private Rectangle nameBox = new Rectangle();
	 public Color nameBoxColor = new Color(0.0f, 0.0f, 0.0f, 0.8f);
	 
	 float fWidth = 0.0f;
	 float fHeight = 0.0f;
	 
	 public int innerAnimation = 0;
	 public int innerFrame = 0;
	 
	 public boolean ready = false;
	 public boolean host = false;
	 
	 public Player(String name, float x, float y) {
		 super(x, y, true);  
		 setName(name);
		 powers = new Powers();
		 ready = false;
	 }
	 
	 public void spawnPlayer(Level level, float x, float y, boolean alive) {
//		 if (alive) return;
	  
		 this.level = level;
		 this.x = x;
		 this.y = y;
		 this.alive = alive;
	 }
	 
	 public void init() {
		 this.animation = new AnimatedPlayer(this);
		 initialized = true;
	 }
	 
	 public void tick() {
		 if (!initialized) {
			 init();
		 }
		 animation.tick();
	 }
	 
	 public void render(SpriteBatch batch) {
		 if (!initialized) {
			 init();
		 }
		 
//		 if (!alive) return;
		 
		 if (animation.getSprite() != null) {
			 animation.getSprite().setPosition(x, y);
			 animation.getSprite().draw(batch);
		 }
	 }
	 
	 public void setName(String name) {
		 this.username = name;
		 fWidth = name.length() * 8;
		 fHeight = 12;
	  
		 nameOffset.x = x + (AnimatedPlayer.playerWidth / 2 - fWidth / 2);
		 nameOffset.y = y - 2 - fHeight + boxOffset.y / 2;
		  
		 nameBox = new Rectangle(x - nameOffset.x - boxOffset.x / 2, y - nameOffset.y - boxOffset.y / 2, fWidth + boxOffset.x, fHeight + boxOffset.y);
		 PNT = new PlayerNameTag(getUsername(), PlayerNameTag.FOCUS_NAME_TAG_COLOR, Color.WHITE);
	 }
	 
	 public void setDirection(int direction) {
	  if (direction >= 0 && direction < 4) {
	   this.direction = direction;
	  }
	 }
	 
	 public void move(float dx, float dy) {
		 float moveX = x;
		 float moveY = y;
		 
		 
		  boolean isCollidingX = checkCollision(dx, 0);
		  boolean isCollidingY = checkCollision(0, dy);
		  
		  if (!isCollidingX) {
			  moveX += dx;
		  }
		  if (!isCollidingY) {
			  moveY += dy;
		  }
		  
		  if (dx == 0 && dy == 0) {
			   if (animation.getCurrentAnimation() != AnimatedPlayer.IDLE) {
				    animation.setCurrentAnimation(AnimatedPlayer.IDLE);
				    setDirection(DIRECTION_SOUTH);
			   }
		  } else { 
			   direction = dx > 0 ? DIRECTION_EAST : (dx < 0 ? DIRECTION_WEST : DIRECTION_SOUTH);
			   direction = dy > 0 ? DIRECTION_NORTH : (dy < 0 ? DIRECTION_SOUTH : direction);
		 
		   if (direction == DIRECTION_NORTH && animation.getCurrentAnimation() != AnimatedPlayer.WALK_U) {
			   animation.setCurrentAnimation(AnimatedPlayer.WALK_U);
		   } else if (direction == DIRECTION_SOUTH && animation.getCurrentAnimation() != AnimatedPlayer.WALK_D) {
			   animation.setCurrentAnimation(AnimatedPlayer.WALK_D);
		   } else if (direction == DIRECTION_EAST && animation.getCurrentAnimation() != AnimatedPlayer.WALK_R) {
		    	animation.setCurrentAnimation(AnimatedPlayer.WALK_R);
		   } else if (direction == DIRECTION_WEST && animation.getCurrentAnimation() != AnimatedPlayer.WALK_L) {
			   animation.setCurrentAnimation(AnimatedPlayer.WALK_L);
		   } 
		  }
		  
		  if (x != moveX || y != moveY || innerAnimation != AnimatedPlayer.IDLE) {
			  int anim = animation.getCurrentAnimation();
			  int frame = animation.getCurrentFrame();
			  Packet02Move packet = new Packet02Move(this.getUsername(), moveX, moveY, anim, frame);
			  packet.writeData(level.getGameClient());
			  x = moveX;
			  y = moveY;
			  innerAnimation = anim;
			  innerFrame = frame;
//			  level.movePlayer(packet.getUsername(), packet.getX(), packet.getY(), packet.getCurrentAnimation(), packet.getCurrentFrame());
		  } 
	 }
	 
	 private boolean checkCollision(float dx, float dy) {
		 if (!alive) return true;
		 if (level == null) return true;
		 boolean result;
		 Rectangle playerBounds = getBounds();
	 
		 playerBounds.setPosition(new Vector2(x + dx, y + dy));

		 result = level.checkCollision(playerBounds);
	
		 // TODO: Collision with bombs, collision with items, collision with players
		 return result;
	 }
	
	 public Rectangle getBounds() {
	  Rectangle rect = new Rectangle(x + playerBoundsOffsetX, y + playerBoundsOffsetY, playerBoundsWidth, playerBoundsHeight);
	  
	  return rect;
	 }
	 
	 public String getUsername() { return username; }
	 public Rectangle getNameBox() {   
	  nameBox.x = x + (AnimatedPlayer.playerWidth / 2 - fWidth / 2) - boxOffset.x / 2;
	  nameBox.y = y - fHeight - boxOffset.y;
	  return nameBox;
	 }
	 public Vector2 getNameOffset() {
	  nameOffset.x = x + (AnimatedPlayer.playerWidth / 2 - fWidth / 2);
	  nameOffset.y = y - boxOffset.y - 1;
	  
	  return nameOffset;
	 }
	 public int getDirection() { return direction; }
	 public AnimatedPlayer getAnimation() { return animation; }
	 public float getX() { return x; }
	 public float getY() { return y; }
	 public float getMovementSpeed() { return powers.getMovSpeed(); }
	 public Powers getPowers() { return powers; }
	 public Level getLevel() { return level; }
	 public Sprite getReadyBoxSprite(float x, float y) {
		 Texture temp = null;
		 if (ready) {
			 temp = new Texture("Sprites/readyT.png");
		 } else {
			 temp = new Texture("Sprites/readyF.png");
		 }
		 if (host) {
			 temp = new Texture("Sprites/hostCrown.png");
		 }
		 Sprite spr = new Sprite(temp);
		 spr.setPosition(x, y);
		 return spr;
	 }

	 public PlayerNameTag getPlayerNameTag() {
		 return PNT;
	 }
	 
	public void sendDeathPacket(String username) {
		Packet11PlayerKilled packet = new Packet11PlayerKilled(username);
		packet.writeData(level.getGameClient());
	}
	 
}
