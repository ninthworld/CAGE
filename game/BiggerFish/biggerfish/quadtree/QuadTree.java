package biggerfish.quadtree;

import cage.core.asset.AssetManager;
import cage.core.graphics.GraphicsDevice;
import cage.core.graphics.buffer.IndexBuffer;
import cage.core.graphics.buffer.VertexBuffer;
import cage.core.graphics.config.LayoutConfig;
import cage.core.graphics.rasterizer.Rasterizer;
import cage.core.graphics.sampler.Sampler;
import cage.core.graphics.type.EdgeType;
import cage.core.graphics.vertexarray.VertexArray;
import cage.core.model.Mesh;
import cage.core.model.Model;
import cage.core.model.material.Material;
import cage.core.scene.SceneManager;
import cage.core.scene.SceneNode;
import cage.core.scene.camera.Camera;
import org.joml.Vector2i;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.system.MemoryStack.stackPush;

public class QuadTree extends SceneNode {

    public static final int NORTH   = 0b1;
    public static final int SOUTH   = 0b10;
    public static final int WEST    = 0b100;
    public static final int EAST    = 0b1000;

    private float[] lodRanges;
    private int divisions;
    private Model model;
    private Camera camera;

    public QuadTree(SceneManager sceneManager, GraphicsDevice graphicsDevice, float[] lodRanges, int divisions) {
        super(sceneManager, null);
        this.lodRanges = lodRanges;
        this.divisions = divisions;
        this.model = generateModel(graphicsDevice);
        this.camera = sceneManager.getDefaultCamera();
        scale(lodRanges[0]);
        initialize();
    }
    
    protected void initialize() {
        addNode(new QuadTreeNode(this, this, 0, new Vector2i(0, 0)));
        addNode(new QuadTreeNode(this, this, 0, new Vector2i(0, 1)));
        addNode(new QuadTreeNode(this, this, 0, new Vector2i(1, 0)));
        addNode(new QuadTreeNode(this, this, 0, new Vector2i(1, 1)));    	
    }
    
    public float[] getLodRanges() {
        return lodRanges;
    }

    public int getDivisions() {
        return divisions;
    }
    
    public Model getModel() {
        return model;
    }

    public Camera getCamera() {
        return camera;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    private Model generateModel(GraphicsDevice graphicsDevice) {
        VertexBuffer vertexBuffer = graphicsDevice.createVertexBuffer();
        vertexBuffer.setLayout(new LayoutConfig().float2());
        vertexBuffer.setUnitCount((divisions + 1) * (divisions + 1));
        try(MemoryStack stack = stackPush()) {
            FloatBuffer buffer = stack.callocFloat(2 * vertexBuffer.getUnitCount());
            for(int i = 0; i <= divisions; ++i) {
                for(int j = 0; j <= divisions; ++j) {
                    buffer.put(i / (float)divisions - 0.5f);
                    buffer.put(j / (float)divisions - 0.5f);
                }
            }
            buffer.rewind();
            vertexBuffer.writeData(buffer);
        }

        VertexArray vertexArray = graphicsDevice.createVertexArray();
        vertexArray.addVertexBuffer(vertexBuffer);

        Rasterizer rasterizer = graphicsDevice.createRasterizer();

        Material material = new Material();
        material.setDiffuse(1.0f, 1.0f, 1.0f);

        Model model = new Model(vertexArray);
        for(int m = 0; m < 16; ++m) {
            List<Integer> indices = new ArrayList<>();
            for(int i = 0; i < divisions; i += 2) {
                for(int j = 0; j < divisions; j += 2) {
                    if((m & NORTH) > 0 && i == 0) {
                        indices.add(i * (divisions + 1) + (j + 2));
                        indices.add((i + 1) * (divisions + 1) + (j + 1));
                        indices.add(i * (divisions + 1) + j);
                    }
                    else {
                        indices.add(i * (divisions + 1) + (j + 1));
                        indices.add((i + 1) * (divisions + 1) + (j + 1));
                        indices.add(i * (divisions + 1) + j);

                        indices.add(i * (divisions + 1) + (j + 2));
                        indices.add((i + 1) * (divisions + 1) + (j + 1));
                        indices.add(i * (divisions + 1) + (j + 1));
                    }

                    if((m & SOUTH) > 0 && i == divisions - 2) {
                        indices.add((i + 2) * (divisions + 1) + j);
                        indices.add((i + 1) * (divisions + 1) + (j + 1));
                        indices.add((i + 2) * (divisions + 1) + (j + 2));
                    }
                    else {
                        indices.add((i + 2) * (divisions + 1) + j);
                        indices.add((i + 1) * (divisions + 1) + (j + 1));
                        indices.add((i + 2) * (divisions + 1) + (j + 1));

                        indices.add((i + 2) * (divisions + 1) + (j + 1));
                        indices.add((i + 1) * (divisions + 1) + (j + 1));
                        indices.add((i + 2) * (divisions + 1) + (j + 2));
                    }

                    if((m & EAST) > 0 && j == divisions - 2) {
                        indices.add((i + 2) * (divisions + 1) + (j + 2));
                        indices.add((i + 1) * (divisions + 1) + (j + 1));
                        indices.add(i * (divisions + 1) + (j + 2));
                    }
                    else {
                        indices.add((i + 1) * (divisions + 1) + (j + 2));
                        indices.add((i + 1) * (divisions + 1) + (j + 1));
                        indices.add(i * (divisions + 1) + (j + 2));

                        indices.add((i + 2) * (divisions + 1) + (j + 2));
                        indices.add((i + 1) * (divisions + 1) + (j + 1));
                        indices.add((i + 1) * (divisions + 1) + (j + 2));
                    }

                    if((m & WEST) > 0 && j == 0) {
                        indices.add(i * (divisions + 1) + j);
                        indices.add((i + 1) * (divisions + 1) + (j + 1));
                        indices.add((i + 2) * (divisions + 1) + j);
                    }
                    else {
                        indices.add(i * (divisions + 1) + j);
                        indices.add((i + 1) * (divisions + 1) + (j + 1));
                        indices.add((i + 1) * (divisions + 1) + j);

                        indices.add((i + 1) * (divisions + 1) + j);
                        indices.add((i + 1) * (divisions + 1) + (j + 1));
                        indices.add((i + 2) * (divisions + 1) + j);
                    }
                }
            }

            IndexBuffer indexBuffer = graphicsDevice.createIndexBuffer();
            indexBuffer.setLayout(new LayoutConfig().int1());
            indexBuffer.setUnitCount(indices.size());
            try(MemoryStack stack = stackPush()) {
                IntBuffer buffer = stack.callocInt(indices.size());
                for (Integer i : indices) {
                    buffer.put(i);
                }
                buffer.rewind();
                indexBuffer.writeData(buffer);
            }
            model.addMesh(new Mesh(indexBuffer, material, rasterizer));
        }

        return model;
    }
}
