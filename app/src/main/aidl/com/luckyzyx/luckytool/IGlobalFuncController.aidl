package com.luckyzyx.luckytool;

interface IGlobalFuncController {
    String getFileText(String dir);
    String getOtaVersion();
    String getMarketName();
    String getLcdInfo();
    String getFlashInfo();
    String getPcbInfo();
    String getSnInfo();
    String getPrjNameInfo();
    String getSlotInfo();
}