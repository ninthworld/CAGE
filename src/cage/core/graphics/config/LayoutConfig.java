package cage.core.graphics.config;

import cage.core.graphics.type.LayoutType;

import java.util.ArrayList;
import java.util.List;

import static cage.core.graphics.type.LayoutType.*;

public class LayoutConfig {

    private int m_unitSize;
    private List<LayoutType> m_layoutStack;

    public LayoutConfig(){
        m_unitSize = 0;
        m_layoutStack = new ArrayList<>();
    }

    public LayoutConfig int1(){
        m_unitSize += LayoutType.sizeof(INT1);
        m_layoutStack.add(INT1);
        return this;
    }

    public LayoutConfig short1(){
        m_unitSize += LayoutType.sizeof(SHORT1);
        m_layoutStack.add(SHORT1);
        return this;
    }

    public LayoutConfig float1(){
        m_unitSize += LayoutType.sizeof(FLOAT1);
        m_layoutStack.add(FLOAT1);
        return this;
    }

    public LayoutConfig float2(){
        m_unitSize += LayoutType.sizeof(FLOAT2);
        m_layoutStack.add(FLOAT2);
        return this;
    }

    public LayoutConfig float3(){
        m_unitSize += LayoutType.sizeof(FLOAT3);
        m_layoutStack.add(FLOAT3);
        return this;
    }

    public LayoutConfig float4(){
        m_unitSize += LayoutType.sizeof(FLOAT4);
        m_layoutStack.add(FLOAT4);
        return this;
    }

    public LayoutConfig float3x3(){
        m_unitSize += LayoutType.sizeof(FLOAT3X3);
        m_layoutStack.add(FLOAT3X3);
        return this;
    }

    public LayoutConfig float4x4(){
        m_unitSize += LayoutType.sizeof(FLOAT4X4);
        m_layoutStack.add(FLOAT4X4);
        return this;
    }

    public List<LayoutType> getLayoutStack(){
        return m_layoutStack;
    }

    public int getUnitSize(){
        return m_unitSize;
    }
}
