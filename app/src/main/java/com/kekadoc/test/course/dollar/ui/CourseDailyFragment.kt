package com.kekadoc.test.course.dollar.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.kekadoc.test.course.dollar.ActivityViewModel
import com.kekadoc.test.course.dollar.R
import com.kekadoc.test.course.dollar.databinding.FragmentCourseDailyBinding
import com.kekadoc.test.course.dollar.repository.HttpRepository
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class CourseDailyFragment : Fragment() {

    private val viewModel by activityViewModels<ActivityViewModel>()

    private lateinit var binding: FragmentCourseDailyBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentCourseDailyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.storage.dailyCourse.onEach {
            val dollarCourse =  it?.valutes?.find { it.id == HttpRepository.DOLLAR_ID }
            if (dollarCourse == null) {
                binding.textViewCurrentCourse.setText(R.string.course_no_data)
                binding.textViewCurrentDate.visibility = View.INVISIBLE
                binding.textViewName.visibility = View.INVISIBLE
            } else {
                binding.textViewCurrentCourse.text = dollarCourse.value
                binding.textViewCurrentDate.visibility = View.VISIBLE
                binding.textViewName.visibility = View.VISIBLE
                binding.textViewCurrentDate.text = it.date
                binding.textViewName.text = dollarCourse.name
            }

        }.launchIn(lifecycleScope)
    }

}