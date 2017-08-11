package com.bidanet.hprose.starter.core;

import java.util.Map;

/**
 * Created by xuejike on 2017/8/11.
 */
public class HproseServerDefinition {
    /**
     * 服务名称
     */
    private String serverName;
    /**
     * 服务类
     */
    private Class serverClass;
    /**
     * 服务描述
     */
    private String describe;
    /**
     * 服务方法以及描述
     */
    private Map<String,String> methods;

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public Class getServerClass() {
        return serverClass;
    }

    public void setServerClass(Class serverClass) {
        this.serverClass = serverClass;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public Map<String, String> getMethods() {
        return methods;
    }

    public void setMethods(Map<String, String> methods) {
        this.methods = methods;
    }
}
