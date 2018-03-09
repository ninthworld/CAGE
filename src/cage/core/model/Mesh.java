package cage.core.model;

import cage.core.graphics.IndexBuffer;
import cage.core.model.material.Material;

public class Mesh {

    private IndexBuffer m_indexBuffer;
    private Material m_material;

    public Mesh(IndexBuffer indexBuffer, Material material) {
        m_indexBuffer = indexBuffer;
        m_material = material;
    }

    public IndexBuffer getIndexBuffer() {
        return m_indexBuffer;
    }

    public void setIndexBuffer(IndexBuffer indexBuffer) {
        m_indexBuffer = indexBuffer;
    }

    public Material getMaterial() {
        return m_material;
    }

    public void setMaterial(Material material) {
        m_material = material;
    }
}
