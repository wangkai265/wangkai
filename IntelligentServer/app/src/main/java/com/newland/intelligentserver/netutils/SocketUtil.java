package com.newland.intelligentserver.netutils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.Timer;
import java.util.TimerTask;
import com.newland.intelligentserver.cons.ConstJdk;
import com.newland.intelligentserver.util.BackBean;
import com.newland.intelligentserver.util.LoggerUtil;

/**
 * socket工具类
 * @author zhengxq
 * 2016-4-6 下午4:53:23
 */
public class SocketUtil implements ConstJdk
{
    Socket socket;// TCP通讯操作
    DatagramSocket datagramSocket;
    DataInputStream input;
    DataOutputStream out;
    String mServerIP;
    int mPort;

    public SocketUtil(String serverIP,int port)
    {
        this.mServerIP = serverIP;
        this.mPort = port;
    }

    // 建立socket的网络层
    public int setSocket(int sock_t)
    {
        int ret = JDK_OK;

        switch (sock_t) {
            case 0:
                try
                {
//				socket = new Socket(mServerIP, mPort);
                    // 换另外一种方式
                    socket = new Socket();
				/* 据赵明权建议wifi如果传入IP地址为String要用第一种接口方式
				 * public Socket(java.net.InetAddress dstAddress,int dstPort)
				 * 这一种适用于IP地址的
				 * public Socket(java.lang.String dstName,int dstPort)
				 * 这一种是用于url的
				*/
                    String[] ipString = mServerIP.split("\\.");
                    byte[] ipByte = new byte[ipString.length];
                    for (int i = 0; i < ipString.length; i++)
                    {
                        int value = Integer.valueOf(ipString[i]);
                        ipByte[i] = (byte) value;
                    }
                    InetAddress inetAddress = InetAddress.getByAddress("", ipByte);
                    InetSocketAddress address = new InetSocketAddress(inetAddress, mPort);
                    socket.connect(address,8000*2);
                    socket.setKeepAlive(true);
//				// 设置Socket的超时时间
//				try {
//					setSoTimeout(sock_t, 40*1000);
//				} catch (Exception e) {
//					e.printStackTrace();
////					gui.cls_show_msg1_record(TAG, "setSocket", 2, "line %d:%s", Tools.getLineInfo(),e.getMessage());
//				}
                } catch (IOException e1)
                {
                    ret = JDK_ERR;
                    e1.printStackTrace();
                }
                break;

            case 1:
                try
                {
                    datagramSocket = new DatagramSocket();
                } catch (SocketException e)
                {
                    ret = JDK_ERR;
                    e.printStackTrace();
                }
                break;

            default:
                break;
        }
        return ret;
    }


    /**
     * 设置超时时间，该方法必须在bind方法之后使用.
     *
     * @param timeout
     *            超时时间
     * @throws Exception
     */
    public final void setSoTimeout(int sock_t,final int timeout) throws Exception
    {
        switch (sock_t)
        {
            case 0:
                socket.setSoTimeout(timeout);
                break;

            case 1:
                datagramSocket.setSoTimeout(timeout);
                break;

            default:
                break;
        }
    }

    /**
     * 获得超时时间.
     *
     * @return 返回超时时间
     * @throws Exception
     */
    public int getSoTimeout() throws Exception {
        return datagramSocket.getSoTimeout();
    }

    public DatagramSocket getSocket() {
        return datagramSocket;
    }

    /**
     * 向指定服务器发送数据
     * @param sock_t TCP/UDP
     * @param bytes	 发送的数据
     * @param len	发送的数据的长度
     * @param timeout	发送的超时时间
     * @throws IOException
     */
    public int send(final int sock_t,final byte[] bytes,int len,int timeout) throws IOException
    {
        // 设置超时时间
        final Timer timer = new Timer();
        if(timeout>0){
            timer.schedule(new TimerTask() {
                public void run() {
                    LoggerUtil.e("-------设定要指定任务--------");
                    close(sock_t);
                }
            },timeout);
        }
        switch (sock_t)
        {
            case 0:
                out = new DataOutputStream(socket.getOutputStream());
                out.write(bytes, 0, len);
                out.flush();
                break;

            case 1:
                DatagramPacket dp = new DatagramPacket(bytes, len,InetAddress.getByName(mServerIP), mPort);
                datagramSocket.send(dp);
                break;

            default:
                break;
        }
        timer.cancel();
        return len;

    }
    /**
     * 接收从指定的服务端发回的数据
     * @return 返回从指定的服务端发回的数据.
     * @throws IOException
     */
    public BackBean receive(final int sock_t,int len,int timeout) throws IOException
    {
        BackBean backBean = new BackBean();
        backBean.setBuffer(new byte[4]);
        byte[] rBuf = new byte[len];
        final Timer timer = new Timer();
        if(timeout>0){

            timer.schedule(new TimerTask() {
                public void run() {
                    System.out.println("-------设定要指定任务--------");
                    close(sock_t);
                }
            },timeout);
        }
        switch (sock_t)
        {
            case 0:
                int readLen = 0;
                int tmplen = 0;
                input = new DataInputStream(socket.getInputStream());
                // 循环读操作
                do
                {
                    // 角标有点问题
                    tmplen=input.read(rBuf, readLen, len-readLen);
                    if(tmplen==-1)
                        break;
                    readLen+=tmplen;

                }
                while(readLen<len);
                len = readLen;
                backBean.setRet(len);
                backBean.setBuffer(rBuf);
                break;

            case 1:
                DatagramPacket dp = new DatagramPacket(rBuf, rBuf.length);
                datagramSocket.receive(dp);
                System.arraycopy(dp.getData(), 0, rBuf, 0, dp.getLength());
                break;

            default:
                break;
        }
        timer.cancel();
        return backBean;
    }

    /**
     * 接收从指定的服务端发回的数据.
     * @return 返回从指定的服务端发回的数据.
     * @throws Exception
     */
    public int receiveScan(int sock_t,byte[] rbuf)throws Exception
    {
        int readLen = 0;
        switch (sock_t)
        {
            case 0:
                readLen = 0;
                input = new DataInputStream(socket.getInputStream());
                // 循环读操作
                // 角标有点问题
                readLen = readLen+input.read(rbuf,0,200);
                LoggerUtil.e("readLen:"+readLen);
//			input.close();
                break;

            case 1:
                DatagramPacket dp = new DatagramPacket(rbuf, rbuf.length);
                datagramSocket.receive(dp);
                System.arraycopy(dp.getData(), 0, rbuf, 0, dp.getLength());
                break;

            default:
                break;
        }
        return readLen;
    }

    /**
     * 关闭udp连接.
     */
    public final int close(int sock_t)
    {
        switch (sock_t)
        {
            case 0:
                try {
                    if(out!=null)
                    {
                        out.close();
                    }
                    if(input!=null)
                    {
                        input.close();
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

                if (socket != null)
                {
                    try
                    {
                        socket.close();
                    } catch (IOException e)
                    {
                        socket = null;
                        return JDK_ERR;
                    }
                }
                return JDK_OK;

            case 1:
                try
                {
                    datagramSocket.close();
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                    return JDK_ERR;
                }
                return JDK_OK;

            default:
                break;
        }
        return JDK_OK;
    }
}
