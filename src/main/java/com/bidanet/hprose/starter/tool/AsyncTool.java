package com.bidanet.hprose.starter.tool;

import hprose.util.concurrent.Promise;
import org.springframework.web.context.request.async.DeferredResult;

/**
 * Created by xuejike on 2017/7/18.
 */
public class AsyncTool {
    public static DeferredResult asyncControllerResult(Promise promise,Long timeout,Object timeObj){
        DeferredResult deferredResult=new DeferredResult(timeout,timeObj);
        promise.then(q->{
            deferredResult.setResult(q);
        });

        return deferredResult;
    }
    public static DeferredResult asyncControllerResult(Promise promise){
        return asyncControllerResult(promise,3*1000L,"请求超时");
    }

}
