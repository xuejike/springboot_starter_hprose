package com.bidanet.hprose.starter.core;

import com.bidanet.hprose.starter.doc.DocBuildTool;
import org.springframework.stereotype.Component;

import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by xuejike on 2017/6/29.
 */
@Component
public class SpringBootHprose {
    protected Map<String,Class> serviceMap=new HashMap<>();
    protected Map<String,Class> entityMap=new HashMap<>();


    public void addService(String name,Class service){
        serviceMap.put(name, service);
    }
    public void addEntity(String name,Class entity){
        entityMap.put(name, entity);
    }

    public void mdDoc(String title, Writer out){
        HashMap<String, Object> map = new HashMap<>();
        map.put("title",title);
        map.put("entities", DocBuildTool.docEntity(entityMap));
        map.put("services", DocBuildTool.docService(serviceMap));


        DocBuildTool.outMdContent(map,out);

    }
    public void htmlDoc(String title,Writer out){
        HashMap<String, Object> map = new HashMap<>();
        map.put("title",title);
        map.put("entities", DocBuildTool.docEntity(entityMap));
        map.put("services", DocBuildTool.docService(serviceMap));

        DocBuildTool.outHtmlContent(map,out);
    }





}
