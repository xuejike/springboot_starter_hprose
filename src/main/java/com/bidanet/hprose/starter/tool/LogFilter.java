package com.bidanet.hprose.starter.tool;

import hprose.common.HproseContext;
import hprose.common.HproseFilter;
import hprose.util.StrUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

/**
 * Created by xuejike on 2017/8/11.
 */
public class LogFilter implements Filter {
    protected Logger logger= LoggerFactory.getLogger(LogFilter.class);
    @Override
    public HproseFilter getClient() {
        return cilent;
    }

    @Override
    public HproseFilter getServer() {
        return server;
    }

    @Override
    public int getOrder() {
        return -10;
    }
    protected HproseFilter server=new HproseFilter() {
        @Override
        public ByteBuffer inputFilter(ByteBuffer data, HproseContext context) {
            logger.info("接收请求->{}", StrUtil.toString(data));
            return data;
        }

        @Override
        public ByteBuffer outputFilter(ByteBuffer data, HproseContext context) {
            logger.info("服务器回复->{}",StrUtil.toString(data));
            return data;
        }
    };
    protected HproseFilter cilent=new HproseFilter() {
        @Override
        public ByteBuffer inputFilter(ByteBuffer data, HproseContext context) {
            logger.info("客户端接收->{}",StrUtil.toString(data));
            return data;
        }

        @Override
        public ByteBuffer outputFilter(ByteBuffer data, HproseContext context) {
            logger.info("客户端发送->{}",StrUtil.toString(data));
            return data;
        }
    };
}
