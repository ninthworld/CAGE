package biggerfish.ai;

import biggerfish.BiggerFishGame;
import biggerfish.fish.FishEntity;
import biggerfish.fish.FishManager;
import biggerfish.networking.PlayerEntity;
import cage.core.model.ExtModel;
import cage.core.scene.InstancedSceneEntity;
import cage.core.scene.Node;
import cage.core.scene.SceneManager;
import cage.core.scene.SceneNode;
import cage.core.utils.math.Direction;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import org.joml.*;

import java.util.Iterator;

public class AIManager {

    private static final long SEED = 1234567890123456789L;

    private final Vector2f[] anchovyOrigins = new Vector2f[]{
            new Vector2f(-528.0f, -236.0f),
            new Vector2f(-128.0f, -616.0f),
            new Vector2f(404.0f, -692.0f)
    };
    private final float[] anchovyRadius = new float[]{
            16.0f,
            16.0f,
            16.0f
    };

    private final Vector2f[] clownfishOrigins = new Vector2f[]{
            new Vector2f(-528.0f, -236.0f),
            new Vector2f(-128.0f, -616.0f),
            new Vector2f(404.0f, -692.0f)
    };
    private final float[] clownfishRadius = new float[]{
            64.0f,
            64.0f,
            64.0f
    };

    private final Vector2f[] tilapiaOrigins = new Vector2f[]{
            new Vector2f(-528.0f, -236.0f),
            new Vector2f(-128.0f, -616.0f),
            new Vector2f(404.0f, -692.0f)
    };
    private final float[] tilapiaRadius = new float[]{
            128.0f,
            128.0f,
            128.0f
    };

    private final Vector2f[] tunaOrigins = new Vector2f[]{
            new Vector2f(-528.0f, -236.0f),
            new Vector2f(-128.0f, -616.0f),
            new Vector2f(404.0f, -692.0f)
    };
    private final float[] tunaRadius = new float[]{
            512.0f,
            512.0f,
            512.0f
    };

    private Random random;
    private SceneManager sceneManager;
    private DiscreteDynamicsWorld dynamicsWorld;
    private BiggerFishGame game;

    private ExtModel[] fishModels;
    private SceneNode[] fishRootNodes;
    private InstancedSceneEntity[] fishInstances;
    private int[] fishCounts;

