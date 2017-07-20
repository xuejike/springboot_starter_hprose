package com.bidanet.hprose.starter.controller;

import com.bidanet.hprose.starter.core.SpringBootHprose;
import com.google.common.base.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * Created by xuejike on 2017/6/29.
 */

public class HproseController {
    @Autowired
    SpringBootHprose springBootHprose;




    public void doc(String title, ServletResponse response){
        if (Strings.isNullOrEmpty(title)){
            title="hprose接口定义文档";
        }
        try {
            ServletOutputStream outputStream = response.getOutputStream();
            OutputStreamWriter writer = new OutputStreamWriter(outputStream);
            springBootHprose.mdDoc(title,writer);
        } catch (IOException e) {
            e.printStackTrace();
        }

//        return springBootHprose.mdDoc(title);
    }
}
