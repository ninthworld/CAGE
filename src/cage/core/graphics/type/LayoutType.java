package cage.core.graphics.type;

public enum LayoutType {
    INT1, SHORT1, FLOAT1, FLOAT2, FLOAT3, FLOAT4, FLOAT3X3, FLOAT4X4;

    public static int sizeof(LayoutType type){
        switch(type){
            case SHORT1: return 2;
            case INT1:
            case FLOAT1: return 4;
            case FLOAT2: return 8;
            case FLOAT3: return 12;
            case FLOAT4: return 16;
            case FLOAT3X3: return 36;
            case FLOAT4X4: return 64;
            default: return 0;
        }
    }
}