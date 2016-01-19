import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;


public class TankNewMessage implements Msg{
	int msgType = Msg.TANK_NEW_MSG;
	Tank tank;
	TankClient tc;
	public TankNewMessage(Tank tank) {
		this.tank = tank;
		
	}
	public TankNewMessage(TankClient tc) {
		this.tc = tc;
	}
	
	
	public void send(DatagramSocket ds, String IP, int udpPort) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		try {
			dos.writeInt(msgType);
			dos.writeInt(tank.id);
			dos.writeInt(tank.x);
			dos.writeInt(tank.y);
			dos.writeInt(tank.dir.ordinal());
			dos.writeBoolean(tank.isGood());
		} catch (IOException e) {
			e.printStackTrace();
		}
		byte[] buf = baos.toByteArray();
		try {
			DatagramPacket dop = new DatagramPacket(buf, buf.length, new InetSocketAddress(IP, udpPort));
			ds.send(dop);
		}
		catch(SocketException e) {
			e.printStackTrace();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}

	public void parse(DataInputStream dis) {
		try {
			int id = dis.readInt();
			if(tc.myTank.id == id) return;
			
			int x = dis.readInt();
			int y = dis.readInt();
			Dir dir = Dir.values()[dis.readInt()];
			boolean good = dis.readBoolean();
//System.out.println("id:" + id + "-x:" + x + "-y:" + y + "-dir:" + dir + "-good:" + good);
			boolean exist = false;
			for(int i = 0; i < tc.tanks.size(); i++) {
				Tank t = tc.tanks.get(i);
				if(t.id == id) {
					exist = true;
					break;
				}
			}
			if(!exist) {
				TankNewMessage tnMsg = new TankNewMessage(tc.myTank);
				tc.nc.send(tnMsg);
				Tank t = new Tank(x, y, good, dir, tc);			
				t.id = id;
				tc.tanks.add(t);
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
