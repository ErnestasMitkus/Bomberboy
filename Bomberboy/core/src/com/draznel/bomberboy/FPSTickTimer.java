package com.draznel.bomberboy;

import com.badlogic.gdx.Gdx;

public class FPSTickTimer {

	final static long second = 1000;
	
	int ticksPerSecond;
	
	long start;
	long last;
	long current;
	
	double duration;
	
	int FPS = 0;
	int ticks = 0;
	
	boolean canTick = false;
	
	boolean printNumbers = false;
	
	// Change manually
	boolean isCapped = true;
	
	public FPSTickTimer(int ticksPerSecond) {
		this.ticksPerSecond = ticksPerSecond <= 0 ? 60 : ticksPerSecond;
		duration = (double) second / this.ticksPerSecond;
		
		start = System.currentTimeMillis();
		last = start;
	}
	
	public void setPrintNumbers(boolean doPrinting) {
		printNumbers = doPrinting;
	}
	
	public void tick() {
		current = System.currentTimeMillis();
		FPS++;
		
		// if we should tick
		if (current - last >= duration) {
			ticks++;
			canTick = true;
			last += duration;
		}
		
		// if second has passed
		if (current - start >= second) {
			if (printNumbers)
				tock();
			
			start += second;
		}
		
	}
	
	private void tock() {
		//System.out.println("Ticks: " + ticks + " FPS: " + FPS);
		
		// Change isCapped manually
		Gdx.graphics.setTitle("Bomberboy (FPS[" + (isCapped?"capped":"uncapped") + "]: " + FPS + ")");
		
		ticks = 0;
		FPS = 0;
	}
	
	public boolean shouldTick() {
		return canTick;
	}
	
	public void setTick(boolean canTick) {
		this.canTick = canTick;
	}
	
}
