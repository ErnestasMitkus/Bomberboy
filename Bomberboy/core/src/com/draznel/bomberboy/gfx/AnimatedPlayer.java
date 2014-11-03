package com.draznel.bomberboy.gfx;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.draznel.bomberboy.entities.Player;

public class AnimatedPlayer {
	
	Player player;

	public static final String sheetPath = "Sprites/Bomberman.png";
	
	public static final int playerWidth = 32;
	public static final int playerHeight = 64;
	
	public static final int IDLE = 0;
	public static final int WALK_D = 1;
	public static final int WALK_L = 2;
	public static final int WALK_R = 3;
	public static final int WALK_U = 4;
	
	public static final int animationAmount = 5;
	public static final int[] animationCount = {2, 3, 3, 3, 3};
	public int[] animationChangeRate = {20, 15, 15, 15, 15};
//	public int[] animationChangeRate = {40, 30, 30, 30, 30};
	
	
	private Texture texture;
	
	private Sprite[][] animations;
	
	private int currentAnimation = IDLE;
	private int currentFrame = 0;
	private int ticksSinceChange = 0;
	
	public AnimatedPlayer(Player player) {
		this.player = player;
		texture = new Texture(sheetPath);
		int max = 0;
		for (int x : animationCount) {
			max = x > max ? x : max;
		}
		
		
		animations = new Sprite[animationAmount][max];
		
		for (int y = 0; y < animationAmount; y++) {
			for (int x = 0; x < animationCount[y]; x++) {
				animations[y][x] = new Sprite(texture, x * playerWidth, y * playerHeight, playerWidth, playerHeight);
			}
		}
	}
	
	public void tick() {
		if (currentAnimation != player.innerAnimation) {
			currentAnimation = player.innerAnimation;
			ticksSinceChange = 0;
		}
		if (currentFrame != player.innerFrame) {
			currentFrame = player.innerFrame;
			ticksSinceChange = 0;
		}
		if (++ticksSinceChange >= animationChangeRate[currentAnimation]) {
			nextFrame();
		}
	}
	
	
	public void prevFrame() {
		currentFrame = --currentFrame > 0 ? currentFrame : animationCount[currentAnimation];
		player.innerFrame = currentFrame;
		ticksSinceChange = 0;
	}
	
	public void nextFrame() {
		currentFrame = ++currentFrame < animationCount[currentAnimation] ? currentFrame : 0;
		player.innerFrame = currentFrame;
		ticksSinceChange = 0;
	}
	
	public void setCurrentAnimation(int animationID) {
		if (animationID >= 0 && animationID < animationAmount) {
			currentAnimation = animationID;
			currentFrame = 0;
			ticksSinceChange = 0;
		}
	}
	
	public void setCurrentFrame(int animationNumber) {
		if (animationNumber >= 0 && animationNumber < animationCount[currentAnimation]) {
			currentFrame = animationNumber;
		}
	}
	
	public int getCurrentAnimation() { return currentAnimation; }
	public int getCurrentFrame() { return currentFrame; }
	
	public Sprite getSprite() {
		return animations[currentAnimation][currentFrame];
	}
	
	public Sprite getSpriteAt(int animationID, int animationNumber) {
		return animations[animationID][animationNumber];
	}
	
	public int getWidth() {
		return playerWidth;
	}
	public int getHeight() {
		return playerHeight;
	}
	
}
