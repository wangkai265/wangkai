package com.newland.intelligentserver;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.newland.AnalogSerialManager;
import android.newland.NLUART3Manager;
import android.newland.NlCashBoxManager;
import android.newland.NlManager;
import android.newland.content.NlContext;
import android.newland.os.DeviceStatisticsManager;
import android.newland.security.SignatureComparison;
import android.os.Bundle;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.net.LocalServerSocket;
import android.net.LocalSocket;
import android.newland.scan.ScanUtil;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.newland.intelligentserver.cons.ConstJdk;
import com.newland.intelligentserver.jdk.JdkCommandId;
import com.newland.intelligentserver.jdk.SpecialInterface;
import com.newland.intelligentserver.ndk.ShowMessage;
import com.newland.intelligentserver.sdk.SdkCommandId;
import com.newland.intelligentserver.sdk.SdkInterface;//调试
import com.newland.intelligentserver.util.BackBean;
import com.newland.intelligentserver.util.ISOUtils;
import com.newland.intelligentserver.util.LoggerUtil;
import com.newland.intelligentserver.util.NfcUtil;
import com.newland.intelligentserver.util.Tools;
import com.newland.mtype.util.Dump;
import com.newland.nlbridge.ndk.JniNdk;
import com.newland.intelligentserver.ndk.ShowMessage;
import android.view.View.OnClickListener;
//import com.newland.ndk.JniNdk;


public class TestLocalSocketActivity extends Activity implements ConstJdk{

    final static String SOCKET_ADDRESS = "adb.socket";
    public String TAG = "TestLocalSocketActivity";
    private final int MAX_SEND_SIZE = 8*1024;
    private LocalSocket mLocalSocket;
    private LocalServerSocket mLocalServerSocket;
    private int typeOfLength = 0;
    private Object paraContent;
    public static int g_Ret = 0;// 调用接口的返回值，默认是成功的
    private SurfaceView mView;
    private SurfaceHolder mHolder;
    private BackBean mBackBean = new BackBean();

    private TextView view;
	private TextView logView;
    private String logStr;
    private ScrollView scrollView;
    public TextView tv_prompt_info;
    public Button bt_sure;
    public MyButtonlistener mButtonListener;
    public ShowMessage mShowMessage;
    public WorkHandler mWorkHandler;


    static {System.loadLibrary("loadNl");}

    public Handler handler = new Handler()
    {
        public void handleMessage(android.os.Message msg)
        {
            switch (msg.what){
                case 1001:
                    mView.setVisibility(View.VISIBLE);
                    LoggerUtil.v("扫码框可见" );
                    break;
                case 1002:
                    mView.setVisibility(View.GONE);
                    LoggerUtil.v("扫码框不可见" );
                    break;
				case 1003:
                    logView.append(logStr);
//                    scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                    break;
            }
        };
    };
	
