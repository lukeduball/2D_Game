package com.tutorialgame.main;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyboardListener implements KeyListener, FocusListener{

	public boolean[] keys = new boolean[120];
	private Game game;
	
	public KeyboardListener(Game game){
		this.game = game;
	}
	
	@Override
	public void focusGained(FocusEvent event) {
		
	}

	@Override
	public void focusLost(FocusEvent event) {
		//When focus is lost in the game, the game doesn't believe key is still pressed
		for(int i = 0; i < keys.length; i++){
			keys[i] = false;
		}
	}

	@Override
	public void keyPressed(KeyEvent event) {
		int keyCode = event.getKeyCode();
		if(keyCode == KeyEvent.VK_F3)
			toggleRenderCollisionBox();
		game.keyTyped(event.getKeyCode());
		if(keyCode < keys.length){
			keys[keyCode] = true;
		}
		if(keys[KeyEvent.VK_CONTROL])
			game.handleCTRL(keys);
	}
	
	public void toggleRenderCollisionBox(){
		if(Game.showCollisionBox)
			Game.showCollisionBox = false;
		else
			Game.showCollisionBox = true;
	}

	@Override
	public void keyReleased(KeyEvent event) {
		int keyCode = event.getKeyCode();
		if(keyCode < keys.length){
			keys[keyCode] = false;
		}
	}

	@Override
	public void keyTyped(KeyEvent event) {
		//game.keyTyped(event.getKeyCode());
	}
	
	public boolean up(){
		return keys[KeyEvent.VK_W];
	}
	
	public boolean down(){
		return keys[KeyEvent.VK_S];
	}
	
	public boolean left(){
		return keys[KeyEvent.VK_A];
	}
	
	public boolean right(){
		return keys[KeyEvent.VK_D];
	}
	
}
