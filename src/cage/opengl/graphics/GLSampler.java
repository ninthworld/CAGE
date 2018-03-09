package cage.opengl.graphics;

import cage.core.graphics.Sampler;
import cage.core.graphics.type.CompareType;
import cage.core.graphics.type.EdgeType;
import cage.core.graphics.type.FilterType;

import java.awt.*;

import static cage.opengl.graphics.type.GLTypeUtils.*;
import static org.lwjgl.opengl.EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL14.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL33.*;

public class GLSampler extends Sampler implements IGLObject {

    private int m_samplerId;

    public GLSampler() {
        super();

        int[] samplers = new int[1];
        glGenSamplers(samplers);
        m_samplerId = samplers[0];

        initialize();
    }

    @Override
    public void destroy() {
        if(m_samplerId > 0) {
            glDeleteSamplers(new int[]{ m_samplerId });
        }
    }

    @Override
    public void bind() {
    }

    @Override
    public void unbind() {
    }

    public void initialize() {
        glSamplerParameteri(m_samplerId, GL_TEXTURE_WRAP_S, getGLEdgeType(m_edgeU));
        glSamplerParameteri(m_samplerId, GL_TEXTURE_WRAP_T, getGLEdgeType(m_edgeV));
        glSamplerParameteri(m_samplerId, GL_TEXTURE_WRAP_R, getGLEdgeType(m_edgeW));

        if(m_mipmapping) {
            glSamplerParameteri(m_samplerId, GL_TEXTURE_MIN_FILTER, getGLFilterType(m_min, m_mipmap));
            glSamplerParameteri(m_samplerId, GL_TEXTURE_MAG_FILTER, getGLFilterType(m_mag, m_mipmap));

            glSamplerParameterf(m_samplerId, GL_TEXTURE_MIN_LOD, m_minLod);
            glSamplerParameterf(m_samplerId, GL_TEXTURE_MAX_LOD, m_maxLod);
            glSamplerParameterf(m_samplerId, GL_TEXTURE_LOD_BIAS, m_biasLod);
        }
        else {
            glSamplerParameteri(m_samplerId, GL_TEXTURE_MIN_FILTER, getGLFilterType(m_min));
            glSamplerParameteri(m_samplerId, GL_TEXTURE_MAG_FILTER, getGLFilterType(m_mag));
        }

        if(m_anisotropy) {
            glSamplerParameterf(m_samplerId, GL_TEXTURE_MAX_ANISOTROPY_EXT, m_maxAnisotropy);
        }

        if(m_compare) {
            glSamplerParameteri(m_samplerId, GL_TEXTURE_COMPARE_MODE, GL_COMPARE_REF_TO_TEXTURE);
            glSamplerParameteri(m_samplerId, GL_TEXTURE_COMPARE_FUNC, getGLCompareType(m_compareFunc));
        }

        float[] borderColor = new float[]{
                m_border.getRed() / 255.0f,
                m_border.getGreen() / 255.0f,
                m_border.getBlue() / 255.0f,
                m_border.getAlpha() / 255.0f
        };
        glSamplerParameterfv(m_samplerId, GL_TEXTURE_BORDER_COLOR, borderColor);
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
        return m_samplerId;
    }
}