package org.spring.util;


public class ClassHandlerUtil {

    /**
     * 格式化路径将 . 替换为 /
     *
     * @return url
     */
    public static String formatUrlPointToVirgule(String url) {
        if (url == null) return "";

        url = url.replace(".", "/");

        return url;
    }


    /**
     * 格式化路径将 / 替换为 .
     *
     * @return url
     */
    public static String formatUrlVirguleToPoint(String url) {
        if (url == null) return "";

        url = url.replace("/", ".");

        url = url.replace("\\", ".");

        return url;
    }


    /**
     * 判断是不是一个class文件
     * @param url 请求路径
     * @return true：是 false：否
     */
    public static boolean isClassFile(String url) {
        if (url == null) return false;

        return url.endsWith(".class");
    }

}
