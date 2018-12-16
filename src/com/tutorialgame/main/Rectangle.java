package com.tutorialgame.main;

public class Rectangle 
{
	int x,y,width,height;
	private int[] pixels;
	
	Rectangle(int x, int y, int width, int height) 
	{
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	Rectangle()
	{
		this(0, 0, 0, 0);
	}
	
	public boolean intersectsWithRectangle(Rectangle rect)
	{
		int dimentionX = rect.x+rect.width*Game.getXZoom();
		int dimentionY = rect.y+rect.height*Game.getYZoom();
		int thisX = x+width*Game.getXZoom();
		int thisY = y+height*Game.getYZoom();
		if(rect.x < thisX && dimentionX > x)
			if(rect.y < thisY && dimentionY > y)
				return true;
		return false;
	}
	
	public boolean intersectsWithOverlay(Rectangle rect)
	{
		int dimentionX = rect.x+rect.width*Game.getXZoom();
		int dimentionY = rect.y+rect.height*Game.getYZoom();
		int thisX = x+width*Game.getXZoom();
		int thisY = y+height*Game.getYZoom();
		if(rect.x < thisX && dimentionX > x)
			if(dimentionY-1 < thisY && dimentionY > y)
				return true;
		return false;
	}
	
	public void generateGraphics(int color)
	{
		pixels = new int[width*height];
		for(int y = 0; y < height; y++)
			for(int x = 0; x < width; x++)
				pixels[x+y*width] = color;
	}
	
	public void generateGraphics(int borderWidth, int color)
	{
		pixels = new int[width*height];
		for(int i = 0; i < pixels.length; i++)
			pixels[i]=Game.alpha;
 		for(int y = 0; y < borderWidth; y++)
 		{
			for(int x = 0; x < width; x++)
			{
				pixels[x + y*width] = color;//Set color of top
				pixels[x + (height-1-y)*width] = color;//Set color of bottom
			}
		}
		
		for(int x = 0; x < borderWidth; x++)
		{
			for(int y = 0; y < height; y++)
			{
				pixels[x + y*width] = color;//Set color of left
				pixels[(width-1)-x + y*width] = color;//Set color of right
			}
		}
		
	}
	
	public int[] getPixels()
	{
		if(pixels != null)
			return pixels;
		else
			System.out.println("Attempted to retrieve pixels from a Reactangle without generated graphics.");
		return null;

	}
}
