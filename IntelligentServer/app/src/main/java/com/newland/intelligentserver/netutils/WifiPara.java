package com.newland.intelligentserver.netutils;

/**
 * Created by Administrator on 2019/6/21.
 */
public class WifiPara
{
    // 设置动态、静态ip，false：静态 true：动态
    public boolean DHCPenable = false;

    // 本地IP地址
    public String localIp = "192.168.4.152";
    // 本地端口号
    public int localPort = 8080;
    // 网关
    public String gateWay = "192.168.4.254";
    // 子网掩码
    public String netMask = "255.255.255.0";
    // 主DNS
    public String dns1 = "8.8.8.8"; // 8.8.8.8
    // 从DNS
    public String dns2 = "114.114.114.114";// 114.114.114.114

    // 服务器IP
    public String serverIp = "218.66.48.230";
    // 服务器端口号
    public int serverPort = 3459;
    // 传输类型
    public int sock_t = 0;
    // 链接类型
    public int type = 0;// 10 代表wifi 1,2,3,4,5 代表wlm


    /**
     * 动态/静态ip设置:默认是静态
     */

    /**
     * WIFI热点名称
     */
    private  String ssid;

    /**
     * wifi的mac地址
     */
    private String Bssid;

    /**
     * WIFI密码
     */
    private  String passwd;
    /**
     * 认证方式
     */
    private int sec=0;

    /**
     * scan_ssid:0 关闭ssid广播false
     * scan_ssid:1 开启ssid广播true
     * 默认情况下为1
     */
    private boolean scan_ssid=true;


    /**
     * 信道
     */
    private int channel=6;


    public WifiPara()
    {
        this.ssid = "002";
        this.passwd = "believe;";
        this.type = 0;
    }

    public boolean isDHCPenable() {
        return DHCPenable;
    }

    public void setDHCPenable(boolean dHCPenable) {
        DHCPenable = dHCPenable;
    }

    public String getLocalIp() {
        return localIp;
    }

    public void setLocalIp(String localIp) {
        this.localIp = localIp;
    }

    public int getLocalPort() {
        return localPort;
    }

    public void setLocalPort(int localPort) {
        this.localPort = localPort;
    }


    public String getGateWay() {
        return gateWay;
    }

    public void setGateWay(String gateWay) {
        this.gateWay = gateWay;
    }

    public String getDns1() {
        return dns1;
    }

    public void setDns1(String dns1) {
        this.dns1 = dns1;
    }

    public String getDns2() {
        return dns2;
    }

    public void setDns2(String dns2) {
        this.dns2 = dns2;
    }

    public String getSsid()
    {
        return ssid;
    }

    public String getBssid()
    {
        return Bssid;
    }

    public void setBssid(String Bssid)
    {
        this.Bssid = Bssid;
    }


    public void setSsid(String ssid)
    {
        this.ssid = ssid;
    }


    /**
     * 判断字符串中是否含有中文
     * @param str
     * @return
     */
    public boolean isChines(String str)
    {
        if(str.getBytes().length==str.length())// 这种情况就是数字和字符串或则特殊编码
            return false;
        else
            return true;// 包含中文字符
    }


    public String getPasswd()
    {
        return passwd;
    }


    public void setPasswd(String passwd)
    {
        this.passwd = passwd;
    }


    public int getSec() {
        return sec;
    }


    public void setSec(int sec) {
        this.sec = sec;
    }


    public boolean isScan_ssid() {
        return scan_ssid;
    }


    public void setScan_ssid(boolean scan_ssid) {
        this.scan_ssid = scan_ssid;
    }

    public int getChannel() {
        return channel;
    }


    public void setChannel(int channel) {
        this.channel = channel;
    }


}

