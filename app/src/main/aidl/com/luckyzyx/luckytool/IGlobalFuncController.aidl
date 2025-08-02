package com.luckyzyx.luckytool;

interface IGlobalFuncController {
    String getFileText(String dir);
    String getOtaVersion();
    String getManifestVersion();
    String getMarketName();
    String getFlashInfo();
    String getPcbInfo();
    String getSnInfo();
    String getPrjNameInfo();
    String getSlotInfo();
    String getCpuInfo();
}