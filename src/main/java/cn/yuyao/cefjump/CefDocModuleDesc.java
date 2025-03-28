package cn.yuyao.cefjump;

import java.util.List;

public class CefDocModuleDesc {

    private String className;

    private String methodName;

    private String module;

    private String func;

    private List<OpenFunc> openFuncList;



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
   }

   public static enum OpenTypeEnum {

       BEFORE,

       REPLACE,

       AFTER;

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
}
