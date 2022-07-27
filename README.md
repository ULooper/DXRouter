### 一个参考ARouter的自定义路由学习框架，相关详细解释请移步博客：[ARouter原理解析之自定义路由框架DXRouter](https://blog.csdn.net/a734474820/article/details/126008898?spm=1001.2014.3001.5502)

### 实现思路如下：
1.各个模块定义属于自己的Module中心统一管理activity跳转逻辑，并在注解上添加各个activity的路由地址，例如：
```java
@DXRouter({"/app/activity1","/app/activity2"})
public class MainModule extends BaseModule {

    private static Map<String, Class> activityMaps = new HashMap<>();

    static {
        activityMaps.put("/app/activity1", MainActivity.class);
        activityMaps.put("/app/activity2", Main2Activity.class);
    }
	//route中完成具体的activity跳转功能
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
```
其中`BaseModule`
```java
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
```
2.注解解析器根据注解动态生成各模块的modulemap集合，apt生成代码如下：
```java
public class DXRouter$$app$$ModuleMaps implements DXRouterPath {
  @Override
  public Map<String, String> getModuleMaps() {
    Map<String,String> moduleMaps= new HashMap<>();
    moduleMaps.put("/app/activity1","com.dongxian.dxrouter.MainModule");
    moduleMaps.put("/app/activity2","com.dongxian.dxrouter.MainModule");
    return moduleMaps;
  }
}
```
3.发起路由跳转时，通过传入的`path`查找对应的`module中心管理类`【根据包名反射生成并做缓存】，完成对应路由跳转；

### 如果此项目对你有一点点帮助，感谢给个Star ~~
