package com.bidanet.hprose.starter.core;

import com.bidanet.hprose.starter.annotation.HproseClient;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.util.Set;

/**
 * Created by xuejike on 2017/7/13.
 */
public class HproseClientScan extends ClassPathBeanDefinitionScanner {



    protected hprose.client.HproseClient hproseClient;


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
//        DefaultListableBeanFactory applicationContext = (DefaultListableBeanFactory) this.applicationContext;

        Set<BeanDefinitionHolder> beanDefinitions = super.doScan(basePackages);
        for (BeanDefinitionHolder holder : beanDefinitions) {
            GenericBeanDefinition definition = (GenericBeanDefinition) holder.getBeanDefinition();
            //保持原始定义
//            definition.setOriginatingBeanDefinition(definition.cloneBeanDefinition());
//
//            definition.getConstructorArgumentValues().addIndexedArgumentValue(0,definition.getBeanClassName());

//            String beanName = holder.getBeanName();
//
//            definition.setBeanClass(HproseClientFactoryBean.class);


            definition.setFactoryBeanName("hproseClientFactory");
            definition.setFactoryMethodName("createClient");
            definition.getConstructorArgumentValues().addGenericArgumentValue(definition.getBeanClassName());
        }

//        beanDefinitions.clear();
        return beanDefinitions;
    }

    public boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
//            boolean candidateComponent = super.isCandidateComponent(beanDefinition);
        boolean b = beanDefinition.getMetadata()
                .hasAnnotation(HproseClient.class.getName())&&beanDefinition.getMetadata().isInterface();
//        if (b) {
////            System.out.println(b);
//        }
        return b;
    }

}