package com.detect.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.detect.utils.ImageHelper;
import com.detect.utils.MediaController;
import com.detect.view.CircleTimeView;
import com.detect.view.FixedSpeedScroller;
import com.detect.view.MotionPagerAdapter;
import com.detect.view.TimeViewContoller;
import com.facecore.bh.R;
import com.libface.bh.camera.CameraError;
import com.libface.bh.camera.CameraHandle;
import com.libface.bh.camera.OnCameraListener;
import com.libface.bh.camera.PreviewView;
import com.libface.bh.library.BoundaryInfo;
import com.libface.bh.library.Difficulty;
import com.libface.bh.library.FaceInfo;
import com.libface.bh.library.LibLiveDetect;
import com.libface.bh.library.ResultCode;
import com.libface.bh.library.Size;
import com.libface.bh.utils.FileUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 活体检测界面
 */
public class AliveActivity extends Activity {

    /**
     * 动作序列传递的key，传递int[]类型
     */
    public static final String EXTRA_SEQUENCES = "extra_sequences";
    /**
     * 录制视频开关传递的key。传递boolean类型
     */
    public static final String EXTRA_IS_RECODING = "extra_is_recoding";
    /**
     * 单个动作超时时间传递的key。传递int类型
     */
    public static final String EXTRA_MOTION_TIMEOUT = "extra_motion_timeout";
    /**
     * 录制视频时长传递的key。传递int类型，单位为秒
     */
    public static final String EXTRA_RECODING_DURATION = "extra_recoding_duration";
    /**
     * 录制视频的存储路径传递的key。传递String类型
     */
    public static final String EXTRA_RECODING_SAVE_PATH = "extra_recoding_save_path";

    /**
     * 返回给上一级界面图片字节数组的key
     */
    public static final String EXTRA_RESULT_IMG_DATA = "result_img_data";
    /**
     * 返回给上一级界面视频存储路径的key
     */
    public static final String EXTRA_RESULT_VIDEO_DATA = "result_video_data";

    private static final String FILES_PATH = Environment.getExternalStorageDirectory().getPath() + "/boomhope/";
    private static final String MODEL_FILE_NAME = "face_1.0.model";
    private static final String LICENSE_FILE_NAME = "bh_liveness.lic";
    private final String[] assetsFiles = {MODEL_FILE_NAME, LICENSE_FILE_NAME, "align.md", "face.md", "face2.md", "jda_model.md", "params.txt"};

    private static final int DELAY_ALIGN_DURATION = 1000; // 进入检测人脸的延时时间
    private static final int DEFAULT_MOTION_TIMEOUT = 10; // 单个动作默认超时时间

    private boolean mIsStopped = true;
    private boolean mMotionChanged = false; // 动作是否改变标识
    private boolean mIsImageDataChanged = false; // 帧数据是否改变

    // 默认动作序列
    private int[] mSequences = new int[]{LibLiveDetect.EYEBLINK, LibLiveDetect.OPENMOUTH, LibLiveDetect.DOWN_PITCH, LibLiveDetect.YAW};

    // 单个动作超时时间
    private int motionTimeOut = DEFAULT_MOTION_TIMEOUT;
    private int mCurrentMotionIndex = -1;
    private long mAlignedStartTime = -1L;

    // 帧数据缓冲大小
    private final byte[] mImageData = new byte[CameraHandle.DEFAULT_PREVIEW_WIDTH * CameraHandle.DEFAULT_PREVIEW_HEIGHT * 3 / 2];
    private byte[] mDetectImageData = null;

    private ExecutorService mLivenessExecutor = null;

    private ImageView mCommonBackground = null;
    private TextView mNoteTextView = null;
    private View mDetectLayout = null;
    private ViewPager mAnimationView = null;

