package biggerfish.terrain;

import biggerfish.quadtree.QuadTree;
import cage.core.asset.AssetManager;
import cage.core.graphics.GraphicsDevice;
import cage.core.graphics.sampler.Sampler;
import cage.core.graphics.type.EdgeType;
import cage.core.model.material.Material;
import cage.core.scene.SceneManager;

public class TerrainManager extends QuadTree {

    private static final float[] LOD_RANGES = new float[] {
            2048.0f, 1024.0f, 512.0f, 256.0f, 128.0f, 64.0f, 32.0f, 16.0f, 8.0f, 0.0f, 0.0f, 0.0f, 0.0f
    };

    private static final int DIVISIONS = 16;

    public TerrainManager(SceneManager sceneManager, GraphicsDevice graphicsDevice, AssetManager assetManager) {
        super(sceneManager, graphicsDevice, LOD_RANGES, DIVISIONS);

        Material material = getModel().getMesh(0).getMaterial();
        material.setDiffuse(0.6f, 0.6f, 0.6f);
        material.setSpecular(0.6f, 0.6f, 0.6f);
        material.setDiffuse(assetManager.loadTextureFile("sand2/sand_color.jpg"));
        material.setSpecular(assetManager.loadTextureFile("sand2/sand_spec.jpg"));
        material.setNormal(assetManager.loadTextureFile("sand2/sand_norm.jpg"));
        material.setShininess(16.0f);

        material.getDiffuseTexture().setMipmapping(true);
        material.getSpecularTexture().setMipmapping(true);
        material.getNormalTexture().setMipmapping(true);

        Sampler sampler = graphicsDevice.createSampler();
        sampler.setMipmapping(true);
        sampler.setEdge(EdgeType.WRAP);
        material.getDiffuseTexture().setSampler(sampler);
        material.getSpecularTexture().setSampler(sampler);
        material.getNormalTexture().setSampler(sampler);
    }
}