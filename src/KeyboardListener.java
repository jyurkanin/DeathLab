import java.awt.event.*;
import java.util.*;

public class KeyboardListener implements KeyListener{
	
	private volatile ArrayList<Character> eventBuffer;
	public KeyboardListener(ArrayList<Character> eb){
		eventBuffer = eb; //connect the references.
	}
	public void keyPressed(KeyEvent e) {	
		eventBuffer.add(e.getKeyChar());
	}

	public void keyReleased(KeyEvent arg0) {
		
		
	}

	public void keyTyped(KeyEvent arg0) {
		
	}
	

}
