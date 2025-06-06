package cn.yuyao.cefjump.dto;

import java.util.List;

public class ParamDesc {
    private Param returnDesc;
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
