package com.dongxian.personal;

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
@DXRouter({"/personal/activity2"})
public class PersonalModule extends BaseModule {

    private static Map<String, Class> activityMaps = new HashMap<>();

    static {
        activityMaps.put("/personal/activity2", PersonalActivity.class);
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
