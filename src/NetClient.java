import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketException;



public class NetClient {
	public static int UDP_PORT_START = 2333;
	public int udpPort;
	TankClient tc;
	DatagramSocket ds = null;
	
	public NetClient(TankClient tc) {
		udpPort = UDP_PORT_START++;
		this.tc = tc;
		try {
			ds = new DatagramSocket(udpPort);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	public void connect(String IP, int port) {
		Socket s = null;
		try {
			s = new Socket(IP, port);
			DataOutputStream dos = new DataOutputStream(s.getOutputStream());
			dos.writeInt(udpPort);
			DataInputStream dis = new DataInputStream(s.getInputStream());
			int id = dis.readInt();
			tc.myTank.id = id;
			System.out.println("Connected to server, and server offerd id:" + id );
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(s != null) {
				try {
					s.close();
					s = null;
				} catch (IOException e) {		
					e.printStackTrace();
				}
			}
		}
		TankNewMessage msg = new TankNewMessage(tc.myTank);
		send(msg);
		new Thread(new UDPReceiveThread()).start();
	}
	
	public void send(TankNewMessage msg) {
		msg.send(ds, "127.0.0.1", TankServer.UDP_PORT);
		
	}
	
	private class UDPReceiveThread implements Runnable {
		byte[] buf = new byte[1024];
		
		public void run() {
			
				while(ds != null) {
					DatagramPacket dp = new DatagramPacket(buf, buf.length);
					try {
						ds.receive(dp);
						parse(dp);
	System.out.println("A data package is received ");
					} catch (IOException e) {
						e.printStackTrace();
					}				
				}
		}
		private void parse(DatagramPacket dp) {
			ByteArrayInputStream bais = new ByteArrayInputStream(buf, 0, dp.getLength());
			DataInputStream dis = new DataInputStream(bais);
			TankNewMessage tnm = new TankNewMessage();
			tnm.parse(dis);
		}
		
	}

	
}
