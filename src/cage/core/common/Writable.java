package cage.core.common;

import java.nio.*;

public interface Writable {
    void writeData(ByteBuffer data);
    void writeData(ShortBuffer data);
    void writeData(IntBuffer data);
    void writeData(FloatBuffer data);
    void writeData(DoubleBuffer data);
}