	    private class WorkHandler extends Handler {
        /** 事件 msg : . */
        private static final int EVENT_SHOW_MESSAGE = 0x01;
        private static final int EVENT_SHOW_MESSAGE_AND_BUTTON = 0x02;
        private static final int EVENT_REMOVE_MESSAGE_AND_BUTTON = 0x03;
        /*-----------------------------------*/
        @Override
        public void handleMessage(Message msg){
            int ret;
            switch ( msg.what) {
                case EVENT_SHOW_MESSAGE:
                    tv_prompt_info.setText((String)msg.obj);
                    break;
                case EVENT_SHOW_MESSAGE_AND_BUTTON:
                    tv_prompt_info.setText((String)msg.obj);
                    bt_sure.setVisibility(View.VISIBLE);
                    break;
                case EVENT_REMOVE_MESSAGE_AND_BUTTON:
                    tv_prompt_info.setText(" ");
                    bt_sure.setVisibility(View.INVISIBLE);
                    break;
                default:
                    Log.w(TAG,"unknown 'what' : " + msg.what);
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mView = (SurfaceView)findViewById(R.id.surfaceView);
        view = (TextView)findViewById(R.id.test);
        mView.setVisibility(View.GONE);
        logView = findViewById(R.id.logView);
        scrollView = findViewById(R.id.scroll);
//        cla = new ScanUtil(this, mView, 0, true, 15000, 1);
//        LoggerUtil.v("结束扫码初始化" );
//        mView.setVisibility(View.VISIBLE);
//        LoggerUtil.v("已更新UI");
//        NlCashBoxManager nlCashBoxManager = (NlCashBoxManager) this.getSystemService(NlContext.CASHBOX_SERVICE);
//        LoggerUtil.v("初始化钱箱");
//        int ret = nlCashBoxManager.getVoltage();
//        LoggerUtil.v("获取钱箱电压");
//        view.setText(""+ret);
        try {
            mLocalServerSocket = new LocalServerSocket(SOCKET_ADDRESS);
            receiverAndsend();
        } catch (IOException e) {
            LoggerUtil.e("create LockServerSocket error!");
            e.printStackTrace();
        }
        tv_prompt_info = (TextView)findViewById(R.id.tv_prompt_info);
        bt_sure = (Button)findViewById(R.id.bt_sure);
        mButtonListener = new MyButtonlistener();
        bt_sure.setOnClickListener(mButtonListener);
        bt_sure.setVisibility(View.INVISIBLE);
        mShowMessage = new ShowMessage(this);
        mWorkHandler = new WorkHandler();
    }
    public void showPromptInfo(String text){
        Message message = mWorkHandler.obtainMessage(mWorkHandler.EVENT_SHOW_MESSAGE);
        message.obj = text;
        mWorkHandler.sendMessage(message);
    }
    public void showPromptInfoandButton(String text){
        Message message = mWorkHandler.obtainMessage(mWorkHandler.EVENT_SHOW_MESSAGE_AND_BUTTON);
        message.obj = text;
        mWorkHandler.sendMessage(message);
    }
    public void removePromptInfoandButton(){
        mWorkHandler.sendEmptyMessage(mWorkHandler.EVENT_REMOVE_MESSAGE_AND_BUTTON);
    }
    class MyButtonlistener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            //相关事件处理
            JniNdk.JNI_ButtonTrigger();
        }
    }

