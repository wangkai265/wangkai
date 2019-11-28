package com.newland.intelligentserver.sdk;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.newland.os.NlBuild;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;

import com.newland.common.RunningModel;
import com.newland.emv.jni.service.EmvJNIService;
import com.newland.industryic.CPUCard;
import com.newland.industryic.IndustryICCardImpl;
import com.newland.intelligentserver.cons.ConstJdk;
import com.newland.intelligentserver.util.BackBean;
import com.newland.intelligentserver.util.LoggerUtil;
import com.newland.intelligentserver.util.ParamEnum;
import com.newland.intelligentserver.util.SwipBack;
import com.newland.intelligentserver.util.Tools;
import com.newland.me.ConnUtils;
import com.newland.me.DeviceManager;
import com.newland.me.SupportMSDAlgorithm;
import com.newland.mtype.ConnectionCloseEvent;
import com.newland.mtype.Device;
import com.newland.mtype.DeviceInfo;
import com.newland.mtype.DeviceInvokeException;
import com.newland.mtype.DeviceRTException;
import com.newland.mtype.ExModuleType;
import com.newland.mtype.ModuleType;
import com.newland.mtype.ProcessTimeoutException;
import com.newland.mtype.common.MESeriesConst;
import com.newland.mtype.conn.DeviceConnParams;
import com.newland.mtype.event.DeviceEventListener;
import com.newland.mtype.module.common.cardreader.CommonCardType;
import com.newland.mtype.module.common.cardreader.K21CardReader;
import com.newland.mtype.module.common.cardreader.K21CardReaderEvent;
import com.newland.mtype.module.common.cardreader.OpenCardReaderResult;
import com.newland.mtype.module.common.cardreader.SearchCardRule;
import com.newland.mtype.module.common.emv.EmvModule;
import com.newland.mtype.module.common.emv.EmvTransController;
import com.newland.mtype.module.common.externalGuestDisplay.DigitalLedKeyboardListener;
import com.newland.mtype.module.common.externalGuestDisplay.ExternalGuestDisplay;
import com.newland.mtype.module.common.externalGuestDisplay.KeyBoardCode;
import com.newland.mtype.module.common.iccard.ICCardModule;
import com.newland.mtype.module.common.iccard.ICCardSlot;
import com.newland.mtype.module.common.iccard.ICCardSlotState;
import com.newland.mtype.module.common.iccard.ICCardType;
import com.newland.mtype.module.common.light.IndicatorLight;
import com.newland.mtype.module.common.light.LightType;
import com.newland.mtype.module.common.pin.CheckKeyResult;
import com.newland.mtype.module.common.pin.EncryptAlgorithm;
import com.newland.mtype.module.common.pin.EncryptType;
import com.newland.mtype.module.common.pin.K21Pininput;
import com.newland.mtype.module.common.pin.KSNKeyType;
import com.newland.mtype.module.common.pin.KSNLoadResult;
import com.newland.mtype.module.common.pin.KekUsingType;
import com.newland.mtype.module.common.pin.KeyManageType;
import com.newland.mtype.module.common.pin.KeyType;
import com.newland.mtype.module.common.pin.MacAlgorithm;
import com.newland.mtype.module.common.pin.TusnData;
import com.newland.mtype.module.common.pin.WorkingKey;
import com.newland.mtype.module.common.pin.WorkingKeyType;
import com.newland.mtype.module.common.printer.PrintContext;
import com.newland.mtype.module.common.printer.Printer;
import com.newland.mtype.module.common.printer.PrinterResult;
import com.newland.mtype.module.common.printer.PrinterStatus;
import com.newland.mtype.module.common.rfcard.K21RFCardModule;
import com.newland.mtype.module.common.rfcard.RFCardType;
import com.newland.mtype.module.common.rfcard.RFKeyMode;
import com.newland.mtype.module.common.rfcard.RFResult;
import com.newland.mtype.module.common.externalrfcard.ExternalRFCard;
import com.newland.mtype.module.common.security.K21SecurityModule;
import com.newland.mtype.module.common.security.SecurityModule;
import com.newland.mtype.module.common.serialport.SerialModule;
import com.newland.mtype.module.common.sm.RSAKeyPair;
import com.newland.mtype.module.common.sm.Sm2Key;
import com.newland.mtype.module.common.sm.SmModule;
import com.newland.mtype.module.common.storage.Storage;
import com.newland.mtype.module.common.storage.StorageResult;
import com.newland.mtype.module.common.swiper.K21Swiper;
import com.newland.mtype.module.common.swiper.MSDAlgorithm;
import com.newland.mtype.module.common.swiper.SwipResult;
import com.newland.mtype.module.common.swiper.SwipResultType;
import com.newland.mtype.module.common.swiper.SwiperReadModel;
import com.newland.mtype.module.common.externalScan.ExternalScan;
import com.newland.mtype.module.common.externalScan.BaudrateType;
import com.newland.mtype.module.common.externalScan.DataBitType;
import com.newland.mtype.module.common.externalScan.OddEvenCheckType;
import com.newland.mtype.module.common.externalScan.ScanDevColorLight;
import com.newland.mtype.module.common.externalScan.ScanDevErrorCode;
import com.newland.mtype.module.common.externalScan.ScanDevParam;
import com.newland.mtype.module.common.externalScan.ScanResultListener;
import com.newland.mtype.module.common.externalScan.StopBitType;
import com.newland.mtype.module.common.externalPin.ExternalPinInput;
import com.newland.mtype.module.common.externalPin.ExternalPinpadType;
import com.newland.mtype.module.common.externalPin.ExtTransResult;
import com.newland.mtype.module.common.externalPin.TransResultCode;
//import com.newland.mtype.module.common.externalPin.BaudrateType;
//import com.newland.mtype.module.common.externalPin.DataBitType;
import com.newland.mtype.module.common.externalsignature.ExternalSignature;
import com.newland.mtype.tlv.TLVPackage;
import com.newland.mtype.util.ISOUtils;
import com.newland.mtypex.nseries.NSConnV100ConnParams;
import com.newland.mtypex.nseries3.NS3ConnParams;
import com.newland.ndk.AlgN;
import com.newland.ndk.NdkApiManager;
import com.newland.ndk.RfCard;
import com.newland.ndk.SecN;
import com.newland.ndk.h.ST_SEC_KCV_INFO;
import com.newland.ndk.h.ST_SEC_KEY_INFO;
import com.newland.ndk.param.FelicaParam;


public class SdkInterface implements ConstJdk
{
    private static final String K21_DRIVER_NAME = "com.newland.me.K21Driver";
    private HashMap<ModuleName, Object> sdkModules = new HashMap<ModuleName, Object>();
    private static DeviceManager deviceManager;
    private DeviceConnParams deviceConnParams = null;
    private Context mContext;
    private BackBean mBackBean = new BackBean();
    private Object lockObj = new Object();

    public enum ModuleName
    {
        Light,CardReader,IcCard,Memory,Printer,Storage,Swiper,Security,RfCard,RfModule,PinModule,SmModule,GuestDisplay,
        ExternalScan,ExternalPin,ExternalSignature,ExternalRFCard,EmvModule
    }

    public SdkInterface(Context context)
    {
        this.mContext = context;
    }

    // 与K21模块建立连接
    public int connectDevice() {
        LoggerUtil.d("Connect Deivce");
        int ret = SDK_OK;
        try {
            deviceManager = ConnUtils.getDeviceManager();
            initConnParams();
            deviceManager.init(mContext, K21_DRIVER_NAME, deviceConnParams, new DeviceEventListener<ConnectionCloseEvent>() {

                @Override
                public void onEvent(ConnectionCloseEvent event, Handler handler) {
                    LoggerUtil.i("onEvent");
                    if (event.isSuccess()) {
                        LoggerUtil.i("connect succ");
                        initLightModule();
                    }
                    if (event.isFailed()) {
                        LoggerUtil.i("connect fail");
                    }
                }
                @Override
                public Handler getUIHandler() {
                            return null;
                        }
            });
            LoggerUtil.i("enter conn");
            deviceManager.connect();
            LoggerUtil.i("end conn device");
        } catch (Exception e1) {
            e1.printStackTrace();
            ret = SDK_Exception;
        }
        return ret;
    }

    // 断开与K21端的连接
    public int disconnect()
    {
        int ret = SDK_OK;
        try {
            if (deviceManager != null) {
                deviceManager.disconnect();
                deviceManager = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            ret = SDK_Exception;
        }
        return ret;
    }

    private void initConnParams(){
        String current_driver_version = NlBuild.VERSION.NL_FIRMWARE;
        if(SDKVersion()=="SDK2.0"){
            deviceConnParams = new NSConnV100ConnParams();
        }else if(SDKVersion()=="SDK3.0"){
            deviceConnParams = new NS3ConnParams();
        }else{			//unkonwn
            if("SA1".equals(NlBuild.VERSION.NL_HARDWARE_ID) && Build.MODEL.equals("N900")){ //900 3G Only supports 2.0
                deviceConnParams = new NSConnV100ConnParams();
                return;
            }
            if(Build.MODEL.equals("N900")){
                if(current_driver_version.equals("V2.0.28")||current_driver_version.equals("V2.1.03")||current_driver_version.equals("V2.1.05")||current_driver_version.equals("V2.1.09")||current_driver_version.equals("V2.1.18")||current_driver_version.equals("V2.1.27")||current_driver_version.equals("V2.1.37")||current_driver_version.equals("V2.1.49")||current_driver_version.equals("V2.1.53")||current_driver_version.equals("V2.1.58")
                        ||current_driver_version.equals("V2.1.62")||current_driver_version.equals("V2.0.16")||current_driver_version.equals("V2.1.17")||current_driver_version.equals("V2.1.21")||current_driver_version.equals("V2.1.23")||current_driver_version.equals("V2.1.24")||current_driver_version.equals("V2.1.29")||current_driver_version.equals("V2.1.31")||current_driver_version.equals("V2.1.32")||current_driver_version.equals("V2.1.40")||current_driver_version.equals("V2.1.41")

                        ||current_driver_version.equals("V2.1.44")||current_driver_version.equals("V2.0.45")||current_driver_version.equals("V2.1.46")||current_driver_version.equals("V2.1.48")||current_driver_version.equals("V2.1.51")||current_driver_version.equals("V2.1.55")||current_driver_version.equals("V2.1.56")||current_driver_version.equals("V2.1.60")){
                    deviceConnParams = new NS3ConnParams(); //3.0 connection
                }else{
                    deviceConnParams = new NSConnV100ConnParams(); //2.0 connect default
                }

            }else if(Build.MODEL.equals("N910")){
                if(current_driver_version.equals("V2.0.28")||current_driver_version.equals("V2.1.03")||current_driver_version.equals("V2.1.05")||current_driver_version.equals("V2.1.09")||current_driver_version.equals("V2.1.18")||current_driver_version.equals("V2.1.27")||current_driver_version.equals("V2.1.35")||current_driver_version.equals("V2.1.40")||current_driver_version.equals("V2.1.52")||current_driver_version.equals("V2.1.54")
                        ||current_driver_version.equals("V2.1.64")||current_driver_version.equals("V2.0.16")||current_driver_version.equals("V2.1.13")||current_driver_version.equals("V2.1.21")||current_driver_version.equals("V2.1.23")||current_driver_version.equals("V2.1.24")||current_driver_version.equals("V2.1.29")||current_driver_version.equals("V2.1.30")||current_driver_version.equals("V2.1.32")||current_driver_version.equals("V2.1.43")||current_driver_version.equals("V2.0.33")
                        ||current_driver_version.equals("V2.1.44")||current_driver_version.equals("V2.0.45")||current_driver_version.equals("V2.1.37")||current_driver_version.equals("V2.0.36")||current_driver_version.equals("V2.1.66")||current_driver_version.equals("V2.1.67")||current_driver_version.equals("V2.1.68")||current_driver_version.equals("V2.1.71")||current_driver_version.equals("V2.1.72")||current_driver_version.equals("V2.3.00")||current_driver_version.equals("V2.1.57")||current_driver_version.equals("V2.1.26")){
                    deviceConnParams = new NS3ConnParams();
                }else{
                    LoggerUtil.d("NSConnV100ConnParams conn");
                    deviceConnParams = new NSConnV100ConnParams(); //2.0 connect default
                }
            }else {
                deviceConnParams = new NS3ConnParams();
            }
        }
    }

    private static String SDKVersion() {
        String version = "unknown";
        version = getProperties("ro.build.newland_sdk");
        if ("unknown".equals(version)) {
            version = getProperties("ro.build.customer_id");
            if ("unknown".equals(version)) {
                return  version;
            } else if ("ChinaUms".equals(version) || "SDK_2.0".equals(version) || "AliQianNiu".equals(version)) {
                return "SDK2.0";
            } else {
                return "SDK3.0";
            }
        } else {
            return version;
        }
    }

    private static String getProperties(String key) {
        String defaultValue = "unknown";
        String value = defaultValue;
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class, String.class);
            value = (String) (get.invoke(c, key, defaultValue));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }


