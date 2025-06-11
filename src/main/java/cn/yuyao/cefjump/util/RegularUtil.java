package cn.yuyao.cefjump.util;

public class RegularUtil {
    public static final String BASE = "^\\s*\\*\\s*@param\\s+";

    public static final String RETURN_RE = "^\\s*\\*\\s*@return\\s*";

    public static String getReturnDesc(String content) {
        return content.replaceFirst( "^\\s*@return\\s*(.*)$", "$1");
    }

    public static String getParamDesc(String content, String key) {
        String pattern = "^\\s*\\*\\s*@param\\s+helloUser\\s*";
        String pattern1 = BASE + key + "\\s*";
        return content.replaceFirst(pattern1, "").trim();
    }

    public static void main(String[] args) {
        System.out.println(getReturnDesc("@return           这个返回的结果"));
        System.out.println(getReturnDesc("   @return           这个返回的结果"));
    }
}
