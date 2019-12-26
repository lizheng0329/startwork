package com.gtcom.janusimport.kafka.comsumerservice;

import com.gtcom.janusimport.config.JanusGraphConfig;
import com.gtcom.janusimport.kafka.worker.DataImportPushTask;
import com.gtcom.janusimport.schema.BuildSchema;
import com.gtcom.janusimport.until.RedisClient;
import net.sf.json.JSONObject;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.janusgraph.core.schema.JanusGraphManagement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @ClassName: ImportDateImpl
 * @Description:
 * @auther GH
 * @date 2019/12/18 18:36
 */


public class ConsumerHandlerImp implements ConsumerHandler {


    private static Logger logger = LoggerFactory.getLogger(ConsumerHandlerImp.class);
    private static Logger log = LoggerFactory.getLogger(ConsumerHandlerImp.class);
    private static  List<JSONObject> list = new ArrayList<>();
    private ExecutorService executor;
    private int numberOfThreads;
    private  AtomicInteger total;
    private  Long startTime ;
    public ConsumerHandlerImp(int numberOfThreads) {
        this.numberOfThreads = numberOfThreads;
        executor = new ThreadPoolExecutor(numberOfThreads, numberOfThreads, 0L, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(1000), new ThreadPoolExecutor.CallerRunsPolicy());
         total = new AtomicInteger(1);
        startTime=System.currentTimeMillis();
    }




    @Override
    public void handle(ConsumerRecord record) {

        Object value = record.value();
        JSONObject jsonObject = null;

        try {
            jsonObject = JSONObject.fromObject(record.value());
        } catch (Exception e) {
            log.error("专题参数不是合法的json格式：{}", value, record.offset());
            log.error("异常信息：", e);
        }

         if(jsonObject!=null){

             total.addAndGet(1);
         try{
                //   if(value.containsKey("EOF")&&("begin").equals(value.getString("EOF")))
                String  VtName = null;
                String  EgName = null;
                VtName = jsonObject.containsKey("lable")? jsonObject.getString("lable"):null;
                EgName = jsonObject.containsKey("edgue")? jsonObject.getString("edgue"):null;
                if(jsonObject.containsKey("EOF")&&("begin").equals(jsonObject.getString("EOF"))){

                    String  edgeNme = null;
                    String  indexName = null;
                    if(EgName!=null){
                        edgeNme = EgName;
                        if(RedisClient.get(edgeNme)!=null){
                            RedisClient.del(edgeNme);
                        }else {
                            RedisClient.incr(edgeNme);
                        }
                    }
                    if(VtName!=null){
                        indexName = VtName;
                    }
                    if(!checkVname(VtName,indexName)){
                        logger.info("开始V建立索引");
                        new BuildSchema().execute(VtName,EgName,indexName,edgeNme);
                    }
                    if(!checkEname(EgName,edgeNme)){
                        logger.info("开始E建立索引");
                        new BuildSchema().execute(VtName,EgName,indexName,edgeNme);
                    }

                }else if(jsonObject.containsKey("EOF")&&("end").equals(jsonObject.getString("EOF"))){
                    if(VtName!=null){
                        logger.info("本次消费；"+VtName+";共计数"+RedisClient.get(VtName));
                        if(!list.isEmpty()){
                            executor.submit(new DataImportPushTask(list,"V"));
                            list.clear();
                        }
                    }
                    if(EgName!=null){
                        logger.info("本次消费；"+EgName+";共计数"+RedisClient.get(VtName));
                        if(!list.isEmpty()){
                            executor.submit(new DataImportPushTask(list,"E"));
                            list.clear();
                        }

                    }
                }else if(jsonObject.containsKey("lable")){
                   // logger.info("---正在消费信息 lable--"+jsonObject.toString());
                  //  logger.error("---------"+total.get()+"------");
                    RedisClient.incr(VtName);
                    list.add(jsonObject);
                    if(list.size()>10){
                        executor.submit(new DataImportPushTask(list,"V"));
                        list.clear();

                    }
                }else if(jsonObject.containsKey("edgue")){
                    logger.info("---正在消费信息 edgue --"+jsonObject.toString());
                //    logger.error("---------"+total.get()+"------");
                    RedisClient.incr(EgName);

                    list.add(jsonObject);
                    if(list.size()>10){
                        executor.submit(new DataImportPushTask(list,"E"));
                        list.clear();
                    }

                }


            }catch (Exception e){

            }
        }


    }

    public  Boolean checkEname( String EgName, String edgeNme) {
        JanusGraphConfig janusGraphConfig = new JanusGraphConfig();
        JanusGraphManagement mgmt= janusGraphConfig.graph.openManagement();
        boolean flag = false;
        if(EgName!=null&&!"".equals(EgName)){
            flag =mgmt.containsEdgeLabel(EgName);
        }else if(edgeNme!=null&&!"".equals(edgeNme)){
            flag =mgmt.containsGraphIndex(edgeNme);
        }
        return flag;
    } public  Boolean checkVname(String VtName,String indexName) {
        JanusGraphConfig janusGraphConfig = new JanusGraphConfig();
        JanusGraphManagement mgmt = janusGraphConfig.graph.openManagement();
        boolean flag = false;
        if(VtName!=null&&!"".equals(VtName)){
            flag =mgmt.containsVertexLabel(VtName);
        }else if(indexName!=null&&!"".equals(indexName)){
            flag =mgmt.containsGraphIndex(indexName);
        }
        return flag;
    }

    public void shutdown(){
        if (executor != null) {
            executor.shutdown();
        }
        try {
            if (!executor.awaitTermination(5000, TimeUnit.MILLISECONDS)) {
                log.error("Timed out waiting for consumer threads to shut down, exiting uncleanly");
            }
        } catch (InterruptedException e) {
            log.error("Interrupted during shutdown, exiting uncleanly");
        }
    }
}