package com.newland.intelligentserver.sdk;

import java.util.Map;
import java.util.HashMap;
import android.content.Context;

import com.newland.intelligentserver.util.LoggerUtil;

/**
 * Created by 002 on 2019/6/20.
 */

public class SdkCommandId {
    private static final Map<Integer,String> gSdk_commandid=new HashMap<Integer,String>();
    private static Object gSdk_Obj;

    static{
        gSdk_commandid.put(1000, "connectDevice");
        gSdk_commandid.put(1001, "disconnect");
        //LED灯
        gSdk_commandid.put(1002, "initLightModule");
        gSdk_commandid.put(1003, "blinkLED");
        gSdk_commandid.put(1004, "turnOnLED");
        gSdk_commandid.put(1005, "tunOffLED");
        // CardReader
        gSdk_commandid.put(2000, "initCardReaderModule");
        gSdk_commandid.put(2001, "openCardReader");
        gSdk_commandid.put(2002, "closeCardReader");
        // IcCard
        gSdk_commandid.put(3000, "initIccardModule");
        gSdk_commandid.put(3001, "icCardPowerOn");
        gSdk_commandid.put(3002, "icCardCommunication");
        gSdk_commandid.put(3003, "icCardSlotState");
        gSdk_commandid.put(3004, "icCardPowerOff");
        // Memory卡
        gSdk_commandid.put(3005, "memoryCardOpen");
        gSdk_commandid.put(3006, "memoryCardVerify");
        gSdk_commandid.put(3007, "memoryCardRead");
        gSdk_commandid.put(3008, "memoryCardWrite");
        gSdk_commandid.put(3009, "memoryCardChangePassword");
        gSdk_commandid.put(3010, "memoryCardClose");
        // 打印
        gSdk_commandid.put(4001,"initPrinterModule");
        gSdk_commandid.put(4002,"getPrinterState");
        gSdk_commandid.put(4003,"printByScript");
        // 非接卡模块
        gSdk_commandid.put(5000,"initRfModule");
        gSdk_commandid.put(5001,"rfPowerOn");
        gSdk_commandid.put(5002,"rfcardCommunication");
        gSdk_commandid.put(5003,"rfcardPowerOff");
        gSdk_commandid.put(5004,"rfcardState");
        gSdk_commandid.put(5005,"M1PowerOn");
        gSdk_commandid.put(5006,"authenticateByExtendKey");
        gSdk_commandid.put(5007,"m1CardWrite");
        gSdk_commandid.put(5008,"m1CardRead");
        gSdk_commandid.put(5009,"m1CardIncrease");
        gSdk_commandid.put(5010,"m1CardDecrease");
        gSdk_commandid.put(5011,"felicaCardPowerOn");
        gSdk_commandid.put(5012,"felicaCardCommunication");
        gSdk_commandid.put(5013,"powerOff_common");
        gSdk_commandid.put(5014,"m0CardPowerOn");
        gSdk_commandid.put(5015,"m0CardAuth");
        gSdk_commandid.put(5016,"m0CardRead");
        gSdk_commandid.put(5017,"m0CardWrite");
        // Storage
        gSdk_commandid.put(6000,"initStorageModule");
        gSdk_commandid.put(6001,"initStorageRecord");
        gSdk_commandid.put(6002,"getStorageRecordNum");
        gSdk_commandid.put(6003,"addStorageRecord");
        gSdk_commandid.put(6004,"getStorageRecord");
        gSdk_commandid.put(6005,"updateStorageRecord");
        // Terminal
        gSdk_commandid.put(7000,"setTerminalTime");
        gSdk_commandid.put(7001,"getTerminalTime");
        gSdk_commandid.put(7002,"setTerminalParam");
        gSdk_commandid.put(7003,"getTerminalParam");
        gSdk_commandid.put(7004,"reset");
        // K21Swiper
        gSdk_commandid.put(8000,"initSwipModule");
        gSdk_commandid.put(8001,"readEncryResultByMask");
        gSdk_commandid.put(8002,"readEncryResult");
        gSdk_commandid.put(8003,"readPlainResult");
        // K21SecurityModule
        gSdk_commandid.put(9000,"initSecurityModule");
        gSdk_commandid.put(9001,"getDeviceInfo");
        gSdk_commandid.put(9002,"getRadomNumber");
        gSdk_commandid.put(9003,"getTusn");
        // PinModule
        gSdk_commandid.put(10000,"intiPinModule");
        gSdk_commandid.put(10001,"loadMainKey");
        gSdk_commandid.put(10002,"loadWorkingKey");
        gSdk_commandid.put(10005,"cancelInputPin");
        gSdk_commandid.put(10006,"calMac");
        gSdk_commandid.put(10007,"encryption");
        gSdk_commandid.put(10008,"decryption");
        gSdk_commandid.put(10009,"deleteMK");
        gSdk_commandid.put(10010,"deleteWK");
        gSdk_commandid.put(10011,"deleteAllKey");
        gSdk_commandid.put(10012,"checkKey");
        gSdk_commandid.put(10013,"getpbc21Info");
        gSdk_commandid.put(10014,"loadDukpt");
        gSdk_commandid.put(10015,"getDukptKsn");
        gSdk_commandid.put(10016,"ksnIncrease");
        gSdk_commandid.put(10017,"inputOnlinePin");// 暂不支持
        gSdk_commandid.put(10018,"canleInputPin");
        gSdk_commandid.put(10019,"calMacDukpt");
        gSdk_commandid.put(10020,"encryptionDukpt");
        gSdk_commandid.put(10021,"decryptionDukpt");
        // SM模块
        gSdk_commandid.put(11000,"initSmModule");
        gSdk_commandid.put(11001,"getSecfig");
        gSdk_commandid.put(11002,"setSecfig");
        gSdk_commandid.put(11003,"calSHA1");
        gSdk_commandid.put(11004,"calSHA256");
        gSdk_commandid.put(11005,"calSHA512");
        gSdk_commandid.put(11006,"generateRSAKey");
        gSdk_commandid.put(11007,"rsaEncryDecry");
        gSdk_commandid.put(11008,"rsaVerify");
        gSdk_commandid.put(11009,"generateSM2Key");
        gSdk_commandid.put(11010,"sm2Encry");
        gSdk_commandid.put(11011,"sm2Decry");
        gSdk_commandid.put(11012,"generateSm2Digest");
        gSdk_commandid.put(11013,"sm2Sign");
        gSdk_commandid.put(11014,"sm2Verify");
        // 客显模块
        gSdk_commandid.put(12000,"initGuestDisplayModule");
        gSdk_commandid.put(12001,"isEnabled");
        gSdk_commandid.put(12002,"startInput");// 暂不支持
        gSdk_commandid.put(12003,"stopInput");
        gSdk_commandid.put(12004,"setLedLight");
        gSdk_commandid.put(12005,"showLedAmount");
        gSdk_commandid.put(12006,"closeLed");
        //外接扫码模块
        gSdk_commandid.put(13000,"initExternalScanModule");
        gSdk_commandid.put(13001,"commInit");
        gSdk_commandid.put(13002,"startScan");
        gSdk_commandid.put(13003,"stopScan");
        gSdk_commandid.put(13004,"clearTags");
        gSdk_commandid.put(13005,"setLed");
        gSdk_commandid.put(13006,"setVolume");
        gSdk_commandid.put(13007,"setSuffix");
        gSdk_commandid.put(13008,"setEnter");
        gSdk_commandid.put(13009,"setAmount");
        gSdk_commandid.put(13010,"setPrefix");
        gSdk_commandid.put(13011,"playVoice");
        gSdk_commandid.put(13012,"setSuccessTip");
        gSdk_commandid.put(13013,"colorLight");
        gSdk_commandid.put(13014,"sendTags");
        gSdk_commandid.put(13015,"setDevParam");
        gSdk_commandid.put(13016,"getDevParam");
        //外接密码键盘
        gSdk_commandid.put(14000,"initExternalPinModule");
        gSdk_commandid.put(14001,"initExternalPinpad");
        gSdk_commandid.put(14002,"externalPinpadLoadMk");
        gSdk_commandid.put(14003,"externalPinpadLoadWK");
        gSdk_commandid.put(14004,"externalPinpadCalMac");
        gSdk_commandid.put(14005,"externalPinpadEncry");
        gSdk_commandid.put(14006,"externalPinpadDecry");
        gSdk_commandid.put(14007,"cancelPinInput");
        gSdk_commandid.put(14008,"externalPinpadSign");
        gSdk_commandid.put(14009,"lcdDisplay");
        gSdk_commandid.put(14010,"clearLCD");
        gSdk_commandid.put(14011,"getSn");




    }

    /** 返回id对应的方法名和类名(不同类名存在相同方法名及参数列表) */
    public static String fromSdkCommandId(int id) {
        if (gSdk_commandid.get(id) != null)
            return gSdk_commandid.get(id);
        else
            return null;
    }


    // 通过类型初始化
    public static Object sdkInterfaceInit(Context c)
    {
        if(gSdk_Obj==null)
        {
            LoggerUtil.d("new SdkInterface");
            SdkExecutors.setThreadPoolRunning(true);
            SdkInterface sdkInterface = new SdkInterface(c);
            gSdk_Obj = sdkInterface;
        }
        return gSdk_Obj;
    }
}
