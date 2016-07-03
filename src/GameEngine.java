import java.awt.*;
import java.io.*;
import java.util.*;

enum Direction{NORTH, EAST, SOUTH, WEST, NONE}
enum Rotation{CLOCK, COUNTER};


public class GameEngine {
	Tile[][] tileMap;
	int[][] tempMap; 
	ArrayList<Enemy> enemies;
	ArrayList<Item> items;
	ArrayList<Point> wallPoints;
	ArrayList<Point> walkingPoints;
	ArrayList<Bolt> bolts;
	int height, width;
	int frameCount;
	GameBoard gameBoard;
	GamePanel gamePanel;
	StatusPanel statusPanel;
	HighScorePanel highScorePanel;
	Random random;
	HashSet<String> directions;
	final HashSet<Direction> DIRECTIONS = new HashSet<Direction>();
	public volatile ArrayList<Character> eventBuffer;
	private KeyboardListener user;
	private Player player;
	private final Point startingPoint;
	public int level; //which floor we are on.
	public GameEngine(){
		startingPoint = new Point();
		//player = new Player(this, Tiles.getTile("CIVILIAN", Color.RED), true);
		eventBuffer = new ArrayList<Character>();
		user = new KeyboardListener(eventBuffer);
		player = new Player(this, Tiles.getTile("CIVILIAN", Color.RED));
		gameBoard = new GameBoard(player);
		gamePanel = gameBoard.getPanel();
		statusPanel = gameBoard.getStatusPanel();
		//Response.setPanel(statusPanel);
		tileMap = gamePanel.getTileMap();
		wallPoints = new ArrayList<Point>();
		walkingPoints = new ArrayList<Point>();
		bolts = new ArrayList<Bolt>();
		gamePanel.setBolts(bolts);
		items = new ArrayList<Item>();
		enemies = new ArrayList<Enemy>();
		level = 1;
		height = tileMap[0].length;
		width = tileMap.length;
		tempMap = new int[width][height];
		directions = new HashSet<String>();
		DIRECTIONS.add(Direction.NORTH);
		DIRECTIONS.add(Direction.EAST);
		DIRECTIONS.add(Direction.SOUTH);
		DIRECTIONS.add(Direction.WEST);
	}
	public void startGame(){
		gameBoard.addKeyListener(user);
		gameBoard.useMenuPanel();
		gameBoard.getGameMenu().addButtonTile(new ButtonTile("Play Game"){public void execute(){initializeGame();gameLoop();}});
		gameBoard.getGameMenu().addButtonTile(new ButtonTile("Highscores"){public void execute(){showHighScores();}});
		gameBoard.setVisible(true);
		
		char c;
		while(true){
			gameBoard.repaint();
			do{
				if(eventBuffer.size() > 0){
					c = eventBuffer.remove(0);
					if(c == 'w')
						gameBoard.getGameMenu().moveSelector(-1);
					else if(c == 's')
						gameBoard.getGameMenu().moveSelector(1);
					gameBoard.repaint();
				}
				else 
					c = ' ';
			}while(c != 'e');
			gameBoard.getGameMenu().getSelectedButtonTile().execute(); // Is this polymorphism? I dont really know.
			gameBoard.useMenuPanel();
			gamePanel.clear();
		}
		
		
		
	}
	public void initializeGame(){
		gameBoard.useGamePanel();
		generateTerrain(System.currentTimeMillis());
		searchForWalkingTiles();
		generateItems(3); //1-5. 5 being most rare
		generateEnemies(4); //same as items.
		//player = new Player(this, Tiles.getTile("CIVILIAN", Color.RED), true);
		player.setPosition(startingPoint);
		displayPlayerInformation(player);
		gamePanel.addEntity(player);
		gamePanel.setCurrentlyVisibleTiles(getLineOfSight(player, player.sightDistance));
		gameBoard.setVisible(true);
		frameCount = 0;
		try{Thread.sleep(1000);}
		catch(Exception e){}
	}
	public void nextLevel(){
		level++;
		player.incrementScore(Player.NEXT_FLOOR_BONUS);
		gameBoard.displayString(0, 0, "Level: " + level);
		gameBoard.repaint();
		initializeGame();
	}
	public void gameLoop(){
		System.out.println("entering gameloop");
		//displayAllTiles(); //this is for debugging.
		try{
			updateGame(' ');
			while(true){
				Thread.sleep(50);  //sleep for 10 milliseconds
				if(!eventBuffer.isEmpty())
					updateGame(eventBuffer.remove(0));
				else
					updateGame('0');
			}
		}
		catch(InterruptedException e){e.printStackTrace();}
		catch(PlayerDiedException e){gameOver(e);}
	}
	private void showHighScores(){
		highScorePanel= gameBoard.getHighScorePanel();
		gameBoard.useHighScorePanel();
		highScorePanel.displayHighScores();
		gameBoard.repaint();
		while(eventBuffer.isEmpty() || !(eventBuffer.remove(0) == 'e')){
			System.out.println("DERP");
			try{Thread.sleep(10);}
			catch(Exception e){e.printStackTrace();}
		}
	}
	private Point[] getAdjacentPoints(Point point){
		Point[] adjacents = new Point[4];
		adjacents[0] = new Point(point.x+1, point.y);
		adjacents[1] = new Point(point.x-1, point.y);
		adjacents[2] = new Point(point.x, point.y+1);
		adjacents[3] = new Point(point.x, point.y-1);
		for(int i = 0; i < 4; i++)
			if(!isPointInBounds(adjacents[i]))
				adjacents[i] = null; //this shouldn't actually be possible to reach since this would require
		return adjacents;  			 //there to be a gap in the wall that is supposed to surround all the walking tiles.
	}
	/*
	 * This is a pathfinding algorithm that returns the list of steps
	 * to get to target. 
	 */
	public ArrayList<Direction> getDirectionsToTarget(Point start, Point end){
		for(int[] t : tempMap)
			Arrays.fill(t, -1);  //I dont create a new tempMap each time to save memory and speed. I think its faster this way maybe
		ArrayList<Direction> directions = new ArrayList<Direction>();
		ArrayList<Point> outside = new ArrayList<Point>();
		ArrayList<Point> temp = new ArrayList<Point>();
		Point[] t = new Point[4];
		int count = 0;
		boolean quit = false;
		int j = 0;
		
		tempMap[start.x][start.y] = count;
		outside.add(start);
		
		while(!quit){
			count++;
			temp = new ArrayList<Point>();
			for(int i = 0; i < outside.size() && !quit; i++){
				t = getAdjacentPoints(outside.get(i));
				for(j = 0; j < 4 && !quit; j++){
					if(t[j].equals(end)){
						tempMap[t[j].x][t[j].y] = count;
						quit = true;
					}
					else if(tempMap[t[j].x][t[j].y] == -1 && tileMap[t[j].x][t[j].y].getAllowTravel()){
						tempMap[t[j].x][t[j].y] = count;
						temp.add(t[j]);
					}
				}
			}
			quit |= count > 100; //this just prevents infinite loops from forming in strange circumstances. DOnt worry about it basically.
			//if(count > 10) System.out.println("I cant Even Imagine Why this would print"); //this would print if there is no possible path to target. or it takes more than 1000 steps.
			outside = temp;
		}
		
		//printMap(tempMap, start, 10);	//this is cool for debugging.
		Point point = t[j-1];
		for(count = tempMap[point.x][point.y]; count > 0; count--){
			t = getAdjacentPoints(point);
			quit = false;
			for(int k = 0; k < 4 && !quit; k++){
				if(tempMap[t[k].x][t[k].y] == count-1){  //if you get a null pointer here, it means that the cavern is not closed and is opened to the edge of the map.
					quit = true;
					if(k == 0) directions.add(0, Direction.WEST);
					else if(k == 1) directions.add(0, Direction.EAST);
					else if(k == 2) directions.add(0, Direction.NORTH);
					else if(k == 3) directions.add(0, Direction.SOUTH);
					point = t[k];
				}
			}
		}
		if(directions.size() == 0) directions.add(Direction.NONE); //this is a bandaid around a bullet wound.
		return directions;	
	}
	private void printMap(int[][] map, Point point, int radius){
 		for(int y = -radius; y < radius; y++){
 			for(int x = -radius; x < radius; x++){
				if(point.x+x > 0 && point.y+y > 0 && point.x+x < map.length && point.y+y < map[0].length)
					System.out.printf("%04d ", map[point.x+x][point.y+y]);
			}
			System.out.println();
		}
	}
	private void gameOver(PlayerDiedException e){
		highScorePanel = gameBoard.getHighScorePanel();
		int hsWidth = (int) highScorePanel.getDimension().getWidth();
		int hsHeight = (int) highScorePanel.getDimension().getHeight();
		gameBoard.useHighScorePanel();
		highScorePanel.displayString(hsWidth/2-5, 0, "Game Over");
		highScorePanel.displayString(hsWidth/2-5, 1, e.getMessage());
		gameBoard.pack();
		
/* too cool to delete
"  ooooooo8      o      oooo     oooo ooooooooooo        ooooooo  ooooo  oooo ooooooooooo oooooooooo\n" +
"o888    88     888      8888o   888   888    88       o888   888o 888    88   888    88   888    888\n" +
"888    oooo   8  88     88 888o8 88   888ooo8         888     888  888  88    888ooo8     888oooo88\n"  +
"888o    88   8oooo88    88  888  88   888    oo       888o   o888   88888     888    oo   888  88o\n" +
" 888ooo888 o88o  o888o o88o  8  o88o o888ooo8888        88ooo88      888     o888ooo8888 o888o  88o8"
*/
		gameBoard.repaint();
		
		eventBuffer.clear();
		while(eventBuffer.isEmpty() || !eventBuffer.get(eventBuffer.size()-1).equals('e'))
		    try {Thread.sleep(1);}
		catch (InterruptedException e1) {e1.printStackTrace();} //isEmpty is used to short-circuit this condition to prevent it from failing on .get(0)
		eventBuffer.clear();
		highScorePanel.randomizeTileMap();
		highScorePanel.displayHighScores();
		gameBoard.repaint();
		gameBoard.pack();
		gameBoard.repaint();
		String temp ="";
		eventBuffer.clear();
		int score = e.getScore();
		while(temp.length() != 3){
		    if(eventBuffer.size() > 0)
			temp += eventBuffer.remove(0);
		    
		    highScorePanel.displayString(hsWidth/2, 1, temp + ": " + score);
		    gameBoard.repaint();
		    gameBoard.pack();
		}
		HighScore x = new HighScore(temp, score);
		highScorePanel.addHighScore(x);
		
		
		statusPanel.clear();
		player.reset();
	}

