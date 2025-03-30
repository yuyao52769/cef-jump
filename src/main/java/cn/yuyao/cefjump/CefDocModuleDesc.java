package cn.yuyao.cefjump;

import java.util.List;

public class CefDocModuleDesc {

    private String className;

    private String methodName;

    private String module;

    private String name;

    private String func;

    private String desc;

    private List<OpenFunc> openFuncList;

    @Override
    public String toString() {
        return "CefDocModuleDesc{" +
                "className='" + className + '\'' +
                ", methodName='" + methodName + '\'' +
                ", module='" + module + '\'' +
                ", func='" + func + '\'' +
                ", openFuncList=" + openFuncList +
                '}';
    }

    public static class OpenFunc {

       private OpenTypeEnum type;

       private String openId;

       public OpenTypeEnum getType() {
           return type;
       }

       public void setType(OpenTypeEnum type) {
           this.type = type;
       }

       public String getOpenId() {
           return openId;
       }

       public void setOpenId(String openId) {
           this.openId = openId;
       }

       @Override
       public String toString() {
           return "OpenFunc{" +
                   "type=" + type +
                   ", openId='" + openId + '\'' +
                   '}';
       }
   }

   public static enum OpenTypeEnum {

       BEFORE("前置"),

       REPLACE("替换"),

       AFTER("后置");

       private final String name;

       OpenTypeEnum(String name) {
           this.name = name;
       }

       public String getName() {
           return name;
       }

   }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getFunc() {
        return func;
    }

    public void setFunc(String func) {
        this.func = func;
    }

    public List<OpenFunc> getOpenFuncList() {
        return openFuncList;
    }

    public void setOpenFuncList(List<OpenFunc> openFuncList) {
        this.openFuncList = openFuncList;
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
}
