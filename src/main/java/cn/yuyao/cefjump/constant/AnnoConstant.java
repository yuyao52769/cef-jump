package cn.yuyao.cefjump.constant;

import cn.yuyao.cefjump.CefDocModuleDesc;

import java.util.Arrays;
import java.util.List;

/**
 * @author yuyao
 * @create 2025/2/12
 */
public class AnnoConstant {

//    public static final String EXTENSION_ANNOTATION = "cn.zcy.ka.cef.method.Extension";
//
//    public static final String OPEN_AFTER_ANNOTATION = "cn.zcy.ka.cef.method.OpenAfter";
//
//    public static final String OPEN_BEFORE_ANNOTATION = "cn.zcy.ka.cef.method.OpenBefore";
//
//    public static final String OPEN_REPLACE_ANNOTATION = "cn.zcy.ka.cef.method.OpenReplace";
//
//    public static final String EXTENSION_SERVICES_METHOD = "ExtensionServices.execute";
//
//    public static final String TARGET_DOC_DESC_ANNO = "cn.zcy.ka.cef.method.CefDocDesc";


    public static final String EXTENSION_ANNOTATION = "cn.yuyao.anno.Extension";

    public static final String OPEN_AFTER_ANNOTATION = "cn.yuyao.anno.OpenAfter";

    public static final String OPEN_BEFORE_ANNOTATION = "cn.yuyao.anno.OpenBefore";

    public static final String OPEN_REPLACE_ANNOTATION = "cn.yuyao.anno.OpenReplace";

    public static final String EXTENSION_SERVICES_METHOD = "ExtensionServices.execute";

    public static final String TARGET_DOC_DESC_ANNO = "cn.yuyao.anno.CefDocDesc";



    public static final List<OpenTypeHandler> OPEN_ANNO_LIST = Arrays.asList(
            new OpenTypeHandler(CefDocModuleDesc.OpenTypeEnum.BEFORE, OPEN_BEFORE_ANNOTATION),
            new OpenTypeHandler(CefDocModuleDesc.OpenTypeEnum.REPLACE, OPEN_REPLACE_ANNOTATION),
            new OpenTypeHandler(CefDocModuleDesc.OpenTypeEnum.AFTER, OPEN_AFTER_ANNOTATION)
    );

    public static class OpenTypeHandler {
        private CefDocModuleDesc.OpenTypeEnum typeEnum;
        private String annoName;

        public OpenTypeHandler(CefDocModuleDesc.OpenTypeEnum typeEnum, String annoName) {
            this.typeEnum = typeEnum;
            this.annoName = annoName;
        }

        public CefDocModuleDesc.OpenTypeEnum getTypeEnum() {
            return typeEnum;
        }

        public void setTypeEnum(CefDocModuleDesc.OpenTypeEnum typeEnum) {
            this.typeEnum = typeEnum;
        }

        public String getAnnoName() {
            return annoName;
        }

        public void setAnnoName(String annoName) {
            this.annoName = annoName;
        }
    }
}
