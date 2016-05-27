import java.awt.Color;
import java.util.Random;


public class Edible extends Item{
	public Edible(){
		super(Tiles.getTile("PLANT", Color.BLACK));
		name = "EDIBLE";
		isEdible = true;
		//de
	}
}
