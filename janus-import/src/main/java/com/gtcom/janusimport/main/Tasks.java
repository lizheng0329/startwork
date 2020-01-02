package com.gtcom.janusimport.main;

import com.gtcom.janusimport.config.JanusGraphConfig;

/**
 * 任务执行方法.
 * @author Shengjun Liu
 * @version 2018-07-27
 *
 */
public interface Tasks {
  public void execute(String options, JanusGraphConfig janusGraphConfig);


}
