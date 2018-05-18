package cage.glfw.utils;

import cage.core.input.component.Axis;
import cage.core.input.component.Button;
import cage.core.input.component.Key;
import cage.core.input.type.InputActionType;

import static org.lwjgl.glfw.GLFW.*;

public class GLFWUtils {

    public static Key getKey(int key) {
        switch(key) {
            case GLFW_KEY_ESCAPE: return Key.ESCAPE;
            case GLFW_KEY_F1: return Key.F1;
            case GLFW_KEY_F2: return Key.F2;
            case GLFW_KEY_F3: return Key.F3;
            case GLFW_KEY_F4: return Key.F4;
            case GLFW_KEY_F5: return Key.F5;
            case GLFW_KEY_F6: return Key.F6;
            case GLFW_KEY_F7: return Key.F7;
            case GLFW_KEY_F8: return Key.F8;
            case GLFW_KEY_F9: return Key.F9;
            case GLFW_KEY_F10: return Key.F10;
            case GLFW_KEY_F11: return Key.F11;
            case GLFW_KEY_F12: return Key.F12;
            case GLFW_KEY_F13: return Key.F13;
            case GLFW_KEY_F14: return Key.F14;
            case GLFW_KEY_F15: return Key.F15;
            case GLFW_KEY_F16: return Key.F16;
            case GLFW_KEY_F17: return Key.F17;
            case GLFW_KEY_F18: return Key.F18;
            case GLFW_KEY_F19: return Key.F19;
            case GLFW_KEY_INSERT: return Key.INSERT;
            case GLFW_KEY_PRINT_SCREEN: return Key.PRINT_SCREEN;
            case GLFW_KEY_PAUSE: return Key.PAUSE;
            case GLFW_KEY_GRAVE_ACCENT: return Key.GRAVE;
            case GLFW_KEY_1: return Key._1;
            case GLFW_KEY_2: return Key._2;
            case GLFW_KEY_3: return Key._3;
            case GLFW_KEY_4: return Key._4;
            case GLFW_KEY_5: return Key._5;
            case GLFW_KEY_6: return Key._6;
            case GLFW_KEY_7: return Key._7;
            case GLFW_KEY_8: return Key._8;
            case GLFW_KEY_9: return Key._9;
            case GLFW_KEY_0: return Key._0;
            case GLFW_KEY_MINUS: return Key.MINUS;
            case GLFW_KEY_EQUAL: return Key.EQUALS;
            case GLFW_KEY_BACKSPACE: return Key.BACKSPACE;
            case GLFW_KEY_HOME: return Key.HOME;
            case GLFW_KEY_END: return Key.END;
            case GLFW_KEY_NUM_LOCK: return Key.NUMLOCK;
            case GLFW_KEY_KP_DIVIDE: return Key.NUMPAD_DIVIDE;
            case GLFW_KEY_KP_MULTIPLY: return Key.NUMPAD_MULTIPLY;
            case GLFW_KEY_KP_SUBTRACT: return Key.NUMPAD_SUBTRACT;
            case GLFW_KEY_TAB: return Key.TAB;
            case GLFW_KEY_Q: return Key.Q;
            case GLFW_KEY_W: return Key.W;
            case GLFW_KEY_E: return Key.E;
            case GLFW_KEY_R: return Key.R;
            case GLFW_KEY_T: return Key.T;
            case GLFW_KEY_Y: return Key.Y;
            case GLFW_KEY_U: return Key.U;
            case GLFW_KEY_I: return Key.I;
            case GLFW_KEY_O: return Key.O;
            case GLFW_KEY_P: return Key.P;
            case GLFW_KEY_LEFT_BRACKET: return Key.LBRACKET;
            case GLFW_KEY_RIGHT_BRACKET: return Key.RBRACKET;
            case GLFW_KEY_BACKSLASH: return Key.BACKSLASH;
            case GLFW_KEY_DELETE: return Key.DELETE;
            case GLFW_KEY_PAGE_UP: return Key.PAGEUP;
            case GLFW_KEY_KP_7: return Key.NUMPAD7;
            case GLFW_KEY_KP_8: return Key.NUMPAD8;
            case GLFW_KEY_KP_9: return Key.NUMPAD9;
            case GLFW_KEY_KP_ADD: return Key.NUMPAD_ADD;
            case GLFW_KEY_CAPS_LOCK: return Key.CAPSLOCK;
            case GLFW_KEY_A: return Key.A;
            case GLFW_KEY_S: return Key.S;
            case GLFW_KEY_D: return Key.D;
            case GLFW_KEY_F: return Key.F;
            case GLFW_KEY_G: return Key.G;
            case GLFW_KEY_H: return Key.H;
            case GLFW_KEY_J: return Key.J;
            case GLFW_KEY_K: return Key.K;
            case GLFW_KEY_L: return Key.L;
            case GLFW_KEY_SEMICOLON: return Key.SEMICOLON;
            case GLFW_KEY_APOSTROPHE: return Key.APOSTROPHE;
            case GLFW_KEY_ENTER: return Key.ENTER;
            case GLFW_KEY_PAGE_DOWN: return Key.PAGEDOWN;
            case GLFW_KEY_KP_4: return Key.NUMPAD4;
            case GLFW_KEY_KP_5: return Key.NUMPAD5;
            case GLFW_KEY_KP_6: return Key.NUMPAD6;
            case GLFW_KEY_LEFT_SHIFT: return Key.LSHIFT;
            case GLFW_KEY_Z: return Key.Z;
            case GLFW_KEY_X: return Key.X;
            case GLFW_KEY_C: return Key.C;
            case GLFW_KEY_V: return Key.V;
            case GLFW_KEY_B: return Key.B;
            case GLFW_KEY_N: return Key.N;
            case GLFW_KEY_M: return Key.M;
            case GLFW_KEY_COMMA: return Key.COMMA;
            case GLFW_KEY_PERIOD: return Key.PERIOD;
            case GLFW_KEY_SLASH: return Key.SLASH;
            case GLFW_KEY_RIGHT_SHIFT: return Key.RSHIFT;
            case GLFW_KEY_UP: return Key.UP;
            case GLFW_KEY_KP_1: return Key.NUMPAD1;
            case GLFW_KEY_KP_2: return Key.NUMPAD2;
            case GLFW_KEY_KP_3: return Key.NUMPAD3;
            case GLFW_KEY_KP_ENTER: return Key.NUMPAD_ENTER;
            case GLFW_KEY_LEFT_CONTROL: return Key.LCONTROL;
            case GLFW_KEY_LEFT_ALT: return Key.LALT;
            case GLFW_KEY_SPACE: return Key.SPACE;
            case GLFW_KEY_RIGHT_ALT: return Key.RALT;
            case GLFW_KEY_RIGHT_CONTROL: return Key.RCONTROL;
            case GLFW_KEY_LEFT: return Key.LEFT;
            case GLFW_KEY_DOWN: return Key.DOWN;
            case GLFW_KEY_RIGHT: return Key.RIGHT;
            case GLFW_KEY_KP_0: return Key.NUMPAD0;
            case GLFW_KEY_KP_DECIMAL: return Key.NUMPAD_DECIMAL;
            default: return Key.NONE;
        }
    }

