package com.draznel.bomberboy.gfx;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

public class Font {

	private List<BitmapFont> font = new ArrayList<BitmapFont>();
	private List<Integer> sizes = new ArrayList<Integer>();
	
	public Font() {
		addFont("font8");
		addFont("font10");
		addFont("font12");
		addFont("font14");
		addFont("font16");
		addFont("font18");
		addFont("font20");
		addFont("font24");
		addFont("font30");
		addFont("font32");
		addFont("font36");
	}
	
	public void addFont(String fontName) {
		int size = Integer.parseInt(fontName.substring(4));
		BitmapFont BF = new BitmapFont(Gdx.files.internal("Fonts/" + fontName + ".fnt"), Gdx.files.internal("Fonts/" + fontName + ".png"), false);
		font.add(BF);
		sizes.add(size);
	}
	
	public BitmapFont getFontBySize(int size) {
		BitmapFont BF = font.get(sizes.indexOf(12));
		if (sizes.contains(size)) {
			BF = font.get(sizes.indexOf(size));
		} else {			
			System.err.println("No " + size + " size font found. Fetching size 12.");
		}
		return BF;
	}
	
	public BitmapFont getBFont(int index) { 
		if (index < 0 || index >= font.size()) 
			return null;
		return font.get(index); 
	}
}
