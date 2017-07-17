package com.bidanet.hprose.starter.doc;

import com.bidanet.hprose.starter.annotation.HproseDoc;
import com.bidanet.hprose.starter.annotation.HproseEntity;
import com.bidanet.hprose.starter.annotation.HproseService;
import com.bidanet.hprose.starter.tool.AnnotationTool;
import com.google.common.base.Strings;
import freemarker.template.*;
import hprose.io.HproseClassManager;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by xuejike on 2017/6/28.
 */
public class DocBuildTool {



    public static void outMdContent(Map<String,Object> data, Writer out){
        outContent(data,out,"HproseDoc.ftl");
    }
    public static void outHtmlContent(Map<String,Object> data,Writer out){
        outContent(data,out,"HproseDocHtml.ftl");
    }
    public static void outContent(Map<String,Object> data,Writer out,String tpl){
        Configuration configuration = new Configuration();
        try {
            configuration.setDirectoryForTemplateLoading(new ClassPathResource(".\\").getFile());
            configuration.setObjectWrapper(new DefaultObjectWrapper());
            Template template = configuration.getTemplate(tpl);
            template.process(data,out);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            e.printStackTrace();
        }
    }

    public static List<DocItem> docEntity(Map<String, Class> entityMap){
        ArrayList<DocItem> docItems = new ArrayList<>();
        for (Map.Entry<String, Class> entry : entityMap.entrySet()) {
            Class value = entry.getValue();
            HproseEntity entityAnno = (HproseEntity) value.getAnnotation(HproseEntity.class);
            String describe = entityAnno.describe();
            if (Strings.isNullOrEmpty(describe)){
                describe=value.getSimpleName();
            }
            HproseDoc docAnn = (HproseDoc) value.getAnnotation(HproseDoc.class);
            if (docAnn!=null){
                String s = docAnn.value();
                if (Strings.isNullOrEmpty(s)){
                    s=value.getSimpleName();
                }
                describe=s;
            }
            DocItem docItem = new DocItem(describe, entry.getKey());
            docItem.setTitle("字段名称","类型","描述");
            Field[] fields = value.getDeclaredFields();
            for (Field field : fields) {
                String title = AnnotationTool.getDocValue(field);

                docItem.addRow(field.getName(),getTypeString(field.getType()), title);
            }

            docItems.add(docItem);
        }
        return docItems;

    }


    public static List<DocItem> docService(Map<String, Class> serviceMap){
        ArrayList<DocItem> docItems = new ArrayList<>();
        for (Map.Entry<String, Class> entry : serviceMap.entrySet()) {
            Class value = entry.getValue();

            HproseService annotation = (HproseService) value.getAnnotation(HproseService.class);
            DocItem docItem = new DocItem(annotation.describe(), entry.getKey());

            Method[] methods = value.getDeclaredMethods();

            for (Method method : methods) {
                int modifiers = method.getModifiers();
                if (modifiers==1){

                    DocItem item = docMethod(method);
                    docItem.addSubDoc(item);
                }

            }
            docItems.add(docItem);
        }

        return docItems;
    }

    private static DocItem docMethod(Method method) {
        HproseDoc doc = method.getAnnotation(HproseDoc.class);
        String value = method.getName();
        if (doc!=null){
            value = doc.value();
        }
        DocItem item = new DocItem(value, method.getName());
        item.setTitle("名称","类型","说明");
        item.addRow("->参数<-","","");
        Parameter[] parameters = method.getParameters();
        //方法参数
        LocalVariableTableParameterNameDiscoverer discoverer = new LocalVariableTableParameterNameDiscoverer();
        String[] parameterNames = discoverer.getParameterNames(method);

        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            String name = parameter.getName();
            if (parameterNames!=null&&i<parameterNames.length){
                name=parameterNames[i];
            }

            Class<?> type = parameter.getType();

            String desc= AnnotationTool.getDocValue(parameter);

            item.addRow(name,getTypeString(type),desc);
        }
//

        //返回值
        item.addRow("->返回值<-","","");
        item.addRow("",getTypeString(method.getReturnType()),"");
        return item;
    }

    protected static String getTypeString(Class cls){
        String s = HproseClassManager.getClassAlias(cls);
        if (s==null){
            return cls.getSimpleName();
        }else{
            return s;
        }
    }
}
