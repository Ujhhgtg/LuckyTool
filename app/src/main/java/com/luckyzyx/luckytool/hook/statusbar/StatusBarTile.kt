package com.luckyzyx.luckytool.hook.statusbar

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.luckyzyx.luckytool.hook.scopes.systemui.ControlCenterTiles
import com.luckyzyx.luckytool.hook.scopes.systemui.FixTileAlignBothSides
import com.luckyzyx.luckytool.hook.scopes.systemui.LongPressTileOpenThePage
import com.luckyzyx.luckytool.hook.scopes.systemui.MediaPlayerPanel
import com.luckyzyx.luckytool.hook.scopes.systemui.RestorePageLayoutRowCountForEditTiles
import com.luckyzyx.luckytool.hook.scopes.systemui.SpecialTileTopGap
import com.luckyzyx.luckytool.utils.A13
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.SDK

object StatusBarTile : YukiBaseHooker() {
    override fun onHook() {
        //磁贴长按跳转事件
        if (prefs(ModulePrefs).getBoolean("restore_some_tile_long_press_event", false)) {
            if (SDK == A13) loadHooker(LongPressTileOpenThePage)
        }

        //特殊磁贴间隙
        if (prefs(ModulePrefs).getBoolean("control_center_custom_gaps_for_special_tile", false)) {
            if (SDK >= A13) loadHooker(SpecialTileTopGap)
        }

        //媒体播放器
        if (SDK >= A13) loadHooker(MediaPlayerPanel)

        //磁贴布局
        loadHooker(ControlCenterTiles)

        //磁贴两侧对齐
        if (prefs(ModulePrefs).getBoolean("fix_tile_align_both_sides", false)) {
            if (SDK >= A13) loadHooker(FixTileAlignBothSides)
        }
        //恢复磁贴编辑页面布局行数
        if (prefs(ModulePrefs).getBoolean("restore_page_layout_row_count_for_edit_tiles", false)) {
            if (SDK >= A13) loadHooker(RestorePageLayoutRowCountForEditTiles)
        }
    }
}