	/*
	 * THis updates the player, enemies, and bolts fired. There is no multithreading and all the items and entities take their turns
	 */
    private void updateGame(char input) throws PlayerDiedException{  //user input
    	if(frameCount % 2 == 0) updateBolts();
    	player.updatePlayer(input);
    	displayPlayerInformation(player);
    	if(input != '0')
    		gamePanel.setCurrentlyVisibleTiles(getLineOfSight(player, player.sightDistance));
    	for(Enemy enemy : enemies)
    		enemy.updateEnemy();
    	gameBoard.repaint();
    	frameCount++;
	}
    private void updateBolts(){
    	Point to;
    	ArrayList<Bolt> boltsToRemove = new ArrayList<Bolt>();
    	ArrayList<Enemy> enemiesToRemove = new ArrayList<Enemy>();
    	for(Bolt b : bolts){
    		to = (Point) b.getPoint().clone();
    		if(b.direction == Direction.NORTH) to.y--;
    		else if(b.direction == Direction.EAST) to.x++;
    		else if(b.direction == Direction.SOUTH) to.y++;
    		else if(b.direction == Direction.WEST) to.x--;
    		
    		if(canEntityTravel(b.getPoint(), b.direction)){
    			b.move();
    		}
    		else if(isOccupied(to)){
    			for(Enemy e: enemies){
    				if(e.getPosition().equals(to)){
    					e.health -= b.damage;
    					if(e.health <= 0){
    						enemiesToRemove.add(e);
    						statusPanel.addMessage("Enemy: " + Responses.getEnemyKilledMsg());
    						player.incrementScore(Player.ENEMY_KILLED_BONUS);
    					}
    					boltsToRemove.add(b);
    				}
    			}
    			if(player.point.equals(to)){
    				boltsToRemove.add(b);
    				player.health -= b.damage;
    			}
    		}
    		else{
    			boltsToRemove.add(b);
    		}
    	}
    	bolts.removeAll(boltsToRemove); //this avoids one of those weird errors.
    	for(Enemy e : enemiesToRemove){
    		enemies.remove(e);
			gamePanel.removeEntity(e);
    	}
    }
    public void addBolt(Bolt b){
    	bolts.add(b);
    }
	private void displayPlayerInformation(Player player){
		String output = "HP: ";
		String health = String.valueOf(player.health);
		if(health.length() == 2)
			health = "0" + health;
		else if(health.length() == 1)
			health = "00" + health;
		output += health + "\n";
		
		output += "HUNGER: ";
		String hunger= String.valueOf(player.hunger);
		if(hunger.length() == 2)
			hunger = "0" + hunger;
		else if(hunger.length() == 1)
			hunger = "00" + hunger;
		output += hunger + "\n";
		
		output += "Score: " + player.score;
		statusPanel.displayString(0, 0, output);
		
		statusPanel.displayString(0, 4, "Inventory");
		int x = 0;
		int y = 5;
		for(Item i: player.inventory){
			if(y< statusPanel.tileMapHeight()-20){
				if(x> statusPanel.tileMapLength()-1){
					x=0;
					y++;
				}
				statusPanel.addTile(x ,y , i.getTile());
				x++;
			}
		}
	}
	private void displayAllTiles(){
		ArrayList<Tile> t = new ArrayList<Tile>();
		for(int x = 0; x < width; x++){
			for(int y = 0; y < height; y++){
				t.add(tileMap[x][y]);
				tileMap[x][y].setIsCurrentlyVisible(true);
				tileMap[x][y].setHasBeenSeen(true);
			}
		}
		gamePanel.setCurrentlyVisibleTiles(t);
	}
	/*
	 * This works by starting from a point and venturing out along a path
	 * that rotates around the start. pretty simple.
	 * Actually: It has to scan over each quadrant separately and both forwards and backwards.
	 */
	public ArrayList<Tile> getLineOfSight(Player e, int s){
		final int sightDistance = s;
		final int divisions = 1*sightDistance; //dear lord does this slow shit down
		
		ArrayList<Tile> los = new ArrayList<Tile>();
		Point ep =  e.getPosition();

		double x = ep.x;
		double y = ep.y;
		double z = 0;
		double angle = 0; //this points straight down. since positive y is down.
		double startAngle;
		
		boolean blocked;
		Point temp;
		
		for(int i = 0; i < 4; i++){
			startAngle = angle = i*(Math.PI/2);
			while(angle < startAngle + (Math.PI/2)){
				blocked = false;
				x = ep.x;
				y = ep.y;
				z = 0;
				temp = new Point((int)Math.round(x), (int)Math.round(y));
				while((z < sightDistance) && isPointInBounds(temp) && !blocked){
					if(!los.contains(tileMap[temp.x][temp.y])){
						tileMap[temp.x][temp.y].setHasBeenSeen(true);
						tileMap[temp.x][temp.y].setPosition(temp.x, temp.y);
						los.add(tileMap[temp.x][temp.y]);
					}
					blocked = !tileMap[temp.x][temp.y].getAllowTravel();  //this means that if I can walk on it then it doesn't block line of sigh
					x += Math.cos(angle);
					y += Math.sin(angle);
					temp = new Point((int)Math.round(x), (int)Math.round(y));
					z = Math.sqrt(Math.pow(Math.abs(ep.x-temp.x), 2) + Math.pow(Math.abs(ep.y-temp.y), 2));
				}
				angle += Math.PI/(2*divisions);
			}
		}
		return los;
	}
	/*
	 * This works by starting from a point and venturing out along a path
	 * that rotates around the start. pretty simple.
	 * Actually: It has to scan over each quadrant separately and both forwards and backwards.
	 */
	public ArrayList<Tile> getLineOfSight(Enemy e, int s){
		final int sightDistance = s;
		final int divisions = 1*sightDistance; //dear lord does this slow shit down
		
		ArrayList<Tile> los = new ArrayList<Tile>();
		Point ep =  e.getPosition();

		double x = ep.x;
		double y = ep.y;
		double z = 0;
		double angle = 0; //this points straight down. since positive y is down.
		double startAngle;
		
		boolean blocked;
		Point temp;
		
		for(int i = 0; i < 4; i++){
			startAngle = angle = i*(Math.PI/2);
			while(angle < startAngle + (Math.PI/2)){
				blocked = false;
				x = ep.x;
				y = ep.y;
				z = 0;
				temp = new Point((int)Math.round(x), (int)Math.round(y));
				while((z < sightDistance) && isPointInBounds(temp) && !blocked){
					if(!los.contains(tileMap[temp.x][temp.y])){
						tileMap[temp.x][temp.y].setPosition(temp.x, temp.y);
						los.add(tileMap[temp.x][temp.y]);
					}
					blocked = !tileMap[temp.x][temp.y].getAllowTravel();  //this means that if I can walk on it then it doesn't block line of sigh
					x += Math.cos(angle);
					y += Math.sin(angle);
					temp = new Point((int)Math.round(x), (int)Math.round(y));
					z = Math.sqrt(Math.pow(Math.abs(ep.x-temp.x), 2) + Math.pow(Math.abs(ep.y-temp.y), 2));
				}
				angle += Math.PI/(2*divisions);
			}
		}
		return los;
	}
	public boolean canEntityTravel(Point from, Direction d){
		char c;
		if(d == Direction.NORTH)
			c = 'w';
		else if(d == Direction.EAST)
			c = 'd';
		else if(d == Direction.SOUTH)
			c = 's';
		else if(d == Direction.WEST)
			c = 'a';
		else
			return false;
		
		return canEntityTravel(from, c);
	}
	public boolean canEntityTravel(Point from, char d){
		Point to = (Point) from.clone();
		if(d == 'w'){
			to.y--;
		}else if(d == 'd'){
			to.x++;
		}else if(d == 's'){
			to.y++;
		}else if(d == 'a'){
			to.x--;
		}else{
			System.err.println("ERROROROROROROROR");
		}
		
		return 	isPointInBounds(to) && tileMap[to.x][to.y].getAllowTravel() && !isOccupied(to);
	}
	/*
	 * cant move into the same position as a bolt
	 */
	public boolean hasBolt(Point point){
		boolean occupied = false;
		for(int i = 0; i < bolts.size() && !occupied; i++)
			occupied = point.equals(bolts.get(i).getPoint());
		return occupied;
	}
	public boolean isOccupied(Point point){
		boolean occupied = point.equals(player.getPosition());
		for(int i = 0; i < enemies.size() && !occupied; i++)
			occupied = point.equals(enemies.get(i).getPosition());
		return occupied;
	}
	private boolean isPointInBounds(Point p){
		return ((p.y > 0) && (p.y < height) && (p.x > 0) && (p.x < width));
	}
	private int getGreatestDistance(Point p1, Point p2){
		int out;
		if(Math.abs(p1.x - p2.x) > Math.abs(p1.y - p2.y))
			out = Math.abs(p2.x - p1.x);
		else
			out = Math.abs(p2.y - p1.y);
		return out;
		
	}
	private Direction changeDirection(Direction current){ //I wonder if enums are references.
			Direction temp = null;
			if(current == null)
				current = (Direction) DIRECTIONS.toArray()[random.nextInt(4)];
			else{
				temp = current;
				DIRECTIONS.remove(current);
				current = (Direction) DIRECTIONS.toArray()[random.nextInt(3)];
				DIRECTIONS.add(temp);
			}
			return current;
		
	}
	private void generateTerrain(long seed){
		random = new Random(seed);
		generateCavern2();
		//runTest();
	}
	private class Room{
		public Point position;
		public Room room;
		public int h;
		public int w;
		public Room(){
			h = 0;
			w = 0;
			position = new Point();
		}
	}
	private void runTest(){
		gameBoard.useGamePanel();
		random = new Random(123);
		Room roomA = new Room();
		Room roomB = new Room();
		roomA.position = new Point(20, 20);
		roomB.position = new Point(20, 50);
		generateRoom(roomA, 5);
		generateRoom(roomB, 6);
		connectRooms(roomA, roomB);
		
		searchForWalls();
		determineAllWallTiles();
		
		searchForWalkingTiles();
		generateItems(1); //1-5. 5 being most rare
		generateEnemies(1); //same as items.
		//player = new Player(this, Tiles.getTile("CIVILIAN", Color.RED), true);
		startingPoint.setLocation(roomA.position);
		player.setPosition(startingPoint);
		displayPlayerInformation(player);
		gamePanel.addEntity(player);
		gamePanel.setCurrentlyVisibleTiles(getLineOfSight(player, player.sightDistance));
		gameBoard.setVisible(true);
	}
	private void generateCavern2(){
		final int maxNumberOfRooms = 10;
		final int maxRoomSize = 5; //the radius of a room.
		final int minDistanceBetweenRooms = (maxRoomSize * 2) +2; //to avoid room's coming in to contact with each other
		
		ArrayList<Room> rooms = new ArrayList<Room>();
		for(int x = 0; x < width; x++){
			for(int y = 0; y < height; y++){
				tileMap[x][y] = Tiles.getTile("BLANK1", Color.GRAY);
			}
		}
				
		Point temp;
		boolean lb;
		for(int roomNumber = 0; roomNumber < maxNumberOfRooms; roomNumber++){
			rooms.add(new Room());
			temp = new Point();
			lb = false;
			while(!lb){ //since this is not in the main loop, we can afford to be non-deterministic and risk infinite loop. even if it is amateurish.
				temp.x = random.nextInt(width - (4*maxRoomSize)) + (2*maxRoomSize); //Maybe there is a case where there is no possible place to put
				temp.y = random.nextInt(height -(4*maxRoomSize)) + (2*maxRoomSize); //this room and it enters an infinite loops.
				lb = true;
				for(int i = 0; i < roomNumber; i++)
					lb &= (minDistanceBetweenRooms <= getGreatestDistance(temp, rooms.get(i).position));
			}
			rooms.get(roomNumber).position = temp;
			generateRoom(rooms.get(roomNumber), maxRoomSize);
		}
		
		ArrayList<Room> connectedRooms = new ArrayList<Room>();
		Room tempRoom;
		Collections.shuffle(rooms);
		for(int i = 0; rooms.size() > 0; i++){
			tempRoom  = rooms.remove(random.nextInt(rooms.size()));
			if(i > 0)
				connectRooms(connectedRooms.get(connectedRooms.size() - 1), tempRoom);
			connectedRooms.add(tempRoom);
		}
		
		startingPoint.setLocation(connectedRooms.get(0).position);
		tileMap[startingPoint.x][startingPoint.y] = Tiles.getTile("UP_STAIRS", Color.BLACK);
		tileMap[startingPoint.x][startingPoint.y].allowTravel(true);
		tileMap[startingPoint.x][startingPoint.y].allowTravelFromAllDirections(true);
		temp = connectedRooms.get(connectedRooms.size()-1).position;
		tileMap[temp.x][temp.y] = Tiles.getTile("DOWN_STAIRS", Color.BLACK);
		tileMap[temp.x][temp.y].allowTravel(true);
		tileMap[temp.x][temp.y].allowTravelFromAllDirections(true);
		
		searchForWalls();
		determineAllWallTiles();
		
	}
	private void generateCavern1(){ //this doesn't work anymore.
		final int maxTurns = 16;
		final int maxCount = 1000;
		final double CHANCE = .3;
		
		for(int x = 0; x < width; x++){
			for(int y = 0; y < height; y++){
				tileMap[x][y] = Tiles.getTile("BLANK1", Color.GRAY);
			}
		}
		
		startingPoint.setLocation(new Point(random.nextInt(width), random.nextInt(height)));
		Point point[] = new Point[3];
		String directionTileName;
		int rd;  //random delta
		boolean rb, lb;   //random boolean, loop boolean
		Direction d = null; //current direction
		Direction oldDirection = null;
		Room room = new Room();
		
		for(int count = 0; count < maxCount; count++){
			if((count % (maxCount / maxTurns)) == 0){
				d = changeDirection(d);
				if(count == 0){
					room.position = startingPoint;
					generateRoom(room, 4 );
				}else{
					room.position = point[1];
					generateRoom(room, 4);
				}
			}
			if(count == 0){
				point[2] = (Point) startingPoint.clone();
				tileMap[startingPoint.x][startingPoint.y] = Tiles.getTile("UP_STAIRS", Color.BLACK);
				tileMap[startingPoint.x][startingPoint.y].allowTravelFromAllDirections(true);
				tileMap[startingPoint.x][startingPoint.y].setStationary(true);
			}
			else if(count == 1){
				lb = true;
				rb = false;
				while(lb){
					if(d == Direction.NORTH)
						if((point[1].y - 1) > 0)
							point[2].y = point[1].y - 1;
						else rb = true;
					else if(d == Direction.SOUTH)
						if((point[1].y + 1) < height)
							point[2].y = point[1].y + 1;
						else rb = true;
					else if(d == Direction.EAST)
						if((point[1].x + 1) < width)
							point[2].x = point[1].x + 1;
						else rb = true;
					else if(d == Direction.WEST)
						if((point[1].x - 1) > 0)
							point[2].x = point[1].x - 1;
						else rb = true;
					
					if(rb) d = changeDirection(d);
					else lb = false;
				}
			}else if(count > 1){ ///different last loop. So I can place stairs leading down.
				if(random.nextDouble() > CHANCE){
					lb = true;
					//System.out.print("Direction Movement ");
					while(lb){
						if((d == Direction.NORTH) && (point[1].y - 1 != point[0].y) && (point[1].y - 1 > 0)){
							point[2].y = point[1].y - 1;
							lb = false;
						}else if((d == Direction.EAST) && (point[1].x + 1 != point[0].x) && (point[1].x + 1 < width)){
							point[2].x = point[1].x + 1;
							lb = false;
						}else if((d == Direction.SOUTH) && (point[1].y + 1 != point[0].y) && (point[1].y + 1 < height)){
							point[2].y = point[1].y + 1;
							lb = false;
						}else if((d == Direction.WEST) && (point[1].x - 1 != point[0].x) && (point[1].x - 1 > 0)){
							point[2].x = point[1].x - 1;
							lb = false;
						}
						
						if(lb) d = changeDirection(d);
					}
				}else{
					if(random.nextBoolean()) rd = 1;
					else rd = -1;
					lb = true;
					rb = true;
					while(lb){
						if((d == Direction.NORTH) || (d == Direction.SOUTH)){
							if((rd == 1) && (point[1].x + 1 < width) && (point[1].x + 1 != point[0].x)){
								point[2].x = point[1].x + 1;
								lb = false;
							}else if((rd == -1) && (point[1].x - 1 > 0)  && (point[1].x - 1 != point[0].x)){
								point[2].x = point[1].x -1;
								lb = false;
							}
						}else if((d == Direction.EAST) || (d == Direction.WEST)){
							if((rd == 1) && (point[1].y + 1 < height)  && (point[1].y + 1 != point[0].y)){
								point[2].y = point[1].y + 1;
								lb = false;
							}else if((rd == -1) && (point[1].y - 1 > 0)  && (point[1].y - 1 != point[0].y)){
								point[2].y = point[1].y - 1;
								lb = false;
							}
						}
						if(rb){
							rb = false;
							if(rd == 1) rd = -1;
							else if(rd == -1) rd = 1;
						}else lb = false;
					}
				}
			}

			if(count == maxCount - 1){
				tileMap[point[1].x][point[1].y] = Tiles.getTile("DOWN_STAIRS", Color.BLACK);
				tileMap[point[1].x][point[1].y].allowTravel(true);
				tileMap[point[1].x][point[1].y].setStationary(true);
				tileMap[point[1].x][point[1].y].allowTravelFromAllDirections(true);
			}			
			else if(point[0] != null){   //then there are 3 points in the buffer which is enough to start making decisions about the type of hallway.
				if(!((point[2].x == point[1].x) && (point[2].y == point[1].y))){ //cant just combine these ifs because I get nullptr
					determineDirectionalTile(point[0], point[1], point[2]);
				}
			}
			
			/*if((d != oldDirection) && (point[1] != null)){
				oldDirection = d;
			}/*
				
			
			/*
			 * There has got to be a better way to write this, but I keep getting null pointer exceptions.
			 */
			if((point[2] != null) && (point[1] != null)){
				if(!((point[2].x == point[1].x) && (point[2].y == point[1].y))){
					if(point[1] != null) point[0] = (Point) point[1].clone();
					point[1] = (Point) point[2].clone();
				}
			}else{
				if(point[1] != null) point[0] = (Point) point[1].clone();
				point[1] = (Point) point[2].clone();
			}
			
			
		}
		
	}
	private void generateRoom(Room room, int maxsize){
		Point start = room.position;
		double h = random.nextInt((2*maxsize) -1) / 2 + 1;
		double w = random.nextInt((2*maxsize) -1) / 2 + 1;
				
		room.h = (int) h;
		room.w = (int) w;
		
		for(int x = (int) -w; x < (int) w +1; x++){     
			for(int y = (int) -h; y < (int) h+1; y++){
				if(isPointInBounds(new Point(start.x + x, start.y + y))){
					tileMap[start.x + x][start.y + y] = Tiles.getTile("ROCK" + (random.nextInt(6) + 1), Color.BLACK);
					tileMap[start.x + x][start.y + y].allowTravelFromAllDirections(true);
					tileMap[start.x + x][start.y + y].allowTravel(true);
					tileMap[start.x + x][start.y + y].setStationary(true);
				}
			}
		}
		
	}
	//the boolean parameter determines whether to include the corners.
	private ArrayList<Point> getOutline(Room r, boolean inclusive){
		Point pointA;
		ArrayList<Point> out = new ArrayList<Point>();
		int temp;
		
		if(inclusive){
			temp = -1;
			
		}
		else
			temp = 0;
		
		for(int x = -r.w-temp; x <= r.w+temp; x++ ){
			pointA = new Point();
			pointA.x = r.position.x+x;
			pointA.y = r.position.y-r.h-(1*temp);
			if(isPointInBounds(pointA) && !out.contains(pointA)) out.add(pointA);
		}
		
		for(int y = -r.h-temp; y <= r.h+temp; y++ ){
			pointA = new Point();
			pointA.y = r.position.y+y;
			pointA.x = r.position.x+r.w+(1*temp);
			if(isPointInBounds(pointA) && !out.contains(pointA)) out.add(pointA);
		}
		
		for(int x = -r.w-temp; x <= r.w+temp; x++ ){
			pointA = new Point();
			pointA.x = r.position.x-x;
			pointA.y = r.position.y+r.h+(1*temp);
			if(isPointInBounds(pointA) && !out.contains(pointA)) out.add(pointA);
		}
		for(int y = -r.h-temp; y <= r.h+temp; y++ ){
			pointA = new Point();
			pointA.y = r.position.y-y;
			pointA.x = r.position.x-r.w-(1*temp);
			if(isPointInBounds(pointA) && !out.contains(pointA)) out.add(pointA);
		}
		return out;
	}
	private void connectRooms(Room a, Room b){//this creates a tunnel between two rooms.
		a.room = b; //good old linked list
		//now I just got to pick a random point on the outlining tiles of the room and pass them to connectPoints()
		Point pointA, pointB;
		
		ArrayList<Point> outline = getOutline(a, true);
		pointA = outline.get(random.nextInt(outline.size()));
		outline.clear();
		
		outline = getOutline(b, true);
		pointB = outline.get(random.nextInt(outline.size()));
		outline.clear();
		
		generateTunnel(pointA, pointB);		
	}
	private void generateTunnel(Point a, Point b){
		//thelist of necessary moves to get from point a to point b.
		ArrayList<Direction> moves = new ArrayList<Direction>();
		for(int i = 0; (Math.abs(b.x - a.x) - i) != 0; i++ ){
			if((b.x - a.x) > 0)
				moves.add(Direction.EAST);
			else
				moves.add(Direction.WEST);
		}
		for(int i = 0; (Math.abs(b.y - a.y) - i) != 0; i++){
			if((b.y - a.y) > 0)
				moves.add(Direction.SOUTH);
			else
				moves.add(Direction.NORTH);
		}
		Collections.shuffle(moves, random); //to add more randomness, you can add randomly add pairs of directions like EAST-WEST into the list
		                                      //but adding pairs would make it possible for it to generate a tile backwards which would be bad.
		Point points[] = new Point[moves.size()+1];
		points[0] = a;
		points[points.length-1] = b;
		
		for(Point p : points)
			walkingPoints.add(p);
		
		for(int i = 0; i < moves.size(); i++){
			points[1+i] = new Point();
			points[1+i].x = points[i].x;
			points[1+i].y = points[i].y;
			if(moves.get(i) == Direction.NORTH)
				points[1+i].y--;
			else if(moves.get(i) == Direction.EAST)
				points[1+i].x++;
			else if(moves.get(i) == Direction.SOUTH)
				points[1+i].y++;
			else if(moves.get(i) == Direction.WEST)
				points[1+i].x--;
				
			tileMap[points[i].x][points[i].y] = Tiles.getTile("ROCK"+(random.nextInt(6)+1), Color.BLACK);
			tileMap[points[i].x][points[i].y].allowTravel(true);
			tileMap[points[i].x][points[i].y].allowTravelFromAllDirections(true);
			tileMap[points[i].x][points[i].y].setStationary(true);
		}
		tileMap[b.x][b.y] = Tiles.getTile("ROCK"+(random.nextInt(6)+1), Color.BLACK);
		tileMap[b.x][b.y].allowTravel(true);
		tileMap[b.x][b.y].allowTravelFromAllDirections(true);
		tileMap[b.x][b.y].setStationary(true);
	}
	private void connectPoints(Point a, Point b){
		ArrayList<Direction> moves = new ArrayList<Direction>();
		for(int i = 0; (Math.abs(b.x - a.x) - i) != 0; i++ ){
			if((b.x - a.x) > 0)
				moves.add(Direction.EAST);
			else
				moves.add(Direction.WEST);
		}
		for(int i = 0; (Math.abs(b.y - a.y) - i) != 0; i++){
			if((b.y - a.y) > 0)
				moves.add(Direction.SOUTH);
			else
				moves.add(Direction.NORTH);
		}
		Point points[] = new Point[moves.size()+1];
		points[0] = a;
		points[points.length-1] = b;
		
		for(Point p : points)
			walkingPoints.add(p);
		
		for(int i = 0; i < moves.size(); i++){
			points[1+i] = new Point();
			points[1+i].x = points[i].x;
			points[1+i].y = points[i].y;
			if(moves.get(i) == Direction.NORTH)
				points[1+i].y--;
			else if(moves.get(i) == Direction.EAST)
				points[1+i].x++;
			else if(moves.get(i) == Direction.SOUTH)
				points[1+i].y++;
			else if(moves.get(i) == Direction.WEST)
				points[1+i].x--;
			
			tileMap[points[i].x][points[i].y] = Tiles.getTile("ROCK"+(random.nextInt(6)+1), Color.BLACK);
			tileMap[points[i].x][points[i].y].allowTravel(true);
			tileMap[points[i].x][points[i].y].allowTravelFromAllDirections(true);
			tileMap[points[i].x][points[i].y].setStationary(true);
		}
		tileMap[b.x][b.y] = Tiles.getTile("ROCK"+(random.nextInt(6)+1), Color.BLACK);
		tileMap[b.x][b.y].allowTravel(true);
		tileMap[b.x][b.y].allowTravelFromAllDirections(true);
		tileMap[b.x][b.y].setStationary(true);
	}
	private void determineAllWallTiles(){
		for(Point p : wallPoints)
			determineWallTile(p);
	}
	private void determineWallTile(Point a){
		String directionTileName = "";
		
		if(a.y-1 > 0)
			if(tileMap[a.x][a.y-1].isDirectional()) 
				directionTileName += "NORTH_";
		if(a.x+1<width)
			if(tileMap[a.x+1][a.y].isDirectional()) 
				directionTileName += "EAST_";
		if(a.y+1<height)
			if(tileMap[a.x][a.y+1].isDirectional())
				directionTileName += "SOUTH_";
		if(a.x-1 > 0)
			if(tileMap[a.x-1][a.y].isDirectional()) 
				directionTileName += "WEST_";
		
		if(directionTileName.equals("NORTH_"))
			directionTileName = "NORTH_SOUTH_";
		if(directionTileName.equals("EAST_"))
			directionTileName = "EAST_WEST_";
		if(directionTileName.equals("SOUTH_"))
			directionTileName = "NORTH_SOUTH_";
		if(directionTileName.equals("WEST_"))
			directionTileName = "EAST_WEST_";
		
		
		
		directionTileName += "1";
		if(directionTileName.equals("1"))
			tileMap[a.x][a.y] = Tiles.getTile("BOULDER", Color.BLACK);
		else if(Tiles.tileNames.contains(directionTileName))
			tileMap[a.x][a.y] = Tiles.getTile(directionTileName, Color.BLACK);
		else
			System.err.println("Error tileName not found: " + directionTileName);
	}
	/*
	 * This will check every tile and if it is neighboring walking space, then identify it as a wall tile
	 * After enumerating wall tiles, it well then call determineAllWallTiles to determine which tile to place one each wall.
	 * This is also going to enumerate the walking tiles so i can place enemies and Items later.
	 */
	private void searchForWalls(){
		boolean isWall;
		wallPoints.clear();
		for(int x = 0; x < width; x++){
			for(int y = 0; y < height; y++){
				isWall = false;
				if(tileMap[x][y].getTileName().equals("BLANK1")){
					for(int a = -1; (a<=1) && !isWall; a++){
						for(int b = -1; (b<=1) && !isWall; b++){
							if(!(a==0 && b==0) && (x+a)>0 && (x+a)<width && (y+b)>0 && (y+b)<height){
								isWall = tileMap[x+a][y+b].getAllowTravel();
							}
						}
					}
				}
				if(isWall){
					tileMap[x][y] = Tiles.getTile("FLOOR1", Color.BLACK);
					tileMap[x][y].setDirectional(true);
					wallPoints.add(new Point(x, y));
				}
				
			}
		}
	}

