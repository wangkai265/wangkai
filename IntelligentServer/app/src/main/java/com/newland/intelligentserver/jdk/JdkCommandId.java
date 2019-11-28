package com.newland.intelligentserver.jdk;

import android.app.enterpriseadmin.ApplicationPolicy;
import android.app.enterpriseadmin.RestrictionPolicy;
import android.content.Context;
import android.location.LocationManager;
import android.newland.AnalogSerialManager;
import android.newland.BootProvider;
import android.newland.NlCashBoxManager;
import android.newland.SettingsManager;
import android.newland.NLUART3Manager;
import android.newland.NlManager;
import android.newland.content.NlContext;
import android.newland.ndk.security.NdkSecurityManager;
import android.newland.os.DeviceStatisticsManager;
import android.newland.os.NlBuild;
import android.newland.os.NlRecovery;
import android.newland.scan.ScanUtil;
import android.newland.security.CertificateInfo;
import android.newland.security.SignatureComparison;
import android.newland.telephony.ApnUtils;
import android.newland.telephony.TelephonyManager;
import android.os.Build;
import android.os.Environment;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import com.gsc.mdm.system.ExDevicePolicyManager;
import com.gsc.mdm.system.SystemPolicy;
import com.newland.digled.Digled;
import com.newland.intelligentserver.TestLocalSocketActivity;
import com.newland.intelligentserver.btutil.BluetoothUtil;
import com.newland.intelligentserver.cons.ConstJdk;
import com.newland.intelligentserver.netutils.Layer;
import com.newland.intelligentserver.netutils.MobileUtil;
import com.newland.intelligentserver.netutils.WifiUtil;
import com.newland.intelligentserver.receiver.ApkBroadCastReceiver;
import com.newland.intelligentserver.receiver.BatteryReceiver;
import com.newland.intelligentserver.util.FileSystem;
import com.newland.intelligentserver.util.LinuxCmd;
import com.newland.intelligentserver.util.LoggerUtil;
import com.newland.intelligentserver.util.NfcUtil;
import com.newland.k21controller.K21ControllerManager;
/**
 * Created by 002 on 2019/6/21.
 */
public class JdkCommandId implements ConstJdk{

    public static final Map<Integer,String[]> commandid=new HashMap<Integer,String[]>();
    public static final Map<Integer,String[]> Specialcommandid=new HashMap<Integer,String[]>();
    public static HashMap<String, Object> gHash_obj = new HashMap<String, Object>();