    public static Axis getAxis(int axis) {
        switch (axis) {
            case GLFW_GAMEPAD_AXIS_LEFT_TRIGGER: return Axis.LEFT_TRIGGER;
            case GLFW_GAMEPAD_AXIS_LEFT_X: return Axis.LEFT_X;
            case GLFW_GAMEPAD_AXIS_LEFT_Y: return Axis.LEFT_Y;
            case GLFW_GAMEPAD_AXIS_RIGHT_TRIGGER: return Axis.RIGHT_TRIGGER;
            case GLFW_GAMEPAD_AXIS_RIGHT_X: return Axis.RIGHT_X;
            case GLFW_GAMEPAD_AXIS_RIGHT_Y: return Axis.RIGHT_Y;
            default: return Axis.NONE;
        }
    }

    public static Button getGamepadButton(int button) {
        switch (button) {
            case GLFW_GAMEPAD_BUTTON_A: return Button.A;
            case GLFW_GAMEPAD_BUTTON_B: return Button.B;
            case GLFW_GAMEPAD_BUTTON_X: return Button.X;
            case GLFW_GAMEPAD_BUTTON_Y: return Button.Y;
            case GLFW_GAMEPAD_BUTTON_BACK: return Button.BACK;
            case GLFW_GAMEPAD_BUTTON_START: return Button.START;
            case GLFW_GAMEPAD_BUTTON_GUIDE: return Button.GUIDE;
            case GLFW_GAMEPAD_BUTTON_DPAD_DOWN: return Button.DPAD_DOWN;
            case GLFW_GAMEPAD_BUTTON_DPAD_LEFT: return Button.DPAD_LEFT;
            case GLFW_GAMEPAD_BUTTON_DPAD_RIGHT: return Button.DPAD_RIGHT;
            case GLFW_GAMEPAD_BUTTON_DPAD_UP: return Button.DPAD_UP;
            case GLFW_GAMEPAD_BUTTON_LEFT_BUMPER: return Button.LEFT_BUMPER;
            case GLFW_GAMEPAD_BUTTON_LEFT_THUMB: return Button.LEFT_THUMB;
            case GLFW_GAMEPAD_BUTTON_RIGHT_BUMPER: return Button.RIGHT_BUMPER;
            case GLFW_GAMEPAD_BUTTON_RIGHT_THUMB: return Button.RIGHT_THUMB;
            default: return Button.NONE;
        }
    }

