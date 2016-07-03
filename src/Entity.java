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
	private MeleeWeapon equippedMeleeWeapon;
	private RangedWeapon equippedRangedWeapon;
	public Entity(GameEngine e, Tile t, boolean b){
		engine = e;
		IS_USER = b;
		tile = t;
		point = tile.getPosition();
		health = 100;
		hunger = 0;
		inventory = new ArrayList<Item>();
		equippedArmor = null;
		equippedMeleeWeapon = null;
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
	public void fireBolt(char d){
		Direction aim = null;
		if(d == '8') aim = Direction.NORTH;
		else if(d == '4') aim = Direction.WEST;
		else if(d == '5') aim = Direction.SOUTH;
		else if(d == '6') aim = Direction.EAST;
		fireBolt(aim);
	}
	public void fireBolt(Direction aim){
		Bolt bolt = new Bolt(aim, getRangedDamage());
		bolt.setPoint((Point) point.clone());
		engine.addBolt(bolt); //let the engine figure it out.
	}
	public int getWeaponDamage(){
		if(equippedMeleeWeapon == null) return 1;
		else return equippedMeleeWeapon.damage;
	}
	public int getRangedDamage(){
		if(equippedRangedWeapon == null) return 1;
		else return equippedRangedWeapon.damage;
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
	public void equipRangedWeapon(RangedWeapon a){
		equippedRangedWeapon = a;
		equippedRangedWeapon.equip();
	}
	public RangedWeapon unequipRangedWeapon(){
		RangedWeapon temp = equippedRangedWeapon;
		equippedRangedWeapon = null;
		temp.unequip();
		return temp;
	}
	public RangedWeapon getEquippedRangedWeapon(){
		return equippedRangedWeapon;
	}
	public void equipMeleeWeapon(MeleeWeapon a){
		equippedMeleeWeapon = a;
		equippedMeleeWeapon.equip();
	}
	public MeleeWeapon unequipMeleeWeapon(){
		MeleeWeapon temp = equippedMeleeWeapon;
		equippedMeleeWeapon = null;
		temp.unequip();
		return temp;
	}
	public MeleeWeapon getEquippedMeleeWeapon(){
		return equippedMeleeWeapon;
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
