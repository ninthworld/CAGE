package cage.opengl.utils;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

public class GLUtils {

    public static void checkError(String name) {
        switch(glGetError()){
            case GL_INVALID_ENUM: logWarning(name + ": GL_INVALID_ENUM"); break;
            case GL_INVALID_VALUE: logWarning(name + ": GL_INVALID_VALUE"); break;
            case GL_INVALID_OPERATION: logWarning(name + ": GL_INVALID_OPERATION"); break;
            case GL_INVALID_FRAMEBUFFER_OPERATION: logWarning(name + ": GL_INVALID_FRAMEBUFFER_OPERATION"); break;
            case GL_OUT_OF_MEMORY: logWarning(name + ": GL_OUT_OF_MEMORY"); break;
        }
    }

    public static void checkFramebufferStatus() {
        switch(glCheckFramebufferStatus(GL_FRAMEBUFFER)) {
            case GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT: logWarning("GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT"); break;
            case GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT: logWarning("GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT"); break;
            case GL_FRAMEBUFFER_UNSUPPORTED: logWarning("GL_FRAMEBUFFER_UNSUPPORTED"); break;
        }
    }

    public static void logWarning(String str){
        System.out.println("Warning: " + str);
    }

    public static void logError(String str){
        System.err.println(str);
    }
}
