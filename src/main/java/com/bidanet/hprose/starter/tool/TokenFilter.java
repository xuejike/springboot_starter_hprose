package com.bidanet.hprose.starter.tool;

import hprose.common.HproseContext;
import hprose.common.HproseFilter;
import hprose.io.ByteBufferStream;
import hprose.util.StrUtil;
import org.apache.commons.codec.net.URLCodec;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Created by xuejike on 2017/7/25.
 */
public class TokenFilter implements Filter {

    protected char TAG='#';
    protected TokenClient tokenClient;
    protected TokenServer tokenServer;
    URLCodec urlCodec = new URLCodec();
    protected ThreadLocal<String> clientUserName =new ThreadLocal<>();

    @Override
    public int getOrder() {
        return -1;
    }

    public String getClientUserName(){
        return clientUserName.get();
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

    }
    public ByteBuffer decode(ByteBuffer data)  {
//        String content = StrUtil.toString(data);
        byte b = data.get(0);
        if (b==TAG){
            byte[] array = data.array();
            int end=0;
            for (int i = 1; i < array.length; i++) {
                if (array[i]==TAG){
                    end=i;
                }
            }
            byte[] tokenArray = Arrays.copyOfRange(array, 1, end);
            String token = StrUtil.toString(tokenArray);
            try {
                String decode = urlCodec.decode(token, "UTF-8");
                String checkUser = tokenServer.checkToken(decode);
                if (checkUser !=null){
//                    String userName = tokenServer.getUserName(decode);
                    clientUserName.set(checkUser);
                }else{
                    throw new TokenCheckErrorExeption();
                }

            } catch (Exception e) {
                throw new RuntimeException("Token 解析异常");
            }


            ByteBufferStream stream = new ByteBufferStream();
            stream.write(Arrays.copyOfRange(array,end+1,array.length));
            return stream.buffer;
        }else{
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
            if (tokenClient==null){
                return data;
            }
            return encode(data, tokenClient.buildToken());
        }
    }
    public class Server implements HproseFilter{

        @Override
        public ByteBuffer inputFilter(ByteBuffer data, HproseContext context) {
            if (tokenServer==null){
                return data;
            }
            return decode(data);
        }

        @Override
        public ByteBuffer outputFilter(ByteBuffer data, HproseContext context) {
            return data;
        }
    }
    public interface TokenClient{
        String buildToken();
    }
    public interface TokenServer{
        String checkToken(String token);
//        String getUserName(String token);
    }
    public class TokenCheckErrorExeption extends RuntimeException{
        public TokenCheckErrorExeption() {
            super("Token校验失败");
        }
    }
}
