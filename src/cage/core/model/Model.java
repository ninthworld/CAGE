package cage.core.model;

import cage.core.graphics.vertexarray.VertexArray;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Model {

    private VertexArray vertexArray;
    private List<Mesh> meshes;

    public Model(VertexArray vertexArray) {
        this.vertexArray = vertexArray;
        this.meshes = new ArrayList<>();
    }

    public VertexArray getVertexArray() {
        return vertexArray;
    }

    public void setVertexArray(VertexArray vertexArray) {
        this.vertexArray = vertexArray;
    }

    public int getMeshCount() {
        return meshes.size();
    }

    public void attachMesh(Mesh mesh) {
        this.meshes.add(mesh);
    }

    public void detachMesh(Mesh mesh) {
        this.meshes.remove(mesh);
    }

    public Iterator<Mesh> getMeshIterator() {
        return meshes.iterator();
    }

    public boolean containsMesh(Mesh mesh) {
        return meshes.contains(mesh);
    }

    public Mesh getMesh(int index) {
        return meshes.get(index);
    }
}
