import java.awt.Color;


public class Bolt extends Item{
	Direction direction;
	int damage;
	public Bolt(Direction d, int dam){
		/* I really wish I didn't have to have super be the first line.*/
		super(Tiles.getTile(   (d == Direction.WEST || d == Direction.EAST) ? "90_ARROW_HEAD" : "0_ARROW_HEAD",    Color.BLACK));
		direction = d;
		damage = dam;
	}
	public void move(){
		if(direction == Direction.NORTH) getPoint().y--;
		else if(direction == Direction.SOUTH) getPoint().y++;
		else if(direction == Direction.WEST) getPoint().x--; 
		else if(direction == Direction.EAST) getPoint().x++;
	}
}
