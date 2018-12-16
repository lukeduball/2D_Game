package com.tutorialgame.main;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

public abstract class Gui {

	private SpriteSheet[] images;
	protected int currentSprite = 0;
	protected ArrayList<Button> buttonList;
	protected int width;
	protected int height;
	protected int xPos;
	protected int yPos;
	protected Game game;
	
	public Gui(Game game, SpriteSheet[] imageSheet, int xPos, int yPos, int width, int height){
		this.game = game;
		this.images = imageSheet;
		this.xPos = xPos;
		this.yPos = yPos;
		this.width = width;
		this.height = height;
		buttonList = new ArrayList();
	}
	
	public void render(RenderHandler renderer, int xZoom, int yZoom) {
		
	}

	public void tick(Game game) {
		
	}
	
	public abstract void handleButtonClick(int buttonID);

	public boolean handleMouseClick(int x, int y, int xZoom, int yZoom) {
		for(int i = 0; i < buttonList.size(); i++){
			if(buttonList.get(i).isClicked(x, y, xZoom, yZoom)){
				this.handleButtonClick(buttonList.get(i).buttonID);
			}
		}
		if(x >= this.xPos && x <= this.xPos+this.width*xZoom)
			if(y >= this.yPos && y <= this.yPos+this.height*yZoom)
				return true;
		return false;
	}
	
	public void handleKeyTyped(int keyCode){
		
	}
	
	public void handleMouseMoved(int x, int y, int xZoom, int yZoom){
		
	}
	
	public void drawImage(RenderHandler renderer, int spriteX, int spriteY, int width, int height, int x, int y, int xZoom, int yZoom){
		Sprite sprite = new Sprite(images[currentSprite], spriteX, spriteY, width, height);
		renderer.renderSprite(sprite, x, y, xZoom, yZoom, true);
	}
	
	public void drawString(RenderHandler renderer, String s, int x, int y, int xZoom, int yZoom, int color){
		Graphics g = renderer.getView().getGraphics();
		g.setColor(new Color(color));
		g.drawString(s, xPos+(x*xZoom), yPos+(y*yZoom));
	}
	
	public void setSpriteSheet(int i){
		if(i > images.length || i < 0){
			this.currentSprite = i;
		}
		else{
			System.out.println("Tried to set a sprite sheet that does not exist.");
		}
	}
	
	class Button{
		public final int buttonID;
		public Rectangle rect;
		
		public Button(int buttonID, Rectangle rect){
			this.buttonID = buttonID;
			this.rect = rect;
		}
		
		public boolean isClicked(int x, int y, int xZoom, int yZoom){
			if(x >= rect.x && x <= rect.x+rect.width*xZoom){
				if(y >= rect.y && y <= rect.y+rect.height*yZoom){
					return true;
				}
			}
			return false;
		}
	}

}
