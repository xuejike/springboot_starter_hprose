package com.bidanet.hprose.starter.config;

import com.bidanet.hprose.starter.annotation.HproseEntity;
import com.bidanet.hprose.starter.controller.HproseController;
import com.bidanet.hprose.starter.core.*;
import com.bidanet.hprose.starter.exception.HproseConfigException;
import com.bidanet.hprose.starter.tool.LoadPackageClasses;
import com.bidanet.hprose.starter.tool.LogFilter;
import com.bidanet.hprose.starter.tool.TokenFilter;
import com.google.common.base.Strings;
import hprose.client.HproseClient;
import hprose.client.HproseHttpClient;
import hprose.client.HproseTcpClient;
import hprose.io.HproseClassManager;
import hprose.server.HproseHttpService;
import hprose.server.HproseService;
import hprose.server.HproseTcpServer;
import hprose.server.HproseWebSocketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by xuejike on 2017/6/28.
 */
@Configuration
@ConditionalOnClass(HproseService.class)
@EnableConfigurationProperties(HproseServerConfigProperties.class)
@Import({HproseApplicationListener.class})
public class HproseServerConfig {

    private static final Logger logger = LoggerFactory.getLogger(HproseServerConfig.class);

    protected HproseService hproseService;
    protected HproseClient hproseClient;

    HproseServerConfigProperties properties;


    public HproseServerConfig(HproseServerConfigProperties properties) {
        this.properties = properties;
    }

    @PostConstruct
    public void init() {

        if (properties != null) {

        }
//        return "ss";
    }

    @PreDestroy
    public void close() {
        if (hproseService != null && hproseService instanceof HproseTcpServer) {
            ((HproseTcpServer) hproseService).stop();
        }
        if (hproseClient != null) {
            hproseClient.close();
        }
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "bd.rpc.hprose", name = "enabled", havingValue = "true")

    public HproseService initService() throws HproseConfigException, IOException, URISyntaxException {
        String url = properties.getUrl();
        HproseService hproseService = getHproseService(properties, url);
        this.hproseService = hproseService;

        return hproseService;
    }


    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "bd.rpc.hprose", name = "clientEnable", havingValue = "true")
    public HproseClient hproseClient() throws HproseConfigException {
//        HproseClient hproseClient;
        String clientServerUrl = properties.getClientServerUrl();
        if (clientServerUrl.contains("tcp")) {
            HproseTcpClient hproseTcpClient = new HproseTcpClient(clientServerUrl);
            hproseTcpClient.setFullDuplex(true);
            hproseTcpClient.setMaxPoolSize(properties.getClientMaxPool());
            hproseClient = hproseTcpClient;

//            loadClient();
            return hproseTcpClient;
        } else if (clientServerUrl.contains("http")) {
            HproseHttpClient hproseHttpClient = new HproseHttpClient(clientServerUrl);
            hproseClient = hproseHttpClient;
//            loadClient();
            return hproseHttpClient;
        }
        throw new HproseConfigException("无效客户端配置");
    }

    protected HproseService getHproseService(HproseServerConfigProperties properties, String url) throws HproseConfigException, URISyntaxException, IOException {
        if (url == null || "".equals(url)) {
            throw new HproseConfigException("URL 为配置");
        }
        if (url.contains("tcp")) {
            HproseTcpServer tcpServer = new HproseTcpServer(url);
            tcpServer.setReactorThreads(properties.getReactorThreads());
            tcpServer.start();
            return tcpServer;
        }
        if (url.contains("http")) {
            return new HproseHttpService();
        }
        if (url.contains("ws")) {
            return new HproseWebSocketService();
        }
        return null;
    }



    public static class AutoConfiguredClientScannerRegistrar implements BeanFactoryAware, ImportBeanDefinitionRegistrar, ResourceLoaderAware {

        private BeanFactory beanFactory;

        private ResourceLoader resourceLoader;

        @Override
        public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
            this.beanFactory = beanFactory;
        }

        @Override
        public void setResourceLoader(ResourceLoader resourceLoader) {
            this.resourceLoader = resourceLoader;
        }

        @Override
        public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry beanDefinitionRegistry) {
            HproseClientScan scanner = new HproseClientScan(beanDefinitionRegistry);

            if (this.resourceLoader != null) {
                scanner.setResourceLoader(this.resourceLoader);
            }
            List<String> packages = new ArrayList<>();
            try {
                packages = AutoConfigurationPackages.get(this.beanFactory);
                if (logger.isDebugEnabled()) {
                    for (String pkg : packages) {
                        logger.debug("客户端扫描包:{}", pkg);
                    }
                }

            } catch (Exception ex) {
                logger.debug("无法获取到AutoConfig基础包", ex);
                packages.add("com");
            }
//            logger.info("客户端扫描包:{}",packag);
            scanner.doScan(StringUtils.toStringArray(packages));


        }

    }


    @org.springframework.context.annotation.Configuration
    @ConditionalOnProperty(prefix = "bd.rpc.hprose", value = "clientEnable", havingValue = "true")
    @Import({AutoConfiguredClientScannerRegistrar.class})
    public static class MapperScannerRegistrarNotFoundConfiguration {

    }




    @Configuration
    @Import({SpringBootHprose.class})
    public static class EntityScan implements BeanFactoryAware {
        @Autowired
        SpringBootHprose springBootHprose;
        BeanFactory beanFactory;

        @PostConstruct
        public void afterPropertiesSet() {
            List<String> packages = new ArrayList<>();
            try {
                packages = AutoConfigurationPackages.get(this.beanFactory);
                if (logger.isDebugEnabled()) {
                    for (String pkg : packages) {
                        logger.debug("Using auto-configuration base package '{}'", pkg);
                    }
                }

            } catch (Exception ex) {
                logger.debug("无法获取到AutoConfig基础包", ex);
                packages.add("com");
            }
            LoadPackageClasses loadPackageClasses = new LoadPackageClasses(StringUtils.toStringArray(packages), HproseEntity.class);
            try {
                Set<Class<?>> classSet = loadPackageClasses.getClassSet();
                for (Class entity : classSet) {
                    registerEntity(entity);

                }
                logger.info("Hprose 实体注册完成");

            } catch (Exception e) {
                logger.error("实体注册失败:",e);
            }
        }


        /**
         * 注册实体
         *
         * @param cls
         */
        public void registerEntity(Class<?> cls) {
            HproseEntity entityAnno = cls.getAnnotation(HproseEntity.class);
            String entityName = entityAnno.value();
            if (Strings.isNullOrEmpty(entityName)) {
                entityName = cls.getSimpleName();
                int entityIndex = entityName.indexOf("Entity");
                if (entityIndex > 0) {
                    entityName = entityName.substring(0, entityIndex);
                }
            }

            HproseClassManager.register(cls, entityName);
            springBootHprose.addEntity(entityName, cls);

            logger.debug("Hprose 注册实体 {} -> {}", entityName, cls.getName());

        }

        @Override
        public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
            this.beanFactory = beanFactory;
        }
    }


}


