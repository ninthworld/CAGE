package cage.core.gui.graphics;

import cage.core.common.Destroyable;

public abstract class GUIFont implements Destroyable {

    private String name;

    public GUIFont(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
