package com.newland.intelligentserver.netutils;

import java.io.IOException;
import java.util.HashMap;
import com.newland.intelligentserver.cons.ConstJdk;
import com.newland.intelligentserver.cons.Lib;
import com.newland.intelligentserver.util.BackBean;
import com.newland.intelligentserver.util.LoggerUtil;
import com.newland.intelligentserver.util.ReflectUtil;
import com.newland.intelligentserver.util.ParamEnum.LinkStatus;
import com.newland.intelligentserver.util.ParamEnum.NetStatus;
import com.newland.intelligentserver.util.ParamEnum.TransStatus;
import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.os.SystemClock;
import android.util.Log;

/**
 * Created by Administrator on 2019/6/21.
 */
public class Layer implements Lib,ConstJdk
{
    /*------------global variables definition-----------------------*/
    private static boolean TCPRESETFLAG = false;
    private final int timeout = 60;

    // 网络
    public HashMap<Integer, LinkStatus> linkStatus = new HashMap<Integer, LinkStatus>();
    public HashMap<Integer, NetStatus> netStatus = new HashMap<Integer, NetStatus>();
    public HashMap<Integer, TransStatus> transStatus = new HashMap<Integer, TransStatus>();
    private HashMap<Integer,WifiPara> objMap = new HashMap<Integer, WifiPara>();
    private HashMap<Integer,SocketUtil> socketUtilMap = new HashMap<Integer, SocketUtil>();


    private Context context;

    public Layer(Context context)
    {
        this.context = context;
    }

    public void initPara(String json,int type,String serverIp,int port,int locate)
    {
        LoggerUtil.d("json="+json);
        if(type==10)
        {
            WifiPara wifiPara = (WifiPara) ReflectUtil.refelctInvoke(json, WifiPara.class.getName());
            objMap.put(locate, wifiPara);
        }
        SocketUtil socketUtil = new SocketUtil(serverIp, port);
        socketUtilMap.put(locate, socketUtil);
        if(socketUtilMap.get(locate)==null)
        {
            LoggerUtil.v("socket is null");
        }
        linkStatus.put(locate, LinkStatus.linkdown);
        netStatus.put(locate, NetStatus.NETDOWN);
        transStatus.put(locate, TransStatus.TRANSDOWN);
    }


    /*---------------functions definition---------------------------*/
    // 建立连接
    public int linkUP(int type,int locate)
    {
		/*process body*/
        if(linkStatus.get(locate) ==LinkStatus.linkup)
        {
            return JDK_OK;
        }
        transStatus.put(locate, TransStatus.TRANSDOWN);
        netStatus.put(locate, NetStatus.NETDOWN);
        return _LinkUp(type,locate);
    }

    private int netUp_WLM(int locate)
    {
		/*private & local definition*/

		/*process body*/
        // 建立socket的网络连接
        netStatus.put(locate, NetStatus.NETUP);
        return JDK_OK;
    }

    // 断开以太网网络连接
    private int netDown_WLM(int locate)
    {
		/*process body*/
        if(linkDown_WLM(locate)!=JDK_OK)
        {
            netStatus.put(locate, NetStatus.NETDOWN);
            return JDK_ERR;
        }
        linkStatus.put(locate, LinkStatus.linkdown);
        netStatus.put(locate, NetStatus.NETDOWN);
        return JDK_OK;
    }

    // 建立无线连接
    private int linkUp_WLM(int locate)
    {
		/*private & local definition*/
        int ret = 0;
        MobileUtil mobileUtil = MobileUtil.getInstance(context);

		/*process body*/
        // 建立连接
        mobileUtil.wlm_openNet();

        // 无线采用循环检测
        long startTime = System.currentTimeMillis();
        boolean flag = false;
        while(getStopTime(startTime)<timeout)
        {
            SystemClock.sleep(500);
            if((flag = mobileUtil.wlm_isConnected()))
                break;
            else
                SystemClock.sleep(500);
        }
        if(flag==false)
        {
            ret = JDK_ERR_TIMEOUT;
            mobileUtil.wlm_closeNet();
        }
        else
            ret = JDK_OK;
        linkStatus.put(locate, LinkStatus.linkup);
        return ret;
    }

