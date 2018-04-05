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
                    System.out.println("Received JOIN : " + tokens[1]);

                    if (tokens[1].equals("success")) {
                        game.setConnected(true);
                        sendCreateMessage(game.getPlayer());
                    }
                }
                else if (tokens[0].equals("leave")) {
                    System.out.println("Received LEAVE");

                    UUID playerId = UUID.fromString(tokens[1]);
                    removePlayer(playerId);
                }
                else if (tokens[0].equals("create")) {
                    System.out.println("Received CREATE");

                    UUID playerId = UUID.fromString(tokens[1]);
                    Vector3f position = new Vector3f(Float.parseFloat(tokens[2]), Float.parseFloat(tokens[3]), Float.parseFloat(tokens[4]));
                    Matrix3f rotation = new Matrix3f(
                            Float.parseFloat(tokens[5]), Float.parseFloat(tokens[6]), Float.parseFloat(tokens[7]),
                            Float.parseFloat(tokens[8]), Float.parseFloat(tokens[9]), Float.parseFloat(tokens[10]),
                            Float.parseFloat(tokens[11]), Float.parseFloat(tokens[12]), Float.parseFloat(tokens[13]));
                    float scale = Float.parseFloat(tokens[14]);
                    createPlayer(playerId, position, rotation, scale);
                }
                else if (tokens[0].equals("update")) {
                    System.out.println("Received UPDATE");

                    UUID playerId = UUID.fromString(tokens[1]);
                    Vector3f position = new Vector3f(Float.parseFloat(tokens[2]), Float.parseFloat(tokens[3]), Float.parseFloat(tokens[4]));
                    Matrix3f rotation = new Matrix3f(
                            Float.parseFloat(tokens[5]), Float.parseFloat(tokens[6]), Float.parseFloat(tokens[7]),
                            Float.parseFloat(tokens[8]), Float.parseFloat(tokens[9]), Float.parseFloat(tokens[10]),
                            Float.parseFloat(tokens[11]), Float.parseFloat(tokens[12]), Float.parseFloat(tokens[13]));
                    float scale = Float.parseFloat(tokens[14]);
                    updatePlayer(playerId, position, rotation, scale);
                }
            }
        }
    }

    private void createPlayer(UUID playerId, Vector3fc position, Matrix3fc rotation, float scale) {
        PlayerEntity playerEntity = new PlayerEntity(playerId, engine.getSceneManager(), engine.getSceneManager().getRootSceneNode(), game.getPlayer().getModel());
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

    public void sendJoinMessage() {
        System.out.println("Sending JOIN");
        try {
            sendPacket("join;" + id.toString());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendLeaveMessage() {
        System.out.println("Sending LEAVE");
        try {
            sendPacket("leave;" + id.toString());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendCreateMessage(PlayerEntity playerEntity) {
        System.out.println("Sending CREATE");
        try {
            Vector3fc pos = playerEntity.getLocalPosition();
            Matrix3fc rot = playerEntity.getLocalRotation();
            float scale = playerEntity.getLocalScale().x();

            String position = pos.x() + ";" + pos.y() + ";" + pos.z();
            String rotation = rot.m00() + ";" + rot.m01() + ";" + rot.m02() + ";" +
                            rot.m10() + ";" + rot.m11() + ";" + rot.m12() + ";" +
                            rot.m20() + ";" + rot.m21() + ";" + rot.m22();

            sendPacket("create;" + id.toString() + ";" + position + ";" + rotation + ";" + scale);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendUpdateMessage(PlayerEntity playerEntity) {
        System.out.println("Sending UPDATE");
        try {
            Vector3fc pos = playerEntity.getLocalPosition();
            Matrix3fc rot = playerEntity.getLocalRotation();
            float scale = playerEntity.getLocalScale().x();

            String position = pos.x() + ";" + pos.y() + ";" + pos.z();
            String rotation = rot.m00() + ";" + rot.m01() + ";" + rot.m02() + ";" +
                            rot.m10() + ";" + rot.m11() + ";" + rot.m12() + ";" +
                            rot.m20() + ";" + rot.m21() + ";" + rot.m22();

            sendPacket("update;" + id.toString() + ";" + position + ";" + rotation + ";" + scale);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
