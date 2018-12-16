package com.tutorialgame.main;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Tiles 
{
	
	private SpriteSheet spriteSheet;
	private ArrayList<Tile> tileList = new ArrayList<Tile>();
	private ArrayList<BuildingObject> buildingList = new ArrayList<BuildingObject>();
	
	public Tiles(File tilesFile, File buildingFile, SpriteSheet spriteSheet)
	{
		this.spriteSheet = spriteSheet;
		try 
		{
			Scanner scanner = new Scanner(tilesFile);
			while(scanner.hasNextLine())
			{
				String line = scanner.nextLine();
				if(!line.startsWith("//"))
				{
					String[] splitString = line.split(",");
					String tileName = splitString[0];
					int spriteX = Integer.parseInt(splitString[1]);
					int spriteY = Integer.parseInt(splitString[2]);
					Tile tile = new Tile(tileName, spriteSheet.getSprite(spriteX, spriteY));
					tileList.add(tile);
				}
			}
			scanner = new Scanner(buildingFile);
			while(scanner.hasNextLine())
			{
				String line = scanner.nextLine();
				if(!line.startsWith("//"))
				{
					String[] splitString = line.split(",");
					String buildingName = splitString[0];
					ArrayList<BuildingBlock> list = new ArrayList<BuildingBlock>();
					String[] blockSplitter = splitString[1].split("<");
					for(int i = 0; i < blockSplitter.length; i++)
					{
						String[] blockObj = blockSplitter[i].split(":");
						BuildingBlock block = new BuildingBlock(Integer.parseInt(blockObj[1]), Integer.parseInt(blockObj[2]), tileList.get(Integer.parseInt(blockObj[0])).sprite);
						list.add(block);
					}
					Rectangle[] rect = null;
					ArrayList<Rectangle> rectList = new ArrayList<Rectangle>();
					if(splitString.length >= 3)
					{
						splitString[2] = splitString[2].substring(1, splitString[2].length()-1);
						String[] collisionSplitter = splitString[2].split("\\]\\[");
						for(int i = 0; i < collisionSplitter.length; i++)
						{
							String[] rectString = collisionSplitter[i].split(":");
							rectList.add(new Rectangle(Integer.parseInt(rectString[0]), Integer.parseInt(rectString[1]), Integer.parseInt(rectString[2]), Integer.parseInt(rectString[3])));
						}
						rect = rectList.toArray(new Rectangle[rectList.size()]);
					}
					buildingList.add(new BuildingObject(buildingName, list, rect));
				}
			}
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		}
	}
	
	public ArrayList<Tile> getTileList()
	{
		return this.tileList;
	}
	
	public ArrayList<BuildingObject> getBuildingList()
	{
		return this.buildingList;
	}
	
	public void renderTile(int tileID, RenderHandler renderer, int xPos, int yPos, int xZoom, int yZoom)
	{
		if(tileID < tileList.size() && tileID >= 0)
		{
			renderer.renderSprite(tileList.get(tileID).sprite, xPos, yPos, xZoom, yZoom, false);
		}
		else
		{
			System.out.println("TileID:"+tileID+" is not within range " + tileList.size()+".");
		}
	}
	
	public void renderBuilding(int buildingID, RenderHandler renderer, int xPos, int yPos, int xZoom, int yZoom)
	{
		if(buildingID < buildingList.size() && buildingID >= 0)
		{
			buildingList.get(buildingID).renderBuildingBlocks(renderer, xPos, yPos, xZoom, yZoom);
		}
	}
	
	class Tile
	{
		public String tileName;
		public Sprite sprite;
		
		public Tile(String tileName, Sprite sprite)
		{
			this.tileName = tileName;
			this.sprite = sprite;
		}
	}
	
	public class BuildingObject 
	{

		private String buildingName; 
		private ArrayList<BuildingBlock> blockList;
		private Rectangle[] collisionBoxes;
		
		public BuildingObject(String buildingName, ArrayList<BuildingBlock> blockList, Rectangle[] collisionRect)
		{
			this.buildingName = buildingName;
			this.blockList = blockList;
			this.collisionBoxes = collisionRect;
		}
		
		public String getName()
		{
			return buildingName;
		}
		
		public Rectangle[] getCollisionBoxes()
		{
			return collisionBoxes;
		}
		

		public void renderBuildingBlocks(RenderHandler renderer, int xPos, int yPos, int xZoom, int yZoom)
		{
			int xAdjustment = xZoom * 16;
			int yAdjustment = yZoom * 16;
			for(int i = 0; i < blockList.size(); i++){
				BuildingBlock block  = blockList.get(i);
				renderer.renderSprite(block.sprite, xPos+block.xOff*xAdjustment, yPos+block.yOff*yAdjustment, xZoom, yZoom, false);
			}
		}
		
	}
	
	class BuildingBlock
	{
		private int xOff;
		private int yOff;
		private Sprite sprite;
		
		public BuildingBlock(int x, int y, Sprite sprite)
		{
			this.xOff = x;
			this.yOff = y;
			this.sprite = sprite;
		}
	}

}
