package cage.core.graphics.config;

import cage.core.graphics.type.LayoutType;

import java.util.ArrayList;
import java.util.List;

import static cage.core.graphics.type.LayoutType.*;

public class LayoutConfig {

    private List<LayoutType> layoutStack;
    private int unitSize;

    public LayoutConfig(){
        this.unitSize = 0;
        this.layoutStack = new ArrayList<>();
    }

    public LayoutConfig int1(){
        this.unitSize += LayoutType.sizeof(INT1);
        this.layoutStack.add(INT1);
        return this;
    }

    public LayoutConfig int2(){
        this.unitSize += LayoutType.sizeof(INT2);
        this.layoutStack.add(INT2);
        return this;
    }

    public LayoutConfig int3(){
        this.unitSize += LayoutType.sizeof(INT3);
        this.layoutStack.add(INT3);
        return this;
    }

    public LayoutConfig int4(){
        this.unitSize += LayoutType.sizeof(INT4);
        this.layoutStack.add(INT4);
        return this;
    }

    public LayoutConfig short1(){
        this.unitSize += LayoutType.sizeof(SHORT1);
        this.layoutStack.add(SHORT1);
        return this;
    }

    public LayoutConfig float1(){
        this.unitSize += LayoutType.sizeof(FLOAT1);
        this.layoutStack.add(FLOAT1);
        return this;
    }

    public LayoutConfig float2(){
        this.unitSize += LayoutType.sizeof(FLOAT2);
        this.layoutStack.add(FLOAT2);
        return this;
    }

    public LayoutConfig float3(){
        this.unitSize += LayoutType.sizeof(FLOAT3);
        this.layoutStack.add(FLOAT3);
        return this;
    }

    public LayoutConfig float4(){
        this.unitSize += LayoutType.sizeof(FLOAT4);
        this.layoutStack.add(FLOAT4);
        return this;
    }

    public LayoutConfig mat3(){
        this.unitSize += LayoutType.sizeof(MAT3);
        this.layoutStack.add(MAT3);
        return this;
    }

    public LayoutConfig mat4(){
        this.unitSize += LayoutType.sizeof(MAT4);
        this.layoutStack.add(MAT4);
        return this;
    }

    public List<LayoutType> getLayoutStack(){
        return layoutStack;
    }

    public int getUnitSize(){
        return unitSize;
    }
}
