package com.tutorialgame.main;

import java.awt.Color;
import java.awt.event.KeyEvent;

import com.tutorialgame.main.Tiles.Tile;

public class GuiHUD extends Gui{

	private int selectedTile = 0;
	private int tileIndex = 0;
	
	public GuiHUD(Game game, SpriteSheet[] imageSheet, int x, int y, int width, int height) {
		super(game, imageSheet, x, y, width, height);
		this.yPos = height/2;
		for(int i = 0; i < 8; i++){
			Button button = new Button(i, new Rectangle(this.xPos+2*Game.getXZoom(), this.yPos+(2+(i*20))*Game.getYZoom(), 16, 16));
			buttonList.add(button);
		}
	}

	@Override
	public void handleButtonClick(int buttonID) {
		this.selectedTile = buttonID;
	}
	
	@Override
	public void handleKeyTyped(int keyCode){
		if(keyCode == KeyEvent.VK_DOWN)
			if(9+tileIndex < game.getTiles().getTileList().size())
				tileIndex++;
		if(keyCode == KeyEvent.VK_UP)
			if(tileIndex-1 >= 0)
				tileIndex--;
			
	}
	
	public int getSelectedTile(){
		return this.selectedTile+tileIndex;
	}
	
	public void render(RenderHandler renderer, int xZoom, int yZoom) {
		this.currentSprite = 0;
		this.drawImage(renderer, 0, 0, 20, 160, this.xPos, this.yPos, xZoom, yZoom);
		this.drawImage(renderer, 20, 0, 22, 22, this.xPos-2, this.yPos-2+(20*selectedTile)*yZoom, xZoom, yZoom);
		for(int i = 0; i < 8; i++){
			if(i+1+tileIndex < game.getTiles().getTileList().size()){
				Tile tile = game.getTiles().getTileList().get(i+1+tileIndex);
				renderer.renderSprite(tile.sprite, this.xPos+2*xZoom, this.yPos+(2+(20*i))*yZoom, xZoom, yZoom, true);
			}
		}
//		for(int i = 0; i < this.buttonList.size(); i++){
//			Button button  = this.buttonList.get(i);
//			button.rect.generateGraphics(Color.CYAN.getRGB());
//			renderer.renderRectangle(button.rect, xZoom, yZoom, true);
//		}
	}

}
