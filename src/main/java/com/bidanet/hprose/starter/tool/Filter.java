package com.bidanet.hprose.starter.tool;

import hprose.common.HproseFilter;

/**
 * Created by xuejike on 2017/8/11.
 */
public interface Filter {
    HproseFilter getClient();

    HproseFilter getServer();
    default int getOrder(){
        return 0;
    }
}
