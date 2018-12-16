package com.tutorialgame.main;

import java.awt.image.BufferedImage;

public class AnimatedSprite extends Sprite implements GameObject
{

	private Sprite[] sprites;
	private int currentSprite = 0;
	//Timer to change sprite
	private int renderTimer = 0;
	//Time to change sprite
	private int changeSpeed;
	
	
	/**
	 * @param images
	 * @param speed Speed represents how many frames will pass until the sprite changes
	 */
	public AnimatedSprite(BufferedImage[] images, int speed)
	{
		sprites = new Sprite[images.length];
		this.changeSpeed = speed;
		for(int i = 0; i < images.length; i++)
		{
			BufferedImage image = images[i];
			sprites[i] = new Sprite(image);
		}
	}
	
	public AnimatedSprite(Sprite[] sprites, int speed)
	{
		this.sprites = sprites;
		this.changeSpeed = speed;
	}

	//Render is handled specifically with the Layer class
	@Override
	public void render(RenderHandler renderer, int xZoom, int yZoom) {}

	@Override
	public void tick(Game game) 
	{
		if(renderTimer >= changeSpeed)
		{
			renderTimer = 0;
			increamentSprite();
		}
		renderTimer++;
	}
	
	public void increamentSprite()
	{
		currentSprite++;
		if(currentSprite >= sprites.length)
		{
			currentSprite = 0;
		}
	}
	
	public int getWidth()
	{
		return sprites[currentSprite].getWidth();
	}
	
	public int getHeight()
	{
		return sprites[currentSprite].getHeight();
	}
	
	public int[] getPixels()
	{
		return sprites[currentSprite].getPixels();
	}
	
	public void handleMouseClick(Rectangle rect, int xZoom, int yZoom){}

	@Override
	public Rectangle getRectangle() 
	{
		return new Rectangle();
	}

}
