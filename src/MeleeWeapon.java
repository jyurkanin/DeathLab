import java.awt.Color;


public class MeleeWeapon extends Weapon{
	public MeleeWeapon(String s){
		super(Tiles.getTile("WEAPON_RACK", Color.BLACK));
		name = s;
		if(name.equals("AXE")){
			damage = 5;
		}
		else if(name.equals("LONGSWORD"))
			damage = 10;
	}
}
