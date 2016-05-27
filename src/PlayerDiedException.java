
public class PlayerDiedException extends Exception{
	private static final long serialVersionUID = 1L;
	Player player;
	int score;
	public PlayerDiedException(Player p){
		super();
		player = p;
		score = player.score;
	}
	public String getMessage(){
		String output = 
				"Player is super ded\n" +
				"Score: " + player.score + "\n" +
				"Level: " + player.engine.level + "\n" +
				"Kills: " + player.kills + "\n";
		
		return output;
	}
	public int getScore(){
		return score;
	}
}
