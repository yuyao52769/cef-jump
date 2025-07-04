package cn.yuyao.cefjump.dto;

import java.util.ArrayList;
import java.util.List;

public class Param {
    // 主要是为了前端可以正确解析，添加一个id
    private String id;
    // 类型
    private String type;
    // 函数入参名(cef二开需要最原始的入参名) 返回参数不需要
    private String name;
    // 注释
    private String desc;
    // 如果类型为dto，这个就是dto的各个字段说明情况
    private List<FieldDesc> fieldDescList = new ArrayList<>();

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
