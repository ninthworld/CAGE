package cage.opengl.graphics.vertexarray;

import cage.core.graphics.vertexarray.VertexArray;
import cage.core.graphics.buffer.VertexBuffer;
import cage.core.graphics.type.LayoutType;
import cage.opengl.common.GLBindable;
import cage.opengl.graphics.buffer.GLVertexBuffer;

import static cage.core.graphics.type.LayoutType.*;
import static cage.opengl.utils.GLUtils.checkError;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class GLVertexArray extends VertexArray implements GLBindable {

    private int vertexArrayId;

    public GLVertexArray() {
        super();

        int[] arrays = new int[1];
        glGenVertexArrays(arrays);
        this.vertexArrayId = arrays[0];
    }

    @Override
    public void destroy() {
        if(vertexArrayId != GL_FALSE) {
            glDeleteVertexArrays(new int[]{ vertexArrayId });
        }
    }

    @Override
    public void bind() {
        glBindVertexArray(vertexArrayId);
        checkError("glBindVertexArray");
        for(int i = 0; i < getAttributeCount(); ++i) {
            glEnableVertexAttribArray(i);
            checkError("glEnableVertexAttribArray");
        }
    }

    @Override
    public void unbind() {
        for(int i = 0; i < getAttributeCount(); ++i) {
            glDisableVertexAttribArray(i);
            checkError("glDisableVertexAttribArray");
        }
        glBindVertexArray(0);
    }

    @Override
    public void addVertexBuffer(VertexBuffer buffer) {
        if(buffer instanceof GLVertexBuffer) {
            GLVertexBuffer glBuffer = (GLVertexBuffer)buffer;

            glBindVertexArray(vertexArrayId);
            glBindBuffer(GL_ARRAY_BUFFER, glBuffer.getBufferId());

            int totalBytes = 0;
            int unitFlag, unitCount;
            for(int i = 0; i < glBuffer.getLayout().getLayoutStack().size(); ++i) {
                LayoutType type = glBuffer.getLayout().getLayoutStack().get(i);
                switch(type) {
                    case INT1: unitFlag = GL_INT; unitCount = 1; break;
                    case INT2: unitFlag = GL_INT; unitCount = 2; break;
                    case INT3: unitFlag = GL_INT; unitCount = 3; break;
                    case INT4: unitFlag = GL_INT; unitCount = 4; break;
                    case SHORT1: unitFlag = GL_SHORT; unitCount = 1; break;
                    case FLOAT2: unitFlag = GL_FLOAT; unitCount = 2; break;
                    case FLOAT3: unitFlag = GL_FLOAT; unitCount = 3; break;
                    case FLOAT4: unitFlag = GL_FLOAT; unitCount = 4; break;
                    case FLOAT1:
                    default: unitFlag = GL_FLOAT; unitCount = 1; break;
                }

                glVertexAttribPointer(
                        i + getAttributeCount(),
                        unitCount, unitFlag, false,
                        glBuffer.getLayout().getUnitSize(),
                        totalBytes);

                switch(unitFlag){
                    case GL_INT: totalBytes += unitCount * sizeof(INT1); break;
                    case GL_SHORT: totalBytes += unitCount * sizeof(SHORT1); break;
                    case GL_FLOAT: totalBytes += unitCount * sizeof(FLOAT1); break;
                }
            }

            glBindBuffer(GL_ARRAY_BUFFER, 0);
            glBindVertexArray(0);
        }

        super.addVertexBuffer(buffer);
    }

    public int getVertexArrayId() {
        return vertexArrayId;
    }
}
