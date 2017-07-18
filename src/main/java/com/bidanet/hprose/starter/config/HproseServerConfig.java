package com.bidanet.hprose.starter.config;

import com.bidanet.hprose.starter.core.HproseClientFactory;
import com.bidanet.hprose.starter.core.HproseClientScan;
import com.bidanet.hprose.starter.core.ScanConfig;
import com.bidanet.hprose.starter.exception.HproseConfigException;
import hprose.client.HproseClient;
import hprose.client.HproseHttpClient;
import hprose.client.HproseTcpClient;
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
import java.util.List;

/**
 * Created by xuejike on 2017/6/28.
 */
@Configuration
@ConditionalOnClass(HproseService.class)
@EnableConfigurationProperties(HproseServerConfigProperties.class)
//@EnableAutoConfiguration
//@ConditionalOnBean(HproseServerConfigProperties.class)
//@Order(Ordered.LOWEST_PRECEDENCE)
public class HproseServerConfig {

    private static final Logger logger = LoggerFactory.getLogger(HproseServerConfig.class);

    protected HproseService hproseService;
    protected HproseClient hproseClient;

    HproseServerConfigProperties properties;

//    @Autowired
//    DefaultListableBeanFactory beanFactory;


    public HproseServerConfig(HproseServerConfigProperties properties) {
        this.properties = properties;
    }

    @PostConstruct
    public String init(){

        System.out.println("ss");
        boolean enabled = properties.isEnabled();
        if (properties.isEnabled()){

        }
        return "ss";
    }
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "bd.rpc.hprose",name = "enabled",havingValue = "true")
    public HproseService initService() throws HproseConfigException, IOException, URISyntaxException {
        String url = properties.getUrl();
        HproseService hproseService = getHproseService(properties,url);
        this.hproseService=hproseService;

        return hproseService;
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "bd.rpc.hprose",name = "clientEnable",havingValue = "true")
    public HproseClient hproseClient() throws HproseConfigException {
//        HproseClient hproseClient;
        String clientServerUrl = properties.getClientServerUrl();
        if (clientServerUrl.contains("tcp")){
            HproseTcpClient hproseTcpClient = new HproseTcpClient(clientServerUrl);
            hproseTcpClient.setFullDuplex(true);
            hproseTcpClient.setMaxPoolSize(properties.getClientMaxPool());
            hproseClient=hproseTcpClient;
//            loadClient();
            return hproseTcpClient;
        }else if (clientServerUrl.contains("http")){
            HproseHttpClient hproseHttpClient = new HproseHttpClient(clientServerUrl);
            hproseClient=hproseHttpClient;
//            loadClient();
            return hproseHttpClient;
        }
        throw new HproseConfigException("无效客户端配置");
    }
//    @Bean
//    @ConditionalOnBean(HproseClient.class)
//    public void loadClient(){
//        HproseClientScan hproseClientScan = new HproseClientScan(beanFactory, hproseClient);
//
//        hproseClientScan.doScan(properties.clientPackage);
//
//    }
//    @Bean

//    public HproseService createHproseService(@Autowired HproseServerConfigProperties properties) throws HproseConfigException, URISyntaxException, IOException {
//        String url = properties.getUrl();
//        HproseService hproseService = getHproseService(properties,url);
//        this.hproseService=hproseService;
//
//        return hproseService;
//
//    }
    protected HproseService getHproseService(HproseServerConfigProperties properties,String url) throws HproseConfigException, URISyntaxException, IOException {
        if (url==null||"".equals(url)){
            throw new HproseConfigException("URL 为配置");
        }
        if (url.contains("tcp")){
            HproseTcpServer tcpServer = new HproseTcpServer(url);
            tcpServer.setReactorThreads(properties.getReactorThreads());
            tcpServer.start();
            return tcpServer;
        }
        if (url.contains("http")){
            return new HproseHttpService();
        }
        if (url.contains("ws")){
            return new HproseWebSocketService();
        }
        return null;
    }
//
//
//    @PreDestroy
//    public void close(){
//        if (hproseService!=null && hproseService instanceof HproseTcpServer){
//            ((HproseTcpServer) hproseService).stop();
//        }
//        if (hproseClient!=null){
//            hproseClient.close();
//        }
//    }
//
//
//    @Bean
//    @ConditionalOnBean(HproseClient.class)
//    public HproseClientFactory hproseClientFactory(@Autowired HproseClient hproseClient){
//        HproseClientFactory hproseClientFactory = new HproseClientFactory(hproseClient);
//        return hproseClientFactory;
//    }
//
//    @Bean
//    @ConditionalOnBean(HproseClient.class)
//    public ScanConfig createScan(){
//        ScanConfig scanConfig = new ScanConfig(hproseClient,new String[]{properties.getClientPackage()});
//        scanConfig.setApp(applicationContext);
//        return scanConfig;
//    }

    public static class AutoConfiguredClientScannerRegistrar implements BeanFactoryAware, ImportBeanDefinitionRegistrar, ResourceLoaderAware{

        private BeanFactory beanFactory;

        private ResourceLoader resourceLoader;

        @Override
        public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
            this.beanFactory=beanFactory;
        }

        @Override
        public void setResourceLoader(ResourceLoader resourceLoader) {
            this.resourceLoader=resourceLoader;
        }

        @Override
        public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry beanDefinitionRegistry) {
            HproseClientScan scanner = new HproseClientScan(beanDefinitionRegistry);


            if (this.resourceLoader != null) {
                scanner.setResourceLoader(this.resourceLoader);
            }

            List<String> packages = AutoConfigurationPackages.get(this.beanFactory);
            if (logger.isDebugEnabled()) {
                for (String pkg : packages) {
                    logger.debug("Using auto-configuration base package '{}'", pkg);
                }
            }

//                scanner.setAnnotationClass(Mapper.class);
//                scanner.registerFilters();
            scanner.doScan(StringUtils.toStringArray(packages));
        }

    }


    @org.springframework.context.annotation.Configuration
    @Import({ AutoConfiguredClientScannerRegistrar.class })
//    @AutoConfigureAfter(HproseServerConfig.class)
//    @ConditionalOnMissingBean(MapperFactoryBean.class)
    public static class MapperScannerRegistrarNotFoundConfiguration {

        @PostConstruct
        public void afterPropertiesSet() {
//            logger.debug("No {} found.", MapperFactoryBean.class.getName());
        }
    }
}


