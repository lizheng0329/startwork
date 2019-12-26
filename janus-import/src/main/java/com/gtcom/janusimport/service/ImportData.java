package com.gtcom.janusimport.service;

import net.sf.json.JSONObject;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.janusgraph.core.JanusGraphTransaction;

public interface ImportData {

    void importDataV(JSONObject list, JanusGraphTransaction tx, GraphTraversalSource g)throws Exception;

    void importDataE(JSONObject object,GraphTraversalSource g )throws Exception;

}
