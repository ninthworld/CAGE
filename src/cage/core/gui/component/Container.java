package cage.core.gui.component;

import cage.core.gui.graphics.GUIGraphics;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Container extends GUIComponent {

    private List<GUIComponent> components;

    public Container() {
        this.components = new ArrayList<>();
    }

    public void addComponent(GUIComponent component) {
        components.add(component);
    }

    public void removeComponent(GUIComponent component) {
        components.remove(component);
    }

    public void removeComponent(int index) {
        components.remove(index);
    }

    public void removeAllComponents() {
        components.forEach(this::removeComponent);
    }

    public int getComponentCount() {
        return components.size();
    }

    public boolean containsComponent(GUIComponent component) {
        return components.contains(component);
    }

    public GUIComponent getComponent(int index) {
        return components.get(index);
    }

    public Iterator<GUIComponent> getComponentIterator() {
        return components.iterator();
    }

    @Override
    public void render(GUIGraphics graphics) {
        for(GUIComponent component : components) {
            component.render(graphics);
        }
    }
}
