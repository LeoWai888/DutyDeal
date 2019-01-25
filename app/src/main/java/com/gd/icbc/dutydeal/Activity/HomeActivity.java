package com.gd.icbc.dutydeal.Activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.support.v7.widget.SnapHelper;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.allenliu.versionchecklib.v2.AllenVersionChecker;
import com.allenliu.versionchecklib.v2.builder.DownloadBuilder;
import com.allenliu.versionchecklib.v2.builder.UIData;
import com.allenliu.versionchecklib.v2.callback.RequestVersionListener;
import com.detect.ui.AliveActivity;
import com.gd.icbc.dutydeal.R;
import com.gd.icbc.dutydeal.base.BaseActivity;
import com.gd.icbc.dutydeal.config.Settings;
import com.gd.icbc.dutydeal.config.constants;
import com.gd.icbc.dutydeal.contract.HomeContract;
import com.gd.icbc.dutydeal.json.DutyingPeople;
import com.gd.icbc.dutydeal.json.ParamObj;
import com.gd.icbc.dutydeal.json.SignPeople;
import com.gd.icbc.dutydeal.json.TimeDownBean;
import com.gd.icbc.dutydeal.json.VersionJson;
import com.gd.icbc.dutydeal.presenter.HomePresenter;
import com.gd.icbc.dutydeal.utils.AppUtils;
import com.gd.icbc.dutydeal.utils.CountDownAdapter;
import com.gd.icbc.dutydeal.utils.ImageHelper;
import com.gd.icbc.dutydeal.utils.LedUtils;
import com.gd.icbc.dutydeal.utils.Mp3Play;
import com.gd.icbc.dutydeal.utils.Preferences;
import com.gd.icbc.dutydeal.utils.TimeUtils;
import com.gd.icbc.dutydeal.utils.Voice;
import com.gd.icbc.dutydeal.utils.CountDownAdapter.SpacesItemDecoration;
import org.angmarch.views.NiceSpinner;
import com.suke.widget.SwitchButton;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import dmax.dialog.SpotsDialog;

public class HomeActivity extends BaseActivity implements View.OnClickListener, HomeContract.View {

    @BindView(R.id.systemTime)
    TextView systemTime;
    @BindView(R.id.systemTime_Rl)
    RelativeLayout systemTimeRl;

    @BindView(R.id.recycle_list_view)
    RecyclerView recycleListView;
    @BindView(R.id.Tocamera)
    ImageView Tocamera;
    @BindView(R.id.signInfo_ly)
    RelativeLayout signInfoLy;
    @BindView(R.id.before_sign_tv)
    TextView beforeSignTv;
    @BindView(R.id.before_sign_time_tv)
    TextView beforeSignTimeTv;
    @BindView(R.id.before_sign_rl)
    RelativeLayout beforeSignRl;

    @BindView(R.id.show_area_tx)
    TextView showArea;

    @BindView(R.id.setting_entrance)
    Button settingEntrance;
    @BindView(R.id.setting_page_detail)
    RelativeLayout settingPageDetail;
    @BindView(R.id.main_radiogroup)
    RadioGroup mRadioGroup;
    @BindView(R.id.nice_spinner_detail_Rl)
    RelativeLayout mNiceSprinnerDetailRl;
    @BindView(R.id.setting_updateVersion_btn)
    Button mUpdateVersionBtn;
    @BindView(R.id.getConfiguration)
    Button getconfiguration;
    @BindView(R.id.setting_camera_rl)
    RelativeLayout mSettingCameraRl;
    @BindView(R.id.setting_leave_btn)
    Button settingLeaveBtn;
    @BindView(R.id.switch_button)
    SwitchButton mSettingCameraSwitch;

