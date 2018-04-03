package biggerfish;

import java.io.IOException;
import java.net.InetAddress;
import java.util.UUID;

import ray.networking.server.GameConnectionServer;
import ray.networking.server.IClientInfo;

public class BiggerFishServer extends GameConnectionServer<UUID> {
	
	public BiggerFishServer(int localPort) throws IOException {
		super(localPort, ProtocolType.UDP);
	}

	@Override
	public void processPacket(Object obj, InetAddress senderIP, int senderPort) {
		if(obj instanceof String) {
			String msg = (String) obj;
			String[] tokens = msg.split(",");
			if(tokens.length > 0) {
				if(tokens[0].equals("join")) {
					try {
						IClientInfo clientInfo = getServerSocket().createClientInfo(senderIP, senderPort);
						UUID clientID = UUID.fromString(tokens[1]);
						addClient(clientInfo, clientID);
						sendJoinedMessage(clientID, true);
					}
					catch(IOException e) {
						e.printStackTrace();
					}
				}
				else if(tokens[0].equals("create")) {
					UUID clientID = UUID.fromString(tokens[1]);
					String[] pos = new String[]{ tokens[2], tokens[3], tokens[4] };
					sendCreateMessages(clientID, pos);
				}
				else if(tokens[0].equals("leave")) {
					UUID clientID = UUID.fromString(tokens[1]);
					sendLeaveMessages(clientID);
					removeClient(clientID);
				}
			}
		}
	}
	
	public void sendJoinedMessage(UUID clientID, boolean success) {
		try {
			String msg = "join," + (success ? "success" : "failure");
			sendPacket(msg, clientID);
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}

	public void sendLeaveMessages(UUID clientID) {
		try {
			String msg = "leave";
			sendPacket(msg, clientID);
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public void sendCreateMessages(UUID clientID, String[] position) {
		try {
			String msg = "create," + clientID.toString() + "," + position[0] + "," + position[1] + "," + position[2];
			forwardPacketToAll(msg, clientID);
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
}
