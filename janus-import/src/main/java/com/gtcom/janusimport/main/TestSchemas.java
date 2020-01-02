package com.gtcom.janusimport.main;

import com.gtcom.janusimport.config.JanusGraphConfig;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestSchemas {


    private static Logger logger = LoggerFactory.getLogger(TestSchemas.class);


   /* public static void main(String[] args) {
        startUp();
    }*/


    public static void startUp() {

        JanusGraphConfig janusGraphConfig = new JanusGraphConfig();
       // GraphSourceConfig graphSourceConfig = new GraphSourceConfig();




        GraphTraversalSource G = janusGraphConfig.graph.traversal();
        //JanusGraphManagement mget = janusGraphConfig.graph.openManagement();
    /*    G.V().hasLabel("TwitterTweet_1").drop().iterate();
        G.tx().commit();
        G.tx().close();*/
/*        List<Object> starts = (List<Object>) G.V().hasLabel("TwitterUser_1").group().by("username").limit(10).valueMap();
        starts.forEach(p->{
            System.out.println("----"+p.toString());
        });


       *//* if(starts.hasNext()){
            System.out.println("----"+starts.next());
        }*//*

        janusGraphConfig.close();*/
        //System.out.println("删除完成");


            String path = "D:\\JanusGraph\\java\\janus-import\\src\\main\\resources\\schema.json";

              //  logger.info(">>>>>>>"+args[0]);
                //创建schema
                new BuildSchemas().execute(path ,janusGraphConfig);
                //导入数据
            //   importJanu(janusGraphConfig);

    }
/*
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






    }*/



}
