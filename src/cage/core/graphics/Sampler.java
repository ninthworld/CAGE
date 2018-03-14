package cage.core.graphics;

import cage.core.graphics.type.CompareType;
import cage.core.graphics.type.EdgeType;
import cage.core.graphics.type.FilterType;

import java.awt.*;

public abstract class Sampler {

    private EdgeType edgeU;
    private EdgeType edgeV;
    private EdgeType edgeW;
    private FilterType min;
    private FilterType mag;
    private boolean mipmapping;
    private FilterType mipmap;
    private float minLod;
    private float maxLod;
    private float biasLod;
    private boolean anisotropy;
    private int maxAnisotropy;
    private boolean compare;
    private CompareType compareFunc;
    private Color border;

    public Sampler() {
        this.edgeU = this.edgeV = this.edgeW = EdgeType.CLAMP;
        this.min = this.mag = this.mipmap = FilterType.LINEAR;
        this.mipmapping = false;
        this.minLod = Float.MIN_VALUE;
        this.maxLod = Float.MAX_VALUE;
        this.biasLod = 0.0f;
        this.anisotropy = false;
        this.maxAnisotropy = 1;
        this.compare = false;
        this.compareFunc = CompareType.NEVER;
        this.border = Color.WHITE;
    }

    public EdgeType getEdgeU() {
        return edgeU;
    }

    public void setEdgeU(EdgeType edge) {
        this.edgeU = edge;
    }
    
    public EdgeType getEdgeV() {
        return edgeV;
    }

    public void setEdgeV(EdgeType edge) {
        this.edgeV = edge;
    }
    
    public EdgeType getEdgeW() {
        return edgeW;
    }

    public void setEdgeW(EdgeType edge) {
        this.edgeW = edge;
    }
    
    public FilterType getFilterMin() {
        return min;
    }

    public void setFilterMin(FilterType filter) {
        this.min = filter;
    }
    
    public FilterType getFilterMag() {
        return mag;
    }

    public void setFilterMag(FilterType filter) {
        this.mag = filter;
    }

    public boolean isMipmapping() {
        return mipmapping;
    }

    public void setMipmapping(boolean mipmapping) {
        this.mipmapping = mipmapping;
    }
    
    public FilterType getFilterMipmap() {
        return mipmap;
    }

    public void setFilterMipmap(FilterType mipmap) {
        this.mipmap = mipmap;
    }
    
    public float getMipmapMinLOD() {
        return minLod;
    }

    public void setMipmapMinLOD(float minLod) {
        this.minLod = minLod;
    }
    
    public float getMipmapMaxLOD() {
        return maxLod;
    }

    public void setMipmapMaxLOD(float maxLod) {
        this.maxLod = maxLod;
    }
    
    public float getMipmapBiasLOD() {
        return biasLod;
    }

    public void setMipmapBiasLOD(float biasLod) {
        this.biasLod = biasLod;
    }
    
    public boolean isAnisotropy() {
        return anisotropy;
    }

    public void setAnisotropy(boolean anisotropy) {
        this.anisotropy = anisotropy;
    }
    
    public int getMaxAnisotropy() {
        return maxAnisotropy;
    }

    public void setMaxAnisotropy(int maxAnisotropy) {
        this.maxAnisotropy = maxAnisotropy;
    }
    
    public boolean isCompare() {
        return compare;
    }

    public void setCompare(boolean compare) {
        this.compare = compare;
    }
    
    public CompareType getCompareFunc() {
        return compareFunc;
    }

    public void setCompareFunc(CompareType compareFunc) {
        this.compareFunc = compareFunc;
    }
    
    public Color getBorderColor() {
        return border;
    }

    public void setBorderColor(Color border) {
        this.border = border;
    }
}
