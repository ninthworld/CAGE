package cage.core.gui.component;

import cage.core.gui.GUIGraphics;

import java.util.ArrayList;
import java.util.List;

public class Container extends GUIComponent {

    private List<GUIComponent> components;

    public Container() {
        this.components = new ArrayList<>();
    }

    public void addComponent(GUIComponent component) {
        components.add(component);
    }

    // TODO: The rest

    @Override
    public void render(GUIGraphics graphics) {
        for(GUIComponent component : components) {
            component.render(graphics);
        }
    }
}
