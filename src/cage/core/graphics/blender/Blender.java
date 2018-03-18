package cage.core.graphics.blender;

import cage.core.common.IDestroyable;
import cage.core.graphics.type.BlendOpType;
import cage.core.graphics.type.BlendType;

public abstract class Blender implements IDestroyable {

    private boolean alphaToCoverage;
    private int index;
    private BlendType blendSrc;
    private BlendType blendDest;
    private BlendOpType blendFunc;
    private boolean maskR, maskG, maskB, maskA;

    public Blender() {
        this.alphaToCoverage = false;
        this.index = 0;
        this.blendSrc = blendDest = BlendType.ONE;
        this.blendFunc = BlendOpType.ADD;
        this.maskR = this.maskG = this.maskB = this.maskA = true;
    }

    public boolean isAlphaToCoverage() {
        return alphaToCoverage;
    }

    public void setAlphaToCoverage(boolean alphaToCoverage) {
        this.alphaToCoverage = alphaToCoverage;
    }

    public int getDrawBufferIndex() {
        return index;
    }

    public void setDrawBufferIndex(int index) {
        this.index = index;
    }

    public BlendType getBlendSrc() {
        return blendSrc;
    }

    public void setBlendSrc(BlendType blendSrc) {
        this.blendSrc = blendSrc;
    }

    public BlendType getBlendDest() {
        return blendDest;
    }

    public void setBlendDest(BlendType blendDest) {
        this.blendDest = blendDest;
    }

    public BlendOpType getBlendFunc() {
        return blendFunc;
    }

    public void setBlendFunc(BlendOpType blendFunc) {
        this.blendFunc = blendFunc;
    }

    public void setBlend(BlendType blendSrc, BlendType blendDest, BlendOpType blendFunc) {
        setBlendSrc(blendSrc);
        setBlendDest(blendDest);
        setBlendFunc(blendFunc);
    }

    public boolean isMaskingRed() {
        return maskR;
    }

    public boolean isMaskingGreen() {
        return maskG;
    }

    public boolean isMaskingBlue() {
        return maskB;
    }

    public boolean isMaskingAlpha() {
        return maskA;
    }

    public void setColorMask(boolean red, boolean green, boolean blue, boolean alpha) {
        this.maskR = red;
        this.maskG = green;
        this.maskB = blue;
        this.maskA = alpha;
    }
}
