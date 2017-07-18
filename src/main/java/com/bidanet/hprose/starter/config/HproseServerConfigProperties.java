package com.bidanet.hprose.starter.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by xuejike on 2017/6/28.
 */
@ConfigurationProperties(prefix = "bd.rpc.hprose")
public class HproseServerConfigProperties {

    /**
     * 服务器地址
     */
    private String url="tcp://0.0.0.0:4321";
    /**
     * 线程数
     */
    private int reactorThreads=10;
    /**
     * 是否开启服务
     */
    private boolean enabled;

    /**
     * 是否开启客户端扫描
     */
    private boolean clientEnable;
    /**
     * 服务器地址
     */
    private String clientServerUrl="tcp://127.0.0.1:4321/";
    /**
     * 客户端最大线程数
     */
    private int clientMaxPool=10;
    /**
     * 客户端扫描包路径
     */
    protected String clientPackage;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getReactorThreads() {
        return reactorThreads;
    }

    public void setReactorThreads(int reactorThreads) {

        this.reactorThreads = reactorThreads;
    }

    public boolean isClientEnable() {
        return clientEnable;
    }

    public void setClientEnable(boolean clientEnable) {
        this.clientEnable = clientEnable;
    }

    public String getClientServerUrl() {
        return clientServerUrl;
    }

    public void setClientServerUrl(String clientServerUrl) {
        this.clientServerUrl = clientServerUrl;
    }

    public int getClientMaxPool() {
        return clientMaxPool;
    }

    public void setClientMaxPool(int clientMaxPool) {

        this.clientMaxPool = clientMaxPool;
    }

    public String getClientPackage() {
        return clientPackage;
    }

    public void setClientPackage(String clientPackage) {
        this.clientPackage = clientPackage;
    }
}
