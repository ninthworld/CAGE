package cage.core.graphics.rasterizer;

import cage.core.common.Destroyable;
import cage.core.graphics.type.CullType;
import cage.core.graphics.type.FillType;

public abstract class Rasterizer implements Destroyable {

    private FillType fill;
    private CullType cull;
    private boolean frontCCW;
    private boolean multisampling;
    private boolean scissoring;
    private boolean depthClipping;

    public Rasterizer() {
        this.fill = FillType.SOLID;
        this.cull = CullType.BACK;
        this.frontCCW = false;
        this.multisampling = true;
        this.scissoring = false;
        this.depthClipping = true;
    }

    public FillType getFillType() {
        return fill;
    }

    public void setFillType(FillType fill) {
        this.fill = fill;
    }

    public CullType getCullType() {
        return cull;
    }

    public void setCullType(CullType cull) {
        this.cull = cull;
    }

    public boolean isFrontCCW() {
        return frontCCW;
    }

    public void setFrontCCW(boolean frontCCW) {
        this.frontCCW = frontCCW;
    }

    public boolean isMultisampling() {
        return multisampling;
    }

    public void setMultisampling(boolean multisampling) {
        this.multisampling = multisampling;
    }

    public boolean isScissoring() {
        return scissoring;
    }

    public void setScissoring(boolean scissoring) {
        this.scissoring = scissoring;
    }

    public boolean isDepthClipping() {
        return depthClipping;
    }

    public void setDepthClipping(boolean depthClipping) {
        this.depthClipping = depthClipping;
    }
}
