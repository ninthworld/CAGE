package cage.core.model;

import cage.core.graphics.IndexBuffer;
import cage.core.model.material.Material;

public class Mesh {

    private IndexBuffer indexBuffer;
    private Material material;

    public Mesh(IndexBuffer indexBuffer, Material material) {
        this.indexBuffer = indexBuffer;
        this.material = material;
    }

    public IndexBuffer getIndexBuffer() {
        return indexBuffer;
    }

    public void setIndexBuffer(IndexBuffer indexBuffer) {
        this.indexBuffer = indexBuffer;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }
}
