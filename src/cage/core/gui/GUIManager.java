package cage.core.gui;

import cage.core.common.Destroyable;
import cage.core.gui.component.Container;
import cage.core.gui.graphics.GUIFont;
import cage.core.gui.graphics.GUIGraphics;
import cage.core.gui.graphics.GUIImage;

import java.nio.ByteBuffer;

public abstract class GUIManager implements Destroyable {

    private GUIGraphics graphics;
    private Container rootContainer;

    public GUIManager(GUIGraphics graphics) {
        this.graphics = graphics;
        this.rootContainer = new Container();
    }

    public abstract GUIImage createImage(int width, int height, ByteBuffer data);

    public abstract GUIFont createFont(String name, ByteBuffer data);

    public void render() {
        rootContainer.render(graphics);
    }

    public Container getRootContainer() {
        return rootContainer;
    }

    public GUIGraphics getGraphics() {
        return graphics;
    }

    @Override
    public void destroy() {
        graphics.destroy();
    }
}
