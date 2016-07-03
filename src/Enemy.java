import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;

public class Enemy extends Entity{
	ArrayList<Tile> los;
	private Direction direction;
	private int count;
	private int countMult;
	private int wait;
	final static int InitialSightDistance = 4; //cyborg zombies are near sighted. this is the minimum search distance for the path algorithm.
	int sightDistance;
	int AILevel;
	int fireCounter;
	boolean wasVisible;
	/* AILevel = the difficulty of the AI starting at 1.
	 * with each level increase they move faster.
	 * 
	 * at level 3 they begin pathing at a search distance that increases by one every level starting at a distance of 4.
	 * 
	 * at level 10 they will begin dodging. 
	 * |-Which means that when they are adjacent to the player, 
	 * |-there is a probability that they will dodge right or left.
	 * |-this probability increases with each level after 10 and maxes at 50.
	 *
	 * at level 15 they will have a 33% chance to spawn with really heavy armor and a long sword but slower movement.
	 *
	 * The Enemy AI will have three states.
	 * 1. Random Walk. It will move around the place. with a tendency to move in a general direction that is chosen at random.
	 * 2. Seek and Destroy. The player has become visible and the Enemy uses pathfinding to travel to it.
	 * 3. Attack. Enemy will attack. Attacking will be described later when I figure out how it might work. 
	 */
	public Enemy(GameEngine e, Tile t){
		super(e, t, false);
		los = new ArrayList<Tile>();
		count = e.random.nextInt(1000); //this is so that all the enemeis dont just move a that the same time.
		countMult = 1;
		wait = 0;
		AILevel = 5+(e.getPlayer().score / 10);
		sightDistance = InitialSightDistance;
		wasVisible = false;
		fireCounter = 0;
		
		if(AILevel >= 15 && engine.random.nextInt(100) < 33){
			generateInventory();
			getTile().switchImage(Tiles.getTile("KNIGHT", Color.RED));
			countMult = 10;
		}
	}
	/*
	 * This describes the conditions required to enter different states and stuff.
	 */
	public void updateEnemy(){
		wait++;
		if((1000*countMult)/((5*AILevel*AILevel)) < wait)
			wait = 0;
		else return;
		
		sightDistance = InitialSightDistance + AILevel-3;
		updateLineOfSight();
		Direction decision = null; 
		Point temp;
		boolean tracking = false;
		//I dont think their is a case in which decision stays null because at least one of the state conditions will always be true.
		//int state; //state is indeterminate at this point.
		if(!isPlayerVisible()){// state = 1;
			decision = randomWalk();
			wasVisible = false;
		}   //at level 15 they can use path finding to locate player even if they cant see the player. Xray vision.
		else if((isPlayerVisible() || AILevel >= 15) && !isPlayerInRange() && AILevel >= 3){//state = 2;
			if(!wasVisible){
				engine.statusPanel.addMessage(Responses.getPlayerDetectedMsg());
				wasVisible = true;
			}
			decision = trackEnemy();
			tracking = true;
		}
		else if(isPlayerVisible() &&  isPlayerInRange()){//state = 3;
			if(AILevel >= 10 && ((AILevel-5)*5 >= 50? engine.random.nextInt(100) < 50: engine.random.nextInt(100) < ((AILevel-5)*5)))
				decision = dodge();
			else{
				decision = Direction.NONE;
				attack(engine.getPlayer());
			}
		}
		
		
		if(isPlayerInLine() && fireCounter == (10/AILevel) && tracking){
			temp = (Point) point.clone();
			if(decision == Direction.NORTH)      temp.y--;
			else if(decision == Direction.EAST)  temp.x++;
			else if(decision == Direction.SOUTH) temp.y++;
			else if(decision == Direction.WEST)  temp.x--;
			if(!engine.hasBolt(temp)) fireBolt(decision);
			fireCounter = 0;
			System.out.println("Enemy fire");
		}
		else if(engine.canEntityTravel(getPosition(), decision)){
			move(decision);
		}
		fireCounter++;
	}
	/*
	 * this moves perpendicular to the target.
	 * preconditions: we are adjacent to the player.
	 */
	private Direction dodge(){
		Player player = engine.getPlayer();
		Point point = (Point) player.getPosition().clone();
		point.translate(-getPosition().x, -getPosition().y);
		Direction deciscion = null;  //probably not spelled right.
		if(point.x != 0) //we are aligned horizontally with target
			if(engine.random.nextInt(2) == 0)
				deciscion = Direction.NORTH;
			else
				deciscion = Direction.SOUTH;
		if(point.y != 0)//aligned vertically with target
			if(engine.random.nextInt(2) == 0)
				deciscion = Direction.EAST;
			else 
				deciscion = Direction.WEST;
		
		return deciscion; //this should not return null. Should always return at least something.
	}
	public Direction trackEnemy(){
		//Response.getPlayerDetectedMessage();
		return engine.getDirectionsToTarget(getPosition(), engine.getPlayer().getPosition()).get(0);
	}
	public Direction randomWalk(){
		Direction decision;
		int temp;
		count++;
		if(count == 50){
			count = 0;
			temp = engine.random.nextInt(4);
			if(temp == 0) direction = Direction.NORTH;
			else if(temp == 1) direction = Direction.EAST;
			else if(temp == 2) direction = Direction.SOUTH;
			else if(temp == 3) direction = Direction.WEST;
		}
		temp = engine.random.nextInt(20);
		if(temp >= 14) decision = Direction.NORTH;
		else if(temp >= 12) decision = Direction.EAST;
		else if(temp >= 10) decision = Direction.SOUTH;
		else if(temp >= 8) decision = Direction.WEST;
		else decision = direction;
		return decision;
	}
	public void move(Direction d){
		if(d == Direction.NORTH)
			point.y--;
		else if(d == Direction.EAST)
			point.x++;
		else if(d == Direction.SOUTH)
			point.y++;
		else if(d == Direction.WEST)
			point.x--;
	}
	public void updateLineOfSight(){
		 los = engine.getLineOfSight(this, sightDistance);
	}
	/*
	 * if the player is in the line of sight then good.
	 */
	private boolean isPlayerVisible(){ //this just checks the current tiles in the line of sight for a player.
		Player player = engine.getPlayer();
		boolean output = false;
		for(Tile t: los)
			output |= player.getPosition().equals(t.getPosition());
		return output;
	}
	/*
	 * Is player in range to attack. So is player adjacent to this enemy. Adjacent does not mean diagnol.
	 */
	private boolean isPlayerInRange(){
		Point playerPoint = engine.getPlayer().getPosition();
		point = getPosition();
		return new Point(point.x-1, point.y).equals(playerPoint) ||
				  new Point(point.x+1, point.y).equals(playerPoint) ||
				  new Point(point.x, point.y-1).equals(playerPoint) ||
				  new Point(point.x, point.y+1).equals(playerPoint);
	}
	/*
	 * can the player be hit by an arrow?
	 */
	private boolean isPlayerInLine(){
		return engine.getPlayer().getPosition().x == point.x || engine.getPlayer().getPosition().y == point.y;
	}
	public void generateInventory(){
		equipArmor(new Armor("SUIT"));
		equipMeleeWeapon( new MeleeWeapon("LONGSWORD"));
		equipRangedWeapon( new RangedWeapon("CROSSBOW"));
		
		inventory.add(getEquippedArmor());
		inventory.add(getEquippedMeleeWeapon());
	}
}
