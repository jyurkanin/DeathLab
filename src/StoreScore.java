/*
 * This is a servlet that the client uses to send scores to the server
 */
import java.io.*;
import java.net.*;

public class StoreScore{
    public static void main(String[] args) throws Exception {
        String highScores = URLEncoder.encode(args[1], "UTF-8");
        PrintWriter file = new PrintWriter("highscores.txt");
        file.println(highScores);
        file.close();
    }
}