	private void searchForWalkingTiles(){
		walkingPoints.clear();
		for(int x = 0; x < width; x++){
			for(int y = 0; y < height; y++){
				if(tileMap[x][y].getTileName().contains("ROCK")){
					walkingPoints.add(new Point(x, y));
				}
			}
		}
	}
	/*
	 * this will generate random items all along the walkable tiles.
	 * the rarity parameter refers is a function that relates the total number of walkable tiles to the number of items
	 * all this does right now is generate a bunch of potions and edibles.
	 */
	private void generateEnemies(int rarity){
		final int NUMBER_OF_ENEMIES = walkingPoints.size() / (10*rarity);
		ArrayList<Point> shuffled = (ArrayList<Point>) walkingPoints.clone();
		Collections.shuffle(shuffled);
		Tile tile;
		Point point;
		Enemy enemy;
		enemies = new ArrayList<Enemy>();
		gamePanel.clearEnemies();
		for(int i = 0; i < NUMBER_OF_ENEMIES; i++){
			point = shuffled.get(i);
			tile = Tiles.getTile("UNDEAD", Color.RED);
			tile.setPosition(point.x, point.y);
			enemy = new Enemy(this, tile);
			enemies.add(enemy);
			gamePanel.addEntity(enemy);
		}
	}
	private void generateItems(int rarity){ 
		final int NUMBER_OF_EDIBLES = walkingPoints.size() / (10*rarity); // you need less of these.
		final int NUMBER_OF_POTIONS = walkingPoints.size() / (5*rarity);
		ArrayList<Point> temp = (ArrayList<Point>) walkingPoints.clone();
		Item item;
		Point t;
		for(int i  = 0; (i < NUMBER_OF_POTIONS) && (temp.size() > 0); i++){
			t = temp.remove(random.nextInt(temp.size()));
			item = Potion.getRandomPotion(random);
			item.setPoint(t);
			item.setTileBehindThis(tileMap[t.x][t.y]);
			items.add(item);
			tileMap[t.x][t.y] = item.getTile();
			tileMap[t.x][t.y].allowTravel(true);
			tileMap[t.x][t.y].allowTravelFromAllDirections(true);
		}
		
		for(int i = 0; i < NUMBER_OF_EDIBLES && temp.size() > 0; i++){
			t = temp.remove(random.nextInt(temp.size()));
			item = new Edible();
			item.setPoint(t);
			item.setTileBehindThis(tileMap[t.x][t.y]);
			items.add(item);
			tileMap[t.x][t.y] = item.getTile();
			tileMap[t.x][t.y].allowTravel(true);
			tileMap[t.x][t.y].allowTravelFromAllDirections(true);
		}
	}
	//this is actually a super useless and stupid method. My new implementation is much better.
	private void determineDirectionalTile(Point a, Point b, Point c){
		String directionTileName = "";
		ArrayList<String> directions = new ArrayList<String>();
				
		if((c.y - b.y) < 0) directions.add("NORTH");
		if((b.y - a.y) > 0) directions.add("NORTH");
		if((c.x - b.x) > 0) directions.add("EAST");
		if((b.x - a.x) < 0) directions.add("EAST");
		if((c.y - b.y) > 0) directions.add("SOUTH");
		if((b.y - a.y) < 0) directions.add("SOUTH");
		if((c.x - b.x) < 0) directions.add("WEST");
		if((b.x - a.x) > 0) directions.add("WEST");
		
		for(String direction : tileMap[b.x][b.y].getDirections())
			directions.add(direction);
		
		if(directions.contains("NORTH")) directionTileName += "NORTH_";
		if(directions.contains("EAST"))  directionTileName +=  "EAST_";
		if(directions.contains("SOUTH")) directionTileName += "SOUTH_";
		if(directions.contains("WEST"))  directionTileName +=  "WEST_";
						
		directionTileName += "1";
		if(tileMap[b.x][b.y].isDirectional() || tileMap[b.x][b.y].equals(Tiles.getTile("BLANK1", Color.BLACK))){  //if there already exists a tile at the point and it is a directional hallway type, or if no tile exists here yet:
			tileMap[b.x][b.y] = Tiles.getTile(directionTileName, Color.BLACK);
			//tileMap[b.x][b.y].allowTravel(true);
			tileMap[b.x][b.y].setStationary(true);
		}
	}
	private void generateField(Random r){
		
	}
	public Player getPlayer(){
		return player;
	}
} 
