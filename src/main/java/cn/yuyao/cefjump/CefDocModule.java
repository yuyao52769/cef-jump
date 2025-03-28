package cn.yuyao.cefjump;

import java.util.List;

public class CefDocModule {

    private String className;

    private String methodName;

    private String module;

    private String func;






   public static class OpenFunc {

       private OpenTypeEnum type;

       private String openId;
   }

   public static enum OpenTypeEnum {

       BEFORE,

       REPLACE,

       AFTER;

   }

}