    // 断开无线连接
    private int linkDown_WLM(int locate)
    {
		/*private & local definition*/
        int ret = 0;
        MobileUtil mobileUtil = MobileUtil.getInstance(context);

		/*process body*/
        linkStatus.put(locate, LinkStatus.linkdown);

        mobileUtil.wlm_closeNet();
        long startTime = System.currentTimeMillis();
        boolean flag = false;
        // 循环检测60s
        while(getStopTime(startTime)<timeout)
        {
            if((flag = mobileUtil.wlm_isConnected())==false)
            {
                flag = true;
                break;
            }
        }
        ret = flag==false?JDK_ERR:JDK_OK;
        linkStatus.put(locate, LinkStatus.linkdown);
        return ret;
    }

    /**
     * 打开wifi 为了测试wifi性能更加准确 应陈镇江要求 把原来打开——扫描——连接 的操作分开计算时间
     */
    private int wifiOpen() {
        int status = 0;
        long startTime = 0;
        WifiUtil wifiUtil = new WifiUtil(context);
        wifiUtil.wifi_openNet();
        status = wifiUtil.wifi_checkState();
        startTime = System.currentTimeMillis();
        // 还未打开wifi
        switch (wifiUtil.wifi_checkState()) {
            case WIFI_STATE_DISABLED:
                while (getStopTime(startTime) < 10) {
                    if ((status = wifiUtil.wifi_checkState()) != WIFI_STATE_DISABLED)
                        break;
                }
                if (status == WIFI_STATE_DISABLED) {
                    return JDK_ERR;
                }
                startTime = System.currentTimeMillis();
                // 循环检测wifi是否打开成功
                if (status != WIFI_STATE_ENABLED) {
                    // 正在打开wifi
                    if (status == WIFI_STATE_ENABLING) {
                        while (getStopTime(startTime) < 10) {
                            if ((status = wifiUtil.wifi_checkState()) == WIFI_STATE_ENABLED)
                                break;
                        }
                        if (status != WIFI_STATE_ENABLED) {
                            return JDK_ERR;
                        }
                    }
                }
                break;

            case WIFI_STATE_ENABLED:
                break;
        }
        LoggerUtil.i("wifi status:"+status);
        return 0;
    }

//	public int scanWifi()
//	{
//		WifiUtil wifiUtil = new WifiUtil(context);
//		wifiUtil.scanBroadCast();
//		return JDK_OK;
//	}

    private int wifiNet(WifiPara wifiPara,int locate) {

        int netId;
        WifiUtil wifiUtil = new WifiUtil(context);
        LoggerUtil.v("wifiNet:"+wifiPara.getSsid());
        if(wifiPara.isDHCPenable())
        {
            LoggerUtil.i("wifi isDHCPenable");
            WifiConfiguration tempConfig = wifiUtil.IsExsits(wifiPara.getSsid());
            if(tempConfig==null){
                // 添加wifi ap的网络
                WifiConfiguration wifiConfiguration = wifiUtil.CreateWifiInfo(wifiPara);
                netId=wifiUtil.addNet(wifiConfiguration);
                LoggerUtil.i("netId:"+netId);
            }else
                netId=tempConfig.networkId;
            try
            {
                boolean connect=wifiUtil.addNetwork(netId);
                LoggerUtil.d("connect:"+connect);
                if(!connect){
                    return JDK_ERR;
                }
            }
            catch(Exception e)
            {
                e.printStackTrace();
                return JDK_ERR;
            }
        }
        else if(!wifiPara.isDHCPenable())
        {
            // 静态
            try {
                boolean connect=wifiUtil.saveStaticWifiConfig(wifiPara, 24);
                if(!connect){
                    return JDK_ERR;
                }
            } catch (Exception e)
            {
                e.printStackTrace();
                return JDK_ERR;
            }
        }
        linkStatus.put(locate, LinkStatus.linkup);
        return JDK_OK;
    }

    /**
     *  建立wifi的连接
     * @param wifiPara
     * @return wifi目前的状态
     */
    private int linkUp_WLAN(WifiPara wifiPara,int locate)
    {
		/*private & local definition*/
        if(wifiOpen()!=JDK_OK)
            return JDK_ERR;
//		wifiUtil.scanBroadCast(); // Android7.0后开启wifi未接收到扫描广播
        // 连接wifi ap的操作
        if(wifiNet(wifiPara,locate)!=JDK_OK)
            return JDK_ERR;
        return JDK_OK;
    }



//	private int wifiDisconnet()
//	{
//		WifiUtil wifiUtil = new WifiUtil(context);
//		boolean result=wifiUtil.disconnectWifi();
//		return result!=true?JDK_ERR:JDK_OK;
//	}

