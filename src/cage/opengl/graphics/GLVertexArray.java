package cage.opengl.graphics;

import cage.core.graphics.VertexArray;
import cage.core.graphics.VertexBuffer;
import cage.core.graphics.type.LayoutType;

import static cage.core.graphics.type.LayoutType.*;
import static cage.opengl.utils.GLUtils.checkError;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class GLVertexArray extends VertexArray implements IGLObject {

    private int m_vertexArrayId;

    public GLVertexArray() {
        super();

        int[] arrays = new int[1];
        glGenVertexArrays(arrays);
        m_vertexArrayId = arrays[0];
    }

    @Override
    public void destroy() {
        if(m_vertexArrayId != GL_FALSE) {
            glDeleteVertexArrays(new int[]{ m_vertexArrayId });
        }
    }

    @Override
    public void bind() {
        glBindVertexArray(m_vertexArrayId);
        checkError("glBindVertexArray");
        for(int i = 0; i < m_attributeCount; ++i) {
            glEnableVertexAttribArray(i);
            checkError("glEnableVertexAttribArray");
        }
    }

    @Override
    public void unbind() {
        for(int i = 0; i < m_attributeCount; ++i) {
            glDisableVertexAttribArray(i);
            checkError("glDisableVertexAttribArray");
        }
        glBindVertexArray(0);
    }

    @Override
    public void attachVertexBuffer(VertexBuffer buffer) {
        if(buffer instanceof GLVertexBuffer) {
            GLVertexBuffer glBuffer = (GLVertexBuffer)buffer;

            glBindVertexArray(m_vertexArrayId);
            glBindBuffer(GL_ARRAY_BUFFER, glBuffer.getBufferId());

            int totalBytes = 0;
            int unitFlag, unitCount;
            for(int i = 0; i < glBuffer.getLayout().getLayoutStack().size(); ++i) {
                LayoutType type = glBuffer.getLayout().getLayoutStack().get(i);
                switch(type) {
                    case INT1: unitFlag = GL_INT; unitCount = 1; break;
                    case SHORT1: unitFlag = GL_SHORT; unitCount = 1; break;
                    case FLOAT2: unitFlag = GL_FLOAT; unitCount = 2; break;
                    case FLOAT3: unitFlag = GL_FLOAT; unitCount = 3; break;
                    case FLOAT4: unitFlag = GL_FLOAT; unitCount = 4; break;
                    case FLOAT1:
                    default: unitFlag = GL_FLOAT; unitCount = 1; break;
                }

                glVertexAttribPointer(
                        i + m_attributeCount,
                        unitCount, unitFlag, false,
                        glBuffer.getLayout().getUnitSize(),
                        totalBytes);

                switch(unitFlag){
                    case GL_INT: totalBytes += unitCount * sizeof(INT1); break;
                    case GL_SHORT: totalBytes += unitCount * sizeof(SHORT1); break;
                    case GL_FLOAT: totalBytes += unitCount * sizeof(FLOAT1); break;
                }
            }

            m_attributeCount += glBuffer.getLayout().getLayoutStack().size();

            glBindBuffer(GL_ARRAY_BUFFER, 0);
            glBindVertexArray(0);

            m_vertexBuffers.add(glBuffer);
        }
    }

    @Override
    public void detachVertexBuffer(VertexBuffer buffer) {
        if(m_vertexBuffers.remove(buffer)) {
            m_attributeCount -= buffer.getLayout().getLayoutStack().size();
        }
    }

    public int getVertexArrayId() {
        return m_vertexArrayId;
    }
}
