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

    public void addMesh(Mesh mesh) {
        meshes.add(mesh);
    }

    public void removeMesh(Mesh mesh) {
        meshes.remove(mesh);
    }

    public void removeMesh(int index) {
        meshes.remove(index);
    }

    public void removeAllMeshs() {
        meshes.forEach(this::removeMesh);
    }

    public int getMeshCount() {
        return meshes.size();
    }

    public boolean containsMesh(Mesh mesh) {
        return meshes.contains(mesh);
    }

    public Mesh getMesh(int index) {
        return meshes.get(index);
    }

    public Iterator<Mesh> getMeshIterator() {
        return meshes.iterator();
    }
}
