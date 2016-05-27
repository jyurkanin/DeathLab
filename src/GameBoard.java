import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyListener;
import javax.swing.*;


public class GameBoard extends JFrame implements ComponentListener{
	private static final long serialVersionUID = 8797133159177097046L;
	private GamePanel gamePanel;
	private GameMenu menuPanel;
	private StatusPanel statusPanel;
	private HighScorePanel highScorePanel;
	//private JPanel panel;
	public GameBoard(Player player){
		super("Game");
		setPreferredSize(new Dimension(700, 700));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Dimension td = new Dimension(642, 675);
		setSize(td);
		Dimension d = new Dimension(1920, 1024);
		gamePanel = new GamePanel(player, d, td);
		menuPanel = new GameMenu(d, td);
		statusPanel = new StatusPanel(td);
		highScorePanel = new HighScorePanel(td);
		addComponentListener(this);
	}
	public StatusPanel getStatusPanel(){
		return statusPanel;
	}
	public GamePanel getPanel(){
		return gamePanel;
	}
	public GameMenu getGameMenu(){
		return menuPanel;
	}
	public HighScorePanel getHighScorePanel(){
		return highScorePanel;
	}
	public void useHighScorePanel(){
		boolean shouldSwap = true;
		if(getContentPane().getComponentCount() > 0){
			shouldSwap = !getContentPane().getComponent(0).equals(highScorePanel);
		 	if(shouldSwap){
				remove(gamePanel);
				remove(statusPanel);
				remove(menuPanel);
			}
		}
		if(shouldSwap){
			getContentPane().setLayout(new BorderLayout());
			add(highScorePanel, BorderLayout.CENTER);
			pack();
		}
	}
	public void useMenuPanel(){
//		KeyListener user;
		boolean shouldSwap = true;
		if(getContentPane().getComponentCount() > 0){
//			if(gamePanel.getKeyListeners().length > 0){
//				user = gamePanel.getKeyListeners()[0];
//				gamePanel.removeKeyListener(user);
//				menuPanel.addKeyListener(user);
//			}
			shouldSwap = !getContentPane().getComponent(0).equals(menuPanel);
			if(shouldSwap){
				remove(gamePanel);
				remove(statusPanel);
				remove(highScorePanel);
			}
		}
		if(shouldSwap){
			getContentPane().setLayout(new BorderLayout());
			add(menuPanel, BorderLayout.CENTER);
			pack();
		}
	}
	public void displayString(int x, int y, String string){
		statusPanel.displayString(x, y, string);
	}
	public void useGamePanel(){
//		KeyListener user;
		boolean shouldSwap = true;
		if(getContentPane().getComponentCount() > 0){
//			if(menuPanel.getKeyListeners().length > 0){
//				user = menuPanel.getKeyListeners()[0];
//				menuPanel.removeKeyListener(user);
//				gamePanel.addKeyListener(user);
//			}
			shouldSwap = !(getContentPane().getComponent(0).equals(gamePanel) || getContentPane().getComponent(0).equals(statusPanel));
			if(shouldSwap){
				remove(menuPanel);
				remove(highScorePanel);
			}
		}
		if(shouldSwap){
			getContentPane().setLayout(new BorderLayout());
			add(gamePanel, BorderLayout.CENTER);
			add(statusPanel, BorderLayout.LINE_END);
			pack();
			statusPanel.initialize();
		}
	}

	public void componentHidden(ComponentEvent arg0) {}
	public void componentMoved(ComponentEvent arg0) {}
	public void componentResized(ComponentEvent arg0) {
		if(getContentPane().getComponent(0).equals(gamePanel) || getContentPane().getComponent(0).equals(statusPanel)){
			statusPanel.initialize();
		}
	}
	public void componentShown(ComponentEvent arg0) {}
}
