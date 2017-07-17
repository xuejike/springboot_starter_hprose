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
@Component
@ConditionalOnProperty(prefix = "bd.rpc.hprose",value = "enabled",havingValue = "true")
public class HproseApplicationListener implements ApplicationListener<ContextRefreshedEvent> {

    protected static Logger logger= LoggerFactory.getLogger(HproseApplicationListener.class);

    @Autowired
    HproseServerConfigProperties properties;
    @Autowired
    hprose.server.HproseService hproseService;

    @Autowired
    HproseClient hproseClient;

    @Autowired
    SpringBootHprose springBootHprose;





    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {


        LoadPackageClasses loadPackageClasses = new LoadPackageClasses(new String[]{"com.fenxiangbao"}, HproseEntity.class);


        Map<String, Object> serviceBeans = contextRefreshedEvent.getApplicationContext().getBeansWithAnnotation(HproseService.class);

        for (Map.Entry<String, Object> entry : serviceBeans.entrySet()) {
            addService(entry.getValue());
        }
        logger.info("add service finish");

        try {
            Set<Class<?>> classSet = loadPackageClasses.getClassSet();
            for (Class entity : classSet) {
                registerEntity(entity);
            }
            logger.info("register entity finish");

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
//        Map<String, Object> entityBeans = contextRefreshedEvent.getApplicationContext().getBeansWithAnnotation(HproseEntity.class);





    }

    /**
     * 注册服务
     * @param service
     */
    public void addService(Object service){
        Class aClass = service.getClass();
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
                if (logger.isDebugEnabled()){
                    logger.debug("add service {} -> {}",serviceName,service.getClass().getName());
                }
            }

        }
//        Annotation[] annotations = aClass.getAnnotations();

    }

    /**
     * 注册实体
     * @param cls
     */
    public void registerEntity( Class<?> cls){
//        Class<?> cls = entity.getClass();
        HproseEntity entityAnno = cls.getAnnotation(HproseEntity.class);
        String entityName = entityAnno.value();
        if (Strings.isNullOrEmpty(entityName)){
            entityName=cls.getSimpleName();
            int entityIndex = entityName.indexOf("Entity");
            if (entityIndex>0){
                entityName=entityName.substring(0,entityIndex);
            }
        }

        HproseClassManager.register(cls,entityName);
        springBootHprose.addEntity(entityName,cls);
        if (logger.isDebugEnabled()){
            logger.debug("register class {} -> {}",entityName,cls.getName());
        }

    }

    public Class getHaveAnnotationClass(Class val,Class<? extends Annotation> annotation){
        Annotation valAnnotation = val.getDeclaredAnnotation(annotation);
        if (valAnnotation==null){

            Class[] interfaces = val.getInterfaces();
            for (Class aClass : interfaces) {
                Class anno = getHaveAnnotationClass(aClass, annotation);
                if (anno !=null){
                    return anno;
                }
            }
            return null;
        }else{
            return val;
        }
    }


}
