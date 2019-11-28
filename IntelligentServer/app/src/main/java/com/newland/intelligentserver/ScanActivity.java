package com.newland.intelligentserver;

import android.app.Activity;
import android.hardware.Camera;
import android.newland.scan.ScanUtil;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.newland.intelligentserver.util.JdkScanUtil;
import com.newland.intelligentserver.util.LoggerUtil;

import java.io.IOException;

/**
 * Created by Administrator on 2019/6/28.
 */

public class ScanActivity extends Activity {
    private SurfaceView mSurfaceView;
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private int previewWidth,previewHeight;
    private ScanUtil mScanUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scan_layout);
        mSurfaceView = findViewById(R.id.surface);
        mHolder = mSurfaceView.getHolder();
        mHolder.addCallback(mSurfaceCallBack);
        previewWidth = 640;
        previewHeight = 480;
        mScanUtil = new ScanUtil(this);
        mScanUtil.initDecode(scanCallBack);
    }

    ScanUtil.ResultCallBack scanCallBack = new ScanUtil.ResultCallBack() {
        @Override
        public void onResult(int eventCode, int codeType, byte[] data1, byte[] data2, int length) {
            LoggerUtil.v("eventCode="+eventCode+",codeType="+codeType+"data="+new String(data1));
            if(eventCode==1)
            {
                JdkScanUtil.gBackBean.setBuffer(data1);
                synchronized (JdkScanUtil.gLockObj)
                {
                    JdkScanUtil.gLockObj.notify();
                }
                releaseCamera();
                scanStopDecode();
                ScanActivity.this.finish();
            }

        }
    };

    private SurfaceHolder.Callback mSurfaceCallBack = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            LoggerUtil.v("surfaceCreated");
            mCamera = Camera.open();
            setStartPreview(mCamera,holder);
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            LoggerUtil.v("SurfaceHolder.CallBack");
            mCamera.stopPreview();
            setStartPreview(mCamera,holder);
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            LoggerUtil.v("surfaceDestroyed");
            releaseCamera();
        }
    };

    private Camera.PreviewCallback MyPreviewCallBack = new Camera.PreviewCallback() {
        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
            if(mScanUtil!=null)
            {
                LoggerUtil.v("PreviewCallback");
                mScanUtil.startYUVDecode(data,previewWidth,previewHeight);
            }
        }
    };

    /**设置预览内容，开始预览*/
    private void  setStartPreview(Camera camera,SurfaceHolder holder)
    {
        try{
            camera.setPreviewDisplay(holder);
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setPreviewSize(previewWidth,previewHeight);
            mCamera.setParameters(parameters);
            /*系统默认camera是横屏的*/
            camera.setDisplayOrientation(90);
            camera.setPreviewCallback(MyPreviewCallBack);
            camera.startPreview();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void releaseCamera()
    {
        LoggerUtil.v("releaseCamera");
        if(mCamera!=null)
        {
            mCamera.stopPreview();/*取消预览功能*/
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }
    }

    private void scanStopDecode()
    {
        if(mScanUtil!=null)
        {
            mScanUtil.stopDecode();
            mScanUtil = null;
        }
    }
}