    // LED模块开始
    public void initLightModule()
    {
        if(deviceManager.getDevice()==null)
            LoggerUtil.d("device is null");
        IndicatorLight indicatorLight=(IndicatorLight) deviceManager.getDevice().getStandardModule(ModuleType.COMMON_INDICATOR_LIGHT);
        sdkModules.put(ModuleName.Light, indicatorLight);
    }

    public boolean blinkLED(byte[] types)
    {
        LightType[] lightTypes = new LightType[types.length];
        for(int i=0;i<types.length;i++)
        {
            lightTypes[i] = LightType.values()[types[i]];
        }
        return ((IndicatorLight)sdkModules.get(ModuleName.Light)).blinkLight(lightTypes);
    }

    public boolean turnOnLED(byte[] types)
    {
        LightType[] lightTypes = new LightType[types.length];
        for(int i=0;i<types.length;i++)
        {
            lightTypes[i] = LightType.values()[types[i]];
        }
        return ((IndicatorLight)sdkModules.get(ModuleName.Light)).turnOnLight(lightTypes);
    }

    public boolean tunOffLED(byte[] types)
    {
        LightType[] lightTypes = new LightType[types.length];
        for(int i=0;i<types.length;i++)
        {
            lightTypes[i] = LightType.values()[types[i]];
        }
        return ((IndicatorLight)sdkModules.get(ModuleName.Light)).turnOffLight(lightTypes);
    }

    // 读卡器模块开始
    public void initCardReaderModule()
    {
        LoggerUtil.i("initCardReaderModule"+deviceManager.getDeviceConnState());
        K21CardReader cardReader=(K21CardReader) deviceManager.getDevice().getStandardModule(ModuleType.COMMON_CARDREADER);
        sdkModules.put(ModuleName.CardReader, cardReader);
    }

