import java.awt.Color;
import java.util.Random;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		System.out.println("05/20");
		runTest();
		
	}
	public static void runTest() throws Exception{
		System.out.println("running");
		GameEngine g = new GameEngine();
		g.startGame();
		System.out.println("Game Over");
	}

}
