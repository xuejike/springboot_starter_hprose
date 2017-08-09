package com.bidanet.hprose.starter.core;

import com.bidanet.hprose.starter.annotation.HproseClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.RuntimeBeanNameReference;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.util.Set;

/**
 * Created by xuejike on 2017/7/13.
 */
public class HproseClientScan extends ClassPathBeanDefinitionScanner {
    public static Logger logger= LoggerFactory.getLogger(HproseClientScan.class);


    protected hprose.client.HproseClient hproseClient;

    public HproseClientScan(BeanDefinitionRegistry registry) {
        super(registry);
    }

    public HproseClientScan(BeanDefinitionRegistry registry, hprose.client.HproseClient hproseClient) {
        super(registry);
        this.hproseClient = hproseClient;
    }

    @Override
    protected void registerDefaultFilters() {
        super.registerDefaultFilters();

        this.addIncludeFilter(new AnnotationTypeFilter(HproseClient.class));
    }

    public Set<BeanDefinitionHolder> doScan(String... basePackages) {

        Set<BeanDefinitionHolder> beanDefinitions = super.doScan(basePackages);
        for (BeanDefinitionHolder holder : beanDefinitions) {
            GenericBeanDefinition definition = (GenericBeanDefinition) holder.getBeanDefinition();
            //保持原始定义
            logger.debug("Hprose 加载客户端:"+definition.getBeanClassName());
            definition.setOriginatingBeanDefinition(definition.cloneBeanDefinition());
            definition.getConstructorArgumentValues().addIndexedArgumentValue(0,definition.getBeanClassName());
            definition.setBeanClass(HproseClientFactoryBean.class);
            definition.getPropertyValues().addPropertyValue("hproseClient",new RuntimeBeanReference("hproseClient"));
            definition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);


        }
        logger.info("Hprose 成功加载客户端->"+beanDefinitions.size());
        return beanDefinitions;
    }

    public boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        boolean b = beanDefinition.getMetadata()
                .hasAnnotation(HproseClient.class.getName())&&beanDefinition.getMetadata().isInterface();

        return b;
    }

}