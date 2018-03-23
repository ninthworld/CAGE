package cage.nanovg.gui.graphics;

import cage.core.gui.graphics.GUIFont;

import java.nio.ByteBuffer;

import static org.lwjgl.nanovg.NanoVG.*;

public class NVGGUIFont extends GUIFont {

    private long context;
    private int id;
    private ByteBuffer data;

    public NVGGUIFont(long context, String name, ByteBuffer data) {
        super(name);
        this.context = context;
        this.data = data;
        this.id = nvgCreateFontMem(context, name, data, 0);
    }

    public int getId() {
        return id;
    }

    @Override
    public void destroy() {
    }
}
