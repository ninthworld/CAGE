package cage.opengl.graphics;

import cage.core.graphics.Rasterizer;
import cage.core.graphics.type.CullType;
import cage.core.graphics.type.FillType;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;

public class GLRasterizer extends Rasterizer implements IGLObject {

    public GLRasterizer() {
        super();
    }

    @Override
    public void destroy() {
    }

    @Override
    public void bind() {

    	glEnable(GL_TEXTURE_2D);
    	
        if(m_fill == FillType.SOLID) {
            glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
        }
        else if(m_fill == FillType.WIREFRAME) {
            glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
        }

        if(m_cull == CullType.NONE) {
            glDisable(GL_CULL_FACE);
        }
        else {
            glEnable(GL_CULL_FACE);
            if(m_cull == CullType.BACK) {
                glCullFace(GL_BACK);
            }
            else if(m_cull == CullType.FRONT) {
                glCullFace(GL_FRONT);
            }
            else if(m_cull == CullType.FRONT_AND_BACK) {
                glCullFace(GL_FRONT_AND_BACK);
            }
        }

        if(m_frontCCW) {
            glFrontFace(GL_CCW);
        }
        else {
            glFrontFace(GL_CW);
        }

        if(m_multisampling) {
            glEnable(GL_MULTISAMPLE);
        }
        else {
            glDisable(GL_MULTISAMPLE);
        }

        if(m_scissoring) {
            glEnable(GL_SCISSOR_TEST);
        }
        else {
            glDisable(GL_SCISSOR_TEST);
        }

        if(m_depthClipping) {
            glEnable(GL_DEPTH_TEST);
        }
        else {
            glDisable(GL_DEPTH_TEST);
        }
    }

    @Override
    public void unbind() {
    }
}
