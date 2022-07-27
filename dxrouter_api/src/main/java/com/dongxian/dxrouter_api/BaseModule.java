package com.dongxian.dxrouter_api;

import android.content.Context;
import android.os.Bundle;

/**
 * @author DongXian
 * on 2022/7/26
 */
public abstract class BaseModule {

    /**
     * 跳转逻辑处理
     * @param context 上下文
     * @param path 路由path
     * @param bundle 携带参数
     * @param requestCode requestCode
     */
    public abstract void route(Context context, String path, Bundle bundle, int requestCode);
}
