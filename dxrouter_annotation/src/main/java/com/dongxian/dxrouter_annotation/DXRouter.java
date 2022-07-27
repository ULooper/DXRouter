package com.dongxian.dxrouter_annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义注解,最终使用效果
 @DXRouter({"path1","path2"})
 public class Module1 extends DXModule{
    Override
    public void start(Context context,Bundle bundle){
    ...
 }
 }
 * @author DongXian
 * on 2022/7/26
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface DXRouter {
    String[] value() default {};
}
