package cage.core.graphics;

import cage.core.graphics.type.BlendOpType;
import cage.core.graphics.type.BlendType;

public abstract class Blender {

    protected boolean m_alphaToCoverage;
    protected int m_index;
    protected BlendType m_blendSrc;
    protected BlendType m_blendDest;
    protected BlendOpType m_blendFunc;
    protected boolean m_maskR, m_maskG, m_maskB, m_maskA;

    protected Blender() {
        m_alphaToCoverage = false;
        m_index = 0;
        m_blendSrc = m_blendDest = BlendType.ONE;
        m_blendFunc = BlendOpType.ADD;
        m_maskR = m_maskG = m_maskB = m_maskA = true;
    }

    public boolean isAlphaToCoverage() {
        return m_alphaToCoverage;
    }

    public void setAlphaToCoverage(boolean alphaToCoverage) {
        m_alphaToCoverage = alphaToCoverage;
    }

    public int getDrawBufferIndex() {
        return m_index;
    }

    public void setDrawBufferIndex(int index) {
        m_index = index;
    }

    public BlendType getBlendSrc() {
        return m_blendSrc;
    }

    public void setBlendSrc(BlendType blendSrc) {
        m_blendSrc = blendSrc;
    }

    public BlendType getBlendDest() {
        return m_blendDest;
    }

    public void setBlendDest(BlendType blendDest) {
        m_blendDest = blendDest;
    }

    public BlendOpType getBlendFunc() {
        return m_blendFunc;
    }

    public void setBlendFunc(BlendOpType blendFunc) {
        m_blendFunc = blendFunc;
    }

    public void setBlend(BlendType blendSrc, BlendType blendDest, BlendOpType blendFunc) {
        setBlendSrc(blendSrc);
        setBlendDest(blendDest);
        setBlendFunc(blendFunc);
    }

    public boolean isMaskingRed() {
        return m_maskR;
    }

    public boolean isMaskingGreen() {
        return m_maskG;
    }

    public boolean isMaskingBlue() {
        return m_maskB;
    }

    public boolean isMaskingAlpha() {
        return m_maskA;
    }

    public void setColorMask(boolean red, boolean green, boolean blue, boolean alpha) {
        m_maskR = red;
        m_maskG = green;
        m_maskB = blue;
        m_maskA = alpha;
    }
}
