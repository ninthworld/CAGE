package cage.core.graphics;

import cage.core.graphics.type.CullType;
import cage.core.graphics.type.FillType;

public abstract class Rasterizer {

    protected FillType m_fill;
    protected CullType m_cull;
    protected boolean m_frontCCW;
    protected boolean m_multisampling;
    protected boolean m_scissoring;
    protected boolean m_depthClipping;

    protected Rasterizer() {
        m_fill = FillType.SOLID;
        m_cull = CullType.NONE;
        m_frontCCW = true;
        m_multisampling = true;
        m_scissoring = false;
        m_depthClipping = true;
    }

    public FillType getFillType() {
        return m_fill;
    }

    public void setFillType(FillType fill) {
        m_fill = fill;
    }

    public CullType getCullType() {
        return m_cull;
    }

    public void setCullType(CullType cull) {
        m_cull = cull;
    }

    public boolean isFrontCCW() {
        return m_frontCCW;
    }

    public void setFrontCCW(boolean frontCCW) {
        m_frontCCW = frontCCW;
    }

    public boolean isMultisampling() {
        return m_multisampling;
    }

    public void setMultisampling(boolean multisampling) {
        m_multisampling = multisampling;
    }

    public boolean isScissoring() {
        return m_scissoring;
    }

    public void setScissoring(boolean scissoring) {
        m_scissoring = scissoring;
    }

    public boolean isDepthClipping() {
        return m_depthClipping;
    }

    public void setDepthClipping(boolean depthClipping) {
        m_depthClipping = depthClipping;
    }
}