    static{
        //设置 方法名+类名
        // systemconfig
        commandid.put(1001, new String[]{"setScreenBrightness",                     SettingsManager.class.getName()});
        commandid.put(1002, new String[]{"setScreenTimeout",                        SettingsManager.class.getName()});
        commandid.put(1003, new String[]{"setSettingStorageDispley",                SettingsManager.class.getName()});
        commandid.put(1004, new String[]{"setAllApkVerifyEnable",                   SettingsManager.class.getName()});
        commandid.put(1005, new String[]{"setAllApkVerifyDisable",                  SettingsManager.class.getName()});
        commandid.put(1006, new String[]{"setSettingAppDispley",                    SettingsManager.class.getName()});
        commandid.put(1007, new String[]{"setSettingApkNeedLogin",                  SettingsManager.class.getName()});
        commandid.put(1008, new String[]{"setLoginPassword",                        SettingsManager.class.getName()});
        commandid.put(1009, new String[]{"setSettingHomeDispley",                   SettingsManager.class.getName()});
        commandid.put(1010, new String[]{"setSettingPrivacyDispley",                SettingsManager.class.getName()});
        commandid.put(1011, new String[]{"setShowBatteryPercent",                   SettingsManager.class.getName()});
        commandid.put(1012, new String[]{"setSettingBatteryDispley",                SettingsManager.class.getName()});
        commandid.put(1013, new String[]{"setSettingDataUsageDispley",              SettingsManager.class.getName()});
        commandid.put(1014, new String[]{"setSettingPrintSettingsDispley",          SettingsManager.class.getName()});
        commandid.put(1015, new String[]{"setAppSwitchKeyEnabled",                  SettingsManager.class.getName()});
        commandid.put(1016, new String[]{"relayoutNavigationBar",                   SettingsManager.class.getName()});
        commandid.put(1017, new String[]{"setSettingAccessibilitySettingsDispley",  SettingsManager.class.getName()});
        commandid.put(1018, new String[]{"setSettingDevelopmentSettingsDispley",    SettingsManager.class.getName()});
        commandid.put(1019, new String[]{"setSettingLocationSettingsDispley",       SettingsManager.class.getName()});
        commandid.put(1020, new String[]{"setSettingSecuritySettingsDispley",       SettingsManager.class.getName()});
        commandid.put(1021, new String[]{"setSettingVpnDispley",                    SettingsManager.class.getName()});
        commandid.put(1022, new String[]{"setSettingLockScreenDisplay",             SettingsManager.class.getName()});
        commandid.put(1023, new String[]{"setDeepSleepEnabled",                     SettingsManager.class.getName()});
        commandid.put(1024, new String[]{"setStatusBarEnabled",                     SettingsManager.class.getName()});
        commandid.put(1025, new String[]{"setStatusBarAdbNotify",                   SettingsManager.class.getName()});
        commandid.put(1026, new String[]{"setSettingLanguageSpellCheckerDisplay",   SettingsManager.class.getName()});
        commandid.put(1027, new String[]{"setSettingLanguageUserDictionaryDisplay", SettingsManager.class.getName()});
        commandid.put(1028, new String[]{"setSettingNotificationItemsDisplay",      SettingsManager.class.getName()});
        commandid.put(1029, new String[]{"setSettingLocales",                       SettingsManager.class.getName()});
        commandid.put(1030, new String[]{"setSettingOtaUpdateEnabled",              SettingsManager.class.getName()});
        commandid.put(1031, new String[]{"setSettingWallpaperDisplay",              SettingsManager.class.getName()});
        commandid.put(1032, new String[]{"setSettingDeviceInfoItemsDisplay",        SettingsManager.class.getName()});
        commandid.put(1033, new String[]{"setTetherDisplay",                        SettingsManager.class.getName()});
        commandid.put(1034, new String[]{"setWifiInstallCedentialDisplay",          SettingsManager.class.getName()});
        commandid.put(1035, new String[]{"setSettingProcessorDisplay",              SettingsManager.class.getName()});
        commandid.put(1036, new String[]{"disableAppCommunication",                 SettingsManager.class.getName()});
        commandid.put(1037, new String[]{"getDisabledApps",                         SettingsManager.class.getName()});
        commandid.put(1038, new String[]{"setAppSignatureVerificationScheme",       SettingsManager.class.getName()});
        commandid.put(1039, new String[]{"setPaymentCertUpdateDisplay",             SettingsManager.class.getName()});
        commandid.put(1040, new String[]{"setHomeKeyEnabled",                       SettingsManager.class.getName()});
        commandid.put(1041, new String[]{"setBluetoothFileTransfer",                SettingsManager.class.getName()});
        commandid.put(1042, new String[]{"setLauncher",                             SettingsManager.class.getName()});
        commandid.put(1043, new String[]{"setProductModel",                         SettingsManager.class.getName()});
        commandid.put(1044, new String[]{"setMenuKeyValue",                         SettingsManager.class.getName()});
        commandid.put(1045, new String[]{"setSystemSetting",                        SettingsManager.class.getName()});
        commandid.put(1046, new String[]{"backupAppData",                           SettingsManager.class.getName()});
        commandid.put(1047, new String[]{"restoreAppData",                          SettingsManager.class.getName()});
        commandid.put(1048, new String[]{"setOtgMode",                              SettingsManager.class.getName()});
        commandid.put(1049, new String[]{"getOtgMode",                              SettingsManager.class.getName()});
        commandid.put(1050, new String[]{"isStatusBarExpandable",                   SettingsManager.class.getName()});
        commandid.put(1051, new String[]{"setDateTimeSettingsDisplay",              SettingsManager.class.getName()});
        commandid.put(1052, new String[]{"setWakeUpSystemEnable",                   SettingsManager.class.getName()});
        commandid.put(1053, new String[]{"getWakeUpSystemEnable",                   SettingsManager.class.getName()});
        commandid.put(1054, new String[]{"getScreenBrightness",                     SettingsManager.class.getName()});
        commandid.put(1055, new String[]{"setStatusBarQsTiles",                     SettingsManager.class.getName()});
        commandid.put(1056, new String[]{"addSystemSetting",                        SettingsManager.class.getName()});
        commandid.put(1057, new String[]{"deleteSystemSetting",                     SettingsManager.class.getName()});
        commandid.put(1058, new String[]{"setDefaultInputMethod",                   SettingsManager.class.getName()});
        commandid.put(1059, new String[]{"setUninstallAPKBlackList",                SettingsManager.class.getName()});
        commandid.put(1060, new String[]{"getUninstallAPKBlackList",                SettingsManager.class.getName()});
        commandid.put(1061, new String[]{"disableAPK",                              SettingsManager.class.getName()});
        commandid.put(1062, new String[]{"getAPKDisableState",                      SettingsManager.class.getName()});

        // 无线
        commandid.put(2000, new String[]{"setMobileDataEnabled",                    TelephonyManager.class.getName()});
        commandid.put(2001, new String[]{"getMobileDataEnabled",                    TelephonyManager.class.getName()});
        commandid.put(2002, new String[]{"getImei",                                 TelephonyManager.class.getName()});
        commandid.put(2003, new String[]{"getMeid",                                 TelephonyManager.class.getName()});
        commandid.put(2004, new String[]{"getIpBlackList",                          TelephonyManager.class.getName()});
        commandid.put(2005, new String[]{"setIpValid",                              TelephonyManager.class.getName()});

        // apn相关接口(getAllApnList接口传输文件太大，返回数据量太大，以文件的形式存储在电脑文件中）
        commandid.put(2100, new String[]{"getPreferApn",                            ApnUtils.class.getName()});
        Specialcommandid.put(2101, new String[]{"getAllApnList",                    ApnUtils.class.getName()});
        commandid.put(2102, new String[]{"removeApn",                               ApnUtils.class.getName()});
        Specialcommandid.put(2103, new String[]{"addNewApn",                        ApnUtils.class.getName()});
        commandid.put(2104, new String[]{"setDefault",                              ApnUtils.class.getName()});
        commandid.put(2105, new String[]{"getCurrentApnList",                       ApnUtils.class.getName()});

        // 获取版本信息-需要特殊处理
        Specialcommandid.put(3000, new String[]{"NL_FIRMWARE",                      "android.newland.os.NlBuild.VERSION"});
        Specialcommandid.put(3001, new String[]{"NL_HARDWARE_ID",                   "android.newland.os.NlBuild.VERSION"});
        Specialcommandid.put(3002, new String[]{"NL_HARDWARE_CONFIG",               "android.newland.os.NlBuild.VERSION"});
        Specialcommandid.put(3003, new String[]{"MODEL",                            "android.newland.os.NlBuild.VERSION"});
        Specialcommandid.put(3004, new String[]{"PROCESSOR_INFO",                   "android.newland.os.NlBuild.VERSION"});
        Specialcommandid.put(3005, new String[]{"SERIAL_NUMBER",                    "android.newland.os.NlBuild.VERSION"});
        Specialcommandid.put(3006, new String[]{"BASEBAND",                         "android.newland.os.NlBuild.VERSION"});
        Specialcommandid.put(3007, new String[]{"BOOTLOADER_VERSION",               "android.newland.os.NlBuild.VERSION"});

        Specialcommandid.put(3008, new String[]{"TUSN",                             NlBuild.class.getName()});
        Specialcommandid.put(3009, new String[]{"CUSTOMER_ID",                      NlBuild.class.getName()});

        Specialcommandid.put(3010, new String[]{"TOUCHSCREEN_RESOLUTION",           "android.newland.os.NlBuild.VERSION"});
        Specialcommandid.put(3011, new String[]{"TOUCHSCREEN_NAME",                 "android.newland.os.NlBuild.VERSION"});
        Specialcommandid.put(3012, new String[]{"TOUCHSCREEN_VERSION",              "android.newland.os.NlBuild.VERSION"});

        Specialcommandid.put(3013, new String[]{"MANUFACTURER",                     Build.class.getName()});
        Specialcommandid.put(3014, new String[]{"SERIAL",                           Build.class.getName()});
        Specialcommandid.put(3015, new String[]{"MODEL",                            Build.class.getName()});
        Specialcommandid.put(3016, new String[]{"BOARD",                            Build.class.getName()});
        Specialcommandid.put(3017, new String[]{"BOOTLOADER",                       Build.class.getName()});
        Specialcommandid.put(3018, new String[]{"BRAND",                            Build.class.getName()});
        Specialcommandid.put(3019, new String[]{"DEVICE",                           Build.class.getName()});
        Specialcommandid.put(3020, new String[]{"DISPLAY",                          Build.class.getName()});
        Specialcommandid.put(3021, new String[]{"FINGERPRINT",                      Build.class.getName()});
        Specialcommandid.put(3022, new String[]{"HARDWARE",                         Build.class.getName()});
        Specialcommandid.put(3023, new String[]{"PRODUCT",                          Build.class.getName()});
        Specialcommandid.put(3024, new String[]{"TAGS",                             Build.class.getName()});


        // 获取/设置节点属性值
        commandid.put(4000, new String[]{"readDevNode",                             LinuxCmd.class.getName()});
        commandid.put(4001, new String[]{"execCmd",                                 LinuxCmd.class.getName()});
        commandid.put(4002, new String[]{"getSystemProperty",                       LinuxCmd.class.getName()});

        // USB虚拟串口
        commandid.put(5000, new String[]{"open",                                    AnalogSerialManager.class.getName()});
        commandid.put(5001, new String[]{"open",                                    AnalogSerialManager.class.getName()});
        commandid.put(5002, new String[]{"getPortName",                             AnalogSerialManager.class.getName()});
        commandid.put(5003, new String[]{"getVersion",                              AnalogSerialManager.class.getName()});
        commandid.put(5004, new String[]{"setconfig",                               AnalogSerialManager.class.getName()});
        commandid.put(5005, new String[]{"ioctl",                                   AnalogSerialManager.class.getName()});
        Specialcommandid.put(5006, new String[]{"read",                             AnalogSerialManager.class.getName()});
        commandid.put(5007, new String[]{"write",                                   AnalogSerialManager.class.getName()});
        commandid.put(5008, new String[]{"close",                                   AnalogSerialManager.class.getName()});
        commandid.put(5009, new String[]{"isValid",                                 AnalogSerialManager.class.getName()});

        // mpos接口
        Specialcommandid.put(6000, new String[]{"connect",                          K21ControllerManager.class.getName()});
        Specialcommandid.put(6001, new String[]{"sendCmd",                          K21ControllerManager.class.getName()});
        Specialcommandid.put(6002, new String[]{"sendCmd",                          K21ControllerManager.class.getName()});
        Specialcommandid.put(6003, new String[]{"close",                            K21ControllerManager.class.getName()});

        // 钱箱相关接口
        commandid.put(7000, new String[]{"setVoltage",                              NlCashBoxManager.class.getName()});
        commandid.put(7001, new String[]{"getVoltage",                              NlCashBoxManager.class.getName()});
        Specialcommandid.put(7002, new String[]{"setTimeSec",                       NlCashBoxManager.class.getName()});
        Specialcommandid.put(7003, new String[]{"getTimeSec",                       NlCashBoxManager.class.getName()});
        commandid.put(7004, new String[]{"OpenCashBox",                             NlCashBoxManager.class.getName()});

        //RS232串口
        commandid.put(8000,new String[]{"setconfig",                                NLUART3Manager.class.getName()});
        commandid.put(8001,new String[]{"open",                                     NLUART3Manager.class.getName()});
        commandid.put(8002,new String[]{"open",                                     NLUART3Manager.class.getName()});
        commandid.put(8003,new String[]{"close",                                    NLUART3Manager.class.getName()});
        Specialcommandid.put(8004,new String[]{"read",                              NLUART3Manager.class.getName()});
        commandid.put(8005,new String[]{"write",                                    NLUART3Manager.class.getName()});
        commandid.put(8006,new String[]{"getVersion",                               NLUART3Manager.class.getName()});
        commandid.put(8007,new String[]{"isValid",                                  NLUART3Manager.class.getName()});

        //PaymentPort串口
        commandid.put(9000,new String[]{"setconfig",                                NlManager.class.getName()});
        commandid.put(9001,new String[]{"connect",                                  NlManager.class.getName()});
        commandid.put(9002,new String[]{"disconnect",                               NlManager.class.getName()});
        Specialcommandid.put(9003,new String[]{"read",                              NlManager.class.getName()});
        commandid.put(9004,new String[]{"write",                                    NlManager.class.getName()});
        commandid.put(9005,new String[]{"getVersion",                               NlManager.class.getName()});
        commandid.put(9006,new String[]{"isValid",                                  NlManager.class.getName()});

        // 数码管显示模块
        Specialcommandid.put(12000, new String[]{"digLedVer",                       Digled.class.getName()});
        commandid.put(12001, new String[]{"showDigLed",                             Digled.class.getName()});
        commandid.put(12002, new String[]{"brightDigLed",                           Digled.class.getName()});
        commandid.put(12003, new String[]{"clrDigLed",                              Digled.class.getName()});

        //扫码
        commandid.put(13000, new String[]{"ScanUtil",                               ScanUtil.class.getName()});// 特殊处理
        commandid.put(13001, new String[]{"init",                                   ScanUtil.class.getName()});
        Specialcommandid.put(13002, new String[]{"doscan",                          ScanUtil.class.getName()});// 静态方法
        commandid.put(13003, new String[]{"setModeContinuous",                      ScanUtil.class.getName()});
        commandid.put(13004, new String[]{"doScan",                                 ScanUtil.class.getName()});
        commandid.put(13005, new String[]{"stopScan",                               ScanUtil.class.getName()});
        commandid.put(13006, new String[]{"relese",                                 ScanUtil.class.getName()});
        commandid.put(13007, new String[]{"setEncodeFormat",                        ScanUtil.class.getName()});
        commandid.put(13008, new String[]{"setNlsUPCEANSwitch",                     ScanUtil.class.getName()});
        commandid.put(13009, new String[]{"setThk88Power",                          ScanUtil.class.getName()});
        Specialcommandid.put(13010, new String[]{"getThk88ID",                      ScanUtil.class.getName()});
        commandid.put(13011, new String[]{"openLight",                              ScanUtil.class.getName()});
        commandid.put(13012, new String[]{"closeLight",                             ScanUtil.class.getName()});
        commandid.put(13013, new String[]{"getNLSVersion",                          ScanUtil.class.getName()});
        Specialcommandid.put(13014,new String[]{"startRGBDecode",                   ScanUtil.class.getName()});
        Specialcommandid.put(13015,new String[]{"startYUVDecode",                   ScanUtil.class.getName()});
        commandid.put(13016, new String[]{"setLED",                                 ScanUtil.class.getName()});
        commandid.put(13017, new String[]{"setRedLED",                              ScanUtil.class.getName()});
        commandid.put(13018, new String[]{"doScanWithRawByte",                      ScanUtil.class.getName()});
        commandid.put(13019, new String[]{"setNlsScn",                              ScanUtil.class.getName()});
        commandid.put(13020, new String[]{"getInstance",                            SoftEngine.class.getName()});


        // payment
        Specialcommandid.put(14000, new String[]{"init_x509",                       SignatureComparison.class.getName()});// 静态方法
        commandid.put(14001, new String[]{"isNewAppSign",                           CertificateInfo.class.getName()});

        // 其他(getDeviceStatisticsInfo数据量太大)
        commandid.put(15000, new String[]{"SetCustomBootLogo",                      BootProvider.class.getName()});
        commandid.put(15001, new String[]{"SetCustomBootAnimation",                 BootProvider.class.getName()});
        commandid.put(15002, new String[]{"RemoveCustomBootAnimation",              BootProvider.class.getName()});
        Specialcommandid.put(15003, new String[]{"getDeviceStatisticsInfo",         DeviceStatisticsManager.class.getName()});
        Specialcommandid.put(15004, new String[]{"getSecTamperStatus",              NdkSecurityManager.class.getName()});

        // 恢复出厂相关接口
        commandid.put(16000, new String[]{"canRecovery",                            NlRecovery.class.getName()});
        commandid.put(16001, new String[]{"recovery",                               NlRecovery.class.getName()});
        commandid.put(16002, new String[]{"keepApps",                               NlRecovery.class.getName()});
        commandid.put(16003, new String[]{"uninstallApps",                          NlRecovery.class.getName()});
        commandid.put(16004, new String[]{"uninstallSecData",                       NlRecovery.class.getName()});

        // psbc相关接口
        commandid.put(17000,new String[]{"installApplication",                      ApplicationPolicy.class.getName()});
        commandid.put(17001,new String[]{"uninstallApplication",                    ApplicationPolicy.class.getName()});
        commandid.put(17002,new String[]{"setDisableApplication",                   ApplicationPolicy.class.getName()});
        commandid.put(17003,new String[]{"setEnableApplication",                    ApplicationPolicy.class.getName()});
        commandid.put(17004,new String[]{"wipeApplicationData",                     ApplicationPolicy.class.getName()});
        commandid.put(17005,new String[]{"setDefaultLauncher",                      ApplicationPolicy.class.getName()});
        commandid.put(17006,new String[]{"removeDefaultLauncher",                   ApplicationPolicy.class.getName()});
        commandid.put(17007,new String[]{"allowBluetooth",                          RestrictionPolicy.class.getName()});
        commandid.put(17008,new String[]{"allowWiFi",                               RestrictionPolicy.class.getName()});
        commandid.put(17009,new String[]{"allowLocationService",                    RestrictionPolicy.class.getName()});
        commandid.put(17010,new String[]{"allowCellularData",                       RestrictionPolicy.class.getName()});
        commandid.put(17011,new String[]{"allowUserEditApn",                        RestrictionPolicy.class.getName()});
        commandid.put(17012,new String[]{"allowAutoTime",                           RestrictionPolicy.class.getName()});
        commandid.put(17013,new String[]{"allowSDCard",                             RestrictionPolicy.class.getName()});
        commandid.put(17014,new String[]{"allowMTP",                                RestrictionPolicy.class.getName()});
        commandid.put(17015,new String[]{"allowMassStorage",                        RestrictionPolicy.class.getName()});
        commandid.put(17016,new String[]{"allowPTP",                                RestrictionPolicy.class.getName()});
        commandid.put(17017,new String[]{"allowBluetoothTransFile",                 SystemPolicy.class.getName()});
        commandid.put(17018,new String[]{"isBluetoothTransFileAllowed",             SystemPolicy.class.getName()});
        commandid.put(17019,new String[]{"isUpdate",                                SystemPolicy.class.getName()});
        commandid.put(17020,new String[]{"startUpdateOS",                           SystemPolicy.class.getName()});
        commandid.put(17021,new String[]{"getPosVerInfo",                           SystemPolicy.class.getName()});

        // wifi原生接口
        commandid.put(20000, new String[]{"wifi_getMacAddress",                     WifiUtil.class.getName()});
        commandid.put(20001, new String[]{"wifi_openNet",                           WifiUtil.class.getName()});
        commandid.put(20002, new String[]{"wifi_closeNet",                          WifiUtil.class.getName()});
        commandid.put(20003, new String[]{"wifi_closeOther",                        WifiUtil.class.getName()});
        commandid.put(20004, new String[]{"wifi_getSignStrength",                   WifiUtil.class.getName()});
        commandid.put(20005, new String[]{"wifi_checkState",                        WifiUtil.class.getName()});
        commandid.put(20006, new String[]{"pingTest",                               WifiUtil.class.getName()});
        commandid.put(20007, new String[]{"wifi_startScan",                         WifiUtil.class.getName()});
        commandid.put(20008, new String[]{"registWifiConnect",                      WifiUtil.class.getName()});
        commandid.put(20009, new String[]{"unRegistWifiConnect",                    WifiUtil.class.getName()});
        commandid.put(20010, new String[]{"setWifiApEnabled",                       WifiUtil.class.getName()});
        commandid.put(20011, new String[]{"getWifiApState",                         WifiUtil.class.getName()});

        // wlm原生接口
        commandid.put(21000, new String[]{"wlm_getSimState",                        MobileUtil.class.getName()});
        commandid.put(21001, new String[]{"wlm_getSimSerialNumber",                 MobileUtil.class.getName()});
        commandid.put(21002, new String[]{"wlm_getNetworkType",                     MobileUtil.class.getName()});
        commandid.put(21003, new String[]{"wlm_netWorkType",                        MobileUtil.class.getName()});
        Specialcommandid.put(21004,new String[]{"getSignalStrengths",               MobileUtil.class.getName()});

        // gps原生接口
        commandid.put(22000, new String[]{"isProviderEnabled",                      LocationManager.class.getName()});

        // 原生其他接口
        Specialcommandid.put(23000, new String[]{"getExternalStoragePublicDirectory",   Environment.class.getName()});
        Specialcommandid.put(23001, new String[]{"getExternalFilesDir",                 Environment.class.getName()});

        // 电池相关
        Specialcommandid.put(24000, new String[]{"batteryRegist",                   BatteryReceiver.class.getName()});
        Specialcommandid.put(24001, new String[]{"batteryUnRegist",                 BatteryReceiver.class.getName()});
        commandid.put(24002, new String[]{"getBatHealth",                           BatteryReceiver.class.getName()});
        commandid.put(24003, new String[]{"getBatMsg",                              BatteryReceiver.class.getName()});
        commandid.put(24004, new String[]{"setCharge",                              BatteryReceiver.class.getName()});
        commandid.put(24005, new String[]{"getPlugType",                            BatteryReceiver.class.getName()});
        commandid.put(24006, new String[]{"getCharge",                              BatteryReceiver.class.getName()});
        commandid.put(24007, new String[]{"getBatVol",                              BatteryReceiver.class.getName()});

        // socket通讯相关
        commandid.put(25000, new String[]{"initPara",                               Layer.class.getName()});
        commandid.put(25001, new String[]{"netUp",                                  Layer.class.getName()});
        commandid.put(25002, new String[]{"transUp",                                Layer.class.getName()});
        commandid.put(25003, new String[]{"transDown",                              Layer.class.getName()});
        commandid.put(25004, new String[]{"netDown",                                Layer.class.getName()});
        commandid.put(25005, new String[]{"sendData",                               Layer.class.getName()});
        commandid.put(25006, new String[]{"receiveData",                            Layer.class.getName()});

        // Android的Fs相关
        commandid.put(26000, new String[]{"JDK_FsExist",                            FileSystem.class.getName()});
        commandid.put(26001, new String[]{"JDK_FsDel",                              FileSystem.class.getName()});
        commandid.put(26002, new String[]{"JDK_FsOpen",                             FileSystem.class.getName()});
        commandid.put(26003, new String[]{"JDK_FsWrite",                            FileSystem.class.getName()});
        commandid.put(26004, new String[]{"JDK_FsFileSize",                         FileSystem.class.getName()});
        commandid.put(26005, new String[]{"JDK_FsRead",                             FileSystem.class.getName()});

        // apk相关
        Specialcommandid.put(27000, new String[]{"apk_install",                     ApkBroadCastReceiver.class.getName()});
        Specialcommandid.put(27001, new String[]{"apk_Uninstall",                   ApkBroadCastReceiver.class.getName()});
        Specialcommandid.put(27002, new String[]{"apk_Regist",                      ApkBroadCastReceiver.class.getName()});
        Specialcommandid.put(27003, new String[]{"apk_UnRigst",                     ApkBroadCastReceiver.class.getName()});
        commandid.put(27004, new String[]{"getPackName",                            ApkBroadCastReceiver.class.getName()});
        commandid.put(27005, new String[]{"getResp",                                ApkBroadCastReceiver.class.getName()});

        // nfc相关
        commandid.put(28000, new String[]{"nfcConnect",                             NfcUtil.class.getName()});
        commandid.put(28001, new String[]{"nfcRw",                                  NfcUtil.class.getName()});
        commandid.put(28002, new String[]{"nfcDisEnableMode",                       NfcUtil.class.getName()});

        // bt相关
        commandid.put(29000, new String[]{"addService",                             BluetoothUtil.class.getName()});
        commandid.put(29001, new String[]{"openOrCloseBt",                          BluetoothUtil.class.getName()});
        commandid.put(29002, new String[]{"connComm",                               BluetoothUtil.class.getName()});
        commandid.put(29003, new String[]{"readComm",                               BluetoothUtil.class.getName()});
        commandid.put(29004, new String[]{"writeComm",                              BluetoothUtil.class.getName()});
        commandid.put(29005, new String[]{"cancel",                                 BluetoothUtil.class.getName()});
        commandid.put(29006, new String[]{"isEnabled",                              BluetoothUtil.class.getName()});

    }

