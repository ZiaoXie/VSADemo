package com.zzy.vsa.demo.appcase.fileoperation;
import java.util.Random;
public class agnomen {

    public static final String random_s = ("0123456789abcdefghijklmnopqrstovwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ");
    public static final int bound = 61;
    public static final int length = 10;
    public static String fileAgnomen(String str){
        int index = str.lastIndexOf(".");
        Random random_1 = new Random();
        StringBuffer sb = new StringBuffer();

        for( int i=0; i<length; i++) {
            int index_1 = random_1.nextInt(bound);
            sb.append(random_s.charAt(index_1));
        }

        String str3 = sb.toString();

        if(index == -1){
            String endStr = str+"_"+ str3;
            return endStr;
        }

        else {
            String str2 = str.substring(index);
            String str1 = str.substring(0, index);
            String endStr = str1 + "_" + str3 + str2;
            return endStr;
        }
    }
}
