package cn.yuyao.cefjump.util;

import com.intellij.psi.PsiClass;

public class ClassWrapper {

    private Boolean isVoid = false;

    /**
     * 基础类型，包括数组，list，set，map
     */
    private Boolean isBaseType = false;

    private PsiClass reallyType;

    private String reallyTypeName;

    /**
     * 是否为 list set，不管map
     */
    private Boolean isContain;

    public Boolean getVoid() {
        return isVoid;
    }

    public void setVoid(Boolean aVoid) {
        isVoid = aVoid;
    }

    public Boolean getBaseType() {
        return isBaseType;
    }

    public void setBaseType(Boolean baseType) {
        isBaseType = baseType;
    }

    public PsiClass getReallyType() {
        return reallyType;
    }

    public void setReallyType(PsiClass reallyType) {
        this.reallyType = reallyType;
    }

    public String getReallyTypeName() {
        return reallyTypeName;
    }

    public void setReallyTypeName(String reallyTypeName) {
        this.reallyTypeName = reallyTypeName;
    }

    public Boolean getContain() {
        return isContain;
    }

    public void setContain(Boolean contain) {
        isContain = contain;
    }
}
