package com.bidanet.hprose.starter.config;

import com.bidanet.hprose.starter.core.HproseClientFactory;
import com.bidanet.hprose.starter.core.ScanConfig;
import com.bidanet.hprose.starter.exception.HproseConfigException;
import hprose.client.HproseClient;
import hprose.client.HproseHttpClient;
import hprose.client.HproseTcpClient;
import hprose.server.HproseHttpService;
import hprose.server.HproseService;
import hprose.server.HproseTcpServer;
import hprose.server.HproseWebSocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Created by xuejike on 2017/6/28.
 */
@Configuration
@ConditionalOnClass(HproseService.class)
@EnableConfigurationProperties(HproseServerConfigProperties.class)
public class HproseServerConfig {



    protected HproseService hproseService;
    protected HproseClient hproseClient;
    @Bean
    @ConditionalOnProperty(prefix = "bd.rpc.hprose",value = "enabled",havingValue = "true")
    public HproseService createHproseService(@Autowired HproseServerConfigProperties properties) throws HproseConfigException, URISyntaxException, IOException {
        String url = properties.getUrl();
        HproseService hproseService = getHproseService(properties,url);
        this.hproseService=hproseService;

        return hproseService;

    }
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


    @PreDestroy
    public void close(){
        if (hproseService!=null && hproseService instanceof HproseTcpServer){
            ((HproseTcpServer) hproseService).stop();
        }
        if (hproseClient!=null){
            hproseClient.close();
        }
    }
    @Bean
    @ConditionalOnProperty(prefix = "bd.rpc.hprose",value = "client-enable",havingValue = "true")
    public HproseClient createClient(@Autowired HproseServerConfigProperties properties) throws HproseConfigException {
//        HproseClient hproseClient;
        String clientServerUrl = properties.getClientServerUrl();
        if (clientServerUrl.contains("tcp")){
            HproseTcpClient hproseTcpClient = new HproseTcpClient(clientServerUrl);
            hproseTcpClient.setFullDuplex(true);
            hproseTcpClient.setMaxPoolSize(properties.getClientMaxPool());
            hproseClient=hproseTcpClient;
            return hproseTcpClient;
        }else if (clientServerUrl.contains("http")){
            HproseHttpClient hproseHttpClient = new HproseHttpClient(clientServerUrl);
            hproseClient=hproseHttpClient;
            return hproseHttpClient;
        }
        throw new HproseConfigException("无效客户端配置");
    }

    @Bean
    @ConditionalOnBean(HproseClient.class)
    public HproseClientFactory hproseClientFactory(@Autowired HproseClient hproseClient){
        HproseClientFactory hproseClientFactory = new HproseClientFactory(hproseClient);
        return hproseClientFactory;
    }
    @Bean
    @ConditionalOnBean(HproseClientFactory.class)
    public ScanConfig createScan(@Autowired HproseClient hproseClient,@Autowired DefaultListableBeanFactory applicationContext,HproseServerConfigProperties properties){
        ScanConfig scanConfig = new ScanConfig(hproseClient,new String[]{properties.clientPackage});
        scanConfig.setApp(applicationContext);
        return scanConfig;
    }
}