    public static Button getMouseButton(int button) {
        switch (button) {
            case GLFW_MOUSE_BUTTON_LEFT: return Button.LEFT;
            case GLFW_MOUSE_BUTTON_MIDDLE: return Button.MIDDLE;
            case GLFW_MOUSE_BUTTON_RIGHT: return Button.RIGHT;
            case GLFW_MOUSE_BUTTON_4: return Button._4;
            case GLFW_MOUSE_BUTTON_5: return Button._5;
            case GLFW_MOUSE_BUTTON_6: return Button._6;
            case GLFW_MOUSE_BUTTON_7: return Button._7;
            case GLFW_MOUSE_BUTTON_8: return Button._8;
            default: return Button.NONE;
        }
    }

    public static InputActionType getAction(int action) {
        switch(action) {
            case GLFW_PRESS: return InputActionType.PRESS;
            case GLFW_RELEASE: return InputActionType.RELEASE;
            case GLFW_REPEAT: return InputActionType.REPEAT;
            default: return InputActionType.NONE;
        }
    }

    public static int getGLFWKey(Key key) {
        switch(key) {
            case ESCAPE: return GLFW_KEY_ESCAPE;
            case F1: return GLFW_KEY_F1;
            case F2: return GLFW_KEY_F2;
            case F3: return GLFW_KEY_F3;
            case F4: return GLFW_KEY_F4;
            case F5: return GLFW_KEY_F5;
            case F6: return GLFW_KEY_F6;
            case F7: return GLFW_KEY_F7;
            case F8: return GLFW_KEY_F8;
            case F9: return GLFW_KEY_F9;
            case F10: return GLFW_KEY_F10;
            case F11: return GLFW_KEY_F11;
            case F12: return GLFW_KEY_F12;
            case F13: return GLFW_KEY_F13;
            case F14: return GLFW_KEY_F14;
            case F15: return GLFW_KEY_F15;
            case F16: return GLFW_KEY_F16;
            case F17: return GLFW_KEY_F17;
            case F18: return GLFW_KEY_F18;
            case F19: return GLFW_KEY_F19;
            case INSERT: return GLFW_KEY_INSERT;
            case PRINT_SCREEN: return GLFW_KEY_PRINT_SCREEN;
            case PAUSE: return GLFW_KEY_PAUSE;
            case GRAVE: return GLFW_KEY_GRAVE_ACCENT;
            case _1: return GLFW_KEY_1;
            case _2: return GLFW_KEY_2;
            case _3: return GLFW_KEY_3;
            case _4: return GLFW_KEY_4;
            case _5: return GLFW_KEY_5;
            case _6: return GLFW_KEY_6;
            case _7: return GLFW_KEY_7;
            case _8: return GLFW_KEY_8;
            case _9: return GLFW_KEY_9;
            case _0: return GLFW_KEY_0;
            case MINUS: return GLFW_KEY_MINUS;
            case EQUALS: return GLFW_KEY_EQUAL;
            case BACKSPACE: return GLFW_KEY_BACKSPACE;
            case HOME: return GLFW_KEY_HOME;
            case END: return GLFW_KEY_END;
            case NUMLOCK: return GLFW_KEY_NUM_LOCK;
            case NUMPAD_DIVIDE: return GLFW_KEY_KP_DIVIDE;
            case NUMPAD_MULTIPLY: return GLFW_KEY_KP_MULTIPLY;
            case NUMPAD_SUBTRACT: return GLFW_KEY_KP_SUBTRACT;
            case TAB: return GLFW_KEY_TAB;
            case Q: return GLFW_KEY_Q;
            case W: return GLFW_KEY_W;
            case E: return GLFW_KEY_E;
            case R: return GLFW_KEY_R;
            case T: return GLFW_KEY_T;
            case Y: return GLFW_KEY_Y;
            case U: return GLFW_KEY_U;
            case I: return GLFW_KEY_I;
            case O: return GLFW_KEY_O;
            case P: return GLFW_KEY_P;
            case LBRACKET: return GLFW_KEY_LEFT_BRACKET;
            case RBRACKET: return GLFW_KEY_RIGHT_BRACKET;
            case BACKSLASH: return GLFW_KEY_BACKSLASH;
            case DELETE: return GLFW_KEY_DELETE;
            case PAGEUP: return GLFW_KEY_PAGE_UP;
            case NUMPAD7: return GLFW_KEY_KP_7;
            case NUMPAD8: return GLFW_KEY_KP_8;
            case NUMPAD9: return GLFW_KEY_KP_9;
            case NUMPAD_ADD: return GLFW_KEY_KP_ADD;
            case CAPSLOCK: return GLFW_KEY_CAPS_LOCK;
            case A: return GLFW_KEY_A;
            case S: return GLFW_KEY_S;
            case D: return GLFW_KEY_D;
            case F: return GLFW_KEY_F;
            case G: return GLFW_KEY_G;
            case H: return GLFW_KEY_H;
            case J: return GLFW_KEY_J;
            case K: return GLFW_KEY_K;
            case L: return GLFW_KEY_L;
            case SEMICOLON: return GLFW_KEY_SEMICOLON;
            case APOSTROPHE: return GLFW_KEY_APOSTROPHE;
            case ENTER: return GLFW_KEY_ENTER;
            case PAGEDOWN: return GLFW_KEY_PAGE_DOWN;
            case NUMPAD4: return GLFW_KEY_KP_4;
            case NUMPAD5: return GLFW_KEY_KP_5;
            case NUMPAD6: return GLFW_KEY_KP_6;
            case LSHIFT: return GLFW_KEY_LEFT_SHIFT;
            case Z: return GLFW_KEY_Z;
            case X: return GLFW_KEY_X;
            case C: return GLFW_KEY_C;
            case V: return GLFW_KEY_V;
            case B: return GLFW_KEY_B;
            case N: return GLFW_KEY_N;
            case M: return GLFW_KEY_M;
            case COMMA: return GLFW_KEY_COMMA;
            case PERIOD: return GLFW_KEY_PERIOD;
            case SLASH: return GLFW_KEY_SLASH;
            case RSHIFT: return GLFW_KEY_RIGHT_SHIFT;
            case UP: return GLFW_KEY_UP;
            case NUMPAD1: return GLFW_KEY_KP_1;
            case NUMPAD2: return GLFW_KEY_KP_2;
            case NUMPAD3: return GLFW_KEY_KP_3;
            case NUMPAD_ENTER: return GLFW_KEY_KP_ENTER;
            case LCONTROL: return GLFW_KEY_LEFT_CONTROL;
            case LALT: return GLFW_KEY_LEFT_ALT;
            case SPACE: return GLFW_KEY_SPACE;
            case RALT: return GLFW_KEY_RIGHT_ALT;
            case RCONTROL: return GLFW_KEY_RIGHT_CONTROL;
            case LEFT: return GLFW_KEY_LEFT;
            case DOWN: return GLFW_KEY_DOWN;
            case RIGHT: return GLFW_KEY_RIGHT;
            case NUMPAD0: return GLFW_KEY_KP_0;
            case NUMPAD_DECIMAL: return GLFW_KEY_KP_DECIMAL;
            default: return 0;
        }
    }

