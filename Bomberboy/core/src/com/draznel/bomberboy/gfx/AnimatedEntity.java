package com.draznel.bomberboy.gfx;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class AnimatedEntity {
	
	Sprite[] animations;
	int animationCount;
	
	public AnimatedEntity(String path, int x, int y, int size, int animationCount) {
		Texture texture = new Texture(path);
		this.animationCount = animationCount;
		
		animations = new Sprite[animationCount];
		for (int i = 0; i < animationCount; i++) {
			animations[i] = new Sprite(texture, (x + i) * size, y * size, size, size);
		}
	}
	
	public int getAnimationCount() {
		return animationCount;
	}
	
	public Sprite[] getAnimations() {
		return animations;
	}
	
	public Sprite getAnimationAt(int index) {
		if (index >= 0 && index < animationCount) {
			return animations[index];
		}
		return null;
	}
	
	
}
