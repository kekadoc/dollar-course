package com.kekadoc.test.course.dollar

import android.app.ActivityManager
import android.content.Context
import android.util.TypedValue
import androidx.annotation.Dimension

fun Context.dpToPx(@Dimension(unit = Dimension.DP) dp: Float): Float {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics)
}

fun Context.isAppForeground(): Boolean {
    val mActivityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val l = mActivityManager.runningAppProcesses
    for (info in l) {
        if (info.uid == applicationInfo.uid && info.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
            return true
        }
    }
    return false
}
