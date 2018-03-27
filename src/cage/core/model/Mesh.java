package cage.core.model;

import cage.core.graphics.buffer.IndexBuffer;
import cage.core.graphics.rasterizer.Rasterizer;
import cage.core.graphics.type.PrimitiveType;
import cage.core.model.material.Material;

public class Mesh {

    private IndexBuffer indexBuffer;
    private Material material;
    private Rasterizer rasterizer;
    private PrimitiveType primitive;

    public Mesh(IndexBuffer indexBuffer, Material material, Rasterizer rasterizer) {
        this.indexBuffer = indexBuffer;
        this.material = material;
        this.rasterizer = rasterizer;
        this.primitive = PrimitiveType.TRIANGLES;
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

    public Rasterizer getRasterizer() {
        return rasterizer;
    }

    public void setRasterizer(Rasterizer rasterizer) {
        this.rasterizer = rasterizer;
    }

    public PrimitiveType getPrimitive() {
        return primitive;
    }

    public void setPrimitive(PrimitiveType primitive) {
        this.primitive = primitive;
    }
}
