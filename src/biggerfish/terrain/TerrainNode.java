package biggerfish.terrain;

import org.joml.Vector2i;

import biggerfish.quadtree.QuadTree;
import biggerfish.quadtree.QuadTreeNode;
import cage.core.scene.Node;

public class TerrainNode extends QuadTreeNode {

	public TerrainNode(QuadTree quadtree, Node parent, int lod, Vector2i nodeIndex) {
		super(quadtree, parent, lod, nodeIndex);
	}

	@Override
	protected void updateNode() {
		super.updateNode();
		getWorldBounds().setMin(getWorldBounds().getMin().x(), -128.0f, getWorldBounds().getMin().z());
		getWorldBounds().setMax(getWorldBounds().getMax().x(), 128.0f, getWorldBounds().getMax().z());
	}
	
	@Override
    protected void addNewChild(QuadTree quadTree, Node parent, int lod, Vector2i nodeIndex) {
    	addNode(new TerrainNode(quadTree, parent, lod, nodeIndex));
    }
}
