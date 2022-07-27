package com.dongxian.dxrouter_compiler;

import java.util.Collection;
import java.util.Map;

/**
 * 字符串工具类
 *
 * @author DongXian
 * on 2022/7/20
 */
public final class StringUtils {

    public static boolean isEmpty(CharSequence charSequence) {
        return charSequence == null || charSequence.length() == 0;
    }
}
