package com.newland.intelligentserver.util;

import android.content.Context;
import android.content.Intent;
import android.newland.scan.ScanUtil;
import android.os.HandlerThread;
import com.newland.intelligentserver.ScanActivity;
import com.newland.intelligentserver.cons.ConstJdk;

/**
 * Created by 002 on 2019/6/26.
 */

public class JdkScanUtil implements ConstJdk
{
    public static BackBean gBackBean = new BackBean();// 用于保存扫码的回调结果
    public static Object gLockObj = new Object();
    private ScanUtil mScanUtil;

    private HandlerThread cameraHandler = new HandlerThread("using open camera");

    ScanUtil.ResultCallBack scanCallBack = new ScanUtil.ResultCallBack() {
        @Override
        public void onResult(int eventCode, int codeType, byte[] data1, byte[] data2, int length) {
            LoggerUtil.v("eventCode="+eventCode+",codeType="+codeType+",data="+new String(data1));
            gBackBean.setRet(eventCode);
            gBackBean.setBuffer(data1);
            synchronized (gLockObj)
            {
                gLockObj.notify();
            }
        }
    };

    public BackBean scanRgbDecode(Context context,String pic_path, int timeout)
    {
        int ret = JDK_OK;
        gBackBean.setRet(ret);
        gBackBean.setBuffer(new byte[4]);
        mScanUtil = new ScanUtil(context);
        if((ret = mScanUtil.initDecode(scanCallBack))!=JDK_OK)
        {
            gBackBean.setRet(ret);
            mScanUtil.stopDecode();// 后置操作
            return gBackBean;
        }
        if((ret = mScanUtil.startRGBDecode(pic_path))==JDK_SCAN_PARSE_FAIL)
        {
            gBackBean.setRet(ret);
            mScanUtil.stopDecode();// 后置操作
            return gBackBean;
        }
        synchronized (gLockObj)
        {
            try
            {
                gLockObj.wait(timeout*1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        mScanUtil.stopDecode();
        mScanUtil = null;
        return gBackBean;
    }

    public BackBean scanYUVDecode(Context c,int cameraId,int timeout)
    {
        gBackBean.setRet(JDK_OK);
        gBackBean.setBuffer(new byte[4]);
        Intent intent = new Intent(c, ScanActivity.class);
        c.startActivity(intent);
        LoggerUtil.v("wait阻塞操作");
        synchronized (gLockObj)
        {
            try{
                gLockObj.wait(timeout*1000);
            }catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        LoggerUtil.v("notify操作");
        return gBackBean;
    }
}
