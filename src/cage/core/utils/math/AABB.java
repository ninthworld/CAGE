package cage.core.utils.math;

import org.joml.Vector3f;
import org.joml.Vector3fc;

public class AABB {

	private Vector3f min;
	private Vector3f max;
	
	public AABB(Vector3f min, Vector3f max) {
		this.min = min;
		this.max = max;
	}
	
	public Vector3fc getMin() {
		return min;
	}
	
	public void setMin(Vector3fc min) {
		this.min.set(min);
	}
	
	public void setMin(float x, float y, float z) {
		this.min.set(x, y, z);
	}
	
	public Vector3f getMax() {
		return max;
}
	
	public void setMax(Vector3fc min) {
		this.max.set(min);
	}
	
	public void setMax(float x, float y, float z) {
		this.max.set(x, y, z);
	}
	
	public boolean inBounds(Vector3f that) {
		return (that.x > this.min.x && that.x < this.max.x) &&
				(that.y > this.min.y && that.y < this.max.y) &&
				(that.z > this.min.z && that.z < this.max.z);
	}
	
	public boolean inBounds(AABB that) {
		return ((this.min.x > that.min.x && this.min.x < that.max.x) || (this.max.x > that.min.x && this.max.x < that.max.x)) &&
				((this.min.y > that.min.y && this.min.y < that.max.y) || (this.max.y > that.min.y && this.max.y < that.max.y)) &&
				((this.min.z > that.min.z && this.min.z < that.max.z) || (this.max.z > that.min.z && this.max.z < that.max.z));
	}
}
