package cage.opengl.graphics;

import cage.core.graphics.Blender;

import static cage.opengl.graphics.type.GLTypeUtils.*;
import static cage.opengl.graphics.type.GLTypeUtils.getGLBlendEquation;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL40.*;

public class GLBlender extends Blender implements IGLObject {

    public GLBlender() {
        super();
    }

    @Override
    public void destroy() {
    }

    @Override
    public void bind() {
        glEnable(GL_BLEND);

        if(isAlphaToCoverage()) {
            glEnable(GL_SAMPLE_ALPHA_TO_COVERAGE);
        }

        glBlendEquationi(getDrawBufferIndex(), getGLBlendEquation(getBlendFunc()));
        glBlendFunci(getDrawBufferIndex(), getGLBlendType(getBlendSrc()), getGLBlendType(getBlendDest()));
        glColorMaski(getDrawBufferIndex(), isMaskingRed(), isMaskingGreen(), isMaskingBlue(), isMaskingAlpha());
    }

    @Override
    public void unbind() {
        if(isAlphaToCoverage()) {
            glDisable(GL_SAMPLE_ALPHA_TO_COVERAGE);
        }

        glDisable(GL_BLEND);
    }
}
