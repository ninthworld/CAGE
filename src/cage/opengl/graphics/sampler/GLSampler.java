package cage.opengl.graphics.sampler;

import cage.core.graphics.sampler.Sampler;
import cage.core.graphics.type.CompareType;
import cage.core.graphics.type.EdgeType;
import cage.core.graphics.type.FilterType;
import cage.opengl.common.GLBindable;

import java.awt.*;

import static cage.opengl.graphics.type.GLTypeUtils.*;
import static org.lwjgl.opengl.EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL14.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL33.*;

public class GLSampler extends Sampler implements GLBindable {

    private int samplerId;

    public GLSampler() {
        super();

        int[] samplers = new int[1];
        glGenSamplers(samplers);
        this.samplerId = samplers[0];

        initialize();
    }

    @Override
    public void destroy() {
        if(samplerId > 0) {
            glDeleteSamplers(new int[]{ samplerId });
        }
    }

    @Override
    public void bind() {
    }

    @Override
    public void unbind() {
    }

    public void initialize() {
        glSamplerParameteri(samplerId, GL_TEXTURE_WRAP_S, getGLEdgeType(getEdgeU()));
        glSamplerParameteri(samplerId, GL_TEXTURE_WRAP_T, getGLEdgeType(getEdgeV()));
        glSamplerParameteri(samplerId, GL_TEXTURE_WRAP_R, getGLEdgeType(getEdgeW()));

        if(isMipmapping()) {
            glSamplerParameteri(samplerId, GL_TEXTURE_MIN_FILTER, getGLFilterType(getFilterMin(), getFilterMipmap()));
            glSamplerParameteri(samplerId, GL_TEXTURE_MAG_FILTER, getGLFilterType(getFilterMag(), getFilterMipmap()));

            glSamplerParameterf(samplerId, GL_TEXTURE_MIN_LOD, getMipmapMinLOD());
            glSamplerParameterf(samplerId, GL_TEXTURE_MAX_LOD, getMipmapMaxLOD());
            glSamplerParameterf(samplerId, GL_TEXTURE_LOD_BIAS, getMipmapBiasLOD());
        }
        else {
            glSamplerParameteri(samplerId, GL_TEXTURE_MIN_FILTER, getGLFilterType(getFilterMin()));
            glSamplerParameteri(samplerId, GL_TEXTURE_MAG_FILTER, getGLFilterType(getFilterMag()));
        }

        if(isAnisotropy()) {
            glSamplerParameterf(samplerId, GL_TEXTURE_MAX_ANISOTROPY_EXT, getMaxAnisotropy());
        }

        if(isCompare()) {
            glSamplerParameteri(samplerId, GL_TEXTURE_COMPARE_MODE, GL_COMPARE_REF_TO_TEXTURE);
            glSamplerParameteri(samplerId, GL_TEXTURE_COMPARE_FUNC, getGLCompareType(getCompareFunc()));
        }

        float[] borderColor = new float[]{
                getBorderColor().getRed() / 255.0f,
                getBorderColor().getGreen() / 255.0f,
                getBorderColor().getBlue() / 255.0f,
                getBorderColor().getAlpha() / 255.0f
        };
        glSamplerParameterfv(samplerId, GL_TEXTURE_BORDER_COLOR, borderColor);
    }

    @Override
    public void setEdgeU(EdgeType edge) {
        super.setEdgeU(edge);
        initialize();
    }

    @Override
    public void setEdgeV(EdgeType edge) {
        super.setEdgeV(edge);
        initialize();
    }

    @Override
    public void setEdgeW(EdgeType edge) {
        super.setEdgeW(edge);
        initialize();
    }

    @Override
    public void setFilterMin(FilterType filter) {
        super.setFilterMin(filter);
        initialize();
    }

    @Override
    public void setFilterMag(FilterType filter) {
        super.setFilterMag(filter);
        initialize();
    }

    @Override
    public void setMipmapping(boolean mipmapping) {
        super.setMipmapping(mipmapping);
        initialize();
    }

    @Override
    public void setFilterMipmap(FilterType mipmap) {
        super.setFilterMipmap(mipmap);
        initialize();
    }

    @Override
    public void setMipmapMinLOD(float minLod) {
        super.setMipmapMinLOD(minLod);
        initialize();
    }

    @Override
    public void setMipmapMaxLOD(float maxLod) {
        super.setMipmapMaxLOD(maxLod);
        initialize();
    }

    @Override
    public void setMipmapBiasLOD(float biasLod) {
        super.setMipmapBiasLOD(biasLod);
        initialize();
    }

    @Override
    public void setAnisotropy(boolean anisotropy) {
        super.setAnisotropy(anisotropy);
        initialize();
    }

    @Override
    public void setMaxAnisotropy(int maxAnisotropy) {
        super.setMaxAnisotropy(maxAnisotropy);
        initialize();
    }

    @Override
    public void setCompare(boolean compare) {
        super.setCompare(compare);
        initialize();
    }

    @Override
    public void setCompareFunc(CompareType compareFunc) {
        super.setCompareFunc(compareFunc);
        initialize();
    }

    @Override
    public void setBorderColor(Color border) {
        super.setBorderColor(border);
        initialize();
    }

    public int getSamplerId(){
        return samplerId;
    }
}