package biggerfish.quadtree;

import cage.core.scene.Node;
import cage.core.scene.SceneEntity;
import org.joml.Vector2f;
import org.joml.Vector2fc;
import org.joml.Vector2i;
import org.joml.Vector3f;

public class QuadTreeNode extends SceneEntity {

    private QuadTree quadTree;
    private Vector2i nodeIndex;
    private int lod;
    private int meshIndex;
    private boolean leaf;

    public QuadTreeNode(QuadTree quadtree, Node parent, int lod, Vector2i nodeIndex) {
        super(quadtree.getSceneManager(), parent, quadtree.getModel());
        this.quadTree = quadtree;
        this.nodeIndex = nodeIndex;
        this.lod = lod;
        this.meshIndex = 0;
        this.leaf = true;

        translate(new Vector3f((nodeIndex.x > 0 ? 0.25f : -0.25f), 0.0f, (nodeIndex.y > 0 ? 0.25f : -0.25f)));
        scale(0.5f);
    }

    @Override
    public void update(boolean forced) {
        super.update(forced);

        float distance = getWorldPosition().distance(quadTree.getCamera().getWorldPosition().mul(1.0f, 0.0f, 1.0f, new Vector3f()));
        if(distance < quadTree.getLodRanges()[lod]) {
            leaf = false;
            if(getNodeCount() == 0) {
                for(int i = 0; i < 2; ++i) {
                    for(int j = 0; j < 2; ++j) {
                        addNewChild(quadTree, this, lod + 1, new Vector2i(i, j));
                    }
                }
            }
        }
        else {
            leaf = true;
            if(getNodeCount() > 0) {
                removeAllNodes();
            }
        }

        if(leaf) {
            meshIndex = 0;
            if(nodeIndex.x == 0 && checkNeighbor(quadTree, new Vector2f(getWorldPosition().x() - getWorldScale().x(), getWorldPosition().z()))) {
                meshIndex |= QuadTree.NORTH;
            }
            else if(nodeIndex.x == 1 && checkNeighbor(quadTree, new Vector2f(getWorldPosition().x() + getWorldScale().x(), getWorldPosition().z()))) {
                meshIndex |= QuadTree.SOUTH;
            }
            if(nodeIndex.y == 0 && checkNeighbor(quadTree, new Vector2f(getWorldPosition().x(), getWorldPosition().z() - getWorldScale().x()))) {
                meshIndex |= QuadTree.WEST;
            }
            else if(nodeIndex.y == 1 && checkNeighbor(quadTree, new Vector2f(getWorldPosition().x(), getWorldPosition().z() + getWorldScale().x()))) {
                meshIndex |= QuadTree.EAST;
            }
        }
    }

    public QuadTree getQuadTree() {
        return quadTree;
    }

    public Vector2i getNodeIndex() {
        return nodeIndex;
    }

    public int getLod() {
        return lod;
    }

    public int getMeshIndex() {
        return meshIndex;
    }

    public boolean isLeaf() {
        return leaf;
    }

    protected void addNewChild(QuadTree quadTree, Node parent, int lod, Vector2i nodeIndex) {
    	addNode(new QuadTreeNode(quadTree, parent, lod, nodeIndex));
    }
    
    private boolean checkNeighbor(Node node, Vector2fc worldPos) {
        if(node.getNodeCount() > 0) {
            Vector2f nodePos = new Vector2f(node.getWorldPosition().x(), node.getWorldPosition().z());
            if(worldPos.x() < nodePos.x) {
                if(worldPos.y() < nodePos.y) {
                    return checkNeighbor(node.getNode(0), worldPos);
                }
                else {
                    return checkNeighbor(node.getNode(1), worldPos);
                }
            }
            else {
                if(worldPos.y() < nodePos.y) {
                    return checkNeighbor(node.getNode(2), worldPos);
                }
                else {
                    return checkNeighbor(node.getNode(3), worldPos);
                }
            }
        }
        else {
            if(node instanceof QuadTreeNode) {
                return (((QuadTreeNode) node).getLod() < lod);
            }
        }
        return false;
    }
}
