package cage.core.input.action;

import cage.core.input.component.IInputComponent;

public interface IInputEvent {
    IInputComponent getComponent();
    float getValue();
}
