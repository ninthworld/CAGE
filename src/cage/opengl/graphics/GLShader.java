package cage.opengl.graphics;

import cage.core.graphics.Shader;
import cage.core.graphics.Texture;
import cage.core.graphics.UniformBuffer;

import java.nio.ByteBuffer;
import java.util.Map.Entry;

import org.lwjgl.BufferUtils;

import static cage.opengl.utils.GLUtils.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL31.*;
import static org.lwjgl.opengl.GL33.*;

public class GLShader extends Shader implements IGLObject {

    private int m_programId;

    public GLShader() {
        super();

        m_programId = glCreateProgram();
        checkError("glCreateProgram");

        if(m_programId == GL_FALSE){
            logError("Failed to create GLShader Program.");
        }
    }

    private int getUniformBufferIndex(String name) {
        int index = glGetUniformBlockIndex(m_programId, name);
        checkError("glGetUniformBlockIndex");
        return index;
    }

    @Override
    public void attachUniformBuffer(String name, UniformBuffer buffer) {
        int index = getUniformBufferIndex(name);
        glUniformBlockBinding(m_programId, index, index);
        checkError("glUniformBlockBinding");
        m_uniformBuffers.put(index, buffer);
    }

    @Override
    public void detachUniformBuffer(String name) {
        int index = getUniformBufferIndex(name);
        m_uniformBuffers.remove(index);
    }

    @Override
    public boolean containsUniformBuffer(String name) {
        int index = getUniformBufferIndex(name);
        return m_uniformBuffers.containsKey(index);
    }

    @Override
    public UniformBuffer getUniformBuffer(String name) {
        int index = getUniformBufferIndex(name);
        return m_uniformBuffers.get(index);
    }

    private int getTextureIndex(String name) {
        int index = glGetUniformLocation(m_programId, name);
        checkError("glGetUniformLocation");
        return index;
    }

    @Override
    public void attachTexture(String name, Texture texture) {
        int index = getTextureIndex(name);
        m_textures.put(index, texture);
    }

    @Override
    public void detachTexture(String name) {
        int index = getTextureIndex(name);
        m_textures.remove(index);
    }

    @Override
    public boolean containsTexture(String name) {
        int index = getTextureIndex(name);
        return m_textures.containsKey(index);
    }

    @Override
    public Texture getTexture(String name) {
        int index = getTextureIndex(name);
        return m_textures.get(index);
    }

    @Override
    public void destroy() {
        unbind();
        if(m_programId != GL_FALSE) {
            glDeleteProgram(m_programId);
        }
    }

    @Override
    public void bind() {
        glUseProgram(m_programId);
        checkError("glUseProgram");

        m_uniformBuffers.forEach((Integer i, UniformBuffer buffer) -> {
            if(buffer instanceof GLUniformBuffer) {
                glBindBufferBase(GL_UNIFORM_BUFFER, i, ((GLUniformBuffer)buffer).getBufferId());
            }
        });
    }

    public void bindTextures() {    	
    	int j = 0;
    	for(Entry<Integer, Texture> entry : m_textures.entrySet()) {
    		if(entry.getValue() instanceof GLTexture) {
                GLTexture glTexture = (GLTexture)entry.getValue();
                glUniform1i(entry.getKey(), j);
                glActiveTexture(GL_TEXTURE0 + j);
                glTexture.bind();
                if(glTexture.getSampler() != null && glTexture.getSampler() instanceof GLSampler) {
                    glBindSampler(j, ((GLSampler)glTexture.getSampler()).getSamplerId());
                }
            }
    		++j;
    	}
    }

    @Override
    public void unbind() {
        glUseProgram(0);
    }

    @Override
    public void compile(){
        if(!m_vertexShaderSrc.isEmpty() && !m_fragmentShaderSrc.isEmpty()) {

            int vertexShaderId = compileShader(m_vertexShaderSrc, GL_VERTEX_SHADER);
            int fragmentShaderId = compileShader(m_fragmentShaderSrc, GL_FRAGMENT_SHADER);

            if (vertexShaderId != GL_FALSE) {
                glAttachShader(m_programId, vertexShaderId);
            }

            if (fragmentShaderId != GL_FALSE) {
                glAttachShader(m_programId, fragmentShaderId);
            }

            int[] param = new int[1];
            glLinkProgram(m_programId);
            glGetProgramiv(m_programId, GL_LINK_STATUS, param);
            if (param[0] == GL_FALSE) {
                logError(glGetProgramInfoLog(m_programId));
                logError("Failed to link GL Shader Program.");
                return;
            }

            glValidateProgram(m_programId);
            glGetProgramiv(m_programId, GL_VALIDATE_STATUS, param);
            if (param[0] == GL_FALSE) {
                logError(glGetProgramInfoLog(m_programId));
                logError("Failed to validate GL Shader Program.");
                return;
            }
        }
    }

    public int getProgramId() {
        return m_programId;
    }

    private int compileShader(String src, int type) {
        int shaderId = glCreateShader(type);
        checkError("glCreateShader");

        if(shaderId == GL_FALSE){
            logError("Failed to create GLSL Shader.");
            return 0;
        }

        glShaderSource(shaderId, src);
        checkError("glShaderSource");

        glCompileShader(shaderId);
        checkError("glCompileShader");

        int[] param = new int[1];
        glGetShaderiv(shaderId, GL_COMPILE_STATUS, param);
        if(param[0] == GL_FALSE){
        	logError(glGetShaderInfoLog(shaderId));
            logError("Failed to compile GLSL Shader.");
            return 0;
        }

        return shaderId;
    }
}
