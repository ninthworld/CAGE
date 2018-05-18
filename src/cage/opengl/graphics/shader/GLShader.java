package cage.opengl.graphics.shader;

import cage.core.graphics.buffer.ShaderStorageBuffer;
import cage.core.graphics.shader.Shader;
import cage.core.graphics.texture.Texture;
import cage.core.graphics.buffer.UniformBuffer;
import cage.opengl.common.GLBindable;
import cage.opengl.graphics.buffer.GLShaderStorageBuffer;
import cage.opengl.graphics.sampler.GLSampler;
import cage.opengl.graphics.buffer.GLUniformBuffer;
import cage.opengl.graphics.texture.GLTexture;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import static cage.opengl.utils.GLUtils.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL31.*;
import static org.lwjgl.opengl.GL32.GL_GEOMETRY_SHADER;
import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.opengl.GL43.*;

public class GLShader extends Shader implements GLBindable {

    private Map<String, Integer> shaderStorageBindings;
    private Map<String, Integer> uniformBindings;
    private Map<String, Integer> textureBindings;
    private int programId;

    public GLShader() {
        super();
        this.shaderStorageBindings = new HashMap<>();
        this.uniformBindings = new HashMap<>();
        this.textureBindings = new HashMap<>();

        this.programId = glCreateProgram();
        checkError("glCreateProgram");

        if(this.programId == GL_FALSE){
            logError("Failed to create GLShader Program.");
        }
    }

    private int getShaderStorageBufferIndex(String name) {
        int index;
        if(shaderStorageBindings.containsKey(name)) {
            index = shaderStorageBindings.get(name);
        }
        else {
            index = glGetProgramResourceIndex(programId, GL_SHADER_STORAGE_BLOCK, name);
            checkError("glGetProgramResourceIndex");
        }
        return index;
    }

    @Override
    public void addShaderStorageBuffer(String name, ShaderStorageBuffer buffer) {
        int index = getShaderStorageBufferIndex(name);
        glShaderStorageBlockBinding(programId, index, index);
        checkError("glShaderStorageBlockBinding");
        shaderStorageBuffers.put(index, buffer);
    }

    @Override
    public void removeShaderStorageBuffer(String name) {
        int index = getShaderStorageBufferIndex(name);
        shaderStorageBuffers.remove(index);
    }

    @Override
    public void removeShaderStorageBuffer(String name, ShaderStorageBuffer buffer) {
        int index = getShaderStorageBufferIndex(name);
        shaderStorageBuffers.remove(index, buffer);
    }

    @Override
    public boolean containsShaderStorageBuffer(String name) {
        int index = getShaderStorageBufferIndex(name);
        return shaderStorageBuffers.containsKey(index);
    }

    @Override
    public ShaderStorageBuffer getShaderStorageBuffer(String name) {
        int index = getShaderStorageBufferIndex(name);
        return shaderStorageBuffers.get(index);
    }

    private int getUniformBufferIndex(String name) {
        int index;
        if(uniformBindings.containsKey(name)) {
            index = uniformBindings.get(name);
        }
        else {
            index = glGetUniformBlockIndex(programId, name);
            checkError("glGetUniformBlockIndex");
        }
        return index;
    }

    @Override
    public void addUniformBuffer(String name, UniformBuffer buffer) {
        int index = getUniformBufferIndex(name);
        glUniformBlockBinding(programId, index, index);
        checkError("glUniformBlockBinding");
        uniformBuffers.put(index, buffer);
    }

    @Override
    public void removeUniformBuffer(String name) {
        int index = getUniformBufferIndex(name);
        uniformBuffers.remove(index);
    }

    @Override
    public void removeUniformBuffer(String name, UniformBuffer buffer) {
        int index = getUniformBufferIndex(name);
        uniformBuffers.remove(index, buffer);
    }

    @Override
    public boolean containsUniformBuffer(String name) {
        int index = getUniformBufferIndex(name);
        return uniformBuffers.containsKey(index);
    }

