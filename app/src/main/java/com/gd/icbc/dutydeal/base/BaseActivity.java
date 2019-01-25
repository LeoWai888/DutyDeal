package com.gd.icbc.dutydeal.base;

import android.app.ActionBar;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

public class
BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);//remove title bar  即隐藏标题栏
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);//remove notification bar  即全屏
    }

    private static BaseActivity INSTANCE = null;

    private static Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            INSTANCE.onMsgResult(msg);
        };
    };

    /**
     * Message 消息回调
     *
     * @param msg
     */
    protected void onMsgResult(Message msg) {

    }

    protected void onRemoveMsgs(int what) {
        mHandler.removeMessages(what);
    }

    protected void onSendMsgs(int what) {
        mHandler.sendMessage(mHandler.obtainMessage(what));
    }

    protected void onSendMsg(int what, Object obj) {
        mHandler.sendMessage(mHandler.obtainMessage(what, obj));
    }

    protected void onSendMsgDelayed(int what, Object obj, long delayMillis) {
        mHandler.removeMessages(what, obj);
        mHandler.sendMessageDelayed(mHandler.obtainMessage(what, obj),
                delayMillis);
    }

    @Override
    protected void onResume() {
        INSTANCE = this;
        super.onResume();
        hideBottomUIMenu();
    }

    @Override
    protected void onPause() {
        super.onPause();
        hideBottomUIMenu();
    }

    //隐藏状态栏
    protected void hideStatusBar(){
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        ActionBar actionBar = getActionBar();
        if (actionBar != null)
            actionBar.hide();
    }

    // 隐藏键盘
    protected void hideKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        // 得到InputMethodManager的实例
        if (imm.isActive()) {
            // 如果开启
            imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,
                    InputMethodManager.HIDE_NOT_ALWAYS);
            // 关闭软键盘，开启方法相同，这个方法是切换开启与关闭状态的
        }
    }

    /**
     * 隐藏虚拟按键，并且全屏
     */
    protected void hideBottomUIMenu() {
        //隐藏虚拟按键，并且全屏
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }
}
