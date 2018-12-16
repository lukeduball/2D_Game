package com.tutorialgame.main;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Scanner;

public class Map 
{
	private Tiles tileSet;
	
	private Dimension[] dimensions;
	
	private File mapFile;
	private Game game;
	
//	private ArrayList<MappedTile> mappedTiles = new ArrayList<MappedTile>();
//	private ArrayList<MappedBuilding> mappedBuildings = new ArrayList<MappedBuilding>();
	private HashMap<Integer, String> comments = new HashMap<Integer, String>();
	
	public Map(Game game, File mapFile, Tiles tileSet)
	{
		this.game = game;
		this.tileSet = tileSet;
		this.mapFile = mapFile;
		try
		{
			Scanner scanner = new Scanner(mapFile);
			int currentLine = 0;
			ArrayList<Dimension> dimensionsList = new ArrayList<Dimension>();
			Dimension dimension = new Dimension("Default", new Rectangle(0, 0, 100, 100));
			dimensionsList.add(dimension);
			int currentDimension = 0;
			while(scanner.hasNextLine()){
				String line = scanner.nextLine();
				if(!line.startsWith("//"))
				{
					if(line.contains(":"))
					{
						String[] splitString = line.split(":");
						if(splitString[0].equalsIgnoreCase("Dimension"))
						{
							dimension = new Dimension(splitString[1], new Rectangle(0,0,Integer.parseInt(splitString[2]),Integer.parseInt(splitString[3])));
							currentDimension = addDimension(dimensionsList, dimension);
						}
						else if(splitString[0].equalsIgnoreCase("Fill"))
						{
							dimensionsList.get(currentDimension).fillTileID = Integer.parseInt(splitString[1]);
							continue;
						}
					}
					if(line.startsWith("OBJ"))
					{
						String[] splitString = line.split(",");
						if(splitString.length >= 4)
						{
							MappedBuilding building = new MappedBuilding(Integer.parseInt(splitString[1]),
									Integer.parseInt(splitString[2]),
									Integer.parseInt(splitString[3]));
							building.setCollisionBoxes(this.tileSet.getBuildingList().get(building.id).getCollisionBoxes());
							dimensionsList.get(currentDimension).mappedBuildings.add(building);
						}
					}
					else{
						String[] splitString = line.split(",");
						if(splitString.length >= 3)
						{
							MappedTile mappedTile = new MappedTile(Integer.parseInt(splitString[0]),
									Integer.parseInt(splitString[1]),
									Integer.parseInt(splitString[2]));
							dimensionsList.get(currentDimension).mappedTiles.add(mappedTile);
						}
					}
				}
				else
				{
					comments.put(currentLine, line);
				}
				currentLine++;
			}
			dimensions = dimensionsList.toArray(new Dimension[dimensionsList.size()]);
		}
		catch(FileNotFoundException e)
		{
			e.printStackTrace();
		}
		for(int i = 0; i < dimensions.length; i++)
			this.sortBuildingList(dimensions[i]);
	}
	
	private int addDimension(ArrayList<Dimension> list, Dimension dimension)
	{
		for(int i = 0; i < list.size(); i++)
		{
			Dimension d = list.get(i);
			if(d.dimensionName.equalsIgnoreCase(dimension.dimensionName))
			{
				d.dimensionRect = dimension.dimensionRect;
				return i;
			}
		}
		list.add(dimension);
		return list.size()-1;
	}
	
	public void sortBuildingList(Dimension dimension)
	{
		Collections.sort(dimension.mappedBuildings, new Comparator<MappedBuilding>()
		{
			@Override
			public int compare(MappedBuilding bld1, MappedBuilding bld2) 
			{
				return bld1.y > bld2.y ? 1 : -1;
			}
			
		});
	}
	
	public Dimension getDimension(int currentDimension)
	{
		return dimensions[currentDimension];
	}
	
	public Dimension getDimensionByName(String name)
	{
		for(int i = 0; i < dimensions.length; i++)
		{
			Dimension dimension = dimensions[i];
			if(dimension.dimensionName.equalsIgnoreCase(name))
			{
				return dimension;
			}
		}
		System.out.println("Could not find a dimension by the name of "+name);
		return dimensions[0];
	}
	
