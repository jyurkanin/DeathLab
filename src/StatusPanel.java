import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;

import javax.swing.*;


public class StatusPanel extends JPanel{
	private static final long serialVersionUID = 1L;
	private Tile[][] tileMap;
	private String messages;
	private boolean refreshMessages;
	private int count;
	public StatusPanel(Dimension d){
		messages = "";
		count = 0;
		refreshMessages = false;
		setPreferredSize(new Dimension(256, d.height));
	}
	public void clear(){
		Tile temp = Tiles.getTile("FLOOR2", Color.BLACK);
		for(int x = 0; x < tileMap.length; x++){
			for(int y = 0; y < tileMap[0].length; y++){
				tileMap[x][y] = temp;
			}
		}
	}
	public void paintComponent(Graphics g){
		Graphics2D graphics = (Graphics2D) g;
		displayString(0, 20, messages);
		refreshMessages = false;
		for(int y = 0; y < tileMap[0].length; y++){
			for(int x = 0; x < tileMap.length; x++){
				graphics.drawImage(tileMap[x][y].getImage(), 16*x, 16*y, null);
			}
		}
		count++;
		if((count = count % 500) == 0){
			clear();
			messages = "";
		}
	}
	public void addTile(int x, int y, Tile t)
	{
		tileMap[x][y] = t;
	}
	public int tileMapLength()
	{
	return tileMap.length;	
	}
	public int tileMapHeight()
	{
		return tileMap[0].length;
	}
	public boolean hasMessage(){
		return messages.length() > 0;
	}
	public void addMessage(String add){
		if(!refreshMessages){
			clear();
			messages = "";
		}
		refreshMessages = true;
		String[] words;
		words = add.split(" ");
		String line = "";
		int lines = messages.split("\n").length;
		int i;
		for(i = 0; i < words.length && lines < tileMap[0].length-2;){
			if((line+words[i]+" ").length() > tileMap.length-2){
				lines++;
				messages += (line + "\n");
				line = "";
			}else{
				line += words[i] + " ";
				i++;
			}
		}
		if(!line.equals(""))
				messages += (line + "\n");
	}
	public void displayString(int x, int y, String string){
		String tempName;
		String[] lines = string.split("\n");
		for(int i = 0; i < lines.length && (i+y < tileMap[0].length); i++){
			for(int j = 0; j < lines[i].length() && j+x < tileMap.length-1; j++){
				tempName = lines[i].substring(j, j+1);
				if(tempName.equals(" ")) 
					tempName = "BLANK2";
				try{tileMap[j+x][i+y] = Tiles.getTile(tempName, Color.BLACK);}
				catch(Exception e){System.out.println(tileMap.length + " " + (j+x) + "\t" + tileMap[0].length + " " + (i+y)); new Exception().printStackTrace();}
			}
		}
	}
	public void initialize(){
		tileMap = new Tile[1+(getSize().width/16)][1+(getSize().height/16)];
		clear();
	}
}
