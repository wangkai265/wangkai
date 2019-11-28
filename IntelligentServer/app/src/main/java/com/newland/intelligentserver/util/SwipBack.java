package com.newland.intelligentserver.util;

/**
 * Created by Administrator on 2019/7/10.
 */

public class SwipBack {
    int ret = -1;
    byte[] firstTrack=new byte[4];
    byte[] secondTrack = new byte[4];
    byte[] thirdTrack = new byte[4];

    public void setRet(int ret) {
        this.ret = ret;
    }

    public void setFirstTrack(byte[] buffer) {
        this.firstTrack = buffer;
    }

    public void setSecondTrack(byte[] buffer)
    {
        this.secondTrack = buffer;
    }

    public void setThirdTrack(byte[] buffer)
    {
        this.thirdTrack = buffer;
    }

    public byte[] getBytes()
    {
        byte[] test1 = Tools.byteMerger(Tools.intToBytes(ret, 4, false), Tools.byteMerger_addLen(firstTrack, 1));
        byte[] test2 = Tools.byteMerger(Tools.byteMerger_addLen(secondTrack, 1), Tools.byteMerger_addLen(thirdTrack, 1));
        return Tools.byteMerger(test1, test2);
    }
}
