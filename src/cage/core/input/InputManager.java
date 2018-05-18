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

    public ActionState addAction(InputController controller, InputComponent component, InputActionType actionType, InputAction action) {
        ActionState actionState = new ActionState(action, controller, component, actionType);
        controller.addActionState(actionState);
        actions.add(actionState);
        return actionState;
    }

    public void removeAction(ActionState action) {
        action.getController().removeActionState(action);
        actions.remove(action);
    }

    public void removeAction(int index) {
        ActionState action = actions.remove(index);
        action.getController().removeActionState(action);
    }

    public void removeAllActions() {
        actions.forEach(this::removeAction);
    }

    public int getActionCount() {
        return actions.size();
    }

    public boolean containsAction(ActionState action) {
        return actions.contains(action);
    }

    public ActionState getAction(int index) {
        return actions.get(index);
    }

    public Iterator<ActionState> getActionIterator() {
        return actions.iterator();
    }
}
