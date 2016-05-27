import java.awt.*;
import java.util.ArrayList;

import javax.swing.*;	

class ButtonTile{
	Point point;
	String msg;
	public ButtonTile(String s){
		msg = s;
	}
	public void set(Point p){
		point = p;
	}
	public void execute(){
		
	}
}

public class GameMenu extends JPanel{
	private static final long serialVersionUID = 11L;
	private int highlightedOption;
	private ArrayList<ButtonTile> options;
	private int width, height;
	private Tile[][] tileMap;
	public GameMenu(Dimension d, Dimension dt){//Dimension d){
		width = d.width/16;
		height = d.height/16;
		setSize(dt); //got to figure out a way to set to default setting.
		tileMap = new Tile[width][height];
		options = new ArrayList<ButtonTile>();
		highlightedOption = 0;
		for(int x = 0; x < width; x++){
			for(int y = 0; y < height; y++){
				tileMap[x][y] = Tiles.getTile("BLANK1", Color.BLACK);
			}
		}
		displayString(8, 1, "Procedural Death Labyrinth");
	}
	public void displayString(int x, int y, String string){
		String tempName;
		string = string.replace("0", "o");
		String[] lines = string.split("\n");
		for(int i = 0; i < lines.length; i++){
			for(int j = 0; j < lines[i].length(); j++){
				tempName = lines[i].substring(j, j+1);
				if(tempName.equals(" ")) tempName = "BLANK2";
				tileMap[j+x][i+y] = Tiles.getTile(tempName, Color.BLACK);
			}
		}
	}
	private void displayString(ButtonTile bt){
		displayString(bt.point.x, bt.point.y, bt.msg);
	}
	public void paintComponent(Graphics g){
		Graphics2D graphics = (Graphics2D) g;
		Point temp;
		int tempi;
				
		for(int i = 0; i < options.size(); i++){
			displayString(options.get(i));
			temp = options.get(i).point;
			tempi = options.get(i).msg.length();
			System.out.println("highlightedoption, i " + highlightedOption + ", " + i);
			
			if(highlightedOption == i){
				System.out.println(highlightedOption);  
				tileMap[temp.x - 1][temp.y] = Tiles.getTile("LEFT_ARROW", Color.BLACK);
				tileMap[temp.x + tempi + 1][temp.y]= Tiles.getTile("RIGHT_ARROW", Color.BLACK); 
			}else{
				tileMap[temp.x - 1][temp.y] = Tiles.getTile("BLANK2", Color.BLACK);
				tileMap[temp.x + tempi + 1][temp.y]= Tiles.getTile("BLANK2", Color.BLACK); 
			}
		}
		
		
		for(int x = 0; x < width; x++)
			for(int y = 0; y < height; y++)
				graphics.drawImage(tileMap[x][y].getImage(), null, 16*x, 16*y);
		
	}
	public void addButtonTile(ButtonTile bt){
		Point point = new Point((getSize().width/32)-(bt.msg.length()/2), (getSize().height/32)+options.size());
		bt.set(point);
		options.add(bt);
	}
	public ButtonTile getSelectedButtonTile(){
		return options.get(highlightedOption);
	}
	public void moveSelector(int i){
		if((i == -1) && (highlightedOption-1 >= 0 )){
			highlightedOption--;
		}
		
		if((i ==  1) && (highlightedOption+1 < options.size())){
			highlightedOption++;
		}
	}
}
