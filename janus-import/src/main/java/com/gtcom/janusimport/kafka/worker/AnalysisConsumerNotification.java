package com.gtcom.janusimport.kafka.worker;

import com.gtcom.janusimport.kafka.comsumerservice.ConsumerHandler;
import com.gtcom.janusimport.kafka.comsumerservice.ConsumerHandlerImp;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Properties;

/**
 * @ClassName: AnalysisConsumerNotification
 * @Description:
 * @Auther: GH
 * @Date: 2019/5/24 10:49
 */
public class AnalysisConsumerNotification {

    // private static Logger log = LoggerFactory.getLogger(JovebirdInsiderConsumerNotification.class);

    private static Logger logger = LoggerFactory.getLogger(AnalysisConsumerNotification.class);

    private final KafkaConsumer<String, String> consumer;
    private final String topic;

    private int numberOfThreads;//对应开启的线程数

    public AnalysisConsumerNotification(String brokers, String groupId, String topic, int numberOfThreads ) {
        Properties prop = initConfig(brokers, groupId);
        this.consumer = new KafkaConsumer<>(prop);
        this.topic = topic;
        this.numberOfThreads = numberOfThreads;
        this.consumer.subscribe(Arrays.asList(this.topic));
    }

    /**
     * 初始化消费者配置
     * @param brokers
     * @param groupId
     * @return
     */
    private static Properties initConfig(String brokers, String groupId) {
        Properties props = new Properties();
        props.put("bootstrap.servers", brokers);
        props.put("group.id", groupId);
        props.put("enable.auto.commit", "true");
        props.put("auto.commit.interval.ms", "600000");
        props.put("session.timeout.ms", "30000");//根据实际项目情况调整
     //  props.put("auto.offset.reset", "latest");
      props.put("auto.offset.reset", "earliest");
        props.put("max.poll.records", "5");//根据实际项目情况调整
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        return props;
    }


    public void execute() {

        ConsumerHandler consumerHandler = new ConsumerHandlerImp(numberOfThreads);

        if (consumerHandler != null) {
            while (true) {

                ConsumerRecords<String, String> records = consumer.poll(100);
                long startTime = System.currentTimeMillis();
                for (final ConsumerRecord record : records) {
                    consumerHandler.handle(record);

                }
                if(records.isEmpty()){
                    consumerHandler.handles(records);
                    try{
                        logger.info("休眠30秒");
                        Thread.sleep(30*1000);

                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
            }
        }
    }

    public void shutdown() {
        if (consumer != null) {
            consumer.close();
        }
    }
}
