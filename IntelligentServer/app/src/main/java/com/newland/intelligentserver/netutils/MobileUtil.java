package com.newland.intelligentserver.netutils;

import java.lang.reflect.Method;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

/**
 *  无线工具类
 *
 */
public class MobileUtil
{

    private static MobileUtil mobileUtil = null;

    private TelephonyManager mTelephonyManager = null;
    private Context context = null;
    int signStrength;
    public static MobileUtil getInstance(Context context)
    {
        if (mobileUtil == null) {
            mobileUtil = new MobileUtil(context);
        }
        return mobileUtil;
    }

    public MobileUtil(Context context)
    {
        this.context=context;
        mTelephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    }
    /**
     * 判断MOBILE网络是否可用
     *
     * @return
     */
    public boolean wlm_isConnected()
    {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkINfo = cm.getActiveNetworkInfo();
        if (networkINfo != null && networkINfo.getType() == ConnectivityManager.TYPE_MOBILE) {
            return true;
        }
        return false;
    }
    /**
     * 打开网络
     */
    public void wlm_openNet(){
        if(android.os.Build.VERSION.SDK_INT>19)
        {
            android.newland.telephony.TelephonyManager teleManager = new android.newland.telephony.TelephonyManager(context);
            teleManager.setMobileDataEnabled(true);
        }
        else
        {
            try {
                ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

                Class<?> ownerClass = mConnectivityManager.getClass();

                Class<?>[] argsClass = new Class[1];
                argsClass[0] = boolean.class;
                Method method = ownerClass.getMethod("setMobileDataEnabled",argsClass);
                method.invoke(mConnectivityManager, true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * 关闭网络
     */
    public void wlm_closeNet(){
        if(android.os.Build.VERSION.SDK_INT>19)
        {
            android.newland.telephony.TelephonyManager teleManager = new android.newland.telephony.TelephonyManager(context);
            teleManager.setMobileDataEnabled(false);
        }
        else
        {
            try {

                ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

                Class<?> ownerClass = mConnectivityManager.getClass();

                Class<?>[] argsClass = new Class[1];
                argsClass[0] = boolean.class;

                Method method = ownerClass.getMethod("setMobileDataEnabled",argsClass);
                method.invoke(mConnectivityManager, false);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * 关闭其他网络
     */
    public void wlm_closeOther()
    {
        if(WifiUtil.getInstance(context).wifi_isConnected())
            WifiUtil.getInstance(context).wifi_closeNet();
    }

    /**
     * 获取移动网络的ip
     * @return
     */
    public  String getIp()
    {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address)
                    {
                        // if (!inetAddress.isLoopbackAddress() && inetAddress
                        // instanceof Inet6Address) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (Exception e) {
        }
        return "";
    }
    /**
     * SIM卡是否存在
     *
     * @return true 存在且可用 false 不存在或者存在不可用
     */
    public boolean isSimReady() {
        int simState = mTelephonyManager.getSimState();
        if (simState == TelephonyManager.SIM_STATE_READY)
            return true;
        return false;
    }

    /**
     * 获取SIM卡状态的描述
     *
     * @return
     */
    public int wlm_getSimState() {
        int simStateNo = mTelephonyManager.getSimState();
        return simStateNo;
    }

    public String wlm_getSimSerialNumber()
    {
        String serialNum = mTelephonyManager.getSimSerialNumber();
        return serialNum;
    }

    public int wlm_getNetworkType()
    {
        int type = mTelephonyManager.getNetworkType();
        switch (type)
        {
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN:
                type = 1;
                break;

            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                type = 3;
                break;

            case TelephonyManager.NETWORK_TYPE_LTE:
                type = 5;
                break;

            default:
                type = 0;
                break;
        }
        return type;
    }

    public int wlm_netWorkType()
    {
        int ret;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if(networkInfo==null)
            ret = -1;
        ret = networkInfo.getType();
        return ret;
    }

//	public int getSignStrength()
//	{
//		return signStrength;
//	}
}
