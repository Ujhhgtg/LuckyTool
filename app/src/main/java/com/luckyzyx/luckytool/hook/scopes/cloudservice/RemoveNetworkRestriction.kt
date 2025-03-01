package com.luckyzyx.luckytool.hook.scopes.cloudservice

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.ContextClass
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.highcapable.yukihookapi.hook.type.java.IntType
import org.lsposed.lsparanoid.Obfuscate
import com.luckyzyx.luckytool.utils.DexkitUtils.checkDataList
import org.luckypray.dexkit.DexKitBridge

@Obfuscate
class RemoveNetworkRestriction(val dexKitBridge: DexKitBridge) : YukiBaseHooker() {
    override fun onHook() {
        //Source BackUpActivity / BackupRestoreHelper -> backup_currently_mobile
        //Source BackupRestoreCheckerUtils -> check -> ? 2 : 0
        //Source RestoreCheck / WifiCheck -> check -> BackupRestoreCode -> NO_WIFI / SUCCESS
        //Source NetworkUtil -> 2 == ?() -> getSystemService -> connectivity
        //Search Const.Callback.NetworkState.NetworkType.NETWORK_MOBILE -> ? 1 : 0 -> Method
        dexKitBridge.findClass {
            matcher {
                methods {
                    add {
                        paramCount(0)
                        returnType(IntType)
                        usingStrings("connectivity")
                        usingNumbers(0, 1, 2)
                    }
                    add {
                        paramTypes(IntType)
                        returnType(BooleanType)
                    }
                    add {
                        paramTypes(ContextClass)
                        returnType(BooleanType)
                        usingStrings("NetworkUtil", "connectivity", "isMobileDataNetwork")
                    }
                    add {
                        paramTypes(ContextClass)
                        returnType(BooleanType)
                        usingStrings("NetworkUtil", "connectivity", "isNetworkConnected")
                    }
                }
            }
        }.apply {
            checkDataList("RemoveNetworkRestriction")
            single().name.toClass().apply {
                method { emptyParam();returnType = IntType }.hookAll {
                    after { if (result<Int>() == 1) result = 2 }
                }
            }
        }
    }
}