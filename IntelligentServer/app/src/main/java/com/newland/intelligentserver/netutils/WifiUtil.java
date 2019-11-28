package com.newland.intelligentserver.netutils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.newland.intelligentserver.util.LoggerUtil;
import com.newland.intelligentserver.util.ParamEnum.WIFI_AP_STATE;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.AuthAlgorithm;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.os.Build;
import android.os.Parcelable;
import android.os.SystemClock;
import android.text.TextUtils;
import android.text.format.Formatter;
/**
 * wifi工具类
 * @author zhengxq
 * 2016-4-6 下午4:52:28
 */
public class WifiUtil
{
    //private final String TAG = WifiUtil.class.getSimpleName();
    private static WifiUtil wifiUtil = null;
    private int wcgID;
    private WifiManager mWifiManager;
    private WifiInfo mWifiInfo;
    private List<ScanResult> mWifiList;
    private List<WifiConfiguration> mWifiConfiguration;
    WifiLock mWifiLock;
    // private MobileUtil mobileUtil;
    private Context context = null;
    public static WifiUtil getInstance(Context context)
    {
        if (wifiUtil == null) {
            wifiUtil = new WifiUtil(context);
        }
        return wifiUtil;
    }
    public WifiUtil(Context context) {
        this.context=context;
        mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        mWifiInfo = mWifiManager.getConnectionInfo();
        //  mobileUtil=MobileUtil.getInstance(context);
    }
    /**
     * 判断WIFI网络是否可用
     *
     * @return
     */
    public boolean wifi_isConnected()
    {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (activeNetworkInfo == null || !activeNetworkInfo.isAvailable()) {
                return false;
            } else {// 可能联网
                int networkType = activeNetworkInfo.getType();
                if (networkType == ConnectivityManager.TYPE_WIFI) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 打开wifi
     */
    public boolean wifi_openNet() {
        return mWifiManager.setWifiEnabled(true);
    }

    /**
     * 关闭wifi
     */
    public boolean wifi_closeNet() {
        return mWifiManager.setWifiEnabled(false);
    }

    /**
     * 关闭其他wifi
     */
    public void wifi_closeOther() {
        if(MobileUtil.getInstance(context).wlm_isConnected())
            MobileUtil.getInstance(context).wlm_closeNet();
        //mobileUtil.closeNet();
    }

    /**
     * 断开已连接的wifi
     *
     * @return
     */
    public boolean disconnectWifi() {
        return mWifiManager.disableNetwork(wcgID);
    }

    /**
     * 获取AP的信号强度
     *
     * @return AP强度值
     */
    public int wifi_getSignStrength() {
        mWifiInfo = mWifiManager.getConnectionInfo();
        // 获取AP强度
        // return mWifiManager.calculateSignalLevel(mWifiInfo.getRssi(), 5);
        return mWifiInfo.getRssi();
    }

    public String getIp() {
        int paramInt=mWifiInfo.getIpAddress();
        return (paramInt & 0xFF) + "." + (0xFF & paramInt >> 8) + "." + (0xFF & paramInt >> 16) + "."
                + (0xFF & paramInt >> 24);
    }

    private BroadcastReceiver wifibroadcastReceiver = new BroadcastReceiver()
    {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(action)) {
                mWifiList = mWifiManager.getScanResults();
                synchronized (context) {
                    context.notify();
                }
            } else if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
                Parcelable parcelableExtra = intent
                        .getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (null != parcelableExtra) {
                    NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;
                    State state = networkInfo.getState();
                    boolean isConnected = state == State.CONNECTED;
                    if (isConnected) {
                        gIsWifiConnected = true;
                    }
                }
            }
        };
    };


