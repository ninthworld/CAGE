package cage.core.input.action;

import cage.core.input.component.InputComponent;

public interface InputEvent {
    InputComponent getComponent();
    float getValue();
}
