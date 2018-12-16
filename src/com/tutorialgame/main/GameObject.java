package com.tutorialgame.main;

public interface GameObject {
	
	public void render(RenderHandler renderer, int xZoom, int yZoom);
	
	//Called at 60FPS
	public void tick(Game game);
	
	public void handleMouseClick(Rectangle mouseRect, int xZoom, int yZoom);
	
	public Rectangle getRectangle();
}
