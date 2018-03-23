package cage.core.input.action;

import cage.core.window.Window;

public class CloseWindowAction implements IInputAction {

    private Window window;

    public CloseWindowAction(Window window) {
        this.window = window;
    }

    @Override
    public void performAction(float deltaTime, IInputEvent event) {
        window.setClosed(true);
    }
}
