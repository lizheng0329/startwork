package com.gtcom.janusimport.schema;


import com.gtcom.janusimport.config.JanusGraphConfig;

/**
 * @ClassName: ImprotTask
 * @Description:
 * @auther GH
 * @date 2019/11/26 17:27
 */


public interface ImprotTask {

    public void execute(String schema, net.sf.json.JSONObject options, JanusGraphConfig janusGraphConfig, org.janusgraph.core.JanusGraphTransaction tx, org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource g, java.util.concurrent.atomic.AtomicInteger total);

}