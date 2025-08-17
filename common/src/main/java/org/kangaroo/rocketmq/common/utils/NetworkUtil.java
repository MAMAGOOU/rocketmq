package org.kangaroo.rocketmq.common.utils;


/**
 * @author dongpengkun
 * @className Networkutil
 * @date 2025/8/15
 * @desc 网络工具类
 */
public class NetworkUtil {
    public static final String OS_NAME = System.getProperty("os.name");

    private static boolean isLinuxPlatform = false;
    private static boolean isWindowsPlatform = false;

    static {
        if (OS_NAME != null && OS_NAME.toLowerCase().contains("linux")) {
            isLinuxPlatform = true;
        }
        if (OS_NAME != null && OS_NAME.toLowerCase().contains("windows")) {
            isWindowsPlatform = true;
        }
    }

    public static boolean isLinuxPlatform() {
        return isLinuxPlatform;
    }

    public static boolean isWindowsPlatform() {
        return isWindowsPlatform;
    }
}
