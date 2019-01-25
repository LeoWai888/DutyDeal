package com.gd.icbc.dutydeal.presenter;


import android.icu.util.LocaleData;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.ToastUtils;
import com.gd.icbc.dutydeal.contract.HomeContract;
import com.gd.icbc.dutydeal.json.DutyingPeople;
import com.gd.icbc.dutydeal.json.ParamObj;
import com.gd.icbc.dutydeal.json.SignPeople;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import com.gd.icbc.dutydeal.config.constants;



public class HomePresenter implements HomeContract.Presenter {
    private HomeContract.View mView;


    @Override
    public void start() {

    }


    public HomePresenter(HomeContract.View view) {
        this.mView = view;
        view.setPresenter(this);
    }

    /*
    获取系统值班参数，获取不成功系统提示报错后退出
     */
    public void loadParam() {
        //转圈开始
        mView.showLoding();

        String url = constants.PARAM_GET;
        OkHttpClient okHttpClient = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(url)
                .get()//默认就是GET请求，可以不写
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
//                转圈结束
                mView.hodeLoding();
                Log.d("BACS", "onFailure: 获取系统值班参数失败！");
                //弹出错误提示窗口
                mView.showInitError("获取系统值班参数失败！");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseStr = response.body().string();
                Log.d("BACS", "onResponse: " + responseStr);
                //转圈结束
                mView.hodeLoding();
                //view端根据获取的参数显示相关view
                ParamObj paramObj = JSON.parseObject(responseStr, ParamObj.class);
                mView.initParams(paramObj);
            }
        });
    }

    /*
   在值班期间程序出现异常重启时，加载已刷脸打卡的人员数据
    */
    public void loadDutyingPeoples(String areaNo) {
        //转圈开始
        mView.showLoding();

        String url = constants.HISTORY_DUTY + "&areano=" + areaNo;
        OkHttpClient okHttpClient = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(url)
                .get()//默认就是GET请求，可以不写
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
//                转圈结束
                mView.hodeLoding();
                Log.d("BACS", "onFailure: 获取地区初始化值班人员失败！");
                //弹出错误提示窗口
                mView.showError("获取地区初始化值班人员失败！ ");
//                ToastUtils.showLong("获取地区初始化值班人员失败！ ");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseStr = response.body().string();
                Log.d("BACS", "onResponse: " + responseStr);
                //转圈结束
                mView.hodeLoding();
                //view端显示值班人员信息

                try {
                    DutyingPeople dutyingPeople = JSON.parseObject(responseStr, DutyingPeople.class);
                    Log.e("人员列表：", String.valueOf(dutyingPeople.getResData().isEmpty()));
                    mView.showDutyingPeople(dutyingPeople.getResData());
                } catch(Exception e){
                    Log.d("BACS", responseStr);
                    //当读取列表为空时什么事都不做
                }
            }
        });
    }

    /*
值班打卡，发送后端记录打开数据，同时反馈前端谁打卡、照片、和打卡时服务器记录的时间
 */
    public void loadSignPeople(String faceBase64,String areaNo) {
        //转圈开始
        mView.showLoding();
        String url = constants.INSERT_IMAGE;
        OkHttpClient okHttpClient = new OkHttpClient();
        FormBody.Builder formBody = new FormBody.Builder();//创建表单请求体
        formBody.add("photo", faceBase64);//传递抓拍照片BASE64
        formBody.add("flowActionName", constants.INSERT_IMAGE_flowActionName);//传递键值对参数flowActionName
        formBody.add("action", constants.INSERT_IMAGE_action);//传递键值对参数action
        formBody.add("AREANO", areaNo); //传入设备设置的地区号

        Request request = new Request.Builder()//创建Request 对象。
                .url(url)
                .post(formBody.build())//传递请求体
                .build();

        //创建/Call
        Call call = okHttpClient.newCall(request);
        //加入队列 异步操作
        call.enqueue(new Callback() {
            //请求错误回调方法
            @Override
            public void onFailure(Call call, IOException e) {
                // 转圈结束
                mView.hodeLoding();
                Log.d("BACS", "onFailure: 考勤打卡失败！");
                //弹出错误提示窗口，数据传输
                mView.showError("考勤打卡失败，请重试！ ");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.code()==200) {
                    // 转圈结束
                    mView.hodeLoding();
                    String responseStr = response.body().string();
                    Log.d("BACS", "onResponse: " + responseStr);
                    try{
                        SignPeople signPeople = JSON.parseObject(responseStr, SignPeople.class);
                        mView.showSignPeople(signPeople);
                    }catch(Exception e){
                        Log.d("BACS", responseStr);
                        mView.showError("数据传输失败！");
                        //当读取列表为空时什么事都不做
                    }

                }else {
                    // 转圈结束
                    mView.hodeLoding();
                    Log.d("BACS", "onFailure: 考勤打卡失败！");
                    //弹出错误提示窗口
                    mView.showError("考勤打卡失败，请重试 ");
                }
            }
        });
    }
}

