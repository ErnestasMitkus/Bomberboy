package com.draznel.bomberboy.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.draznel.bomberboy.Main;
import com.draznel.bomberboy.entities.Player;
import com.draznel.bomberboy.gfx.Font;
import com.draznel.bomberboy.items.ItemSpritesheet;

public class HUD {

	Main game;
	Player player;
	OrthographicCamera camera;
	
	float hudX = 560f;
	float hudY = 0f;
	
	private Sprite background;
	private Sprite[] border;
	
	private int powerNumber = 3;
	private float[] borderX = {hudX + 26f, hudX + 26f + 36f * 1, hudX + 26f + 36f * 2};
	private float[] borderY = {hudY + 405f - 88f, hudY + 405f - 88f, hudY + 405f - 88f};
	
	private Sprite[] powerSprite;	
	
	private BitmapFont font;
	private BitmapFont fontName;
	
	public HUD(Main game, Player player) {
		this.game = game;
		this.player = player;
		
		readyHUD();
	}
	
	private void readyHUD() {
		camera = game.getCamera();
		
		background = new Sprite(new Texture(Gdx.files.internal("Sprites/HUD/background.png")));
		
		border = new Sprite[powerNumber];
		for (int i = 0; i < powerNumber; i++) {
			border[i] = new Sprite(new Texture(Gdx.files.internal("Sprites/HUD/border.png")));
		}
		
		powerSprite = new Sprite[powerNumber];
		// 0 Spread
		powerSprite[0] = ItemSpritesheet.Item_Spread_Sprite;
		// 1 Speed
		powerSprite[1] = ItemSpritesheet.Item_Speed_Sprite;		
		// 2 Bomb amount
		powerSprite[2] = ItemSpritesheet.Item_Bomb_Limit_Sprite;
		
		font = (new Font()).getFontBySize(14);
		fontName = (new Font()).getFontBySize(18);
	}
	
	
	public void render(SpriteBatch batch) {
		float dx = camera.position.x - Main.WIDTH / 2;
		float dy = (int) camera.position.y - Main.HEIGHT / 2;
		
		background.setPosition(dx + hudX, dy + hudY);
		background.draw(batch);

		
		for (int i = 0; i < powerNumber; i++) {
			border[i].setPosition(dx + borderX[i], dy + borderY[i]);
			powerSprite[i].setPosition(border[i].getX() + 2, border[i].getY() + 2);
			
			border[i].draw(batch);
			powerSprite[i].draw(batch);
		}
		
		String username = player.getUsername();
		float hudMiddle = background.getX() + background.getWidth() / 2;
		float hudTop = background.getY() + background.getHeight() - 15f;
		fontName.draw(batch, username, hudMiddle - font.getBounds(username).width / 2, hudTop - font.getBounds(username).height);
		
		renderNumbers(batch);
	}
	
	private void renderNumbers(SpriteBatch batch) {
		String str;
		float offsetX;
		float offsetY;
		float wx;
		float wy;
		
		// Spread
		str = "" + player.getPowers().getSpread();
		offsetX = -font.getBounds(str).width;
		offsetY = font.getBounds(str).height + 2f;
		wx = powerSprite[0].getX() + powerSprite[0].getWidth() - 3f + offsetX;
		wy = powerSprite[0].getY() + offsetY;
		font.draw(batch, str, wx, wy);

		// Speed
		str = "" + Math.round((player.getPowers().getMovSpeed() * 10)); // 3.4 mov speed = 34
		offsetX = -font.getBounds(str).width;
		offsetY = font.getBounds(str).height + 2f;
		wx = powerSprite[1].getX() + powerSprite[1].getWidth() - 3f + offsetX;
		wy = powerSprite[1].getY() + offsetY;
		font.draw(batch, str, wx, wy);
	
		// Bomb limit
		str = "" + player.getPowers().getMaxBombs();
		offsetX = -font.getBounds(str).width;
		offsetY = font.getBounds(str).height + 2f;
		wx = powerSprite[2].getX() + powerSprite[2].getWidth() - 3f + offsetX;
		wy = powerSprite[2].getY() + offsetY;
		font.draw(batch, str, wx, wy);
	}
	
	public float getWidth() {
		return background.getWidth();
	}
	
}
