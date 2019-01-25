package com.gd.icbc.dutydeal.config;

public class constants {
    public static final String USERPREFERENCE = "user_preference";

    public static final String ALIVE_TYPE = "type";//1注册、2认证

    public static final String ID = "id";//用户标识

    public static final String IS_FIRST = "is_first";//是否首次安装运行程序

    public static final String IS_FIRST_REGISTER = "is_first_register";//是否首次进行语音验证

    /**用户操作业务类型为注册*/
    public final static int TYPE_REGISTER = 1;
    /**用户操作业务类型为搜索*/
    public final  static int TYPE_SEARCH = 2;
    /**用户操作业务类型为认证*/
    public final static int TYPE_AUTHENITCATION = 3;
    public static final String CONFIG_FILE="appConfig";
//    public static final String BASE_URL = "http://115.6.157.118:9082";
    public static final String BASE_URL = "http://115.0.14.168:9082";

    public static final String INSERT_IMAGE = BASE_URL + "/servlet/com.icbc.cte.cs.servlet.WithoutSessionReqServlet";
    public static final String PARAM_GET = BASE_URL + "/servlet/com.icbc.cte.cs.servlet.WithoutSessionReqServlet?flowActionName=subop0&action=configuration_flowc.flowc";
    public static final String HISTORY_DUTY = BASE_URL+"/servlet/com.icbc.cte.cs.servlet.WithoutSessionReqServlet?flowActionName=subop0&action=dacsctpdutygetlistmain.flowc";
    public static final String INSERT_IMAGE_flowActionName = "adddacsctpduty";
    public static final String INSERT_IMAGE_action = "dacsctpdutydetailmain.flowc";
    public static final String REQUESTURL=BASE_URL+"/servlet/com.icbc.cte.cs.servlet.WithoutSessionReqServlet?flowActionName=subop0&action=versioncheck.flowc";
    public static final String DOWNLOADURL=BASE_URL+"/file/Android.apk";
}
