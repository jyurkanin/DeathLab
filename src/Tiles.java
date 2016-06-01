import java.awt.*;
import java.awt.image.*;
import javax.imageio.ImageIO;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;


public class Tiles {  //static resource. Not really an object type thing. Doesnt need an instance.
	private static BufferedImage TileSetImage;  //16 x 16     I think.
	static BufferedImage tiles[];
	static final String PATH = "";
	public static final int BACKGROUND_COLOR;
	public static final ArrayList<String> tileNames = new ArrayList<String>();
	public static final String[] tileNamesArray = {
		"DUST", "CIVILIAN", "SOLDIER", "HEART", "DIAMOND", "CLOVER", "SPADE", "DOT", "INVERTED_DOT", "CIRCLE", "INVERTED_CIRCLE",
		"MALE", "FEMALE", "NOTE", "ARMOR_STAND", "NINJA_STAR", "LEFT_SLOPE", "RIGHT_SLOPE", "UP_DOWN_ARROW", "DERP-1", "JAR", 
		"DERP1", "DERP2", "DERP3", "UP_ARROW", "DOWN_ARROW", "LEFT_ARROW", "RIGHT_ARROW", "DERP4", "LEFT_RIGHT_ARROW", "UP_SLOPE", "DOWN_SLOPE", 
		"BLANK1", "!", "PLANT", "GRATE", "SACK", "ROCK1", "GOBLIN", "ROCK2", "(", ")", "*", "FLOOR1", "ROCK3", "90_ARROW_HEAD", 
		"ROCK4",  "45_ARROW_HEAD", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", ":", ";", "UP_STAIRS", "FLOOR2", "DOWN_STAIRS", 
		"?", "KNIGHT", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "FLOOR3",
		"Y", "Z", "ARMOR1", "135_ARROW_HEAD", "ARMOR2", "ROCK5", "DIGGING", "ROCK6", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", 
		"m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "{", "0_ARROW_HEAD", "}", "DERP6", "DERP7", "GEARS", "ii", "é", "â",
		"ä", "à", "&", "SKULL", "ê", "ë", "è", "ï", "CART", "ì", "Ä", "THING", "É", "TOY", "DERP8", "ô", "ö", "OFF_SWITCH", "DERP9", "ù", "ÿ", "Ö", "Ü", 
		"PRESSURE_PLATE", "WEB", "CRAB", "DERP10", "FORTE", "á", "í", "ON_SWITCH", "ú", "ñ", "Ñ", "DERP11", "DERP12", "GUITAR", "DERP13", "DERP14",
		"SHINY1", "SHINY2", "POTION", "<", ">", "CARVED_FLOOR_1", "CARVED_FLOOR_2", "CARVED_FLOOR_3", "NORTH_SOUTH_1", "NORTH_SOUTH_WEST_1", "DERP15",
		"NORTH_SOUTH_WEST_2", "SOUTH_WEST_1", "SOUTH_WEST_2", "NORTH_SOUTH_WEST_3", "NORTH_SOUTH_2", "SOUTH_WEST_3", "NORTH_WEST_1", "NORTH_WEST_2", "NORTH_WEST_3",
		"SOUTH_WEST_4", "NORTH_EAST_1", "NORTH_EAST_WEST_1", "EAST_SOUTH_WEST_1", "NORTH_EAST_SOUTH_1", "EAST_WEST_1", "DOOR1", "DERP17", "NORTH_EAST_SOUTH_2", "NORTH_EAST_2", 
		"EAST_SOUTH_1", "NORTH_EAST_WEST_2", "EAST_SOUTH_WEST_2", "NORTH_EAST_SOUTH_2", "EAST_WEST_2", "NORTH_EAST_SOUTH_WEST_1", "NORTH_EAST_SOUTH_3", "DERP18", "TABLE", "DERP19",
		"NORTH_EAST_3", "NORTH_EAST_4", "EAST_SOUTH_2", "EAST_SOUTH_3", "DERP20", "DERP21", "NORTH_WEST_4", "EAST_SOUTH_4", "FULL_TILE", "BOTTOM_HALF_TILE", 
		"LEFT_HALF_TILE", "RIGHT_HALF_TILE", "TOP_HALF_TILE", "DERP22", "DERP23", "DERP25", "CABINET", "SERRATED_DISK", "DERP26", "DERP27", "UNRIPE_PLANT", "RIPE_PLANT",
		"BED", "UNDEAD", "DERP28", "BOULDER", "DERP29", "CROSS_BOW", "DERP30", "LOGS", "CARVED_FLOOR_4", "DERP31", "DERP32", "DERP33", "DERP34", "BARREL", "DERP35",
		"DERP36", "DERP37", ".", "WEAPON_RACK", "DERP39", "CORPSE", "DERP40", "BLANK2"};
	public static final String[] FOREGROUND_TILES = {
		"CIVILIAN", "SOLDIER", "KNIGHT", "GOBLIN", "UNDEAD"
	};
	
	static{
		
		for(String name : tileNamesArray)
			tileNames.add(name);
		
		try{
			/*
			 * Okay. This is blowing my fucking mind how could this possibly work
			 */
			InputStream configStream = Tiles.class.getResourceAsStream("tileset.png");  
			System.out.println(configStream == null);
			TileSetImage = ImageIO.read(configStream);
			
			//TileSetImage = ImageIO.read(new File(PATH + "tileset.png"));
			tiles = new BufferedImage[256];
			for(int x = 0; x < 16; x++){
				for(int y = 0; y < 16; y++){
					tiles[(x*16) + y] = TileSetImage.getSubimage(16*y, 16*x, 16, 16);
				}
			}
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			BACKGROUND_COLOR = tiles[0].getRGB(1, 1);
		}
	}
	
	/*
	 * @return an image representing a tile.
	 */
	public static Tile getTile(String tileName, Color color){ //this has to replace the background manually which could be super slow.
		int index = tileNames.indexOf(tileName);
		if(index == -1){
			System.err.println("The TileName has not been found: " + tileName);
			Thread.dumpStack();
			System.exit(1);
		}
		
		BufferedImage tile = tiles[index];
		for(int x = 0; x < tile.getWidth(); x++){
			for(int y = 0; y <tile.getHeight(); y++){
				if(isForegroundTile(tileName) && (tile.getRGB(x, y) == Color.WHITE.getRGB())){
					tile.setRGB(x, y, color.getRGB());
				}
				else if(!isForegroundTile(tileName) && (tile.getRGB(x, y) == BACKGROUND_COLOR)){
					tile.setRGB(x,  y, color.getRGB());					
				}
			}
		}
		return new Tile(tile, tileName);
	}
	
	public static boolean isForegroundTile(String tileName){
		for(String name : FOREGROUND_TILES)
			if(tileName.equals(name))
				return true;
		return false;
	}
}
