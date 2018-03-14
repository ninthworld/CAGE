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
    	
        if(getFillType() == FillType.SOLID) {
            glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
        }
        else if(getFillType() == FillType.WIREFRAME) {
            glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
        }

        if(getCullType() == CullType.NONE) {
            glDisable(GL_CULL_FACE);
        }
        else {
            glEnable(GL_CULL_FACE);
            if(getCullType() == CullType.BACK) {
                glCullFace(GL_BACK);
            }
            else if(getCullType() == CullType.FRONT) {
                glCullFace(GL_FRONT);
            }
            else if(getCullType() == CullType.FRONT_AND_BACK) {
                glCullFace(GL_FRONT_AND_BACK);
            }
        }

        if(isFrontCCW()) {
            glFrontFace(GL_CCW);
        }
        else {
            glFrontFace(GL_CW);
        }

        if(isMultisampling()) {
            glEnable(GL_MULTISAMPLE);
        }
        else {
            glDisable(GL_MULTISAMPLE);
        }

        if(isScissoring()) {
            glEnable(GL_SCISSOR_TEST);
        }
        else {
            glDisable(GL_SCISSOR_TEST);
        }

        if(isDepthClipping()) {
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
