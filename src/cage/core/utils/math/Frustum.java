package cage.core.utils.math;

import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector4f;
import org.joml.Vector4fc;

public class Frustum {

	private Vector4f[] planes;
	
	public Frustum() {
		this.planes = new Vector4f[6];
		for(int i = 0; i < this.planes.length; ++i) {
			this.planes[i] = new Vector4f();
		}
	}
	
	public Vector4fc getFrustum(int index) {
		if(index >= 0 && index < planes.length) {
			return planes[index];
		}
		return null;
	}
	
	public void setFrustum(Matrix4fc viewMatrix, Matrix4fc projMatrix) {
		Matrix4f tViewProj = new Matrix4f(projMatrix).mul(viewMatrix).transpose();
		Vector4f finalRow = new Vector4f();
		tViewProj.getColumn(3, finalRow);
		for(int i = 0; i < 3; ++i) {
			tViewProj.getColumn(i, planes[i * 2]).add(finalRow).normalize();
			tViewProj.getColumn(i, planes[i * 2 + 1]).mul(-1.0f).add(finalRow).normalize();
		}
	}

	// Code from http://www.iquilezles.org/www/articles/frustumcorrect/frustumcorrect.htm
	// inigo quilez 1994-2017
	public boolean inFrustum(AABB box) {
		for (int i = 0; i < planes.length; i++) {
			int out = 0;
			out += (new Vector4f(box.getMin().x(), box.getMin().y(), box.getMin().z(), 1.0f).dot(planes[i]) < 0.0f ? 1 : 0);
			out += (new Vector4f(box.getMax().x(), box.getMin().y(), box.getMin().z(), 1.0f).dot(planes[i]) < 0.0f ? 1 : 0);
			out += (new Vector4f(box.getMin().x(), box.getMax().y(), box.getMin().z(), 1.0f).dot(planes[i]) < 0.0f ? 1 : 0);
			out += (new Vector4f(box.getMax().x(), box.getMax().y(), box.getMin().z(), 1.0f).dot(planes[i]) < 0.0f ? 1 : 0);
			out += (new Vector4f(box.getMin().x(), box.getMin().y(), box.getMax().z(), 1.0f).dot(planes[i]) < 0.0f ? 1 : 0);
			out += (new Vector4f(box.getMax().x(), box.getMin().y(), box.getMax().z(), 1.0f).dot(planes[i]) < 0.0f ? 1 : 0);
			out += (new Vector4f(box.getMin().x(), box.getMax().y(), box.getMax().z(), 1.0f).dot(planes[i]) < 0.0f ? 1 : 0);
			out += (new Vector4f(box.getMax().x(), box.getMax().y(), box.getMax().z(), 1.0f).dot(planes[i]) < 0.0f ? 1 : 0);
			if (out == 8) {
				return false;
			}
		}
		return true;
	}
}
