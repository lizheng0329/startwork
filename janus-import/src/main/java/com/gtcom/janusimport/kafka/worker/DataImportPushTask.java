package com.gtcom.janusimport.kafka.worker;

import com.gtcom.janusimport.config.JanusGraphConfig;
import com.gtcom.janusimport.kafka.comsumerservice.ConsumerHandlerImp;
import com.gtcom.janusimport.service.impl.ImportDataImpl;
import net.sf.json.JSONObject;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.janusgraph.core.JanusGraphTransaction;
import org.janusgraph.core.schema.JanusGraphManagement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @ClassName: DataImportPushTask
 * @Description:
 * @auther GH
 * @date 2019/12/18 18:40
 */


public class DataImportPushTask implements Runnable {


    private static Logger logger = LoggerFactory.getLogger(ConsumerHandlerImp.class);
 /*   private ExecutorService executor;
    private int numberOfThreads = 1;


   // private  String janusIndexPath = "D:\\JanusGraph\\JanusGraph\\src\\main\\resources\\schema.json";

    {
        executor = new ThreadPoolExecutor(numberOfThreads, numberOfThreads, 0L, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(1000), new ThreadPoolExecutor.CallerRunsPolicy());
    }
*/

/*    private ImportDataImpl importData;
    private BuildSchema  buildSchema;*/
    private JanusGraphTransaction tx;
    private GraphTraversalSource g ;
    private JanusGraphManagement mgmt;
    private JanusGraphConfig janusGraphConfig;

/*      {
*//*        importData= (ImportDataImpl) SpringContextUtil.getBean("ImportDataImpl");
        buildSchema= (BuildSchema) SpringContextUtil.getBean("BuildSchema");*//*
          janusGraphConfig = new JanusGraphConfig();
          tx= this.janusGraphConfig.graph.newTransaction();
          g= this.janusGraphConfig.graph.traversal();
          mgmt= this.janusGraphConfig.graph.openManagement();
          logger.info("---初始化-----------完成------------");
      }*/
    public DataImportPushTask() {

    }
    private List<JSONObject> value;
    private String type;

    public DataImportPushTask(List<JSONObject> value,String type) {
        this.value = value;
        this.type = type;

    }

     @Override
    public void run() {
        logger.error("正在消费的数据"+value.toString());
         improtDataVEList(value);
/*
         System.out.println("------"+type+"----------");
            if("V".equals(type)){
                improtDataVEList(value);
            }
            if("E".equals(type)){
                improtDataEList(value);
            }
*/


    }


    public  void improtDataVEList(List<JSONObject> list) {
        logger.error(">>>>>>正在遍历插入数据--V-->>>>>>>>>");
        long startTime=System.currentTimeMillis();
       JanusGraphConfig janusGraphConfig = new JanusGraphConfig();
        JanusGraphTransaction tx= janusGraphConfig.graph.newTransaction();
        GraphTraversalSource g= janusGraphConfig.graph.traversal();
        for (JSONObject p : list) {
            try {

                if(p.containsKey("lable")){
                    new ImportDataImpl().importDataV(p, tx, g);
                }
                if(p.containsKey("edgue")){
                    new ImportDataImpl().importDataE(p, g);
                }
            } catch (Exception E) {
                logger.error(">>>>>>" + p.toString() + ">>>>>>>>>插入异常"+E.getMessage());

                E.printStackTrace();
            }
        }
        try {
                if(tx.isClosed()){
                    tx = new JanusGraphConfig().graph.newTransaction();
                    tx.commit();

                }else {
                    tx.commit();

                }
                g.tx().commit();
                g.tx().open();
                logger.warn("Current Position  >>----" +
                        " "+"---提交---;话费时间；"+(System.currentTimeMillis()-startTime)/1000+"秒");

            } catch (java.lang.Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    g.close();
                    tx.close();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


    }
/*    public  void improtDataEList(List<JSONObject> list) {
        logger.error(">>>>>>正在遍历插入数据---E--->>>>>>>>>");
        long startTime=System.currentTimeMillis();
        JanusGraphConfig janusGraphConfig = new JanusGraphConfig();
        JanusGraphTransaction tx= janusGraphConfig.graph.newTransaction();
        GraphTraversalSource g= janusGraphConfig.graph.traversal();
        for (JSONObject p : list) {
            try {
                new ImportDataImpl().importDataE(p, g);
            } catch (Exception E) {
                logger.error(">>>>>>" + p.toString() + ">>>>>>>>>插入异常"+E.getMessage());

                E.printStackTrace();
            }
        }
        try {
                g.tx().commit();
                g.tx().open();
            logger.warn("Current Position  >>----" +
                    " "+"---提交---;话费时间；"+(System.currentTimeMillis()-startTime)/1000+"秒");

        } catch (java.lang.Exception e) {
            e.printStackTrace();
        } finally {
            try {
                g.close();
                tx.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }*/








}