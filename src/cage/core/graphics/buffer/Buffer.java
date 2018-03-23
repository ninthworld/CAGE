package cage.core.graphics.buffer;

import cage.core.common.Destroyable;
import cage.core.common.Writable;
import cage.core.graphics.config.LayoutConfig;

import java.nio.*;

public abstract class Buffer implements Destroyable, Writable {

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
}
