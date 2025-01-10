package com.luckyzyx.luckytool;

interface IDexOptController {
    void clearApplicationProfileData(String packageName);
    boolean performDexOptMode(String packageName);
}