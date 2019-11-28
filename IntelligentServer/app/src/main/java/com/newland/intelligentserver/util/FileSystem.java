package com.newland.intelligentserver.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import com.newland.intelligentserver.cons.ConstJdk;

/***
 * JDK文件系统
 */
public class FileSystem implements ConstJdk
{

    /**
     * 打开Android端文件文件
     * @param path   文件路径
     * @param mode   打开模式"r"（已只读方式打开，如果不存在则失败）or "w"（以写的方式打开，如果文件不存在则创建）
     * @return
     * 			JDK_OK 操作成功
     * 			JDK_PARA_ERR 参数错误（文件名为NULL，模式不正确）
     * 			JDK_FS_PATH_ERR 文件路径错误
     * 			JDK_FS_CREATE_FAIL 创建文件失败
     * 			JDK_FS_NO_EXIST  文件不存在
     */
    public int JDK_FsOpen(String path, String mode)
    {
        int value = 0;
        File file;
        // 异常情况
        if (path.equals("") || path == null||(!mode.equalsIgnoreCase("r") && !mode.equalsIgnoreCase("w")))
        {
            value = JDK_FS_PARA_ERR;
            return value;
        }
        file = new File(path);
        if (mode.equalsIgnoreCase("w")) // 以写模式打开 若文件不存在会创建
        {
            if (!file.exists())
            {
                try
                {
                    // 创建目录
                    JDK_FsCreateDirectory(file.getParent()+"/");
                    // 创建文件
                    if (file.createNewFile()) {
                        value = JDK_OK;
                    } else {
                        value = JDK_FS_CREATE_FAIL;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return IO_Exception;
                }
            }
        } else if (mode.equalsIgnoreCase("r"))
        {
            if (!file.exists()) {
                value = JDK_FS_NO_EXIST;
            } else {
                value = JDK_OK;
            }
        }
        return value;
    }


    /**
     * @description 从打开的文件当前指针读unLength个字符到缓冲区psBuffer
     * @param fileName  	文件路径
     * @param length   需要读取的字符的长度
     * @return 返回实际读到的数据长度
     * 		    JDK_PARA_ERR 参数错误
     * 			JDK_FS_READ_FAIL 读取失败
     * 			JDK_FS_NO_EXIST 文件不存在
     */
    public BackBean JDK_FsRead(String fileName,int offset,int length)
    {
        int rlen = 0;
        BackBean backBean = new BackBean();
        if(fileName == null||fileName.equals(""))
        {
            backBean.setRet(JDK_FS_PARA_ERR);
            return backBean;
        }
        byte[] psBuffer = new byte[length];

        try
        {
            RandomAccessFile randomAccessFile = new RandomAccessFile(fileName, "rw");
            randomAccessFile.seek(offset);
            rlen = randomAccessFile.read(psBuffer, 0, length);
            randomAccessFile.close();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
            rlen = File_NotFound_Exception;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            rlen = IO_Exception;
        }
        backBean.setRet(rlen);
        if(rlen>0)
        {
            backBean.setBuffer(Tools.subBytes(psBuffer, 0, rlen));
        }
        return backBean;
    }

//	/**
//	 * 输入流
//	 * @param input
//	 */
//	public int JDK_FsClose(String path)
//	{
//		int nRet = JDK_OK;
//		FileInputStream fileInput;
//
//		try {
//			fileInput = new FileInputStream(new File(path));
//			fileInput.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//			nRet = JDK_FS_CLOSE_FAIL;
//		}
//		return nRet;
//	}

    public int JDK_FsWrite(String fileName,byte[] psBuffer,int unLength,int mode)
    {
        int wrlen = 0;
        if(fileName == null||fileName.equals(""))
            return JDK_FS_PARA_ERR;
        try
        {
            RandomAccessFile randomAccessFile = new RandomAccessFile(fileName, "rw");
            long fileLength = randomAccessFile.length();
            switch (mode)
            {
                case 0:
                    randomAccessFile.write(psBuffer, 0, unLength);
                    wrlen = (int) randomAccessFile.length();
                    break;

                case 2:
                    randomAccessFile.seek(fileLength);
                    randomAccessFile.write(psBuffer, 0, unLength);
                    wrlen = (int) (randomAccessFile.length()-fileLength);
                    break;

                default:
                    break;
            }
            randomAccessFile.close();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
            wrlen = File_NotFound_Exception;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            wrlen = IO_Exception;
        }
        return wrlen;
    }

    /**
     * @param pszName 要删除的文件名
     * @return JDK_OK 操作成功
     * 		   JDK_PARA_ERR 参数错误
     * 		   JDK_FS_NO_EXIST 文件不存在
     * 		   JDK_FS_DEL_FAIL 删除文件失败
     */
    public int JDK_FsDel(String pszName)
    {
        if (pszName == null || pszName.equals(""))
            return JDK_FS_PARA_ERR;
        File file = new File(pszName);
        if (file.exists())
        {
            if (!file.delete())
                return JDK_FS_DEL_FAIL;
        } else
            return JDK_FS_NO_EXIST;
        return JDK_OK;
    }

    /**
     * 文件重命名
     * @param srcName  源文件名
     * @param DstName  目标文件名
     * @return NDK_OK 操作成功 NDK_ERR 操作失败 NDK_ERR_PARA 参数错误
     */
    public int JDK_FsRename(String path, String srcName, String DstName) {
        int value = 0;
        if (path == null || path.equals("") || srcName.equals("")|| srcName == null || DstName.equals("") || DstName == null) {
            value = JDK_FS_PARA_ERR;
            return value;
        }
        // 原文件跟目标文件名不同时才进行重命名
        if (!srcName.equals(DstName)) {
            File srcFile = new File(path + "/" + srcName);
            File dstFile = new File(path + "/" + DstName);
            if (dstFile.exists()) {
                value = JDK_ERR;
            } else {
                if (srcFile.renameTo(dstFile)) {
                    value = JDK_OK;
                } else {
                    value = JDK_ERR;
                }
            }
        }
        return value;
    }

    /**
     * 测试报告是否存在不存在的话就生成测试报告
     * @param path	文件路径
     * @return
     * 			JDK_OK 操作成功（文件存在）
     * 			JDK_FS_NO_EXIST 文件不存在
     * 			JDK_PARA_ERR 参数错误
     */
    public int JDK_FsExist(String path)
    {
        int value;
        if (path.equalsIgnoreCase("") || path == null) {
            value = JDK_FS_PARA_ERR;
            return value;
        }
        File file = new File(path);
        if (file.exists())
            value = JDK_OK;
        else
            value = JDK_FS_NO_EXIST;
        return value;
    }

    /**
     * 获取文件大小
     * @param pszName
     * @return 返回文件的大小
     * 			JDK_PARA_ERR 参数错误
     * 			JDK_FS_SIZE_FAIL 获取文件大小失败
     * 			JDK_FS_CLOSE_FAIL 文件关闭失败
     * 			JDK_FS_NO_EXIST 文件不存在
     */
    public int JDK_FsFileSize(String pszName) {
        int nRet = JDK_OK;
        if (pszName == null || pszName.equals("")) {
            return JDK_FS_PARA_ERR;
        }
        File file = new File(pszName);
        if (file.exists())
        {
            try {
                FileInputStream fis = new FileInputStream(file);
                nRet = fis.available();
                fis.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                nRet = File_NotFound_Exception;
            } catch (IOException e1) {
                e1.printStackTrace();
                nRet = IO_Exception;
            }
        } else {
            nRet = JDK_FS_NO_EXIST;
        }
        return nRet;
    }

    /**
     * 创建目录
     *
     * @param pszName
     *            目录名称
     * @return NDK_OK 操作成功 NDK_ERR_PARA 参数错误 NDK_ERR 操作失败 NDK_ERR_PATH 文件路径错误
     */
    public int JDK_FsCreateDirectory(String pszName) {
        if (pszName == null || pszName.equals(""))
            return JDK_FS_PARA_ERR;
        File file = new File(pszName);
        if (file.exists()==false) {
            LoggerUtil.d("JDK_FsCreateDirectory");
            file.mkdirs();
        }
        return JDK_OK;
    }

}
