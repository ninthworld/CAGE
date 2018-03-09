package cage.core.model;

import cage.core.graphics.VertexArray;

import java.util.ArrayList;
import java.util.List;

public class Model {

    private VertexArray m_vertexArray;
    private List<Mesh> m_meshes;

    public Model(VertexArray vertexArray) {
        m_vertexArray = vertexArray;
        m_meshes = new ArrayList<>();
    }

    public VertexArray getVertexArray() {
        return m_vertexArray;
    }

    public void setVertexArray(VertexArray vertexArray) {
        m_vertexArray = vertexArray;
    }

    public void addMesh(Mesh mesh) {
        m_meshes.add(mesh);
    }

    public void removeMesh(int index) {
        m_meshes.remove(index);
    }

    public int getMeshCount() {
        return m_meshes.size();
    }

    public List<Mesh> getMeshes() {
        return m_meshes;
    }
}
