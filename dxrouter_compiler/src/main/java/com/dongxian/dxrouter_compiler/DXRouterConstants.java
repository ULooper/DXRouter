package com.dongxian.dxrouter_compiler;

/**
 * 常量
 *
 * @author DongXian
 * on 2022/7/26
 */
public final class DXRouterConstants {

    /**
     * 用于接收每个module的名称
     */
    public static final String OPTIONS = "moduleName";
    /**
     * 自定义注解对应包名
     */
    public static final String DX_ROUTER_ANNOTATION_PACKAGE_NAME = "com.dongxian.dxrouter_annotation.DXRouter";


    public static final String MODULE_MAPS = "moduleMaps";

    /**
     * 文件名称
     */
    public static final String MODULE_MAPS_FILE_NAME = "DXRouter$$";

    /**
     * 文件最终生成包名
     */
    public static final String MODULE_MAPS_FILE_PACKAGE_NAME = "com.dongxian.dxrouter_api";

    /**
     * DXRouter api的ARouterPath 高层标准
     */
    public static final String DXROUTER_API_PATH = MODULE_MAPS_FILE_PACKAGE_NAME + ".DXRouterPath";
}
