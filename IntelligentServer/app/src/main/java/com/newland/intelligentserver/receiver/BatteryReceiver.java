package com.newland.intelligentserver.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
/**
 * 电池相关广播
 * @author zhengxq
 * 2017年6月21日 下午2:13:44
 */
public class BatteryReceiver extends BroadcastReceiver
{
    String batMsg;
    String  chargeType;	// 充电类型
    int batLevel;		// 电池电量
    int batVol;			// 电池电压
    String batHealth;	// 电池健康情况
    String batStatus;	// 电池状态
    int batTemp;		// 电池温度
    String batTech;		// 电池技术

    boolean isCharge = false;
    boolean isPresent = true;



    public int getBatVol() {
        return batVol;
    }

    public void setBatVol(int batVol) {
        this.batVol = batVol;
    }

    public String getBatMsg()
    {
        return batMsg;
    }

    public void setCharge()
    {
        isCharge = false;
    }

    /**
     * 是否充电
     * @return
     */
    public boolean getCharge()
    {
        return isCharge;
    }

    /**
     * 获取目前充电类型
     * @param plugType
     * @return
     */
    private void SetPlugType(int plugType)
    {
        switch (plugType) {
            case BatteryManager.BATTERY_PLUGGED_AC:
                chargeType = BatteryManager.BATTERY_PLUGGED_AC + "(AC)";
                return;

            case BatteryManager.BATTERY_PLUGGED_USB:
                chargeType = BatteryManager.BATTERY_PLUGGED_USB + "(USB)";
                return;
            case BatteryManager.BATTERY_PLUGGED_WIRELESS:// 无线方式
                chargeType = BatteryManager.BATTERY_PLUGGED_WIRELESS + "(WIRELESS)";
                return;

            default:
                chargeType = "未充电";
                return;
        }
    }

    public String getPlugType()
    {
        return chargeType;
    }


    public void SetBatHealth(int health)
    {
        switch (health) {
            case BatteryManager.BATTERY_HEALTH_COLD:// 电池过冷
                batHealth = "COLD";
                break;

            case BatteryManager.BATTERY_HEALTH_DEAD:
                batHealth = "DEAD";
                break;

            case BatteryManager.BATTERY_HEALTH_GOOD:// 健康状态良好
                batHealth = "GOOD";
                break;

            case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:// 电压过高
                batHealth = "OVER_VOLTAGE";
                break;

            case BatteryManager.BATTERY_HEALTH_OVERHEAT:// 过热
                batHealth = "OVERHEAT";
                break;

            case BatteryManager.BATTERY_HEALTH_UNKNOWN:
                batHealth = "UNKNOWN";
                break;

            case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE:
                batHealth = "UNSPECIFIED_FAILURE";
                break;

            default:
                break;
        }
    }

    /**
     * 电池健康状况
     * @return
     */
    public String getBatHealth()
    {
        return batHealth;
    }

    /**
     * 设置电池状态
     * @param status
     */
    public void SetBatStatus(int status)
    {
        switch (status)
        {
            case BatteryManager.BATTERY_STATUS_CHARGING:
                batStatus= "电池正在充电(CHARGING)";
                break;

            case BatteryManager.BATTERY_STATUS_DISCHARGING:
                batStatus = "电池正在放电(DISCHARGING)";
                break;

            case BatteryManager.BATTERY_STATUS_FULL:
                batStatus = "电池已充满(FULL)";
                break;

            case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                batStatus = "电池未充电(NOT_CHARGING)";
                break;

            case BatteryManager.BATTERY_STATUS_UNKNOWN:
                batStatus = "电池状态未知(UNKNOWN)";
                break;

            default:
                break;
        }
    }

    /**
     * 获取电池状态
     * @return
     */
    public String getBatStatus()
    {
        return batStatus;
    }


    @Override
    public void onReceive(Context context, Intent intent)
    {
        final String action = intent.getAction();
        if(action.equals(Intent.ACTION_BATTERY_CHANGED))
        {
            batLevel			= intent.getIntExtra(BatteryManager.EXTRA_LEVEL,0);//获得当前电量
            int total		= intent.getIntExtra(BatteryManager.EXTRA_SCALE,1);//获得总电量
            int plugType		= intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0); // 充电类型
            batVol 			= intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0);// 电池电压
            int health		= intent.getIntExtra(BatteryManager.EXTRA_HEALTH,0);// 电池的健康状况
//         int iconSmall 	= intent.getIntExtra(BatteryManager.EXTRA_ICON_SMALL,0);// 电池图标的id值
            isPresent 		= intent.getBooleanExtra(BatteryManager.EXTRA_PRESENT,false);// 电池是否存在的额外值
            int status		= intent.getIntExtra(BatteryManager.EXTRA_STATUS,0);// 电池状态
            batTemp 			= intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0);// 电池温度
            batTech			= intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY);// 电池技术

            SetPlugType(plugType);
            SetBatHealth(health);
            SetBatStatus(status);
            batMsg  = "电池信息\n目前电池的电量："+(int)(batLevel*1.0/total*total)+"\n目前电池的电压："+batVol+"mv"+"\n充电方式："+getPlugType()
                    +"\n电池健康："+getBatHealth()+"\n电池在位："+isPresent+"\n电池状态："+getBatStatus()+"\n电池温度："+batTemp+"\n电池技术："
                    +batTech;
        }
        if(action.equals(Intent.ACTION_POWER_CONNECTED)) // POS定制需求
        {
            // 连接圆孔充电器会接收到该广播
            isCharge = true;
        }
    }
}

