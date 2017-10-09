package com.bidanet.hprose.starter.tool;

import hprose.common.HproseContext;
import hprose.common.HproseFilter;
import hprose.io.ByteBufferStream;
import hprose.util.StrUtil;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.net.URLCodec;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Created by xuejike on 2017/7/25.
 */
public class TokenFilter implements Filter {
    protected String token=null;
    protected char TAG='#';
    URLCodec urlCodec = new URLCodec();
    protected ThreadLocal<String> clientToken=new ThreadLocal<>();

    @Override
    public int getOrder() {
        return -1;
    }

    public String getClientToken(){
        return clientToken.get();
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public HproseFilter getClient(){
        return new Client();
    }
    @Override
    public HproseFilter getServer(){
        return new Server();
    }
    public ByteBuffer encode(ByteBuffer data,String token)  {
//        String content = StrUtil.toString(data);
        try{
            if (token==null){
                return data;
            }else{

                ByteBufferStream stream = new ByteBufferStream();
                stream.write(TAG);
                try {
                    stream.write(urlCodec.encode(token,"UTF-8").getBytes());
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException("Token 编码异常");
                }
                stream.write(TAG);
                stream.write(data);

                return stream.buffer;
            }
        }catch (Exception ex){
            ex.printStackTrace();
            return data;
        }


    }
    public ByteBuffer decode(ByteBuffer data)  {

        try {
            String content = StrUtil.toString(data);
            byte[] array = content.getBytes();

            byte b = array[0];
            if (b==TAG){

                int end=0;
                for (int i = 1; i < array.length; i++) {
                    if (array[i]==TAG){
                        end=i;
                        break;
                    }
                }
                byte[] tokenArray = Arrays.copyOfRange(array, 1, end);
                String token = StrUtil.toString(tokenArray);
                try {
                    String decode = urlCodec.decode(token, "UTF-8");
                    clientToken.set(decode);
                } catch (Exception e) {
                    throw new RuntimeException("Token 解析异常");
                }


                ByteBufferStream stream = new ByteBufferStream();
                stream.write(Arrays.copyOfRange(array,end+1,array.length));
                return stream.buffer;
            }else{
                return data;
            }
        }catch (Exception ex){
            ex.printStackTrace();
            return data;
        }


    }

    public class Client implements HproseFilter {



        @Override
        public ByteBuffer inputFilter(ByteBuffer data, HproseContext context) {
//            System.out.println("client->input");
            return data;
        }

        @Override
        public ByteBuffer outputFilter(ByteBuffer data, HproseContext context) {
            return encode(data,token);
        }
    }
    public class Server implements HproseFilter{

        @Override
        public ByteBuffer inputFilter(ByteBuffer data, HproseContext context) {
            return decode(data);
        }

        @Override
        public ByteBuffer outputFilter(ByteBuffer data, HproseContext context) {
            return data;
        }
    }
}
