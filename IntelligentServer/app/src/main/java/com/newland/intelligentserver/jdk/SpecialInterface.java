package com.newland.intelligentserver.jdk;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Camera;
import android.net.Uri;
import android.newland.AnalogSerialManager;
import android.newland.NLUART3Manager;
import android.newland.NlCashBoxManager;
import android.newland.NlManager;
import android.newland.content.NlContext;
import android.newland.ndk.security.NdkSecurityManager;
import android.newland.os.DeviceStatisticsManager;
import android.newland.os.NlBuild;
import android.newland.scan.ScanUtil;
import android.newland.security.SignatureComparison;
import android.newland.telephony.ApnEntity;
import android.newland.telephony.ApnUtils;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;

import com.newland.digled.Digled;
import com.newland.intelligentserver.cons.ConstJdk;
import com.newland.intelligentserver.receiver.ApkBroadCastReceiver;
import com.newland.intelligentserver.receiver.BatteryReceiver;
import com.newland.intelligentserver.util.BackBean;
import com.newland.intelligentserver.util.BitmapTool;
import com.newland.intelligentserver.util.JdkScanUtil;
import com.newland.intelligentserver.util.LoggerUtil;
import com.newland.intelligentserver.util.ReflectUtil;
import com.newland.intelligentserver.util.Tools;
import com.newland.k21controller.CommandInvokeResult;
import com.newland.k21controller.ControllerException;
import com.newland.k21controller.K21CmdInvokeNotifyListener;
import com.newland.k21controller.K21ControllerManager;
import com.newland.k21controller.K21DeviceCommand;
import com.newland.k21controller.K21DeviceResponse;
import android.newland.scan.SoftEngine;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.QName;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

/**
 * JDK特殊接口封装
 * @author 002
 *
 */
public class SpecialInterface implements ConstJdk
{
    int mWlm_Signal = 0;// 用于保存无线的信号强度
    Object lockObj = new Object();

