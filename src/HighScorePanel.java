import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.*;

import javax.swing.JPanel;


public class HighScorePanel extends JPanel {
	public static final String URI = "http://98.215.7.147";
	private static final long serialVersionUID = -2919691051370747700L;
	private Tile[][] tileMap;
	private int mapHeight;
	private int mapWidth;
	public HighScorePanel(Dimension d)
	{
		setPreferredSize(new Dimension(d.width , d.height));
		mapHeight = d.height/16;
		mapWidth =  d.width/16;
		tileMap = new Tile[mapWidth][mapHeight];
		randomizeTileMap();
	}
	//paints the tileMap
	public void paintComponent(Graphics g){
		Graphics2D graphics = (Graphics2D) g;
		for(int y = 0; y < tileMap[0].length; y++){
			for(int x = 0; x < tileMap.length; x++){
				graphics.drawImage(tileMap[x][y].getImage(), 16*x, 16*y, null);
			}
		}
	}
	//puts a string in the tileMap
	//@param x x coord
	//@param y y coord 
	//@param string printed string
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
			}
		}
			}
	//@return sorted highscores
	private ArrayList<HighScore> getHighScores() 
	{
		try {
			//URLConnection connection = new URL(URI + "/DeathLab/highscores.txt").openConnection();
			//BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			Scanner scan = new Scanner(new InputStreamReader(new FileInputStream("highscores.txt")));
			ArrayList<HighScore> highscores = new ArrayList<HighScore>();
			while(scan.hasNext()){
				System.out.println("reading");
				String name = scan.next();
				if(scan.hasNext()){
					int score = Integer.parseInt(scan.next());
					HighScore temp = new HighScore(name, score);
					highscores.add(temp);
				}
			}
			//selection sort ftw
			for(int i=0; i < highscores.size()-1; i++){
				int minindex = i;
				HighScore temp;
			
				for(int c = i; c<highscores.size()-1; c++){
					if(highscores.get(c).getScore() > highscores.get(minindex).getScore()){
						minindex = c;
					}
				}
				if(minindex!=i){
					temp = highscores.get(i);
					highscores.set(i,highscores.get(minindex));
					highscores.set(minindex, temp);
				}
			}
			while(highscores.size()>11)
				highscores.remove(11);
			scan.close();
			return highscores;
	
		} catch (IOException e) {
			e.printStackTrace();
			try {PrintWriter printWriter = new PrintWriter("highscores.txt"); printWriter.close();}
			catch (FileNotFoundException e1) {e1.printStackTrace();}
			return null;
		}
		
		
}
	//fills the tile map with highscores
	public void displayHighScores()
	{
		int x = tileMap.length/2;
		int y = 5;
		displayString(tileMap.length/2-2,0 , "High Scores:");
		
		ArrayList<HighScore> highscores = getHighScores();
		if(highscores.size()>10)
		highscores.remove(highscores.size()-1);
		for(HighScore h : highscores)
		{
			displayString(x,y,(h.getName()+" :"+h.getScore()));
			y++;
		}
	}
	//places tile in the tilemap
	public void setTile(int x, int y, Tile tile)
	{
		tileMap[x][y] = tile;
	}
	//makes a random background for the tiles
	public void randomizeTileMap()
	{
		Random random = new Random();
		final String[] LIST = {"CORPSE", "KNIGHT", "UNDEAD", "GOBLIN", "SKULL"};
		int c;
		for(int x = 0; x < tileMap.length; x++)
		{
			for(int y = 0; y < tileMap[0].length ; y++){
				c = random.nextInt(4);
					setTile(x,y,Tiles.getTile(LIST[c], Color.RED));
			}
		
		}
		}
	//finds where the highscore goes
	public int findSpot(int score)
	{
		ArrayList<HighScore> scores = getHighScores();
		for(int i = 0; i < scores.size(); i++)
			if(score > scores.get(i).getScore())
				return i;
		return -1;
	}
	//adds highscore
	public void addHighScore(HighScore x)
	{
		try{
			ArrayList<HighScore> temp = getHighScores();
			PrintWriter print = new PrintWriter("highscores.txt");
			for(HighScore h : temp){
				print.println(h.getName());
				print.println(Integer.toString(h.getScore()));
			}
			print.println(x.getName());
			print.println(Integer.toString((x.getScore())));
			print.close();
		}catch (FileNotFoundException f){}
		
	}
	public Dimension getDimension()
	{
		return new Dimension(mapHeight, mapWidth);
	}

}


