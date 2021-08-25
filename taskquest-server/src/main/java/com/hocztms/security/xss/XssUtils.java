package com.hocztms.security.xss;


public class XssUtils {


    public  static String cleanXss(String valueBefore){
        String valueAfter = valueBefore.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
        valueAfter = valueAfter.replaceAll("<", "& lt;").replaceAll(">", "& gt;");
        valueAfter = valueAfter.replaceAll("\\(", "& #40;").replaceAll("\\)", "& #41;");
        valueAfter = valueAfter.replaceAll("'", "& #39;");
        valueAfter = valueAfter.replaceAll("eval\\((.*)\\)", "");
        valueAfter = valueAfter.replaceAll("[\"'][\\s]*javascript:(.*)[\"']", "\"\"");
        valueAfter = valueAfter.replaceAll("script", "");
        return valueAfter;
    }

}
