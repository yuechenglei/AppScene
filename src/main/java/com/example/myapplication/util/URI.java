package com.example.myapplication.util;

/**
 * Created by Halo on 2015/8/31.
 */
public class URI {
    // final static String ip = "211.87.226.141:8080";
    final static String ip = "211.87.229.10:8080";
    private final static String ServerAddr = "http://" + ip + "/start/";
    public static final String LoginAddr = ServerAddr + "app/applogin.jsp";// 用户登录
    public static final String createAddr = ServerAddr + "app/appAddCase.jsp";//添加案件
    public static final String getCase = ServerAddr + "servlet/appGetCase";//获取案件
    public static final String pushPhoto = ServerAddr + "app/appAddphoto.jsp";//添加照片
    //public static final String pushPhoto = ServerAddr + "servlet/appAddphoto";//添加照片
}
