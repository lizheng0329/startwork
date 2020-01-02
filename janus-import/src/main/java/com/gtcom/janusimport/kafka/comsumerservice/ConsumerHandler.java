package com.gtcom.janusimport.kafka.comsumerservice;


import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;

/**
 * @ClassName: ConsumerHandler
 * @Description: 消费者处理器
 * @Author: gh
 * @Date: 2019/12/18
 */


public interface ConsumerHandler {

    void handle(ConsumerRecord record);

    void handles(ConsumerRecords record);

}
