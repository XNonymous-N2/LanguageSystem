package de.xnonymous.languagesystem.utils;

public class ReplaceUtils {

    public static String replaceColon(String string) {
        return string.replace(":", "&#058;");
    }

    public static String replaceColonBack(String string) {
        return string.replace("&#058;", ":");
    }

}