    public BackBean openCardReader(byte[] card_types,int timeout,boolean rf_first)
    {
        ModuleType[] openReaders = new ModuleType[card_types.length];
        for(int i=0;i<card_types.length;i++)
        {
            openReaders[i] = ModuleType.values()[card_types[i]];
        }
        SearchCardRule searchCardRule = rf_first?SearchCardRule.RFCARD_FIRST:SearchCardRule.NORMAL;
        ((K21CardReader)sdkModules.get(ModuleName.CardReader)).openCardReader(openReaders,true,timeout, TimeUnit.SECONDS,cardReaderEvent,searchCardRule);
        LoggerUtil.i("阻塞等待");
        synchronized (lockObj)
        {
            try
            {
                lockObj.wait(timeout*1000);
            }catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
        LoggerUtil.i("阻塞退出");
        return mBackBean;
    }

    public void closeCardReader()
    {
        ((K21CardReader)sdkModules.get(ModuleName.CardReader)).cancelCardRead();
        synchronized (lockObj)
        {
            lockObj.notify();
        }
    }

    private DeviceEventListener<K21CardReaderEvent> cardReaderEvent = new DeviceEventListener<K21CardReaderEvent>() {
        @Override
        public void onEvent(K21CardReaderEvent openCardReaderEvent, Handler handler) {
            LoggerUtil.i("监听到事件触发");
            mBackBean.setBuffer(new byte[4]);// 前置，设置为空
            OpenCardReaderResult cardResult = openCardReaderEvent.getOpenCardReaderResult();
            CommonCardType[] openedModuleTypes = null;
            if(openCardReaderEvent.isFailed())// 失败
            {
                if(openCardReaderEvent.getException() instanceof ProcessTimeoutException)
                {
                    mBackBean.setRet(Process_Timeout_Exception);
                }
                mBackBean.setRet(SDK_Reader_Open_Failed);
            }
            if(cardResult==null||(openedModuleTypes=cardResult.getResponseCardTypes())==null||openedModuleTypes.length<=0)
                mBackBean.setRet(SDK_Reader_Open_Failed);
            else if(openCardReaderEvent.isSuccess())
            {
                switch (openCardReaderEvent.getOpenCardReaderResult().getResponseCardTypes()[0])
                {
                    case MSCARD:
                        mBackBean.setRet(ParamEnum.Card_Type.MSCARD.ordinal());
                        break;
                    case ICCARD:
                        mBackBean.setRet(ParamEnum.Card_Type.ICCARD.ordinal());
                        break;
                    case RFCARD:
                        RFCardType rfCardType = openCardReaderEvent.getOpenCardReaderResult().getResponseRFCardType();
                        if(null==rfCardType)
                        {
                            mBackBean.setRet(ParamEnum.Card_Type.RFCARD.ordinal());
                            break;
                        }
                        switch (rfCardType)
                        {
                            case ACARD:
                                mBackBean.setRet(ParamEnum.Card_Type.ACARD.ordinal());
                                break;
                            case BCARD:
                                mBackBean.setRet(ParamEnum.Card_Type.BCARD.ordinal());
                                break;
                            case M1CARD:
                                mBackBean.setRet(ParamEnum.Card_Type.M1CARD.ordinal());
                                byte sak = openCardReaderEvent.getOpenCardReaderResult().getSAK();
                                mBackBean.setBuffer(new byte[]{sak});
                                break;
                            default:
                                mBackBean.setRet(ParamEnum.Card_Type.UNKNOWN.ordinal());
                                break;
                        }
                }
            }
            synchronized (lockObj)// 监听结束之后唤醒阻塞的操作
            {
                lockObj.notify();
            }
        }

        @Override
        public Handler getUIHandler() {
            return null;
        }
    };

    // IC卡模块开始
    public void initIccardModule()
    {
        ICCardModule iCCardModule=(ICCardModule) deviceManager.getDevice().getStandardModule(ModuleType.COMMON_ICCARDREADER);
        IndustryICCardImpl icCardImpl = new IndustryICCardImpl();
        sdkModules.put(ModuleName.IcCard, iCCardModule);
        sdkModules.put(ModuleName.Memory,icCardImpl);
    }

    private ICCardSlot icCardSlot = ICCardSlot.IC1;
    private ICCardType icCardType = ICCardType.CPUCARD;
    public byte[] icCardPowerOn(int ic_type)
    {
        icCardSlot =  ICCardSlot.values()[ic_type];
        icCardType = ICCardType.CPUCARD;
        return ((ICCardModule)sdkModules.get(ModuleName.IcCard)).powerOn(icCardSlot,icCardType);
    }

    public byte[] icCardCommunication(byte[] req,int timeout)
    {
        return ((ICCardModule)sdkModules.get(ModuleName.IcCard)).call(icCardSlot,icCardType,req,timeout,TimeUnit.SECONDS);
    }

    public String icCardSlotState(String key)
    {
        String cardState="NO_CARD";
        Map<ICCardSlot, ICCardSlotState> map = new HashMap<ICCardSlot, ICCardSlotState>();
        map = ((ICCardModule)sdkModules.get(ModuleName.IcCard)).checkSlotsState();
        for (Map.Entry<ICCardSlot, ICCardSlotState> entry : map.entrySet()) {
            if (entry.getKey() != null)
            {
                if(entry.getKey().toString().equals(key))
                {
                    cardState = entry.getValue().toString();
                }
            }
        }
        return cardState;
    }

    public void icCardPowerOff()
    {
        ICCardType icCardType = ICCardType.CPUCARD;
        ((ICCardModule)sdkModules.get(ModuleName.IcCard)).powerOff(icCardSlot,icCardType);
    }

    public  int memoryCardOpen(int memory_type)
    {
        return ((IndustryICCardImpl)sdkModules.get(ModuleName.Memory)).open(memory_type);
    }

    /***
     *
     * @param memory_type:
     * @param atr:
     */
    public int memoryCardVerify(int memory_type,byte[] atr)
    {
        return ((IndustryICCardImpl)sdkModules.get(ModuleName.Memory)).openWithATRVerification(memory_type,atr);
    }

    public byte[] memoryCardRead(byte[] pwd,int address,int read_len)
    {
        return ((IndustryICCardImpl)sdkModules.get(ModuleName.Memory)).read(pwd,address,read_len);
    }

    public boolean memoryCardWrite(byte[] pwd,int address,byte[] data)
    {
        return ((IndustryICCardImpl)sdkModules.get(ModuleName.Memory)).write(pwd,address,data);
    }

    public boolean memoryCardChangePassword(byte[] oldPassWorld,byte[] newPassWord)
    {
        return ((IndustryICCardImpl)sdkModules.get(ModuleName.Memory)).changePassword(oldPassWorld,newPassWord);
    }

    public void memoryCardClose()
    {
        ((IndustryICCardImpl)sdkModules.get(ModuleName.Memory)).close();
    }

    // 打印模块开始
    public void initPrinterModule()
    {
        Printer printer=(Printer) deviceManager.getDevice().getStandardModule(ModuleType.COMMON_PRINTER);
        sdkModules.put(ModuleName.Printer, printer);
    }

    public int getPrinterState()
    {
        PrinterStatus status = ((Printer)sdkModules.get(ModuleName.Printer)).getStatus();
        return status.ordinal();
    }

    public int printByScript(String scripBuffer,int timeout,String[] bitmapKeys,String[] bitmapPaths)
    {

        String fontsPath = ((Printer)sdkModules.get(ModuleName.Printer)).getFontsPath(mContext, "DroidSansFallback.ttf", false);
        LoggerUtil.d("fontPath="+fontsPath);
        Map<String,Bitmap> map =new HashMap<>();
        if(bitmapKeys!=null)// 此时不需要用到图片
        {
            for (int i=0;i<bitmapKeys.length;i++)
            {
                LoggerUtil.d("bitmapKeys="+bitmapKeys[i]);
                Bitmap bitmap = BitmapFactory.decodeFile(bitmapPaths[i]);
                map.put(bitmapKeys[i],bitmap);
            }
        }
        PrinterResult printerResult = ((Printer)sdkModules.get(ModuleName.Printer)).printByScript(PrintContext.defaultContext(),scripBuffer,map,timeout,TimeUnit.SECONDS);
        return printerResult.ordinal();
    }

    // 存储模块开始
    public void initStorageModule()
    {
        Storage storage=(Storage) deviceManager.getDevice().getStandardModule(ModuleType.COMMON_STORAGE);
        sdkModules.put(ModuleName.Storage, storage);
    }

    /***
     *初始化存储记录
     * @param recordName 记录名
     * @param recordLen  每条记录长度
     * @param param1Offset 检索字段1在记录中的偏移
     * @param param1Len 检索字段1的长度
     * @param param2Offset 检索字段2在记录中的偏移
     * @param param2Len 检索字段2的长度
     */
    public boolean initStorageRecord(String recordName,int recordLen,int param1Offset,int param1Len,int param2Offset,int param2Len)
    {
        return ((Storage)sdkModules.get(ModuleName.Storage)).initializeRecord(recordName,recordLen,param1Offset,param1Len,param2Offset,param2Len);
    }

    /***
     * 获取存储记录\n
     * @param recordName 记录名
     */
    public int getStorageRecordNum(String recordName)
    {
        return ((Storage)sdkModules.get(ModuleName.Storage)).fetchRecordCount(recordName);
    }

    /***
     * 增加存储记录
     * @param recordName 记录名
     * @param content   记录内容
     */
    public int addStorageRecord(String recordName,byte[] content)
    {
        StorageResult storageResult = ((Storage)sdkModules.get(ModuleName.Storage)).addRecord(recordName,content);
        return storageResult.ordinal();
    }

    /***
     * 获取存储记录
     * @param recordName 记录名
     * @param recordNo 记录号
     * @param checkParams1 检索字段1
     * @param checkParam2   检索字段2
     */
    public byte[] getStorageRecord(String recordName,int recordNo,String checkParams1,String checkParam2)
    {
        byte[] record = ((Storage)sdkModules.get(ModuleName.Storage)).fetchRecord(recordName,recordNo,checkParams1,checkParam2);
        return record;
    }

    /***
     * 更新存储记录
     * @param recordName 记录名
     * @param recordNo 记录号
     * @param checkParams1 检索字段1
     * @param checkParams2 检索字段2
     * @param content 记录内容
     */
    public int updateStorageRecord(String recordName,int recordNo,String checkParams1,String checkParams2,byte[] content)
    {
        StorageResult storageResult = ((Storage)sdkModules.get(ModuleName.Storage)).updateRecord(recordName,recordNo,checkParams1,checkParams2,content);
        return storageResult.ordinal();
    }

    // 终端管理模块开始
    /***
     * 设置终端时间
     * @param year 年
     * @param date 日期
     * @param time 时间
     */
   public void setTerminalTime(int year,String date,String time)
   {
       Calendar c = Calendar.getInstance();
       c.set(Calendar.YEAR,year);
       int month = Integer.parseInt(date.substring(0,2));
       c.set(Calendar.MONTH,month-1);
       int day = Integer.parseInt(date.substring(2,4));
       c.set(Calendar.DAY_OF_MONTH,day);
       int hourOfDay = Integer.parseInt(time.substring(0,2));
       c.set(Calendar.HOUR_OF_DAY,hourOfDay);
       int minute = Integer.parseInt(time.substring(2,4));
       c.set(Calendar.MINUTE,minute);
       int second = Integer.parseInt(time.substring(4,6));
       c.set(Calendar.SECOND,second);
       long when = c.getTimeInMillis();
       for(int i=0;i<3;i++)
       {
           if(SystemClock.setCurrentTimeMillis(when))
               break;
       }
       Date nowDate = new Date();
       deviceManager.getDevice().setDeviceDate(nowDate);
   }

    /***
     * 获取终端时间
     */
   public String getTerminalTime()
   {
       Date date = deviceManager.getDevice().getDeviceDate();
       SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
       String dateStr = formatter.format(date);
       return dateStr;
   }

   public void setTerminalParam(int param_tag,byte[] param_value)
   {
       TLVPackage tlvPackage = ISOUtils.newTlvPackage();
       tlvPackage.append(param_tag,param_value);
       deviceManager.getDevice().setDeviceParams(tlvPackage);
   }

   public  byte[] getTerminalParam(int param_tag)
   {
       TLVPackage pack = deviceManager.getDevice().getDeviceParams(param_tag);
       return pack.getValue(getOriginTag(param_tag));
   }

   private int getOriginTag(int tag)
   {
       if((tag&0xFF0000)==0xFF0000)
           return tag&0xFFFF;
       else if((tag&0xFF00)==0xFF00)
           return tag&0xFF;
       return tag;
   }

   public void reset()
   {
       deviceManager.getDevice().reset();
   }

    // 初始化磁卡模块
    public  void initSwipModule()
    {
        K21Swiper k21Swiper = (K21Swiper) deviceManager.getDevice().getStandardModule(ModuleType.COMMON_SWIPER);
        sdkModules.put(ModuleName.Swiper, k21Swiper);
    }

    // 磁道信息(掩码) 要先安装磁道密钥
    public SwipBack readEncryResultByMask(byte[] swiperModel,int workIndex,byte[] acctMask,String pay_model)
    {
        SwipBack swipBack = new SwipBack();
        SwiperReadModel[] readModels = new SwiperReadModel[swiperModel.length];
        MSDAlgorithm msdAlgorithm = SupportMSDAlgorithm.getMSDAlgorithm(pay_model);
        for(int i=0;i<swiperModel.length;i++)
        {
            readModels[i] = SwiperReadModel.values()[swiperModel[i]];
        }
        SwipResult swipResult = ((K21Swiper)sdkModules.get(ModuleName.Swiper)).readEncryptResult(readModels,new WorkingKey(workIndex),acctMask,msdAlgorithm);
        if(null!=swipResult)
            swipBack.setRet(swipResult.getRsltType().ordinal());
        if(null!=swipResult&& swipResult.getRsltType()==SwipResultType.SUCCESS)
        {
            swipBack.setFirstTrack(swipResult.getFirstTrackData());
            swipBack.setSecondTrack(swipResult.getSecondTrackData());
            swipBack.setThirdTrack(swipResult.getThirdTrackData());
        }
        return swipBack;
    }

    // 磁道信息(加密)
    public SwipBack readEncryResult(byte[] swiperModel,int workIndex,String pay_model)
    {
        SwipBack swipBack = new SwipBack();
        SwiperReadModel[] readModels = new SwiperReadModel[swiperModel.length];
        MSDAlgorithm msdAlgorithm = SupportMSDAlgorithm.getMSDAlgorithm(pay_model);
        for(int i=0;i<swiperModel.length;i++)
        {
            readModels[i] = SwiperReadModel.values()[swiperModel[i]];
        }
        SwipResult swipResult = ((K21Swiper)sdkModules.get(ModuleName.Swiper)).readEncryptResult(readModels,new WorkingKey(workIndex),msdAlgorithm);
        if(null!=swipResult)
            swipBack.setRet(swipResult.getRsltType().ordinal());
        if(null!=swipResult&& swipResult.getRsltType()==SwipResultType.SUCCESS)
        {
            swipBack.setFirstTrack(swipResult.getFirstTrackData());
            swipBack.setSecondTrack(swipResult.getSecondTrackData());
            swipBack.setThirdTrack(swipResult.getThirdTrackData());
        }
        return swipBack;
    }

    // 磁道信息(明文)
    public SwipBack readPlainResult(byte[] swiperModel)
    {
        SwipBack swipBack = new SwipBack();
        SwiperReadModel[] readModels = new SwiperReadModel[swiperModel.length];
        for(int i=0;i<swiperModel.length;i++)
        {
            readModels[i] = SwiperReadModel.values()[swiperModel[i]];
        }
        SwipResult swipResult = ((K21Swiper)sdkModules.get(ModuleName.Swiper)).readPlainResult(readModels);
        if(null!=swipResult)
            swipBack.setRet(swipResult.getRsltType().ordinal());
        if(null!=swipResult&&swipResult.getRsltType()== SwipResultType.SUCCESS)// 成功的情况
        {
            swipBack.setFirstTrack(swipResult.getFirstTrackData());
            swipBack.setSecondTrack(swipResult.getSecondTrackData());
            swipBack.setThirdTrack(swipResult.getThirdTrackData());
        }
        return swipBack;
    }

    // 安全认证模块开始
    public void initSecurityModule()
    {
        K21SecurityModule securityModule =  (K21SecurityModule) deviceManager.getDevice().getStandardModule(ModuleType.COMMON_SECURITY);
        sdkModules.put(ModuleName.Security, securityModule);
    }

    // 获取设备信息
    public String getDeviceInfo()
    {
        DeviceInfo deviceInfo = ((K21SecurityModule)sdkModules.get(ModuleName.Security)).getDeviceInfo();
        String custom_id = deviceInfo.getCustomerID();
        String csn_no = null;
        try{
            csn_no = (deviceInfo.getCSNData()==null)?null:new String(deviceInfo.getCSNData(),"GBK");
        }catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String ksn_no = deviceInfo.getKSN();
        String app_version = deviceInfo.getAppVer();
        String boot_version = deviceInfo.getBootVersion();
        String model = deviceInfo.getModel();
        String sn_no = deviceInfo.getSN();
        boolean isAudio = deviceInfo.isSupportAudio();
        boolean isBlue = deviceInfo.isSupportBlueTooth();
        boolean isIc = deviceInfo.isSupportICCard();
        boolean isLcd = deviceInfo.isSupportLCD();
        boolean isMag = deviceInfo.isSupportMagCard();
        boolean isOffLine = deviceInfo.isSupportOffLine();
        boolean isPrinter = deviceInfo.isSupportPrint();
        boolean isRf = deviceInfo.isSupportQuickPass();
        boolean isUsb = deviceInfo.isSupportUSB();
        boolean isStatues = deviceInfo.isFactoryModel();
        boolean isMainKey = deviceInfo.isMainkeyLoaded();
        boolean isWorkingKey = deviceInfo.isWorkingkeyLoaded();
        boolean isDukpt = deviceInfo.isDUKPTkeyLoaded();
        boolean isGps = deviceInfo.isSupportGPS();
        boolean isPinpad = deviceInfo.isSupportPinpadPort();
        boolean isRs232 = deviceInfo.isSupport232Port();
        boolean isEthernet = deviceInfo.isSupportEthernet();
        boolean isSam = deviceInfo.isSupportSam();
        boolean isCashBox = deviceInfo.isSupportCashBox();
        boolean isCamera = deviceInfo.isSupportCamera();

        return  "custom_id:"+custom_id+",csn_no:"+csn_no+",ksn_no:"+ksn_no+",app_version:"+app_version+",boot_version:"+boot_version+
                ",model:"+model+",sn_no:"+sn_no+",isAudio:"+isAudio+",isBlue:"+isBlue+",isIc:"+isIc+",isLcd:"+isLcd+",isMag:"+isMag+",isOffLine:"+
                isOffLine+",isPrinter:"+isPrinter+",isRf:"+isRf+",isUsb:"+isUsb+",isStatues:"+isStatues+",isMainKey:"+isMainKey+",isWorkingKey:"+isWorkingKey+
                ",isDukpt:"+isDukpt+",isGps:"+isGps+",isPinpad:"+isPinpad+",isRs232:"+isRs232+",isEthernet:"+isEthernet+",isSam:"+isSam+",isCashBox:"+isCashBox+
                ",isCamera:"+isCamera;
    }

    // 获取随机数
    public byte[] getRadomNumber()
    {
        return ((K21SecurityModule)sdkModules.get(ModuleName.Security)).getSecurityRandom();
    }

    // 获取设备TUSN
    public String getTusn()
    {
        return ((K21SecurityModule)sdkModules.get(ModuleName.Security)).getPosTusn();
    }

    // 射频模块开始
    public void initRfModule()
    {
        K21RFCardModule rfCardModule = (K21RFCardModule) deviceManager.getDevice().getStandardModule(ModuleType.COMMON_RFCARDREADER);
        RfCard rfCard = NdkApiManager.getNdkApiManager().getRfCard();
        ExternalRFCard externalRFCard = (ExternalRFCard) deviceManager.getDevice().getExModule(ExModuleType.RFCARD);
        EmvModule emvModule = (EmvModule) deviceManager.getDevice().getStandardModule(ModuleType.COMMON_EMV);
        sdkModules.put(ModuleName.RfModule, rfCardModule);
        sdkModules.put(ModuleName.RfCard, rfCard);
        sdkModules.put(ModuleName.ExternalRFCard, externalRFCard);
        sdkModules.put(ModuleName.EmvModule,emvModule);
    }

    // 非接卡上电
    public String rfPowerOn(byte[] rfTypeList,int timeout)
    {
        RFCardType[] rfCardTypes = new RFCardType[rfTypeList.length];
        String result = null;
        for(int i=0;i<rfCardTypes.length;i++)
        {
            rfCardTypes[i] = RFCardType.values()[rfTypeList[i]];
        }
        RFResult rfResult = ((K21RFCardModule)sdkModules.get(ModuleName.RfModule)).powerOn(rfCardTypes,timeout,TimeUnit.SECONDS);
        if(null!=rfResult&&rfResult.getQpCardType()!=null)
        {
            String rfType = rfResult.getQpCardType().toString();
            String serialNo = ISOUtils.hexString(rfResult.getCardSerialNo());
            String ATQA = rfResult.getATQA()==null?null:new String(rfResult.getATQA());
            result = "rfType:"+rfType+",serialNo:"+serialNo+",ATQA:"+ATQA;
        }
        return result;
    }

    // 非接通讯
    public byte[] rfcardCommunication(byte[] req,int timeout)
    {
        return ((K21RFCardModule)sdkModules.get(ModuleName.RfModule)).call(req,timeout,TimeUnit.SECONDS);
    }

    // 非接下电
    public void rfcardPowerOff()
    {
        ((K21RFCardModule)sdkModules.get(ModuleName.RfModule)).powerOff(0xFFFF);
    }

    // 非接卡是否在位
    public boolean rfcardState()
    {
        return ((K21RFCardModule)sdkModules.get(ModuleName.RfModule)).isRfcardExist();
    }

    // M1卡寻卡上电
    public byte[] M1PowerOn(int m1_type,int timeout)
    {
        RFResult rfResult = ((K21RFCardModule)sdkModules.get(ModuleName.RfModule)).powerOn(new RFCardType[]{RFCardType.M1CARD},timeout,TimeUnit.SECONDS,(byte)m1_type);
        return rfResult.getCardSerialNo();
    }

    // 外部密钥认证
    public void authenticateByExtendKey(int keyMode,byte[] serialNo,int block,byte[] key)
    {
        RFKeyMode rfKeyMode = RFKeyMode.values()[keyMode];
        ((K21RFCardModule)sdkModules.get(ModuleName.RfModule)).authenticateByExtendKey(rfKeyMode,serialNo,block,key);
    }

    // 写块
    public void m1CardWrite(int block,byte[] input)
    {
        ((K21RFCardModule)sdkModules.get(ModuleName.RfModule)).writeDataBlock(block,input);
    }

    // 读块
    public byte[] m1CardRead(int block)
    {
        return ((K21RFCardModule)sdkModules.get(ModuleName.RfModule)).readDataBlock(block);
    }

    // 增量
    public void m1CardIncrease(int block,byte[] input)
    {
        ((K21RFCardModule)sdkModules.get(ModuleName.RfModule)).incrementOperation(block,input);
    }

    // 减量
    public void m1CardDecrease(int block,byte[] input)
    {
        ((K21RFCardModule)sdkModules.get(ModuleName.RfModule)).decrementOperation(block,input);
    }

    // felica上电
    public BackBean felicaCardPowerOn(byte[] systemcode,int requestcode,int timeslot)
    {
        BackBean backBean = new BackBean();
        backBean.setBuffer(new byte[4]);
        FelicaParam felicaParam = new FelicaParam(systemcode,(byte)requestcode,(byte)timeslot);
        ((RfCard)sdkModules.get(ModuleName.RfCard)).NDK_RfidInit(null);
        ((RfCard)sdkModules.get(ModuleName.RfCard)).NDK_RfidOpenRf();
        byte[] recvBuf = new byte[512];
        int[] recvLen = new int[1];
        int ret =  ((RfCard)sdkModules.get(ModuleName.RfCard)).NDK_FelicaPoll(felicaParam,recvBuf,recvLen);
        backBean.setRet(ret);
        if(ret==0)
        {
            backBean.setBuffer(Tools.subBytes(recvBuf,0,recvLen[0]));
        }
        return backBean;
    }

    // felica通讯
    public BackBean felicaCardCommunication(byte[] idmAndPMm)
    {
        BackBean backBean = new BackBean();
        backBean.setBuffer(new byte[4]);
        byte[] req = new byte[16];
        byte[] recvBuf = new byte[512];
        int[] recvLen = new int[1];
        req[0] = 16;
        req[1] = 0x06;
        req[10] = 0x01;
        req[11] = 0x09;
        req[12] = 0x00;
        req[13] = 0x01;
        req[14] = (byte)0x80;
        req[15] = 0x00;
        System.arraycopy(idmAndPMm,2,req,2,8);
        int ret = ((RfCard)sdkModules.get(ModuleName.RfCard)).NDK_RfidFelicaApdu(req.length,req,recvLen,recvBuf);
        backBean.setRet(ret);
        if(ret == 0)
        {
            backBean.setBuffer(Tools.subBytes(recvBuf,0,recvLen[0]));
        }
        return backBean;
    }

    // 射频卡下电
    public int powerOff_common()
    {
        return ((RfCard)sdkModules.get(ModuleName.RfCard)).NDK_RfidCloseRf();
    }

    // m0卡上电
    public String m0CardPowerOn()
    {
        String result = null;
        byte[] uid = new byte[512];
        int[] uidLen = new int[1];
        byte[] sak = new byte[1];
        int ret = ((RfCard)sdkModules.get(ModuleName.RfCard)).NDK_MifareActive((byte)0x52,uid,uidLen,sak);
        if(ret==0)
        {
            result = "ret:"+ret+",uid:"+ISOUtils.hexString(uid,0,uidLen[0])+",sak:"+sak[0];
        }
        else
            result = "ret:"+ret;
        return result;
    }

    // m0卡认证
    public String m0CardAuth()
    {
        String result=null;
        byte[] ek_RandB = new byte[8];
        byte[] RandB = new byte[9];
        byte[] RandBI = new byte[8];
        byte[] RandA = new byte[8];
        byte[] RandAI = new byte[8];
        byte[] RandA_BI = new byte[16];
        byte[] ek_RandA_BI = new byte[16];
        byte[] ek_RandAI = new byte[8];
        byte[] initIV = new byte[8];
        byte[] sendbuff = new byte[30];
        int[] recvLen0 = new int[1];
        byte[]  recvbuff = new byte[64];
        byte[] psKey = ISOUtils.hex2byte("49454D4B41455242214E4143554F5946");
        sendbuff[0] = 0x1A;
        sendbuff[1] = 0x00;
        int nRet;
        nRet = ((RfCard)sdkModules.get(ModuleName.RfCard)).NDK_M0Authen_Release(sendbuff.length,sendbuff,recvLen0,recvbuff);
        LoggerUtil.i("NDK_M0Authen_Release="+nRet);
        if(nRet!=0)
        {
            result = "ret:"+nRet;
            return result;
        }
        System.arraycopy(recvbuff,1,ek_RandB,0,8);
        nRet = TDES_ExtendEncrypt(ek_RandB,8,initIV,RandB,psKey,16,1);
        LoggerUtil.i("TDES_ExtendEncrypt="+nRet);
        if(nRet!=0)
        {
            result = "ret:"+nRet;
            return result;
        }
        NdkApiManager ndkApiManager = NdkApiManager.getNdkApiManager();
        nRet = ndkApiManager.getSecN().NDK_SecGetRandom(8,RandA);

        RandB[8] = RandB[0];
        System.arraycopy(RandB,1,RandBI,0,8);
        System.arraycopy(ek_RandB,0,initIV,0,8);
        System.arraycopy(RandA,0,RandA_BI,0,8);
        System.arraycopy(RandBI,0,RandA_BI,8,8);
        nRet = TDES_ExtendEncrypt(RandA_BI,16,initIV,ek_RandA_BI,psKey,16,2);
        if(nRet!=0)
        {
            result = "ret:"+nRet;
            return result;
        }
        System.arraycopy(ek_RandA_BI,8,initIV,0,8);
        sendbuff = new byte[17];
        sendbuff[0] = (byte)0xAF;
        System.arraycopy(ek_RandA_BI,0,sendbuff,1,16);

        nRet = ((RfCard)sdkModules.get(ModuleName.RfCard)).NDK_M0Authen_Release(sendbuff.length,sendbuff,recvLen0,recvbuff);
        if(nRet!=0)
        {
            result = "ret:"+nRet;
            return result;
        }
        System.arraycopy(recvbuff,1,ek_RandAI,0,8);
        nRet = TDES_ExtendEncrypt(ek_RandAI,8,initIV,RandAI,psKey,16,1);
        if(nRet!=0)
        {
            result = "ret:"+nRet;
            return result;
        }
        if((RandA[0]!=RandAI[7]))
        {
            result = "ret:"+nRet;
            return result;
        }
        return result;
    }

    private int TDES_ExtendEncrypt(byte[] psDataIn,int inLen,byte[] initIV,byte[] psDataOut,byte[] psKey,int nKeyLen,int nMode)
    {
        byte[] inbuf = new byte[128];
        byte[] outbuf = new byte[128];
        byte[] iv = new byte[8];
        int i,j;
        int nRet = 0;
        NdkApiManager ndkApiManager = NdkApiManager.getNdkApiManager();
        AlgN algN = ndkApiManager.getAlgN();
        if((psDataIn == null)||(psDataOut == null)||(psKey == null)||(initIV == null))
            return -1;
        if((nKeyLen!=8)&&(nKeyLen!=16)&&(nKeyLen!=24))
            return -1;
        if((nMode!=2)&&(nMode!=1))// ALG_TDS_MODE_ENC||ALG_TDS_MODE_DEC
            return -1;
        if(inLen>128)
            return -1;
        else
            System.arraycopy(psDataIn,0,inbuf,0,inLen);
        System.arraycopy(initIV,0,iv,0,8);
        if(nMode==2) {
            for (i=0;i<inLen;i=i+8) {
                for (j=0;j<8;j++){
                    inbuf[i+j]^=iv[j];
                }
                byte[] inbuff = new byte[nKeyLen];
                byte[] outbuff = new byte[nKeyLen];
                System.arraycopy(inbuf,i,inbuff,0,nKeyLen);
                algN.NDK_AlgTDes(inbuf,outbuff,psKey,nKeyLen,0);
                if(nRet<0)
                    return nRet;
                System.arraycopy(outbuff,0,outbuf,i,nKeyLen);
                System.arraycopy(outbuf,i,iv,0,8);
            }
        }
        else
        {
            for (i=0;i<inLen;i=i+8){
                byte[] inbuff = new byte[nKeyLen];
                byte[] outbuff = new byte[nKeyLen];
                System.arraycopy(inbuf,i,inbuff,0,nKeyLen);
                algN.NDK_AlgTDes(inbuff,outbuff,psKey,nKeyLen,1);
                if(nRet<0)
                    return nRet;
                System.arraycopy(outbuff,0,outbuf,i,nKeyLen);
                for(j = 0; j < 8; j++){
                    outbuf[i + j] ^= iv[j];
                }
                System.arraycopy(inbuf,i,iv,0,8);
            }
        }
        System.arraycopy(outbuf,0,psDataOut,0,inLen);
        return 0;
    }

    // m0读
    public BackBean m0CardRead(int block)
    {
        BackBean backBean = new BackBean();
        backBean.setBuffer(new byte[4]);
        int[] recvLen = new int[1];
        byte[] recvBuf = new byte[512];
        int ret = ((RfCard)sdkModules.get(ModuleName.RfCard)).NDK_M0Read((byte) block,recvLen,recvBuf);
        backBean.setRet(ret);
        if(ret==0)
        {
            LoggerUtil.i("m0CardRead:"+ISOUtils.hexString(recvBuf,0,recvLen[0]));
            backBean.setBuffer(Tools.subBytes(recvBuf,0,recvLen[0]));
        }
        return backBean;
    }

    // m0写
    public int m0CardWrite(int block,byte[] input)
    {
        LoggerUtil.i("m0CardWrite："+input.length);
        return ((RfCard)sdkModules.get(ModuleName.RfCard)).NDK_M0Write((byte)block,input.length,input);
    }

    private int externaltype_sp10 = 1;
    private int portType_sp10 = 0;
    private int baudRate_sp10 = 9;
    //sp10上电
    public int sp10PowerOn(int external_types, int port_types, int baud_rate, byte[] timeout)
    {
        int ret;
        try{
            if(ExternalPinpadType.values()[external_types] != ExternalPinpadType.SP10 || ExternalPinpadType.values()[external_types] != ExternalPinpadType.SP100_OVERSEAS)
            {
                ret = SDK_ERR_PARA;
                return ret;
            }
            ((ExternalRFCard)sdkModules.get(ModuleName.ExternalRFCard)).setPinpadType(ExternalPinpadType.values()[external_types],port_types);
            ((ExternalRFCard)sdkModules.get(ModuleName.ExternalRFCard)).pinPadPortInit(
                    com.newland.mtype.module.common.externalPin.BaudrateType.values()[baud_rate],
                    com.newland.mtype.module.common.externalPin.DataBitType.DATA_BIT_8,
                    com.newland.mtype.module.common.externalPin.OddEvenCheckType.NO_CHECK,
                    com.newland.mtype.module.common.pin.StopBitType.STOP_BIT_ONE
            );
            ret = ((ExternalRFCard)sdkModules.get(ModuleName.ExternalRFCard)).powerOn(timeout)?0:1;
        }catch (DeviceRTException e){
            ret = Device_RT_Exception;
        }
        return ret;
    }

    //sp10通讯
    public BackBean sp10Communication(byte[] req)
    {
        mBackBean.setBuffer(new byte[4]);
        byte[] result;
        try{
            result = ((ExternalRFCard)sdkModules.get(ModuleName.ExternalRFCard)).communicate(req);
            mBackBean.setRet(0);
            mBackBean.setBuffer(result);
        }catch (DeviceRTException e){
            mBackBean.setRet(Device_RT_Exception);
        }
        return mBackBean;
    }

    //sp10下电
    public int sp10PowerOff()
    {
        int ret;
        try{
            ret = ((ExternalRFCard)sdkModules.get(ModuleName.ExternalRFCard)).powerOff()?0:1;
        }catch (DeviceRTException e){
            ret = Device_RT_Exception;
        }
        return ret;
    }

    //软件版本
    public BackBean getSp10Version()
    {
        mBackBean.setBuffer(new byte[4]);
        byte[] rsp;
        try{
            rsp = ((ExternalRFCard)sdkModules.get(ModuleName.ExternalRFCard)).getVersion();
            mBackBean.setRet(0);
            mBackBean.setBuffer(rsp);
        }catch (DeviceRTException e){
            mBackBean.setRet(Device_RT_Exception);
        }
        return mBackBean;
    }

    private BigDecimal amt;
    private EmvTransController controller;
//    private SimpleTransferExtListener simpleTransferExtListener;

    //外接密码键盘消费
//    public int sp10EmvTransaction(String amount, String cashback, boolean forceOnline, boolean isSupportSM)
//    {
//        int ret;
//        try{
//            DecimalFormat df = new DecimalFormat("#.00");
//            if(amount.equals("")||amount == null){
//                amt = null;
//            }else {
//                amt = new BigDecimal(amount);
//            }
//            EmvJNIService emvJNIService = new EmvJNIService();
//            emvJNIService.jniemvUseOutCardReader(1);
//            boolean isSucess = ((ExternalRFCard)sdkModules.get(ModuleName.ExternalRFCard)).powerOn(new byte[] { 0x00, 0x30 });
//            if (isSucess)
//            {
//                ((EmvModule)sdkModules.get(ModuleName.EmvModule)).initEmvModule(mContext);
//
//            }
//        }
//    }

     // PIN模块开始
    public void intiPinModule()
    {
        K21Pininput k21Pininput = (K21Pininput) deviceManager.getDevice().getStandardModule(ModuleType.COMMON_PININPUT);
        sdkModules.put(ModuleName.PinModule, k21Pininput);
    }

    // 装载主密钥
    public BackBean loadMainKey(int kekUsingType,int indexMk,byte[] mainKey,byte[] checkValue)
    {
        mBackBean.setBuffer(new byte[4]);
        byte[] mainBack;
        try{
            if(kekUsingType==KekUsingType.ENCRYPT_TMK.ordinal()||kekUsingType==KekUsingType.ENCRYPT_TMK_SM4.ordinal())
            {
                mainBack = ((K21Pininput)sdkModules.get(ModuleName.PinModule)).loadMainKey(KekUsingType.values()[kekUsingType],indexMk,mainKey,checkValue,-1);
            }
            else if(kekUsingType==KekUsingType.ENCRYPT_TMK_AES.ordinal())
            {
                mainBack = ((K21Pininput)sdkModules.get(ModuleName.PinModule)).loadMainKey(KekUsingType.values()[kekUsingType],indexMk,mainKey,checkValue,-1,null);
            }
            else
            {
                mBackBean.setRet(SDK_ERR_PARA);
                return mBackBean;
            }
            mBackBean.setRet(0);
            mBackBean.setBuffer(mainBack);
        }catch (DeviceRTException e){
            mBackBean.setRet(Device_RT_Exception);
        }
        return mBackBean;
    }

    // 装载工作密钥
    public BackBean loadWorkingKey(int workingType,int indexMk,int indexWork,byte[] workingKey,byte[] checkValue)
    {
        mBackBean.setBuffer(new byte[4]);
        try{
            byte[] kcv = ((K21Pininput)sdkModules.get(ModuleName.PinModule)).loadWorkingKey(WorkingKeyType.values()[workingType],indexMk,indexWork,workingKey,checkValue);
            mBackBean.setRet(0);
            mBackBean.setBuffer(kcv);
        }catch (DeviceRTException e){
            mBackBean.setRet(Device_RT_Exception);
        }
        return mBackBean;
    }

    // 取消密码输入
    public void  cancelInputPin()
    {
        ((K21Pininput)sdkModules.get(ModuleName.PinModule)).cancelPinInput();
    }

    // MAC计算
    public BackBean calMac(int macAlgorithm,int indexMac,byte[] input)
    {
        MacAlgorithm macType = MacAlgorithm.values()[macAlgorithm];
        try{
            byte[] output = ((K21Pininput)sdkModules.get(ModuleName.PinModule)).calcMac(macType, KeyManageType.MKSK,new WorkingKey(indexMac),input).getMac();
            mBackBean.setRet(0);
            mBackBean.setBuffer(output);
        }
        catch (DeviceRTException e)
        {
            mBackBean.setRet(Device_RT_Exception);
            return mBackBean;
        }
        return mBackBean;
    }

    // 加密
    public BackBean encryption(int mode,int algorithm,int indexWk,byte[] cbcIV,byte[] input)
    {
        mBackBean.setBuffer(new byte[4]);
        try{
            byte[] result = ((K21Pininput)sdkModules.get(ModuleName.PinModule)).encrypt(new EncryptAlgorithm(EncryptAlgorithm.KeyMode.values()[mode], EncryptAlgorithm.ManufacturerAlgorithm.values()[algorithm]),new WorkingKey(indexWk),input,cbcIV);
            mBackBean.setRet(0);
            mBackBean.setBuffer(result);
        }catch (DeviceRTException e){
            mBackBean.setRet(Device_RT_Exception);
        }
        return mBackBean;
    }
    // 解密
    public BackBean decryption(int mode,int algorithm,int indexWk,byte[] input,byte[] cbcIV)
    {
        mBackBean.setBuffer(new byte[4]);
        try{
            byte[] decryResult = ((K21Pininput)sdkModules.get(ModuleName.PinModule)).decrypt(new EncryptAlgorithm(EncryptAlgorithm.KeyMode.values()[mode],EncryptAlgorithm.ManufacturerAlgorithm.values()[algorithm]),new WorkingKey(indexWk),input,cbcIV);
            mBackBean.setRet(0);
            mBackBean.setBuffer(decryResult);
        }catch (DeviceRTException e){
            mBackBean.setRet(Device_RT_Exception);
            return mBackBean;
        }
        return mBackBean;
    }

    // 删除主密钥
    public int deleteMK(int kekUsingType,int indexMk)
    {
        boolean isSucc = false;
        try{
            if(kekUsingType==KekUsingType.ENCRYPT_TMK.ordinal())
                isSucc = ((K21Pininput)sdkModules.get(ModuleName.PinModule)).deleteMainKey(indexMk,false);
            else if(kekUsingType == KekUsingType.ENCRYPT_TMK_SM4.ordinal())
                isSucc = ((K21Pininput)sdkModules.get(ModuleName.PinModule)).deleteMainKey(indexMk,true);
        }catch (DeviceRTException e){
           return Device_RT_Exception;
        }
        return isSucc?0:1;
    }

    // 删除工作密钥
    public int deleteWK(int wkType,int indexWk)
    {
        boolean isSucc = false;
        try{
            isSucc = ((K21Pininput)sdkModules.get(ModuleName.PinModule)).deleteWorkingKey(WorkingKeyType.values()[wkType],indexWk);
        }catch (DeviceRTException e){
            return Device_RT_Exception;
        }
        return isSucc?0:1;
    }

    // 删除所有密钥
    public int deleteAllKey()
    {
        boolean isSucc =false;
        try{
            isSucc = ((K21Pininput)sdkModules.get(ModuleName.PinModule)).deleteKeyStore();
        }catch (DeviceRTException e){
            return Device_RT_Exception;
        }
        return isSucc?0:1;
    }

    // 检测密钥是否存在
    public BackBean checkKey(int keyType,int keyIndex)
    {
        mBackBean.setBuffer(new byte[4]);
        try{
            CheckKeyResult result = ((K21Pininput)sdkModules.get(ModuleName.PinModule)).checkKeyIsExist(KeyType.values()[keyType],keyIndex,null);
            mBackBean.setRet(result.isExist?0:1);
            mBackBean.setBuffer(result.getCheckValue()==null?new byte[4]:result.getCheckValue());
        }catch (DeviceRTException e){
            e.printStackTrace();
            mBackBean.setRet(Device_RT_Exception);
        }
        return mBackBean;
    }

    // 获取人行21号数据
    public String getpbc21Info()
    {
        NdkApiManager ndkApiManager = NdkApiManager.getNdkApiManager();
        SecN secNdk = ndkApiManager.getSecN();
        String result;
        int setOwnerResult = secNdk.NDK_SecSetKeyOwner("_NL_TERM_MGR");
        if(setOwnerResult!=0)
        {
            result = "ret:"+setOwnerResult;
            return result;
        }
        ST_SEC_KEY_INFO keyInfo = new ST_SEC_KEY_INFO();
        ST_SEC_KCV_INFO kcvInfo = new ST_SEC_KCV_INFO();
        int SEC_KEY_SM4 = (1 << 6);
        int SEC_KEY_TYPE_TDK = 4;
        keyInfo.ucScrKeyIdx = 0; // Plain text type
        keyInfo.ucScrKeyType = 0; // Terminal loading key
        keyInfo.ucDstKeyIdx = (byte) 255; //  key index
        keyInfo.ucDstKeyType = (byte) (SEC_KEY_TYPE_TDK | SEC_KEY_SM4); // 鏁版嵁鍔犺В瀵嗗瘑閽�
        Arrays.fill(keyInfo.DstKeyValue, (byte) 0x32);
        keyInfo.nDstKeyLen = 16;

        kcvInfo.nCheckMode = 1;
        byte[] checkValue = new byte[]{(byte) 0x2B, (byte) 0x44, 0x59, (byte) 0xDE};
        kcvInfo.nLen = 4;
        kcvInfo.sCheckBuf = Arrays.copyOf(checkValue, 4);

        int ndkRslt = secNdk.NDK_SecLoadKey(keyInfo, kcvInfo);
        if(ndkRslt!=0)
        {
            secNdk.NDK_SecSetKeyOwner("*");
            result = "ret:"+ndkRslt;
            return result;
        }

        try{
            TusnData info = ((K21Pininput)sdkModules.get(ModuleName.PinModule)).getTusnData("123456");
            result = "ret:"+0+",device_type:"+info.getDeviceType()+",hard_sn:"+info.getSn()+",hard_sn_en:"+info.getEncryptedData();
        }catch (DeviceRTException e)
        {
            result = "ret:"+Device_RT_Exception;
            return result;
        }
        return result;
    }

    // 装载DUKPT的KSN
    public int loadDukpt(byte[] ksn,int index,byte[] ipek)
    {
        int ret;
        try{
            KSNLoadResult ksnResult = ((K21Pininput)sdkModules.get(ModuleName.PinModule)).loadIPEK(KSNKeyType.TRANSFERKEY_TYPE,index,ksn,ipek,-1,null);
            ret = ksnResult.getResultCode().ordinal();
        }catch (DeviceRTException e){
            ret = Device_RT_Exception;
        }
        return ret;
    }

    // 获取dukpt ksn
    public BackBean getDukptKsn(int index)
    {
        mBackBean.setBuffer(new byte[4]);
        try {
            byte[] ksn = ((K21Pininput)sdkModules.get(ModuleName.PinModule)).getDukptKsn(index);
            if(ksn!=null)
                mBackBean.setBuffer(ksn);
            mBackBean.setRet(0);
        }catch (DeviceRTException e){
            mBackBean.setRet(Device_RT_Exception);
        }
        catch (Exception e){
            mBackBean.setRet(Common_Exception);
        }
        return mBackBean;
    }

    // ksn自增
    public int ksnIncrease(int index)
    {
        int ret;
        try{
            ret = ((K21Pininput)sdkModules.get(ModuleName.PinModule)).ksnIncrease(index)?0:1;
        }catch (DeviceRTException e){
            ret = Device_RT_Exception;
        }
        return ret;
    }

    // 输入密码，暂时无法自动
    public int inputOnlinePin()
    {
        return -1;
    }

    // 取消密码输入
    public int canleInputPin()
    {
        int ret;
        try{
            ((K21Pininput)sdkModules.get(ModuleName.PinModule)).cancelPinInput();
            ret = 0;
        }catch (DeviceRTException e){
            ret = Device_RT_Exception;
        }
        return ret;
    }

    // MAC计算
    public BackBean calMacDukpt(int macAlgorithm,int index,byte[] input)
    {
        mBackBean.setBuffer(new byte[4]);
        MacAlgorithm macType = MacAlgorithm.values()[macAlgorithm];
        try{
            byte[] output = ((K21Pininput)sdkModules.get(ModuleName.PinModule)).calcMac(macType,KeyManageType.DUKPT,new WorkingKey(index),input).getMac();
            mBackBean.setRet(0);
            if(output!=null)
                mBackBean.setBuffer(output);
        }catch (DeviceRTException e){
            mBackBean.setRet(Device_RT_Exception);
        }
        return mBackBean;
    }

    // 加密
    public BackBean encryptionDukpt(int mode,int algorithm,int index,byte[] input,byte[] cbcIV)
    {
        mBackBean.setBuffer(new byte[4]);
        try{
            byte[] result = ((K21Pininput)sdkModules.get(ModuleName.PinModule)).encrypt(new EncryptAlgorithm(EncryptAlgorithm.KeyMode.values()[mode], EncryptAlgorithm.ManufacturerAlgorithm.values()[algorithm]),new WorkingKey(index),input,cbcIV);
            mBackBean.setRet(0);
            mBackBean.setBuffer(result);
        }catch (DeviceRTException e){
            mBackBean.setRet(Device_RT_Exception);
        }
        return mBackBean;
    }

    // 解密
    public BackBean decryptionDukpt(int mode,int algorithm,int index,byte[] input,byte[] cbcIV) {
        mBackBean.setBuffer(new byte[4]);
        try {
            byte[] decryResult = ((K21Pininput) sdkModules.get(ModuleName.PinModule)).decrypt(new EncryptAlgorithm(EncryptAlgorithm.KeyMode.values()[mode], EncryptAlgorithm.ManufacturerAlgorithm.values()[algorithm]), new WorkingKey(index), input, cbcIV);
            mBackBean.setRet(0);
            mBackBean.setBuffer(decryResult);
        } catch (DeviceRTException e) {
            mBackBean.setRet(Device_RT_Exception);
            return mBackBean;
        }
        return mBackBean;
    }


    // 国密模块开始
    public void initSmModule()
    {
        SmModule smModule = (SmModule) deviceManager.getDevice().getStandardModule(ModuleType.COMMON_SM);
        sdkModules.put(ModuleName.SmModule,smModule);
    }

    // 获取安全配置
    public int getSecfig()
    {
        int secfg;
        try{
            secfg = ((SmModule)sdkModules.get(ModuleName.SmModule)).getSecCfg();
        }catch (DeviceRTException e){
            secfg = Device_RT_Exception;
        }
        return secfg;
    }

    // 设置安全配置
    public int setSecfig(int secCfg)
    {
        int ret;
        try{
            ret = ((SmModule)sdkModules.get(ModuleName.SmModule)).setSecCfg(secCfg)?0:1;
        }catch (DeviceRTException e) {
            ret = Device_RT_Exception;
        }
        return ret;
    }

    // SHA1计算
    public BackBean calSHA1(byte[] data)
    {
        mBackBean.setBuffer(new byte[4]);
        try{
            byte[] back = ((SmModule)sdkModules.get(ModuleName.SmModule)).calcSHA1(data);
            mBackBean.setRet(0);
            mBackBean.setBuffer(back);
        }catch (DeviceRTException e){
            mBackBean.setRet(Device_RT_Exception);
        }
        return mBackBean;
    }

    // SHA256计算
    public BackBean calSHA256(byte[] data)
    {
        mBackBean.setBuffer(new byte[4]);
        try{
            byte[] back = ((SmModule)sdkModules.get(ModuleName.SmModule)).calcSHA256(data);
            mBackBean.setRet(0);
            mBackBean.setBuffer(back);
        }catch (DeviceRTException e){
            mBackBean.setRet(Device_RT_Exception);
        }
        return mBackBean;
    }

    // SHA512计算
    public BackBean calSHA512(byte[] data)
    {
        mBackBean.setBuffer(new byte[4]);
        try{
            byte[] back = ((SmModule)sdkModules.get(ModuleName.SmModule)).calcSHA512(data);
            mBackBean.setRet(0);
            mBackBean.setBuffer(back);
        }catch (DeviceRTException e){
            mBackBean.setRet(Device_RT_Exception);
        }
        return mBackBean;
    }

    private RSAKeyPair rsaKeyPair;
    // 生成RSA密钥对
    public int generateRSAKey(int len,int mode)
    {
        int ret;
        try{
            rsaKeyPair = ((SmModule)sdkModules.get(ModuleName.SmModule)).genRSAKeyPair(len,mode);
//            result = "ret:"+0+",pub_mod:"+ISOUtils.hexString(rsaKeyPair.pubkey.modulus)+",pub_mod_len:"
//                    +rsaKeyPair.pubkey.modulus.length+",pub_exponent:"+ISOUtils.hexString(rsaKeyPair.pubkey.exponent)
//                    +",prik_mod:"+ISOUtils.hexString(rsaKeyPair.prikey.modulus)+",pri_mod_len:"+rsaKeyPair.prikey.modulus.length
//                    +",pri_mod_exponent:"+ISOUtils.hexString(rsaKeyPair.prikey.exponent);
            ret = 0;
        }catch (DeviceRTException e){
            ret = Device_RT_Exception;
        }
        return ret;
    }

    // RSA密钥对加解密
    public BackBean rsaEncryDecry(byte[] encryDataSource)
    {
        mBackBean.setBuffer(new byte[4]);
        try{
            byte[] rsaEncryData = ((SmModule)sdkModules.get(ModuleName.SmModule)).rsaRecover(new String(rsaKeyPair.prikey.modulus,"gbk"),rsaKeyPair.prikey.modulus.length/8,rsaKeyPair.prikey.exponent,encryDataSource);
            byte[] rsaDecryData = ((SmModule)sdkModules.get(ModuleName.SmModule)).rsaRecover(new String(rsaKeyPair.pubkey.modulus,"gbk"),rsaKeyPair.pubkey.modulus.length/8,rsaKeyPair.pubkey.exponent,rsaEncryData);
            mBackBean.setRet(0);
            mBackBean.setBuffer(rsaDecryData);
        }catch (UnsupportedEncodingException e){
            mBackBean.setRet(Unsupported_Encoding_Exception);
        }
        catch (DeviceRTException e){
            mBackBean.setRet(Device_RT_Exception);
        }
        return mBackBean;
    }

    // RSA密钥对校验
    public int rsaVerify()
    {
        int rslt;
        try {
            rslt = ((SmModule)sdkModules.get(ModuleName.SmModule)).rsaKeyPairVerify(rsaKeyPair.pubkey,rsaKeyPair.prikey);
        }catch (DeviceRTException e){
            rslt = Device_RT_Exception;
        }
        return rslt;
    }

    // 生成SM2密钥对
    public String generateSM2Key()
    {
        StringBuffer result = new StringBuffer();
        try{
            Sm2Key sm2KeyPair = ((SmModule)sdkModules.get(ModuleName.SmModule)).genSM2KeyPair();
            byte[] eccPriKey = sm2KeyPair.eccprikey;
            byte[] eccPubKey = sm2KeyPair.eccpubKey;
            result.append("ret:"+0);
            if(eccPubKey!=null)
            {
                result.append(",sm2_pub_len:"+eccPubKey.length+",sm2_pub:"+ISOUtils.hexString(eccPubKey));
            }
            if(eccPriKey!=null)
            {
                result.append(",sm2_pri_len:"+eccPriKey.length+",sm2_pri:"+ISOUtils.hexString(eccPriKey));
            }
        }catch (DeviceRTException e){
            return "ret:"+Device_RT_Exception;
        }
        return result.toString();
    }

    // SM2公钥加密
    public BackBean sm2Encry(byte[] eccPubKey,byte[] data)
    {
        mBackBean.setBuffer(new byte[4]);
        try{
            byte[] sm2EncryData = ((SmModule)sdkModules.get(ModuleName.SmModule)).sm2Encrypt(eccPubKey,data);
            mBackBean.setRet(0);
            mBackBean.setBuffer(sm2EncryData);
        }catch (DeviceRTException e){
            mBackBean.setRet(Device_RT_Exception);
        }
        return mBackBean;
    }

    // sm2私钥解密
    public BackBean sm2Decry(byte[] eccPriKey,byte[] sm2EncryData)
    {
        mBackBean.setBuffer(new byte[4]);
        try{
            byte[] decryData = ((SmModule)sdkModules.get(ModuleName.SmModule)).sm2Decrypt(eccPriKey,sm2EncryData);
            mBackBean.setRet(0);
            mBackBean.setBuffer(decryData);
        }catch (DeviceRTException e){
            mBackBean.setRet(Device_RT_Exception);
        }
        return mBackBean;
    }

    // 生成SM2签名摘要
    public BackBean generateSm2Digest(byte[] idData,byte[] digestDataResourece,byte[] eccPubKey)
    {
        mBackBean.setBuffer(new byte[4]);
        try{
            byte[] digestData = ((SmModule)sdkModules.get(ModuleName.SmModule)).sm2GenDigest(idData,digestDataResourece,eccPubKey);
            mBackBean.setRet(0);
            mBackBean.setBuffer(digestData);
        }catch (DeviceRTException e){
            mBackBean.setRet(Device_RT_Exception);
        }
        return mBackBean;
    }

    // SM2签名
    public BackBean sm2Sign(byte[] eccPriKey,byte[] digestData)
    {
        mBackBean.setBuffer(new byte[4]);
        try{
            byte[] signedData = ((SmModule)sdkModules.get(ModuleName.SmModule)).sm2Sign(eccPriKey,digestData);
            mBackBean.setRet(0);
            mBackBean.setBuffer(signedData);
        }catch (DeviceRTException e){
            mBackBean.setRet(Device_RT_Exception);
        }
        return mBackBean;
    }

    // SM2验证函数
    public int sm2Verify(byte[] eccPubKey,byte[] digestData,byte[] signedData)
    {
        int ret;
        try{
            ret = ((SmModule)sdkModules.get(ModuleName.SmModule)).sm2Verify(eccPubKey,digestData,signedData);
        }catch (DeviceRTException e){
            ret = Device_RT_Exception;
        }
        return ret;
    }

    public void initGuestDisplayModule()
    {
        ExternalGuestDisplay externalGuestDisplay = (ExternalGuestDisplay) deviceManager.getDevice().getExModule(ExModuleType.GUEST_DISPLAY);
        sdkModules.put(ModuleName.GuestDisplay,externalGuestDisplay);
    }

    // 是否可用
    public int isEnabled()
    {
        int ret;
        try{
            ret = ((ExternalGuestDisplay)sdkModules.get(ModuleName.GuestDisplay)).isEnabled()?0:1;
        }catch (NullPointerException e){
            e.printStackTrace();
            ret = Null_Pointer_Exception;
        }
       return ret;
    }

    // 开始输入，需要手动操作，暂未封装
    public int startInput()
    {
        return 0;
    }

    // 停止输入
    public int stopInput()
    {
        int ret;
        try{
            ret = ((ExternalGuestDisplay)sdkModules.get(ModuleName.GuestDisplay)).stopKeyboardInput()?0:1;
        }catch (NullPointerException e){
            ret = Null_Pointer_Exception;
        }
        return ret;
    }

    // 设置亮度
    public int setLedLight(int value)
    {
        int ret;
        try{
            ret = ((ExternalGuestDisplay)sdkModules.get(ModuleName.GuestDisplay)).setDigitalLedBrightness(value)?0:1;
        }catch (NullPointerException e){
            ret = Null_Pointer_Exception;
        }
        return ret;
    }

    // 显示金额
    public int showLedAmount(String value)
    {
        int ret;
        try{
            ret = ((ExternalGuestDisplay)sdkModules.get(ModuleName.GuestDisplay)).showDigitalLed(value)?0:1;
        }catch (NullPointerException e){
            ret = Null_Pointer_Exception;
        }
        return ret;
    }

    // 关闭客显
    public int closeLed()
    {
        int ret;
        try{
            ret = ((ExternalGuestDisplay)sdkModules.get(ModuleName.GuestDisplay)).turnOffDigitalLed()?0:1;
        }catch (NullPointerException e){
            ret = Null_Pointer_Exception;
        }
        return ret;
    }


    //外接扫码模块开始
    public void initExternalScanModule()
    {
        ExternalScan externalScan = (ExternalScan) deviceManager.getDevice().getExModule(ExModuleType.SCAN);
        sdkModules.put(ModuleName.ExternalScan,externalScan);
    }

    //初始化
    public int commInit(boolean isScandev, int baudrateTypes, int databitTypes, int oddevencheckTypes,int stopbitTypes)
    {
        int ret;
        try{
            ret = ((ExternalScan)sdkModules.get(ModuleName.ExternalScan)).setDevComm(
                    isScandev,
                    BaudrateType.values()[baudrateTypes],
                    DataBitType.values()[databitTypes],
                    OddEvenCheckType.values()[oddevencheckTypes],
                    StopBitType.values()[stopbitTypes])?0:1;
        }catch (NullPointerException e){
            ret = Null_Pointer_Exception;
        }
        return ret;
    }

    private ScanResultListener scanResultListener = new ScanResultListener() {
        @Override
        public void onSuccess(byte[] bytes) {
            LoggerUtil.i("扫码成功:"+bytes!=null? ISOUtils.hexString(bytes):"null");
        }

        @Override
        public void onTimeOut() {
            LoggerUtil.i("扫码超时");
        }

        @Override
        public void onError(ScanDevErrorCode scanDevErrorCode, String s) {
            LoggerUtil.i("扫码失败原因：" + s);
        }
    };

    //开始扫描
    public int startScan(String amount, int interval, int timeout)
    {
        int ret;
        String start_str = "开始扫码";
        String success_str = "扫码成功：";
        try
        {
            byte[] startTip = start_str.getBytes();
            byte[] successTip = success_str.getBytes();
            ret = ((ExternalScan)sdkModules.get(ModuleName.ExternalScan)).startScan(
                    startTip,
                    successTip,
                    amount,
                    interval,
                    timeout,
                    scanResultListener)?0:1;
        }catch (NullPointerException e){
            ret = Null_Pointer_Exception;
        }
        return ret;
    }

    //停止扫描
    public int stopScan(boolean isoffpeyment)
    {
        int ret;
        try
        {
            ret = ((ExternalScan)sdkModules.get(ModuleName.ExternalScan)).stopScan(isoffpeyment)?0:1;
        }catch (NullPointerException e){
            ret = Null_Pointer_Exception;
        }
        return ret;
    }

    //清空设置缓存区
    public int clearTags()
    {
        int ret;
        try
        {
            ret = ((ExternalScan)sdkModules.get(ModuleName.ExternalScan)).clearParamBuffer()?0:1;
        }catch (NullPointerException e){
            ret = Null_Pointer_Exception;
        }
        return ret;
    }

    //设置背景灯
    public int setLed(boolean islight)
    {
        int ret;
        try
        {
            ret = ((ExternalScan)sdkModules.get(ModuleName.ExternalScan)).setBacklight(islight)?0:1;
        }catch (NullPointerException e){
            ret = Null_Pointer_Exception;
        }
        return ret;
    }

    //设置音量
    public int setVolume(int value)
    {
        int ret;
        try
        {
            ret = ((ExternalScan)sdkModules.get(ModuleName.ExternalScan)).setVolume(value)?0:1;
        }catch (NullPointerException e){
            ret = Null_Pointer_Exception;
        }
        return ret;
    }

    //设置后缀
    public int setSuffix(byte[] suffix)
    {
        int ret;
        try
        {
            ret = ((ExternalScan)sdkModules.get(ModuleName.ExternalScan)).setSuffix(suffix)?0:1;
        }catch (NullPointerException e){
            ret = Null_Pointer_Exception;
        }
        return ret;
    }

    //添加回车换行
    public int setEnter(boolean isenter)
    {
        int ret;
        try
        {
            ret = ((ExternalScan)sdkModules.get(ModuleName.ExternalScan)).setEnter(isenter)?0:1;
        }catch (NullPointerException e){
            ret = Null_Pointer_Exception;
        }
        return ret;
    }

    //添加金额
    public int setAmount(String amount)
    {
        int ret;
        try
        {
            ret = ((ExternalScan)sdkModules.get(ModuleName.ExternalScan)).setAMount(amount)?0:1;
        }catch (NullPointerException e){
            ret = Null_Pointer_Exception;
        }
        return ret;
    }

    //设置前缀
    public int setPrefix(byte[] prefix)
    {
        int ret;
        try
        {
            ret = ((ExternalScan)sdkModules.get(ModuleName.ExternalScan)).setPrefix(prefix)?0:1;
        }catch (NullPointerException e){
            ret = Null_Pointer_Exception;
        }
        return ret;
    }

    //播放声音
    public int playVoice(byte[] value)
    {
        int ret;
        try
        {
            ret = ((ExternalScan)sdkModules.get(ModuleName.ExternalScan)).playVoice(value)?0:1;
        }catch (NullPointerException e){
            ret = Null_Pointer_Exception;
        }
        return ret;
    }

    //成功语音提示
    public int setSuccessTip(byte[] successtip)
    {
        int ret;
        try
        {
            ret = ((ExternalScan)sdkModules.get(ModuleName.ExternalScan)).setSuccessTip(successtip)?0:1;
        }catch (NullPointerException e){
            ret = Null_Pointer_Exception;
        }
        return ret;
    }

    //设置彩灯
    public int colorLight(int colorlightTypes, boolean iscolorlight)
    {
        int ret;
        try
        {
            ret = ((ExternalScan)sdkModules.get(ModuleName.ExternalScan)).setColorLight(
                    ScanDevColorLight.values()[colorlightTypes],
                    iscolorlight)?0:1;
        }catch (NullPointerException e){
            ret = Null_Pointer_Exception;
        }
        return ret;
    }

    //发送设置参数
    public int sendTags()
    {
        int ret;
        try
        {
            ret = ((ExternalScan)sdkModules.get(ModuleName.ExternalScan)).sendParam()?0:1;
        }catch (NullPointerException e){
            ret = Null_Pointer_Exception;
        }
        return ret;
    }

    //设置系统参数
    public int setDevParam(int devparam, byte[] value)
    {
        int ret;
        try
        {
            ret = ((ExternalScan)sdkModules.get(ModuleName.ExternalScan)).setDevParam(
                    ScanDevParam.values()[devparam],
                    value)?0:1;
        }catch (NullPointerException e){
            ret = Null_Pointer_Exception;
        }
        return ret;
    }

    //获取系统参数
    public String getDevParam(byte[] devparam)
    {
        Map<String,String> map = new HashMap<String,String>();
        ScanDevParam[] devParam = new ScanDevParam[devparam.length];
        for(int i=0;i<devparam.length;i++)
        {
            devParam[i] = ScanDevParam.values()[devparam[i]];
        }
        String result;
        try{
            map = ((ExternalScan)sdkModules.get(ModuleName.ExternalScan)).getDevParam(devParam);
            if(map == null)
            {
                result = "ret:1";
                return result;
            }
            String sn = map.get("SN");
            String pn = map.get("PN");
            String csn = map.get("CSN");
            String pid = map.get("PID");
            String vid = map.get("VID");
            String app = map.get("APP");
            String master = map.get("MASTER");
            String boot = map.get("BOOT");
            result = "ret:"+0
                    +",SN:"+sn
                    +",PN:"+pn
                    +",CSN:" +csn
                    +",PID:"+pid
                    +",VID:"+vid
                    +",APP:"+app
                    +",MASTER:"+master
                    +",BOOT:"+boot;
        }catch (DeviceRTException e)
        {
            result = "ret:"+Device_RT_Exception;
            return result;
        }
        return result;
    }

    //外接扫码模块结束


    //外接密码键盘模块开始
    public void initExternalPinModule()
    {
        ExternalPinInput externalPin = (ExternalPinInput) deviceManager.getDevice().getStandardModule(ModuleType.EXTERNAL_PININPUT);
        sdkModules.put(ModuleName.ExternalPin,externalPin);
        ExternalSignature signatureModule = (ExternalSignature) deviceManager.getDevice().getExModule(ExModuleType.SIGNATURE);
        sdkModules.put(ModuleName.ExternalSignature,signatureModule);
    }

    private boolean isOverseas = false;
    private int externaltype = 1;
    private int porttype = 0;
    private int baudrate = 9;

    //Pinpad口初始化
    public int initExternalPinpad(int externalTypes, int portTypes, int baudrateTypes)
    {
        int ret;
        try
        {
            externaltype = externalTypes;
            porttype = portTypes;
            baudrate = baudrateTypes;
            if(ExternalPinpadType.values()[externalTypes] == ExternalPinpadType.PP60)
            {
                isOverseas =false;
                ((ExternalPinInput)sdkModules.get(ModuleName.ExternalPin)).setPinpadType(
                        ExternalPinpadType.values()[externalTypes],
                        portTypes
                );
                ((ExternalPinInput)sdkModules.get(ModuleName.ExternalPin)).pinPadPortInit(
                        com.newland.mtype.module.common.externalPin.BaudrateType.values()[baudrateTypes],
                        com.newland.mtype.module.common.externalPin.DataBitType.DATA_BIT_6,
                        com.newland.mtype.module.common.externalPin.OddEvenCheckType.NO_CHECK,
                        com.newland.mtype.module.common.pin.StopBitType.STOP_BIT_ONE
                );
                ret = 0;
            }
            else if(ExternalPinpadType.values()[externalTypes] == ExternalPinpadType.SP10)
            {
                isOverseas =false;
                ((ExternalPinInput)sdkModules.get(ModuleName.ExternalPin)).setPinpadType(
                        ExternalPinpadType.values()[externalTypes],
                        portTypes
                );
                ((ExternalPinInput)sdkModules.get(ModuleName.ExternalPin)).pinPadPortInit(
                        com.newland.mtype.module.common.externalPin.BaudrateType.values()[baudrateTypes],
                        com.newland.mtype.module.common.externalPin.DataBitType.DATA_BIT_8,
                        com.newland.mtype.module.common.externalPin.OddEvenCheckType.NO_CHECK,
                        com.newland.mtype.module.common.pin.StopBitType.STOP_BIT_ONE
                );
                ret = 0;
            }
            else if(ExternalPinpadType.values()[externalTypes] == ExternalPinpadType.SP100_OVERSEAS)
            {
                isOverseas =true;
                ((ExternalPinInput)sdkModules.get(ModuleName.ExternalPin)).setPinpadType(
                        ExternalPinpadType.values()[externalTypes],
                        portTypes
                );
                ((ExternalPinInput)sdkModules.get(ModuleName.ExternalPin)).pinPadPortInit(
                        com.newland.mtype.module.common.externalPin.BaudrateType.values()[baudrateTypes],
                        com.newland.mtype.module.common.externalPin.DataBitType.DATA_BIT_8,
                        com.newland.mtype.module.common.externalPin.OddEvenCheckType.NO_CHECK,
                        com.newland.mtype.module.common.pin.StopBitType.STOP_BIT_ONE
                );
                ret = 0;
            }
            else ret=1;
        }catch (DeviceRTException e)
        {
            ret = Device_RT_Exception ;
        }
        return ret;
    }

    //装载主密钥
    public BackBean externalPinpadLoadMk(int main_index, byte[] data)
    {
        mBackBean.setBuffer(new byte[4]);
        byte[] mainback;
        try {
            mainback = ((ExternalPinInput)sdkModules.get(ModuleName.ExternalPin)).loadMainKey(main_index, data);
            mBackBean.setRet(0);
            if(mainback != null)
            {
                mBackBean.setBuffer(mainback);
            }
        }catch (DeviceRTException e)
        {
            mBackBean.setRet(Device_RT_Exception);
        }
        return mBackBean;
    }

    //装载工作密钥
    public BackBean externalPinpadLoadWK(int wk_types, int mkindex, int wkindex, byte[] data)
    {
        mBackBean.setBuffer(new byte[4]);
        byte[] workback;
        try {
            workback = ((ExternalPinInput)sdkModules.get(ModuleName.ExternalPin)).loadWorkingKey(
                    WorkingKeyType.values()[wk_types],
                    mkindex,
                    wkindex,
                    data
            );
            mBackBean.setRet(0);
            if(workback != null)
            {
                mBackBean.setBuffer(workback);
            }
        }catch (DeviceRTException e)
        {
            mBackBean.setRet(Device_RT_Exception);
        }
        return mBackBean;

    }

    //MAC计算
    public BackBean externalPinpadCalMac(int mac_algorithm, int wkindex, byte[] data, int mkindex)
    {
        mBackBean.setBuffer(new byte[4]);
        try {
            byte[] calacMac = ((ExternalPinInput)sdkModules.get(ModuleName.ExternalPin)).calcMac(
                    MacAlgorithm.values()[mac_algorithm],
                    new WorkingKey(wkindex),
                    data,
                    mkindex
            );
            mBackBean.setRet(0);
//            mBackBean.setBuffer(calacMac);
            if(calacMac != null)
            {
                mBackBean.setBuffer(calacMac);
            }
        }catch (DeviceRTException e){

            mBackBean.setRet(Device_RT_Exception);
        }
        return mBackBean;
    }

    //加密
    public BackBean externalPinpadEncry(int key_mode, int algorithm, int index_track, byte[] encry,int index_mk)
    {
        mBackBean.setBuffer(new byte[4]);
        try {
            byte[] encryptData = ((ExternalPinInput)sdkModules.get(ModuleName.ExternalPin)).encrypt(
                    new EncryptAlgorithm(EncryptAlgorithm.KeyMode.values()[key_mode],EncryptAlgorithm.ManufacturerAlgorithm.values()[algorithm]),
                    new WorkingKey(index_track),
                    encry,
                    null,
                    index_mk
            );
            mBackBean.setRet(0);
            mBackBean.setBuffer(encryptData);
        }catch (DeviceRTException e){
            mBackBean.setRet(Device_RT_Exception);
        }
        return mBackBean;
    }

    //解密
    public BackBean externalPinpadDecry(int indexTrack, int encryptTypes, byte[] data, int indexMk)
    {
        mBackBean.setBuffer(new byte[4]);
        try {
            byte[] decryptData = ((ExternalPinInput)sdkModules.get(ModuleName.ExternalPin)).decrypt(
                    new WorkingKey(indexTrack),
                    EncryptType.values()[encryptTypes],
                    data,
                    null,
                    indexMk
            );
            mBackBean.setRet(0);
//            mBackBean.setBuffer(decryptData);
            if(decryptData != null)
            {
                mBackBean.setBuffer(decryptData);
            }
        }catch (DeviceRTException e){
            mBackBean.setRet(Device_RT_Exception);
        }
        return mBackBean;
    }

    //取消密码输入
    public void cancelPinInput()
    {
        ((ExternalPinInput)sdkModules.get(ModuleName.ExternalPin)).cancelPinInput();
    }

    //签名
    public BackBean externalPinpadSign(String code, String filepath)
    {
        mBackBean.setBuffer(new byte[4]);
        try{
            RunningModel.isDebugEnabled = true;
            ((ExternalSignature)sdkModules.get(ModuleName.ExternalSignature)).setPinpadType(
                    ExternalPinpadType.values()[externaltype],
                    porttype
            );
            ((ExternalPinInput)sdkModules.get(ModuleName.ExternalPin)).pinPadPortInit(
                    com.newland.mtype.module.common.externalPin.BaudrateType.values()[baudrate],
                    com.newland.mtype.module.common.externalPin.DataBitType.DATA_BIT_8,
                    com.newland.mtype.module.common.externalPin.OddEvenCheckType.NO_CHECK,
                    com.newland.mtype.module.common.pin.StopBitType.STOP_BIT_ONE);
            boolean result = ((ExternalSignature)sdkModules.get(ModuleName.ExternalSignature)).handShake();
            result = ((ExternalSignature)sdkModules.get(ModuleName.ExternalSignature)).setBordTimeout(55);
            result = ((ExternalSignature)sdkModules.get(ModuleName.ExternalSignature)).setBordReSignTimes(10);
            result = ((ExternalSignature)sdkModules.get(ModuleName.ExternalSignature)).setBordLedBl(true);
            result = ((ExternalSignature)sdkModules.get(ModuleName.ExternalSignature)).setBordMode(false);
            byte[] data = ((ExternalSignature)sdkModules.get(ModuleName.ExternalSignature)).signInput(code);
            if (data != null){
                File file = new File(filepath);
                file.delete();
                file.createNewFile();
                FileOutputStream fos = new FileOutputStream(file);
                Bitmap bmp = BitmapFactory.decodeByteArray(data, 3, data.length-3);
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);

            }
            mBackBean.setRet(0);
            mBackBean.setBuffer(data);
        }catch (DeviceRTException e){
            mBackBean.setRet(Device_RT_Exception);
        }catch (IOException e){
            mBackBean.setRet(IO_Exception);
        }
        return mBackBean;
    }