    private TextToSpeech tts;
    private AlertDialog mDialog;//等待对话框
    private HomeContract.Presenter mPresenter;
    public String unDutyBeginTime="2018-12-21 00:00:00";//非值班开始时间
    public String unUnDutyEndTime="2018-12-21 01:00:00";//非值班结束时间
    public String sysTimeStr;//当前系统时间
    public int dutyingInterval;//每次刷脸间隔
    public int firstDutyCountdown;//首次刷脸的倒计时
    private List<TimeDownBean> mTimeDownBeanList=new ArrayList<>();
    private CountDownAdapter mCountDownAdapter;
    private boolean isPermissionAccessed = true;
    private static final int PERMISSION_REQUEST_CODE = 0;  //申请权限请求码
    private int type;// 用户操作的业务类型
    private byte[] image;

    private Voice mvoice;
    private Mp3Play mMp3Play;
    private boolean playNoneMp3;
    private boolean playOneMp3;
    private boolean playTwoMp3;
    private boolean inCamera;
    Properties prop = null;

    private String FREE_TIME;
    private String RING_TIME;
    private String ON_TIME="00:00:00";
    private String OFF_TIME="00:00:00";
    private String AREANO="";
    Preferences prefer;//自定义的类
    SharedPreferences preference;
    private DownloadBuilder builder;   //版本控制
    private Boolean ledState=true;   //闪光灯开关状态
    private int settingAreaPosition=0;

    /*
    1、在非值班時間顯示系統時間；
     */
    class TimeThread extends Thread {
        @Override
        public void run() {
            do {
                try {
                    sysTimeStr = TimeUtils.getCurrentTimeString();
                    if (inCamera) {
                    } else if ((sysTimeStr.compareTo(unDutyBeginTime)>=0) && (sysTimeStr.compareTo(unUnDutyEndTime) < 0)) {
//                        onRemoveMsgs(R.id.wait_state);
                        onSendMsgs(R.id.unDuty_state);// 每隔1秒发送一个msg给mHandler
                    }
                    else if (mTimeDownBeanList.size() == 0 ) {
//                        onRemoveMsgs(R.id.wait_state);
                        onSendMsgs(R.id.NoneDuty_state);
                    } else if (mTimeDownBeanList.size() == 1) {
//                        onRemoveMsgs(R.id.wait_state);
                        onSendMsgs(R.id.FristDuty_state);
                    } else {
                        onSendMsgs(R.id.SecondDuty_state);
                    }
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } while (true);
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        mvoice = new Voice(this);
        mMp3Play = new Mp3Play(this);
        mSettingCameraSwitch.toggle(true);//设置拍照闪光灯开关动画

        initEvents();
        initViews();
        checkVersion();
        mPresenter = new HomePresenter(this);
        mPresenter.loadParam();

        String areaNo="";
        if (getParam("AREA_NO").length()>5) {
            areaNo = getParam("AREA_NO").substring(0, 5);
        }
        AREANO = areaNo;

        if ((areaNo=="") || (areaNo==null)) {
//            弹出配置框,修改配置文件
            onSendMsgs(R.id.config_state);
        }else {
            showArea.setText("-- "+getParam("AREA_NO").substring(7)+" --");
        }

    }


    public String getParam(String key){
        preference=getSharedPreferences("shared", MODE_PRIVATE);
        Map<String, String> map=new HashMap<String, String>();
        map.put(key,preference.getString("AREA_NO",""));
        return map.get(key);
    }

    public void saveParam(String areaNo) {

        prefer=new Preferences(this);
        prefer.save(areaNo);
        Toast.makeText(getApplicationContext(), "修改成功", Toast.LENGTH_SHORT).show();

    }

    /*
    1、读取初始化数据包括:1、参数（开始时间、结束时间、考勤间隔时间）
    2、读取正在值班人员的最新耍脸考勤数据
    */
    private void loadHomeData(String areaNo) {
        /*
        1.初始化系统设置，这些设置应该从服务器获取
        包括
        值班开始时间（unUnDutyEndTime）
        值班结束时间（unDutyBeginTime）
        刷脸时间间隔（dutyingInterval）
        值班开始后首次刷脸的缓冲时间 （firstDutyCountdown）
        */

        dutyingInterval = Integer.parseInt(String.valueOf(FREE_TIME)) * 60;
        firstDutyCountdown = Integer.parseInt(String.valueOf(FREE_TIME)) * 60;
        /*
        2.初始化上次刷脸情况数据
        上次刷脸情况
        读取正在值班人员的最新刷脸考勤数据
        每个考勤人显示信息（照片、上一次刷脸考勤时间、距离上一次刷脸考勤的间隔时间
        (假设考勤间隔为1分钟60秒)显示=60-（当前时间-上次刷脸时间）,当它为负值时，显示绝对值，同时变红色报警状态
        */
        mPresenter.loadDutyingPeoples(areaNo);
            /*
            //读取正在值班人员的最新耍脸考勤数据
            每个考勤人显示信息（照片、上一次刷脸考勤时间、距离上一次刷脸考勤的间隔时间
            (假设考勤间隔为1分钟60秒)显示=60-（当前时间-上次刷脸时间）,当它为负值时，显示绝对值，同时变红色报警状态
            */

        playNoneMp3 = false;
        playOneMp3 = false;
        playTwoMp3 = false;
        inCamera = false;
        LedUtils.prepare();   //调用相机闪光灯准备
        setmSettingCameraSwitch();
    }

    /*
    1、根据当前时间显示内容，状态1：在非值班考勤时间内，显示系统时间。
                            状态2：在考勤时间内，显示已考勤人列表，每个考勤人显示信息（照片、上一次刷脸考勤时间、距离上一次刷脸考勤的间隔时间(假设考勤间隔为1分钟)
                            =60-（当前时间-上次刷脸时间）,当它为正值时作为倒计时时间 数字- -、当它为负值取正 数字++
    2、视图需要
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initViews() {
        /*
        初始化recycleListView 并指定mTimeDownBeanList为recycleListView展示的数据，其二者用mCountDownAdapter进行适配
        开启线程进行每秒一次的页面刷新
         */
        mDialog = new SpotsDialog(this);
        mDialog.getWindow().setGravity(Gravity.CENTER);

        SnapHelper snapHelper=new LinearSnapHelper();
        snapHelper.attachToRecyclerView(recycleListView);

        LinearLayoutManager ms=new LinearLayoutManager(this) ;
        ms.setOrientation(LinearLayoutManager.HORIZONTAL);
        mCountDownAdapter = new CountDownAdapter(this, mTimeDownBeanList);
        int space=8;
        recycleListView.setHasFixedSize(true);
        recycleListView.setLayoutManager(ms);
        recycleListView.addItemDecoration(new SpacesItemDecoration(space));
        recycleListView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        ((SimpleItemAnimator) recycleListView.getItemAnimator()).setSupportsChangeAnimations(false);
        recycleListView.setAdapter(mCountDownAdapter);
    }

