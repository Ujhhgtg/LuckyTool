package com.luckyzyx.luckytool;

interface IGlobalFuncController {
    String getFileText(String dir);
    String getOtaVersion();
    String getMarketName();
    String getFlashInfo();
    String getPcbInfo();
    String getSnInfo();
    String getPrjNameInfo();
    String getSlotInfo();
}