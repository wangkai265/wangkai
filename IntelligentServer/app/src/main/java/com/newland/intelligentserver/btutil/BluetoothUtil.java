package com.newland.intelligentserver.btutil;

import com.newland.intelligentserver.cons.ConstJdk;
import com.newland.intelligentserver.util.BackBean;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
/**
 * 蓝牙工具类
 */
public class BluetoothUtil implements ConstJdk
{

    public static final int REQUEST_ENABLE_BT = 0;

    private Context mContext;

    private BluetoothAdapter mBtAdapter;

    private BluetoothService mBtService;

//	private Set<BluetoothDevice> foundedDevices = new HashSet<BluetoothDevice>();

//	private ArrayList<BluetoothDevice> mUnpairDevices;

//	private String mConnStatus;

//	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver()
//	{
//		@Override
//		public void onReceive(Context context, Intent intent)
//		{
//			String action = intent.getAction();
//			LoggerUtil.e(action);
//			if(action.equals(BluetoothAdapter.ACTION_DISCOVERY_STARTED))
//			{
//			}
//			else if(action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED))/**蓝牙扫描广播完毕*/
//			{
////				// 扫描完毕也刷新一次界面
////				DefaultFragment.unpairAdapter.notifyDataSetChanged();
//			}
//			else if(action.equals(BluetoothDevice.ACTION_FOUND))/**搜索到蓝牙设备会接收到此广播*/
//			{
//				// 判断搜索到的蓝牙设备是否是已配对的
//				boolean flag = false;
//				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//				// 不添加重名的蓝牙
//				for(BluetoothDevice blueDevice:foundedDevices)
//				{
//					if(device.getAddress().equals(blueDevice.getAddress()))
//					{
//						flag = true;
//					}
//				}
//				if(!flag)
//				{
//					foundedDevices.add(device);
//					//蓝牙打印的内置打印机改成取消保存不了，并且在前面已经做过解绑操作，这里添加就不重复 2019/1/21 zhangixnj
////					if(device.getBondState() == BluetoothDevice.BOND_NONE)
////					{
//							// 添加未配对的蓝牙，蓝牙Dongle的时候只搜索底座蓝牙
//						mUnpairDevices.add(device);
////					}
//				}
//			}
//			else if(action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED))
//			{
//				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//                switch (device.getBondState())
//                {
//                case BluetoothDevice.BOND_BONDING:
//                    break;
//                case BluetoothDevice.BOND_BONDED:
//                    break;
//                case BluetoothDevice.BOND_NONE:
//                	break;
//                default:
//                    break;
//                }
//			}
//			else if(action.equals(BluetoothDevice.ACTION_ACL_CONNECTED))
//			{
//				// 蓝牙链路连接上
//				mConnStatus = BluetoothDevice.ACTION_ACL_CONNECTED;
//			}
//			else if(action.equals(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED))
//			{
//				// 正在断开蓝牙链路
//				mConnStatus = BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED;
//			}
//			else if(action.equals(BluetoothDevice.ACTION_ACL_DISCONNECTED))
//			{
//				// 蓝牙链路已经断开
//				mConnStatus = BluetoothDevice.ACTION_ACL_DISCONNECTED;
//			}
//		}
//	};


//	public boolean pair(String strAddr, String strPsw)
//	{
//		boolean result = false;
//
//		if (!BluetoothAdapter.checkBluetoothAddress(strAddr))
//		{ // 检查蓝牙地址是否有效
//			LoggerUtil.d("devAdd un effient!");
//		}
//		BluetoothDevice device = mBtAdapter.getRemoteDevice(strAddr);
//
//		if (device.getBondState() != BluetoothDevice.BOND_BONDED)
//		{
//			try
//			{
//				ClsUtils.setPin(device.getClass(), device, strPsw); // 手机和蓝牙采集器配对
//				ClsUtils.createBond(device.getClass(), device);
//				result = true;
//			}
//			catch (Exception e)
//			{
//				e.printStackTrace();
//			}
//		}
//		else
//		{
//			try
//			{
//				ClsUtils.createBond(device.getClass(), device);
//				ClsUtils.setPin(device.getClass(), device, strPsw); // 手机和蓝牙采集器配对
//				ClsUtils.createBond(device.getClass(), device);
//				result = true;
//			}
//			catch (Exception e)
//			{
//				LoggerUtil.d("setPiN failed!");
//				e.printStackTrace();
//			}
//		}
//		return result;
//	}

