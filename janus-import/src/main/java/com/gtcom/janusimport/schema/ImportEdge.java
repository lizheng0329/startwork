package com.gtcom.janusimport.schema;


import com.gtcom.janusimport.config.JanusGraphConfig;
import com.gtcom.janusimport.until.RowkeyUtil;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.DefaultGraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @ClassName: ImportEdge
 * @Description:
 * @auther GH
 * @date 2019/11/27 9:36
 */


public class ImportEdge implements ImprotTask{

    public void execute(String schema, net.sf.json.JSONObject options, JanusGraphConfig janusGraphConfig , org.janusgraph.core.JanusGraphTransaction tx , GraphTraversalSource g, java.util.concurrent.atomic.AtomicInteger total) {

        net.sf.json.JSONObject content = null;
        try{
            total.addAndGet(1);
            if(options!=null){
                content = options;
            }
            content = net.sf.json.JSONObject.fromObject(options);
            String startUser = content.getString("followScreanName");
            String endUser = content.getString("user_screan_name");
            /**
             * {"name":"edgeName","mapping":"DEFAULT"},
             *       {"name":"edgeType","mapping":"DEFAULT"},
             *       {"name":"edgeContent
             */
            String id = RowkeyUtil.rowkeyByName(startUser+endUser);
            content.put("edgeName","关注");
            content.put("edgeType",1);
            content.put("edgeContent",startUser+schema+endUser);
          //  System.out.println("endUser"+endUser +"  startUser  "+startUser);
            // 获取起始点和结束点
            Vertex startV = null;
            Vertex endV = null;
            // 判断顶点是否存在
           org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal<Vertex, Vertex> starts = g.V().hasLabel("TwitterFollowUsers").has("followScreanName",startUser);
            org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal<Vertex, Vertex> endVs = g.V().hasLabel("TwitterUsers").has("username",endUser);

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
                    Edge e = startV.addEdge(schema, endV);
                    for (Object key : content.keySet()) {

                        e.property(key.toString(), content.get(key));
                    }
                    System.out.println("Current Position >> " + "起始点::" + startV + " >> 结束点::" + endV + " >> 关系::" + schema+"  新增");

                }else{

                    for (Object key : content.keySet()) {
                        g.E(map.get("id")).property(key.toString(), content.get(key)).next();
                    }
                    System.out.println("Current Position >> " + "起始点::" + startV + " >> 结束点::" + endV + " >> 关系::" + schema+"  已存在");
                }
                System.err.println("Current Position >> " + "起始点::" + startV + " >> 结束点::" + endV + " >> 关系::" + schema);
            }

            g.tx().commit();
            g.tx().open();
           // System.exit(1);


        }catch (Exception E){
            E.printStackTrace();

        }



    }


    public static Map checkIsExitEdge(Object outid,Object inid, GraphTraversalSource g) {

       AtomicReference<String> flag = new AtomicReference<>("true");
        List<Object> id = g.V(outid).outE().id().toList();
        Map map= new HashMap();

        System.out.println(id.toString());
        id.forEach(index->{
            DefaultGraphTraversal list6 = (DefaultGraphTraversal) g.E(index);
            if(list6.hasNext()){
                Edge e = (Edge) list6.next();
                if(id.size()>1){
                    g.E(e.id()).drop().iterate();
                    flag.set("true");
                }else {
                    if(e.inVertex().id().equals(inid)){
                        map.put("id",e.id());
                        flag.set("false");
                    }else {
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