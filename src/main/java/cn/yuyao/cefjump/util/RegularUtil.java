package cn.yuyao.cefjump.util;

public class RegularUtil {
    public static final String BASE = "^\\s*\\*\\s*@param\\s+";

    public static final String RETURN_RE = "^\\s*\\*\\s*@return\\s*";

    public static String getReturnDesc(String content) {
        return content.replaceFirst(RETURN_RE, "");
    }

    public static String getParamDesc(String content, String key) {
        String pattern = "^\\s*\\*\\s*@param\\s+helloUser\\s*";
        String pattern1 = BASE + key + "\\s*";
        return content.replaceFirst(pattern1, "").trim();
    }
}
