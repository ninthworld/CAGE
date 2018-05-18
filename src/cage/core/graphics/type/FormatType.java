package cage.core.graphics.type;

public enum FormatType {
    RGBA_8_UNORM,
    RGB_8_UNORM,
    RG_8_UNORM,
    R_8_UNORM,

    RGBA_16_UNORM,
    RGB_16_UNORM,
    RG_16_UNORM,
    R_16_UNORM,

    DEPTH_16,
    DEPTH_32,
    DEPTH_24_STENCIL_8;

    public int getBits() {
        switch (this) {
            case RGBA_8_UNORM:
            case RGB_8_UNORM:
            case RG_8_UNORM:
            case R_8_UNORM:
                return 8;
            case RGBA_16_UNORM:
            case RGB_16_UNORM:
            case RG_16_UNORM:
            case R_16_UNORM:
            case DEPTH_16:
                return 16;
            case DEPTH_24_STENCIL_8:
                return 24;
            case DEPTH_32:
                return 32;
            default: return 0;
        }
    }

    public int getChannels() {
        switch (this) {
            case RGBA_8_UNORM:
            case RGBA_16_UNORM:
                return 4;
            case RGB_8_UNORM:
            case RGB_16_UNORM:
                return 3;
            case RG_8_UNORM:
            case RG_16_UNORM:
                return 2;
            case R_8_UNORM:
            case R_16_UNORM:
                return 1;
            default: return 0;
        }
    }
}
