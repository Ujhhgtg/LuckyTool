package com.luckyzyx.luckytool.hook.scope.oplusgames

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.java.ArrayListClass
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.highcapable.yukihookapi.hook.type.java.ListClass
import com.highcapable.yukihookapi.hook.type.java.StringClass
import com.luckyzyx.luckytool.utils.DexkitUtils.checkDataList
import org.luckypray.dexkit.DexKitBridge

class EnableSupportCompetitionMode(val dexKitBridge: DexKitBridge) : YukiBaseHooker() {
    override fun onHook() {
        //Source CompetitionModeManager
        //Search isSupportCompetitionMode
        dexKitBridge.findClass {
            matcher {
                fields {
                    addForType(ListClass.name)
                }
                methods {
                    add { paramCount(0);returnType(ListClass) }
                    add { paramCount(0);returnType(BooleanType) }
                    add { paramTypes(StringClass, ArrayListClass) }
                }
            }
        }.apply {
            checkDataList("EnableSupportCompetitionMode")
            first().name.toClass().apply {
                method {
                    emptyParam()
                    returnType = BooleanType
                    order().index(2)
                }.hook {
                    replaceToTrue()
                }
            }
        }
    }
}