    // 接收到报文后，传给JNI_HandleNDKRequst的indata参数，将接口返回的outData返回
    public void receiverAndsend()
    {
        new Thread() {
            @Override
            public void run()
            {
                while (true) {
                    if (mLocalServerSocket == null)
                        break;
                    try {
                        // 获取接收的LocalSocket
                        mLocalSocket = mLocalServerSocket.accept();
                        LoggerUtil.v("获取接收的LocalSocket");
                    } catch (IOException e) {
                        LoggerUtil.e("accept socket error!");
                        e.printStackTrace();
                        return;
                    }
                    long startTime = System.currentTimeMillis();
                    byte[] inputData = new byte[10 * 1024];
                    int[] outLen = new int[1];
                    outLen[0] = 257 * 1024;
                    try {
                        InputStream inputStream = mLocalSocket.getInputStream();// 接收数据
                        LoggerUtil.v("接收数据");
                        OutputStream outputStream = mLocalSocket.getOutputStream();// 返回数据
                        LoggerUtil.v("返回数据");
                        int inlen = inputStream.read(inputData);
                        byte[] verBytes = new byte[4];
                        System.arraycopy(inputData, 0, verBytes, 0, 4);
                        String verStr = new String(verBytes);
                        LoggerUtil.d("ver="+verStr);
                        byte[] content = new byte[inlen-12];// 先暂时裁剪前面的12个字符
                        System.arraycopy(inputData, 12, content, 0, content.length);
                        int commandid=Tools.byteArrayToInt(Tools.subBytes(content,0,4));//方法名id
                        logStr = ""+commandid+"\n";//测试
                        handler.sendEmptyMessage(1003);//测试
                        if(verStr.equals("JDK1"))// 执行JDK操作
                        {
                            // jdk接口
                            LoggerUtil.v("执行JDK操作");
                            byte[] out = refectJDK(content, content.length);// 返回报文
                            if (out != null){
                                    LoggerUtil.v("outDataLen=" + out.length);
                                    LoggerUtil.v("time="+(System.currentTimeMillis()-startTime)/1000.0);
                                    outputStream.write(out, 0, out.length);
//                                    if(out.length > MAX_SEND_SIZE) // 发送的长度过长，需要分多包发送
//                                    {
//                                        int count = out.length/2048;
//                                        byte[] packsign = "P".getBytes("GBK");
//                                        int index = 0;
//                                        LoggerUtil.v("count="+count);
//                                        for(;index<count;index++)
//                                        {
//                                            if(index == count-1)
//                                            {
//                                                byte[] splitData = new byte[out.length-(2048*index)+9];
//                                                System.arraycopy(packsign, 0, splitData, 0, 1);
//                                                System.arraycopy(Tools.intToBytes(count, 4, false),0, splitData, 1, 4);
//                                                System.arraycopy(Tools.intToBytes(index, 4,false),0, splitData,5,4);
//                                                System.arraycopy(out, index*2048, splitData, 9, out.length-(2048*index));
//                                                LoggerUtil.v("time="+(System.currentTimeMillis()-startTime)/1000.0);
//                                                outputStream.write(splitData, 0, splitData.length);
//                                            }
//                                            else
//                                            {
//                                                byte[] splitData = new byte[2048+9];
//                                                System.arraycopy(packsign, 0, splitData, 0, 1);
//                                                System.arraycopy(Tools.intToBytes(count, 4, false),0, splitData, 1, 4);
//                                                System.arraycopy(Tools.intToBytes(index, 4,false),0, splitData,5,4);
//                                                System.arraycopy(out, index*2048, splitData, 9, 2048);
//                                                LoggerUtil.v("time="+(System.currentTimeMillis()-startTime)/1000.0);
//                                                outputStream.write(splitData, 0, splitData.length);
//                                            }
//
//                                        }
//                                    }
//                                    else
//                                    {
//                                        LoggerUtil.v("time="+(System.currentTimeMillis()-startTime)/1000.0);
//                                        outputStream.write(out, 0, out.length);
//                                    }
//                                    if(out.length>MAX_SEND_SIZE) // 发送的长度过长，需要分多包发送
//                                    {
//                                        int count = out.length/MAX_SEND_SIZE;
//                                        int index=0;
//                                        LoggerUtil.v("count="+count);
//                                        while(count>0)
//                                        {
//                                            LoggerUtil.v("index="+index);
//                                            outputStream.write(out, index*MAX_SEND_SIZE, MAX_SEND_SIZE);
//                                            count--;
//                                            index++;
//                                        }
//                                    }
//                                    else
                                }else{
                                    Log.v("wang", "out=null???");
                            }
                        }
                        else if(verStr.equals("NDK3"))// 执行NDK操作
                        {
                            LoggerUtil.v("执行NDK操作");
//                            for(int i=0;i<content.length;i++)
//                                LoggerUtil.v("content"+i+":"+content[i]);
//                            int ndk_commandid=Tools.byteArrayToInt(Tools.subBytes(content,0,4));//方法名id
//                            if (ndk_commandid == 1003)//
//                            {
//
//                            }
                            byte[] outData = new byte[1024*10];
                            int ret = JniNdk.JNI_HandleNDKRequst(content,content.length,outData,outLen);
//                            int ret = JniNdk.JNI_HandleNDKRequst(content,content.length,outData,outLen);
                            if (outData != null&&ret==0){
                                LoggerUtil.v("outData=" + ISOUtils.hexString(outData));
                                outputStream.write(outData, 0, outLen[0]);
                            }
                            else// 调用出现错误的情况
                            {
                                LoggerUtil.e("handle ndk request error!");
                                System.arraycopy(Tools.subBytes(content,0,4), 0, outData, 0, 4);
                                System.arraycopy(Tools.intToBytes(ret, 4, false), 0, outData, 4, 4);
                                System.arraycopy(new byte[4], 0, outData, 8, 4);
                                outputStream.write(outData, 0, 12);
                            }
                            outData = null;
                        }
                        if(verStr.equals("SDK1"))// 执行SDK操作
                        {
                            // sdk接口
                            byte[] out = refectSDK(content, content.length);// 返回报文
                            if (out != null){
                                LoggerUtil.v("outData=" + ISOUtils.hexString(out));
                                outputStream.write(out, 0, out.length);
                            }else{
                                Log.v("wang", "out=null???");
                            }
                        }
                        mLocalSocket.close();
                    } catch (Exception e) {
                        Log.v("wang", e.getMessage());
                        e.printStackTrace();
                    }
                    inputData = null;
                }
            }
        }.start();
    }



