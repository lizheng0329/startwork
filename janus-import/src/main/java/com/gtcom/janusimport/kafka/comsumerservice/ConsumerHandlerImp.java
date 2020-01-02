package com.gtcom.janusimport.kafka.comsumerservice;

import com.gtcom.janusimport.config.JanusGraphConfig;
import com.gtcom.janusimport.kafka.worker.DataImportPushTask;
import com.gtcom.janusimport.schema.BuildSchema;
import com.gtcom.janusimport.until.RedisClient;
import net.sf.json.JSONObject;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.janusgraph.core.schema.JanusGraphManagement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.*;
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
    private static   List<JSONObject> listV = new ArrayList<>();
    private static  List<JSONObject> listE = new ArrayList<>();
    private  static  final Integer times = 12*60*60*1000;


    private ExecutorService executor;
    private int numberOfThreads;
    private  AtomicInteger total;
    private  Long startTime ;
    public ConsumerHandlerImp(int numberOfThreads) {
        this.numberOfThreads = numberOfThreads;
        executor = new ThreadPoolExecutor(numberOfThreads, numberOfThreads, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingDeque<>(10), new ThreadPoolExecutor.CallerRunsPolicy());
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
                        if(!listV.isEmpty()){

                            setEndValue(VtName);
                            executor.submit(new DataImportPushTask(listV,"V"));
                            listV = new ArrayList<>();

                        }
                    }
                    if(EgName!=null){
                        if(!listE.isEmpty()){
                            setEndValue(EgName);
                            executor.submit(new DataImportPushTask(listE,"E"));
                            listE = new ArrayList<>();
                        }

                    }
                }else if(jsonObject.containsKey("lable")){
                   // logger.info("---正在消费信息 lable--"+jsonObject.toString());
                  //  logger.error("---------"+total.get()+"------");
                    RedisClient.incr(VtName);
                    listV.add(jsonObject);
                    if(listV.size()>100){
                        executor.submit(new DataImportPushTask(listV,"V"));
                        listV = new ArrayList<>();

                    }
                }else if(jsonObject.containsKey("edgue")){
                //    logger.info("---正在消费信息 edgue --"+jsonObject.toString());
                //    logger.error("---------"+total.get()+"------");
                    RedisClient.incr(EgName);
                    listE.add(jsonObject);
                    if(listE.size()>100){
                        executor.submit(new DataImportPushTask(listE,"E"));
                        listE = new ArrayList<>();
                    }

                }


            }catch (Exception e){

                e.printStackTrace();
            }
        }

    }


    public  void setEndValue(String key) {

        long currTime =System.currentTimeMillis();
        String allcounts = RedisClient.get(key);
        String isExisted = RedisClient.get(key+"_isExisted");
        String isCreated = RedisClient.get(key+"_isCreated");
        HashMap map = new HashMap();
        map.put("allcounts",allcounts);
        map.put("isExisted",isExisted);
        map.put("isCreated",isCreated);
        try {
            if(RedisClient.setKeyWithTime(key+"_"+currTime,map.toString(),times)){
                logger.info("本次消费完成；数据集ID "+key+";共计数"+map.toString());
            }else{
                logger.info("本次消费完成；数据集ID "+key+";共计数"+map.toString());
            }
            RedisClient.del(key);
            RedisClient.del(key+"_isExisted");
            RedisClient.del(key+"_isCreated");

        }catch (Exception E){

        }

    }

    @Override
    public void handles(ConsumerRecords record) {
        logger.info("消费完成----清理缓存数据");
        if(!listE.isEmpty()){
            executor.submit(new DataImportPushTask(listE,"E"));
            listE.clear();
        }
        if(!listE.isEmpty()){
            executor.submit(new DataImportPushTask(listV,"V"));
            listV.clear();
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