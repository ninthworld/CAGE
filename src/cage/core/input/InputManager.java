package cage.core.input;

import cage.core.input.action.InputAction;
import cage.core.input.component.InputComponent;
import cage.core.input.controller.InputController;
import cage.core.input.controller.JoystickController;
import cage.core.input.controller.KeyboardController;
import cage.core.input.controller.MouseController;
import cage.core.input.type.InputActionType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class InputManager {

    private List<ActionState> actions;

    public InputManager() {
        this.actions = new ArrayList<>();
    }

    public abstract void update(float deltaTime);

    public abstract KeyboardController getKeyboardController();

    public abstract MouseController getMouseController();

    public abstract JoystickController getJoystickController(int index);

    public abstract Iterator<JoystickController> getJoystickControllerIterator();

    public ActionState registerAction(InputController controller, InputComponent component, InputActionType actionType, InputAction action) {
        ActionState actionState = new ActionState(action, controller, component, actionType);
        controller.registerAction(actionState);
        actions.add(actionState);
        return actionState;
    }

    public void unregisterAction(ActionState actionState) {
        actionState.getController().unregisterAction(actionState);
        actions.remove(actionState);
    }

    public void unregisterAllActions() {
        actions.forEach(this::unregisterAction);
    }
}