    //液晶屏显示
    public int lcdDisplay(byte[] list_bytes1, byte[] list_bytes2)
    {
        int ret;
        try{
            List<byte[]> data= new ArrayList<>();
            if(!isOverseas){
                data.add(list_bytes1);
                data.add(list_bytes2);
            }else{
                data.add(null);
                data.add(list_bytes1);
                data.add(null);
                data.add(list_bytes2);
            }
            ret = ((ExternalPinInput)sdkModules.get(ModuleName.ExternalPin)).showMessage(data)?0:1;
        }catch (DeviceRTException e){
            ret = Device_RT_Exception;
        }
        return ret;
    }

    //外接键盘清盘
    public int clearLCD()
    {
        int ret;
        try{
            ret = ((ExternalPinInput)sdkModules.get(ModuleName.ExternalPin)).clearScreen()?0:1;
        }catch (DeviceRTException e)
        {
            ret = Device_RT_Exception;
        }
        return ret;
    }

    //外接键盘获取sn号
    public String getSn()
    {
        String result;
        try{
            String rslt = ((ExternalPinInput)sdkModules.get(ModuleName.ExternalPin)).getSn();
            result = "ret:"+0
                    +",SN:"+rslt;

        }catch (DeviceRTException e){
            result = "ret:"+Device_RT_Exception;
            return result;
        }
        return result;
    }

    //外接键盘模块结束

}

