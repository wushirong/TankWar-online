import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class TankServer {
	private static int ID = 100;
	public static final int TCP_PORT = 8888;
	public static final int UDP_PORT = 6666;
	List<Client> clients = new ArrayList<Client>();
	
	@SuppressWarnings("resource")
	public void start() {
		new Thread(new UDPThread()).start();;
		ServerSocket ss = null;
		
		try {
			ss = new ServerSocket(TCP_PORT);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		while(true) {
			Socket socket = null;
			try {
				socket = ss.accept();
				DataInputStream dis = new DataInputStream(socket.getInputStream());
				String IP = socket.getInetAddress().getHostAddress();
				int udpPort = dis.readInt();
				Client c = new Client(IP, udpPort);
				clients.add(c);
				DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
				dos.writeInt(ID++); 
				System.out.println("A Client is connected " + socket.getInetAddress() + ":" + socket.getPort());
			}
			catch(IOException e) {
				e.printStackTrace();
			}
			finally {
				if(socket != null) {
					try {
						socket.close();
						socket = null;
					}
					catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	public static void main(String[] args) {
		new TankServer().start();
		
	}
	private class Client {
		String IP;
		int udpPort;
		public Client(String IP, int port) {
			this.IP = IP;
			this.udpPort = port;
		}
	}
	
	
	private class UDPThread implements Runnable{
		byte[] buf = new byte[1024];
		
		public void run() {
			DatagramSocket ds = null;
			try {
				ds = new DatagramSocket(UDP_PORT);

			} catch (SocketException e) {
				e.printStackTrace();
			}
System.out.println("A thread is started at port: " + UDP_PORT);
			while(ds != null) {
				DatagramPacket dp = new DatagramPacket(buf, buf.length);
				try {
					ds.receive(dp);
System.out.println("A package is received");
					for(int i = 0; i < clients.size(); i++) {
						Client c = clients.get(i);
						dp.setSocketAddress(new InetSocketAddress(c.IP, c.udpPort));
						ds.send(dp);								
					}
System.out.println("A data package is received ");
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
		}
		
	}
}