	public void render(RenderHandler renderer, int xZoom, int yZoom)
	{
		int xOffset = 16*xZoom;
		int yOffset = 16*yZoom;
		if(dimensions[game.getPlayerDimension()].fillTileID >= 0)
		{
			Rectangle camera = renderer.getCamera();
			for(int x = camera.x - xOffset - (camera.x % xOffset); x < camera.x + camera.width; x+= xOffset)
			{
				for(int y = camera.y - yOffset - (camera.y % yOffset); y < camera.y + camera.height; y+= yOffset)
				{
					tileSet.renderTile(dimensions[game.getPlayerDimension()].fillTileID, renderer, x, y, xZoom, yZoom);
				}
			}
		}
		
		for(int i = 0; i < dimensions[game.getPlayerDimension()].mappedTiles.size(); i++)
		{
			MappedTile tile = dimensions[game.getPlayerDimension()].mappedTiles.get(i);
			tileSet.renderTile(tile.id, renderer, tile.x*xOffset, tile.y*yOffset, xZoom, yZoom);
		}
	}
	
	public boolean canMoveObject(GameObject object, int xMove, int yMove)
	{
		Rectangle objectRect = object.getRectangle();
		int xMove2 = xMove + objectRect.width*Game.getXZoom();
		int yMove2 = yMove + objectRect.height*Game.getYZoom();
		Rectangle dimensionRect = dimensions[game.getPlayerDimension()].getAdjustedRect();
		if(xMove < dimensionRect.x || xMove2 > dimensionRect.x+dimensionRect.width)
			return false;
		if(yMove < dimensionRect.y || yMove2 > dimensionRect.y+dimensionRect.height)
			return false;
		for(int i = 0; i < dimensions[game.getPlayerDimension()].mappedBuildings.size(); i++)
		{
			Rectangle[] rect = dimensions[game.getPlayerDimension()].mappedBuildings.get(i).collisionBoxes;
			if(rect != null)
			{
				for(int j = 0; j < rect.length; j++)
				{
					Rectangle rectangle = rect[j];
					if(rectangle.intersectsWithOverlay(new Rectangle(xMove, yMove, objectRect.width, objectRect.height)))
						return false;
				}
			}
		}
		return true;
	}
	
	public void renderObjectsWithGameLayer(Game game, int xZoom, int yZoom)
	{
		int xOff = 16*xZoom;
		int yOff = 16*yZoom;
		
		int currentY = -100000000;
		for(int i = 0; i < dimensions[game.getPlayerDimension()].mappedBuildings.size(); i++)
		{
			MappedBuilding building = dimensions[game.getPlayerDimension()].mappedBuildings.get(i);
			if(currentY != building.y){
				for(int j = 0; j < game.renderQueue.size(); j++){
					GameObject object = (GameObject)game.renderQueue.get(j);
					if(object.getRectangle().y+object.getRectangle().height <= building.y*yOff)
					{
						object.render(game.getRenderer(), xZoom, yZoom);
						game.renderQueue.remove(object);
					}
				}
			}
			tileSet.renderBuilding(building.id, game.getRenderer(), building.x*xOff, building.y*yOff, xZoom, yZoom);
			if(Game.showCollisionBox){
				if(building.collisionBoxes != null)
				{
					for(int l = 0; l < building.collisionBoxes.length; l++)
					{
						Rectangle rect = building.collisionBoxes[l];
						rect.generateGraphics(2, Color.CYAN.getRGB());
						game.getRenderer().renderRectangle(rect, xZoom, yZoom, false);
					}
				}
			}
			currentY = building.y;
		}
		
		if(!game.renderQueue.isEmpty())
		{
			for(int j = 0; j < game.renderQueue.size(); j++)
				game.renderQueue.get(j).render(game.getRenderer(), xZoom, yZoom);
		}
	}
	
	public void setTile(int id, int tileX, int tileY)
	{
		boolean foundTile = false;
		for(int i = 0; i < dimensions[game.getPlayerDimension()].mappedTiles.size(); i++)
		{
			MappedTile tile = dimensions[game.getPlayerDimension()].mappedTiles.get(i);
			if(tile.x == tileX && tile.y == tileY)
			{
				tile.id = id;
				foundTile = true;
				break;
			}
		}
		if(!foundTile)
			dimensions[game.getPlayerDimension()].mappedTiles.add(new MappedTile(id, tileX, tileY));
	}
	
