package com.gtcom.janusimport.pojo;

import lombok.Data;

import java.util.Date;

/**
 * <p>【描述】：tweet用户信息</p>
 * <p>【作者】: lizheng</p>
 * <p>【日期】: 2019-08-16</p>
 **/
@Data
public class TweetUserVo {
    private Long id;

    private String md5id;

    private String taskId;

    private Date inputTime;

    private String userUrl;

    private String userWebUrl;

    private String username;

    private String rid;

    private String fullname;

    private String userAddr;

    private String bornTime;

    private String registeredTime;

    private String useravatar;

    private String useravatarMd5;

    private String useravatarState;

    private String bkgdurl;

    private String bkgdurlMd5;

    private String bkgdurlState;

    private String userflag;

    private Integer tweets;

    private Integer following;

    private Integer followers;

    private Integer likes;

    private String listed;

    private String moments;

    private String verified;

    private String protecte;

    private String tfEffective;

    private Float everydayTweets;

    private Date updateClrTime;

    private String remarks;

    private String reserved1;

    private String reserved2;

    private String reserved3;

    private String spiderTaskId;

}