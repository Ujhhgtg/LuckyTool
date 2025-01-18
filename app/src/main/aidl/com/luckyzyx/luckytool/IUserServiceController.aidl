package com.luckyzyx.luckytool;

import android.content.pm.UserInfo;

interface IUserServiceController {
    List<UserInfo> getUsers();
    UserInfo getUserInfo(int userId);
}