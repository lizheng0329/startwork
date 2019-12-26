package com.gtcom.janusimport.schema;

import com.gtcom.janusimport.config.JanusGraphConfig;
import com.gtcom.janusimport.until.RowkeyUtil;
import net.sf.json.JSONObject;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 每类用于通过文本文档向JanusGraph导入数据.
 * 文档内容大体如下:
 * {"~id":1,"~label":"person","key1":"value1","key2":"value2","key3":"value3"}
 * 其中label为必须项,当setvertexid为true时,id也为必须项,反之为可选项,其他内容可自由添加(Schema中存在的)
 *
 * @author gh
 *
 **/


public class ImportVertexNoCheckID implements ImprotTask {

  private static Logger logger = LoggerFactory.getLogger(ImportVertexNoCheckID.class);


  public void execute(String schema, net.sf.json.JSONObject options, JanusGraphConfig janusGraphConfig , org.janusgraph.core.JanusGraphTransaction tx , org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource g, java.util.concurrent.atomic.AtomicInteger total) {

    //初始化数据
    //~id自动生成


    JSONObject content = null;
  //  org.janusgraph.core.JanusGraphVertex v = null;
     Vertex v =null;
    try {
      total.addAndGet(1);
        if(options!=null){
          content= options;
      }
      content.put("label",schema);

      //时间格式转换
      String username = "";
      if(content.containsKey("username")){

         username = content.getString("username");
      }
      if(content.containsKey("followScreanName")){

            username = content.getString("followScreanName");
      }
      if(content.containsKey("registeredTime")){
        String registeredTime = content.getString("registeredTime");
        if(registeredTime!=null){
          content.put("registeredTime",TimeToStamp(registeredTime));
        }
      }
      if(content.containsKey("inputTime")){
        String inputTime = content.getString("inputTime");
        if(inputTime!=null){
          content.put("inputTime",TimeToStamp(inputTime));
        }
      }
      if(content.containsKey("pdateClrTime")){

        String pdateClrTime = content.getString("pdateClrTime");
        if(pdateClrTime!=null){
          content.put("pdateClrTime",TimeToStamp(pdateClrTime));
        }
      }
      content.remove("bornTime");
     /* if(content.containsKey("bornTime")){

        String bornTime = content.getString("bornTime");
        if(bornTime!=null){
          content.put("bornTime",TimeToStamp(bornTime));
        }
      }*/
      logger.info(">>>>>>>"+content.toString());
    //  System.exit(1);

      //生成唯一ID
      String id = RowkeyUtil.rowkeyByName(username);

      content.put("uuid",id);

      long vertexid = 0;
      Vertex vertex = null;
       // id = (content.containsKey("~id") ? content.getLong("~id") : (content.containsKey("id") ? content.getLong("id") : null)); // 获取id信息
      String label = (content.containsKey("~label") ? content.getString("~label")
              : (content.containsKey("label") ? content.getString("label") : null)); // 获取label信息
      logger.info(">>>>>>"+"id :"+id+"; label:" +label);

  //    System.exit(1);
      org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal<Vertex, Vertex> Vertex = g.V().hasLabel(schema).has("uuid",id);
      if(Vertex.hasNext()){
        v =  Vertex.next();

      }
      if(v!=null){
         vertexid = (long) v.id();

      }

       if(g.V(vertexid).hasNext()){
        logger.info("Current Position >> " + total.get() + " >> vertexid :: " + vertexid + " >> existed.");
         // 将数据中的其他字段添加到Vertex
         for (Object key : content.keySet()) {
           if (key.equals("~id") || key.equals("~label") || key.equals("id") || key.equals("label")) { // 忽略~id & ~label & id & label
             continue;
           }
           g.V(vertexid).property(key.toString(), content.get(key)).next();
          // logger.info("更新——————");
         }

      }else{
         if(tx.isClosed()){
           tx=new JanusGraphConfig().graph.newTransaction();
         }
        //生成新节点
          v = tx.addVertex(org.apache.tinkerpop.gremlin.structure.T.label, label);

         if (null == v) { // 生成新Vertex异常
           logger.info("Current Position >> " + total.get() + " >> tempString :: " + content.toString() + " >> 生成新Vertex异常");
           return;
         }
         // 将数据中的其他字段添加到Vertex
         for (Object key : content.keySet()) {
           if (key.equals("~id") || key.equals("~label") || key.equals("id") || key.equals("label")) { // 忽略~id & ~label & id & label
             continue;
           }
           v.property(key.toString(), content.get(key));
         }


      }
      if (total.get() % 1000== 0) {
        try {
         /* tx.commit();
          tx.traversal();*/
          g.tx().commit();
          g.tx().open();

        } catch (Exception e) {
          e.printStackTrace();
        }finally {
        }
        //  g= janusGraphConfig.graph.traversal();tx =janusGraphConfig.graph.newTransaction();
        System.err.println("Current Position >>---- " + total.get());
      }



    } catch (Exception E){
      E.printStackTrace();
    }


    //logger.info("I'm Main Thread, I'm Over!");
  }

/*
*
   * 判断节点是否存在.
   *
   * @param g
   *          A graph traversal source.
   * @param kvs
   *          keyValues key-value pairs of properties to check the vertex.
   * @return <tt>true</tt> if this GraphTraversalSource contains the specified
   *         vertex.
*/


  public static boolean has(org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource g,
      java.util.HashMap<String, Object> kvs) {
    org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal<Vertex, Vertex> r = null;
    r = g.V();
    for (java.util.Map.Entry<String, Object> kv : kvs.entrySet()) {
      r = r.has(kv.getKey(), kv.getValue());
    }
    return (null == r) ? false : r.hasNext();
  }

  /*
   *yyyy-MM-dd格式转换为时间戳 /
   */
  public static long  TimeToStamp(String times) throws Exception {
    long timess = 0;
    if(!times.equals("")&&times!=null&&times!="null"){
      if(times.length()>19){
        times =times.substring(0,19);
      }
      logger.info("times"+times);
      final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      // String mydate = "2018-09-08 18:09:09";
      final Date datetime = sdf.parse(times);//将你的日期转换为时间戳
       timess =datetime.getTime();
    }
    return timess;


  }


}
