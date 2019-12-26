//package com.gtcom.janusimport.schema;
//
//
//import com.gtcom.janusimport.config.JanusGraphConfig;
//import com.gtcom.janusimport.config.JdbcUtil;
//import org.json.simple.JSONObject;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.ResultSetMetaData;
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * @ClassName: MysqlSelect
// * @Description:
// * @auther GH
// * @date 2019/11/25 17:43
// */
//
//
//public class MysqlSelect {
//
//
//    private static Logger logger = LoggerFactory.getLogger(MysqlSelect.class);
//
//    public static  void addDataVer(org.janusgraph.core.JanusGraphTransaction tx, org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource g, java.util.concurrent.atomic.AtomicInteger total, JanusGraphConfig janusGraphConfig) {
//        PreparedStatement pstmt = null;
//        ResultSet result = null;
//        Connection db = null;
//        try {
//            JdbcUtil jdbcUtil = new JdbcUtil();
//            db= jdbcUtil.getInstanceMysql();
//            //TwitterUser 基本信息
//        //    String sql = "SELECT * FROM analysis_data_blog_users_bak  WHERE  id >1000";
//           //TwitterFollowUser
//            String sql = "SELECT follow_screan_name,follow_user_name,follow_useravatar,follow_user_id,follow_useravatar,follow_useravatar_md5id,follow_useravatar_state FROM analysis_data_follow ";
//            //TwitterUser 关系表
//          //  String sql = "SELECT user_screan_name,follow_screan_name FROM analysis_data_follow ";
//            pstmt = db.prepareStatement(sql);
//            // 关闭自动提交事务
//            Long startTime = System.currentTimeMillis();
//            result = pstmt.executeQuery();
//            while(result.next()) {
//                ResultSetMetaData rsmd = result.getMetaData();
//                Map<String,String> map = new HashMap<>();
//                for(int i = 1; i <= rsmd.getColumnCount(); i++) {
//       /*             {"name":"followScreanName","type":"String"},
//                    {"name":"followUserName","type":"String"},
//                    {"name":"followUserId","type":"String"},
//                    {"name":"followUseravatar","type":"String"},
//                    {"name":"followUseravatarMd5id","type":"String"},
//                    {"name":"followUseravatarState","type":"String"},*/
//                    if((rsmd.getColumnLabel(i)).equals("follow_screan_name")){
//                        map.put("followScreanName", result.getString(i));
//                    }else
//                    if((rsmd.getColumnLabel(i)).equals("follow_user_name")){
//                        map.put("followUserName", result.getString(i));
//                    }else
//                    if((rsmd.getColumnLabel(i)).equals("follow_user_id")){
//                        map.put("followUserId", result.getString(i));
//                    }else
//                    if((rsmd.getColumnLabel(i)).equals("follow_useravatar_md5id")){
//                        map.put("followUseravatarMd5id", result.getString(i));
//                    }else
//                    if((rsmd.getColumnLabel(i)).equals("follow_useravatar_state")){
//                        map.put("followUseravatarState", result.getString(i));
//                    }else
//                    if((rsmd.getColumnLabel(i)).equals("follow_useravatar")){
//                        map.put("followUseravatar", result.getString(i));
//                    }else {
//                        map.put(rsmd.getColumnLabel(i), result.getString(i));
//                    }
//
//
//                }
//                String optionsS =JSONObject.toJSONString(map);
//                net.sf.json.JSONObject  options=net.sf.json.JSONObject.fromObject(optionsS);
//               logger.info("->>>>>"+ options);
//              //   System.exit(1);
//                //TwitterFollowUser
//               new ImportVertexNoCheckID().execute("TwitterFollowUsers",options,janusGraphConfig, tx, g,total);
//                //TwitterUser
//           //    new ImportVertexNoCheckID().execute("TwitterUsers",options,janusGraphConfig, tx, g,total);
//              //  System.exit(1);
//
//               //break;
//
//
//            }
//          /*  tx.commit();
//            tx.traversal();*/
//            g.tx().commit();
//            g.tx().open();
//
//            Long endTime = System.currentTimeMillis();
//           logger.info("批量导入数据，用时：" + (endTime - startTime) / 1000 + "秒");
//
//        } catch (Exception e) {
//            e.printStackTrace();
//           logger.info("---0"+e.getMessage());
//        }
//    }
//
//    /**
//     *  { "name": "belong"},{ "name": "posting"},{ "name": "comment"}
//     *  新增参数    {"name":"edgeName","type":"关注"},
//     *          belong {"edgeType":"1"},
//     *            {"name":"edgeContent","type":"String"}
//     * @param tx
//     * @param g
//     * @param total
//     * @param janusGraphConfig
//     */
//
//
//    public static  void addDataEdge(org.janusgraph.core.JanusGraphTransaction tx, org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource g, java.util.concurrent.atomic.AtomicInteger total, JanusGraphConfig janusGraphConfig) {
//        PreparedStatement pstmt = null;
//        ResultSet result = null;
//        Connection db = null;
//        /**
//         *   {"name":"edgeName","type":"String"},
//         *   {"name":"edgeType","type":"Integer"},
//         *   {"name":"edgeContent","type":"String"}
//         */
//        //关注度默认参数 edgeType 1
//        String defaults ="{\"edgeName\":\"edgeName\",\"edgeContent\":\"edgeContent\",\"edgeType\":1}";
//
//        net.sf.json.JSONObject JSON = net.sf.json.JSONObject.fromObject(defaults);
//        try {
//            JdbcUtil jdbcUtil = new JdbcUtil();
//            db= jdbcUtil.getInstanceMysql();
//
//            //TwitterUser 关系表
//            String sql = "SELECT user_screan_name,follow_screan_name FROM analysis_data_follow ";
//            pstmt = db.prepareStatement(sql);
//            // 关闭自动提交事务
//            Long startTime = System.currentTimeMillis();
//            result = pstmt.executeQuery();
//            while(result.next()) {
//                ResultSetMetaData rsmd = result.getMetaData();
//                Map<String,String> map = new HashMap<>();
//                for(int i = 1; i <= rsmd.getColumnCount(); i++) {
//       /*             {"name":"followScreanName","type":"String"},
//                    {"name":"followUserName","type":"String"},
//                    {"name":"followUserId","type":"String"},
//                    {"name":"followUseravatar","type":"String"},
//                    {"name":"followUseravatarMd5id","type":"String"},
//                    {"name":"followUseravatarState","type":"String"},*/
//                    if((rsmd.getColumnLabel(i)).equals("follow_screan_name")){
//                        map.put("followScreanName", result.getString(i));
//                    }else
//                    if((rsmd.getColumnLabel(i)).equals("follow_user_name")){
//                        map.put("followUserName", result.getString(i));
//                    }else
//                    if((rsmd.getColumnLabel(i)).equals("follow_user_id")){
//                        map.put("followUserId", result.getString(i));
//                    }else
//                    if((rsmd.getColumnLabel(i)).equals("follow_useravatar_md5id")){
//                        map.put("followUseravatarMd5id", result.getString(i));
//                    }else
//                    if((rsmd.getColumnLabel(i)).equals("follow_useravatar_state")){
//                        map.put("followUseravatarState", result.getString(i));
//                    }else
//                    if((rsmd.getColumnLabel(i)).equals("follow_useravatar")){
//                        map.put("followUseravatar", result.getString(i));
//                    }else {
//                        map.put(rsmd.getColumnLabel(i), result.getString(i));
//                    }
//
//
//                }
//                String optionsS =JSONObject.toJSONString(map);
//                net.sf.json.JSONObject  options=net.sf.json.JSONObject.fromObject(optionsS);
//                options.putAll(JSON);
//               logger.info("->>>>>"+ options);
//                //   System.exit(1);
//                //     new ImportVertexNoCheckID().execute("TwitterFollowUsers",options,janusGraphConfig, tx, g,total);
//                //  System.exit(1);
//                new ImportEdge().execute("belong",options,janusGraphConfig, tx, g,total);
//                //break;
//                if (total.get() % 1000 == 0) {
//                    try {
//                        g.tx().commit();
//                        g.tx().open();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    System.err.println("Current Position >> " + total.get());
//                }
//
//            }
//            g.tx().commit();
//            g.tx().open();
//            Long endTime = System.currentTimeMillis();
//           logger.info("批量导入数据，用时：" + (endTime - startTime) / 1000 + "秒");
//
//        } catch (Exception e) {
//            e.printStackTrace();
//           logger.info("---0"+e.getMessage());
//        }
//    }
//
//
//
//    public static void main(String[] args) {
//
//       // MysqlSelect.addDataTest();
//    }
//
//}