    private int WifiClose()
    {
        long startTime;
        int ret = 0;
        WifiUtil wifiUtil = new WifiUtil(context);

		/*process body*/
        wifiUtil.wifi_closeNet();
        // 检测wifi的当前状态
        switch (wifiUtil.wifi_checkState())
        {
            case WIFI_STATE_DISABLED:
                ret = JDK_OK;
                break;

            default:
                startTime = System.currentTimeMillis();
                if(wifiUtil.wifi_checkState() != WIFI_STATE_ENABLING)
                {
                    while(getStopTime(startTime)<10)
                    {
                        if(wifiUtil.wifi_checkState() == WIFI_STATE_DISABLED)
                        {
                            ret = JDK_OK;
                            break;
                        }
                    }
                    if(wifiUtil.wifi_checkState() != WIFI_STATE_DISABLED)
                    {
                        ret = JDK_ERR;
                    }
                }else{
                    ret = JDK_ERR;
                }
                break;
        }
        return ret;
    }
    // add by 20150318
    // 断开以太网的链路连接
    private int linkDownWLAN(int locate)
    {
		/*private & local definition*/
        if(WifiClose()!=JDK_OK)
            return JDK_ERR;
        // wifi关闭成功
        linkStatus.put(locate, LinkStatus.linkdown);
        return JDK_OK;

    }
    // end by 20150318

    // add by 20150318
    // 建立wifi的网络层
    private int netUpWLAN(WifiPara wifiPara,int locate)
    {
		/*private & local definition*/
        WifiUtil wifiUtil = new WifiUtil(context);

		/*process body*/
        // 动态ip
        if(wifiPara.isDHCPenable()==true)
        {
            wifiUtil.getWifiMsg(wifiPara);
        }
        netStatus.put(locate, NetStatus.NETUP);
        return JDK_OK;
    }
    // end by 20150318

    // add by 20150318
    // 断开wifi的链路层
    private int netDownWLAN(WifiPara wifipara,int locate)
    {
		/*private & local definition*/
        int ret = JDK_OK;

		/*process body*/
        if(linkDownWLAN(locate)!=JDK_OK)
            ret = JDK_ERR;
        netStatus.put(locate, NetStatus.NETDOWN);
        return ret;
    }
    // end by 20150318

    // 断开连接
    public int linkDown(int type,int locate)
    {
		/*process body*/
        if(linkStatus.get(locate)==LinkStatus.linkdown)
            return JDK_OK;
        return _LinkDown(type,locate);
    }

    // 具体建立哪种连接
    private int _LinkUp(int type,int locate)
    {
		/* private & local definition */
        int ret = JDK_OK;

		/* process body */
        switch (type)
        {
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
                ret = linkUp_WLM(locate);
                break;

            case 10:
                ret =  linkUp_WLAN(objMap.get(locate),locate);
                break;

            default:
                return JDK_ERR;
        }
        return ret;
    }

    private int _LinkDown(int type,int locate)
    {
		/*private & local definition*/
        int ret = 0;

		/*process body*/
        switch (type)
        {
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
                ret = linkDown_WLM(locate);
                break;
            case 10:
                ret = linkDownWLAN(locate);
                break;
            default:
                return JDK_ERR;
        }
        return ret;
    }

    // add by 20140312
    // 建立连接
    public int netUp(int type,int locate)
    {
		/*private & local definition*/
        int ret = 0;

		/*process body*/
        if((ret = linkUP(type,locate))!= JDK_OK)
        {
            return ret;
        }
//        if(WifiUtil.getInstance(context).wifi_isConnected()){
//            netStatus.put(locate, NetStatus.NETUP);
//            return JDK_OK;
//        }
        transStatus.put(locate, TransStatus.TRANSDOWN);
        return _netUp(type,locate);
    }
    // end by 20150312

    public int _netUp(int type,int locate)
    {
		/*private & local definition*/
        int ret = 0;

		/*process body*/
        switch (type)
        {
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
                ret = netUp_WLM(locate);
                break;

            case 10:
                LoggerUtil.i("_netUp wifi");
                ret = netUpWLAN(objMap.get(locate),locate);
                break;

            default:
                return JDK_ERR;
        }
        LoggerUtil.d("_netUp ret="+ret);
        return ret;
    }

