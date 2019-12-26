/*
package com.gtcom.janusimport.main;

import com.gtcom.janusimport.config.JanusGraphConfig;
import com.gtcom.janusimport.schema.MysqlSelect;
import org.janusgraph.core.JanusGraphTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

*/
/**
 * @ClassName: TestSchema
 * @Description:
 * @auther GH
 * @date 2019/11/25 14:02
 *//*



@Service
public class TestSchema  {


    private static Logger logger = LoggerFactory.getLogger(TestSchema.class);


    @Autowired
    JanusGraphConfig janusGraphConfig;

    public static void startUp() {

        JanusGraphConfig janusGraphConfig = new JanusGraphConfig();


              //  logger.info(">>>>>>>"+args[0]);
                //创建schema
             //   new BuildSchema().execute("" ,janusGraphConfig);
                //导入数据
            //   importJanu(janusGraphConfig);


    }

    public  static void importJanu(JanusGraphConfig janusGraphConfig ) {

        JanusGraphTransaction tx = null;
        try{
            logger.info("graphFactory 初始化完成......");
             tx =  janusGraphConfig.graph.newTransaction(); // 获取新事务,添加节点信息使用
            logger.info("tx           初始化完成......");
            org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource g = janusGraphConfig.graph.traversal(); // 获取遍历源,判断是否存在使用
            logger.info("g            初始化完成......");
            java.util.concurrent.atomic.AtomicInteger total = new java.util.concurrent.atomic.AtomicInteger(1); // 计数器

            //导入顶点
          //  MysqlSelect.addDataVer( tx, g,total,janusGraphConfig);
            //导入边
            MysqlSelect.addDataEdge( tx, g,total,janusGraphConfig);

            logger.info("Current Position >> " + total.get());

            // 分批次提交,每次提交事务都会关闭,提交后需要重新创建事务

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            tx.close();
            janusGraphConfig.close();

        }






    }



}*/