    private void startMinuteThread(){
        new TimeThread().start();
    }

    private void sleepMinuteTread(){
        try {
            TimeThread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onMsgResult(Message msg) {
        super.onMsgResult(msg);
        switch (msg.what) {
            case R.id.wait_state:
                to_waitState();
                break;
            case R.id.config_state:
                to_confProp();
                break;
            case R.id.unDuty_state:
                to_unDuty();
                break;
            case R.id.NoneDuty_state:
                to_NoneDuty();
                break;
            case R.id.FristDuty_state:
                to_FristDuty();
                break;
            case R.id.SecondDuty_state:
                to_SecondDuty();
                break;
            case R.id.update_Version:
                to_sendVersion();
                break;
        }
    }

    private void to_confProp() {
        //地区參數配置處理
        hodeLoding();
        Tocamera.setVisibility(View.GONE);
        signInfoLy.setVisibility(View.GONE);
        settingPageDetail.setVisibility(View.VISIBLE);
        monitoringRadioGrop();
        NiceSpinner niceSpinner=(NiceSpinner)findViewById(R.id.nice_spinner_detail);
        final List<String> dataList = new ArrayList<>();
        dataList.add("-- 请选择 --");
        dataList.add("02001——广东省本级");
        dataList.add("02011——中山");
        dataList.add("02010——东莞");
        dataList.add("02008——惠州");
        dataList.add("02005——韶关");
        dataList.add("02003——汕头");
        dataList.add("02009——汕尾");
        dataList.add("02007——梅州");
        dataList.add("02004——潮州");
        dataList.add("02002——珠海");
        dataList.add("02019——揭阳");
        dataList.add("02016——茂名");
        dataList.add("02014——阳江");
        dataList.add("02013——佛山");
        dataList.add("02006——河源");
        dataList.add("02020——云浮");
        dataList.add("02017——肇庆");
        dataList.add("02015——湛江");
        dataList.add("03602——广州");
        dataList.add("02014——阳江");
        dataList.add("02018——清远");
        dataList.add("02012——江门");

        niceSpinner.attachDataSource(dataList);
        if (dataList.contains(getParam("AREA_NO")))  //地区选择显示初始化，原则上显示当前地区
        {
            niceSpinner.setSelectedIndex(dataList.indexOf(getParam("AREA_NO")));
        }

        niceSpinner.addOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String area=dataList.get(position).substring(7);
                Log.e("地区名：", area);
                showArea.setText("-- "+area+" --");
                AREANO=dataList.get(position).substring(0,5);
                String areaNo=dataList.get(position);
                saveParam(areaNo);
                Log.e("设置编号：", areaNo);
            }
        });
    }

