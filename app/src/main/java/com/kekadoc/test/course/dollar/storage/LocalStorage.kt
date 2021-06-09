package com.kekadoc.test.course.dollar.storage

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit
import com.kekadoc.test.course.dollar.model.ValCurs
import com.kekadoc.test.course.dollar.model.ValCursRange
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class LocalStorage private constructor(context: Context)  {

    companion object {
        private const val TAG: String = "LocalStorage-TAG"
        const val KEY_DAILY_COURSE = "DailyCourse"
        const val KEY_MONTHLY_COURSE = "MonthlyCourse"

        @Volatile private var instance: LocalStorage? = null

        fun getInstance(context: Context): LocalStorage {
            return instance ?: synchronized(this) {
                instance ?: LocalStorage(context).also { instance = it }
            }
        }

    }

    private val preference = context.getSharedPreferences("localStorage", Context.MODE_PRIVATE)
    private val listener = SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
        Log.e(TAG, "OnSharedPreferenceChangeListener: $key")
        if (key == KEY_DAILY_COURSE) _dailyCourse.value = loadDailyCourse()
        if (key == KEY_MONTHLY_COURSE) _monthlyCourse.value = loadMonthlyCourse()
    }

    private val _dailyCourse = MutableStateFlow<ValCurs?>(null)
    val dailyCourse = _dailyCourse.asStateFlow()

    private val _monthlyCourse = MutableStateFlow<ValCursRange?>(null)
    val monthlyCourse = _monthlyCourse.asStateFlow()

    init {
        _dailyCourse.value = loadDailyCourse()
        _monthlyCourse.value = loadMonthlyCourse()
        preference.registerOnSharedPreferenceChangeListener(listener)
    }

    private fun clear() {
        preference.unregisterOnSharedPreferenceChangeListener(listener)
    }

    suspend fun clearStorage(): Boolean {
        val clearing = preference.editSuspend { clear() }
        if (clearing) {
            _dailyCourse.value = null
            _monthlyCourse.value = null
        }
        return clearing
    }

    suspend fun saveDailyCourse(course: ValCurs) {
        val str = Json.encodeToString(course)
        val commit = preference.editSuspend { putString(KEY_DAILY_COURSE, str) }
        if (commit) _dailyCourse.value = course
    }
    suspend fun saveMonthlyCourse(course: ValCursRange) {
        val str = Json.encodeToString(course)
        val commit = preference.editSuspend { putString(KEY_MONTHLY_COURSE, str) }
        if (commit) _monthlyCourse.value = course
    }

    private fun loadDailyCourse(): ValCurs? {
        val data = preference.getString(KEY_DAILY_COURSE, null) ?: return null
        return try {
            Json.decodeFromString<ValCurs>(data)
        } catch (e: Throwable) {
            null
        }
    }
    private fun loadMonthlyCourse(): ValCursRange? {
        val data = preference.getString(KEY_MONTHLY_COURSE, null) ?: return null
        return try {
            Json.decodeFromString<ValCursRange>(data)
        } catch (e: Throwable) {
            null
        }
    }


    private suspend fun SharedPreferences.editSuspend(block: SharedPreferences.Editor.() -> Unit): Boolean {
        return suspendCoroutine { continuation ->
            kotlin.runCatching {
                val editor = edit()
                block(editor)
                editor.commit()
            }.onFailure {
                continuation.resumeWithException(it)
            }.onSuccess {
                continuation.resume(it)
            }
        }
    }

}