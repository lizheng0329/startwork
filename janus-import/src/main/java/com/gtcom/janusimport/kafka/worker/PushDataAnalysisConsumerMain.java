package com.gtcom.janusimport.kafka.worker;

import com.gtcom.janusimport.config.ImportConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ClassName: PushDataAnalysisConsumerMain
 * @Description:
 * @Auther: GH
 * @Date: 2019/3/28 14:46
 */
public class PushDataAnalysisConsumerMain {

    private static Logger logger = LoggerFactory.getLogger(PushDataAnalysisConsumerMain.class);
    // private static Logger log = LoggerFactory.getLogger(InsiderIncreaseConsumerMain.class);
    private final String brokers = ImportConfiguration.kafkaUrl;
    private String groupId;
    private String topic ;
    private int number;
    public PushDataAnalysisConsumerMain() {
    }
    public PushDataAnalysisConsumerMain(String area,String groupId,int number ) {

        this.topic = area;
        this.groupId = groupId;
        this.number = number;
    }

    /**
     * 启动消费
     */
    public void startConsumer() {
        logger.info("启动janus的消费者 - - - - - - > > > > > > ；TopicName为"+topic+"；开启的当前线程数："+number);
        logger.info("kafka 消费服务地址："+ brokers);
        String brokers = this.brokers;
        String groupId = this.groupId;
        String topic = this.topic;

        AnalysisConsumerNotification consumers = new AnalysisConsumerNotification(brokers, groupId, topic,number);
        consumers.execute();

        try {
            Thread.sleep(100000);
        } catch (InterruptedException ie) {

        }
        consumers.shutdown();
    }
/*

    public static void main(String[] args) {
        */
/*String brokers = "localhost:9092";
        *//*
*/
/*String groupId = "NewSpecialNoticeId";
        String topic = "NewSpecialNotice";*//*
*/
/*
        String groupId = "storm-consumer";
        String topic = "storm-data";

        ConsumerNotification consumers = new ConsumerNotification(brokers, groupId, topic);
        consumers.execute();

        try {
            Thread.sleep(100000);
        } catch (InterruptedException ie) {

        }
        consumers.shutdown();*//*

    }
*/

}
