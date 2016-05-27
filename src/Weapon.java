import java.awt.Color;


public class Weapon extends Item implements Equippable{
	int damage;
	boolean isWeaponEquipped;
	public Weapon(Tile t){
		super(t);
		isWeaponEquipped = false;
	}
	public Weapon(String s){
		super(Tiles.getTile("WEAPON_RACK", Color.BLACK));
		name = s;
		if(name.equals("AXE")){
			damage = 5;
		}
		else if(name.equals("LONGSWORD"))
			damage = 10;
	}
	public boolean isEquipped() {
		return isWeaponEquipped;
	}
	public void equip() {
		isWeaponEquipped = true;
		
	}
	public void unequip() {
		isWeaponEquipped = false;
		
	}
}
