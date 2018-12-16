package com.tutorialgame.main;

import java.awt.Color;
import java.awt.MouseInfo;
import java.util.ArrayList;

import com.tutorialgame.main.Tiles.BuildingObject;

public class GuiBuildingBar extends Gui{
	
	private ArrayList<BuildingObject> buildingsList;
	private boolean[] mouseOver;
	private int selectedBuilding;

	public GuiBuildingBar(Game game, SpriteSheet[] imageSheet, int xPos, int yPos, int width, int height) {
		super(game, imageSheet, xPos, yPos, width, height);
		buildingsList = game.getTiles().getBuildingList();
		mouseOver = new boolean[10];
		selectedBuilding = 0;
		for(int i = 0; i < 10; i++){
			Button button = new Button(i, new Rectangle(this.xPos + 3*Game.getXZoom(), this.yPos + (3 + i*7)*Game.getYZoom(), 58, 7));
			buttonList.add(button);
		}
	}

	@Override
	public void handleButtonClick(int buttonID) {
		for(int i = 0; i < 10; i++){
			if(i < buildingsList.size() && buttonID == i){
				selectedBuilding = i;
				break;
			}
		}
	}
	
	@Override
	public void handleKeyTyped(int keyCode){
		
	}
	
	@Override
	public void handleMouseMoved(int x, int y, int xZoom, int yZoom){
		for(int i = 0; i < 10; i++){
			if(i < buildingsList.size()){
				if(buttonList.get(i).rect.intersectsWithRectangle(new Rectangle(x, y, 1, 1)))
					mouseOver[i] = true;
				else
					mouseOver[i] = false;
			}
		}
	}
	
	@Override
	public void render(RenderHandler renderer, int xZoom, int yZoom) {
		this.currentSprite = 0;
		this.drawImage(renderer, 0, 0, 64, 80, xPos, yPos, xZoom, yZoom);
		for(int i = 0; i < 10; i++){
			if(i < buildingsList.size()){
				if(i == selectedBuilding)
					this.drawImage(renderer, 3, 80, 58, 7, xPos + 3*xZoom, yPos + (3+ i*7)*yZoom, xZoom, yZoom);
				else if(mouseOver[i])
					this.drawImage(renderer, 3, 80, 58, 7, xPos + 3*xZoom, yPos + (3+ i*7)*yZoom, xZoom, yZoom);
				this.drawString(renderer, buildingsList.get(i).getName(), 4, 8+(i*7), xZoom, yZoom, Color.BLACK.getRGB());
			}
		}
	}
	
	public int getSelectedBuilding(){
		return this.selectedBuilding;
	}

	@Override
	public void tick(Game game) {
		
	}

}
