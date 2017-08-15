package com.bidanet.hprose.starter.tool;

import com.google.common.base.Strings;
import org.apache.commons.codec.digest.HmacUtils;

/**
 * Created by xuejike on 2017/8/15.
 */
public class UserNamePwdToken {
    protected static String  signature(String username,String pwd,String time){
        String user = username + "\n" +time ;
        String hex = HmacUtils.hmacSha1Hex(user, pwd);
        return user+"\n"+hex;
    }

    public static class Client implements TokenFilter.TokenClient{
        protected String userName;
        protected String pwd;

        @Override
        public String buildToken() {
            return signature(userName,pwd, String.valueOf(System.currentTimeMillis()));
        }
    }
    public static abstract class Server implements TokenFilter.TokenServer{

        @Override
        public String checkToken(String token) {
            String[] split = token.split("\\n");
            String pwd = getPwdByUsername(split[0]);
            String signature = signature(split[0], pwd, split[1]);
            if (signature.equals(token)){
                return split[0];
            }
            return null;
        }
        protected abstract String getPwdByUsername(String username);
    }

}
