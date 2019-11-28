package com.newland.intelligentserver.util;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.content.res.AssetManager;
import android.newland.telephony.ApnEntity;
import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Administrator on 2019/6/20.
 */

public class Tools {
    private Tools() {
        throw new AssertionError();
    }
    //byte 数组�? int 的相互转�?
    public static int byteArrayToInt(byte[] b) {
        return   b[0] & 0xFF |
                (b[1] & 0xFF) << 8 |
                (b[2] & 0xFF) << 16 |
                (b[3] & 0xFF) << 24;
    }

    /**
     * 将正整型转成具体的字节数组表
     *
     * @param value 待转换的字节数组
     * @param len 被转换的长度,若不足则会被填充00
     * @param isBigEndian 是否高位在前
     * @return
     */
    public static byte[] intToBytes(int value, int len, boolean isBigEndian) {

//        /** 不再判断长度限制 **/
//        if (value < 0) {
//            throw new IllegalArgumentException("illegal input:" + value);
//        }

        byte[] bs = new byte[len];
        Arrays.fill(bs, (byte) 0x00);
        for (int i = 0; i < len; i++) {
            int j = i;
            if (isBigEndian) {
                j = len - i - 1;
            }
            bs[j] = (byte) ((value >> (i * 8)) & 0xff);
        }
        return bs;
    }

    public static byte[] subBytes(byte[] src, int begin, int count) {
        byte[] bs = new byte[count];
        System.arraycopy(src, begin, bs, 0, count);
        return bs;
    }

    // 合并command_id和ret
    public static byte[] byteCommanid_ret(int command_id,int ret)
    {
        byte[] comm_id = intToBytes(command_id, 4, false);
        byte[] ret_bytes = intToBytes(ret, 4, false);
        return byteMerger(comm_id, ret_bytes);
    }

    //合并连个byte[]
    public static byte[] byteMerger(byte[] bt1, byte[] bt2){
        if(bt2==null)
            return bt1;
        byte[] bt3 = new byte[bt1.length+bt2.length];
        System.arraycopy(bt1, 0, bt3, 0, bt1.length);
        System.arraycopy(bt2, 0, bt3, bt1.length, bt2.length);
        return bt3;
    }

    //合并连个byte[],带返回报文长�?
    public static byte[] byteMerger_addLen(byte[] bt2,int time)
    {
        if(bt2==null)
        {
            return new byte[4];
        }
        byte[] output = new byte[bt2.length+time*4];
        for(int i=0;i<time;i++)
        {
            int len = bt2.length+i*4;
            byte[] trans = intToBytes(len, 4, false);
//        	LoggerUtil.d(ISOUtils.hexString(trans));
            System.arraycopy(trans, 0, output, (time-i-1)*4, 4);
        }
        System.arraycopy(bt2, 0, output, time*4, bt2.length);
        return output;
    }



