package com.newland.intelligentserver.util;

/**
 * Created by Administrator on 2019/6/20.
 */
public class BackBean {
    int ret;
    byte[] buffer=null;

    public void setRet(int ret) {
        this.ret = ret;
    }

    public void setBuffer(byte[] buffer) {
        this.buffer = buffer;
    }

    public byte[] getBytes()
    {
        if(buffer==null)
            return Tools.intToBytes(ret, 4, false);
        else
            return Tools.byteMerger(Tools.intToBytes(ret, 4, false), Tools.byteMerger_addLen(buffer, 1));
    }

    //自测自我填写
    public int getRet(){return ret;}
}
