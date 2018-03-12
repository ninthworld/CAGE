package cage.core.model;

import cage.core.graphics.VertexArray;

import java.util.ArrayList;
import java.util.Iterator;
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

    public int getMeshCount() {
        return m_meshes.size();
    }

    public void attachMesh(Mesh mesh) {
        m_meshes.add(mesh);
    }

    public void detachMesh(Mesh mesh) {
        m_meshes.remove(mesh);
    }

    public Iterator<Mesh> getMeshIterator() {
        return m_meshes.iterator();
    }

    public boolean containsMesh(Mesh mesh) {
        return m_meshes.contains(mesh);
    }

    public Mesh getMesh(int index) {
        return m_meshes.get(index);
    }
}