    /**返回id对应的方法名和类名(不同类名存在相同方法名及参数列表)*/
    public static String[] fromCommandId(int id) {
        if(commandid.get(id)!=null)
            return commandid.get(id);
        else
            return Specialcommandid.get(id);
    }

    /**返回id对应的方法名和类名(带有输出参数的接口)*/
    public static String[] fromSpecialcommandid(int id) {
        return Specialcommandid.get(id);
    }

    // 通过类型初始化
    public static Object fromCommandId2Init(Context c,String className)
    {
        LoggerUtil.d("fromCommandId2Init:"+className);
        if(gHash_obj.get(className)!=null)
        {
            LoggerUtil.d("fromCommandId2Init:"+className);
            return gHash_obj.get(className);
        }
        if(className.equals("android.newland.SettingsManager"))
        {
            SettingsManager settingsManager = (SettingsManager) c.getSystemService(NlContext.SETTINGS_MANAGER_SERVICE);
            gHash_obj.put(className, settingsManager);
            return gHash_obj.get(className);
        }
        if(className.equals("android.newland.AnalogSerialManager"))
        {
            AnalogSerialManager analogSerialManager = (AnalogSerialManager) c.getSystemService(NlContext.ANALOG_SERIAL_SERVICE);
            gHash_obj.put(className, analogSerialManager);
            return gHash_obj.get(className);
        }
        if(className.equals("android.newland.NLUART3Manager"))
        {
            NLUART3Manager nlaurt3Manager = (NLUART3Manager) c.getSystemService(NlContext.UART3_SERVICE);
            gHash_obj.put(className, nlaurt3Manager);
            return gHash_obj.get(className);
        }
        if(className.equals("android.newland.NlManager"))
        {
//            NlManager nlManager = (NlManager) c.getSystemService(NlContext.NLMODEM_SERVICE);
            NlManager nlManager = NlManager.getInstance(c);
            gHash_obj.put(className, nlManager);
            return gHash_obj.get(className);
        }
        if(className.equals("android.location.LocationManager"))
        {
        LocationManager locationManager = (LocationManager) c.getSystemService(Context.LOCATION_SERVICE);
        gHash_obj.put(className, locationManager);
        return gHash_obj.get(className);
    }
        if(className.equals("com.newland.k21controller.K21ControllerManager"))
        {
            K21ControllerManager k21Manager = K21ControllerManager.getInstance(c);
            gHash_obj.put(className, k21Manager);
            return gHash_obj.get(className);
        }
        if(className.equals("android.newland.os.DeviceStatisticsManager"))
        {
			DeviceStatisticsManager service = DeviceStatisticsManager.getInstance(c);
			gHash_obj.put(className, service);
			return gHash_obj.get(className);
        }
        if(className.equals("android.newland.NlCashBoxManager"))
        {
            NlCashBoxManager nlCashBoxManager = (NlCashBoxManager) c.getSystemService(NlContext.CASHBOX_SERVICE);
            gHash_obj.put(className, nlCashBoxManager);
            return gHash_obj.get(className);
        }
        if(className.equals("android.newland.telephony.TelephonyManager")|className.equals(ApnUtils.class.getName())|
                className.equals("android.newland.os.NlRecovery")|className.equals("android.newland.security.CertificateInfo")|
                className.equals("android.newland.BootProvider")|className.equals(Layer.class.getName())|
                className.equals(WifiUtil.class.getName())|className.equals(MobileUtil.class.getName())|
                className.equals(NfcUtil.class.getName())|className.equals(BluetoothUtil.class.getName()))
        {
            try {
                Class<?> cls = Class.forName(className);// 取得class对象
                Constructor<?> cons = cls.getConstructor(Context.class);
                Object obj = cons.newInstance(c);
                gHash_obj.put(className, obj);
                return gHash_obj.get(className);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                TestLocalSocketActivity.g_Ret = Class_NotFound_Exception;
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
                TestLocalSocketActivity.g_Ret = NoSuch_Method_Exception;
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
                TestLocalSocketActivity.g_Ret = Invocation_Target_Exception;
            }
        }
        if(className.equals(LinuxCmd.class.getName())|className.equals("com.newland.digled.Digled")
                |className.equals(BatteryReceiver.class.getName()) |className.equals(FileSystem.class.getName())
                |className.equals(ApkBroadCastReceiver.class.getName()) |className.equals("android.newland.ndk.security.NdkSecurityManager"))
        {
            try {
                Class<?> cls = Class.forName(className);
                Object obj = cls.newInstance();
                gHash_obj.put(className, obj);
                return gHash_obj.get(className);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        if(className.equals(ApplicationPolicy.class.getName()))
        {
            ApplicationPolicy mAppPolicy = ((android.app.enterpriseadmin.ExDevicePolicyManager)c.getSystemService("ex_device_policy")).getApplicationPolicy();
            gHash_obj.put(className, mAppPolicy);
            return gHash_obj.get(className);
        }
        if(className.equals(RestrictionPolicy.class.getName()))
        {
            RestrictionPolicy resPolicy = ((android.app.enterpriseadmin.ExDevicePolicyManager)c.getSystemService("ex_device_policy")).getRestrictionPolicy();
            gHash_obj.put(className, resPolicy);
            return gHash_obj.get(className);
        }
        if(className.equals(SystemPolicy.class.getName()))
        {
            SystemPolicy systemPolicy = ((ExDevicePolicyManager)c.getSystemService("ex_device_policy")).getSystemPolicy();
            gHash_obj.put(className, systemPolicy);
            return gHash_obj.get(className);
        }
        return null;
    }
}
