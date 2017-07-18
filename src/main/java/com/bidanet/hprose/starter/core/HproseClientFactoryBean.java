package com.bidanet.hprose.starter.core;

import com.bidanet.hprose.starter.exception.HproseConfigException;
import hprose.client.HproseClient;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by xuejike on 2017/7/13.
 */
public class HproseClientFactoryBean<T> implements FactoryBean<T> {
//    @Autowired
    protected HproseClient hproseClient;
    protected String className;

//    public HproseClientFactoryBean(String className,HproseClient hproseClient) {
//        this.hproseClient = hproseClient;
//        this.className = className;
//    }

    public HproseClientFactoryBean(String className) {
        this.className = className;
    }

    public HproseClient getHproseClient() {
        return hproseClient;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public void setHproseClient(HproseClient hproseClient) {
        this.hproseClient = hproseClient;
    }

    @Override
    public T getObject() throws Exception {
        Class<?> aClass = Class.forName(className);
        if (aClass!=null){
            return (T) createObj(aClass);
        }
        return null;
    }

    @Override
    public Class<?> getObjectType() {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }



    Object createObj(Class innerClass) throws HproseConfigException {
//        Class innerClass = Class.forName(innerClassName);
        if (innerClass.isInterface()) {
            com.bidanet.hprose.starter.annotation.HproseClient annotation = (com.bidanet.hprose.starter.annotation.HproseClient) innerClass.getAnnotation(com.bidanet.hprose.starter.annotation.HproseClient.class);
            if (annotation != null) {
                String s = annotation.serverName();
                if (s == null || "".equals(s)) {
                    throw new HproseConfigException(innerClass + "->初始化失败 ->未设置 serverName");
                } else {
                    Object o = hproseClient.useService(innerClass, s);
                    return o;
                }

            } else {
                throw new HproseConfigException(innerClass + "->初始化失败,读取不到 com.bidanet.hprose.starter.annotation.HproseClient ");
            }

        }
        throw new HproseConfigException(innerClass + "->初始化失败，不是接口");
    }
}
