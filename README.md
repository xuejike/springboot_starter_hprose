# 配置说明
## 服务开启
```properties
#开启服务
bd.rpc.hprose.enabled=true
#发布端口
bd.rpc.hprose.url="tcp://0.0.0.0:4321" 
```
```java
@com.bidanet.hprose.starter.annotation.HproseService(value = "testService")
class Service{
    public String test(String test){
        return "-->"+test;
    }
}
```
自动发布服务 为 testService_test

## 客户端开启
```properties
#开启客户端
bd.rpc.hprose.client-enable=true
bd.rpc.hprose.client-server-url="tcp://127.0.0.1:4321/"
```
```java
@com.bidanet.hprose.starter.annotation.HproseClient(serverName = "testService")
interface TestService{
    public String test(String test);
}
```
Spring 会自动加载 并初始化 TestService客户端，可以在Spring 直接使用。
## 配置