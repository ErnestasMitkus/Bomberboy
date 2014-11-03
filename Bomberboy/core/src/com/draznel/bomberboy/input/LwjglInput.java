package com.draznel.bomberboy.input;

import java.util.LinkedList;
import java.util.Queue;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.draznel.bomberboy.Main;
import com.draznel.bomberboy.gfx.CallBack;

public class LwjglInput implements InputProcessor {

	public class MouseEvent {
		public static final int EVENT_TOUCH_DOWN = 0;
		public static final int EVENT_TOUCH_UP = 1;
		
		public int event;
		public int screenX, screenY;
		public int button;
		
		public MouseEvent(int event, int screenX, int screenY, int button) {
			this.event = event;
			this.screenX = screenX;
			this.screenY = screenY;
			this.button = button;
		}
	}
	
	private boolean[] keys = new boolean[256];
	
	Main game;
	
	public boolean autoReset = false;
	
	public int scrollAmount = 0;
	
	public Queue<MouseEvent> queue = new LinkedList<MouseEvent>();
	
	public LwjglInput(Main game) {
		this.game = game;	
		for (boolean k : keys) {
			k = false;
		}
	}
	
	@Override
	public boolean keyDown(int keycode) {
		// TODO Auto-generated method stub
		keys[keycode] = true;
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
//		System.out.println("KeyUp [" + Keys.toString(keycode) +"] with keycode: " + keycode);
		// TODO Auto-generated method stub
		switch(keycode) {
		case Keys.SHIFT_LEFT:
			keys[Keys.SHIFT_LEFT] = false;
			break;
		case Keys.SHIFT_RIGHT:
			keys[Keys.SHIFT_RIGHT] = false;
			break;
		default:
			break;
		}
		
		if (autoReset) {
			keys[keycode] = false;
		}
		
		return false;
	}
	
	public void resetKey(int keycode) {
		keys[keycode] = false;
	}
	
	public void resetAll() {
		for(int i = 0; i < keys.length; i++) {
			keys[i] = false;
		}
	}
	
	public boolean isPressed(int keycode) {
		return keys[keycode];
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		queue.add(new MouseEvent(MouseEvent.EVENT_TOUCH_DOWN, screenX, screenY, button));
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		queue.add(new MouseEvent(MouseEvent.EVENT_TOUCH_UP, screenX, screenY, button));
		return false;
	}
	
	public int getQueueSize() {
		return queue.size();
	}
	public MouseEvent getMouseEvent() {
		return queue.poll(); // returns null if queue.size() == 0;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		scrollAmount += amount;
		return false;
	}
	
	public void ifPressed(int keycode, CallBack CB) {
		if (isPressed(keycode)) {
			boolean shiftPressed = isPressed(Keys.SHIFT_LEFT) || isPressed(Keys.SHIFT_RIGHT);
			CB.methodToCallBack(keycode, shiftPressed);
		}
	}

}
