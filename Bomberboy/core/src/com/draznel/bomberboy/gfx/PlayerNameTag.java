package com.draznel.bomberboy.gfx;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class PlayerNameTag {

	public static final Color FOCUS_NAME_TAG_COLOR = new Color(0.0f, 0.0f, 0.0f, 0.8f);
	public static final Color DEFAULT_NAME_TAG_COLOR = new Color(0.0f, 0.0f, 0.0f, 0.6f);
	
	public static final Vector2 boxOffset = new Vector2(8.0f, 4.0f);
	
	private float x;
	private float y;
	private String name;
	
	private Color nameTagColor;
	private Color nameColor;
	
	private Rectangle nameTagBox;
	private Vector2 nameOffset = new Vector2(0, 0);
	
	BitmapFont font = null;
	
	private float fWidth = 56;
	private float fHeight = 12;
	
	boolean initialized = false;
	
	public PlayerNameTag(String name, Color nameTagColor, Color nameColor) {
		setName(name);
		this.nameTagColor = nameTagColor;
		this.nameColor = nameColor;
	}
	
	public void setName(String name) {
		this.name = name;
		initialized = false;
	}
	
	public void renderNameTag(ShapeRenderer shapeRenderer, float x, float y) {
		if (!initialized) {
			if (font == null) {
				font = new Font().getFontBySize(16);
			}
			fWidth = font.getBounds(name).width;
			fHeight = font.getBounds(name).height;
			nameTagBox = new Rectangle(0, 0, fWidth + boxOffset.x, fHeight + boxOffset.y);
			initialized = true;
		}
		
		nameTagBox.x = x + (AnimatedPlayer.playerWidth / 2 - fWidth / 2) - boxOffset.x / 2;
		nameTagBox.y = y - fHeight - boxOffset.y - boxOffset.y / 2;
		
		shapeRenderer.setColor(nameTagColor);
		shapeRenderer.rect(nameTagBox.x, nameTagBox.y, nameTagBox.width, nameTagBox.height);
	}
	
	public void renderName(SpriteBatch batch, float x, float y, Color color) {
		if (font == null) {
			font = new Font().getFontBySize(16);
		}
		renderName(batch, font, x, y, color);
	}
	
	public void renderName(SpriteBatch batch, BitmapFont BF, float x, float y, Color color) {
		BF.setColor(color);
		
		nameOffset.x = x + (AnimatedPlayer.playerWidth / 2 - fWidth / 2);
		nameOffset.y = y - boxOffset.y - 1;
		
		BF.draw(batch, name, nameOffset.x, nameOffset.y);
	}
	
}