    // 带输出参数的接口特出处理,要传入参数列表
    public byte[] fromSpecialcommandidDo(Context c,Object obj,String[] method_class,int id,List<Object> paraList)
    {
        int iret = 0;
        int method_ret;
        List<ApnEntity> apnlist;
        String version = "";
        BackBean backBean = new BackBean();
        switch (id)
        {
            case 2101://获取所有Apn
                ApnUtils apnUtil_all = (ApnUtils) obj;
                apnlist = apnUtil_all.getAllApnList();// 返回最后一个Apn给python
//                try {
//                    String filepath = "/sdcard/RFTest/getAllApnList.txt";
//                    File file = new File("/sdcard/RFTest/");
//                    if(!file.exists())
//                        file.mkdirs();
//                    FileOutputStream fos = new FileOutputStream(filepath);
//                    //将要写入的字符串转换为byte数组
//                    byte[]  content = Tools.Object2bytes(apnlist);
//                    fos.write(content);//将byte数组写入文件
//                    // 关闭文件输出流
//                    fos.close();
//                    backBean.setRet(0);
//                    backBean.setBuffer(filepath.getBytes());
//                }catch (Exception e) {
//                    LoggerUtil.i(""+e);
//                    backBean.setRet(1);
//                    backBean.setBuffer(new byte[4]);
//                }
                LoggerUtil.v("size:"+apnlist.size());
                return Tools.byteMerger(Tools.byteCommanid_ret(id,iret),Tools.byteMerger_addLen(Tools.byteMerger(Tools.intToBytes(apnlist.size(),4,false),Tools.Object2bytes(apnlist.get(apnlist.size()-1))),1));

            case 2103:// 添加默认的Apn
                ApnUtils apnUtil = (ApnUtils) obj;
                Object apnEntity = ReflectUtil.instanceApnEntity((String)paraList.get(0));
                method_ret = apnUtil.addNewApn((ApnEntity) apnEntity);
                return Tools.byteMerger(Tools.byteCommanid_ret(id,iret),Tools.byteMerger_addLen(Tools.Object2bytes(method_ret), 1));

            //NL_FIRMWARE
            case 3000:case 3001:case 3002:case 3003:case 3004:case 3005:case 3006:case 3007:case 3010:case 3011:case 3012:
            try {
                Field field = NlBuild.VERSION.class.getDeclaredField(method_class[0]);
                version = (String) field.get(NlBuild.VERSION.class);
            } catch (NoSuchFieldException e) {
                iret = NoSuch_Field_Error;
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                iret = Illegal_Access_Exception;
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                iret = Illegal_Argument_Exception;
                e.printStackTrace();
            }
            return Tools.byteMerger(Tools.byteCommanid_ret(id,iret),Tools.byteMerger_addLen(Tools.Object2bytes(version), 1));

            case 3013:case 3014:case 3015:case 3016:case 3017:case 3018:case 3019:case 3020:case 3021:case 3022:case 3023:case 3024:// android.newland.OS.Build
            case 3008:case 3009:// TUSN|CUSTOMER_ID  类:android.os.NlBuild
            try {
                Class<?> cla = Class.forName(method_class[1]);
                Field field = cla.getDeclaredField(method_class[0]);
                version = (String) field.get(cla);
            } catch (ClassNotFoundException e) {
                iret = Class_NotFound_Exception;
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                iret = NoSuch_Field_Error;
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                iret = Illegal_Access_Exception;
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                iret = Illegal_Argument_Exception;
                e.printStackTrace();
            }
            return Tools.byteMerger(Tools.byteCommanid_ret(id,iret),Tools.byteMerger_addLen(Tools.Object2bytes(version), 1));

            case 5006:// USB串口的读操作
                AnalogSerialManager serialManager = (AnalogSerialManager) obj;
                int lengthMax = (Integer) paraList.get(2);
                int bufLen = (Integer) paraList.get(0);
                byte[] readBuf = new byte[bufLen];
                Arrays.fill(readBuf, (byte) 0);
//                byte[] readBuf = paraList.get(1)==null?null:paraList.get(1).equals("")?"".getBytes():new byte[bufLen];
                method_ret = serialManager.read(readBuf, bufLen, (Integer)paraList.get(3));
                backBean.setRet(method_ret);
                if(method_ret>0)
                    backBean.setBuffer(readBuf);
                else
                    backBean.setBuffer(new byte[4]);
                return Tools.byteMerger(Tools.byteCommanid_ret(id,iret),Tools.byteMerger_addLen(Tools.Object2bytes(backBean),1));

            case 6000:case 6001:case 6002:case 6003:
            return mpos_k21Control(id, obj,paraList);

            case 7002://cashbox的setTimeSec
                NlCashBoxManager nlCashBoxManager = (NlCashBoxManager) obj;
                long setTime = (Integer) paraList.get(0);
                nlCashBoxManager.setTimeSec(setTime);
                return Tools.byteMerger(Tools.byteCommanid_ret(id,iret),Tools.byteMerger_addLen(Tools.Object2bytes(null), 1));

            case 7003://cashbox的getTimeSec
                NlCashBoxManager nlCashBoxManager_get = (NlCashBoxManager) obj;
                long getTime = nlCashBoxManager_get.getTimeSec();
                int getTime_int = new Long(getTime).intValue();
                return Tools.byteMerger(Tools.byteCommanid_ret(id,iret),Tools.byteMerger_addLen(Tools.Object2bytes(getTime_int), 1));

            case 8004:// RS232串口的读操作
                NLUART3Manager nlaurt3Manager = (NLUART3Manager) obj;
                int lengthMax_uart = (Integer) paraList.get(2);
                int bufLen_uart = (Integer) paraList.get(0);
                byte[] readBuf_uart = new byte[2048];
                Arrays.fill(readBuf_uart, (byte) 0);
//                byte[] readBuf_uart = paraList.get(1)==null?null:paraList.get(1).equals("")?"".getBytes():new byte[bufLen_uart];
                method_ret = nlaurt3Manager.read(readBuf_uart, bufLen_uart, (Integer)paraList.get(3));
                backBean.setRet(method_ret);
                if(method_ret>0)
                    backBean.setBuffer(readBuf_uart);
                else
                    backBean.setBuffer(new byte[4]);
                return Tools.byteMerger(Tools.byteCommanid_ret(id,iret),Tools.byteMerger_addLen(Tools.Object2bytes(backBean),1));

            case 9003:// paymentport的操作
                NlManager nlManager = (NlManager) obj;
                int lengthMax_nl = (Integer) paraList.get(2);
                int bufLen_nl = (Integer) paraList.get(0);
                byte[] readBuf_nl = paraList.get(1)==null?null:paraList.get(1).equals("")?"".getBytes():new byte[bufLen_nl];
                method_ret = nlManager.read(readBuf_nl, lengthMax_nl, (Integer)paraList.get(3));
                backBean.setRet(method_ret);
                if(method_ret>0)
                    backBean.setBuffer(readBuf_nl);
                else
                    backBean.setBuffer(new byte[4]);
                return Tools.byteMerger(Tools.byteCommanid_ret(id,iret),Tools.byteMerger_addLen(Tools.Object2bytes(backBean),1));

            case 12000:// 12000和13010本质是一样的
                Digled digLed = (Digled) obj;
                byte[] buf = paraList.get(1)==null?null:new byte[(Integer)paraList.get(0)];
                method_ret = digLed.digLedVer(buf);
                backBean.setRet(method_ret);
                if(method_ret==0)
                    backBean.setBuffer(buf);
                return Tools.byteMerger(Tools.byteCommanid_ret(id,iret),Tools.byteMerger_addLen(Tools.Object2bytes(backBean), 1));

            case 13010:// 获取扫码的thk88的sn号
                LoggerUtil.i("getThk88ID");
                backBean.setBuffer(new byte[4]);
                ScanUtil scanUtil = (ScanUtil)obj;
                byte[] sn_buf = paraList.get(1)==null?null:paraList.get(1).equals("")?"".getBytes():new byte[(Integer)paraList.get(0)];
                method_ret = scanUtil.getThk88ID(sn_buf);
                backBean.setRet(method_ret);
                if(method_ret==0)
                    backBean.setBuffer(sn_buf);
                return Tools.byteMerger(Tools.byteCommanid_ret(id,iret),Tools.byteMerger_addLen(Tools.Object2bytes(backBean), 1));

            case 13002:// doscan文件流的方式解析图片
                String path = (String)paraList.get(0);
                InputStream inputStream = path==null?null:path.startsWith("http")?BitmapTool.bitmap2InputStream(BitmapTool.getImage(path)):BitmapTool.bitmap2InputStream(BitmapTool.getDiskBitmap(path));
                String code_result = ScanUtil.doScan(inputStream);
                if(code_result == null)
                    code_result = "NULL";
                return Tools.byteMerger(Tools.byteCommanid_ret(id,iret),Tools.byteMerger_addLen(code_result.getBytes(),2));

            case 13014:// YUV方式调用camera解析
                backBean = new JdkScanUtil().scanYUVDecode(c,(int)paraList.get(0),(int)paraList.get(1));
                return Tools.byteMerger(Tools.byteCommanid_ret(id,iret),Tools.byteMerger_addLen(Tools.Object2bytes(backBean), 1));

            case 13015:// RGB方式解析图片
                backBean = new JdkScanUtil().scanRgbDecode(c,(String) paraList.get(0),(int)paraList.get(1));
                return Tools.byteMerger(Tools.byteCommanid_ret(id,iret),Tools.byteMerger_addLen(Tools.Object2bytes(backBean), 1));

            case 14000:// init_x509
                String ifnew = (String)paraList.get(1);
                if (ifnew == null)
                    method_ret = SignatureComparison.init_x509((String)paraList.get(0))?0:1;
                else method_ret = SignatureComparison.init_x509((String)paraList.get(0), (String)paraList.get(1));
                return Tools.byteMerger(Tools.byteCommanid_ret(id,iret),Tools.byteMerger_addLen(Tools.Object2bytes(method_ret), 1));

            case 15003:// getDeviceStatisticsInfo 统计服务,获取整个xml文件
                DeviceStatisticsManager deviceManager = (DeviceStatisticsManager) obj;
                int mode = 0; // 0-可正常获取的节点，1-特殊处理的节点或者无法获取的节点，2-获取的节点数据太大，3-产生错误
                try {
                    if(paraList == null) {
                        version = deviceManager.getDeviceStatisticsInfo();
                        String filepath = "/sdcard/getDeviceStatisticsInfo.txt";
////                    File file = new File("/sdcard/RFTest/");
////                    if(!file.exists())
////                        file.mkdirs();
                        FileOutputStream fos = new FileOutputStream(filepath);
//                    //将要写入的字符串转换为byte数组
                        byte[]  content = Tools.Object2bytes(version);
                        fos.write(content);//将byte数组写入文件
////                    // 关闭文件输出流
                        fos.close();
                        mode = 1;
                    }else{
                        String tag = (String) paraList.get(0);
                        version = deviceManager.getDeviceStatisticsInfo(tag);
                        if (tag.equals("app_info")) {
                            Document doc = null;
                            try {
                                doc = DocumentHelper.parseText(version);
                            } catch (DocumentException e) {
                                LoggerUtil.e("parse text error : " + e);
                            }
                            Element rootElement = doc.getRootElement();
                            Map<String,Object> appInfoMap = new HashMap<String,Object>();
                            element2Map(appInfoMap,rootElement);
                            version = (String) appInfoMap.get("app_cnt");
                            mode = 1;
                        }
                        else if (version == null){
                            mode = 0;
                            version = "no such node";
                        }
                        else if(version.length()>2048)
                            mode = 1;
                        else if (version.equals("")) {
                            mode = 0;
                            version = "no such node";
                        }
                        else mode = 0;
                    }
                    switch (mode){
                        case 0:
                            backBean.setRet(0);
                            backBean.setBuffer(version.getBytes());
                            break;
                        case 1:
                            backBean.setRet(1);
                            if (paraList == null)
                                backBean.setBuffer(new byte[4]);
                            else if (paraList.get(0).equals("app_info"))
                                backBean.setBuffer(version.getBytes());
                            else backBean.setBuffer(new byte[4]);
                            break;
                    }
                }catch (Exception e) {
                    LoggerUtil.i("ret（2）error"+e);
                    backBean.setRet(2);//2-出现错误
                    backBean.setBuffer(new byte[4]);
                }
                return Tools.byteMerger(Tools.byteCommanid_ret(id,iret),Tools.byteMerger_addLen(Tools.Object2bytes(backBean),1));


            case 15004:// getSecTamperStatus
                NdkSecurityManager ndkManager = (NdkSecurityManager)obj;
                int[] status = paraList.get(0)==null?null:new int[1];
                method_ret = ndkManager.getSecTamperStatus(status);
                backBean.setRet(method_ret);
                if(method_ret==0)
                    backBean.setBuffer(new byte[]{(byte) status[0]});
                return Tools.byteMerger(Tools.byteCommanid_ret(id,iret),Tools.byteMerger_addLen(Tools.Object2bytes(backBean), 1));

            case 21004:// 获取无线信号强度
                final TelephonyManager telephonyManager = (TelephonyManager) c.getSystemService(Context.TELEPHONY_SERVICE);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Looper.prepare();
                        telephonyManager.listen(new PhoneStateListener()
                        {
                            @Override
                            public void onSignalStrengthsChanged(SignalStrength signalStrength) {
                                super.onSignalStrengthsChanged(signalStrength);
                                mWlm_Signal = signalStrength.getGsmSignalStrength();
                                synchronized (lockObj)
                                {
                                    lockObj.notify();
                                }
                            }
                        }, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
                        Looper.loop();
                    }
                }).start();
                synchronized (lockObj)
                {
                    try{
                        lockObj.wait(30*1000);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
                return Tools.byteMerger(Tools.byteCommanid_ret(id,iret),Tools.byteMerger_addLen(Tools.Object2bytes(mWlm_Signal), 1));

            case 23000:
                String type = (String) paraList.get(0);
                version = Environment.getExternalStoragePublicDirectory(type).getPath();
                return Tools.byteMerger(Tools.byteCommanid_ret(id, iret),Tools.byteMerger_addLen(Tools.Object2bytes(version), 1));

            case 23001:
                String str_para1 = (String) paraList.get(0);
                version = c.getExternalFilesDir(str_para1).getPath();
                return Tools.byteMerger(Tools.byteCommanid_ret(id, iret),Tools.byteMerger_addLen(Tools.Object2bytes(version), 1));

            case 24000:// 注册电池广播
                batteryRegist(c, (BatteryReceiver) obj);
                return Tools.byteMerger(Tools.byteCommanid_ret(id,iret),new byte[4]);

            case 24001:// 注销电池广播
                batteryUnRegist(c, (BatteryReceiver) obj);
                return Tools.byteMerger(Tools.byteCommanid_ret(id,iret),new byte[4]);

            case 27000:// install_apk
                Intent intent = new Intent((String)paraList.get(0));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setDataAndType(Uri.fromFile(new File((String)paraList.get(1))),"application/vnd.android.package-archive");
                c.startActivity(intent);
                return Tools.byteMerger(Tools.byteCommanid_ret(id,iret),new byte[4]);

            case 27001:
                LoggerUtil.d("卸载apk="+paraList.get(0)+"==="+paraList.get(1));

                Uri uri6 = Uri.parse("package:"+(String)paraList.get(1));
                Intent intent6 = new Intent((String)paraList.get(0), uri6);
                c.startActivity(intent6);
                return Tools.byteMerger(Tools.byteCommanid_ret(id,iret),new byte[4]);

            case 27002:
                apkRegist(c, (ApkBroadCastReceiver)obj, (String[])paraList.get(0));
                return Tools.byteMerger(Tools.byteCommanid_ret(id,iret),new byte[4]);

            case 27003:
                apkUnRegist(c, (ApkBroadCastReceiver)obj);
                return Tools.byteMerger(Tools.byteCommanid_ret(id,iret),new byte[4]);

            default:
                break;
        }
        return null;
    }

