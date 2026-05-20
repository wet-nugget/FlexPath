package com.example.flexpath.ui

import android.view.View
import android.view.ViewGroup

fun ViewGroup.setEnabledRecursive(enabled: Boolean) {
    isEnabled = enabled
    for (i in 0 until childCount) {
        val child = getChildAt(i)
        child.isEnabled = enabled
        if (child is ViewGroup) child.setEnabledRecursive(enabled)
    }
}

fun View.setVisible(visible: Boolean) {
    visibility = if (visible) View.VISIBLE else View.GONE
}
