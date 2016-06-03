import java.awt.Point;


public class Player extends Entity{
	final static int ENEMY_KILLED_BONUS = 1;
	final static int NEXT_FLOOR_BONUS = 10;
	public int score;
	public int kills;
	public final int sightDistance = 10; //hero has enhanced vision
	public Player(GameEngine e, Tile t){
		super(e, t, true);
		score = 0;
		kills = 0;
		health = 1;
		hunger = 100;
		generateInventory();
	}	
	public void generateInventory(){
		equipArmor(new Armor("MAIL"));
		equipWeapon((Weapon) new MeleeWeapon("AXE"));
		inventory.add(getEquippedArmor());
		inventory.add(getEquippedWeapon());
	}
	public void incrementScore(int s){
		score += s;
	}
	public void updatePlayer(char input) throws PlayerDiedException{
		Point temp = (Point) point.clone();
		if("wasd".contains(String.valueOf(input))){
			
			if(engine.random.nextInt(100) > 90 && !engine.statusPanel.hasMessage()) 
				engine.statusPanel.addMessage(Responses.getRandomStatusMsg());
			
			if(input == 'w'){temp.y--;}
			else if(input == 'd'){temp.x++;}
			else if(input == 's'){temp.y++;}
			else if(input == 'a'){temp.x--;}
			if(engine.canEntityTravel(point, input)){
				point.setLocation(temp);
				if(hunger < 100)  hunger++;
				if(hunger >= 100) health--;
				engine.statusPanel.clear();
			}else if(engine.isOccupied(temp))
				if(attack(temp)){
					incrementScore(Player.ENEMY_KILLED_BONUS);	//attack enemy at given location
					kills++;
				}
		}
		else if(input == ',') pickUpItem();
		else if(input == 'e') eatItem();
		else if(input == 'q') quaffPotion();
		else if(input == '\n') descendStairs();
		
		if(!isAlive()) throw new PlayerDiedException(this);
	}
	private boolean attack(Point point){
		Enemy enemy = null;
		for(int i = 0; i < engine.enemies.size(); i++)
			if(engine.enemies.get(i).getPosition().equals(point))
				enemy = engine.enemies.get(i);
		if(enemy != null)
			return super.attack(enemy);
		else return false;
	}
	private void descendStairs(){
		if(engine.tileMap[point.x][point.y].getTileName().equals("DOWN_STAIRS"))
			engine.nextLevel();
	}
	private void quaffPotion(){
		boolean havePotion1 = false;
		boolean havePotion2 = false;
		boolean havePotion3 = false;
		boolean havePotion4 = false;
		boolean havePotion5 = false;
		Potion p;
		String output;
		
		for(int i = 0; i < inventory.size(); i++){
			if(inventory.get(i).isPotion()){
				p = (Potion) inventory.get(i);
				if(p.getHeal() == 20)
					havePotion1 = true;
				else if(p.getHeal() == 40)
					havePotion2 = true;
				else if(p.getHeal() == 60)
					havePotion3 = true;
				else if(p.getHeal() == 80)
					havePotion4 = true;
				else if(p.getHeal() == 100)
					havePotion5 = true;
			}
		}
		
		int selection = -1;
		Character temp = ' ';
		if(havePotion1 || havePotion2 || havePotion3 || havePotion4 || havePotion5){
			output = "Quaff: ";
			if(havePotion1)
				output += "1 ";
			if(havePotion2)
				output += "2 ";
			if(havePotion3)
				output += "3 ";
			if(havePotion4)
				output += "4 ";
			if(havePotion5)
				output += "5 ";
			engine.gameBoard.displayString(0,  3,  output);
			engine.gameBoard.repaint();
			
			do{
				try{Thread.sleep(10);}
				catch(Exception e){e.printStackTrace();}
				if(!engine.eventBuffer.isEmpty())
					temp = engine.eventBuffer.remove(0);
			}while(!(Character.isDigit(temp) && output.contains(String.valueOf(temp))));
			selection = Integer.parseInt(String.valueOf(temp)); //there has to be a better way.
			
			for(int i = 0; i < inventory.size() && !(selection == -1); i++){
				if(inventory.get(i).isPotion()){
					p = (Potion) inventory.get(i);
					if(p.getHeal() == selection * 20){
						inventory.remove(i);
						health += p.getHeal();
						selection = -1; //this is just to end the loop
						if(health > 100)
							health = 100;
					}
				}
			}
			engine.gameBoard.displayString(0, 3, "You Feel Better");
		}else{
			output = "You have no potions";
			engine.gameBoard.displayString(0,  2,  output);
		}
		
	}
	private void eatItem(){
		boolean haveFood = false;
		boolean eating = false;
		for(int i = 0; i < inventory.size() && !haveFood; i++)
			haveFood |=  inventory.get(i).isEdible();
		
		if(haveFood){
			engine.gameBoard.displayString(0, 3, "You Feel Full");
			for(int i = 0; i < inventory.size() && !eating; i++)
			{
				if(inventory.get(i).isEdible()) 
				{	
					inventory.remove(i);
					eating = true;
				}
			}
			hunger = 0;
		}
		else
			engine.gameBoard.displayString(0,  3, "You have no food");
		
	}
	private void pickUpItem(){
		Item it;
		boolean notMatched = true;
		for(int i = 0; i < engine.items.size() && notMatched; i++){
			it = engine.items.get(i);
			if(it.getPoint().equals(point)){
				engine.items.remove(i);
				inventory.add(it);
				engine.tileMap[point.x][point.y] = it.getTileBehindThis();
				notMatched = false;
			}
		}
	}
	public void reset(){
		inventory.clear();
		generateInventory();
		health = 100;
		hunger = 0;
		score = 0;
	}
}