    private byte[] mpos_k21Control(int command_id,Object obj,List<Object> paraList)
    {
        int iret=0;
        K21ControllerManager k21_obj = (K21ControllerManager) obj;
        switch (command_id) {
            case 6000:// connect
                try {
                    k21_obj.connect();
                } catch (ControllerException e) {
                    e.printStackTrace();
                    iret = Mpos_Controller_Exception;
                }
                return Tools.byteMerger(Tools.byteCommanid_ret(command_id,iret),new byte[4]);

            case 6001:// sendCmd 超时时间3s
                K21DeviceResponse resp1 = k21_obj.sendCmd(new K21DeviceCommand((byte[]) paraList.get(0)), paraList.get(1).equals("NULL")?null:listener);
                if(resp1.getInvokeResult()==CommandInvokeResult.SUCCESS)
                    return Tools.byteMerger(Tools.byteCommanid_ret(command_id,resp1.getInvokeResult().ordinal()),Tools.byteMerger_addLen(resp1.getResponse(), 2));
                else
                    return Tools.byteMerger(Tools.byteCommanid_ret(command_id,resp1.getInvokeResult().ordinal()),new byte[4]);

            case 6002:// sendCmd 超时时间由参数决定
                TimeUnit timeUnit;
                if(paraList.get(2).equals("s"))
                    timeUnit = TimeUnit.SECONDS;
                else if(paraList.get(2).equals("ms"))
                    timeUnit = TimeUnit.MILLISECONDS;
                else if(paraList.get(2).equals("min"))
                    timeUnit = TimeUnit.MINUTES;
                else
                    timeUnit = null;
                K21DeviceResponse resp2 = k21_obj.sendCmd(new K21DeviceCommand((byte[]) paraList.get(0)), ((Integer)paraList.get(1)).intValue(), timeUnit, paraList.get(3).equals("NULL")?null:listener);
                if(resp2.getInvokeResult()==CommandInvokeResult.SUCCESS)
                    return Tools.byteMerger(Tools.byteCommanid_ret(command_id,resp2.getInvokeResult().ordinal()),Tools.byteMerger_addLen(resp2.getResponse(), 2));
                else
                    return Tools.byteMerger(Tools.byteCommanid_ret(command_id,resp2.getInvokeResult().ordinal()),new byte[4]);

            case 6003:
                k21_obj.close();
                return Tools.byteMerger(Tools.byteCommanid_ret(command_id,iret),new byte[4]);

            default:
                break;
        }
        return null;
    }

