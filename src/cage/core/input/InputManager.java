package cage.core.input;

import cage.core.input.action.IAction;
import cage.core.input.component.IComponent;
import cage.core.input.controller.InputController;
import cage.core.input.controller.JoystickController;
import cage.core.input.controller.KeyboardController;
import cage.core.input.controller.MouseController;
import cage.core.input.type.ActionType;

import java.util.Iterator;

public abstract class InputManager {

    public InputManager() {
    }

    public abstract void update(float deltaTime);

    public abstract KeyboardController getKeyboardController();

    public abstract MouseController getMouseController();

    public abstract JoystickController getJoystickController(int index);

    public abstract Iterator<JoystickController> getJoystickControllerIterator();

    public ActionState registerAction(InputController controller, IComponent component, ActionType actionType, IAction action) {
        ActionState actionState = new ActionState(action, controller, component, actionType);
        controller.registerAction(actionState);
        return actionState;
    }

    public void unregisterAction(ActionState actionState) {
        actionState.getController().unregisterAction(actionState);
    }
}
