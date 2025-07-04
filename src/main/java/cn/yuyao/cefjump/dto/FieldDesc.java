package cn.yuyao.cefjump.dto;

import java.util.ArrayList;
import java.util.List;

public class FieldDesc {
    private String id;
    // 字段的注释
    private String desc;
    // 字段的类型
    private String type;
    // 字段的名称
    private String name;
    // 字段本身可能是其他dto，进行递归构建
    private List<FieldDesc> fieldDescList = new ArrayList<>();

    public FieldDesc() {}

    public FieldDesc(String desc, String type, String name) {
        this.desc = desc;
        this.type = type;
        this.name = name;
    }

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

    public List<FieldDesc> getFieldDescList() {
        return fieldDescList;
    }

    public void setFieldDescList(List<FieldDesc> recursionList) {
        this.fieldDescList = recursionList;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
