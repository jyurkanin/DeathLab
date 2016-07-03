import java.awt.Color;

//this probably wont be implemented.
//Oh but it will be.
public class RangedWeapon extends Weapon{
	public RangedWeapon(Tile t){
		super(t);
	}
	public RangedWeapon(String n){
		super(Tiles.getTile("CROSS_BOW", Color.BLACK));
		damage = 10;
	}
}