//    /**
//     * 注册广播
//     */
//    public void registWifiScan()
//    {
//    	IntentFilter intentFilter = new IntentFilter();
//		intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
//		context.registerReceiver(wifibroadcastReceiver, intentFilter);
//    }
    /**
     * 注册wifi连接广播
     */
    public void registWifiConnect()
    {
        gIsWifiConnected = false;
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        context.registerReceiver(wifibroadcastReceiver, intentFilter);
    }

    /**
     * 注销wifi广播
     */
    public void unRegistWifiConnect() {
        if(wifibroadcastReceiver != null){
            if(context != null){
                context.unregisterReceiver(wifibroadcastReceiver);
            }
        }
    }


    /**
     * 检测目前的wifi状态
     * @return
     */
    public int wifi_checkState() {
        return mWifiManager.getWifiState();
    }

    public void acquireWifiLock() {
        mWifiLock.acquire();
    }

    public void releaseWifiLock() {
        if (mWifiLock.isHeld()) {
            mWifiLock.acquire();
        }
    }

    public void creatWifiLock() {
        mWifiLock = mWifiManager.createWifiLock("Test");
    }

    public List<?> getConfiguration() {
        return mWifiConfiguration;
    }

    public void connectConfiguration(int index) {
        if (index > mWifiConfiguration.size()) {
            return;
        }
        mWifiManager.enableNetwork(mWifiConfiguration.get(index).networkId,
                true);
    }
    /**
     * 开启扫描并返回扫描列表
     * @return
     */
    public boolean wifi_startScan()
    {
        boolean flag = mWifiManager.startScan();

        mWifiConfiguration = mWifiManager.getConfiguredNetworks();
        return flag;
    }
    //    public void scanBroadCast(){
//    	 //注册
//        registWifiScan();
//        //扫描完成
//        synchronized (context) {
//			try {
//				context.wait();
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//		}
//        //注销
//        unRegistWifiBroadCast();
//    }
    public List<ScanResult> getWifiList() {
        return mWifiManager.getScanResults();
    }

    public StringBuilder lookUpScan() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < mWifiList.size(); i++) {
            stringBuilder.append("Index_" + Integer.valueOf(i + 1).toString() + ":");
            stringBuilder.append((mWifiList.get(i)).toString());
            stringBuilder.append("/n");
        }
        return stringBuilder;
    }

    public String wifi_getMacAddress() {
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.getMacAddress();
    }
//
//    public String getBSSID() {
//        return (mWifiInfo == null) ? "NULL" : mWifiInfo.getBSSID();
//    }
//
//    public int getIPAddress() {
//        return (mWifiInfo == null) ? 0 : mWifiInfo.getIpAddress();
//    }

    public String intToIp(int i)
    {

        return (i & 0xFF)+ "." + ((i >> 8 ) & 0xFF)+ "." + ((i >> 16 ) & 0xFF) +"."+((i >> 24 ) & 0xFF );
    }

