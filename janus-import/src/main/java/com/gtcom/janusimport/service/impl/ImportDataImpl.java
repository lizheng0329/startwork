package com.gtcom.janusimport.service.impl;

import com.gtcom.janusimport.config.JanusGraphConfig;
import com.gtcom.janusimport.schema.ImportVertexNoCheckID;
import com.gtcom.janusimport.service.ImportData;
import com.gtcom.janusimport.until.InfoUnCompleteException;
import net.sf.json.JSONObject;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.DefaultGraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.janusgraph.core.JanusGraphTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;


/**
 * @ClassName: importDataImpl
 * @Description:
 * @auther GH
 * @date 2019/12/18 10:09
 */
public class ImportDataImpl implements ImportData {

    private static Logger logger = LoggerFactory.getLogger(ImportVertexNoCheckID.class);


    @Override
    public void importDataV(JSONObject vlue,JanusGraphTransaction tx, GraphTraversalSource g)throws Exception {

            //初始化数据
            //~id自动生成
                JSONObject content = null;
                //  org.janusgraph.core.JanusGraphVertex v = null;
                Vertex v =null;
                if(vlue!=null){
                    content= vlue;
                }else {
                    throw new InfoUnCompleteException("數據不能為空");

                }
                //logger.info(">>>>>>>"+content.toString());
                String lable=content.getString("lable");//TwitterUser_1TEST111
                String uniqueField=content.getString("uniqueField");

                content=content.getJSONObject("data");

                String username =content.containsKey(uniqueField)?content.getString(uniqueField):null;
                Integer bornTime=content.containsKey("inputTime")?content.getInt("inputTime"):null;
                String usernameS =content.containsKey("userName")?content.getString("userName"):null;
                if(bornTime.equals("")||bornTime==null){
                content.put("inputTime",0);
                }
                content.remove("userName");
                content.put("username",usernameS);
                content.remove("collectCommentsFrequent");
                content.remove("commentsScroll");
                content.put("bornTime",0);
                content.put("likes",0);
            //    content.put("likes",0);
        /*        if(content.containsKey("registeredTime")){
                    String registeredTime = content.getString("registeredTime");
                    if(registeredTime!=null){

                        content.put("registeredTime",TimeToStamp(registeredTime));

                    }
                }*/
        /*        if(content.containsKey("userJson")){
                    String userJson = content.getString("userJson");
                    if(userJson!=null){
                        content.put("userJson",userJson);
                    }
                }*/

                long vertexid = 0;

                org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal<Vertex, Vertex> Vertex = g.V().hasLabel(lable).has(uniqueField,username);
                if(Vertex.hasNext()){
                    v =  Vertex.next();

                }
                if(v!=null){
                    vertexid = (long) v.id();

                }
                if(g.V(vertexid).hasNext()){
                    logger.info(  " >> vertexid :: " + vertexid + " >> existed.");
                    // 将数据中的其他字段添加到Vertex
                    for (Object key : content.keySet()) {
                        if (key.equals("~id") || key.equals("~label") || key.equals("id") || key.equals("label")) { // 忽略~id & ~label & id & label
                            continue;
                        }
                        g.V(vertexid).property(key.toString(), content.get(key));
                        // logger.info("更新——————");
                    }

                }else{
                    if(tx.isClosed()){
                        tx=new JanusGraphConfig().graph.newTransaction();
                    }
                    //生成新节点
                    v = tx.addVertex(org.apache.tinkerpop.gremlin.structure.T.label, lable);


                    if (null == v) { // 生成新Vertex异常
                        logger.info( " >> tempString :: " + content.toString() + " >> 生成新Vertex异常");
                        return;
                    }
                    // 将数据中的其他字段添加到Vertex
                    for (Object key : content.keySet()) {
                        if (key.equals("~id") || key.equals("~label") || key.equals("id") || key.equals("label")) { // 忽略~id & ~label & id & label
                            continue;
                        }
                        v.property(key.toString(), content.get(key));
                    }
                    logger.error( " >> 生成新的:: " + v.id() + " >> 生成新Vertex异常");


                }





    }

