import java.awt.Color;
import java.util.Random;


public class Potion extends Item{
	int heal;
	public Potion(int i){ //this will be a number 1 - 5 which will determine the amount of health that gets restored.
		super(Tiles.getTile("POTION", Color.BLACK));
		isPotion = true;
		name = "POTION";
		if(!(i <= 5 && i >= 1)){
			System.err.println("Invalid Argument for Potion constructor: " + i);
			System.exit(0);
		}
		
		heal = i * 20;
			
	}
	public int getHeal(){
		return heal;
	}
	public static Potion getRandomPotion(Random r){ //this returns a random potion based on a distribution by health: the ones that restore the most health are more rare.
		int i = r.nextInt(100);
		int j = 0; //value isn't used.
		if(i < 50)
			j = 1;
		else if(i < 75)
			j = 2;
		else if(i < 90)
			j = 3;
		else if(i < 98)
			j = 4;
		else if(i < 100)
			j = 5;
		return new Potion(j);
	}
}