//    public int getNetworkId() {
//        return (mWifiInfo == null) ? 0 : mWifiInfo.getNetworkId();
//    }
//
//    public String getWifiInfo() {
//        return (mWifiInfo == null) ? "NULL" : mWifiInfo.toString();
//    }
    /**
     * 清除所有wifi记录，保证WiFi压力的准确性
     * add by zhangxinj on 2017/7/14
     */
    public void clearAllSavaWifi(){
        SystemClock.sleep(8000);
        List<WifiConfiguration> config = (List<WifiConfiguration>) mWifiManager.getConfiguredNetworks();
        if (config != null) {
            // 在 Android 6以上,无法使用 removeNetwork
            // 这个公开的方法删除一个已有的配置,虽然在删除后,在系统中显示已经被删除,
            // 但是当重新关闭再打开 Wifi 开关时,会发现被删除的配置由系统进行创建.这里,使用系统的隐藏方法 forget
            // 进行删除也无法删除，只能用disableNetwork方法
            // 使用使其他wifi不可用
            for (int i = 0; i < config.size(); i++) {
                // mWifiManager.disableNetwork(config.get(i).networkId);
                mWifiManager.removeNetwork(config.get(i).networkId);
            }
        }

    }
    public int addNet(WifiConfiguration wifiConfiguration){
        return mWifiManager.addNetwork(wifiConfiguration);
    }

    public static boolean gIsWifiConnected = false;
    /**
     * 返回wifi的连接状态
     * @param wcg
     * @return
     */
    public boolean addNetwork(int id)
    {
        boolean b=mWifiManager.enableNetwork(id, true); // 打开添加的网络
        int counts = 60; // 等待时间为60s
        registWifiConnect();
        while (gIsWifiConnected == false && counts > 0)
        {
            try {
                Thread.sleep(1000);
                counts--;
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
        unRegistWifiConnect();
        return gIsWifiConnected;
    }
    public String getConnetWifiBSSID(){
        WifiInfo info = mWifiManager.getConnectionInfo();
        return info.getBSSID();
    }

    public WifiConfiguration config;
    // 动态ip
    public WifiConfiguration CreateWifiInfo(WifiPara wifiPara)
    {
        config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        // 将中文的SSID转换为16进制的形式
        config.SSID = "\"" + wifiPara.getSsid() + "\"";
        config.BSSID = wifiPara.getBssid();
//         WifiConfiguration tempConfig = this.IsExsits(wifiPara.getSsid());
//         if(tempConfig != null) {
        //在安卓7.0已经不能修改更新或者删除一个不是自己创建的wifi
//             mWifiManager.updateNetwork(tempConfig.networkId);
//         }
        switch (wifiPara.getSec()) {
            case 2:
                config.hiddenSSID = wifiPara.isScan_ssid();
                config.wepKeys[0] = "\"\"";
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                config.wepTxKeyIndex = 0;
                break;

            case 1:
                config.hiddenSSID = wifiPara.isScan_ssid();
                if (wifiPara.getPasswd().length() != 0) {
                    int length = wifiPara.getPasswd().length();
                    // WEP-40, WEP-104, and 256-bit WEP
                    // (WEP-232?)
                    if ((length == 10 || length == 26 || length == 58)&& wifiPara.getPasswd().matches("[0-9A-Fa-f]*")) {
                        config.wepKeys[0] = wifiPara.getPasswd();
                    } else {
                        config.wepKeys[0] = '"' + wifiPara.getPasswd() + '"';
                    }
                }
                config.allowedKeyManagement.set(KeyMgmt.NONE);
                config.allowedAuthAlgorithms.set(AuthAlgorithm.OPEN);
                config.allowedAuthAlgorithms.set(AuthAlgorithm.SHARED);
                config.wepTxKeyIndex=0;
                break;

            case 0:
                //密码长度超过63位去掉双引号才可以连接，具体原因待查 by zhangxinj on 2017/11/1
                if(wifiPara.getPasswd().length()>63)
                {
                    config.preSharedKey =wifiPara.getPasswd();
                }

                else
                    config.preSharedKey = "\"" + wifiPara.getPasswd() + "\"";
                config.hiddenSSID = wifiPara.isScan_ssid();
                config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
                config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                config.status = WifiConfiguration.Status.ENABLED;
                break;

            default:
                break;
        }
        return config;
    }

    public  WifiConfiguration IsExsits(String SSID)
    {
        WifiConfiguration wc=null;
        SystemClock.sleep(8 * 1000);
        List<WifiConfiguration> existingConfigs = mWifiManager.getConfiguredNetworks();
        if (existingConfigs != null) {
            //在高平台 removeNetwork方法对于不是本apk连接创建的wifi已经不起作用了，改成disableNetwork方法
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                for (WifiConfiguration existingConfig : existingConfigs) {
                    mWifiManager.disableNetwork(existingConfig.networkId);
                    if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
                        wc= existingConfig;
                    }
                }
            }else
            {
                for (WifiConfiguration existingConfig : existingConfigs) {
                    mWifiManager.removeNetwork(existingConfig.networkId);
//    				if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
//    					LoggerUtil.e("已存在的名称：" + existingConfig.SSID);
//    					wc= existingConfig;
//    				}
                }
            }
        }else {
        }

        return wc;
    }

    // 这种判断是有问题的，需要修改