    K21CmdInvokeNotifyListener listener =new K21CmdInvokeNotifyListener() {

        @Override
        public void notify(K21DeviceResponse response) {

        }
    };

    /**
     * 电池广播注册
     */
    public void batteryRegist(Context c,BatteryReceiver batteryReceiver)
    {
        IntentFilter intent1 = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        IntentFilter intent2 = new IntentFilter(Intent.ACTION_POWER_CONNECTED);
        c.registerReceiver(batteryReceiver, intent1);
        c.registerReceiver(batteryReceiver, intent2);
    }

    /**电池广播注销*/
    public void batteryUnRegist(Context c,BatteryReceiver batteryReceiver)
    {
        c.unregisterReceiver(batteryReceiver);
    }

    /***
     * apk广播注册
     * @param c
     * @param apkReceiver
     */
    public void apkRegist(Context c, ApkBroadCastReceiver apkReceiver,String[] actions)
    {
        for(String action:actions)
        {
            LoggerUtil.d("action:"+action);
            IntentFilter intentFilter = new IntentFilter(action);
            c.registerReceiver(apkReceiver, intentFilter);
        }
        LoggerUtil.i("apkRegist succ");
    }

    public void apkUnRegist(Context c,ApkBroadCastReceiver apkReceiver)
    {
        if(apkReceiver!=null)
        {
            c.unregisterReceiver(apkReceiver);
            LoggerUtil.i("apkUnRegist succ");
        }
    }

