package com.newland.intelligentserver.cons;


/**
 * Created by Administrator on 2019/6/20.
 */
public interface ConstJdk {
    final int NoSuch_Field_Error			= -1001;
    final int Invocation_Target_Exception 	= -1002;
    final int NoSuch_Method_Exception		= -1003;
    final int Illegal_Access_Exception		= -1004;
    final int Illegal_Argument_Exception	= -1005;
    final int Class_NotFound_Exception		= -1006;
    final int File_NotFound_Exception		= -1007;
    final int IO_Exception					= -1008;
    final int Process_Timeout_Exception     = -1009;
    final int Device_Invoke_Exception       = -1010;
    final int Unsupported_Encoding_Exception = -1011;
    final int Common_Exception              = -1012;
    final int Null_Pointer_Exception        = -1013;
    final int Device_RT_Exception           = -1014;

    final int Mpos_Controller_Exception = -2001;

    /**SDK的错误码返回值*/
    final int SDK_ERR_PARA = -3001;

    /**JDK的错误码返回值*/
    final int JDK_OK =0;
    final int JDK_ERR = -1;
    /** Android端文件系统的返回值 add by zhengxq 20171115*/
    public final int JDK_FS_PARA_ERR    = -6;
    public final int JDK_FS_OPEN_FAIL   = -11;
    public final int JDK_FS_CREATE_FAIL = -12;
    public final int JDK_FS_PATH_ERR    = -13;
    public final int JDK_FS_NO_EXIST    = -14;
    public final int JDK_FS_READ_FAIL   = -15;/**读文件操作失败*/
    public final int JDK_FS_DEL_FAIL    = -16;/**删除文件失败*/
    public final int JDK_FS_CLOSE_FAIL  = -17;/**关闭文件失败*/
    public final int JDK_FS_SIZE_FAIL   = -18;/**获取文件大小失败*/

    /**扫码相关错误码*/
    public final int JDK_SCAN_NO_RELEASE = -2;  /**回调接口注册未释放，初始化失败**/
    public final int JDK_SCAN_PARSE_LIBRARY_FAIL = -1;  /**解析库初始化异常**/
    public final int JDK_SCAN_PARSE_FAIL = 0;  /**无法解析，图片有损坏**/
    public final int JDK_SCAN_PARSE_SUCC = 1;  /**转换数据成功，正在解析中**/


    /**SDK的错误码返回值*/
    final int SDK_OK                    =0;
    final int SDK_Reader_Open_Failed    = -3000;
    final int SDK_Exception             = -3001;

}
