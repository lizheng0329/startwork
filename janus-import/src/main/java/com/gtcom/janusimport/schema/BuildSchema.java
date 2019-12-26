package com.gtcom.janusimport.schema;


import com.google.gson.GsonBuilder;
import com.gtcom.janusimport.config.ImportConfiguration;
import com.gtcom.janusimport.config.JanusGraphConfig;
import com.gtcom.janusimport.schema.entity.IndexKey;
import com.gtcom.janusimport.schema.entity.IndexPropertyKey;
import com.gtcom.janusimport.schema.entity.Schema;
import com.gtcom.janusimport.schema.enuminfo.Mapping;
import org.janusgraph.core.EdgeLabel;
import org.janusgraph.core.Multiplicity;
import org.janusgraph.core.PropertyKey;
import org.janusgraph.core.VertexLabel;
import org.janusgraph.core.schema.ConsistencyModifier;
import org.janusgraph.core.schema.EdgeLabelMaker;
import org.janusgraph.core.schema.JanusGraphIndex;
import org.janusgraph.core.schema.JanusGraphManagement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.util.List;

/**
 * 创建Schema. // 初版,后续完善.
 * An external index cannot be unique.
 * @author Shengjun Liu
 * @version 2018-07-25
 *
 */

@Service("BuildSchema")
public class BuildSchema implements Task{

  private static Logger logger = LoggerFactory.getLogger(BuildSchema.class);

