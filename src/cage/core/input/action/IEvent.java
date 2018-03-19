package cage.core.input.action;

import cage.core.input.component.IComponent;

public interface IEvent {
    IComponent getComponent();
    float getValue();
}
