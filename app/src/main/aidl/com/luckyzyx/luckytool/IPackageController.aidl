package com.luckyzyx.luckytool;

interface IPackageController {
    void clearApplicationProfileData(String packageName);
    boolean performDexOptMode(String packageName);
}