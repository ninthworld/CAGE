package cage.core.input;

import cage.core.input.action.IAction;
import cage.core.input.component.IComponent;
import cage.core.input.controller.InputController;
import cage.core.input.type.ActionType;

public class ActionState {

    private IAction action;
    private InputController controller;
    private IComponent component;
    private ActionType actionType;

    public ActionState(IAction action, InputController controller, IComponent component, ActionType actionType) {
        this.action = action;
        this.controller = controller;
        this.component = component;
        this.actionType = actionType;
    }

    public IAction getAction() {
        return action;
    }

    public void setAction(IAction action) {
        this.action = action;
    }

    public InputController getController() {
        return controller;
    }

    public IComponent getComponent() {
        return component;
    }

    public void setComponent(IComponent component) {
        this.component = component;
    }

    public ActionType getActionType() {
        return actionType;
    }

    public void setActionType(ActionType actionType) {
        this.actionType = actionType;
    }
}
