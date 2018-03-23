package cage.core.gui;

import cage.core.common.Destroyable;
import cage.core.gui.component.Container;

public abstract class GUIManager implements Destroyable {

    private GUIGraphics graphics;
    private Container rootContainer;

    public GUIManager(GUIGraphics graphics) {
        this.graphics = graphics;
        this.rootContainer = new Container();
    }

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