    @Override
    public UniformBuffer getUniformBuffer(String name) {
        int index = getUniformBufferIndex(name);
        return uniformBuffers.get(index);
    }

    private int getTextureIndex(String name) {
        int index;
        if(textureBindings.containsKey(name)) {
            index = textureBindings.get(name);
        }
        else {
            index = glGetUniformLocation(programId, name);
            checkError("glGetUniformLocation");
        }
        return index;
    }

    @Override
    public void addTexture(String name, Texture texture) {
        int index = getTextureIndex(name);
        textures.put(index, texture);
    }

    @Override
    public void removeTexture(String name) {
        int index = getTextureIndex(name);
        textures.remove(index);
    }

    @Override
    public void removeTexture(String name, Texture texture) {
        int index = getTextureIndex(name);
        textures.remove(index, texture);
    }

    @Override
    public boolean containsTexture(String name) {
        int index = getTextureIndex(name);
        return textures.containsKey(index);
    }

    @Override
    public Texture getTexture(String name) {
        int index = getTextureIndex(name);
        return textures.get(index);
    }

    @Override
    public void destroy() {
        unbind();
        if(programId != GL_FALSE) {
            glDeleteProgram(programId);
        }
    }

    @Override
    public void bind() {
        glUseProgram(programId);
        checkError("glUseProgram");
        uniformBuffers.forEach((Integer i, UniformBuffer buffer) -> {
            if(buffer instanceof GLUniformBuffer) {
                glBindBufferBase(GL_UNIFORM_BUFFER, i, ((GLUniformBuffer)buffer).getBufferId());
            }
        });
        shaderStorageBuffers.forEach((Integer i, ShaderStorageBuffer buffer) -> {
            if(buffer instanceof GLShaderStorageBuffer) {
                glBindBufferBase(GL_SHADER_STORAGE_BUFFER, i, ((GLShaderStorageBuffer)buffer).getBufferId());
            }
        });
    }

    public void bindTextures() {    	
    	int j = 0;
    	for(Entry<Integer, Texture> entry : textures.entrySet()) {
    	    Texture texture = entry.getValue();
    		if(texture instanceof GLTexture) {
                GLTexture glTexture = (GLTexture)texture;
                glUniform1i(entry.getKey(), j);
                checkError("glUniform1i");
                glActiveTexture(GL_TEXTURE0 + j);
                glTexture.bind();
                if(texture.getSampler() != null && texture.getSampler() instanceof GLSampler) {
                    glBindSampler(j, ((GLSampler)texture.getSampler()).getSamplerId());
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
        if(!getVertexShaderSource().isEmpty() && !getFragmentShaderSource().isEmpty()) {

            int vertexShaderId = compileShader(getVertexShaderSource(), GL_VERTEX_SHADER);
            int fragmentShaderId = compileShader(getFragmentShaderSource(), GL_FRAGMENT_SHADER);

            if (vertexShaderId != GL_FALSE) {
                glAttachShader(programId, vertexShaderId);
            }

            if (fragmentShaderId != GL_FALSE) {
                glAttachShader(programId, fragmentShaderId);
            }

            if (getGeometryShaderSrc() != null) {
                int geometryShaderId = compileShader(getGeometryShaderSrc(), GL_GEOMETRY_SHADER);
                if (geometryShaderId != GL_FALSE) {
                    glAttachShader(programId, geometryShaderId);
                }
            }

            int[] param = new int[1];
            glLinkProgram(programId);
            glGetProgramiv(programId, GL_LINK_STATUS, param);
            if (param[0] == GL_FALSE) {
                logError(glGetProgramInfoLog(programId));
                logError("Failed to link GL Shader Program.");
                return;
            }

            glValidateProgram(programId);
            glGetProgramiv(programId, GL_VALIDATE_STATUS, param);
            if (param[0] == GL_FALSE) {
                logError(glGetProgramInfoLog(programId));
                logError("Failed to validate GL Shader Program.");
                return;
            }
        }
    }

    public int getProgramId() {
        return programId;
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
