package cage.core.graphics;

import cage.core.graphics.type.CompareType;
import cage.core.graphics.type.EdgeType;
import cage.core.graphics.type.FilterType;

import java.awt.*;

public abstract class Sampler {

    protected EdgeType m_edgeU;
    protected EdgeType m_edgeV;
    protected EdgeType m_edgeW;
    protected FilterType m_min;
    protected FilterType m_mag;
    protected boolean m_mipmapping;
    protected FilterType m_mipmap;
    protected float m_minLod;
    protected float m_maxLod;
    protected float m_biasLod;
    protected boolean m_anisotropy;
    protected int m_maxAnisotropy;
    protected boolean m_compare;
    protected CompareType m_compareFunc;
    protected Color m_border;

    protected Sampler() {
        m_edgeU = m_edgeV = m_edgeW = EdgeType.CLAMP;
        m_min = m_mag = m_mipmap = FilterType.LINEAR;
        m_mipmapping = false;
        m_minLod = Float.MIN_VALUE;
        m_maxLod = Float.MAX_VALUE;
        m_biasLod = 0.0f;
        m_anisotropy = false;
        m_maxAnisotropy = 1;
        m_compare = false;
        m_compareFunc = CompareType.NEVER;
        m_border = Color.WHITE;
    }

    public EdgeType getEdgeU() {
        return m_edgeU;
    }

    public void setEdgeU(EdgeType edge) {
        m_edgeU = edge;
    }
    
    public EdgeType getEdgeV() {
        return m_edgeV;
    }

    public void setEdgeV(EdgeType edge) {
        m_edgeV = edge;
    }
    
    public EdgeType getEdgeW() {
        return m_edgeW;
    }

    public void setEdgeW(EdgeType edge) {
        m_edgeW = edge;
    }
    
    public FilterType getFilterMin() {
        return m_min;
    }

    public void setFilterMin(FilterType filter) {
        m_min = filter;
    }
    
    public FilterType getFilterMag() {
        return m_mag;
    }

    public void setFilterMag(FilterType filter) {
        m_mag = filter;
    }

    public boolean isMipmapping() {
        return m_mipmapping;
    }

    public void setMipmapping(boolean mipmapping) {
        m_mipmapping = mipmapping;
    }
    
    public FilterType getFilterMipmap() {
        return m_mipmap;
    }

    public void setFilterMipmap(FilterType mipmap) {
        m_mipmap = mipmap;
    }
    
    public float getMipmapMinLOD() {
        return m_minLod;
    }

    public void setMipmapMinLOD(float minLod) {
        m_minLod = minLod;
    }
    
    public float getMipmapMaxLOD() {
        return m_maxLod;
    }

    public void setMipmapMaxLOD(float maxLod) {
        m_maxLod = maxLod;
    }
    
    public float getMipmapBiasLOD() {
        return m_biasLod;
    }

    public void setMipmapBiasLOD(float biasLod) {
        m_biasLod = biasLod;
    }
    
    public boolean isAnisotropy() {
        return m_anisotropy;
    }

    public void setAnisotropy(boolean anisotropy) {
        m_anisotropy = anisotropy;
    }
    
    public int getMaxAnisotropy() {
        return m_maxAnisotropy;
    }

    public void setMaxAnisotropy(int maxAnisotropy) {
        m_maxAnisotropy = maxAnisotropy;
    }
    
    public boolean isCompare() {
        return m_compare;
    }

    public void setCompare(boolean compare) {
        m_compare = compare;
    }
    
    public CompareType getCompareFunc() {
        return m_compareFunc;
    }

    public void setCompareFunc(CompareType compareFunc) {
        m_compareFunc = compareFunc;
    }
    
    public Color getBorderColor() {
        return m_border;
    }

    public void setBorderColor(Color border) {
        m_border = border;
    }
}
