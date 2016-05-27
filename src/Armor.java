import java.awt.Color;



public class Armor extends Item implements Equippable{
	boolean isArmorEquipped;
	int resistance;
	public Armor(String n){
		super(Tiles.getTile("ARMOR1", Color.BLACK));
		name = n;
		if(n.equals("MAIL"))
			resistance = 2;
		else if(n.equals("SUIT"))
			resistance = 4;
		isArmorEquipped = false;
	}
	public boolean isEquipped() {
		return isArmorEquipped;
	}

	public void equip() {
		isArmorEquipped = true;
		
	}

	public void unequip() {
		isArmorEquipped = false;
		
	}
}
