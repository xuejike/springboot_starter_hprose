package com.bidanet.hprose.starter.core;

import hprose.client.HproseClient;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;

/**
 * Created by xuejike on 2017/7/13.
 */
//@Component
public class ScanConfig implements BeanDefinitionRegistryPostProcessor {

    protected HproseClient hproseClient;
    protected String[] scanPackages;
    private DefaultListableBeanFactory app;

    public ScanConfig(HproseClient hproseClient, String[] scanPackages) {
        this.hproseClient = hproseClient;
        this.scanPackages = scanPackages;
    }


    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {

        HproseClientScan hproseClientScan = new HproseClientScan(registry,hproseClient);
//        hproseClientScan.setApplicationContext(app);
        hproseClientScan.scan(scanPackages);

    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }

    public void setApp(DefaultListableBeanFactory app) {
        this.app = app;
    }

    public DefaultListableBeanFactory getApp() {
        return app;
    }
}