    private void to_waitState() {
        settingPageDetail.setVisibility(View.GONE);
        Tocamera.setVisibility(View.VISIBLE);
        signInfoLy.setVisibility(View.VISIBLE);
        mPresenter = new HomePresenter(this);
        mPresenter.loadDutyingPeoples(AREANO);
    }

    private void to_SecondDuty() {
        systemTimeRl.setVisibility(View.GONE);
        Tocamera.setVisibility(View.VISIBLE);
        signInfoLy.setVisibility(View.VISIBLE);
        beforeSignRl.setVisibility(View.GONE);
        refreshBeanList(true);
    }

    private void to_FristDuty() {
        systemTimeRl.setVisibility(View.GONE);
        signInfoLy.setVisibility(View.VISIBLE);
        Tocamera.setVisibility(View.VISIBLE);
        beforeSignRl.setVisibility(View.VISIBLE);
        String noneDutyEndTime = TimeUtils.getTimeAfterSeconds(unUnDutyEndTime, firstDutyCountdown);
        //距离首次打卡截止的剩余时间（单位：秒）
        long noneDutyEndCountdown = TimeUtils.differenceSenconds(sysTimeStr, noneDutyEndTime);
        if (noneDutyEndCountdown > 0) {
            beforeSignTv.setText("还有一人没考勤签到，倒计时");
            mMp3Play.play_release();
            playOneMp3 = false;
        } else {
            beforeSignTv.setText("还有一人没考勤签到，已超时");
            noneDutyEndCountdown = -noneDutyEndCountdown;
            if (!playOneMp3) {
                mMp3Play.play(this, R.raw.baojing1);
                playOneMp3 = true;
            }
        }
        beforeSignTimeTv.setText(TimeUtils.CountDownSenconds(noneDutyEndCountdown));
        refreshBeanList(playOneMp3);
    }

    private void to_NoneDuty() {
        systemTimeRl.setVisibility(View.GONE);
        Tocamera.setVisibility(View.VISIBLE);
        signInfoLy.setVisibility(View.VISIBLE);
        beforeSignRl.setVisibility(View.VISIBLE);
        firstDutyCountdown = Integer.parseInt(String.valueOf(FREE_TIME)) * 60;
        String noneDutyEndTime = TimeUtils.getTimeAfterSeconds(unUnDutyEndTime, firstDutyCountdown);
        //距离首次打卡截止的剩余时间（单位：秒）
        long noneDutyEndCountdown = TimeUtils.differenceSenconds(sysTimeStr, noneDutyEndTime);
//        Log.e("首次时间", String.valueOf(noneDutyEndCountdown));
        if (noneDutyEndCountdown > 0) {
            beforeSignTv.setText("还没人考勤签到，倒计时");

            beforeSignTimeTv.setText(TimeUtils.CountDownSenconds(noneDutyEndCountdown));
            long hour = (int) (noneDutyEndCountdown / 3600);
            long min = (noneDutyEndCountdown / 60) % 60;
            long second = noneDutyEndCountdown % 60;
            if (min <= Integer.parseInt(FREE_TIME)) {
                if (hour == 0 && min > 0 && (second == 0 || second == 30)) {
                    //大于一分钟，每30秒播报
                    mvoice.speak("距离下次打卡时间还有" + min + "分钟");
                } else if (hour == 0 && min == 0 && (second == 0 || second == 20 || second == 40)) {
                    //最后一分钟，每20秒播报
                    mvoice.speak(String.valueOf(second) + "秒");
                }
            }
            mMp3Play.play_release();
            playNoneMp3 = false;
        } else {
            if (!playNoneMp3) {
                mMp3Play.play(this, R.raw.baojing1);   //2018
                playNoneMp3 = true;
            }
            beforeSignTv.setText("还没人考勤签到，已超时");
            beforeSignTimeTv.setText(TimeUtils.CountDownSenconds(-noneDutyEndCountdown));
        }
    }

