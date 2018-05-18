package biggerfish.networking;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import biggerfish.fish.FishManager;
import org.joml.*;
import ray.networking.server.GameConnectionServer;
import ray.networking.server.IClientInfo;

public class BiggerFishServer extends GameConnectionServer<UUID> {

    private List<PlayerEntity> players;
    private UUID host;

	public BiggerFishServer(int localPort) throws IOException {
		super(localPort, ProtocolType.UDP);
		this.players = new ArrayList<>();
		this.host = null;
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
						addClient(clientInfo, playerId);

                        System.out.println("Client Connected - " + senderIP + ":" + senderPort);

						sendJoinedMessage(playerId, true);
						for(PlayerEntity entity : players) {
						    sendCreatePlayerMessage(playerId, entity);
                        }

                        if(host == null) {
						    host = playerId;
						    sendHostMessage(playerId);
                        }
					}
					catch(IOException e) {
						e.printStackTrace();
					}
				}
				else if (tokens[0].equals("leave")) {
					UUID playerId = UUID.fromString(tokens[1]);
					broadcastLeaveMessage(playerId);
					Iterator<PlayerEntity> it = players.iterator();
					while(it.hasNext()) {
					    PlayerEntity entity = it.next();
					    if(entity.getUUID().equals(playerId)) {
					        players.remove(entity);
					        entity.destroy();
					        break;
                        }
                    }
                    if(host.equals(playerId)) {
					    if(players.isEmpty()) {
					        host = null;
                        }
                        else {
					        host = players.get(0).getUUID();
					        sendHostMessage(host);
                        }
                    }
					removeClient(playerId);
				}
                else if (tokens[0].equals("createPlayer")) {
                    UUID playerId = UUID.fromString(tokens[1]);
                    Vector3f position = new Vector3f(Float.parseFloat(tokens[2]), Float.parseFloat(tokens[3]), Float.parseFloat(tokens[4]));
                    Matrix3f rotation = new Matrix3f(
                            Float.parseFloat(tokens[5]), Float.parseFloat(tokens[6]), Float.parseFloat(tokens[7]),
                            Float.parseFloat(tokens[8]), Float.parseFloat(tokens[9]), Float.parseFloat(tokens[10]),
                            Float.parseFloat(tokens[11]), Float.parseFloat(tokens[12]), Float.parseFloat(tokens[13]));
                    float mass = Float.parseFloat(tokens[14]);
                    Vector3f linearVelocity = new Vector3f(Float.parseFloat(tokens[15]), Float.parseFloat(tokens[16]), Float.parseFloat(tokens[17]));
                    Vector3f angularVelocity = new Vector3f(Float.parseFloat(tokens[18]), Float.parseFloat(tokens[19]), Float.parseFloat(tokens[20]));
                    int type = Integer.parseInt(tokens[21]);
                    PlayerEntity entity = createPlayer(playerId, type, position, rotation, mass, linearVelocity, angularVelocity);
                    broadcastCreatePlayerMessage(playerId, entity);
                }
                else if (tokens[0].equals("updatePlayer")) {
                    UUID playerId = UUID.fromString(tokens[1]);
                    Vector3f position = new Vector3f(Float.parseFloat(tokens[2]), Float.parseFloat(tokens[3]), Float.parseFloat(tokens[4]));
                    Matrix3f rotation = new Matrix3f(
                            Float.parseFloat(tokens[5]), Float.parseFloat(tokens[6]), Float.parseFloat(tokens[7]),
                            Float.parseFloat(tokens[8]), Float.parseFloat(tokens[9]), Float.parseFloat(tokens[10]),
                            Float.parseFloat(tokens[11]), Float.parseFloat(tokens[12]), Float.parseFloat(tokens[13]));
                    float mass = Float.parseFloat(tokens[14]);
                    Vector3f linearVelocity = new Vector3f(Float.parseFloat(tokens[15]), Float.parseFloat(tokens[16]), Float.parseFloat(tokens[17]));
                    Vector3f angularVelocity = new Vector3f(Float.parseFloat(tokens[18]), Float.parseFloat(tokens[19]), Float.parseFloat(tokens[20]));
                    int type = Integer.parseInt(tokens[21]);
                    PlayerEntity entity = updatePlayer(playerId, type, position, rotation, mass, linearVelocity, angularVelocity);
                    broadcastUpdatePlayerMessage(playerId, entity);
                }
                else if (tokens[0].equals("updateFish")) {
                    Vector3f position = new Vector3f(Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2]), Float.parseFloat(tokens[3]));
                    Matrix3f rotation = new Matrix3f(
                            Float.parseFloat(tokens[4]), Float.parseFloat(tokens[5]), Float.parseFloat(tokens[6]),
                            Float.parseFloat(tokens[7]), Float.parseFloat(tokens[8]), Float.parseFloat(tokens[9]),
                            Float.parseFloat(tokens[10]), Float.parseFloat(tokens[11]), Float.parseFloat(tokens[12]));
                    Vector2f headingTime = new Vector2f(Float.parseFloat(tokens[13]), Float.parseFloat(tokens[14]));
                    Vector2f headingDelta = new Vector2f(Float.parseFloat(tokens[15]), Float.parseFloat(tokens[16]));
                    int type = Integer.parseInt(tokens[17]);
                    int schoolIndex = Integer.parseInt(tokens[18]);
                    int fishIndex = Integer.parseInt(tokens[19]);
                    broadcastUpdateFishMessage(position, rotation, headingTime, headingDelta, type, schoolIndex, fishIndex);
                }
                else if (tokens[0].equals("removeFish")) {
                    int type = Integer.parseInt(tokens[1]);
                    int schoolIndex = Integer.parseInt(tokens[2]);
                    int fishIndex = Integer.parseInt(tokens[3]);
                    broadcastRemoveFishMessage(type, schoolIndex, fishIndex);
                }
			}
		}
	}

    private PlayerEntity createPlayer(UUID playerId, int type, Vector3fc position, Matrix3fc rotation, float mass, Vector3fc linearVelocity, Vector3fc angularVelocity) {
	    PlayerEntity playerEntity = new PlayerEntity(playerId, null, null, null, type, new Vector3f(), mass, null);
	    playerEntity.setLocalPosition(position);
        playerEntity.setLocalRotation(rotation);
        playerEntity.setLinearVelocity(linearVelocity);
        playerEntity.setAngularVelocity(angularVelocity);
        playerEntity.setDummy(true);
        players.add(playerEntity);
        return playerEntity;
    }

    private PlayerEntity updatePlayer(UUID playerId, int type, Vector3fc position, Matrix3fc rotation, float mass, Vector3fc linearVelocity, Vector3fc angularVelocity) {
        PlayerEntity playerEntity = null;
        for(PlayerEntity player : players) {
            if(player.getUUID().equals(playerId)) {
                playerEntity = player;
            }
        }

        if(playerEntity != null) {
            playerEntity.setLocalPosition(position);
            playerEntity.setLocalRotation(rotation);
            playerEntity.setMass(mass);
            playerEntity.setLinearVelocity(linearVelocity);
            playerEntity.setAngularVelocity(angularVelocity);
        }
        else {
            playerEntity = createPlayer(playerId, type, position, rotation, mass, linearVelocity, angularVelocity);
        }

        return playerEntity;
    }

    private String getPlayerEntityMessage(PlayerEntity playerEntity) {
        Vector3fc pos = playerEntity.getLocalPosition();
        Matrix3fc rot = playerEntity.getLocalRotation();
        float mass = playerEntity.getMass();
        javax.vecmath.Vector3f linearVel = playerEntity.getRigidBody().getLinearVelocity(new javax.vecmath.Vector3f());
        javax.vecmath.Vector3f angularVel = playerEntity.getRigidBody().getAngularVelocity(new javax.vecmath.Vector3f());
        int type = playerEntity.getFishType();

        String position = pos.x() + ";" + pos.y() + ";" + pos.z();
        String rotation = rot.m00() + ";" + rot.m01() + ";" + rot.m02() + ";" +
                rot.m10() + ";" + rot.m11() + ";" + rot.m12() + ";" +
                rot.m20() + ";" + rot.m21() + ";" + rot.m22();
        String linear = linearVel.x + ";" + linearVel.y + ";" + linearVel.z;
        String angular = angularVel.x + ";" + angularVel.y + ";" + angularVel.z;

        return playerEntity.getUUID() + ";" + position + ";" + rotation + ";" + mass + ";" + linear + ";" + angular + ";" + type;
    }

    public void sendJoinedMessage(UUID playerId, boolean success) {
		try {
			sendPacket("join;" + (success ? "success" : "failure"), playerId);
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}

    public void broadcastLeaveMessage(UUID playerId) {
        try {
            forwardPacketToAll("leave;" + playerId, playerId);
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void sendHostMessage(UUID playerId) {
	    try {
	        sendPacket("host", playerId);
        }
        catch (IOException e) {
	        e.printStackTrace();
        }
    }

    public void sendCreatePlayerMessage(UUID playerId, PlayerEntity playerEntity) {
        try {
            if(playerEntity != null) sendPacket("createPlayer;" + getPlayerEntityMessage(playerEntity), playerId);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void broadcastCreatePlayerMessage(UUID playerId, PlayerEntity playerEntity) {
        try {
            if(playerEntity != null) forwardPacketToAll("createPlayer;" + getPlayerEntityMessage(playerEntity), playerId);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void broadcastUpdatePlayerMessage(UUID playerId, PlayerEntity playerEntity) {
        try {
            if(playerEntity != null) forwardPacketToAll("updatePlayer;" + getPlayerEntityMessage(playerEntity), playerId);
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void broadcastUpdateFishMessage(Vector3fc position, Matrix3fc rotation, Vector2fc headingTime, Vector2fc headingDelta, int type, int schoolIndex, int fishIndex) {
        try {
            String pos = position.x() + ";" + position.y() + ";" + position.z();
            String rot = rotation.m00() + ";" + rotation.m01() + ";" + rotation.m02() + ";" +
                    rotation.m10() + ";" + rotation.m11() + ";" + rotation.m12() + ";" +
                    rotation.m20() + ";" + rotation.m21() + ";" + rotation.m22();
            String time = headingTime.x() + ";" + headingTime.y();
            String delta = headingDelta.x() + ";" + headingDelta.y();
            forwardPacketToAll("updateFish;" + pos + ";" + rot + ";" + time + ";" + delta + ";" + type + ";" + schoolIndex + ";" + fishIndex, host);
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void broadcastRemoveFishMessage(int type, int schoolIndex, int fishIndex) {
        try {
            forwardPacketToAll("removeFish;" + type + ";" + schoolIndex + ";" + fishIndex, host);
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