    /**
     * 使用递归调用将多层级xml转为map
     * @param map
     * @param rootElement
     */
    public static void element2Map(Map<String, Object> map, Element rootElement) {
        // 获得当前节点的子节点
        List<Element> elements = rootElement.elements();

        LoggerUtil.d(",element2Map:"+elements.size());
        if (elements.size() == 0) {
            // 没有子节点说明当前节点是叶子节点，直接取值
            map.put(rootElement.getName(), rootElement.getText());
        }/* else if (elements.size() == 1) {
			// 只有一个子节点说明不用考虑list的情况，继续递归
			Map<String, Object> tempMap = new HashMap<String, Object>();
			element2Map(tempMap, elements.get(0));
			map.put(rootElement.getName(), tempMap);
		}*/ else {
            // 多个子节点的话就要考虑list的情况了，特别是当多个子节点有名称相同的字段时
            Map<String, Object> tempMap = new HashMap<String, Object>();
            for (Element element : elements) {
                tempMap.put(element.getName(), null);
            }
            Set<String> keySet = tempMap.keySet();
            for (String string : keySet) {
                Namespace namespace = elements.get(0).getNamespace();
                List<Element> sameElements = rootElement.elements(new QName(
                        string, namespace));
                // 如果同名的数目大于1则表示要构建list
                if (sameElements.size() > 1) {
                    List<Map> list = new ArrayList<Map>();
                    for (Element element : sameElements) {
                        Map<String, Object> sameTempMap = new HashMap<String, Object>();
                        element2Map(sameTempMap, element);
                        list.add(sameTempMap);
                    }
                    map.put(string, list);
                } else {
                    // 同名的数量不大于1直接递归
                    if (sameElements.get(0).elements().size() == 0) {
                        // 没有子节点说明当前节点是叶子节点，直接取值,不再递归
                        map.put(string, sameElements.get(0).getText());
                    } else {
                        // 接下来还有子节点则继续递归
                        Map<String, Object> sameTempMap = new HashMap<String, Object>();
                        element2Map(sameTempMap, sameElements.get(0));
                        map.put(string, sameTempMap);
                    }
                }
            }
        }
    }

    /**
     * 获取xml的某节点内容
     * @param node
     * @param nodeName
     */
    public Element listNodes(Element node, String nodeName) {
        System.out.println("当前节点的名称：：" + node.getName());
        if (node.getName().equals(nodeName))
            return node;
        else {
            if (!(node.getTextTrim().equals("")))
                System.out.println("文本内容：：：：" + node.getText());
            // 当前节点下面子节点迭代器
            Iterator<Element> it = node.elementIterator();
            // 遍历
            while (it.hasNext()) {
                // 获取某个子节点对象
                node = it.next();
                // 对子节点进行遍历
                node = listNodes(node,nodeName);
                if (node.getName().equals(nodeName))
                    break;
            }
        }
        return node;
    }
}