    // 构造方法
    public BluetoothUtil(Context context)
    {
        this.mContext = context;
        this.mBtAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public void addService(String address)
    {
        // 得到蓝牙适配器
//		this.mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        this.mBtService = new BluetoothService(address.toUpperCase());
    }

    public boolean isEnabled()
    {
        return mBtAdapter.isEnabled();
    }

//	/**
//	 * 注册广播
//	 */
//	public void regist()
//	{
//
//		IntentFilter intentFilter = new IntentFilter();
//		intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
//		intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
//		intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
//		intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
//		// 连接方面的广播
//		intentFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
//		intentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
////		intentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
//		if(mContext != null){
//			mContext.registerReceiver(broadcastReceiver, intentFilter);
//		}
//	}

//	/**
//	 * 取消广播注册
//	 */
//	public void unRegist()
//	{
//		if(broadcastReceiver != null)
//		{
//			if(mContext != null)
//			{
//				mContext.unregisterReceiver(broadcastReceiver);
//			}
//		}
//	}

    /**
     * 开关蓝牙
     */
    public boolean openOrCloseBt(boolean enabled)
    {
        boolean isSucc = false;
        if(mBtAdapter != null&&enabled==false)
            isSucc = mBtAdapter.disable();
        if(mBtAdapter!=null&&enabled==true)
            isSucc = mBtAdapter.enable();
        return isSucc;
    }


//	/**
//	 * 获取已配对蓝牙设备
//	 * @return
//	 */
//	public Set<BluetoothDevice> queryPairedDevices()
//	{
//		Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();
//		return pairedDevices;
//	}

//	/**
//	 * 获取搜索到的所有蓝牙设备
//	 * <p>
//	 * 改方法为阻塞方法，需要耗时12s左右
//	 * @return	蓝牙设备集合
//	 */
//	public Set<BluetoothDevice> queryFoundedDevice(ArrayList<BluetoothDevice> unPairList)
//	{
//		this.foundedDevices.clear();
//		mBtAdapter.startDiscovery();
//		this.mUnpairDevices = unPairList;
//		return this.foundedDevices;
//	}

//	/**
//	 * 获取搜索到的所有未配对蓝牙设备
//	 * @return
//	 */
//	public Set<BluetoothDevice> queryUnpairedDevices() {
//		mBtAdapter.startDiscovery();
//		return null;
//	}

    /**
     * 获取蓝牙状态
     * @return
     */
    public int getBluetoothState() {
        return mBtAdapter.getState();
    }

    /**
     * 数据通道建立，得到的socket就是传进来的socket
     * @param chanel
     */
    public boolean connComm(int chanel)
    {
        return mBtService.ConnectChanel(chanel);
    }

    /**
     * 读数据线程
     * @param read_len
     */
    public BackBean readComm(int read_len)
    {
        BackBean backBean = new BackBean();
        backBean.setBuffer(new byte[4]);
        byte[] rbuf = new byte[read_len];
        boolean isSucc = mBtService.readComm(rbuf);
        backBean.setRet(isSucc==true?1:0);
        if(isSucc)
            backBean.setBuffer(rbuf);
        return backBean;
    }

    /**
     * 写数据线程
     * @param wbuf
     */
    public boolean writeComm(byte[] wbuf)
    {
        return mBtService.writeComm(wbuf);
    }

    /**
     * 断开蓝牙连接
     */
    public void cancel()
    {
        mBtService.cancel();
    }

//	public Set<BluetoothDevice> getFoundDevices()
//	{
//		return foundedDevices;
//	}

//	public int getBondState(String address)
//	{
//		BluetoothDevice bluetoothDevice = mBtAdapter.getRemoteDevice(address);
//		return bluetoothDevice.getBondState();
//	}

//	public String getConnStatus()
//	{
//		return mConnStatus;
//	}
}