	public void setBuilding(int id, int buildX, int buildY)
	{
		boolean foundBuilding = false;
		for(int i = 0; i < dimensions[game.getPlayerDimension()].mappedBuildings.size(); i++)
		{
			MappedBuilding building = dimensions[game.getPlayerDimension()].mappedBuildings.get(i);
			if(building.x == buildX && building.y == buildY)
			{
				building.id = id;
				foundBuilding = true;
				building.setCollisionBoxes(this.tileSet.getBuildingList().get(id).getCollisionBoxes());
				break;
			}
		}
		if(!foundBuilding)
		{
			MappedBuilding building = new MappedBuilding(id, buildX, buildY);
			building.setCollisionBoxes(this.tileSet.getBuildingList().get(id).getCollisionBoxes());
			dimensions[game.getPlayerDimension()].mappedBuildings.add(building);
		}
		this.sortBuildingList(dimensions[game.getPlayerDimension()]);
	}
	
	public void removeTile(int tileX, int tileY)
	{
		for(int i = 0; i < dimensions[game.getPlayerDimension()].mappedTiles.size(); i++)
		{
			MappedTile tile = dimensions[game.getPlayerDimension()].mappedTiles.get(i);
			if(tile.x == tileX && tile.y == tileY)
			{
				dimensions[game.getPlayerDimension()].mappedTiles.remove(i);
				break;
			}
		}
	}
	
	public void removeBuilding(int buildX, int buildY)
	{
		for(int i = 0; i < dimensions[game.getPlayerDimension()].mappedBuildings.size(); i++)
		{
			MappedBuilding building = dimensions[game.getPlayerDimension()].mappedBuildings.get(i);
			if(building.x == buildX && building.y == buildY)
			{
				dimensions[game.getPlayerDimension()].mappedBuildings.remove(i);
				break;
			}
		}
	}
	
	public void saveMap()
	{		
		System.out.println("Saving File!");
		try 
		{
			int currentLine = 0;
			if(mapFile.exists()) 
				mapFile.delete();
			mapFile.createNewFile();
			PrintWriter printWriter = new PrintWriter(mapFile);
			for(int l = 0; l < dimensions.length; l++)
			{
				Dimension dimension = dimensions[l];
				printWriter.println("Dimension:"+dimension.dimensionName+":"+dimension.dimensionRect.width+":"+dimension.dimensionRect.height);
				if(comments.containsKey(currentLine))
				{
					printWriter.println(comments.get(currentLine));
					currentLine++;
				}
				if(dimension.fillTileID >= 0)
				{
					printWriter.println("Fill:"+dimension.fillTileID);
					currentLine++;
				}
				for(int i = 0; i < dimension.mappedTiles.size(); i++)
				{
					MappedTile tile = dimension.mappedTiles.get(i);
					printWriter.println(tile.id+","+tile.x+","+tile.y);
					currentLine++;
				}
				for(int i = 0; i < dimension.mappedBuildings.size(); i++)
				{
					MappedBuilding building = dimension.mappedBuildings.get(i);
					printWriter.println("OBJ"+","+building.id+","+building.x+","+building.y);
				}
			}
			printWriter.close();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	
	//TODO: Add comment function
	public boolean containsComment(int currentLine)
	{
		return comments.containsKey(currentLine);
	}
	
	//Tile ID in the tileSet and the position in the map
	class MappedTile
	{
		public int id, x, y;
		
		public MappedTile(int id, int x, int y)
		{
			this.id = id;
			this.x = x;
			this.y = y;
		}
	}
	
	class MappedBuilding
	{
		public int id, x, y;
		public Rectangle[] collisionBoxes;
		
		public MappedBuilding(int id, int x, int y)
		{
			this.id = id;
			this.x = x;
			this.y = y;
		}
		
		public void setCollisionBoxes(Rectangle[] rect)
		{
			if(rect != null){
				Rectangle[] rectangle = new Rectangle[rect.length];
				for(int i = 0; i < rect.length; i++)
				{
					rectangle[i] = new Rectangle(rect[i].x + x*Game.getXZoom()*16, rect[i].y + y*Game.getYZoom()*16, rect[i].width, rect[i].height);
				}
				this.collisionBoxes = rectangle;
			}
		}
	}
	
	class Dimension
	{
		
		private Rectangle dimensionRect;
		private String dimensionName;
		private ArrayList<MappedTile> mappedTiles;
		private ArrayList<MappedBuilding> mappedBuildings;
		public int fillTileID = -1;
		
		public Dimension(String name, Rectangle rect)
		{
			dimensionName = name;
			dimensionRect = rect;
			mappedTiles = new ArrayList<MappedTile>();
			mappedBuildings = new ArrayList<MappedBuilding>();
		}
		
		public Rectangle getAdjustedRect()
		{
			return new Rectangle(0, 0, dimensionRect.width*16*Game.getXZoom(), dimensionRect.height*16*Game.getYZoom());
		}
	}
}