    public AIManager(SceneManager sceneManager, FishManager fishManager, DiscreteDynamicsWorld dynamicsWorld, BiggerFishGame game) {
        this.random = new Random(SEED);
        this.sceneManager = sceneManager;
        this.dynamicsWorld = dynamicsWorld;
        this.game = game;

        this.fishModels = new ExtModel[FishManager.NUM_FISH_TYPES];
        this.fishModels[FishManager.ANCHOVY_TYPE] = fishManager.getAnchovyModel();
        this.fishModels[FishManager.CLOWNFISH_TYPE] = fishManager.getClownfishModel();
        this.fishModels[FishManager.TILAPIA_TYPE] = fishManager.getTilapiaModel();
        this.fishModels[FishManager.TUNA_TYPE] = fishManager.getTunaModel();

        this.fishRootNodes = new SceneNode[FishManager.NUM_FISH_TYPES];
        this.fishInstances = new InstancedSceneEntity[FishManager.NUM_FISH_TYPES];
        for(int i=0; i<FishManager.NUM_FISH_TYPES; ++i) {
            this.fishRootNodes[i] = new SceneNode(this.sceneManager, null);
            this.fishInstances[i] = new InstancedSceneEntity(this.sceneManager, this.sceneManager.getRootSceneNode(), this.fishModels[i]);
        }

        this.fishCounts = new int[FishManager.NUM_FISH_TYPES];

        // Anchovy
        final int ANCHOVY_SCHOOL_COUNT = 8;
        for(int i=0; i<anchovyOrigins.length; ++i) {
            for(int j=0; j<16; ++j) {
                Vector2f origin2 = anchovyOrigins[i].add(new Vector2f(random.nextFloat(), random.nextFloat()).mul(2.0f).sub(1.0f, 1.0f).mul(anchovyRadius[i]));
                Vector3f origin3 = new Vector3f(origin2.x, game.getTerrainHeightAt(origin2.x, origin2.y) + 1.0f, origin2.y);
                AISchoolNode school = new AISchoolNode(sceneManager, this.fishRootNodes[FishManager.ANCHOVY_TYPE], FishManager.ANCHOVY_TYPE, origin3, anchovyRadius[i], ANCHOVY_SCHOOL_COUNT, 1.0f, random);
                for(int k=0; k<ANCHOVY_SCHOOL_COUNT; ++k) {
                    addFish(school);
                }
            }
        }

        // Clownfish
        final int CLOWNFISH_SCHOOL_COUNT = 8;
        for(int i=0; i<clownfishOrigins.length; ++i) {
            for(int j=0; j<8; ++j) {
                Vector2f origin2 = clownfishOrigins[i].add(new Vector2f(random.nextFloat(), random.nextFloat()).mul(2.0f).sub(1.0f, 1.0f).mul(clownfishRadius[i]));
                Vector3f origin3 = new Vector3f(origin2.x, game.getTerrainHeightAt(origin2.x, origin2.y) + 1.0f, origin2.y);
                AISchoolNode school = new AISchoolNode(sceneManager, this.fishRootNodes[FishManager.CLOWNFISH_TYPE], FishManager.CLOWNFISH_TYPE, origin3, clownfishRadius[i], CLOWNFISH_SCHOOL_COUNT, 1.0f, random);
                for(int k=0; k<CLOWNFISH_SCHOOL_COUNT; ++k) {
                    addFish(school);
                }
            }
        }

        // Clownfish
        final int TILAPIA_SCHOOL_COUNT = 8;
        for(int i=0; i<tilapiaOrigins.length; ++i) {
            for(int j=0; j<8; ++j) {
                Vector2f origin2 = tilapiaOrigins[i].add(new Vector2f(random.nextFloat(), random.nextFloat()).mul(2.0f).sub(1.0f, 1.0f).mul(tilapiaRadius[i]));
                Vector3f origin3 = new Vector3f(origin2.x, game.getTerrainHeightAt(origin2.x, origin2.y) + 2.0f, origin2.y);
                AISchoolNode school = new AISchoolNode(sceneManager, this.fishRootNodes[FishManager.TILAPIA_TYPE], FishManager.TILAPIA_TYPE, origin3, tilapiaRadius[i], TILAPIA_SCHOOL_COUNT, 2.0f, random);
                for(int k=0; k<TILAPIA_SCHOOL_COUNT; ++k) {
                    addFish(school);
                }
            }
        }

        // Tuna
        final int TUNA_SCHOOL_COUNT = 8;
        for(int i=0; i<tunaOrigins.length; ++i) {
            for(int j=0; j<8; ++j) {
                Vector2f origin2 = tunaOrigins[i].add(new Vector2f(random.nextFloat(), random.nextFloat()).mul(2.0f).sub(1.0f, 1.0f).mul(tunaRadius[i]));
                Vector3f origin3 = new Vector3f(origin2.x, game.getTerrainHeightAt(origin2.x, origin2.y) + 4.0f, origin2.y);
                AISchoolNode school = new AISchoolNode(sceneManager, this.fishRootNodes[FishManager.TUNA_TYPE], FishManager.TUNA_TYPE, origin3, tunaRadius[i], TUNA_SCHOOL_COUNT, 4.0f, random);
                for(int k=0; k<TUNA_SCHOOL_COUNT; ++k) {
                    addFish(school);
                }
            }
        }
    }

    public void updateFish(Vector3fc position, Matrix3fc rotation, Vector2fc headingTime, Vector2fc headingDelta, int type, int schoolIndex, int fishIndex) {
        if(fishRootNodes.length > type) {
            AISchoolNode school = (AISchoolNode) fishRootNodes[type].getNode(schoolIndex);
            if (fishIndex < school.getFishCount()) {
                while (school.getNodeCount() <= fishIndex) {
                    addFish(school);
                }
                AIFishEntity fish = (AIFishEntity) school.getNode(fishIndex);
                fish.setPosition(position);
                fish.setRotation(rotation);
                fish.setHeading(headingTime, headingDelta);
            }
        }
    }

    private void addFish(AISchoolNode school) {
        float mass = 0.0f;
        switch (school.getFishType()) {
            case FishManager.ANCHOVY_TYPE: mass = FishManager.ANCHOVY_MASS; break;
            case FishManager.CLOWNFISH_TYPE: mass = FishManager.CLOWNFISH_MASS; break;
            case FishManager.TILAPIA_TYPE: mass = FishManager.TILAPIA_MASS; break;
            case FishManager.TUNA_TYPE: mass = FishManager.TUNA_MASS; break;
        }

        new AIFishEntity(sceneManager, school, fishModels[school.getFishType()], school.getFishType(), school.getOrigin().add(school.getOffset(school.getNodeCount()), new Vector3f()), mass, dynamicsWorld);
        fishCounts[school.getFishType()]++;
    }

