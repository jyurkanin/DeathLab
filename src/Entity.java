import java.awt.Point;
import java.util.ArrayList;


public class Entity {
	private Tile tile;
	public Point point;
	private String name;
	public int health;
	public int strength;
	public int hunger;
	public final boolean IS_USER;
	public GameEngine engine;
	ArrayList<Item> inventory;
	private Armor equippedArmor;
	private Weapon equippedWeapon;
	public Entity(GameEngine e, Tile t, boolean b){
		engine = e;
		IS_USER = b;
		tile = t;
		point = tile.getPosition();
		health = 100;
		hunger = 0;
		inventory = new ArrayList<Item>();
		equippedArmor = null;
		equippedWeapon = null;
	}
	/*
	 * This returns a true or false if it kills the opponent
	 */
	public boolean attack(Entity opponent){
		if(!(opponent.IS_USER || IS_USER)) return false; //this prevents an enemy form attacking another enemy
		int totalDamage = (10 * getWeaponDamage())/opponent.getArmorResistance();
		opponent.health -= totalDamage;
		String s =  IS_USER? "Player: " : "Enemy: ";
		engine.statusPanel.addMessage(s + Responses.getBattleCry());
		if(opponent.health <= 0){
			if(!opponent.IS_USER){
				engine.enemies.remove(opponent);
				engine.gamePanel.removeEntity(opponent);
				engine.statusPanel.addMessage("Enemy: " + Responses.getEnemyKilledMsg());
			}
			return true;
		}
		return false; //false as in not dead.
	}
	public int getWeaponDamage(){
		if(equippedWeapon == null) return 1;
		else return equippedWeapon.damage;
	}
	public int getArmorResistance(){
		if(equippedArmor == null) return 1;
		else return equippedArmor.resistance;
	}
	public void equipArmor(Armor a){
		equippedArmor = a;
		equippedArmor.equip();
	}
	public Armor unequipArmor(){
		Armor temp = equippedArmor;
		equippedArmor = null;
		return temp;
	}
	public Armor getEquippedArmor(){
		return equippedArmor;
	}
	public void equipWeapon(Weapon a){
		equippedWeapon = a;
		equippedWeapon.equip();
	}
	public Weapon unequipWeapon(){
		Weapon temp = equippedWeapon;
		equippedWeapon = null;
		return temp;
	}
	public Weapon getEquippedWeapon(){
		return equippedWeapon;
	}
	public void setPosition(Point p){
		point = (Point) p.clone();
	}
	public Point getPosition(){
		return point;
	}
	public Tile getTile(){
		return tile;
	}
	public boolean isAlive(){
		return health > 0;
	}
}
