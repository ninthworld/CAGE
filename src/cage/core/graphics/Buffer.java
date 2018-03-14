package cage.core.graphics;

import cage.core.graphics.config.LayoutConfig;

import java.nio.*;

public abstract class Buffer {

    private int unitCount;
    private LayoutConfig layout;

    public Buffer() {
        this.unitCount = 0;
        this.layout = new LayoutConfig();
    }

    public int getUnitCount() {
        return unitCount;
    }

    public void setUnitCount(int unitCount) {
        this.unitCount = unitCount;
    }

    public LayoutConfig getLayout() {
        return layout;
    }

    public void setLayout(LayoutConfig layout) {
        this.layout = layout;
    }

    public abstract void setData(ByteBuffer data);
    public abstract void setData(ShortBuffer data);
    public abstract void setData(IntBuffer data);
    public abstract void setData(FloatBuffer data);
    public abstract void setData(DoubleBuffer data);
    public abstract void setData(LongBuffer data);
}
