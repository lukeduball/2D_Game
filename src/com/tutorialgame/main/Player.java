package com.tutorialgame.main;

import java.awt.Color;

public class Player implements GameObject{

	Rectangle playerRectangle;
	int speed = 8;
	private Sprite[] playerMovement;
	private int cuedSprite = 0;
	private int heading  = 1;
	
	private int currentDimension;
	
	public Player(){
		playerRectangle = new Rectangle(18, 26, 18, 26);
		playerRectangle.generateGraphics(3, Color.BLUE.getRGB());
	}
	
	public void generateGraphics(Game game, SpriteSheet spriteSheet){
		playerMovement = new Sprite[8];
		Sprite[] animatedMovement = new Sprite[4];
		for(int i = 0; i < animatedMovement.length; i++){
			Sprite[] sprites = new Sprite[8];
			for(int j = 0; j < sprites.length; j++){
				sprites[j] = new Sprite(spriteSheet, j*20, i*26, 20, 26);
				if(j == 0)
					playerMovement[i] = sprites[0];
			}
			playerMovement[i+4] = new AnimatedSprite(sprites, 5);
		}
	}
	
	@Override
	public void render(RenderHandler renderer, int xZoom, int yZoom) {
		//renderer.renderRectangle(this.playerRectangle, xZoom, yZoom);
		renderer.renderSprite(playerMovement[cuedSprite], this.playerRectangle.x, this.playerRectangle.y, xZoom, yZoom, false);
		//renderer.renderRectangle(this.playerRectangle, xZoom, yZoom, false);
	}

	@Override
	public void tick(Game game) {
		KeyboardListener keyListener = game.getKeyListener();
		if(keyListener.up()){
			if(game.getMap().canMoveObject(this, playerRectangle.x, playerRectangle.y-speed))
				playerRectangle.y -= speed;
			move(2);
		}
		else if(keyListener.down()){
			if(game.getMap().canMoveObject(this, playerRectangle.x, playerRectangle.y+speed))
				playerRectangle.y += speed;
			move(3);
		}
		else if(keyListener.left()){
			if(game.getMap().canMoveObject(this, playerRectangle.x-speed, playerRectangle.y))
				playerRectangle.x -= speed;
			move(1);
		}
		else if(keyListener.right()){
			if(game.getMap().canMoveObject(this, playerRectangle.x+speed, playerRectangle.y))
				playerRectangle.x += speed;
			move(0);
		}
		else{
			this.cuedSprite = heading;
		}
		
		this.updateCamera(game, game.getRenderer().getCamera());
		
		if(playerMovement[cuedSprite] instanceof AnimatedSprite){
			AnimatedSprite sprite = (AnimatedSprite)playerMovement[cuedSprite];
			sprite.tick(game);
		}
	}
	
	//0 for right, 1 for left, 2 for up, 3 for down
	public void move(int direction){
		this.cuedSprite = direction+4;
		this.heading = direction;
	}
	
	public void updateCamera(Game game, Rectangle camera){
		Rectangle dimensionRect = game.getMap().getDimension(this.currentDimension).getAdjustedRect();
		if(playerRectangle.x >=  dimensionRect.x + (camera.width / 2) && playerRectangle.x <= (dimensionRect.x+dimensionRect.width)-(camera.width/2))
			camera.x = playerRectangle.x - (camera.width / 2);
		if(playerRectangle.y >=  dimensionRect.y + (camera.height / 2) && playerRectangle.y <= (dimensionRect.y+dimensionRect.height)-(camera.height/2))
			camera.y = playerRectangle.y - (camera.height / 2);
	}
	
	public void setDimension(int i, int x, int y){
		this.currentDimension = i;
		this.playerRectangle.x = x;
		this.playerRectangle.y = y;
	}
	
	public void handleMouseClick(Rectangle rect, int xZoom, int yZoom){}
	
	@Override
	public Rectangle getRectangle(){
		return this.playerRectangle;
	}
	
	public int getCurrentDimension(){
		return this.currentDimension;
	}

}
