package com.dongxian.dxrouter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.dongxian.dxrouter_annotation.DXRouter;
import com.dongxian.dxrouter_api.BaseModule;

import java.util.HashMap;
import java.util.Map;

/**
 * @author DongXian
 * on 2022/7/26
 */
@DXRouter({"/app/activity1","/app/activity2"})
public class MainModule extends BaseModule {

    private static Map<String, Class> activityMaps = new HashMap<>();

    static {
        activityMaps.put("/app/activity1", MainActivity.class);
        activityMaps.put("/app/activity2", Main2Activity.class);
    }

    @Override
    public void route(Context context, String path, Bundle bundle, int requestCode) {
        Class clazz = activityMaps.get(path);
        if (clazz != null) {
            Intent intent = new Intent(context, clazz);
            intent.putExtras(bundle);
            if (requestCode > 0 && context instanceof Activity) {
                ((Activity) context).startActivityForResult(intent, requestCode);
            } else {
                context.startActivity(intent);
            }
        }
    }
}
