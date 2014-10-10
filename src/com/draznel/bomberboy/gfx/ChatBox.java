package com.draznel.bomberboy.gfx;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class ChatBox {

	public static final int MAX_MESSAGES_IN_HISTORY = 30;
	
	private Rectangle historyField;
	private Color historyFieldColor = new Color(0.0f, 0.0f, 0.0f, 0.8f);
	private Color historyFieldColorFocus = new Color(0.0f, 0.0f, 0.0f, 1.0f);
	
	private Rectangle inputField;
	private Color inputFieldColor = new Color(0.0f, 0.0f, 0.0f, 0.8f);
	
	private int maxLines = 0;
	private int maxCharactersInLine = 0;
	
	private int lineSpacing = 3;
	
	private List<String> messageHistory = new ArrayList<String>();
	
	public ChatBox(Rectangle historyField, Rectangle inputField, BitmapFont bitmapFont) {
		this.historyField = historyField;
		this.inputField = inputField;
		
		float width = ((float) (bitmapFont.getBounds("W").width));
		float height = bitmapFont.getBounds("W").height;
		
		maxLines = (int) (historyField.height / (height + lineSpacing));
		maxCharactersInLine = (int) (historyField.width / width);
						
		inputField.width = historyField.width;
	}
	
	public void append(String name, String message) {
		append(name + ":" + message);
	}
	
	public void append(String message) {
		String temp = "";
		while (message.length() >= maxCharactersInLine) {
			temp = message.substring(0, maxCharactersInLine - 1);
			message = message.substring(maxCharactersInLine - 1);
			messageHistory.add(temp);
		}
		messageHistory.add(message);
		
		while (messageHistory.size() > MAX_MESSAGES_IN_HISTORY) {
			messageHistory.remove(0);
		}
	}
	
	public void clear() {
		messageHistory.clear();
	}
	
	public String[] getHistoryMessages() {
		int max = messageHistory.size() < maxLines ? messageHistory.size() : maxLines;
		
		String[] historyMessages = new String[max];
		int startIndex = messageHistory.size() - max;
		for (int i = startIndex; i < messageHistory.size(); i++) {
			historyMessages[i - startIndex] = messageHistory.get(i);
		}
		
		return historyMessages;
	}
	
	public String[] getHistoryMessages(int fromIndex) {
		int max = messageHistory.size() < maxLines ? messageHistory.size() : maxLines;
		
		String[] historyMessages = new String[max];
		for (int i = fromIndex; i < messageHistory.size() && i - fromIndex < max; i++) {
			historyMessages[i - fromIndex] = messageHistory.get(i);
		}
		
		return historyMessages;
	}
	
	public Rectangle getHistoryField() {
		return historyField;
	}
	
	public Rectangle getInputField() {
		return inputField;
	}
	
	public Color getHistoryFieldColor() {
		return historyFieldColor;
	}
	
	public Color getHistoryFieldColorFocus() {
		return historyFieldColorFocus;
	}
	
	public Color getInputFieldColor() {
		return inputFieldColor;
	}
	
	public int getMaxCharactersInLine() {
		return maxCharactersInLine;
	}
	
	public int getMaxLines() {
		return maxLines;
	}
	
	public int getLineSpacing() {
		return lineSpacing;
	}
	
	public List<String> getMessageHistory() {
		return messageHistory;
	}

}
