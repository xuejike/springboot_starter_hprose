package com.bidanet.hprose.starter.core;

import hprose.client.HproseClient;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;

/**
 * Created by xuejike on 2017/7/13.
 */
//@Component
public class ScanConfig implements BeanDefinitionRegistryPostProcessor {

    protected HproseClient hproseClient;
    protected String[] scanPackages;

    public ScanConfig(HproseClient hproseClient, String[] scanPackages) {
        this.hproseClient = hproseClient;
        this.scanPackages = scanPackages;
    }


    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {

        HproseClientScan hproseClientScan = new HproseClientScan(registry,hproseClient);

        hproseClientScan.scan(scanPackages);

    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }
}