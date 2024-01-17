package com.oplus.darkmode;

public class OplusDarkModeData {
    
    public boolean mAlreadyClickByUser;
    public int mCurType;
    public int mIsRecommend;
    public int mOldType;
    public boolean mOpenByUser;
    public long mVersionCode;
    
    public OplusDarkModeData() {
        this.mVersionCode = -1L;
        this.mIsRecommend = 0;
        this.mOldType = 0;
        this.mCurType = 0;
        this.mOpenByUser = false;
        this.mAlreadyClickByUser = false;
    }
    
}
