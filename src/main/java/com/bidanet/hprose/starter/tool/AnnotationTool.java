package com.bidanet.hprose.starter.tool;

import com.bidanet.hprose.starter.annotation.HproseDoc;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * Created by xuejike on 2017/6/29.
 */
public class AnnotationTool {
    public static String getDocValue(Method method){
        HproseDoc hproseDoc = method.getAnnotation(HproseDoc.class);
        if (hproseDoc==null){
            return method.getName();
        }
        return hproseDoc.value();
    }
    public static String getDocValue(Parameter parameter){
        HproseDoc hproseDoc = parameter.getAnnotation(HproseDoc.class);
        if (hproseDoc==null){
            return parameter.getName();
        }else{
            return hproseDoc.value();
        }
    }
    public static String getDocValue(Field field){
        HproseDoc hproseDoc = field.getAnnotation(HproseDoc.class);
        if (hproseDoc==null){
            return field.getName();
        }else{
            return hproseDoc.value();
        }
    }

}
