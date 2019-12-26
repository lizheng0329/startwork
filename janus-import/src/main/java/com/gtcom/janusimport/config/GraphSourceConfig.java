package com.gtcom.janusimport.config;

import org.apache.tinkerpop.gremlin.driver.Client;
import org.apache.tinkerpop.gremlin.driver.Cluster;
import org.apache.tinkerpop.gremlin.driver.remote.DriverRemoteConnection;
import org.apache.tinkerpop.gremlin.driver.ser.GryoMessageSerializerV3d0;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.io.gryo.GryoMapper;
import org.apache.tinkerpop.gremlin.structure.util.empty.EmptyGraph;
import org.janusgraph.graphdb.tinkerpop.JanusGraphIoRegistry;

import java.net.URLDecoder;

import static org.apache.tinkerpop.gremlin.process.traversal.AnonymousTraversalSource.traversal;

/**
 * @author 李兵
 * @version V1.0
 * @description TODO: 获取图对象配置
 * @date 2019/8/30 20:05
 */



public class GraphSourceConfig {

    /**
     * 基于官网介绍编写1
     * TODO:需要修改 remote-graph.properties 配置文件的gremlin.remote.driver.clusterFile=值为绝对路径
     *
     * @return
     * @throws Exception
     */
    public GraphTraversalSource getGts1() throws Exception {
        Graph graph = EmptyGraph.instance();
        //不这样写获取读不到文件路径
        String path = getClass().getClassLoader().getResource("remote-graph.properties").getPath();
        String decode = URLDecoder.decode(path.substring(1), "utf-8");
        GraphTraversalSource g = graph.traversal().withRemote(decode);
        return g;
    }

    /**
     * 基于官网介绍编写2
     * TODO:需要修改 remote-graph.properties 配置文件的gremlin.remote.driver.clusterFile=值为绝对路径
     *
     * @return
     * @throws Exception
     */
    public GraphTraversalSource getGts2() throws Exception {
        String path1 = Thread.currentThread()
                .getContextClassLoader()
                .getResource("remote-graph.properties").getPath();
        GraphTraversalSource g = traversal().withRemote(URLDecoder.decode(path1.substring(1), "utf-8"));
        return g;
    }


    /**
     * 基于官网介绍编写3
     */
    public GraphTraversalSource getGts3() {
        GraphTraversalSource g = traversal()
                .withRemote(
                        DriverRemoteConnection.using("192.168.12.50", 8182, "g")
                );
        return g;
    }


    /**
     * 池的写法
     *
     * @return
     */
    public Cluster getCluster() {
        GryoMapper.Builder builder = GryoMapper.build().
                addRegistry(JanusGraphIoRegistry.getInstance());
        GryoMessageSerializerV3d0 serializer = new GryoMessageSerializerV3d0(builder);
        //Caused by: io.netty.handler.codec.DecoderException: org.apache.tinkerpop.gremlin.driver.ser.SerializationException: org.apache.tinkerpop.shaded.kryo.KryoException: Encountered unregistered class ID: 65536
        //https://github.com/orientechnologies/orientdb-gremlin/issues/161

        //TODO:配置地址-> http://tinkerpop.apache.org/javadocs/3.4.1/core/org/apache/tinkerpop/gremlin/driver/Cluster.Builder.html
        return Cluster.build()
                .serializer(serializer)
                .maxConnectionPoolSize(20)
                .maxInProcessPerConnection(15)
                .maxWaitForConnection(3000)
                .reconnectInterval(10)
                //可配置多个,是你的janusgraph 的地址
                .addContactPoint("192.168.12.50")
                .port(8182)
                .create();
    }

    public Client getClient() {
        Cluster cluster = getCluster();
        return cluster.connect();
    }

    /**
     * 基于官网介绍编写4
     *
     * @return
     */
    public GraphTraversalSource getGts4(Client client) {
        GraphTraversalSource g = traversal().
                withRemote(DriverRemoteConnection.
                        using(client, "g")
                );
        return g;
    }


    public void close(GraphTraversalSource g, Client client) {
        try {
            g.close();
            client.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
