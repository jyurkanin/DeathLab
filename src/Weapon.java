import java.awt.Color;


public class Weapon extends Item implements Equippable{
	int damage;
	boolean isWeaponEquipped;
	public Weapon(Tile t){
		super(t);
		isWeaponEquipped = false;
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
