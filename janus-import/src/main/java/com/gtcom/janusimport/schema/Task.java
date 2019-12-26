package com.gtcom.janusimport.schema;

/**
 * 任务执行方法.
 * @author Shengjun Liu
 * @version 2018-07-27
 *
 */
public interface Task {
  public void execute(String VtName, String EgName, String indexName, String edgeNme);


}