    /*
     * 报警播放控制
     */
    private void refreshBeanList(boolean needPlayMp3) {
        int normal=0;   //异常标志，3人以上有两人正常则不发出报警声音
        for (TimeDownBean timeDownBean : mTimeDownBeanList) {
            String nextDutyTimeString = TimeUtils.getTimeAfterSeconds(timeDownBean.getLastTime(), dutyingInterval);
            long nextDutyCountdown = TimeUtils.differenceSenconds(sysTimeStr, nextDutyTimeString);
            timeDownBean.setUseTime(nextDutyCountdown);

            if(nextDutyCountdown>0)   //正常
            {
                normal = normal+1;
            }
        }
        if (normal>=2)
        {
                mMp3Play.play_release();

        }
        else {
            mMp3Play.play(this, R.raw.baojing1);   //2018
        }

        mCountDownAdapter.notifyDataSetChanged();
    }

    //進入值班時間或進入非值班時間,显示系统时间
    private void to_unDuty() {
        systemTimeRl.setVisibility(View.VISIBLE);
        Tocamera.setVisibility(View.GONE);
        signInfoLy.setVisibility(View.GONE);
        beforeSignRl.setVisibility(View.GONE);
        systemTime.setText(sysTimeStr.substring(11)); //更新时间
    }



    private void initEvents() {
        Tocamera.setOnClickListener(this);
        settingEntrance.setOnClickListener(this);
        settingLeaveBtn.setOnClickListener(this);
        mUpdateVersionBtn.setOnClickListener(this);
        getconfiguration.setOnClickListener(this);
    }

