package cage.core.graphics;

import java.nio.*;

public abstract class Buffer {

    protected int m_unitCount;

    protected Buffer() {
        m_unitCount = 0;
    }

    public int getUnitCount() {
        return m_unitCount;
    }

    public void setUnitCount(int unitCount) {
        m_unitCount = unitCount;
    }

    public abstract void setData(ByteBuffer data);
    public abstract void setData(ShortBuffer data);
    public abstract void setData(IntBuffer data);
    public abstract void setData(FloatBuffer data);
    public abstract void setData(DoubleBuffer data);
    public abstract void setData(LongBuffer data);
}