    public int transUp(int sock_t,int locate)
    {
		/* private & local definition */

		/* process body */
        if (transStatus.get(locate) == TransStatus.TRANSUP)
            return JDK_OK;
        return _transUp(sock_t,locate);
    }

    // 传输
    private int _transUp(int sock_t,int locate)
    {
		/*private & local definition*/
        int ret = JDK_OK;

		/*process body*/
        switch (sock_t)
        {
            case 0:
            case 1:
                try
                {
                    ret = ndkTransUp(sock_t,locate);
                } catch (Exception e)
                {
                    e.printStackTrace();
                    return JDK_ERR;
                }
                break;

            default:
                return JDK_OK;
        }
        if(ret == JDK_OK)
            transStatus.put(locate, TransStatus.TRANSUP);
        return ret;
    }

    public int ndkTransUp(int sock_t,int locate)
    {
		/*private & local definition*/
        int ret;

		/*process body*/
        if(sock_t != 0&& sock_t != 1)
            return JDK_ERR;

        if((ret = socketUtilMap.get(locate).setSocket(sock_t))!=JDK_OK)
            return ret;
        return JDK_OK;
    }

    // 断开socket的连接
    public int ndkTransDown(int sock_t,int locate) throws IOException
    {
		/*private & local definition*/
        int ret = 0;

		/*process body*/
        if(TCPRESETFLAG == false)
        {
            if(socketUtilMap.get(locate)!=null)
            {
                if((ret = socketUtilMap.get(locate).close(sock_t))!=JDK_OK)
                    return JDK_ERR;
            }
        }
        transStatus.put(locate, TransStatus.TRANSDOWN);
        return ret;
    }

    public int sendData(int sock_t,byte[] data,int len,int timeOut,int locate)
    {
        int slen=0;
        if(sock_t == 0||sock_t == 1)
        {
            try
            {
                slen = socketUtilMap.get(locate).send(sock_t, data, len, timeout);
            } catch (IOException e)
            {
                e.printStackTrace();
                return IO_Exception;
            }
        }
        return slen;
    }

    public BackBean receiveData(int sock_t,int len,int timeout,int locate)
    {
        BackBean backBean=new BackBean();
        backBean.setBuffer(new byte[4]);

        if(sock_t == 0|| sock_t == 1)
        {
            try {
                if(socketUtilMap.get(locate)==null)
                {
                    LoggerUtil.v("socketUtil is null");
                }
                backBean = socketUtilMap.get(locate).receive(sock_t, len, timeout);
            } catch (IOException e) {
                e.printStackTrace();
                backBean.setRet(IO_Exception);
            }
        }
        return backBean;
    }

    public int netDown(int sock_t,int type,int locate)
    {
		/*process body*/
        transDown(sock_t,locate);
        transStatus.put(locate, TransStatus.TRANSDOWN);

        if(netStatus.get(locate) == NetStatus.NETDOWN)
            return JDK_OK;
        return _netDown(type,locate);
    }

    public int transDown(int sock_t,int locate)
    {
		/*process body*/
        if(transStatus.get(locate) == TransStatus.TRANSDOWN)
            return JDK_OK;
        return _transDown(sock_t,locate);
    }

    private int _transDown(int sock_t,int locate)
    {
		/*private & local definition*/
        int ret = 0;

		/*process body*/
        switch (sock_t)
        {
            case 0:
            case 1:
                try
                {
                    ndkTransDown(sock_t,locate);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                break;

            default:
                break;
        }
        transStatus.put(locate, TransStatus.TRANSDOWN);
        return ret;
    }

    private int _netDown(int type,int locate)
    {
		/*private & local definition*/
        int ret = 0;

		/*process body*/
        switch (type)
        {
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
                ret = netDown_WLM(locate);
                break;
            case 10:
                ret = netDownWLAN(objMap.get(locate),locate);
                break;

            default:
                ret = JDK_ERR;
                break;
        }
        if(ret == JDK_OK)
            netStatus.put(locate, NetStatus.NETDOWN);
        return ret;
    }

    private float getStopTime(long startTime)
    {
        long endTime = System.currentTimeMillis();
        float time =  (float) ((endTime-startTime)/1000.0);
        return time;
    }
}
