package cage.core.input;

import cage.core.input.action.IInputAction;
import cage.core.input.component.IInputComponent;
import cage.core.input.controller.InputController;
import cage.core.input.type.InputActionType;

public class ActionState {

    private IInputAction action;
    private InputController controller;
    private IInputComponent component;
    private InputActionType actionType;

    public ActionState(IInputAction action, InputController controller, IInputComponent component, InputActionType actionType) {
        this.action = action;
        this.controller = controller;
        this.component = component;
        this.actionType = actionType;
    }

    public IInputAction getAction() {
        return action;
    }

    public void setAction(IInputAction action) {
        this.action = action;
    }

    public InputController getController() {
        return controller;
    }

    public IInputComponent getComponent() {
        return component;
    }

    public void setComponent(IInputComponent component) {
        this.component = component;
    }

    public InputActionType getActionType() {
        return actionType;
    }

    public void setActionType(InputActionType actionType) {
        this.actionType = actionType;
    }
}
