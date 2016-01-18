import java.awt.Color;
import java.util.*;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.*;



public class TankClient extends Frame {
	public static final int GAME_WIDTH = 800;
	public static final int GAME_HEIGHT = 600;
	
	List<Missile> missile = new ArrayList<Missile>();
	
	Tank myTank = new Tank(50, 50, true, Dir.D, this);
	List<Tank> enemy = new ArrayList<Tank>();
	//Tank enemy = new Tank(200, 200, false, this);
	
	Missile m;
	List<Explode> explodes = new ArrayList<Explode>();
	Image offScreenImage = null;
	NetClient nc = new NetClient(this);
	
	public void paint(Graphics g) {
		g.drawString("missile count:" + missile.size(), 40, 40);
		for(int i = 0; i < enemy.size(); i++) {
			Tank t = enemy.get(i);
			t.draw(g);
		}
		for(int i =0; i < missile.size(); i++) {
			Missile m = missile.get(i);
			m.hitTanks(enemy);
			m.hitTank(myTank);
			m.draw(g);
		}
		myTank.draw(g);

		for(int i = 0; i < explodes.size(); i++) {
			Explode e = explodes.get(i);
			e.draw(g);
		}
	}
    
	 
	public void update(Graphics g) { 
		if(offScreenImage == null) {
			offScreenImage = this.createImage(GAME_WIDTH, GAME_HEIGHT);
		}
		Graphics gOffScreen = offScreenImage.getGraphics();
		Color c = gOffScreen.getColor(); 
		gOffScreen.setColor(Color.GREEN);
		gOffScreen.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);
		gOffScreen.setColor(c);
		paint(gOffScreen);
		g.drawImage(offScreenImage, 0, 0, null);
		
	}

	public void lauchFrame() {
		
		
		//this.setLocation(400, 300);
		this.setSize(GAME_WIDTH, GAME_HEIGHT);
		this.setName("TankWar");
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		this.setResizable(false);
		this.setBackground(Color.GREEN); 
		setVisible(true);
		new Thread(new PaintThread()).start() ;
		nc.connect("127.0.0.1", TankServer.TCP_PORT);
		this.addKeyListener(new KeyMonitor());
	}
 
	public static void main(String[] args) {
		TankClient tc = new TankClient();
		tc.lauchFrame();
	}

	private class PaintThread implements Runnable {
		public void run() {
			while(true) {
				repaint();
				try {
					Thread.sleep(100);
				}
				catch (InterruptedException e){
					e.printStackTrace(); 
				}
			}
		}
	}
	
	private class KeyMonitor extends KeyAdapter {

		public void keyPressed(KeyEvent e) {
			myTank.KeyPressed(e);
		}
		
		public void keyReleased(KeyEvent e) {
			myTank.KeyReleased(e);
		}
	}
}