    /*
    1、一个刷脸button事件：点击激活活体拍照
    2、每个值班人员的退出考勤button事件，点击退出考勤（条件判断：列表中不能少于二人）
    3、倒计时报警消息处理事件
     */
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.Tocamera) {
            if (!isPermissionAccessed) {
                checkPremissions();
            }
        }

        if (v.getId() == R.id.Tocamera) {
            inCamera = true;
            if (ledState)
                LedUtils.openLed();   //打开相机闪光灯
            else
                LedUtils.closeLed();

            mMp3Play.play_release();
//            open(v);
            //补光灯
            final Intent intent = new Intent(HomeActivity.this, AliveActivity.class);
            //传递动作序列的int数组
            intent.putExtra(AliveActivity.EXTRA_SEQUENCES, Settings.INSTANCE.getSequencesInt(getApplicationContext()));
            //传递是否录制视频的参数
            intent.putExtra(AliveActivity.EXTRA_IS_RECODING, true);
            //传递录制视频的持续时间参数，不传开启视频录制的情况下默认10秒
            intent.putExtra(AliveActivity.EXTRA_RECODING_DURATION, 10);
            //传递单个动作超时时间参数，单位为秒
            intent.putExtra(AliveActivity.EXTRA_MOTION_TIMEOUT, 10);
            //视频保存路径（不传默认未外存储根目录）
            type = constants.TYPE_AUTHENITCATION;
            startActivityForResult(intent, 1);
        }
        if(v.getId()==R.id.setting_entrance)   //地区参数设置
        {
            mMp3Play.play_release();
            onRemoveMsgs(R.id.wait_state);
            onSendMsgs(R.id.config_state);
        }
        if(v.getId()==R.id.setting_leave_btn) //地区页面的确认按钮
        {
            onRemoveMsgs(R.id.config_state);
            onSendMsgs(R.id.wait_state);
        }
        if (v.getId()==R.id.setting_updateVersion_btn)
        {
            onSendMsgs(R.id.update_Version);
        }
        if(v.getId()==R.id.getConfiguration)
        {
          RestarApp.restartAPP(HomeActivity.this);
        }
    }

    private static boolean isMarshmallow() {
        return Build.VERSION.SDK_INT >= 23;
    }

    private void checkPremissions() {
        if (isMarshmallow()) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                isPermissionAccessed = false;
                if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) || shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                }

                requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
            } else {
            }
        }
    }

    @Override
    public void setPresenter(HomeContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public void showLoding() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d("BACS", "mDialog.show()");
                mDialog.show();
            }
        });
    }

    @Override
    public void hodeLoding() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d("BACS", "mDialog.dismiss()");
                mDialog.dismiss();
            }
        });
    }

    /*
    读取服务端参数异常，3秒重试
     */
    @Override
    public void showInitError(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG);
                showMyToast(toast, 3000);
                onRemoveMsgs(R.id.wait_state);
                onSendMsgDelayed(R.id.wait_state, null, 3000);
            }
        });

    }

    @Override
    public void showError(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG);
                showMyToast(toast, 3000);
            }
        });

    }

    //土司属性设置

    public void showMyToast(final Toast toast, final int cnt) {
        //字体大小设置
        LinearLayout layout = (LinearLayout) toast.getView();
        TextView messageTextView = (TextView) layout.getChildAt(0);
        messageTextView.setTextSize(40);
        //显示时间
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                toast.show();
            }
        }, 0, 3000);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                toast.cancel();
                timer.cancel();
            }
        }, cnt);
    }
    /*
    MVP 的V实现过程
     */
    public void showDutyingPeople(List<DutyingPeople.ResDataBean> resData) {
        //在值班开始第一个时间间隔进行数据初始化，没有人签到时
        //=当前时间-非值班结束时间
//        sleepMinuteTread();
        mTimeDownBeanList.clear();
        if (resData!=null){
            if (resData.size()!=0) {
                for (DutyingPeople.ResDataBean dutyingDataBean : resData) {
                    long initTimeDownSencond = dutyingInterval  - (TimeUtils.differenceSenconds(dutyingDataBean.getPunchTime(), sysTimeStr)+1);
                    TimeDownBean timeDownBean = new TimeDownBean(dutyingDataBean.getPunchTime(), initTimeDownSencond, dutyingDataBean.getEmpName(), dutyingDataBean.getEmpPhoto());
                    mTimeDownBeanList.add(timeDownBean);   //添加数据
                }
            }
            else
            {
//                mCountDownAdapter.notifyDataSetChanged();

            }
        }else {
//            mCountDownAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void showSignPeople(SignPeople signPeopleData) {
        if (signPeopleData == null){
//            Toast.makeText(this,"考勤失败，请重试！！" ,Toast.LENGTH_LONG ).show();
            Log.d("BACS:", "考勤失败，请重试！！");
            showError("考勤失败，请重试!");
        }else
        {
//            if (AREANO.equals(signPeopleData.getAreaNo()))
//            {
            if (signPeopleData.getResult().equals("1") && signPeopleData.getRetCode().equals("0")) {
                boolean flag = false;  //判断是否插入的标志，未插入为false
                String currentTimeString = TimeUtils.getCurrentTimeString();
                dutyingInterval = Integer.parseInt(String.valueOf(FREE_TIME)) * 60;
                long useTime = dutyingInterval - TimeUtils.differenceSenconds(currentTimeString, sysTimeStr);
                TimeDownBean timeDownBean = new TimeDownBean(currentTimeString, useTime, signPeopleData.getUserName(),signPeopleData.getUserPhoto());
                for (int i = 0; i < mTimeDownBeanList.size(); i++) {
                    if ((signPeopleData.getUserName().equals(mTimeDownBeanList.get(i).getContent())))   //判断是否存在同名情况
                    {
                        mTimeDownBeanList.set(i, timeDownBean);   //存在同名，在对应位置刷新列表项目
                        flag = true;   //修改插入标志
                    }
                }
                //不存在同名，在后面添加列表新项
                if (!flag) {
                    mTimeDownBeanList.add(timeDownBean);
                    flag = true;
                }

                Log.d("更新列表:", "");

            } else {
                //弹出异常信息提示
                if (signPeopleData.getRetCode().equals("-2"))
                {
                    showError("人员与地区不匹配，请重新设置对应地区。");
                }
                else
                    showError("照片无法识别，请重试");
            }
//            }
//            else
//            {
//                showError("人员归属错误，请设置正确的地区再重试。");
//            }
        }
    }


    //刷脸后显示列表信息
    @Override
    public String getPhotosData(byte[] image) {
        String ImageData = String.valueOf(ImageHelper.BitmapTo64(ImageHelper.bytes2Bimap(image)));
        Log.d("显示照片base64", ImageData);
        return ImageData;
    }

    //获取报警开始倒计时
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void initParams(ParamObj params) {
        if (params!=null)
        {
            FREE_TIME=params.getFREE_TIME(); //登记周期
            RING_TIME=params.getRING_TIME(); //报警开始时间
            OFF_TIME=params.getOFF_TIME();//值班开始时间
            ON_TIME=params.getON_TIME();//值班结束时间
            String NOW_TIME=params.getSystimestamp();
            getServerTime(NOW_TIME);  //设置android系统时间

            sysTimeStr = TimeUtils.getCurrentTimeString();   //系统日期
            Log.d("时间：（sys）", String.valueOf(params.getSystimestamp()));


            unDutyBeginTime = sysTimeStr.substring(0, 11) + OFF_TIME;   //日期加时间：2018-12-21 00:00:00
            unUnDutyEndTime = sysTimeStr.substring(0, 11) + ON_TIME;   //日期加时间：2018-12-21 00:00:00
            Log.d("时间：（begin、end）", unDutyBeginTime+";  "+unUnDutyEndTime);
            Log.e("时间：（on、off）",ON_TIME+" ;   "+OFF_TIME);

            CountDownAdapter.setRingTime(RING_TIME);
            if (!((sysTimeStr.compareTo(unDutyBeginTime)>=0) && (sysTimeStr.compareTo(unUnDutyEndTime) < 0))) {
                mTimeDownBeanList.clear();
                loadHomeData(AREANO);
            }

            startMinuteThread();
        }
        else {
            Log.d("BACS:","获取参数失败" );
            showError("获取参数失败！三秒后重试！");
            //弹出错误窗体，点击后退出应用
        }
    }

    //刷脸的代码
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Intent intent = new Intent();
            intent.putExtra("img", data.getByteArrayExtra(AliveActivity.EXTRA_RESULT_IMG_DATA));
//			data.getStringExtra(AliveActivity.EXTRA_RESULT_VIDEO_DATA);// 获取录制的视频存储路径
            switch (type) {
                case constants.TYPE_AUTHENITCATION:
                    //可能出错在这里——是否可以直接接收
                    image = intent.getByteArrayExtra("img");
                    mPresenter.loadSignPeople(getPhotosData(image),AREANO);
                    break;
                default:
                    break;
            }
//            startActivity(intent);
//            finish();
        } else if (resultCode == RESULT_CANCELED) {
            // 活体检测失败返回
        }
        LedUtils.closeLed();   //关闭相机闪光灯
        inCamera = false;
    }


    private void to_sendVersion() {
        builder = AllenVersionChecker
                .getInstance()
                .requestVersion()
                .setRequestUrl(constants.REQUESTURL)
                .request(new RequestVersionListener() {
                    @Nullable
                    @Override
                    public UIData onRequestVersionSuccess(String result) {
                        //get the data response from server,parse,get the `downloadUlr` and some other ui date
                        Log.d("BACS_VERSION", result);
                        VersionJson versionJson = JSON.parseObject(result, VersionJson.class);

                        String currVersion = AppUtils.getVersionName(HomeActivity.this);
                        if (!currVersion.equals(versionJson.getVersion())) {
                            //return null if you dont want to update application
                            return UIData.create()
                                    .setDownloadUrl(constants.DOWNLOADURL)
                                    .setTitle("版本更新")
                                    .setContent("版本更新为" +versionJson.getVersion());
                        }
                        else{
                            showError("目前已是最新版本，无需更新。");
                            return null;
                        }
                    }
                    @Override
                    public void onRequestVersionFailure(String message) {

                    }
                });
        builder.executeMission(this);
    }


    //设置页面
    private void monitoringRadioGrop(){
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.main_radiobutton_area:
                        mNiceSprinnerDetailRl.setVisibility(View.VISIBLE);
                        mSettingCameraRl.setVisibility(View.GONE);
                        mUpdateVersionBtn.setVisibility(View.GONE);
                        getconfiguration.setVisibility(View.GONE);
                        break;
                    case R.id.main_radiobutton_camera:
                        mNiceSprinnerDetailRl.setVisibility(View.GONE);
                        mSettingCameraRl.setVisibility(View.VISIBLE);
                        mUpdateVersionBtn.setVisibility(View.GONE);
                        getconfiguration.setVisibility(View.GONE);
                        break;
                    case R.id.main_radiobutton_version:
                        mNiceSprinnerDetailRl.setVisibility(View.GONE);
                        mSettingCameraRl.setVisibility(View.GONE);
                        mUpdateVersionBtn.setVisibility(View.VISIBLE);
                        getconfiguration.setVisibility(View.VISIBLE);
                        break;
                    default:
                        mNiceSprinnerDetailRl.setVisibility(View.GONE);
                        mSettingCameraRl.setVisibility(View.GONE);
                        mUpdateVersionBtn.setVisibility(View.GONE);
                        getconfiguration.setVisibility(View.GONE);
                        Log.d("tag","监听事件不明");
                        break;
                }
            }
        });
    }

    //设置拍照闪光灯开关
    private void setmSettingCameraSwitch()
    {
        mSettingCameraSwitch.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                mSettingCameraSwitch.isChecked();
                ledState=isChecked;   //设置闪光灯开关状态
            }
        });
    }

    //获取服务器时间
    public void getServerTime(String NOW_TIME){
        try {
            Process process = Runtime.getRuntime().exec("su");
            String datetime=NOW_TIME; //测试的设置的时间【时间格式 yyyyMMdd.HHmmss】
            DataOutputStream os = new DataOutputStream(process.getOutputStream());
            os.writeBytes("setprop persist.sys.timezone GMT\n");
            os.writeBytes("/system/bin/date -s "+datetime+"\n");
            os.writeBytes("clock -w\n");
            os.writeBytes("exit\n");
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //开机检测更新
    private void checkVersion() {
        builder = AllenVersionChecker
                .getInstance()
                .requestVersion()
                .setRequestUrl(constants.REQUESTURL)
                .request(new RequestVersionListener() {
                    @Nullable
                    @Override
                    public UIData onRequestVersionSuccess(String result) {
                        //get the data response from server,parse,get the `downloadUlr` and some other ui date
                        Log.d("BACS_VERSION", result);
                        VersionJson versionJson = JSON.parseObject(result, VersionJson.class);

                        String currVersion = AppUtils.getVersionName(HomeActivity.this);
                        if (!currVersion.equals(versionJson.getVersion())) {
                            //return null if you dont want to update application
                            return UIData.create()
                                    .setDownloadUrl(constants.DOWNLOADURL)
                                    .setTitle("版本更新")
                                    .setContent("版本更新为" +versionJson.getVersion());
                        }
                        else{
                            return null;
                        }
                    }
                    @Override
                    public void onRequestVersionFailure(String message) {

                    }
                });
        builder.executeMission(this);
    }


}