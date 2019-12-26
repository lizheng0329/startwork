package com.gtcom.janusimport.config;

import org.springframework.stereotype.Service;

import java.util.Properties;

/**
 * @ClassName: ImportConfiguration
 * @Description:
 * @auther GH
 * @date 2019/12/19 18:23
 */


@Service
public class ImportConfiguration {

    public  String janusIndexPath ;

    private static Properties prop;
    public static String kafkaUrl ;

    public ImportConfiguration() {
        //读配置文件
        prop = FileUtil.getPropertyFile("import_config.properties");
        //---------------------------------------------------------------
        janusIndexPath = prop.getProperty("janusIndexPath").trim();
        kafkaUrl = prop.getProperty("kafkaUrl");

    }
}