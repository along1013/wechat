package com.jfinal.weixin.util;

public class StringUtil {  
	/** 
     * @方法名 translate 
     * @功能 简单的数字转中文 
     * @param a 
     *            原始数字 
     * @return 中文字符串 
     */  
    public static String translateToChinese(int a) {  
  
        String[] units = { "", "十", "百", "千", "万", "十", "百", "千", "亿" };  
        String[] nums = { "一", "二", "三", "四", "五", "六", "七", "八", "九", "十" };  
  
        String result = "";  
        if (a < 0) {  
            result = "负";  
            a = Math.abs(a);  
        }  
        String t = String.valueOf(a);  
        for (int i = t.length() - 1; i >= 0; i--) {  
            int r = (int) (a / Math.pow(10, i));  
            if (r % 10 != 0) {  
                String s = String.valueOf(r);  
                String l = s.substring(s.length() - 1, s.length());  
                result += nums[Integer.parseInt(l) - 1];  
                result += (units[i]);  
            } else {  
                if (!result.endsWith("零")) {  
                    result += "零";  
                }  
            }  
        }  
        String num = a + "";  
        /* 
         * 因为方法对10-20之间的数字支持不好，比如11返回一十一，不能满足需求 所以这里单独判断 
         */  
        if (a == 10) {  
            return "十";  
        } else if (a > 10 && a < 20) {  
            return result.substring(1);  
        } else if (num.endsWith("0")) {  
            result = result.substring(0, result.length() - 1);  
        }  
        return result;  
    }  
   
    
}