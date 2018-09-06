package biggerfish.ai;

import cage.core.scene.Node;
import cage.core.scene.SceneManager;
import cage.core.scene.SceneNode;
import org.joml.Random;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class AISchoolNode extends SceneNode {

    private Vector3f origin;
    private float originRadius;
    private Vector3f[] offset;
    private float offsetRadius;
    private int fishCount;
    private int type;
    private float tickTime;
    private float maxTickTime;

    public AISchoolNode(SceneManager sceneManager, Node parent, int type, Vector3fc origin, float originRadius, int fishCount, float offsetRadius, Random random) {
        super(sceneManager, parent);
        this.origin = new Vector3f(origin);
        this.originRadius = originRadius;
        this.offsetRadius = offsetRadius;
        this.fishCount = fishCount;
        this.type = type;
        this.tickTime = 0.0f;
        this.maxTickTime = 0.0f;

        this.offset = new Vector3f[fishCount];
        for(int i=0; i<fishCount; ++i) {
            this.offset[i] = new Vector3f(random.nextFloat(), random.nextFloat(), random.nextFloat()).mul(2.0f).sub(1.0f, 1.0f, 1.0f).mul(offsetRadius, offsetRadius, offsetRadius);
        }
    }

    public Vector3fc getOffset(int index) {
        return offset[index];
    }

    public Vector3fc getOrigin() {
        return origin;
    }

    public void setOrigin(Vector3fc origin) {
        this.origin.set(origin);
    }

    public float getOriginRadius() {
        return originRadius;
    }

    public void setOriginRadius(float originRadius) {
        this.originRadius = originRadius;
    }

    public float getOffsetRadius() {
        return offsetRadius;
    }

    public void setOffsetRadius(float offsetRadius) {
        this.offsetRadius = offsetRadius;
    }

    public int getFishType() {
        return type;
    }

    public void setFishType(int type) {
        this.type = type;
    }

    public float getTickTime() {
        return tickTime;
    }

    public void setTickTime(float tickTime) {
        this.tickTime = tickTime;
    }

    public float getMaxTickTime() {
        return maxTickTime;
    }

    public void setMaxTickTime(float maxTickTime) {
        this.maxTickTime = maxTickTime;
    }

    public int getFishCount() {
        return fishCount;
    }

    public void setFishCount(int fishCount) {
        this.fishCount = fishCount;
    }
}
