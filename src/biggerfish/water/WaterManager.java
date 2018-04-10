package biggerfish.water;

import biggerfish.quadtree.QuadTree;
import cage.core.graphics.GraphicsDevice;
import cage.core.graphics.buffer.IndexBuffer;
import cage.core.graphics.buffer.VertexBuffer;
import cage.core.graphics.config.LayoutConfig;
import cage.core.graphics.rasterizer.Rasterizer;
import cage.core.graphics.type.CullType;
import cage.core.graphics.vertexarray.VertexArray;
import cage.core.model.Mesh;
import cage.core.model.Model;
import cage.core.model.material.Material;
import cage.core.scene.SceneEntity;
import cage.core.scene.SceneManager;
import cage.core.scene.camera.Camera;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.system.MemoryStack.stackPush;

public class WaterManager extends SceneEntity {

    private static final float WATER_LEVEL = 128.0f + 16.0f;
    private static final float WATER_SCALE = 2048.0f;

    private Camera camera;

    public WaterManager(SceneManager sceneManager, GraphicsDevice graphicsDevice) {
        super(sceneManager, null, generateModel(graphicsDevice));
        this.camera = sceneManager.getDefaultCamera();
        moveUp(WATER_LEVEL);
        scale(WATER_SCALE);
        update(false);
    }

    public Camera getCamera() {
        return camera;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    private static Model generateModel(GraphicsDevice graphicsDevice) {
        VertexBuffer vertexBuffer = graphicsDevice.createVertexBuffer();
        vertexBuffer.setLayout(new LayoutConfig().float2());
        vertexBuffer.setUnitCount(4);
        try(MemoryStack stack = stackPush()) {
            FloatBuffer buffer = stack.callocFloat(2 * vertexBuffer.getUnitCount());
            buffer.put(-0.5f).put(-0.5f);
            buffer.put(-0.5f).put(0.5f);
            buffer.put(0.5f).put(-0.5f);
            buffer.put(0.5f).put(0.5f);
            buffer.rewind();
            vertexBuffer.writeData(buffer);
        }

        VertexArray vertexArray = graphicsDevice.createVertexArray();
        vertexArray.addVertexBuffer(vertexBuffer);

        Rasterizer rasterizer = graphicsDevice.createRasterizer();
        rasterizer.setCullType(CullType.NONE);

        Material material = new Material();
        material.setDiffuse(1.0f, 1.0f, 1.0f);

        Model model = new Model(vertexArray);
        IndexBuffer indexBuffer = graphicsDevice.createIndexBuffer();
        indexBuffer.setLayout(new LayoutConfig().int1());
        indexBuffer.setUnitCount(6);
        try(MemoryStack stack = stackPush()) {
            IntBuffer buffer = stack.callocInt(indexBuffer.getUnitCount());
            buffer.put(0).put(1).put(3);
            buffer.put(3).put(2).put(0);
            buffer.rewind();
            indexBuffer.writeData(buffer);
        }
        model.addMesh(new Mesh(indexBuffer, material, rasterizer));

        return model;
    }
}
