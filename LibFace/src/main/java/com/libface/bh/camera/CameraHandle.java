package com.libface.bh.camera;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.View;

import java.util.List;

public enum CameraHandle
{
  INSTANCE;

  public static final int DEFAULT_PREVIEW_WIDTH = 640;
  public static final int DEFAULT_PREVIEW_HEIGHT = 480;
  private Camera mCamera;
  private CameraInfo mCameraInfo;
  private OnCameraListener mListener;
  private RecordingUtil recordingUtil;

  public void initRecording(int duration, String vedioSavePath)
  {
    this.recordingUtil = new RecordingUtil(duration, vedioSavePath);
  }

  public void setPreviewView(final PreviewView previewView)
  {
    if (previewView == null) {
      return;
    }
    previewView.addSurfaceCallback(new Callback()
    {
      @TargetApi(Build.VERSION_CODES.GINGERBREAD)
      @RequiresApi(api = Build.VERSION_CODES.FROYO)
      public void surfaceCreated(SurfaceHolder holder)
      {
        CameraHandle.this.openCamera(holder);
        CameraHandle.this.updateCameraParameters(previewView);
      }

      public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

      public void surfaceDestroyed(SurfaceHolder holder)
      {
        CameraHandle.this.releaseCamera();
      }
    });
  }

  public void setOnCameraListener(OnCameraListener listener)
  {
    this.mListener = listener;
  }

  public int getCameraOrientation()
  {
    if (this.mCameraInfo == null) {
      return -1;
    }
    if(this.mCameraInfo.orientation==0){//实体机器(orientation=0)与手机情况不一致，为快速开发，这里暂时判死
      return 270;
      //0 miss  90 unknow  180 miss 270 miss
    }else{
      return this.mCameraInfo.orientation;
    }
//    return this.mCameraInfo.orientation;
  }

  private void releaseCamera()
  {
    if (this.mCamera == null) {
      return;
    }
    try
    {
      this.mCamera.setPreviewCallback(null);
      this.mCamera.stopPreview();
      this.mCamera.release();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    this.mCamera = null;
    if (this.recordingUtil != null) {
      this.recordingUtil.stopVideoRecoder();
    }
  }

  @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
  private void openCamera(SurfaceHolder holder)
  {
    releaseCamera();

    CameraInfo info = new CameraInfo();
    for (int i = 0; i < Camera.getNumberOfCameras(); i++)
    {
      Camera.getCameraInfo(i, info);
      if (info.facing == 1) {
        try
        {
          this.mCamera = Camera.open(i);
          this.mCameraInfo = info;
        }
        catch (RuntimeException e)
        {
          e.printStackTrace();
          if (this.mCamera != null)
          {
            this.mCamera.release();
            this.mCamera = null;
          }
        }
      }
    }
    if (this.mCamera == null) {
      try
      {
        this.mCamera = Camera.open(0);
        this.mCameraInfo = info;
      }
      catch (RuntimeException e)
      {
        e.printStackTrace();
        if (this.mCamera != null)
        {
          this.mCamera.release();
          this.mCamera = null;
        }
      }
    }
    if (this.mCamera == null)
    {
      if (this.mListener != null) {
        this.mListener.onError(CameraError.OPEN_CAMERA);
      }
      return;
    }
    try
    {
      this.mCamera.setPreviewDisplay(holder);
    }
    catch (Exception ex)
    {
      releaseCamera();
      if (this.mListener != null) {
        this.mListener.onError(CameraError.OPEN_CAMERA);
      }
    }
  }

  @RequiresApi(api = Build.VERSION_CODES.FROYO)
  private void updateCameraParameters(PreviewView previewView)
  {
    if (this.mCamera == null) {
      return;
    }
    try
    {
      Parameters parameters = this.mCamera.getParameters();
      parameters.setPreviewFormat(ImageFormat.NV21);
      parameters.setPreviewSize(DEFAULT_PREVIEW_WIDTH, DEFAULT_PREVIEW_HEIGHT);
      if ((parameters.getMinExposureCompensation() < 0) &&
        (parameters.getMaxExposureCompensation() > 0) &&
        (Math.abs(parameters.getMinExposureCompensation()) == parameters
        .getMaxExposureCompensation())) {
        parameters.setExposureCompensation(0);
      }
      if (parameters.getSupportedFocusModes().contains(Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
        parameters.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
      }
      parameters.setSceneMode(Parameters.SCENE_MODE_AUTO);
      if (previewView.getScreenOrientation() != 2)
      {
        parameters.set("orientation", Parameters.SCENE_MODE_PORTRAIT);
        parameters.set("rotation", 90);
        if ((this.mCameraInfo.facing == CameraInfo.CAMERA_FACING_FRONT) &&
          (this.mCameraInfo.orientation == 90)) {
          this.mCamera.setDisplayOrientation(270);
        } else {
          this.mCamera.setDisplayOrientation(90);
        }
      }
      else
      {
        parameters.set("orientation", Parameters.SCENE_MODE_LANDSCAPE);
//        this.mCamera.setDisplayOrientation(0);//实体机器orientation与手机情况不一致，为快速开发，这里改为90
        this.mCamera.setDisplayOrientation(90);
      }
      previewView.updatePreviewSize(DEFAULT_PREVIEW_WIDTH, DEFAULT_PREVIEW_HEIGHT);

      this.mCamera.setParameters(parameters);
      this.mCamera.setPreviewCallback(new PreviewCallback()
      {
        public void onPreviewFrame(byte[] data, Camera camera)
        {
          if ((CameraHandle.this.mListener != null) &&
            (CameraHandle.this.recordingUtil != null)) {
            CameraHandle.this.recordingUtil.videoRecoder(data, true);
          }
          CameraHandle.this.mListener.onCameraDataFetched(data);
        }
      });
      this.mCamera.startPreview();
      if (this.recordingUtil != null) {
        this.recordingUtil.initVideoEncode((Activity)previewView.getContext(), DEFAULT_PREVIEW_WIDTH,
          DEFAULT_PREVIEW_HEIGHT, getCameraOrientation());
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  public void preparedRecoder()
  {
    if (this.recordingUtil == null) {
      return;
    }
    this.recordingUtil.prepared();
  }

  public String getVedioName()
  {
    if (this.recordingUtil == null) {
      return null;
    }
    return this.recordingUtil.getFileName();
  }
}
