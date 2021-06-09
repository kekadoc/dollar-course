package com.kekadoc.test.course.dollar

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.work.ListenableWorker
import androidx.work.testing.TestListenableWorkerBuilder
import com.kekadoc.test.course.dollar.service.CourseUpdateWorker
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TestCourseWorker {
    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun runWork() {
        val worker = TestListenableWorkerBuilder<CourseUpdateWorker>(context).build()
        runBlocking {
            val result = worker.doWork()
            assert(result == ListenableWorker.Result.success())
        }
    }

}