//	public boolean isWifiConnected(Context context)
//	{
//		if (context != null) {
//			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
//					.getSystemService(Context.CONNECTIVITY_SERVICE);
//			NetworkInfo mWiFiNetworkInfo = mConnectivityManager
//					.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
//			if (mWiFiNetworkInfo != null)
//			{
//				return mWiFiNetworkInfo.isAvailable();
//			}
//		}
//		return false;
//	}

    /**
     * static"or"DHCP"
     * @param assign
     * @param wifiConf
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws NoSuchMethodException
     * @throws NoSuchFieldException
     * @throws SecurityException
     */
    public static void setIpAssignment(String assign, WifiConfiguration wifiConf)
            throws SecurityException, NoSuchFieldException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException
    {
        setEnumField(wifiConf, assign, "ipAssignment");
    }

	/*public static void setStaticIpConfig(InetAddress gateway,InetAddress addr, ArrayList<InetAddress> dnsServers,int prefixLength,WifiConfiguration wifiConf)
			throws SecurityException,
			IllegalArgumentException, NoSuchFieldException,
			IllegalAccessException, NoSuchMethodException,
			ClassNotFoundException, InstantiationException,
			InvocationTargetException {
		// new staticConfiguration
		Class staticConfiguration = Class.forName("android.net.StaticIpConfiguration");
		Object staticInstance = staticConfiguration.newInstance();
		// set static ip
		Class laClass = Class.forName("android.net.LinkAddress");
		Constructor laConstructor = laClass.getConstructor(new Class[] {
				InetAddress.class, int.class });
		Object linkAddress = laConstructor.newInstance(addr, prefixLength);
		// 设置IP地址
		Field f1 = staticConfiguration.getDeclaredField("ipAddress");
		f1.set(staticInstance, linkAddress);
		// 设置网关
		Field f2 = staticConfiguration.getDeclaredField("gateway");
		f2.set(staticInstance, gateway);
		// 设置DNS服务器
		ArrayList<Object> dnsServer1 = (ArrayList<Object>) GetField(staticConfiguration, "dnsServers");
		dnsServer1.clear();
		for (int i = 0; i < dnsServers.size(); i++) {
			dnsServer1.add(dnsServers.get(i));
		}
//		Field f3 = staticConfiguration.getDeclaredField("dnsServers");
//		f3.set(staticInstance,dnsServers);

		// set staticConfiguration
		Method method = wifiConf.getClass().getMethod("setStaticIpConfiguration",staticInstance.getClass());
		Object object = method.invoke(wifiConf,staticInstance);
	}*/

    //5.x静态设置IP的方式
    @SuppressWarnings("unchecked")
    public static void setStaticIpConfiguration(WifiConfiguration config,InetAddress ipAddress,int prefixLength,InetAddress gateway,InetAddress[] dns)
            throws ClassNotFoundException,IllegalAccessException,IllegalArgumentException,InvocationTargetException,NoSuchMethodException,NoSuchFieldException,InstantiationException
    {
//		Object ipAssignment = getEnumValue("android.net.IpConfiguration", new Object[]{ipAssignment});
        Object staticIpConfig = newInstance("android.net.StaticIpConfiguration");
        Object linkAddress = newInstance("android.net.LinkAddress", new Class[]{InetAddress.class,int.class}, new Object[]{ipAddress,prefixLength});
        SetFiled(staticIpConfig, "ipAddress", linkAddress);
        SetFiled(staticIpConfig, "gateway", gateway);

        ArrayList<Object> dnsServer = (ArrayList<Object>) GetField(staticIpConfig, "dnsServers");
        dnsServer.clear();
        for (int i = 0; i < dns.length; i++) {
            dnsServer.add(dns[i]);
        }
        // 好像没有这个方法呀
        callMethod(config, "setStaticIpConfiguration", new String[]{"android.net.StaticIpConfiguration"}, new Object[]{staticIpConfig});

//		manager.updateNetwork(config);
//		manager.saveConfiguration();

    }