  public void execute( String VtName, String EgName, String indexName, String edgeNme) {
      JanusGraphConfig janusGraphConfig = new JanusGraphConfig();
       ImportConfiguration  importConfiguration = new ImportConfiguration();
       String  options=importConfiguration.janusIndexPath;
        JanusGraphManagement mgmt = janusGraphConfig.graph.openManagement(); // 获取管理入口
    try {

      Schema schema = new GsonBuilder().disableHtmlEscaping().create().fromJson(new FileReader(String.class.cast(options)),
          Schema.class);
      // 初始化属性
       logger.info("开始初始化属性信息...");
       schema.getProps().forEach((p) -> {
        logger.info("开始..."+p.toString());
        // 判断属性是否存在
        logger.info("       >> " + p.getName() + " :: " + p.getType().getClazz());
        if (mgmt.containsPropertyKey(p.getName())) {
          PropertyKey hisPropertyKey = mgmt.getPropertyKey(p.getName());
          logger.info("已存在  >> " + hisPropertyKey.name() + " :: " + hisPropertyKey.dataType());
          logger.info("       >> " + p.getName() + " :: " + p.getType().getClazz());
        } else {
          mgmt.makePropertyKey(p.getName()).dataType(p.getType().getClazz()).make();
          logger.info("已添加  >> " + p.getName() + " :: " + p.getType().getClazz());
        }
      });
      logger.info("结束初始化属性信息...");


      if(VtName!=null){
          // 初始化顶点类型
          logger.info("开始初始化顶点类型信息...");
          schema.getVertices().forEach((v) -> {
              // 判断顶点类型是否存在
              logger.info(">>>>>动态生成顶点名称>>>>>>"+VtName);
              v.setName(VtName);
              if (mgmt.containsVertexLabel(v.getName())) {
                  VertexLabel hisVertexLabel = mgmt.getVertexLabel(v.getName());
                  logger.info("已存在  >> " + hisVertexLabel.name());
                  logger.info("       >> " + v.getName());
              } else {
                  mgmt.makeVertexLabel(v.getName()).make();
                  logger.info("已添加  >> " + v.getName());
              }
          });
          logger.info("结束初始化顶点类型信息...");
      }

      if(edgeNme!=null){
          // 初始化关系类型
          logger.info("开始初始化关系类型信息...");
          schema.getEdges().forEach((e) -> {
              // 判断关系类型是否存在
              logger.info(">>>>>动态生成边名称>>>>>>"+EgName);
              e.setName(EgName);
              if (mgmt.containsEdgeLabel(e.getName())) {
                  EdgeLabel hisEdgeLabel = mgmt.getEdgeLabel(e.getName());
                  logger.info("已存在  >> " + hisEdgeLabel.name() + " :: " + hisEdgeLabel.multiplicity());
                  logger.info("       >> " + e.getName() + " :: " + e.getMultiplicity());
              } else {
                  EdgeLabelMaker edgeLabelMaker = mgmt.makeEdgeLabel(e.getName());
                  edgeLabelMaker.multiplicity((null == e.getMultiplicity()) ? Multiplicity.MULTI : e.getMultiplicity());
                  PropertyKey propertyKey = ((null == e.getSignature() || "".equals(e.getSignature().trim())) ? null
                          : mgmt.getPropertyKey(e.getSignature()));
                  if (null != propertyKey) edgeLabelMaker.signature(propertyKey);
                  edgeLabelMaker.make();
                  logger.info("已添加  >> " + e.getName());
              }
          });
          logger.info("结束初始化关系类型信息...");

          // 初始化索引类型
          logger.info("开始初始化索引类型信息...");
      }

      if(indexName!=null||edgeNme!=null){
          logger.info(">>>>>动态生成索引名称>>>>>>"+indexName);
          List<IndexKey> List =schema.getIndexes();
          IndexKey i = null;
          if(indexName!=null){
              i =schema.getIndexes().get(0);
              i.setName(indexName);
          }

          if(edgeNme!=null){
              i =schema.getIndexes().get(1);
              i.setName(edgeNme);
          }

              // 判断关系类型是否存在

              if (mgmt.containsGraphIndex(i.getName())) {
                  JanusGraphIndex hisGraphIndex = mgmt.getGraphIndex(i.getName());
                  logger.info("已存在  >> " + hisGraphIndex.name() + " :: isUnique=" + hisGraphIndex.isUnique() + " :: isCompositeIndex="
                          + hisGraphIndex.isCompositeIndex() + " :: isMixedIndex=" + hisGraphIndex.isMixedIndex());
                  logger.info("       >> " + i.getName() + " :: isUnique=" + i.isUniqueIndex() + " :: isCompositeIndex="
                          + i.isCompositeIndex() + " :: isMixedIndex=" + i.isMixedIndex());
              } else {
                  JanusGraphManagement.IndexBuilder index = null;
                  if (null == i.getType()) {
                      logger.info("未指定索引类型  >> " + i.getName() + " >> " + " 跳过本次索引内容创建. ");
                      return;
                  } else {
                      index = mgmt.buildIndex(i.getName(), i.getType().getClazz());
                  }
                  // 处理索引字段
                  for (IndexPropertyKey p : i.getProps()) {
                      if (mgmt.containsPropertyKey(p.getName())) {
                          PropertyKey key = mgmt.getPropertyKey(p.getName());
                          if (null == p.getMapping() || Mapping.NULL == p.getMapping()) {
                              index.addKey(key);
                          } else {
                              index.addKey(key, p.getMapping().getMapping().asParameter());
                          }
                      } else {
                          logger.info("未发现属性类型  >> " + p.getName() + " >> 索引" + i.getName() + "忽略此属性.");
                      }
                  }
                  // 创建唯一性索引
                  if (i.isUniqueIndex()) {
                      index.unique();
                  }
                  // 创建复合索引
                  if (i.isCompositeIndex()) {
                      mgmt.setConsistency(index.buildCompositeIndex(),
                              null == i.getConsistencyModifier() ? ConsistencyModifier.LOCK : i.getConsistencyModifier());
                  }
                  // 创建混合索引
                  if (i.isMixedIndex()) {
                      if (null == i.getMixedIndexName() || "".equals(i.getMixedIndexName().trim())) {
                          logger.info("未发现后端索引方式  >> " + i.getName() + " :: isMixedIndex=" + i.isMixedIndex() + " :: MixedIndexName="
                                  + i.getMixedIndexName() + " >> 跳过本次索引内容创建. ");
                      } else {
                          index.buildMixedIndex(i.getMixedIndexName());
                      }
                  } else {
                      logger.info("未发现索引方式  >> " + i.getName() + " :: isUnique=" + i.isUniqueIndex() + " :: isCompositeIndex="
                              + i.isCompositeIndex() + " :: isMixedIndex=" + i.isMixedIndex() + " >> 跳过本次索引内容创建. ");
                  }
                  logger.info("已添加  >> " + i.getName());
              }


      }
        logger.info("结束初始化索引类型信息...");
        mgmt.commit();


        logger.info("\n\n\\^o^/字段创建完成\\^o^/\n\n");

    } catch (Exception e) {
      e.printStackTrace();
    } finally {
    //  janusGraphConfig.close();
    //  System.exit(1);
    }

  }
}
