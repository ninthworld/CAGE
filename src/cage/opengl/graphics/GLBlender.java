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

        if(m_alphaToCoverage) {
            glEnable(GL_SAMPLE_ALPHA_TO_COVERAGE);
        }

        glBlendEquationi(m_index, getGLBlendEquation(m_blendFunc));
        glBlendFunci(m_index, getGLBlendType(m_blendSrc), getGLBlendType(m_blendDest));
        glColorMaski(m_index, m_maskR, m_maskG, m_maskB, m_maskA);
    }

    @Override
    public void unbind() {
        if(m_alphaToCoverage) {
            glDisable(GL_SAMPLE_ALPHA_TO_COVERAGE);
        }

        glDisable(GL_BLEND);
    }
}
