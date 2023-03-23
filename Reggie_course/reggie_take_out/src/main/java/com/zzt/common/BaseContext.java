package com.zzt.common;

/**
 * 基于ThreadLocal封装工具类,保存和获取当前登录用户id,方便同线程方法之间获取
 */
public class BaseContext {
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void setCurrentId(Long id) {
        threadLocal.set(id);
    }

    public static Long getCurrentId() {
        return threadLocal.get();
    }
}
