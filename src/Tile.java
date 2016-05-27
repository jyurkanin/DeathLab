import java.awt.*;
import java.awt.image.*;
import java.util.ArrayList;
import java.util.HashSet;


public class Tile {
	private BufferedImage image;
	private int x, y;   //position by tile index. Imagine the display as a 2d array of these tiles and x, y only specify the index.
	private boolean isStationary;
	private boolean allowTravel;
	private String tileName;
	private HashSet<String> directions;
	private boolean isDirectional;
	private boolean isCurrentlyVisible;
	private boolean hasBeenSeen;
	
	public Tile(BufferedImage i, String n){
		image = i;
		isStationary = true;
		allowTravel = false;
		isCurrentlyVisible = false;
		hasBeenSeen = false;
		tileName = n;
		
		directions = new HashSet<String>();
		if(tileName.contains("NORTH")) directions.add("NORTH");
		if(tileName.contains("EAST"))  directions.add("EAST");
		if(tileName.contains("SOUTH")) directions.add("SOUTH");
		if(tileName.contains("WEST"))  directions.add("WEST");
		
		isDirectional = directions.size() > 0;
	}
	public void switchImage(Tile t){
		image = t.getImage();
	}
	public String getTileName(){
		return tileName;
	}
	
	public boolean getIsCurrentlyVisible(){
		return isCurrentlyVisible;
	}
	
	public void setIsCurrentlyVisible(boolean temp){
		isCurrentlyVisible = temp;
	}
	
	public boolean getHasBeenSeen(){
		return hasBeenSeen;
	}
	
	public void setHasBeenSeen(boolean b){
		hasBeenSeen = b;
	}
	
	public void setPosition(int x, int y){
		this.x = x;
		this.y = y;
	}
	public Point getPosition(){
		return new Point(x, y);
	}
	
	public boolean equals(Tile t){
		return t.getImage() == getImage(); //since the images are all from the same reference.
	}
	
	public void translate(int xo, int yo){
		if(!isStationary){
			x += xo;
			y += yo;
		}
	}
	
	public BufferedImage getImage(){
		return image;
	}
	
	public void setStationary(boolean s){
		isStationary = s;
	}
	
	public void allowTravel(boolean s){
		allowTravel = s;
	}
	public boolean isDirectional(){
		return isDirectional;
	}
	public void setDirectional(Boolean b){
		isDirectional = b;
	}
	public void allowTravelFromAllDirections(boolean s){
		directions.add("NORTH");
		directions.add("EAST");
		directions.add("SOUTH");
		directions.add("WEST");
		allowTravel = s;
	}
	public void addDirections(String directionString){
		if(directionString.contains("NORTH")) directions.add("NORTH");
		if(directionString.contains("EAST"))  directions.add("EAST");
		if(directionString.contains("SOUTH")) directions.add("SOUTH");
		if(directionString.contains("WEST"))  directions.add("WEST");
	}
	public boolean getAllowTravel(){
		return allowTravel;
	}
	public HashSet<String> getDirections(){
		return directions;
	}
	public boolean hasDirection(Direction d){
		String directionString = "";
		if(d == Direction.NORTH) directionString = "NORTH";
		else if(d == Direction.EAST) directionString = "EAST";
		else if(d == Direction.SOUTH) directionString = "SOUTH";
		else if(d == Direction.WEST) directionString = "WEST";
		return directions.contains(directionString);
	}
	
	public Tile copy(){
		Tile t = new Tile(image, tileName);
		t.setPosition(x, y);
		t.setStationary(isStationary);
		t.allowTravel(allowTravel);
		return t;
	}
}