//	/**
//	 * 设置代理信息 exclList是添加不用代理的网址用的 （用于实验DNS的缓存）
//	 * */
//	   public void setHttpPorxySetting(Context context,String host, int port, List<String> exclList)
//	           throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException,
//	           IllegalAccessException, NoSuchFieldException {
//	       WifiManager wifiManager =(WifiManager)context.getSystemService(Context.WIFI_SERVICE);
//	       WifiConfiguration config = getCurrentWifiConfiguration(wifiManager);
//	       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
//	           mInfo = ProxyInfo.buildDirectProxy(host,port);
//	       }
//	       if (config != null){
//	          Class clazz = Class.forName("android.net.wifi.WifiConfiguration");
//	           Class parmars = Class.forName("android.net.ProxyInfo");
//	           Method method = clazz.getMethod("setHttpProxy",parmars);
//	           method.invoke(config,mInfo);
//	           Object mIpConfiguration = getDeclaredFieldObject(config,"mIpConfiguration");
//
//	           setEnumField(mIpConfiguration, "STATIC", "proxySettings");
//	           setDeclardFildObject(config,"mIpConfiguration",mIpConfiguration);
//	           //save the settings
//	           wifiManager.updateNetwork(config);
//	           wifiManager.disconnect();
//	           wifiManager.reconnect();
//	       }
//
//	   }

    private static Object newInstance(String className) throws ClassNotFoundException,InstantiationException,IllegalAccessException,
            NoSuchMethodException,IllegalArgumentException,InvocationTargetException
    {
        // 这是什么表达？
        return newInstance(className, new Class[0], new Object[0]);
    }

    private static Object newInstance(String className,Class[] parameterClasses,Object[] parameterValues)
            throws NoSuchMethodException,InstantiationException,IllegalAccessException,IllegalArgumentException,
            InvocationTargetException,ClassNotFoundException
    {
        Class class_instance = Class.forName(className);
        Constructor constructor = class_instance.getConstructor(parameterClasses);
        return constructor.newInstance(parameterValues);
    }

    @SuppressWarnings("unchecked")
    private static Object getEnumValue(String enumClassName,String enumValue) throws ClassNotFoundException
    {
        Class enumClass = (Class) Class.forName(enumClassName);
        return Enum.valueOf(enumClass, enumValue);
    }

    private static void SetFiled(Object object,String fieldName,Object value)
            throws IllegalAccessException,IllegalArgumentException,NoSuchFieldException
    {
        Field field = object.getClass().getDeclaredField(fieldName);
        field.set(object, value);
    }

    private static Object GetField(Object object,String fieldName)
            throws IllegalAccessException,IllegalArgumentException,NoSuchFieldException
    {
        Field field = object.getClass().getDeclaredField(fieldName);
        Object out = field.get(object);
        return out;
    }

    private static void callMethod(Object object,String methodName,String[] parameterTypes,Object[] parameterValues)
            throws ClassNotFoundException,IllegalThreadStateException,InvocationTargetException,NoSuchMethodException, IllegalAccessException
    {
        Class[] parametercls = new Class[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++)
            parametercls[i] = Class.forName(parameterTypes[i]);
        Method method  = object.getClass().getDeclaredMethod(methodName, parametercls);
        method.invoke(object, parameterValues);
    }

    public static void setIpAddress(InetAddress addr, int prefixLength,
                                    WifiConfiguration wifiConf) throws SecurityException,
            IllegalArgumentException, NoSuchFieldException,
            IllegalAccessException, NoSuchMethodException,
            ClassNotFoundException, InstantiationException,
            InvocationTargetException {
        Object linkProperties = getField(wifiConf, "linkProperties");
        if (linkProperties == null)
            return;
        Class laClass = Class.forName("android.net.LinkAddress");
        Constructor laConstructor = laClass.getConstructor(new Class[] {
                InetAddress.class, int.class });
        Object linkAddress = laConstructor.newInstance(addr, prefixLength);

        ArrayList mLinkAddresses = (ArrayList) getDeclaredField(linkProperties,
                "mLinkAddresses");
        mLinkAddresses.clear();
        mLinkAddresses.add(linkAddress);
    }

    public static void setGateway(InetAddress gateway,
                                  WifiConfiguration wifiConf) throws SecurityException,
            IllegalArgumentException, NoSuchFieldException,
            IllegalAccessException, ClassNotFoundException,
            NoSuchMethodException, InstantiationException,
            InvocationTargetException {
        Object linkProperties = getField(wifiConf, "linkProperties");
        if (linkProperties == null)
            return;
        Class routeInfoClass = Class.forName("android.net.RouteInfo");
        Constructor routeInfoConstructor = routeInfoClass
                .getConstructor(new Class[] { InetAddress.class });
        Object routeInfo = routeInfoConstructor.newInstance(gateway);

        ArrayList mRoutes = (ArrayList) getDeclaredField(linkProperties,
                "mRoutes");
        mRoutes.clear();
        mRoutes.add(routeInfo);
    }

    public static void setDNS(InetAddress dns, WifiConfiguration wifiConf)
            throws SecurityException, IllegalArgumentException,
            NoSuchFieldException, IllegalAccessException {
        Object linkProperties = getField(wifiConf, "linkProperties");
        if (linkProperties == null)
            return;

        ArrayList<InetAddress> mDnses = (ArrayList<InetAddress>) getDeclaredField(linkProperties, "mDnses");
//		mDnses.clear(); // or add a new dns address , here I just want to replace DNS1
        mDnses.add(dns);
    }

    public static InetAddress getDNS(WifiConfiguration wifiConf) {
        InetAddress address = null;
        try {
            Object linkProperties = getField(wifiConf, "linkProperties");

            if (linkProperties != null){
                ArrayList<InetAddress> mDnses = (ArrayList<InetAddress>) getDeclaredField(linkProperties, "mDnses");
                if(mDnses != null && mDnses.size() > 0){
                    address = (InetAddress)mDnses.get(0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return address;
    }

    public static Object getField(Object obj, String name)
            throws SecurityException, NoSuchFieldException,
            IllegalArgumentException, IllegalAccessException {
        Field f = obj.getClass().getField(name);
        Object out = f.get(obj);
        return out;
    }

    public static Object getDeclaredField(Object obj, String name)
            throws SecurityException, NoSuchFieldException,
            IllegalArgumentException, IllegalAccessException {
        Field f = obj.getClass().getDeclaredField(name);
        f.setAccessible(true);
        Object out = f.get(obj);
        return out;
    }

    public static void setEnumField(Object obj, String value, String name)
            throws SecurityException, NoSuchFieldException,NoSuchMethodException,
            IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        // 4.x
        if(android.os.Build.VERSION.SDK_INT < 22)
        {
            Field f = obj.getClass().getField(name);
            f.set(obj, Enum.valueOf((Class<Enum>)f.getType(), value));
        }
        // 5.x
        else
        {
            // set static type
            Method method = obj.getClass().getMethod("getIpConfiguration");
            Object object = method.invoke(obj);
            Field f = object.getClass().getDeclaredField(name);
            f.set(object, Enum.valueOf((Class<Enum>) f.getType(), value));
        }
    }

    // support SDK 4.x
    public boolean saveStaticWifiConfig(WifiPara wifiPara,int networkPrefixLength) throws SecurityException, NoSuchFieldException, NoSuchMethodException,
            IllegalArgumentException, IllegalAccessException, InvocationTargetException, UnknownHostException, ClassNotFoundException, InstantiationException
    {
        InetAddress intetAddress  = InetAddress.getByName(wifiPara.getLocalIp());
//    	int intIp = inetAddressToInt(intetAddress);
        WifiConfiguration historyWifiConfig = IsExsits(wifiPara.getSsid());
        boolean connectBefore=true;
        int netId;
        if(historyWifiConfig == null){
            connectBefore=false;
            historyWifiConfig = CreateWifiInfo(wifiPara);
        }else{
            if(!TextUtils.isEmpty(wifiPara.getPasswd())){
                historyWifiConfig.preSharedKey = "\""+ wifiPara.getPasswd() + "\"";
            }
        }
        // 4.x 平台
        if(android.os.Build.VERSION.SDK_INT < 22)
        {
//    		String dns = (intIp & 0xFF ) + "." + ((intIp >> 8 ) & 0xFF) + "." + ((intIp >> 16 ) & 0xFF) + ".1";
            setIpAssignment("STATIC", historyWifiConfig);
            setIpAddress(intetAddress, networkPrefixLength, historyWifiConfig);
            setGateway(InetAddress.getByName(wifiPara.getGateWay()), historyWifiConfig);
            setDNS(InetAddress.getByName(wifiPara.getDns1()), historyWifiConfig);
            setDNS(InetAddress.getByName(wifiPara.getDns2()), historyWifiConfig);
        }
        // 5.x 平台
        else
        {
            setIpAssignment("STATIC", historyWifiConfig);
//    		ArrayList<InetAddress> dnsServer = new ArrayList<InetAddress>();
//    		dnsServer.add(InetAddress.getByName(wifiPara.getDns1()));
//    		dnsServer.add(InetAddress.getByName(wifiPara.getDns2()));
            InetAddress[] dnsServer = {InetAddress.getByName(wifiPara.getDns1()),InetAddress.getByName(wifiPara.getDns2())};
            setStaticIpConfiguration( historyWifiConfig, intetAddress, 24, InetAddress.getByName(wifiPara.getGateWay()), dnsServer);
//        	setStaticIpConfig(InetAddress.getByName(wifiPara.getGateWay()),intetAddress,dnsServer, networkPrefixLength, historyWifiConfig);
        }
//        mWifiManager.removeNetwork(historyWifiConfig.networkId);
        if(!connectBefore){
            netId =mWifiManager.addNetwork(historyWifiConfig);
        }else{
            netId=historyWifiConfig.networkId;
        }
        boolean net=addNetwork(historyWifiConfig.networkId);
//        int netId = mWifiManager.addNetwork(historyWifiConfig);
//		mWifiManager.enableNetwork(netId, true);
//        mWifiManager.updateNetwork(historyWifiConfig); //apply the setting
//        mWifiManager.saveConfiguration();
        return net;
    }

    public WifiConfiguration createComWifiConfig(String ssid,String pwd){
    	WifiConfiguration wc = new WifiConfiguration();
        wc.SSID = "\"" + ssid + "\"";
        wc.preSharedKey = "\""+ pwd + "\"";
        wc.hiddenSSID = true;
        wc.status = WifiConfiguration.Status.ENABLED;
        wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        wc.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        return wc;
    }

public static InetAddress getIpAddress(WifiConfiguration wifiConf) {
    InetAddress address = null;
    try {
        Object linkProperties = getField(wifiConf, "linkProperties");
        if (linkProperties == null)
            return null;

        if (linkProperties != null){
            ArrayList mLinkAddresses = (ArrayList) getDeclaredField(linkProperties,"mLinkAddresses");
            if(mLinkAddresses != null && mLinkAddresses.size() > 0){
                Object linkAddressObj = mLinkAddresses.get(0);
                address = (InetAddress)linkAddressObj.getClass().getMethod("getAddress",  new Class[]{}).invoke(linkAddressObj, new  Object[]{});
            }
        }

    } catch (Exception e) {
        e.printStackTrace();
    }
    return address;
}

    public static InetAddress getGateway(WifiConfiguration wifiConf)  {
        InetAddress address = null;
        try {
            Object linkProperties = getField(wifiConf, "linkProperties");

            if (linkProperties != null){
                ArrayList mRoutes = (ArrayList) getDeclaredField(linkProperties,"mRoutes");
                if(mRoutes != null && mRoutes.size() > 0){
                    Object linkAddressObj = mRoutes.get(0);
                    address = (InetAddress)linkAddressObj.getClass().getMethod("getGateway",  new Class[]{}).invoke(linkAddressObj, new  Object[]{});
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return address;
    }

    public void getWifiMsg(WifiPara wifiPara)
    {
        DhcpInfo dhcpInfo = mWifiManager.getDhcpInfo();
        wifiPara.setLocalIp(Formatter.formatIpAddress(dhcpInfo.ipAddress));
        wifiPara.setGateWay(Formatter.formatIpAddress(dhcpInfo.gateway));
        wifiPara.setDns1(Formatter.formatIpAddress(dhcpInfo.dns1));
    }

    public final int DefaultTimePiece = 100;
    /**
     * PING测试<p>
     *
     * 3G上网卡拨号需要一段时间
     *
     * @param serverIp	服务端IP地址
     * @param timeout	超时时间
     * @throws IOException
     */
    public boolean pingTest(String serverIp, int timeout) {
        Date startTime = new Date();
        Date endTime = null;
        long result = 0;
        try {
            do {
                if(ping(serverIp))
                    return true;
                endTime = new Date();
                result = (endTime.getTime()-startTime.getTime())/1000;
                SystemClock.sleep(DefaultTimePiece);
            } while (result < timeout);
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    /**
     * ping ip 地址
     *
     * @param address
     * @return 返回结果
     * @throws IOException
     */
    private boolean ping(String address) throws IOException {
        boolean success = false;

        StringBuilder pingStr = new StringBuilder("ping -c 5 -i 0.5 -s 32 ");
        pingStr.append(address);
        LoggerUtil.v("{pingStr}" + pingStr);
        Process process = Runtime.getRuntime().exec(pingStr.toString());
        InputStream is = process.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        String line = br.readLine();	// 第一句显示：PING 61.135.169.125 (61.135.169.125) 32(60) bytes of data.

        while((line = br.readLine()) != null){
            LoggerUtil.v(line);
            if(line.contains("bytes from "+address)){
                success = true;
                break;
            }else{
                success = false;
                break;
            }
        }
        br.close();
        process.destroy();
        return success;
    }

    // add by 20150612
    // wifi热点开关 1 打开wifi ap 0 关闭wifi ap
    public boolean setWifiApEnabled(boolean enabled, String ssid, String key,int sec, boolean isHid)
    {
        if (enabled) { // disable WiFi in any case
            // wifi和热点不能同时打开，所以打开热点的时候需要关闭wifi
            mWifiManager.setWifiEnabled(false);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1){
            return false;
        }else{
            try {
                // 热点的配置类
                WifiConfiguration apConfig = new WifiConfiguration();
                apConfig.hiddenSSID = isHid;
                // 配置热点的名称(可以在名字后面加点随机数什么的)
                apConfig.SSID = ssid;
                // 配置热点的密码
                apConfig.preSharedKey = key;

                apConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
                // 加密模式
                // 安全：WPA2_PSK
                if (sec == 0) {
                    apConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                } else if (sec == 1|| sec == 2) {
                    apConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                }
                // 通过反射调用设置热点
                Method method = mWifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, Boolean.TYPE);
                // 返回热点打开状态
                return (Boolean) method.invoke(mWifiManager, apConfig, enabled);
            } catch (Exception e) {
                return false;
            }
        }
    }

    public int getWifiApState()
    {
        int tmp;
        try {
            Method method = mWifiManager.getClass().getMethod("getWifiApState");
            tmp = ((Integer) method.invoke(mWifiManager));
            // Fix for Android 4
            if (tmp > 10) {
                tmp = tmp - 10;
            }
            return WIFI_AP_STATE.class.getEnumConstants()[tmp].ordinal();
        } catch (Exception e) {
            e.printStackTrace();
            return WIFI_AP_STATE.WIFI_AP_STATE_FAILED.ordinal();
        }
    }
}