    public static byte[] Object2bytes(Object obj)
    {

        if (obj==null)//void
        {
            LoggerUtil.d("Object2bytes null");
            return Tools.byteMerger_addLen("NULL".getBytes(),1);// 作为remaind_size
        }
        LoggerUtil.v("call Object2bytes:"+obj.getClass());
        if (obj.getClass().equals(int.class)||obj.getClass().equals(Integer.class))//int或long
        {
            return Tools.intToBytes((Integer)obj,4,false);
        }
        if (obj.getClass().equals(String.class))//String
        {
            String s=(String)obj;
            LoggerUtil.d("sss="+s);
            try {
                return Tools.byteMerger_addLen(s.getBytes("GBK"),1);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        }
        if (obj.getClass().equals(new byte[1].getClass()))//byte[]
        {
            byte[] B=(byte[])obj;
            return Tools.byteMerger_addLen(B, 1);
        }
        if (obj.getClass().equals(Boolean.class)||obj.getClass().equals(boolean.class))//与关键字封装层约定用c做boolean类型,内容占位1
        {
            LoggerUtil.d("boolean==="+(Boolean)obj);
            byte[] b={0x00};
            if((Boolean)obj)
                b[0]=0x01;
            return b;
        }
        if(obj.getClass().equals(ApnEntity.class))
        {
            ApnEntity apnEntity = (ApnEntity) obj;
            LoggerUtil.d(apnEntity.getName());
            ObjectMapper mapper = new ObjectMapper();
            String strJson="";
            try {
                strJson = mapper.writeValueAsString(apnEntity);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            return Tools.byteMerger_addLen(strJson.toString().getBytes(),1);
        }
        if(obj.getClass().equals(ArrayList.class))
        {
            List<Object> objList = (List<Object>) obj;
            LoggerUtil.v("size:"+objList.size());
            if(objList.size()>1)
            {
                if(objList.get(0).getClass().equals(ApnEntity.class))// ApnEntity实体�?
                {
                    StringBuffer strJson = new StringBuffer();
                    List<ApnEntity> entityList = (List<ApnEntity>) obj;
                    for (ApnEntity apnEntity:entityList)
                    {
                        ObjectMapper mapper = new ObjectMapper();
                        try {
                            strJson.append(mapper.writeValueAsString(apnEntity)+"|");
                        } catch (JsonProcessingException e) {
                            e.printStackTrace();
                        }
                    }
                    strJson.deleteCharAt(strJson.length()-1);// 删除�?后一个|
                    return Tools.byteMerger_addLen(strJson.toString().getBytes(),1);
                }
                if(objList.get(0).getClass().equals(String.class))// List<String>
                {
                    StringBuffer strBuffer = new StringBuffer();
                    List<String> str_list = (List<String>) obj;
                    for (String value:str_list)
                    {
                        strBuffer.append(value+",");
                    }
                    strBuffer.deleteCharAt(strBuffer.length()-1);// 删除�?后一个|
                    return Tools.byteMerger_addLen(strBuffer.toString().getBytes(),1);
                }
            }
        }
        if(obj.getClass().equals(String[].class))// 字符串数�?
        {
            String[] strArray=(String[])obj;
            if(strArray.length==1)
                return Tools.byteMerger_addLen(strArray[0].getBytes(),1);
            else
            {
                StringBuffer appStr = new StringBuffer();
                for (String str:strArray) {
                    appStr.append(str+",");
                }
                appStr.deleteCharAt(appStr.length()-1);// 删除�?后一个字符串
                return Tools.byteMerger_addLen(appStr.toString().getBytes(),1);
            }
        }
        if(obj.getClass().equals(BackBean.class))
        {
            BackBean bean = (BackBean) obj;
            return bean.getBytes();
        }
        if(obj.getClass().equals(SwipBack.class))
        {
            SwipBack swipBack = (SwipBack) obj;
            return swipBack.getBytes();
        }
        return "o".getBytes();//默认void
    }

    /**
     * 拷贝assets目录下的文件到指定目录下
     * <br><b>注意：如果指定的目录下已经存在相同名称的文件，则会被覆盖</b>
     * @param context 上下文对象
     * @param assetsFile assets里db文件目录，如：databaseDir/city.db
     * @param outFilePath 目标目录，如：/storage/emulated/0/MyDatabase/city.db
     * @return true:拷贝成功，false:拷贝失败
     */
    public static boolean copyAssetsFileToOutPath(Context context, String assetsFile, String outFilePath) {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            File outFile = new File(outFilePath);
            if (!outFile.exists()) {
                if (!outFile.getParentFile().exists()) {
                    outFile.getParentFile().mkdirs();
                }
                outFile.createNewFile();
            }

            AssetManager am = context.getAssets();
            inputStream = am.open(assetsFile);
            outputStream = new FileOutputStream(outFilePath);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            outputStream.flush();
            return true;
        } catch (Exception e) {
            Log.e("IntelligentServer：", String.format("拷贝assets目录下文件：[%s] 到 [%s]失败：%s", assetsFile, outFilePath, e.toString()));
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (Exception e) {
            }
        }
        return false;
    }

    public static int copyFilesFromAssets(Context context, String oldPath, String newPath) {
        try {
            String[] fileNames = context.getAssets().list(oldPath);
            if (fileNames.length > 0) {
                File file = new File(newPath);
                for (String fileName : fileNames) {
                    copyFilesFromAssets(context, oldPath + "/" + fileName,
                            newPath + "/" + fileName);
                }
            } else {
                // file
                InputStream is = context.getAssets().open(oldPath);
                FileOutputStream fos = new FileOutputStream(new File(newPath));
                byte[] buffer = new byte[1024];
                int byteCount;
                while ((byteCount = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, byteCount);
                }
                fos.flush();
                is.close();
                fos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
        return 1;
    }

    public static Boolean copyFilesFromSdcard(String srcPath, String dstPath) {
        try {
            File srcFile = new File(srcPath);
            if (!srcFile.exists() || !srcFile.isFile() || !srcFile.canRead()) {
                return false;
            }
            FileInputStream fileInputStream = new FileInputStream(srcPath);
            FileOutputStream fileOutputStream = new FileOutputStream(dstPath);
            byte[] buffer = new byte[1024];
            int byteRead;
            while (-1 != (byteRead = fileInputStream.read(buffer))) {
                fileOutputStream.write(buffer, 0, byteRead);
            }
            fileInputStream.close();
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