    public void eatFish(PlayerEntity player, AIFishEntity fish) {
        float playerMass = player.getMass();
        float fishMass = fish.getMass();
        if(playerMass > fishMass && fish.getParentNode() != null) {
            player.setMass(playerMass + fishMass * player.getEfficiency());
            removeFish(fish);
            game.playBiteSound();
        }
    }

    private void removeFish(AIFishEntity fish) {
        if(fish.getParentNode() != null) {
            if(game.isMultiplayer() && game.isConnected() && game.isHost()) {
                int type = fish.getFishType();

                int schoolIndex = 0;
                Iterator<Node> itSchool = fishRootNodes[type].getNodeIterator();
                while(itSchool.hasNext()) {
                    if (!itSchool.next().equals(fish.getParentNode())) schoolIndex++;
                    else break;
                }

                int fishIndex = 0;
                Iterator<Node> itFish = fish.getParentNode().getNodeIterator();
                while(itFish.hasNext()) {
                    if (!itFish.next().equals(fish)) fishIndex++;
                    else break;
                }

                game.getClient().sendRemoveFishMessage(type, schoolIndex, fishIndex);
            }

            fishCounts[fish.getFishType()]--;
            fish.getRigidBody().destroy();
            fish.getParentNode().removeNode(fish);
        }
    }

    public void removeFish(int type, int schoolIndex, int fishIndex) {
        if(fishRootNodes.length > type) {
            AISchoolNode school = (AISchoolNode) fishRootNodes[type].getNode(schoolIndex);
            if (fishIndex < school.getNodeCount()) {
                removeFish((AIFishEntity) school.getNode(fishIndex));
            }
        }
    }

    public void update(float deltaTime) {
        for(int i=0; i<fishRootNodes.length; ++i) {
            fishRootNodes[i].update(true);
            Matrix4f[] entityTransforms = new Matrix4f[fishCounts[i]];
            int index = 0;

            int schoolIndex = 0;
            Iterator<Node> itSchool = fishRootNodes[i].getNodeIterator();
            while(itSchool.hasNext()) {
                AISchoolNode school = (AISchoolNode) itSchool.next();

                if(!game.isMultiplayer() || !game.isConnected() || game.isHost()) {
                    school.setTickTime(school.getTickTime() + deltaTime);
                    if (school.getTickTime() >= school.getMaxTickTime()) {
                        Vector2f headingTime = new Vector2f(random.nextFloat() * (school.getMaxTickTime() / 2.0f), random.nextFloat() * (school.getMaxTickTime() / 4.0f));
                        Vector2f headingDelta = new Vector2f(random.nextFloat() < 0.5f ? -1.0f : 1.0f, random.nextFloat() < 0.5f ? -1.0f : 1.0f);
                        school.setMaxTickTime(random.nextFloat() * 8.0f + 4.0f);
                        school.setTickTime(0.0f);

                        if (school.getNodeCount() < school.getFishCount() / 4 && random.nextFloat() < 0.25f) {
                            for (int j = 0; j < school.getFishCount() - school.getNodeCount(); ++j) {
                                addFish(school);
                            }
                        }

                        int fishIndex = 0;
                        Iterator<Node> itChild = school.getNodeIterator();
                        while(itChild.hasNext()) {
                            Node child = itChild.next();
                            if(child instanceof AIFishEntity) {
                                AIFishEntity fish = (AIFishEntity)child;
                                fish.setHeading(headingTime, headingDelta);
                                if(game.isMultiplayer() && game.isConnected() && game.isHost()) {
                                    game.getClient().sendUpdateFishMessage(fish.getLocalPosition(), fish.getLocalRotation(), headingTime, headingDelta, fish.getFishType(), schoolIndex, fishIndex);
                                }
                                fishIndex++;
                            }
                        }
                    }
                }

                Iterator<Node> itChild = school.getNodeIterator();
                while(itChild.hasNext()) {
                    Node child = itChild.next();
                    if(child instanceof AIFishEntity) {
                        AIFishEntity fish = (AIFishEntity)child;
                        fish.updateDelta(deltaTime);
                        if(index < entityTransforms.length) {
                            entityTransforms[index++] = new Matrix4f(fish.getWorldTransform());
                        }
                    }
                }

                schoolIndex++;
            }

            fishInstances[i].setInstanceBuffer(entityTransforms);
        }
    }

    public int getNumFishTypes() {
        return FishManager.NUM_FISH_TYPES;
    }

    public int getFishCount(int type) {
        return fishCounts[type];
    }

    public SceneNode getFishNode(int type) {
        return fishRootNodes[type];
    }
}
