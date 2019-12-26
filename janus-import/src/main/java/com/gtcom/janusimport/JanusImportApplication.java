package com.gtcom.janusimport;

import com.gtcom.janusimport.kafka.worker.PushDataAnalysisConsumerMain;
import com.gtcom.janusimport.until.SpringContextUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class JanusImportApplication {

    public static void main(String[] args) {

        ApplicationContext applicationContext =SpringApplication.run(JanusImportApplication.class, args);
        SpringContextUtil.setApplicationContext(applicationContext);

        /*PushDataAnalysisConsumerMain pushDataAnalysisConsumerMain = new PushDataAnalysisConsumerMain("twitter_tweet_forward",1);
        pushDataAnalysisConsumerMain.startConsumer();*/
        PushDataAnalysisConsumerMain pushDataAnalysisConsumerMain = new PushDataAnalysisConsumerMain("twitter_user_follow",2);
        pushDataAnalysisConsumerMain.startConsumer();
  /*      PushDataAnalysisConsumerMain pushDataAnalysisConsumerMains= new PushDataAnalysisConsumerMain("test_user",4);
        pushDataAnalysisConsumerMains.startConsumer();*/

    }

}
