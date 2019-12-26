package com.gtcom.janusimport.config;

import org.apache.tinkerpop.gremlin.structure.Transaction;
import org.janusgraph.core.JanusGraph;
import org.janusgraph.core.JanusGraphFactory;
import org.janusgraph.core.schema.JanusGraphManagement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import java.io.InputStream;
import java.util.Iterator;
import java.util.Properties;

/**
 * @author 李兵
 * @version V1.0
 * @description TODO:
 * @date 2019/9/3 17:09
 */

@Configuration
public class JanusGraphConfig {

    private static Logger log = LoggerFactory.getLogger(JanusGraphConfig.class);


    private static final String CONFIG_FILE = "janusgraph.properties";
    public final JanusGraph graph;
    public JanusGraphManagement mgt;




    public static String getRootPath() {
        return System.getProperty("user.dir");
    }

    /**
     * Initialize the graph and the graph management interface.
     * 使用无参构造
     */

    /**此方法也可以获取**/
    public  JanusGraphConfig() {
        Iterator<Object> iter = null;
        JanusGraphFactory.Builder build = null;
        Properties properties = null;
        try{
            InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(CONFIG_FILE);
            properties = new Properties();
            properties.load(inputStream);
            iter = properties.keySet().iterator();
            build = JanusGraphFactory.build();
             }catch (Exception E){
            E.printStackTrace();
         }
            while(iter.hasNext())
            {
                String key = (String) iter.next();
                String value = properties.getProperty(key);
                build.set(key, value);
            }
            graph = build.open();
            boolean open = graph.isOpen();

            if (open) {
                System.out.println("janusgraph open");
                mgt = graph.openManagement();

            }


    }
/*    public JanusGraphConfig() {
        try {
//            this.dropOldKeyspace();
        } catch (Exception ex) {
            log.info("Cannot drop keyspace janusgraph");
        }

        System.out.println(">>>>>>>>>>>>+"+CONFIG_FILE);
        String path = Thread.currentThread()
                .getContextClassLoader()
                .getResource(CONFIG_FILE).getPath().substring(1);
        String decode = "";
        try {
            decode = URLDecoder.decode(path);
            System.out.println(">>>>>>>>>>>>+"+decode);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("获取配置文件路径错误原因：" + e.getLocalizedMessage());
        }
        log.info("Connecting graph");
        graph = JanusGraphFactory.open(decode);
        log.info("Getting management");
        mgt = graph.openManagement();
    }
    */


    public void close() {
        mgt.commit();
        graph.close();
    }

    public void rollback() {
        Transaction tx = graph.tx();
        tx.rollback();
    }

}
