import java.util.Random;

public class Responses {
	static Random random = new Random();
		static String getPlayerDetectedMsg(){
			String[] detected = {"Who goes there!", "Stop right there!", " Halt!", "Resistance is futile!", "Hands up Buttercup!", "Ill bash ye hed in m8!","Check yourself!","One does not simply stroll through my cavern.","Its dat boi! Oh shoot waddup?"};
			return detected [random.nextInt(detected.length)];
		}
		static String getEnemyKilledMsg(){
			String [] killed = {"Ah im dead!", "Tis but a flesh wound!", "AAAAAAAAAaaaaaaaaaahhhhhh", "Lights fading. limbs growing cold...", "You win...this time!", "All those moments will be lost in time like tears in rain. Time to die", "Oh the horror!", "Ouch!", "How is this possible?!?"};
			return killed [random.nextInt(killed.length)];
		}
		static String getBattleCry(){
			String [] cry = {"Feelin lucky punk?", "AaaaAaaaAaaaAaaa!!!", "I have you right where i want you!", "Heeeyah!", "You want some of this?", "Give me your best move!", "Freeeeeeedom!", "Yippeekiyay!", "Youre in for a world of pain son.", "Prepare to meet your maker!"};
			return cry [random.nextInt(cry.length)];
		}
		static String getRandomStatusMsg(){
			String [] status = {"The rain continues to fall...", "You hear the drops of water from the ceiling of the cave you wonder if you will ever escape.", "The monotony of eating flowers irratates you.", "You hear the ruffling of bats wings", "You step into a puddle", "The thought that the mouse may one day get the cheese fills you with determination", "The thought of one day escaping the cavern fills you with determination."};
			return status [random.nextInt(status.length)];
		}
	}