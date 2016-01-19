import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;


public class TankMoveMsg implements Msg {
	int msgType = Msg.TANK_MOVE_MSG;
	int id;
	Dir dir;
	TankClient tc;
	public TankMoveMsg(int id, Dir dir) {
		super();
		this.id = id;
		this.dir = dir;
	}
	public TankMoveMsg(TankClient tc) {
		this.tc = tc;
		
	}
	@Override
	public void send(DatagramSocket ds, String IP, int udpPort) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		try {
			dos.writeInt(msgType);
			dos.writeInt(id);
			dos.writeInt(dir.ordinal());
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
	@Override
	public void parse(DataInputStream dis) {
		try {
			int id = dis.readInt();
			if(tc.myTank.id == id) return;

			Dir dir = Dir.values()[dis.readInt()];
			//boolean good = dis.readBoolean();
//System.out.println("id:" + id + "-x:" + x + "-y:" + y + "-dir:" + dir + "-good:" + good);
			boolean exist = false;
			for(int i = 0; i < tc.tanks.size(); i++) {
				Tank t = tc.tanks.get(i);
				if(t.id == id) {
					t.dir = dir;
					exist = true;
					break;
				}
			}
			//需继续处理加新坦克的问题
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
