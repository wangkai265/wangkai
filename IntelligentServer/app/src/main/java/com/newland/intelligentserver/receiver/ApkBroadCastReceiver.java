package com.newland.intelligentserver.receiver;

import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.newland.security.CertificateInfo;
import android.util.Log;

/**
 * Created by Administrator on 2019/6/21.
 */
public class ApkBroadCastReceiver extends BroadcastReceiver
{
    public final int APK_INSTALL = 100;
    public final int APK_UNINSTALL = 101;
    private String installName;
    private String unintallName;
    private int installResp;
    private int uninstallResp;
    private int count=-1;
    private Map<Integer,String> nameMap=new HashMap<Integer,String>();
    private Map<Integer,Integer> resultMap=new HashMap<Integer,Integer>();

    // 初始化证书名CN,默认为""
    private String init_CN(Context c)
    {
        String cn ="";
        CertificateInfo certificateInfo = new CertificateInfo(c);
        X509Certificate  x509=certificateInfo.getCertificateInfo();
        String[] info=null;
        if (x509 != null)
        {
            info = x509.getSubjectDN().getName().split(",");
            for (int i = 0; i < info.length; i++)
            {
                if (info[i].contains("CN="))
                {
                    cn= info[i].substring(3);
                }
            }
        }
        return cn;
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {

        Log.e("ApkBroadCastReceiver",  "进入广播count="+count);
        String customerId = "";//NlBuild.CUSTOMER_ID;
        String cn = "";//init_CN(context);
        // 安装操作
        if (intent.getAction().equals("android.intent.action.INSTALL_APP"))
        {
            if(installName!=null)
            {
                Log.d("INSTALL_APP", installName);
            }
            else
            {
                Log.d("INSTALL_APP", "????");
            }

            if(installName==null||installName.equals(intent.getStringExtra("file"))==false)
            {
                //美团固件且安装了美团证书时，安装app返回广播的respCode类型为Int,邮储类似
                if(customerId.equals("MeiTuan")&&cn.equals("MeiTuan")||customerId.equals("PSBC")&&cn.equals("root"))
                    installResp = intent.getIntExtra("respCode", -10086);//美团固件
                else
                    installResp = Integer.parseInt(intent.getStringExtra("respCode"));
                // 目前安装apk的包名
                installName = intent.getStringExtra("file");
                Log.e("INSTALL_APP", installName + "  "+installResp);
            }
        } else if (intent.getAction().equals("android.intent.action.DELETE_APP"))
        {
            if(unintallName==null||unintallName.equals(intent.getStringExtra("packageName"))==false)
            {
                // 目前卸载apk的包名
                unintallName = intent.getStringExtra("packageName");
                uninstallResp = Integer.parseInt(intent.getStringExtra("respCode"));
                // 接收一次的值就好了
                Log.e("DELETE_APP", unintallName + ""+  uninstallResp);
            }
        }

        // 安装操作
        if(intent.getAction().equals("android.intent.action.INSTALL_APP_HIDE"))
        {
            count++;
            Log.e("ApkBroadCastReceiver",  "进入隐式安装广播count="+count);
//			if (installName == null || installName.equals(intent.getStringExtra("file")) == false)
//			{
            //美团固件且安装了美团证书时，安装app返回广播的respCode类型为Int,邮储类似
            if(customerId.equals("MeiTuan")&&cn.equals("MeiTuan")||customerId.equals("PSBC")&&cn.equals("root"))
                installResp = intent.getIntExtra("respCode", -100086);// 美团固件
            else
                installResp = Integer.parseInt(intent.getStringExtra("respCode"));
            // 目前隐式安装apk的包名
            installName = intent.getStringExtra("file");
            Log.e("INSTALL_APP_HIDE", installName + "  "+installResp);
            Log.e("INSTALL_APP_HIDE", "count="+count);
            resultMap.put(count, installResp);
            nameMap.put(count, installName);
//			}
            // 1表示第一次，2表示第二次
        }
        else if(intent.getAction().equals("android.intent.action.DELETE_APP_HIDE"))
        {

            if(unintallName==null||unintallName.equals(intent.getStringExtra("packageName"))==false)
            {
                // 目前隐式卸载apk的包名
                unintallName = intent.getStringExtra("packageName");
                uninstallResp = Integer.parseInt(intent.getStringExtra("respCode"));
                Log.e("INSTALL_APP_HIDE", unintallName +"");
            }
        }
    }

    public void setUnintallName(String unintallName) {
        this.unintallName = unintallName;
    }

    public int getResp(int flag)
    {
        switch (flag) {
            case APK_INSTALL:
                return installResp;

            case APK_UNINSTALL:
                return uninstallResp;
        }
        return installResp;
    }

    public int getResp(int flag,int count)
    {
        switch (flag) {
            case APK_INSTALL:
                return resultMap.get(count);

            case APK_UNINSTALL:
                return uninstallResp;
        }
        return resultMap.get(count);
    }

    public String getPackName(int flag)
    {
        switch (flag) {
            case APK_INSTALL:
                return installName;

            case APK_UNINSTALL:
                return unintallName;
        }
        return installName;
    }

    public String getPackName(int flag,int count)
    {
        switch (flag) {
            case APK_INSTALL:
                return nameMap.get(count);

            case APK_UNINSTALL:
                return unintallName;
        }
        return nameMap.get(count);
    }
    public int getHideInstallCount()
    {
        return count;
    }
    public void setHideInstallCount(int count)
    {
        this.count=count;
        resultMap.clear();
        nameMap.clear();
    }
}