    public static int getGLFWAxis(Axis axis) {
        switch (axis) {
            case LEFT_TRIGGER: return GLFW_GAMEPAD_AXIS_LEFT_TRIGGER;
            case LEFT_X: return GLFW_GAMEPAD_AXIS_LEFT_X;
            case LEFT_Y: return GLFW_GAMEPAD_AXIS_LEFT_Y;
            case RIGHT_TRIGGER: return GLFW_GAMEPAD_AXIS_RIGHT_TRIGGER;
            case RIGHT_X: return GLFW_GAMEPAD_AXIS_RIGHT_X;
            case RIGHT_Y: return GLFW_GAMEPAD_AXIS_RIGHT_Y;
            default: return 0;
        }
    }

    public static int getGLFWGamepadButton(Button button) {
        switch (button) {
            case A: return GLFW_GAMEPAD_BUTTON_A;
            case B: return GLFW_GAMEPAD_BUTTON_B;
            case X: return GLFW_GAMEPAD_BUTTON_X;
            case Y: return GLFW_GAMEPAD_BUTTON_Y;
            case BACK: return GLFW_GAMEPAD_BUTTON_BACK;
            case START: return GLFW_GAMEPAD_BUTTON_START;
            case GUIDE: return GLFW_GAMEPAD_BUTTON_GUIDE;
            case DPAD_DOWN: return GLFW_GAMEPAD_BUTTON_DPAD_DOWN;
            case DPAD_LEFT: return GLFW_GAMEPAD_BUTTON_DPAD_LEFT;
            case DPAD_RIGHT: return GLFW_GAMEPAD_BUTTON_DPAD_RIGHT;
            case DPAD_UP: return GLFW_GAMEPAD_BUTTON_DPAD_UP;
            case LEFT_BUMPER: return GLFW_GAMEPAD_BUTTON_LEFT_BUMPER;
            case LEFT_THUMB: return GLFW_GAMEPAD_BUTTON_LEFT_THUMB;
            case RIGHT_BUMPER: return GLFW_GAMEPAD_BUTTON_RIGHT_BUMPER;
            case RIGHT_THUMB: return GLFW_GAMEPAD_BUTTON_RIGHT_THUMB;
            default: return 0;
        }
    }

    public static int getGLFWMouseButton(Button button) {
        switch (button) {
            case LEFT: return GLFW_MOUSE_BUTTON_LEFT;
            case MIDDLE: return GLFW_MOUSE_BUTTON_MIDDLE;
            case RIGHT: return GLFW_MOUSE_BUTTON_RIGHT;
            case _4: return GLFW_MOUSE_BUTTON_4;
            case _5: return GLFW_MOUSE_BUTTON_5;
            case _6: return GLFW_MOUSE_BUTTON_6;
            case _7: return GLFW_MOUSE_BUTTON_7;
            case _8: return GLFW_MOUSE_BUTTON_8;
            default: return 0;
        }
    }

    public static int getGLFWAction(InputActionType action) {
        switch(action) {
            case PRESS: return GLFW_PRESS;
            case RELEASE: return GLFW_RELEASE;
            case REPEAT: return GLFW_REPEAT;
            default: return 0;
        }
    }
}
