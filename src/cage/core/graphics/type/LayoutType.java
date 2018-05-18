package cage.core.graphics.type;

public enum LayoutType {
    INT1, INT2, INT3, INT4, SHORT1, FLOAT1, FLOAT2, FLOAT3, FLOAT4, MAT3, MAT4;

    public static int sizeof(LayoutType type){
        switch(type){
            case SHORT1: return 2;
            case INT1:
            case FLOAT1: return 4;
            case INT2:
            case FLOAT2: return 8;
            case INT3:
            case FLOAT3: return 12;
            case INT4:
            case FLOAT4: return 16;
            case MAT3: return 36;
            case MAT4: return 64;
            default: return 0;
        }
    }
}