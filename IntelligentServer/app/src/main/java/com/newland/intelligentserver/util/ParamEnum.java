package com.newland.intelligentserver.util;

/**
 * Created by Administrator on 2019/6/21.
 */

public class ParamEnum {
    public enum LinkStatus
    {
        linkup,linkdown;
    }

    public enum NetStatus
    {
        NETUP,NETDOWN
    }

    public enum TransStatus
    {
        TRANSUP,TRANSDOWN;
    }

    public enum LinkType
    {
        NONE,GPRS,WCDMA,CDMA,TD,LTE,ASYN,SYNC,ETH,SERIAL,WLAN,BT,AP
    }

    public enum WIFI_SEC
    {
        WPA,WEP,NOPASS
    }

    public enum WIFI_AP_STATE
    {
        WIFI_AP_STATE_DISABLING, WIFI_AP_STATE_DISABLED, WIFI_AP_STATE_ENABLING,  WIFI_AP_STATE_ENABLED, WIFI_AP_STATE_FAILED
    }

    public enum Card_Type
    {
        MSCARD,ICCARD,RFCARD,ACARD,BCARD,M1CARD,UNKNOWN
    }
}
