package com.tutorialgame.main;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JFrame;


public class Game extends JFrame implements Runnable
{

	private static final long serialVersionUID = 8393730448341462237L;

	public static int alpha = 0xFFFF00DC;
	
	private Canvas canvas = new Canvas();
	
	private boolean running = false;
	private Thread gameThread;
	
	private static int xZoom = 3;
	private static int yZoom = 3;
	public static boolean showCollisionBox = false;
	
	private RenderHandler renderer;
	private SpriteSheet sheet;
	private SpriteSheet playerSheet;
	
	private final String DIRECTORY = "bin/com/tutorialgame/main/";
	private Tiles tiles;
	private Map map;
	private Player player;
	
	private GameObject[] objects;
	private KeyboardListener keyListener = new KeyboardListener(this);
	private MouseEventListener mouseListener = new MouseEventListener(this);
	
	private ArrayList<Gui> openGUIs = new ArrayList<Gui>();
	private GuiHUD guiHUD;
	private GuiBuildingBar guiBuildingMenu;
	
	public ArrayList<GameObject> renderQueue = new ArrayList<GameObject>();
	
	public int objectType = 0;
	
	public Game()
	{	
		canvas.requestFocus();
		//Shutdown the program when exiting
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//Sets the initial position of the frame.
		canvas.setBounds(0, 0, 1000, 800);
		
		//Add our graphics component
		this.add(canvas);
		pack();
		
		//Puts Frame in center of the screen
		this.setLocationRelativeTo(null);
		
		this.setVisible(true);
		
		//Creates our object for buffer strategy
		canvas.createBufferStrategy(3);
		renderer = new RenderHandler(this, canvas.getWidth(), canvas.getHeight());
		
		BufferedImage sheetImage = renderer.loadImage("resources/Tiles1.png");
		sheet = new SpriteSheet(sheetImage);
		sheet.loadSprites(16, 16);
		
		//Animated Image Test
		Sprite[] sprites = new Sprite[5];
		sprites[0] = new Sprite(sheet, 0, 0, 16, 16);
		sprites[1] = new Sprite(sheet, 1, 0, 16, 16);
		sprites[2] = new Sprite(sheet, 2, 0, 16, 16);
		sprites[3] = new Sprite(sheet, 3, 0, 16, 16);
		sprites[4] = new Sprite(sheet, 4, 0, 16, 16);
		
		
		//Load Tiles
		tiles = new Tiles(new File(DIRECTORY+"Tiles.txt"), new File(DIRECTORY+"Buildings.txt"), sheet);
		//Load Map
		map = new Map(this, new File(DIRECTORY+"Map.txt"), tiles);
		
		BufferedImage hudImage = renderer.loadImage("resources/HUD.png");
		SpriteSheet hudSprites = new SpriteSheet(hudImage);
		guiHUD = new GuiHUD(this, new SpriteSheet[]{hudSprites}, 10, 0, 22, 160);
		this.openGUIs.add(guiHUD);
		
		BufferedImage buildingMenuImage = renderer.loadImage("resources/BuildingSelector.png");
		SpriteSheet buildingMenuSprite = new SpriteSheet(buildingMenuImage);
		guiBuildingMenu = new GuiBuildingBar(this, new SpriteSheet[]{buildingMenuSprite}, canvas.getWidth() - 64*xZoom, 0, 64, 80);
		
		
		BufferedImage playerSheetImage = renderer.loadImage("resources/Player.png");
		playerSheet = new SpriteSheet(playerSheetImage);
		playerSheet.loadSprites(20, 26);
		
		//Load Objects
		objects = new GameObject[1];
		player = new Player();
		player.generateGraphics(this, playerSheet);
		objects[0] = player;
		
		//Add Listeners
		canvas.addKeyListener(keyListener);
		canvas.addFocusListener(keyListener);
		canvas.addMouseListener(mouseListener);
		canvas.addMouseMotionListener(mouseListener);
		this.start();
		
	}
	
	public synchronized void start()
	{
		//Creates the thread and starts it -- have to pass in component to Thread
		gameThread = new Thread(this);
		gameThread.start();
		running = true;
	}
	
