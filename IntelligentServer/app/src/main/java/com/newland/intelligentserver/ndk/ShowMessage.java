package com.newland.intelligentserver.ndk;

import com.newland.intelligentserver.TestLocalSocketActivity;
public class ShowMessage {
    static private TestLocalSocketActivity mTestLocalSocketActivity;
    public ShowMessage(TestLocalSocketActivity testLocalSocketActivity) {
        mTestLocalSocketActivity = testLocalSocketActivity;
    }
    static public void showmsg(String text){
        mTestLocalSocketActivity.showPromptInfo(text);
    }
    static public void showmsgAndButton(String text){
        mTestLocalSocketActivity.showPromptInfoandButton(text);
    }
    static public void removemsgAndButton(){
        mTestLocalSocketActivity.removePromptInfoandButton();
    }
}
