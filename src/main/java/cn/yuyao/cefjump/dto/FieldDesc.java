package cn.yuyao.cefjump.dto;

import java.util.List;

public class FieldDesc {

    private String type;
    private String name;
    // 字段本身可能是其他dto，进行递归构建
    private List<FieldDesc> recursionList;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<FieldDesc> getRecursionList() {
        return recursionList;
    }

    public void setRecursionList(List<FieldDesc> recursionList) {
        this.recursionList = recursionList;
    }
}
