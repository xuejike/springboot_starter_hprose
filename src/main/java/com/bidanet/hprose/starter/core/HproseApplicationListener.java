package com.bidanet.hprose.starter.core;

import com.bidanet.hprose.starter.annotation.HproseEntity;
import com.bidanet.hprose.starter.annotation.HproseService;
import com.bidanet.hprose.starter.config.HproseServerConfigProperties;
import com.bidanet.hprose.starter.tool.LoadPackageClasses;
import com.google.common.base.Strings;
import hprose.client.HproseClient;
import hprose.io.HproseClassManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Set;

/**
 * Created by xuejike on 2017/6/26.
 */


public class HproseApplicationListener implements ApplicationListener<ContextRefreshedEvent> {

    protected static Logger logger= LoggerFactory.getLogger(HproseApplicationListener.class);

    @Autowired
    HproseServerConfigProperties properties;
    @Autowired
    hprose.server.HproseService hproseService;


    @Autowired
    SpringBootHprose springBootHprose;





    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {


        if (properties.isEnabled()){


            Map<String, Object> serviceBeans = contextRefreshedEvent.getApplicationContext().getBeansWithAnnotation(HproseService.class);

            for (Map.Entry<String, Object> entry : serviceBeans.entrySet()) {
                addService(entry.getValue());
            }
            logger.info("Hprose 服务注册完成");
        }

    }

    /**
     * 注册服务
     * @param service
     */
    public void addService(Object service){
        Class aClass = getHaveAnnotationClass(service.getClass(),HproseService.class);
        if (aClass!=null){

            HproseService serviceAnnotation = (HproseService) aClass.getAnnotation(HproseService.class);
            if (serviceAnnotation!=null){
                String serviceName=serviceAnnotation.value();

                if (!Strings.isNullOrEmpty(serviceName)){
                    serviceName=aClass.getSimpleName();

                    int serviceIndex = serviceName.indexOf("Service");
                    if (serviceIndex>0){
                        serviceName=serviceName.substring(0,serviceIndex);
                    }
                }
                Class serviceClass = serviceAnnotation.serviceClass();
                if (serviceClass==Object.class){
                    serviceClass=service.getClass();
                }

                hproseService.add(service,serviceClass,serviceName);
                springBootHprose.addService(serviceName,aClass);

                logger.info("注册 service {} -> {}",serviceName,service.getClass().getName());
            }

        }
//        Annotation[] annotations = aClass.getAnnotations();

    }


    public Class getHaveAnnotationClass(Class val,Class<? extends Annotation> annotation){
        Annotation valAnnotation = val.getDeclaredAnnotation(annotation);
        if (valAnnotation==null){
            Class superclass = val.getSuperclass();
            if (superclass!=Object.class){
               return getHaveAnnotationClass(superclass,annotation);
            }

            return null;
        }else{
            return val;
        }
    }


}
