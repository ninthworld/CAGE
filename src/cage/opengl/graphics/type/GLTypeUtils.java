package cage.opengl.graphics.type;

import cage.core.graphics.type.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL14.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL33.*;

public class GLTypeUtils {

    public static int getGLCubeMapFace(CubeFaceType face) {
        switch (face) {
            case LEFT: return GL_TEXTURE_CUBE_MAP_NEGATIVE_X;
            case TOP: return GL_TEXTURE_CUBE_MAP_POSITIVE_Y;
            case BOTTOM: return GL_TEXTURE_CUBE_MAP_NEGATIVE_Y;
            case BACK: return GL_TEXTURE_CUBE_MAP_POSITIVE_Z;
            case FRONT: return GL_TEXTURE_CUBE_MAP_NEGATIVE_Z;
            case RIGHT:
            default: return GL_TEXTURE_CUBE_MAP_POSITIVE_X;
        }
    }

    public static int getGLBlendEquation(BlendOpType func) {
        switch(func) {
            case SUBTRACT: return GL_FUNC_SUBTRACT;
            case REV_SUBTRACT: return GL_FUNC_REVERSE_SUBTRACT;
            case MIN: return GL_MIN;
            case MAX: return GL_MAX;
            case ADD:
            default: return GL_FUNC_ADD;
        }
    }

    public static int getGLBlendType(BlendType type) {
        switch(type) {
            case ONE: return GL_ONE;
            case SRC_COLOR: return GL_SRC_COLOR;
            case INV_SRC_COLOR: return GL_ONE_MINUS_SRC_COLOR;
            case SRC_ALPHA: return GL_SRC_ALPHA;
            case INV_SRC_ALPHA: return GL_ONE_MINUS_SRC_ALPHA;
            case DEST_ALPHA: return GL_DST_ALPHA;
            case INV_DEST_ALPHA: return GL_ONE_MINUS_DST_ALPHA;
            case DEST_COLOR: return GL_DST_COLOR;
            case INV_DEST_COLOR: return GL_ONE_MINUS_DST_COLOR;
            case BLEND_FACTOR: return GL_CONSTANT_COLOR;
            case INV_BLEND_FACTOR: return GL_ONE_MINUS_CONSTANT_COLOR;
            case SRC1_COLOR: return GL_SRC1_COLOR;
            case INV_SRC1_COLOR: return GL_ONE_MINUS_SRC1_COLOR;
            case SRC1_ALPHA: return GL_SRC1_ALPHA;
            case INV_SRC1_ALPHA: return GL_ONE_MINUS_SRC1_ALPHA;
            case ZERO:
            default: return GL_ZERO;
        }
    }

    public static int getGLCompareType(CompareType compare) {
        switch(compare) {
            case LESS_EQUAL: return GL_LEQUAL;
            case GREATER_EQUAL: return GL_GEQUAL;
            case LESS: return GL_LESS;
            case GREATER: return GL_GREATER;
            case EQUAL: return GL_EQUAL;
            case NOT_EQUAL: return GL_NOTEQUAL;
            case ALWAYS: return GL_ALWAYS;
            case NEVER:
            default: return GL_NEVER;
        }
    }

    public static int getGLEdgeType(EdgeType edge) {
        switch(edge) {
            case MIRROR: return GL_REPEAT;
            case CLAMP: return GL_CLAMP_TO_EDGE;
            case BORDER: return GL_CLAMP_TO_BORDER;
            case WRAP:
            default: return GL_REPEAT;
        }
    }

    public static int getGLFilterType(FilterType filter) {
        switch(filter) {
            case NEAREST: return GL_NEAREST;
            case LINEAR:
            default: return GL_LINEAR;
        }
    }

    public static int getGLFilterType(FilterType filter, FilterType mipmap) {
        switch(filter) {
            case NEAREST:
                switch(mipmap) {
                    case NEAREST: return GL_NEAREST_MIPMAP_NEAREST;
                    case LINEAR:
                    default: return GL_NEAREST_MIPMAP_LINEAR;
                }
            case LINEAR:
            default:
                switch(mipmap) {
                    case NEAREST: return GL_LINEAR_MIPMAP_NEAREST;
                    case LINEAR:
                    default: return GL_LINEAR_MIPMAP_LINEAR;
                }
        }
    }
    
    public static int getGLDataType(FormatType format) {
        switch(format) {
            case RGBA_32_FLOAT: return GL_FLOAT;
            case DEPTH_24_STENCIL_8: return GL_UNSIGNED_INT_24_8;
            case DEPTH_32: return GL_UNSIGNED_INT;
            case DEPTH_16:
            case R_16_UNORM:
            case RG_16_UNORM:
            case RGB_16_UNORM:
            case RGBA_16_UNORM: return GL_UNSIGNED_SHORT;
            case R_8_UNORM:
            case RG_8_UNORM:
            case RGB_8_UNORM:
            case RGBA_8_UNORM:
            default: return GL_UNSIGNED_BYTE;            
        }
    }

    public static int getGLFormatType(FormatType format) {
        switch(format) {
            case DEPTH_16:
            case DEPTH_32: return GL_DEPTH_COMPONENT;
            case DEPTH_24_STENCIL_8: return GL_DEPTH_STENCIL;
            case RGBA_8_UNORM:
            case RGBA_16_UNORM:
            case RGBA_32_FLOAT: return GL_RGBA;
            case RGB_8_UNORM:
            case RGB_16_UNORM: return GL_RGB;
            case RG_8_UNORM:
            case RG_16_UNORM: return GL_RG;
            case R_8_UNORM:
            case R_16_UNORM:
            default: return GL_RED;
        }
    }

    public static int getGLInternalFormatType(FormatType format) {
        switch(format) {
            case RGBA_32_FLOAT: return GL_RGBA32F;
            case RGBA_16_UNORM: return GL_RGBA16;
            case RGB_16_UNORM: return GL_RGB16;
            case RG_16_UNORM: return GL_RG16;
            case R_16_UNORM: return GL_R16;
            case RGBA_8_UNORM: return GL_RGBA8;
            case RGB_8_UNORM: return GL_RGB8;
            case RG_8_UNORM: return GL_RG8;
            case R_8_UNORM: return GL_R8;
            default: return getGLFormatType(format);
        }
    }
}
