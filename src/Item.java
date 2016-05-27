import java.awt.Point;


public class Item {
	String name;
	private Tile tile;
	private Point point;
	private Tile tileBehindThis;
	boolean isEdible;
	boolean isPotion;
	public Item(Tile t){
		tile = t;
	}
	public boolean isEdible(){
		return isEdible;
	}
	public boolean isPotion(){
		return isPotion;
	}
	public Tile getTile(){
		return tile;
	}
	public Tile getTileBehindThis(){
		return tileBehindThis;
	}
	public void setTileBehindThis(Tile t){
		tileBehindThis = t;
	}
	public void setPoint(Point p){
		point = p;
	}
	public Point getPoint(){
		return point;
	}
}
