package biggerfish.networking;

import biggerfish.BiggerFishGame;
import cage.core.engine.Engine;
import org.joml.*;
import ray.networking.client.GameConnectionClient;

import java.io.IOException;
import java.net.InetAddress;
import java.util.*;

public class BiggerFishClient extends GameConnectionClient {

    private BiggerFishGame game;
    private Engine engine;
    private UUID id;
    private List<PlayerEntity> players;

    public BiggerFishClient(InetAddress remoteAddr, int remotePort, ProtocolType protocolType, BiggerFishGame game, Engine engine) throws IOException {
        super(remoteAddr, remotePort, protocolType);
        this.game = game;
        this.engine = engine;
        this.id = UUID.randomUUID();
        this.players = new ArrayList<>();
    }

    @Override
    protected void processPacket(Object obj) {
        if(obj instanceof String) {
            String msg = (String) obj;
            String[] tokens = msg.split(";");
            if (tokens.length > 0) {
                if (tokens[0].equals("join")) {
                    if (tokens[1].equals("success")) {
                        System.out.println("Connected to Server");
                        game.setConnected(true);
                    }
                }
                else if (tokens[0].equals("leave")) {
                    UUID playerId = UUID.fromString(tokens[1]);
                    Iterator<PlayerEntity> it = players.iterator();
                    while(it.hasNext()) {
                        PlayerEntity entity = it.next();
                        if(entity.getUUID().equals(playerId)) {
                            players.remove(entity);
                            entity.destroy();
                            break;
                        }
                    }
                }
                else if (tokens[0].equals("host")) {
                    game.setHost(true);
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
                    createPlayer(playerId, type, position, rotation, mass, linearVelocity, angularVelocity);
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
                    updatePlayer(playerId, type, position, rotation, mass, linearVelocity, angularVelocity);
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
                    game.getAIManager().updateFish(position, rotation, headingTime, headingDelta, type, schoolIndex, fishIndex);
                }
                else if (tokens[0].equals("removeFish")) {
                    int type = Integer.parseInt(tokens[1]);
                    int schoolIndex = Integer.parseInt(tokens[2]);
                    int fishIndex = Integer.parseInt(tokens[3]);
                    game.getAIManager().removeFish(type, schoolIndex, fishIndex);
                }
            }
        }
    }

    private PlayerEntity createPlayer(UUID playerId, int type, Vector3fc position, Matrix3fc rotation, float mass, Vector3fc linearVelocity, Vector3fc angularVelocity) {
        PlayerEntity playerEntity = new PlayerEntity(playerId, engine.getSceneManager(), engine.getSceneManager().getRootSceneNode(), game.getFishManager().createPlayerModel(type), type, position, mass, game.getDynamicsWorld());
        playerEntity.setRotation(rotation);
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
            playerEntity.setPosition(position);
            playerEntity.setRotation(rotation);
            playerEntity.setMass(mass);
            playerEntity.setLinearVelocity(linearVelocity);
            playerEntity.setAngularVelocity(angularVelocity);
            playerEntity.setParentNode(engine.getSceneManager().getRootSceneNode());
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

        return id.toString() + ";" + position + ";" + rotation + ";" + mass + ";" + linear + ";" + angular + ";" + type;
    }

    public void sendJoinMessage() {
        try {
            sendPacket("join;" + id.toString());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendLeaveMessage() {
        try {
            sendPacket("leave;" + id.toString());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendCreatePlayerMessage(PlayerEntity playerEntity) {
        try {
            sendPacket("createPlayer;" + getPlayerEntityMessage(playerEntity));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendUpdatePlayerMessage(PlayerEntity playerEntity) {
        try {
            sendPacket("updatePlayer;" + getPlayerEntityMessage(playerEntity));
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void sendUpdateFishMessage(Vector3fc position, Matrix3fc rotation, Vector2fc headingTime, Vector2fc headingDelta, int type, int schoolIndex, int fishIndex) {
        try {
            String pos = position.x() + ";" + position.y() + ";" + position.z();
            String rot = rotation.m00() + ";" + rotation.m01() + ";" + rotation.m02() + ";" +
                    rotation.m10() + ";" + rotation.m11() + ";" + rotation.m12() + ";" +
                    rotation.m20() + ";" + rotation.m21() + ";" + rotation.m22();
            String time = headingTime.x() + ";" + headingTime.y();
            String delta = headingDelta.x() + ";" + headingDelta.y();
            sendPacket("updateFish;" + pos + ";" + rot + ";" + time + ";" + delta + ";" + type + ";" + schoolIndex + ";" + fishIndex);
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void sendRemoveFishMessage(int type, int schoolIndex, int fishIndex) {
        try {
            sendPacket("removeFish;" + type + ";" + schoolIndex + ";" + fishIndex);
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }
}
