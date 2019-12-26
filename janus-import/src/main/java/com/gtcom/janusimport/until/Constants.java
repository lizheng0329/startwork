package com.gtcom.janusimport.until;

/**
 * @ClassName: Constants
 * @Description:
 * @Author: gh
 * @Date: 2017/6/23 上午10:14
 */
public class Constants {

    /**
     * 消费者类型
     * 新建，增量
     */
    public enum ConsumerType {
        NEW, INCREASE, DEMO,insider,jovebird,ANALYSIS,ANALYSIS_INCREASE
    }

    /**
     * 数据推送队列
     * 新闻新建，新闻增量，社交新建，社交增量,社交推送
     */
    public enum TopicDataType {
        NEWS, NEWS_INCREASE, SOCIAL, SOCIAL_INCREASE, DEMO
    }
}
