package com.dongxian.dxrouter_api;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.LruCache;

import java.util.HashMap;
import java.util.Map;

/**
 * 核心跳转类
 *
 * @author DongXian
 * on 2022/7/26
 */
public final class DXRouter {
    private final String TAG = DXRouter.class.getSimpleName();
    /**
     * 跳转路径前缀
     */
    private final String TARGET_PREFIX = "com.dongxian.dxrouter_api.DXRouter$$";
    /**
     * 跳转路径后缀
     */
    private final String TARGET_SUFFIX = "$$ModuleMaps";

    private static volatile DXRouter instance;
    private Map<String, Map<String, String>> groupMap;
    private Map<String, BaseModule> moduleMap;

    private DXRouter() {
        groupMap = new HashMap<>(20);
        moduleMap = new HashMap<>(100);
    }

    public static DXRouter getInstance() {
        if (instance == null) {
            synchronized (DXRouter.class) {
                if (instance == null) {
                    instance = new DXRouter();
                }
            }
        }
        return instance;
    }

    /**
     * 携带Bundle数据传输
     *
     * @param path    目标
     * @param context 上下文参数
     * @param bundle  携带数据
     */
    public void navigation(String path, Context context, Bundle bundle) {
        if (bundle == null) {
            bundle = new Bundle();
        }
        this.navigation(context, path, bundle, -1);
    }


    /**
     * 直接跳转
     *
     * @param path    目标
     * @param context 上下文参数
     */
    public void navigation(String path, Context context) {
        this.navigation(context, path, new Bundle(), -1);
    }


    /**
     * 携带Bundle以及requestCode跳转
     *
     * @param context
     * @param path
     * @param bundle
     * @param requestCode
     */
    public void navigation(Context context, String path, Bundle bundle, int requestCode) {
        if (!isLegalPath(path)) {
            //路径非法直接return
            Log.e(TAG, "The path:" + path + "is Illegal!!!");
            return;
        }
        //截图出group名称
        String finalGroup = path.substring(1, path.indexOf("/", 1));
        Map<String, String> moduleMaps = groupMap.get(finalGroup);
        if (moduleMaps == null) {
            //通过反射生成相关类
            String clazzName = TARGET_PREFIX + finalGroup + TARGET_SUFFIX;
            try {
                Class<?> moduleMap = Class.forName(clazzName);
                DXRouterPath dxRouterPath = (DXRouterPath) moduleMap.newInstance();
                moduleMaps = dxRouterPath.getModuleMaps();
                groupMap.put(finalGroup, moduleMaps);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (moduleMaps == null) {
            Log.e(TAG, "The groupMap is null");
            return;
        }
        String moduleName = moduleMaps.get(path);
        if (TextUtils.isEmpty(moduleName)) {
            Log.e(TAG, "No Find Module by The path:" + path);
            return;
        }
        BaseModule baseModule = moduleMap.get(moduleName);
        if (baseModule == null) {
            try {
                Class<?> module = Class.forName(moduleName);
                baseModule = (BaseModule) module.newInstance();
                moduleMap.put(moduleName, baseModule);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (baseModule == null) {
            Log.e(TAG, "Not Find Module by Path!!!");
            return;
        }
        if (bundle == null) {
            bundle = new Bundle();
        }
        baseModule.route(context, path, bundle, requestCode);
    }


    /**
     * 检查path路径是否符合/app/activity格式要求
     *
     * @param path
     * @return
     */
    private boolean isLegalPath(String path) {
        if (TextUtils.isEmpty(path) || !path.startsWith("/")) {
            return false;
        }

        if (path.lastIndexOf("/") == 0) {
            return false;
        }
        return true;

    }
}
