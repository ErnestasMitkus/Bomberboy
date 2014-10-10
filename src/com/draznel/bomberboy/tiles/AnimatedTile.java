package com.draznel.bomberboy.tiles;


public class AnimatedTile extends BasicTile {

	private int[][] animationTileCoords;
	private int currentAnimationIndex;
	private long lastIterationTime;
	private int animationSwitchDelay;
	
	public AnimatedTile(int id, int[][] animationCoords, int animationSwitchDelay, int levelColor) {
		super(id, animationCoords[0][0], animationCoords[0][0], levelColor);
		this.animationTileCoords = animationCoords;
		this.currentAnimationIndex = 0;
		this.animationSwitchDelay = animationSwitchDelay;
		this.lastIterationTime = System.currentTimeMillis() - animationSwitchDelay;
	}
	
	public void tick() {
		if ((System.currentTimeMillis() - lastIterationTime) >= (animationSwitchDelay)) {
			lastIterationTime = System.currentTimeMillis();
			currentAnimationIndex = ((currentAnimationIndex + 1) % animationTileCoords.length);
			this.tileId = (animationTileCoords[currentAnimationIndex][0] + (animationTileCoords[currentAnimationIndex][1] * 32));
		}
	}

}
