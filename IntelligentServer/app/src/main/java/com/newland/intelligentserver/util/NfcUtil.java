package com.newland.intelligentserver.util;

import java.io.IOException;
import com.newland.intelligentserver.cons.ConstJdk;
import android.app.Activity;
import android.content.Context;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NfcB;
import android.os.Bundle;
/**
 * Created by Administrator on 2019/6/21.
 */
public class NfcUtil implements ConstJdk
{
    private NfcB nfcB;
    private Context mContext;
    private NfcAdapter nfcAdapter;
    private NfcAdapter.ReaderCallback nfcCallBack;
    private Object lock = new Object();

    // 目前只支持B卡，初始化ReaderCallback接口
//    NfcAdapter.ReaderCallback nfcCallBack=new NfcAdapter.ReaderCallback()
//    {
//
//        @Override
//        public void onTagDiscovered(Tag tag)
//        {
//            String[] s = tag.getTechList();
//            if(s[0].equals(NfcB.class.getName()))
//            {
//                nfcB = NfcB.get(tag);
//                LoggerUtil.v("初始化nfcB    ");
//                synchronized (lock) {
//                    lock.notify();
//                }
//            }
//        }
//    };

    public void initCallBack()
    {
        nfcCallBack = new NfcAdapter.ReaderCallback()
        {

            @Override
            public void onTagDiscovered(Tag tag)
            {
                String[] s = tag.getTechList();
                if(s[0].equals(NfcB.class.getName()))
                {
                    nfcB = NfcB.get(tag);
                    LoggerUtil.v("初始化nfcB    ");
                    synchronized (lock) {
                        lock.notify();
                    }
                }
            }
        };
    }



    public NfcUtil(Context context)// 构造方法
    {
        nfcAdapter = NfcAdapter.getDefaultAdapter(context);
        mContext = context;
        initCallBack();
    }

    /**
     * 连接操作
     * @param readFlag 滤波，设置放置的卡类型
     * @return
     */
    public int nfcConnect(int readFlag,int timeout)
    {
        LoggerUtil.v("开始nfcConnect");
        int ret = JDK_ERR;
        Bundle option = new Bundle();
        option.putInt(NfcAdapter.EXTRA_READER_PRESENCE_CHECK_DELAY, 5000);
        nfcAdapter.enableReaderMode((Activity) mContext, nfcCallBack, readFlag, option);
        synchronized (lock) {
            try {
                lock.wait(timeout*1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        switch (readFlag)
        {
            case NfcAdapter.FLAG_READER_NFC_B:
                if(nfcB!=null)
                {
                    try {
                        nfcB.close();
                        nfcB.connect();
                        ret = JDK_OK;
                        LoggerUtil.v("有进行nfcConnect");
                    } catch (IOException e) {
                        e.printStackTrace();
                        ret = IO_Exception;
                    }
                }
                break;

            default:
                break;
        }
        LoggerUtil.v("没有进行nfcConnect");
        LoggerUtil.v("结束nfcConnect");
        return ret;
    }

    /**
     * 进行NFC卡操作
     * @param card_type
     * @return
     * @throws IOException
     */
    public byte[] nfcRw(int card_type,byte[] cmd)
    {
        byte[] back = "NULL".getBytes();
        switch (card_type)
        {
            case 2:
                try {
                    back = nfcB.transceive(cmd);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            default:
                break;
        }
        return back;
    }

    /**
     * 断开NFC卡连接
     */
    public void nfcDisEnableMode()
    {
        if(nfcAdapter!=null)
            nfcAdapter.disableReaderMode((Activity) mContext);
        if(nfcB!=null)
            try {
                nfcB.close();
            } catch (IOException e) {
                e.printStackTrace();
            }


    }
}
