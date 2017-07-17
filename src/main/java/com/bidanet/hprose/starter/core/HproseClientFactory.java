package com.bidanet.hprose.starter.core;

import com.bidanet.hprose.starter.exception.HproseConfigException;
import hprose.client.HproseClient;
import org.springframework.beans.factory.FactoryBean;

/**
 * Created by xuejike on 2017/7/13.
 */
public class HproseClientFactory {
    protected HproseClient hproseClient;
//    protected String className;

    public HproseClientFactory(HproseClient hproseClient) {
        this.hproseClient = hproseClient;
//        this.className = className;
    }


    <T> T createClient(Class<T> cls) throws HproseConfigException {
//        Class innerClass = Class.forName(innerClassName);

            com.bidanet.hprose.starter.annotation.HproseClient annotation = (com.bidanet.hprose.starter.annotation.HproseClient)
                    cls.getAnnotation(com.bidanet.hprose.starter.annotation.HproseClient.class);
            if (annotation != null) {
                String s = annotation.serverName();
                if (s == null || "".equals(s)) {
                    throw new HproseConfigException(cls + "->初始化失败 ->未设置 serverName");
                } else {
                    T o = hproseClient.useService(cls, s);
                    return o;
                }

            } else {
                throw new HproseConfigException(cls + "->初始化失败,读取不到 com.bidanet.hprose.starter.annotation.HproseClient ");
            }

    }
}
