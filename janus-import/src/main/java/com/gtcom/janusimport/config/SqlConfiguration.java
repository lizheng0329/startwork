package com.gtcom.janusimport.config;

import java.util.Properties;

/**
 * Created by 51771 on 2018/3/27.
 */
public class SqlConfiguration {
    public  String GP_URL ;
    public  String GP_USER ;
    public  String GP_PASSWD ;
    public  String MYSQL_URL;
    public  String MYSQL_USER;
    public  String MYSQL_PASSWD;

    public  String GP_NEWS_FIELD;
    public  String GP_NEWS_STR_FIELD;
    public  String GP_SOCIAL_FIELD;
    public  String GP_SOCIAL_STR_FIELD;

    private static Properties prop;

    public SqlConfiguration(){
        //读配置文件
        prop = FileUtil.getPropertyFile("sql_config.properties");
        //---------------------------------------------------------------
        GP_URL = prop.getProperty("GP_URL").trim();
        GP_USER = prop.getProperty("GP_USER").trim();
        GP_PASSWD = prop.getProperty("GP_PASSWD").trim();
        MYSQL_URL = prop.getProperty("MYSQL_URL").trim();
        MYSQL_USER = prop.getProperty("MYSQL_USER").trim();
        MYSQL_PASSWD = prop.getProperty("MYSQL_PASSWD").trim();

        GP_NEWS_FIELD = prop.getProperty("GP_NEWS_FIELD").trim();
        GP_NEWS_STR_FIELD = prop.getProperty("GP_NEWS_STR_FIELD").trim();
        GP_SOCIAL_FIELD = prop.getProperty("GP_SOCIAL_FIELD").trim();
        GP_SOCIAL_STR_FIELD = prop.getProperty("GP_SOCIAL_STR_FIELD").trim();

    }
}
