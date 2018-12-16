package com.tutorialgame.main;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.IOException;

import javax.imageio.ImageIO;

public class RenderHandler {
	
	private BufferedImage view;
	private Rectangle camera;
	private int[] pixels;
	public Game game;
	
	public RenderHandler(Game game, int width, int height)
	{
		view = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		camera = new Rectangle(0, 0, width, height);
		pixels = ((DataBufferInt)view.getRaster().getDataBuffer()).getData();
		this.game = game;
	}
	
	//Render Pixels to the Screen
	public void render(Graphics g)
	{
		g.drawImage(view, 0, 0, view.getWidth(), view.getHeight(), null);
	}
	
	//Set image pixels to screen pixels
	public void renderImage(BufferedImage image, int x, int y, int xZoom, int yZoom, boolean fixed)
	{
		int[] imagePixels = ((DataBufferInt)image.getRaster().getDataBuffer()).getData();
		this.renderArray(imagePixels, image.getWidth(), image.getHeight(), x, y, xZoom, yZoom, fixed);
	}
	
	public void renderSprite(Sprite sprite, int xPos, int yPos, int xZoom, int yZoom, boolean fixed)
	{
		this.renderArray(sprite.getPixels(), sprite.getWidth(), sprite.getHeight(), xPos, yPos, xZoom, yZoom, fixed);
	}
	
	public void renderArray(int[] renderPixels, int width, int height, int xPos, int yPos, int xZoom, int yZoom, boolean fixed){
		for(int h = 0; h < height; h++)
		{
			for(int w = 0; w < width; w++)
			{
				for(int zoomY = 0; zoomY < yZoom; zoomY++)
				{
					for(int zoomX = 0; zoomX < xZoom; zoomX++)
					{
						if(!fixed)
							setPixel((w*xZoom) + zoomX + xPos, (h*yZoom) + zoomY + yPos, renderPixels[w + (h*width)]);
						else
							setPixelWithoutCamera((w*xZoom) + zoomX + xPos, (h*yZoom) + zoomY + yPos, renderPixels[w + (h*width)]);
					}
				}
			}
		}
	}
	
	private void setPixel(int x, int y, int pixel)
	{
		if(x >= camera.x && y >= camera.y && x <= camera.x + camera.width && y <= camera.y + camera.height)
		{
			int location = x-camera.x + (y-camera.y)*view.getWidth();
			if(location < pixels.length && pixel != Game.alpha)
				pixels[location] = pixel;
		}
	}
	
	private void setPixelWithoutCamera(int x, int y, int pixel)
	{
		if(x >= 0 && y >= 0 && x <= camera.width && y <= camera.height)
		{
			int location = x + (y)*view.getWidth();
			if(location < pixels.length && pixel != Game.alpha)
				pixels[location] = pixel;
		}
	}
	
	public void renderRectangle(Rectangle rect, int xZoom, int yZoom, boolean fixed)
	{
		int[] rectanglePixels = rect.getPixels();
		if(rectanglePixels != null)
			this.renderArray(rectanglePixels, rect.width, rect.height, rect.x, rect.y, xZoom, yZoom, fixed);
	}
	
	public BufferedImage loadImage(String path){
		try 
		{
			BufferedImage loadedImage = ImageIO.read(Game.class.getResource(path));
			BufferedImage formattedImage = new BufferedImage(loadedImage.getWidth(), loadedImage.getHeight(), BufferedImage.TYPE_INT_RGB);
			formattedImage.getGraphics().drawImage(loadedImage, 0, 0, null);
			return formattedImage;
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	public Rectangle getCamera()
	{
		return this.camera;
	}
	
	public void clear()
	{
		for(int i = 0; i < pixels.length; i++)
			pixels[i] = 0xFFFFFF;		
	}
	
	public BufferedImage getView()
	{
		return this.view;
	}
}
