package com.kekadoc.test.course.dollar

import android.app.Application
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.work.BackoffPolicy
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.kekadoc.test.course.dollar.databinding.ActivityMainBinding
import com.kekadoc.test.course.dollar.repository.HttpRepository
import com.kekadoc.test.course.dollar.service.CourseUpdateWorker
import com.kekadoc.test.course.dollar.repository.LocalStorage
import com.kekadoc.test.course.dollar.ui.Navigation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.TimeUnit

/*
    Используя ресурс http://cbr.ru/development/SXML/
    Написать приложение, которое будет:
    1. Выводить списком курс доллара за последний месяц
    2. Проверять текущий курс доллара каждый день и если он больше заданного в приложении, то выводить нотификацию.
    Обязательно использовать Calendar, Retrofit + ConverterFactory для XML
    Желательно использовать rxjava или корутины
    Kotlin или java не имеет значения.
*/

class ActivityViewModel(application: Application) : AndroidViewModel(application) {

    val storage = LocalStorage.getInstance(application)

    init {
        if (storage.dailyCourse.value == null) {
            viewModelScope.launch(Dispatchers.IO) {
                storage.saveDailyCourse(HttpRepository.loadDailyCourse())
            }
        }
        if (storage.monthlyCourse.value == null) {
            viewModelScope.launch(Dispatchers.IO) {
                storage.saveMonthlyCourse(HttpRepository.loadMonthlyCourse())
            }
        }
    }

}

class MainActivity : AppCompatActivity(), Navigation {

    companion object {
        private const val COURSE_UPDATE_TASK_TAG = "COURSE_UPDATE_TASK_TAG"
        private const val TAG: String = "MainActivity-TAG"
    }

    private val viewModel by viewModels<ActivityViewModel>()

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navController = findNavController(R.id.nav_host_fragment_activity_main)

        binding.toolbar.menu.findItem(R.id.menu_item_0).apply {
            setOnMenuItemClickListener {
                navController.navigate(R.id.action_destination_course_day_to_destination_course_month)
                true
            }
        }

        tryRunWork()

    }

    private fun tryRunWork() {
        val currentTimeCalendar = Calendar.getInstance()
        val currentTimeMs = currentTimeCalendar.time.time
        val timeUpdateCalendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 8)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (currentTimeMs > time.time) add(Calendar.HOUR_OF_DAY, 24)
        }
        val timeUpdateMs = timeUpdateCalendar.time.time
        val delay = timeUpdateMs - currentTimeMs
        val uploadWorkRequest = PeriodicWorkRequestBuilder<CourseUpdateWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setBackoffCriteria(BackoffPolicy.LINEAR, 1, TimeUnit.HOURS)
            .build()

        val enqueueUniquePeriodicWork = WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            COURSE_UPDATE_TASK_TAG,
            ExistingPeriodicWorkPolicy.KEEP,
            uploadWorkRequest
        )

    }

    override fun navigate(id: Int, data: Bundle) {
        navController.navigate(id, data)
    }

}