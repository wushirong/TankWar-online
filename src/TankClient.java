import java.awt.Button;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Label;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;




public class TankClient extends Frame {
	public static final int GAME_WIDTH = 800;
	public static final int GAME_HEIGHT = 600;
	
	List<Missile> missiles = new ArrayList<Missile>();
	List<Explode> explodes = new ArrayList<Explode>();
	List<Tank> tanks = new ArrayList<Tank>();
	
	Tank myTank = new Tank(50, 50, true, Dir.STOP, this);
	List<Tank> enemy = new ArrayList<Tank>();
	//Tank enemy = new Tank(200, 200, false, this);
	
	Missile m;
	Image offScreenImage = null;
	NetClient nc = new NetClient(this);
	
	ConnDialog dialog = new ConnDialog();
	
	public void paint(Graphics g) {
		g.drawString("missile count:" + missiles.size(), 40, 40);
		for(int i = 0; i < enemy.size(); i++) {
			Tank t = enemy.get(i);
			t.draw(g);
		}
		for(int i =0; i < missiles.size(); i++) {
			Missile m = missiles.get(i);
			//m.hitTanks(enemy);
			if(m.hitTank(myTank)) {
				TankDeadMsg msg = new TankDeadMsg(myTank.id);
				MissileDeadMsg mdmsg = new MissileDeadMsg(m.tankId, m.id);
				nc.send(mdmsg);
				nc.send(msg);
			}
			m.draw(g);
		}
		for(int i = 0; i < tanks.size(); i++) {
			tanks.get(i).draw(g);
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
		//nc.connect("127.0.0.1", TankServer.TCP_PORT);
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
			int key = e.getKeyCode();
			if(key == KeyEvent.VK_C) {
				dialog.setVisible(true);
			}
			else {
			myTank.keyPressed(e);
			}
		}
		
		public void keyReleased(KeyEvent e) {
			myTank.keyReleased(e);
		}
	}
	
	class ConnDialog extends Dialog {
		Button b = new Button("Confirm");
		TextField tfIP = new TextField("127.0.0.1", 12);
		TextField tfPort = new TextField("" + TankServer.TCP_PORT, 4);
		TextField tfMyUDPPort = new TextField("2333", 4);
		public ConnDialog() {
			super(TankClient.this, true);
			
			this.setLayout(new FlowLayout());
			this.add(new Label("IP:"));
			this.add(tfIP);
			this.add(new Label("Port:"));
			this.add(tfPort);
			this.add(new Label("My UDP Port:"));
			this.add(tfMyUDPPort);
			this.setLocation(200, 200);
			this.add(b);
			this.pack();
			
			this.addWindowListener(new WindowAdapter() {
				@Override 
				public void windowClosing(WindowEvent e) {
					setVisible(false);
				}
			});
			
			b.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					String IP = tfIP.getText();
					int port = Integer.parseInt(tfPort.getText().trim());
					int myUDPPort = Integer.parseInt(tfMyUDPPort.getText().trim() );
					nc.setUdpPort(myUDPPort);
				
					nc.connect(IP, port);
					setVisible(false);
				}
			});
		}
		
	}
}
