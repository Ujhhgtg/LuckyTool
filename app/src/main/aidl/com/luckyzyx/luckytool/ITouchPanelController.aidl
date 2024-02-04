package com.luckyzyx.luckytool;

interface ITouchPanelController {
    boolean checkTouchMode();
    int getTouchMode();
    void setTouchMode(int value);
}