package cn.yuyao.cefjump.dto;

import java.util.List;

public class ParamDesc {
    // 函数的出参
    private Param returnDesc;
    // 函数的入参
    private List<Param> paramDescList;

    public static final String RETURN = "return";

    public Param getReturnDesc() {
        return returnDesc;
    }

    public void setReturnDesc(Param returnDesc) {
        this.returnDesc = returnDesc;
    }

    public List<Param> getParamDescList() {
        return paramDescList;
    }

    public void setParamDescList(List<Param> paramDescList) {
        this.paramDescList = paramDescList;
    }
}