	public synchronized void stop()
	{
		try
		{
			gameThread.join();
			running = false;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() 
	{
		long lastTime = System.nanoTime(); //longs can be 2^63 highest value
		double nanoSecondConversion = 1000000000.0 / 60; //Nano second conversion divided by desired FPS(60 in this case)
		//Change in seconds variable
		double deltaSeconds = 0;
		
		while(running)
		{
			long now = System.nanoTime();
			deltaSeconds += (now - lastTime) / nanoSecondConversion;
			while(deltaSeconds >= 1)
			{
				tick();
				deltaSeconds = 0;
			}
			render();
			lastTime = now;
		}
		stop();
	}
	
	public void render()
	{
		BufferStrategy bufferStrategy = canvas.getBufferStrategy();
		Graphics graphics = bufferStrategy.getDrawGraphics();
		super.paint(graphics);
		for(int i = 0; i < objects.length; i++)
			this.renderQueue.add(objects[i]);
		map.render(renderer, this.xZoom, this.yZoom);
		map.renderObjectsWithGameLayer(this, xZoom, yZoom);
//		for(int i = 0; i < objects.length; i++)
//			objects[i].render(renderer, this.xZoom, this.yZoom);
		for(int i = 0; i < openGUIs.size(); i++)
			openGUIs.get(i).render(renderer, this.xZoom, this.yZoom);
		
//		Graphics g = renderer.getView().getGraphics();
//		g.setColor(Color.BLACK);
//		g.setFont(g.getFont().deriveFont(20.0F));
//		g.drawString("THIS IS A TEST", 100, 100);
//		System.out.println((g.getFontMetrics(g.getFont()).getHeight()));
		
		renderQueue.clear();
		renderer.render(graphics);
		
		graphics.dispose();
		bufferStrategy.show();
		renderer.clear();
	}

	
	public void tick()
	{
		for(int i = 0; i < objects.length; i++)
			objects[i].tick(this);
		for(int i = 0; i < openGUIs.size(); i++)
			openGUIs.get(i).tick(this);
	}
	
	public static void main(String[] args)
	{
		new Game();
	}
	
	public KeyboardListener getKeyListener()
	{
		return keyListener;
	}
	
	public RenderHandler getRenderer()
	{
		return renderer;
	}
	
	public Map getMap()
	{
		return map;
	}
	
	public MouseEventListener getMouseListener()
	{
		return mouseListener;
	}
	
	public static int getXZoom()
	{
		return xZoom;
	}
	
	public static int getYZoom()
	{
		return yZoom;
	}
	
	public Tiles getTiles()
	{
		return this.tiles;
	}
	
	public void mouseMoved(int x, int y)
	{
		for(int i = 0; i < openGUIs.size(); i++)
			openGUIs.get(i).handleMouseMoved(x, y, this.xZoom, this.yZoom);
	}
	
	public void leftClicked(int x, int y)
	{
		boolean flag = false;
		for(int i = 0; i < openGUIs.size(); i++)
		{
			if(flag == true)
				openGUIs.get(i).handleMouseClick(x, y, this.xZoom, this.yZoom);
			else
				flag = openGUIs.get(i).handleMouseClick(x, y, this.xZoom, this.yZoom);
		}
		
		if(!flag)
		{
			x = (int)Math.floor((x + renderer.getCamera().x)/(16.0*xZoom));
			y = (int)Math.floor((y + renderer.getCamera().y)/(16.0*yZoom));
			if(this.objectType == 0)
				map.setTile(this.guiHUD.getSelectedTile()+1, x, y);
			else
				map.setBuilding(this.guiBuildingMenu.getSelectedBuilding(), x, y);
		}
	}
	
	public void rightClicked(int x, int y)
	{
		x = (int)Math.floor((x + renderer.getCamera().x)/(16.0*xZoom));
		y = (int)Math.floor((y + renderer.getCamera().y)/(16.0*yZoom));
		if(this.objectType == 0)
			map.removeTile(x, y);
		else
			map.removeBuilding(x, y);
	}
	
	public void handleCTRL(boolean[] keys)
	{
		if(keys[KeyEvent.VK_S])
			map.saveMap();
	}
	
	public void keyTyped(int keyCode){
		if(keyCode == KeyEvent.VK_B)
			this.toggleBuildingMenu();
		for(int i = 0; i < openGUIs.size(); i++){
			openGUIs.get(i).handleKeyTyped(keyCode);
		}
	}
	
	private void toggleBuildingMenu(){
		if(this.openGUIs.contains(this.guiBuildingMenu))
		{
			this.objectType = 0;
			this.openGUIs.remove(this.guiBuildingMenu);
		}
		else
		{
			this.objectType = 1;
			this.openGUIs.add(this.guiBuildingMenu);
		}
	}
	
	public int getPlayerDimension()
	{
		return this.player.getCurrentDimension();
	}
	
}
