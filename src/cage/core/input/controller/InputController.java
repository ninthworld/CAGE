package cage.core.input.controller;

import cage.core.gui.component.GUIComponent;
import cage.core.input.ActionState;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class InputController {

    private int index;
    private String name;
    private List<ActionState> actionStates;

    public InputController(int index, String name) {
        this.index = index;
        this.name = name;
        this.actionStates = new ArrayList<>();
    }

    public abstract void update(float deltaTime);

    public int getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }

    public void addActionState(ActionState actionState) {
        if(!actionState.getController().equals(this)) {
            return;
        }
        actionStates.add(actionState);
    }

    public void removeActionState(ActionState actionState) {
        if(!actionState.getController().equals(this)) {
            return;
        }
        actionStates.remove(actionState);
    }

    public void removeActionState(int index) {
        if(!actionStates.get(index).getController().equals(this)) {
            return;
        }
        actionStates.remove(index);
    }

    public void removeAllActionStates() {
        actionStates.forEach(this::removeActionState);
    }

    public int getActionStateCount() {
        return actionStates.size();
    }

    public boolean containsActionState(ActionState actionState) {
        return actionStates.contains(actionState);
    }

    public ActionState getActionState(int index) {
        return actionStates.get(index);
    }

    public Iterator<ActionState> getActionStateIterator() {
        return actionStates.iterator();
    }
}