    //通过接口名反射调用jdk接口,执行JDK接口操作
    public byte[] refectJDK(byte[] inData,int inlen)
    {
        g_Ret =0;
        int jdk_commandid=Tools.byteArrayToInt(Tools.subBytes(inData,0,4));//方法名id
        LoggerUtil.v("jdk_commandid："+jdk_commandid );
        //一般接口通过反射，得到参数列表
        List<Object> classList=null,paraList=null;//存放参数类型,存放参数数据
        int startlen=4;
        LoggerUtil.d("startLen="+startlen+",inlen="+inlen);
        while(startlen<inlen)
        {
            if(classList==null)
                classList=new ArrayList<Object> ();
            if(paraList==null)
                paraList=new ArrayList<Object> ();
            Object type=unpack(inData,startlen);
            classList.add(type);
            paraList.add(paraContent);
            startlen=startlen+typeOfLength+1;
            LoggerUtil.d("type="+type.toString()+",paraContent="+paraContent);
        }
        Object cla = null;
        LoggerUtil.v("开始判断jdk_commandid");
        if(jdk_commandid==13000)// 扫码的NLS构造方法，调用完毕后直接返回
        {
//            if(JdkCommandId.gHash_obj.get(JdkCommandId.fromCommandId(jdk_commandid)[1])!=null)// 要先释放扫码操作
//            {
//                LoggerUtil.v("开始释放之前的对象" );
//                ((ScanUtil)JdkCommandId.gHash_obj.get(JdkCommandId.fromCommandId(jdk_commandid)[1])).relese();
//            }
//            surfaceView.setVisibility(View.VISIBLE);
            LoggerUtil.v("开始扫码初始化" );
            handler.sendEmptyMessage(1002);
            cla = new ScanUtil(this, mView, (Integer)paraList.get(0), (Boolean)paraList.get(1), (Integer)paraList.get(2), (Integer)paraList.get(3));
            handler.sendEmptyMessage(1001);
            LoggerUtil.v("结束扫码初始化" );
            JdkCommandId.gHash_obj.put(JdkCommandId.fromCommandId(jdk_commandid)[1], cla);
            return Tools.byteMerger(Tools.byteCommanid_ret(jdk_commandid, g_Ret),new byte[4]);
        }
        else if (jdk_commandid==13006)// 扫码初始化后，直接release会导致服务崩溃
        {
//            handler.sendEmptyMessage(1002);
            ((ScanUtil)JdkCommandId.gHash_obj.get(JdkCommandId.fromCommandId(13000)[1])).release();
            LoggerUtil.v("扫码release" );
            handler.sendEmptyMessage(1002);
        }
        else
        {
            cla =JdkCommandId.fromCommandId2Init(TestLocalSocketActivity.this, (JdkCommandId.fromCommandId(jdk_commandid)[1]));
            if(JdkCommandId.fromSpecialcommandid(jdk_commandid)==null&&cla==null)// 这个固件不支持该类
            {
                return Tools.byteMerger(Tools.byteCommanid_ret(jdk_commandid, Invocation_Target_Exception),new byte[4]);
            }
        }
        /**是带有输出参数的接口，做特殊处理*/
        if(JdkCommandId.fromSpecialcommandid(jdk_commandid)!=null)
        {
            SpecialInterface sp_in = new SpecialInterface();
            return sp_in.fromSpecialcommandidDo(this,cla, JdkCommandId.fromCommandId(jdk_commandid),jdk_commandid,paraList);
        }

        // 反射
        try {
            Class c[] = null;
            Object[] arg = null;
            // 1.参数存在
            if (classList != null) {
                int len = classList.size();
                c = new Class[len];
                arg = new Object[len];

                // 2.根据参数得到相应的 Class的类对象和值
                for (int i = 0; i < len; ++i) {
                    c[i] = (Class) classList.get(i);
                    arg[i] = paraList.get(i);
                }
            }
            if(cla!=null)
            {
                LoggerUtil.i(JdkCommandId.fromCommandId(jdk_commandid)[0]);
                Class<?> clazz =cla.getClass();
                LoggerUtil.v("执行释放扫码操作第一步：" );
                Method method = clazz.getMethod(JdkCommandId.fromCommandId(jdk_commandid)[0], c);
                LoggerUtil.v("执行释放扫码操作第二步：" );
                Object obj = method.invoke(cla, arg);//void返回null
                LoggerUtil.v("执行释放扫码操作第三步：" );
                LoggerUtil.v("打包返回流：" );
                //打包返回流=id+data长度+data,组装返回报文 command_id,Tools.intToBytes(Tools.Object2bytes(obj).length,4,false)
                return Tools.byteMerger(Tools.byteCommanid_ret(jdk_commandid, g_Ret),Tools.byteMerger_addLen(Tools.Object2bytes(obj), 1));
            }else{
                Log.v("wang", "cla=null???");
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            g_Ret = NoSuch_Method_Exception;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            g_Ret = Invocation_Target_Exception;
        }
        return Tools.byteMerger(Tools.byteCommanid_ret(jdk_commandid, g_Ret),new byte[4]);
    }


    //通过接口名反射调用jdk接口,执行SDK接口操作
    public byte[] refectSDK(byte[] inData,int inlen)
    {
        g_Ret =0;
        int sdk_commandid=Tools.byteArrayToInt(Tools.subBytes(inData,0,4));//方法名的id
        LoggerUtil.v("command_id:"+sdk_commandid);
        //一般接口通过反射，得到参数列表
        List<Object> classList=null,paraList=null;//存放参数类型,存放参数数据
        int startlen=4;
        LoggerUtil.d("startLen="+startlen+",inlen="+inlen);
        while(startlen<inlen)
        {
            if(classList==null)
                classList=new ArrayList<Object> ();
            if(paraList==null)
                paraList=new ArrayList<Object> ();
            Object type = unpack(inData,startlen);
            classList.add(type);
            paraList.add(paraContent);
            startlen=startlen+typeOfLength+1;
            LoggerUtil.d("type="+type.toString()+",paraContent="+paraContent);
        }
        Object cla = null;
        cla = SdkCommandId.sdkInterfaceInit(TestLocalSocketActivity.this);
        LoggerUtil.d(cla.toString());

        // 反射
        try {
            Class c[] = null;
            Object[] arg = null;
            // 1.参数存在
            if (classList != null) {
                int len = classList.size();
                c = new Class[len];
                arg = new Object[len];
                // 2.根据参数得到相应的 Class的类对象和值
                for (int i = 0; i < len; ++i) {
                    c[i] = (Class) classList.get(i);
                    arg[i] = paraList.get(i);
                }
            }
            if(cla!=null)
            {
                LoggerUtil.i(SdkCommandId.fromSdkCommandId(sdk_commandid));
                Class<?> clazz =cla.getClass();
                Method method = clazz.getMethod(SdkCommandId.fromSdkCommandId(sdk_commandid), c);
                Object obj = method.invoke(cla, arg);//void返回null
                //打包返回流=id+data长度+data,组装返回报文 command_id,Tools.intToBytes(Tools.Object2bytes(obj).length,4,false)
                return Tools.byteMerger(Tools.byteCommanid_ret(sdk_commandid, g_Ret),Tools.byteMerger_addLen(Tools.Object2bytes(obj), 1));
            }else{
                Log.v("wang", "cla=null???");
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            g_Ret = NoSuch_Method_Exception;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            g_Ret = Invocation_Target_Exception;
        }
        return Tools.byteMerger(Tools.byteCommanid_ret(sdk_commandid, g_Ret),new byte[4]);
    }

    //解包输入流数组取值的开始位置，参数类型，取出的内容
    public Object unpack(byte[] inData,int start){
        char c = (char)(inData[start]&0xFF);
        Log.v("wang", "c="+c);
        if(c=='i'||c=='d')//int
        {
            typeOfLength=4;
            paraContent=Tools.byteArrayToInt(Tools.subBytes(inData,start+1,4));//i的内容
            return int.class;
        }
        if(c=='s')//String
        {
            int len=Tools.byteArrayToInt(Tools.subBytes(inData,start+1,4));//s的长度
            typeOfLength=4+len;
            paraContent=new String(Tools.subBytes(inData,start+1+4,len));//s的内容
            if(paraContent.equals("NULL"))
                paraContent = null;
            return String.class;
        }
        if(c=='S')//String[],上层带，隔开
        {
            int len=Tools.byteArrayToInt(Tools.subBytes(inData,start+1,4));//s的长度
            typeOfLength=4+len;
            String pythonContent = new String(Tools.subBytes(inData,start+1+4,len));
            if(pythonContent.equals("NULL"))
            {
                paraContent = null;
                return (new String[1]).getClass();
            }
            paraContent=pythonContent.split(","); //s的内容
            return paraContent.getClass();
        }
        if(c=='L')// List<>类型
        {
            int len=Tools.byteArrayToInt(Tools.subBytes(inData,start+1,4));//s的长度
            typeOfLength=4+len;
            String pythonContent = new String(Tools.subBytes(inData,start+1+4,len));
            LoggerUtil.v("python="+pythonContent);
            if(pythonContent.equals("NULL"))
            {
                paraContent = null;
                return (new ArrayList<String>()).getClass();
            }
            paraContent= new ArrayList<String>(Arrays.asList(new String(Tools.subBytes(inData,start+1+4,len)).split(",")));
            return paraContent.getClass();
        }
        if(c=='B')//byte[]直接用
        {
            int len=Tools.byteArrayToInt(Tools.subBytes(inData,start+1,4));//byte[]的长度
            typeOfLength=4+len;
            String pythonContent = new String(Tools.subBytes(inData,start+1+4,len));
            if(pythonContent.equals("NULL"))
            {
                paraContent = null;
                return (new byte[1]).getClass();
            }
            paraContent=Tools.subBytes(inData,start+1+4,len) ;//byte[]的内容
            return paraContent.getClass();
        }
        if(c=='c')//与关键字封装层约定用c做boolean类型,内容占位1
        {
            typeOfLength=1;
            paraContent=false;
            if(inData[start+1]==0x01)
                paraContent=true;
            return boolean.class;
        }
        if(c=='o')
        {
            int len=Tools.byteArrayToInt(Tools.subBytes(inData,start+1,4));//s的长度
            typeOfLength=4+len;
            paraContent=new String(Tools.subBytes(inData,start+1+4,len));//s的内容
            if(paraContent.equals("NULL"))
                paraContent = null;
            return String.class;
        }
        return Object.class;
    }
}
