import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.*;

public class GamePanel extends JPanel{
	private static final long serialVersionUID = 5309705996895440041L;
	Tile[][] tileMap; //this holds the tiles of the current game.
	private Tile[][] oldTileMap; //this holds the tiles the way they looked the last time they were seen by the player.
	private ArrayList<Entity> entities; //these get painted last.
	private ArrayList<Tile> currentlyVisibleTiles;
	int mapHeight;  //these measure tileMap. so 1/16th of pixels.
	int mapWidth;
	Player player;
	
	public GamePanel(Player p, Dimension d, Dimension dt){
		player = p;
		setPreferredSize(dt);
		mapHeight = d.height/16;
		mapWidth =  d.width/16;
		tileMap = new Tile[mapWidth][mapHeight];
		oldTileMap = new Tile[mapWidth][mapHeight];
		entities = new ArrayList<Entity>();
		currentlyVisibleTiles = new ArrayList<Tile>();
		clear();
	}
	public void displayString(int x, int y, String string){
		String tempName;
		string = string.replace("0", "o");
		String[] lines = string.split("\n");
		//System.out.println("chirp: " +lines.length);
		for(int i = 0; i < lines.length; i++){
			for(int j = 0; j < lines[i].length(); j++){
				tempName = lines[i].substring(j, j+1);
				if(tempName.equals(" ")) tempName = "BLANK2";
				tileMap[j+x][i+y] = Tiles.getTile(tempName, Color.BLACK);
				currentlyVisibleTiles.add(tileMap[j+x][i+y]);
			}
		}
	}
	public void clear(){
		for(int x = 0; x < mapWidth; x++){
			for(int y = 0; y < mapHeight; y++){
				oldTileMap[x][y] = tileMap[x][y] = Tiles.getTile("BLANK1", Color.BLACK);
			}
		}
	}
	private boolean isPointInBounds(Point test){
		return (test.x > 0) && (test.x < getSize().width) &&
			   (test.y > 0) && (test.y < getSize().height);
	}
	/*
	 * This method will only paint the part of the tileMap that will fit on the panel.
	 */
	public void paintComponent(Graphics g){
		Graphics2D graphics = (Graphics2D) g;
		Tile blank = Tiles.getTile("BLANK1", Color.BLACK);
		int height = getSize().height/16;
		int width  = getSize().width/16;
				
		int tileMapOffsetX = player.getPosition().x;
		int tileMapOffsetY = player.getPosition().y;
		
		if(tileMapOffsetX < (width/2)) tileMapOffsetX = width/2;
		else if(tileMapOffsetX > mapWidth-(width/2)-2) tileMapOffsetX = mapWidth-(width/2)-2;
		
		if(tileMapOffsetY < (height/2)) tileMapOffsetY = height/2;
		else if(tileMapOffsetY > mapHeight-(height/2)-2) tileMapOffsetY = mapHeight-(height/2)-2;
		
		for(int x = tileMapOffsetX-(width/2); x < (tileMapOffsetX+(width/2))+2; x++){
			for(int y = tileMapOffsetY-(height/2); y < (tileMapOffsetY+(height/2))+2; y++){
				if(currentlyVisibleTiles.contains(tileMap[x][y])){
					graphics.drawImage(tileMap[x][y].getImage(), null, 16*(x-tileMapOffsetX+(width/2)), 16*(y-tileMapOffsetY+(height/2)));
					oldTileMap[x][y] = tileMap[x][y];
				}
				else if(!tileMap[x][y].getHasBeenSeen())
					graphics.drawImage(blank.getImage(), null, 16*(x-tileMapOffsetX+(width/2)), 16*(y-tileMapOffsetY+(height/2)));
				else if(tileMap[x][y].getHasBeenSeen())
					graphics.drawImage(oldTileMap[x][y].getImage(), null, 16*(x-tileMapOffsetX+(width/2)), 16*(y-tileMapOffsetY+(height/2)));
			}
		}
		
		BufferedImage temp; //temp is the superposition of the background and foreground tiles.
		Tile tile;
		Point point;
		for(Entity entity : entities){
			temp = new BufferedImage( 16, 16, BufferedImage.TYPE_4BYTE_ABGR);
			for(int x = 0; x < 16; x++){
				for(int y = 0; y < 16; y++){
					if(entity.getTile().getImage().getRGB(x, y) == Tiles.BACKGROUND_COLOR)
						temp.setRGB(x, y, tileMap[entity.getPosition().x][entity.getPosition().y].getImage().getRGB(x, y));
					else
						temp.setRGB(x, y, entity.getTile().getImage().getRGB(x, y));
				}
			}
			point = (Point) entity.getPosition().clone(); //these meddling object references are going to kill me.
			tile = tileMap[point.x][point.y];
			point.translate((width/2) - tileMapOffsetX, (height/2) - tileMapOffsetY);  //this will map the point to a point that fits on the panel.
			if((entity.IS_USER || tile.getHasBeenSeen()) && isPointInBounds(point))
				graphics.drawImage(temp, null, 16*point.x, 16*point.y);
		}
		currentlyVisibleTiles.clear();
	}
	public void setCurrentlyVisibleTiles(ArrayList<Tile> los){
		currentlyVisibleTiles.addAll(los);
	}
	public void addCurrentlyVisibleTile(Tile tile){
		currentlyVisibleTiles.add(tile);
	}
	
	public void addEntity(Entity e){
		entities.add(e);
	}
	public void removeEntity(Entity e){
		entities.remove(e);
	}
	public void clearEnemies(){
		ArrayList<Entity> toRemove = new ArrayList<Entity>(); //this avoids concurrent modification exception
		for(Entity e : entities)
			if(!e.IS_USER) toRemove.add(e);
		entities.removeAll(toRemove);
	}
	public void insertTile(Tile t, int x, int y){
		tileMap[x][y] = t;
	}
	
	public Tile[][] getTileMap(){
		return tileMap;
	}
}
