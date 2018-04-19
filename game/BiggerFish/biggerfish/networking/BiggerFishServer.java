package biggerfish.networking;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.joml.Matrix3f;
import org.joml.Matrix3fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import ray.networking.server.GameConnectionServer;
import ray.networking.server.IClientInfo;

public class BiggerFishServer extends GameConnectionServer<UUID> {

    private List<PlayerEntity> players;

	public BiggerFishServer(int localPort) throws IOException {
		super(localPort, ProtocolType.UDP);
		this.players = new ArrayList<>();
	}

	@Override
	public void processPacket(Object obj, InetAddress senderIP, int senderPort) {
		if(obj instanceof String) {
			String msg = (String) obj;
			String[] tokens = msg.split(";");
			if(tokens.length > 0) {
				if (tokens[0].equals("join")) {
					try {
						IClientInfo clientInfo = getServerSocket().createClientInfo(senderIP, senderPort);
						UUID playerId = UUID.fromString(tokens[1]);
                        System.out.println("Received JOIN from " + playerId.toString());

						addClient(clientInfo, playerId);
						sendJoinedMessage(playerId, true);
						for(PlayerEntity player : players) {
						    sendCreateMessage(playerId, player);
                        }
					}
					catch(IOException e) {
						e.printStackTrace();
					}
				}
				else if (tokens[0].equals("leave")) {
					UUID playerId = UUID.fromString(tokens[1]);
                    System.out.println("Received LEAVE from " + playerId.toString());

                    removePlayer(playerId);

					sendLeaveMessages(playerId);
					removeClient(playerId);
				}
                else if (tokens[0].equals("create")) {
                    UUID playerId = UUID.fromString(tokens[1]);
                    System.out.println("Received CREATE from " + playerId.toString());

                    Vector3f position = new Vector3f(Float.parseFloat(tokens[2]), Float.parseFloat(tokens[3]), Float.parseFloat(tokens[4]));
                    Matrix3f rotation = new Matrix3f(
                            Float.parseFloat(tokens[5]), Float.parseFloat(tokens[6]), Float.parseFloat(tokens[7]),
                            Float.parseFloat(tokens[8]), Float.parseFloat(tokens[9]), Float.parseFloat(tokens[10]),
                            Float.parseFloat(tokens[11]), Float.parseFloat(tokens[12]), Float.parseFloat(tokens[13]));
                    float scale = Float.parseFloat(tokens[14]);

                    createPlayer(playerId, position, rotation, scale);

                    sendCreateMessages(playerId, position, rotation, scale);
                }
                else if (tokens[0].equals("update")) {
                    UUID playerId = UUID.fromString(tokens[1]);
                    System.out.println("Received UPDATE from " + playerId.toString());

                    Vector3f position = new Vector3f(Float.parseFloat(tokens[2]), Float.parseFloat(tokens[3]), Float.parseFloat(tokens[4]));
                    Matrix3f rotation = new Matrix3f(
                            Float.parseFloat(tokens[5]), Float.parseFloat(tokens[6]), Float.parseFloat(tokens[7]),
                            Float.parseFloat(tokens[8]), Float.parseFloat(tokens[9]), Float.parseFloat(tokens[10]),
                            Float.parseFloat(tokens[11]), Float.parseFloat(tokens[12]), Float.parseFloat(tokens[13]));
                    float scale = Float.parseFloat(tokens[14]);

                    updatePlayer(playerId, position, rotation, scale);

                    sendUpdateMessages(playerId, position, rotation, scale);
                }
			}
		}
	}

    private void createPlayer(UUID playerId, Vector3fc position, Matrix3fc rotation, float scale) {
        PlayerEntity playerEntity = new PlayerEntity(playerId, null, null, null);
        playerEntity.setLocalPosition(position);
        playerEntity.setLocalRotation(rotation);
        playerEntity.setLocalScale(scale, scale, scale);
        players.add(playerEntity);
    }

    private void removePlayer(UUID playerId) {
        PlayerEntity playerEntity = null;
        for(PlayerEntity player : players) {
            if(player.getUUID().equals(playerId)) {
                playerEntity = player;
            }
        }

        if(playerEntity != null) {
            players.remove(playerEntity);
            playerEntity.destroy();
        }
    }

    private void updatePlayer(UUID playerId, Vector3fc position, Matrix3fc rotation, float scale) {
        PlayerEntity playerEntity = null;
        for(PlayerEntity player : players) {
            if(player.getUUID().equals(playerId)) {
                playerEntity = player;
            }
        }

        if(playerEntity != null) {
            playerEntity.setLocalPosition(position);
            playerEntity.setLocalRotation(rotation);
            playerEntity.setLocalScale(scale, scale, scale);
        }
    }

	public void sendJoinedMessage(UUID playerId, boolean success) {
        System.out.println("Sending JOIN to " + playerId);
		try {
			sendPacket("join;" + (success ? "success" : "failure"), playerId);
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}

    private void sendCreateMessage(UUID playerId, PlayerEntity playerEntity) {
        System.out.println("Sending CREATE to " + playerId);
        try {
            Vector3fc pos = playerEntity.getLocalPosition();
            Matrix3fc rot = playerEntity.getLocalRotation();
            float scale = playerEntity.getLocalScale().x();

            String position = pos.x() + ";" + pos.y() + ";" + pos.z();
            String rotation = rot.m00() + ";" + rot.m01() + ";" + rot.m02() + ";" +
                    rot.m10() + ";" + rot.m11() + ";" + rot.m12() + ";" +
                    rot.m20() + ";" + rot.m21() + ";" + rot.m22();
            sendPacket("create;" + playerEntity.getUUID() + ";" + position + ";" + rotation + ";" + scale, playerId);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

	public void sendLeaveMessages(UUID playerId) {
        System.out.println("Sending LEAVE");
		try {
            forwardPacketToAll("leave;" + playerId, playerId);
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}

    private void sendCreateMessages(UUID playerId, Vector3fc pos, Matrix3fc rot, float scale) {
        System.out.println("Sending CREATE");
        try {
            String position = pos.x() + ";" + pos.y() + ";" + pos.z();
            String rotation = rot.m00() + ";" + rot.m01() + ";" + rot.m02() + ";" +
                            rot.m10() + ";" + rot.m11() + ";" + rot.m12() + ";" +
                            rot.m20() + ";" + rot.m21() + ";" + rot.m22();
            forwardPacketToAll("create;" + playerId + ";" + position + ";" + rotation + ";" + scale, playerId);
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

    private void sendUpdateMessages(UUID playerId, Vector3fc pos, Matrix3fc rot, float scale) {
        System.out.println("Sending UPDATE");
        try {
            String position = pos.x() + ";" + pos.y() + ";" + pos.z();
            String rotation = rot.m00() + ";" + rot.m01() + ";" + rot.m02() + ";" +
                            rot.m10() + ";" + rot.m11() + ";" + rot.m12() + ";" +
                            rot.m20() + ";" + rot.m21() + ";" + rot.m22();
            forwardPacketToAll("update;" + playerId + ";" + position + ";" + rotation + ";" + scale, playerId);
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
	    if(args.length > 0) {
            try {
                new BiggerFishServer(Integer.parseInt(args[0]));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
