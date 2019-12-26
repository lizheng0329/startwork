package com.gtcom.janusimport.until;

import org.springframework.context.ApplicationContext;

/**
 * @ClassName: SpringContextUtil
 * @Description:獲取上下文工具
 * @auther GH
 * @date 2019/12/18 10:18
 */


public class SpringContextUtil {

    private static ApplicationContext applicationContext;

    public static void setApplicationContext(ApplicationContext context) {
        applicationContext = context;
    }

    public static Object getBean(String beanId) {
        return applicationContext.getBean(beanId);
    }
}