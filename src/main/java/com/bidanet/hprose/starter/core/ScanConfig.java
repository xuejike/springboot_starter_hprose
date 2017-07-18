package com.bidanet.hprose.starter.core;

import hprose.client.HproseClient;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

/**
 * Created by xuejike on 2017/7/13.
 */
//@Configuration
//@ConditionalOnBean
public class ScanConfig implements BeanDefinitionRegistryPostProcessor {


    public ScanConfig() {
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {

//        HproseClientScan hproseClientScan = new HproseClientScan(registry,hproseClient);
//        hproseClientScan.setApplicationContext(app);
//        hproseClientScan.scan(scanPackages);

    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }


}