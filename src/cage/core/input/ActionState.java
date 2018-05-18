package cage.core.input;

import cage.core.input.action.InputAction;
import cage.core.input.component.InputComponent;
import cage.core.input.controller.InputController;
import cage.core.input.type.InputActionType;

public class ActionState {

    private InputAction action;
    private InputController controller;
    private InputComponent component;
    private InputActionType actionType;

    public ActionState(InputAction action, InputController controller, InputComponent component, InputActionType actionType) {
        this.action = action;
        this.controller = controller;
        this.component = component;
        this.actionType = actionType;
    }

    public InputAction getAction() {
        return action;
    }

    public void setAction(InputAction action) {
        this.action = action;
    }

    public InputController getController() {
        return controller;
    }

    public InputComponent getComponent() {
        return component;
    }

    public void setComponent(InputComponent component) {
        this.component = component;
    }

    public InputActionType getActionType() {
        return actionType;
    }

    public void setActionType(InputActionType actionType) {
        this.actionType = actionType;
    }
}
