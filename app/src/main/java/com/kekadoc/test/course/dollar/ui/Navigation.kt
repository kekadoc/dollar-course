package com.kekadoc.test.course.dollar.ui

import android.os.Bundle

interface Navigation {
    fun navigate(id: Int, data: Bundle = Bundle.EMPTY)
}