    private PreviewView mCameraPreviewView = null; // 视频预览控件
    private LivenessState mState = new AlignmentState(); // 活体检测状态回调接口，默认初始化为人脸对齐检测状态回调
    private TimeViewContoller mTimerViewContoller = null; // 倒计时控件


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!checkPermission()) {
            exit(R.string.txt_error_permission);
        }

        setContentView(R.layout.activity_alive);

        // init setting values.
        Intent intent = getIntent();
        int[] sequences = intent.getIntArrayExtra(EXTRA_SEQUENCES);
        if (sequences != null && sequences.length > 0) {
            mSequences = sequences;
        }

        // 是否开启视频录制，默认未开启
        boolean isRecoding = intent.getBooleanExtra(EXTRA_IS_RECODING, false); // 获取是否开启录制视频
        // 如果开启了录制视频功能，获取录制时长和存储路径
        if (isRecoding) {
            // 开启录制视频时，录制时长，默认10s。
            int recoding_duration = intent.getIntExtra(EXTRA_RECODING_DURATION, 10);

            // 视频存放路径
            String vedio_save_path = intent.getStringExtra(EXTRA_RECODING_SAVE_PATH);
            // 初始化视频录制，第一个参数为视频录制时长；第二个参数为视频存储路径，不传默认存储在sdcard根目录名称为alive.mp4
            CameraHandle.INSTANCE.initRecording(recoding_duration, vedio_save_path);
        }

        motionTimeOut = intent.getIntExtra(EXTRA_MOTION_TIMEOUT, DEFAULT_MOTION_TIMEOUT);

        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                exit(R.string.txt_error_canceled);
            }
        });

        mCommonBackground = (ImageView) findViewById(R.id.iv_common_background);
        mNoteTextView = (TextView) findViewById(R.id.txt_note);

        mCameraPreviewView = (PreviewView) findViewById(R.id.camera_preview);
        CameraHandle.INSTANCE.setPreviewView(mCameraPreviewView); // 设置摄像头预览视图
        // 设置摄像头预览回调监听
        CameraHandle.INSTANCE.setOnCameraListener(new OnCameraListener() {
            @Override
            public void onCameraDataFetched(byte[] data) {
                // 摄像头预览回调，data为预览帧数据
                synchronized (mImageData) {
                    if (data == null || data.length < 1) {
                        return;
                    }
                    /*调试用
                    try {
                        Size size = new Size(CameraHandle.DEFAULT_PREVIEW_WIDTH,CameraHandle.DEFAULT_PREVIEW_HEIGHT);
                        //手机上测试 图片方向 ->   cameraOrientation 270
                        //实体机器上测试 图片方向  <-   cameraOrientation 0
                        YuvImage image = new YuvImage(data, ImageFormat.NV21, size.getWidth(), size.getHeight(), null);
                        if (image != null) {
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            image.compressToJpeg(new Rect(0, 0, size.getWidth(), size.getHeight()), 80, stream);
                            Bitmap bmp = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size());
                            stream.close();
//                            Log.e("CameraDataFetchedBm照片：", String.valueOf(ImageHelper.BitmapTo64(bmp)));   //随时抓照片
                        }
                    } catch (Exception ex) {
                        Log.e("Sys", "Error:" + ex.getMessage());
                    }*/
                    //Log.d("onCameraDataFetched", String.valueOf(mImageData));
                    Arrays.fill(mImageData, (byte) 0);
                    System.arraycopy(data, 0, mImageData, 0, data.length);
                    mIsImageDataChanged = true;
                }
            }

            @Override
            public void onError(CameraError error) {
                exit(R.string.txt_error_camera);
            }
        });

        mDetectLayout = findViewById(R.id.layout_detect);
        initDetectLayout();
    }

    @Override
    protected void onResume() {
        super.onResume();

        File dir = new File(FILES_PATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        // 拷贝assets文件至指定目录，如果文件存在，不再进行拷贝
        FileUtil.copyAssets(this, assetsFiles, dir.getAbsolutePath());
        ResultCode result = LibLiveDetect.init(AliveActivity.this, FILES_PATH + LICENSE_FILE_NAME, FILES_PATH + MODEL_FILE_NAME);
        if (result != ResultCode.OK) {
            exit(getMessageId(result.name()));
            return;
        }
        // 启动活体检测任务
        startDetectThread();
    }

    @Override
    protected void onPause() {
        MediaController.getInstance().release();
        LibLiveDetect.getInstance().release();
        destroyExecutor();
        super.onPause();
    }

    private int getMessageId(String resultCodeName) {
        int messageId = -1;
        if (ResultCode.ERROR_MODEL_FILE_NOT_FOUND.name().equals(resultCodeName)) {
            messageId = R.string.txt_error_model_not_found;
        } else if (ResultCode.ERROR_LICENSE_FILE_NOT_FOUND.name().equals(resultCodeName)) {
            messageId = R.string.txt_error_license_not_found;
        } else if (ResultCode.ERROR_CHECK_CONFIG_FAIL.name().equals(resultCodeName)) {
            messageId = R.string.txt_error_config;
        } else if (ResultCode.ERROR_CHECK_LICENSE_FAIL.name().equals(resultCodeName)) {
            messageId = R.string.txt_error_license;
        } else if (ResultCode.ERROR_CHECK_MODEL_FAIL.name().equals(resultCodeName)) {
            messageId = R.string.txt_error_model;
        } else if (ResultCode.ERROR_LICENSE_EXPIRE.name().equals(resultCodeName)) {
            messageId = R.string.txt_error_license_expire;
        } else if (ResultCode.ERROR_LICENSE_PACKAGE_NAME_MISMATCH.name().equals(resultCodeName)) {
            messageId = R.string.txt_error_license_package_name;
        } else if (ResultCode.ERROR_WRONG_STATE.name().equals(resultCodeName)) {
            messageId = R.string.txt_error_state;
        }
        return messageId;
    }

    private void showErrorMessage(int messageId) {
        if (messageId != -1) {
            Toast.makeText(this, messageId, Toast.LENGTH_SHORT).show();
        }
    }

    // 异常情况退出操作
    private void exit(final int messageId) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                mIsStopped = true;
                if (mTimerViewContoller != null) {
                    mTimerViewContoller.stop();
                }

                LibLiveDetect.getInstance().stopDetect(false, false);
                showErrorMessage(messageId);
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }

    private void updateMessage(final FaceInfo.FaceState faceState, final FaceInfo.FaceDistance faceDistance) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (faceDistance == FaceInfo.FaceDistance.CLOSE) {
                    mNoteTextView.setText(R.string.common_face_too_close);
                } else if (faceDistance == FaceInfo.FaceDistance.FAR) {
                    mNoteTextView.setText(R.string.common_face_too_far);
                } else if (faceState == FaceInfo.FaceState.NORMAL) {
                    mNoteTextView.setText(R.string.common_detecting);
                } else {
                    mNoteTextView.setText(R.string.common_tracking_missed);
                }
            }
        });
    }

    @SuppressLint("NewApi") // Already check in first case(Build.VERSION.SDK_INT < 23);
    private boolean checkPermission() {
        return Build.VERSION.SDK_INT < 23 || (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED);
    }

    private void initDetectLayout() {
        // init anim view.
        mAnimationView = (ViewPager) findViewById(R.id.pager_action);
        mAnimationView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });// return true prevent touch event
        mAnimationView.setAdapter(new MotionPagerAdapter(mSequences));
        try {
            // FixedSpeedScroller control change time
            Field mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            FixedSpeedScroller fScroller = new FixedSpeedScroller(mAnimationView.getContext());
            mScroller.set(mAnimationView, fScroller);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        // init timer view.
        CircleTimeView timerView = (CircleTimeView) findViewById(R.id.time_view);
        timerView.setTime(motionTimeOut);
        mTimerViewContoller = new TimeViewContoller(timerView);
        mTimerViewContoller.setCallBack(new TimeViewContoller.CallBack() {
            @Override
            public void onTimeEnd() {
                exit(R.string.txt_error_timeout);
            }
        });
    }

    // 切换动作动画、语音提示
    private void switchMotion(final int index) {
        mCurrentMotionIndex = index;
        mMotionChanged = true;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAnimationView.setCurrentItem(index, true);
                mTimerViewContoller.start(true);
            }
        });
        // 播放语音提示
        MediaController.getInstance().playNotice(AliveActivity.this, mSequences[mCurrentMotionIndex]);
    }

    // 活体检测任务
    private void startDetectThread() {
        mLivenessExecutor = Executors.newSingleThreadExecutor();
        mLivenessExecutor.execute(new Runnable() {
            @Override
            public void run() {
                ResultCode code = LibLiveDetect.getInstance().prepare(Difficulty.DIFFICULTY_NORMAL);
                if (code != ResultCode.OK) {
                    // Try restart liveness if prepare fail.
                    LibLiveDetect.getInstance().stopDetect(false, false);

                    code = LibLiveDetect.getInstance().prepare(Difficulty.DIFFICULTY_NORMAL);
                    if (code != ResultCode.OK) {
                        exit(getMessageId(code.name()));
                        return;
                    }
                }

                while (true) {
                    if (mIsStopped) {
                        break;
                    }
                    if (!mIsImageDataChanged) {
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        continue;
                    }
                    mState.beforeDetect();
                    synchronized (mImageData) {
                        if (mDetectImageData == null) {
                            mDetectImageData = new byte[mImageData.length];
                        }
                        System.arraycopy(mImageData, 0, mDetectImageData, 0, mImageData.length);
                    }
                    mDetectImageData = rotateYUV420Degree180(mDetectImageData,CameraHandle.DEFAULT_PREVIEW_WIDTH,CameraHandle.DEFAULT_PREVIEW_HEIGHT);
/*
                    try {
                        Size size = new Size(CameraHandle.DEFAULT_PREVIEW_WIDTH,CameraHandle.DEFAULT_PREVIEW_HEIGHT);
                        //手机上测试 图片方向 ->   cameraOrientation 270
                        //实体机器上测试 图片方向  <-   cameraOrientation 0
                        YuvImage image = new YuvImage(mDetectImageData, ImageFormat.NV21, size.getWidth(), size.getHeight(), null);
                        if (image != null) {
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            image.compressToJpeg(new Rect(0, 0, size.getWidth(), size.getHeight()), 80, stream);
                            Bitmap bmp = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size());
                            stream.close();
//                            Log.e("CameraDataFetchedBm照片：", String.valueOf(ImageHelper.BitmapTo64(bmp)));   //随时抓照片
                        }
                    } catch (Exception ex) {
                        Log.e("Sys", "Error:" + ex.getMessage());
                    }*/
                    // 活体检测，包括正脸对齐和动作检测
                    /*Size previewSize = new Size(CameraHandle.DEFAULT_PREVIEW_WIDTH, CameraHandle.DEFAULT_PREVIEW_HEIGHT);
//                    Size containerSize = new Size(mCameraPreviewView.getWidth(), mCameraPreviewView.getHeight());
//                    BoundaryInfo boundaryInfo = new BoundaryInfo(((View) mCameraPreviewView.getParent()).getWidth() / 2, ((View) mCameraPreviewView.getParent()).getHeight() / 2, (((View) mCameraPreviewView.getParent()).getWidth() / 3));

                    Size containerSize = new Size(CameraHandle.DEFAULT_PREVIEW_HEIGHT, CameraHandle.DEFAULT_PREVIEW_WIDTH);;
                    BoundaryInfo boundaryInfo = new BoundaryInfo(CameraHandle.DEFAULT_PREVIEW_HEIGHT / 2,CameraHandle.DEFAULT_PREVIEW_WIDTH / 2,  CameraHandle.DEFAULT_PREVIEW_HEIGHT / 3);
                    final FaceInfo info = LibLiveDetect.getInstance().detect(mDetectImageData, previewSize, containerSize, CameraHandle.INSTANCE.getCameraOrientation(), boundaryInfo);
                    */
                    final FaceInfo info = LibLiveDetect.getInstance()
                            .detect(mDetectImageData,
                                    new Size(CameraHandle.DEFAULT_PREVIEW_WIDTH, CameraHandle.DEFAULT_PREVIEW_HEIGHT),
                                    new Size(mCameraPreviewView.getWidth(), mCameraPreviewView.getHeight()),
                                    CameraHandle.INSTANCE.getCameraOrientation(),
                                    new BoundaryInfo(((View)mCameraPreviewView.getParent()).getWidth() /
                                            2,
                                            ((View)mCameraPreviewView.getParent()).getHeight() /
                                                    2,
                                            (((View)mCameraPreviewView.getParent()).getWidth() /
                                                    3)));
                    Log.e("faceInfo ", info.toString());

                    mIsImageDataChanged = false;
                    if (mIsStopped) {
                        break;
                    }
                    mState.checkResult(info);
                }
            }
        });
        mIsStopped = false;
    }

    private byte[] rotateYUV420Degree180(byte[] data, int imageWidth, int imageHeight) {
        byte[] yuv = new byte[imageWidth * imageHeight * 3 / 2];
        int i = 0;
        int count = 0;
        for (i = imageWidth * imageHeight - 1; i >= 0; i--) {
            yuv[count] = data[i];
            count++;
        }
        i = imageWidth * imageHeight * 3 / 2 - 1;
        for (i = imageWidth * imageHeight * 3 / 2 - 1; i >= imageWidth * imageHeight; i -= 2) {
            yuv[count++] = data[i - 1];
            yuv[count++] = data[i];
        }
        return yuv;
    }

    private void destroyExecutor() {
        if (mLivenessExecutor == null) {
            return;
        }
        mLivenessExecutor.shutdown();
        try {
            mLivenessExecutor.awaitTermination(100, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mLivenessExecutor = null;
    }

    //开始活体检测状态
    private void switchToDetectState() {
        mState = new DetectState();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mNoteTextView.setVisibility(View.GONE);
                mDetectLayout.setVisibility(View.VISIBLE);

                // 调用此方法准备编码视频流。开启视频录制情况下此方法有效，且必须调用此方法才会自动开始编码视频流。
                CameraHandle.INSTANCE.preparedRecoder();

                mAnimationView.setCurrentItem(0, false);
                if (mCurrentMotionIndex > -1) {
                } else {
                    mCommonBackground.setImageResource(R.drawable.icon_user_face);
                }

                // restart detect when align passed.
                mIsStopped = true;
                destroyExecutor();
                startDetectThread();

                switchMotion(0);
            }
        });
    }

    private interface LivenessState {
        void checkResult(FaceInfo info);

        void beforeDetect();
    }

    // 人脸对齐结果回调。在检测动作之前回调。
    private class AlignmentState implements LivenessState {

        private boolean mIsMotionSet = false;

        @Override
        public void checkResult(FaceInfo info) {
            if (info.getFaceState() == FaceInfo.FaceState.NORMAL && info.getFaceDistance() == FaceInfo.FaceDistance.NORMAL) {
                if (mAlignedStartTime < 0) {
                    mAlignedStartTime = SystemClock.uptimeMillis();
                } else {
                    if (SystemClock.uptimeMillis() - mAlignedStartTime > DELAY_ALIGN_DURATION) {
                        mAlignedStartTime = -1;
                        switchToDetectState(); // 切换为动作检测状态回调
                        return;
                    }
                }
            } else {
                mAlignedStartTime = -1L;
            }
            updateMessage(info.getFaceState(), info.getFaceDistance());
        }

        @Override
        public void beforeDetect() {
            if (!mIsMotionSet) {
                mIsMotionSet = LibLiveDetect.getInstance().setMotion(LibLiveDetect.EYEBLINK);
            }
        }
    }

    //检测状态回调
    private class DetectState implements LivenessState {

        @Override
        public void checkResult(FaceInfo info) {
            if (info.getFaceState() == null || info.getFaceState() == FaceInfo.FaceState.UNKNOWN || info.getFaceState() == FaceInfo.FaceState.OUT_OF_BOUND) {
                exit(R.string.txt_error_action_over);
                return;
            }
            if (!info.isPass()) {
                return;
            }
            if (mCurrentMotionIndex == mSequences.length - 1) {
                //检测到最后一个动作成功
                LibLiveDetect.getInstance().stopDetect(true, true);
                mIsStopped = true;
                mTimerViewContoller.stop();// 计数停止

                ((MotionPagerAdapter) mAnimationView.getAdapter()).stopAnimation();//动画停止
                // 释放资源
                MediaController.getInstance().release();
                LibLiveDetect.getInstance().release();
                destroyExecutor();
                Intent intent = new Intent();
                intent.putExtra(EXTRA_RESULT_IMG_DATA, LibLiveDetect.getInstance().getDetectImageData()); // 返回抓取到的图片字节数据
//                Toast.makeText(getApplicationContext(),EXTRA_RESULT_IMG_DATA , Toast.LENGTH_LONG).show();
                Log.e("字节数组：", EXTRA_RESULT_IMG_DATA);
                intent.putExtra(EXTRA_RESULT_VIDEO_DATA, CameraHandle.INSTANCE.getVedioName()); // 返回录制的文件名称
                setResult(RESULT_OK, intent);
                finish();
            } else {
                switchMotion(mCurrentMotionIndex + 1);
            }
        }

        @Override
        public void beforeDetect() {
            if (mMotionChanged && mCurrentMotionIndex > -1) {
                if (LibLiveDetect.getInstance().setMotion(mSequences[mCurrentMotionIndex])) {
                    mMotionChanged = false;
                }
            }
        }
    }
}