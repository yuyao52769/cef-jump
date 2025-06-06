package cn.yuyao.cefjump.dto;

import java.util.List;

public class Param {
    // 类型
    private String reallyType;
    // 函数入参名(cef二开需要最原始的入参名)
    private String paramName;
    // 注释
    private String desc;
    // 如果类型为dto，这个就是dto的各个字段说明情况
    private List<FieldDesc> fieldDescList;

    public String getReallyType() {
        return reallyType;
    }

    public void setReallyType(String reallyType) {
        this.reallyType = reallyType;
    }

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public List<FieldDesc> getFieldDescList() {
        return fieldDescList;
    }

    public void setFieldDescList(List<FieldDesc> fieldDescList) {
        this.fieldDescList = fieldDescList;
    }
}
