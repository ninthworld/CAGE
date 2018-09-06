package cage.core.scene;

import cage.core.model.Model;
import cage.core.utils.math.AABB;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

public class InstancedSceneEntity extends SceneEntity {

    private int instanceCount;
    private FloatBuffer instanceBuffer;

    public InstancedSceneEntity(SceneManager sceneManager, Node parent, Model model) {
        super(sceneManager, parent, model, new AABB(new Vector3f(-10000, -10000, -10000), new Vector3f(10000, 10000, 10000)));
        this.instanceCount = 0;
        this.instanceBuffer = BufferUtils.createFloatBuffer(0);
    }

    public int getInstanceCount() {
        return instanceCount;
    }

    public void setInstanceBuffer(Matrix4f[] instanceTransforms) {
        if(instanceTransforms.length != instanceCount) {
            instanceCount = instanceTransforms.length;
            instanceBuffer = BufferUtils.createFloatBuffer(instanceCount * 16);
        }

        for(int i=0; i<instanceTransforms.length; ++i) {
            instanceTransforms[i].get(i * 16, instanceBuffer);
        }
    }

    @Override
    public FloatBuffer readData() {
        return instanceBuffer;
    }
}
