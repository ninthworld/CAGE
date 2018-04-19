package biggerfish.terrain;

import org.joml.Vector2i;

import biggerfish.quadtree.QuadTree;
import biggerfish.quadtree.QuadTreeNode;
import cage.core.asset.AssetManager;
import cage.core.graphics.GraphicsDevice;
import cage.core.graphics.sampler.Sampler;
import cage.core.graphics.type.EdgeType;
import cage.core.model.material.Material;
import cage.core.scene.SceneManager;

public class TerrainManager extends QuadTree {

    private static final float[] LOD_RANGES = new float[] {
            2048.0f, 1024.0f, 512.0f, 256.0f, 128.0f, 64.0f, 32.0f, 0.0f
    };

    private static final int DIVISIONS = 16;

    public TerrainManager(SceneManager sceneManager, GraphicsDevice graphicsDevice, AssetManager assetManager) {
        super(sceneManager, graphicsDevice, LOD_RANGES, DIVISIONS);
    }
    
    @Override
    protected void initialize() {
        addNode(new TerrainNode(this, this, 0, new Vector2i(0, 0)));
        addNode(new TerrainNode(this, this, 0, new Vector2i(0, 1)));
        addNode(new TerrainNode(this, this, 0, new Vector2i(1, 0)));
        addNode(new TerrainNode(this, this, 0, new Vector2i(1, 1)));    	
    }
}