    @Override
    public void importDataE(JSONObject object,GraphTraversalSource g) throws Exception {


        JSONObject content = null;
            if(object!=null){
                content = object;
            }else {
                throw new InfoUnCompleteException("數據不能為空");
            }
            String lable=content.getString("edgue");//TwitterUser_1TEST111
 /*           String uniqueField=content.getString("uniqueField");
          //  content=content.getJSONObject("data");

            String username=content.getString(uniqueField);*/
            String startUser = content.getString("startV");
            String endUser = content.getString("endV");
            String startField = content.getString("startField");
            String endField = content.getString("endField");
            String startlable = content.getString("startlable");
            String endlable = content.getString("endlable");
            content = new JSONObject();
            content.put("startV",startUser);
            content.put("endV",endUser);
            /**
             * {"name":"edgeName","mapping":"DEFAULT"},
             *       {"name":"edgeType","mapping":"DEFAULT"},
             *       {"name":"edgeContent
             */
            //  System.out.println("endUser"+endUser +"  startUser  "+startUser);
            // 获取起始点和结束点
            Vertex startV = null;
            Vertex endV = null;
            // 判断顶点是否存在
            org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal<Vertex, Vertex> starts = g.V().hasLabel(startlable).has(startField,startUser);
            org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal<Vertex, Vertex> endVs = g.V().hasLabel(endlable).has(endField,endUser);

            if (starts.hasNext()) {
                startV = starts.next();
            }
            if (endVs.hasNext()) {
                endV = endVs.next();
            }
            System.err.println("--------startV-"+startV+"--------endV-"+endV);
            if (null != startV && null != endV) {
                Map map = checkIsExitEdge(startV.id(),endV.id(),g);
                String flag = (String) ((AtomicReference)map.get("flag")).get();

                if("true".equals(flag)){
                    Edge e = startV.addEdge(lable, endV);
                    for (Object key : content.keySet()) {

                        e.property(key.toString(), content.get(key));
                    }
                    System.out.println("Current Position >> " + "起始点::" + startV + " >> 结束点::" + endV + " >> 关系::" + lable+"  新增");

                }else{

                    for (Object key : content.keySet()) {
                        g.E(map.get("id")).property(key.toString(), content.get(key)).next();
                    }
                    System.out.println("Current Position >> " + "起始点::" + startV + " >> 结束点::" + endV + " >> 关系::" + lable+"  已存在");
                }
                System.err.println("Current Position >> " + "起始点::" + startV + " >> 结束点::" + endV + " >> 关系::" + lable);
            }
        /*    g.tx().commit();
            g.tx().open();*/

    }

        public static Map checkIsExitEdge(Object outid,Object inid, GraphTraversalSource g) {

            AtomicReference<String> flag = new AtomicReference<>("true");
            List<Object> id = g.V(outid).outE().id().toList();
            Map map = new HashMap();

            System.out.println(id.toString());
            id.forEach(index -> {
                DefaultGraphTraversal list6 = (DefaultGraphTraversal) g.E(index);
                if (list6.hasNext()) {
                    Edge e = (Edge) list6.next();
                    if (id.size() > 1) {
                        g.E(e.id()).drop().iterate();
                        flag.set("true");
                    } else {
                        if (e.inVertex().id().equals(inid)) {
                            map.put("id", e.id());
                            flag.set("false");
                        } else {
                            g.E(e.id()).drop().iterate();
                            flag.set("true");
                        }

                    }

                }
            });
            map.put("flag",flag);
            System.out.println(map.toString()+">>>>>>>>>>>>>>>>"+flag.get()+"~~~~~~"+map.get("flag")+"-----------"+map.get("id"));

            return map;

        }

}