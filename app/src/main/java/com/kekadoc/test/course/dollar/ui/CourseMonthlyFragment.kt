package com.kekadoc.test.course.dollar.ui

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kekadoc.test.course.dollar.ActivityViewModel
import com.kekadoc.test.course.dollar.R
import com.kekadoc.test.course.dollar.databinding.CourseViewBinding
import com.kekadoc.test.course.dollar.databinding.FragmentCourseMonthlyBinding
import com.kekadoc.test.course.dollar.dpToPx
import com.kekadoc.test.course.dollar.model.CourseRecord
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class CourseMonthlyFragment : DialogFragment() {

    private val viewModel by activityViewModels<ActivityViewModel>()
    private val adapter = Adapter()
    private lateinit var binding: FragmentCourseMonthlyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentCourseMonthlyBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.recyclerView.apply {
            adapter = this@CourseMonthlyFragment.adapter
            addItemDecoration(Decorator(context.dpToPx(4f).toInt()))
        }
        binding.toolbar.apply {
            setNavigationOnClickListener {
                val activity = requireActivity()
                if (activity is Navigation)
                    activity.navigate(R.id.action_destination_course_month_to_destination_course_day)
                else dismiss()
            }
        }
        viewModel.storage.monthlyCourse.onEach {
            if (it == null) {
                binding.toolbar.title = getString(R.string.course_no_data)
                binding.toolbar.subtitle = null
            } else {
                binding.toolbar.title = getString(R.string.dialog_course_monthly_title)
                binding.toolbar.subtitle = "${it.from} - ${it.to}"
            }
            adapter.submitList(it?.records)
        }.launchIn(lifecycleScope)
    }

    private class Decorator(val space: Int) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            outRect.set(space, space, space, space)
        }
    }

    private object ItemDataCallback : DiffUtil.ItemCallback<CourseRecord>() {
        override fun areItemsTheSame(oldItem: CourseRecord, newItem: CourseRecord): Boolean {
            return oldItem.id == newItem.id
        }
        override fun areContentsTheSame(oldItem: CourseRecord, newItem: CourseRecord): Boolean {
            return oldItem == newItem
        }
    }
    private class VH(val binding: CourseViewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(value: CourseRecord) {
            binding.textViewDate.text = value.date
            binding.textViewValue.text = value.value
        }
    }

    private class Adapter : ListAdapter<CourseRecord, VH>(ItemDataCallback) {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            return VH(CourseViewBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }
        override fun onBindViewHolder(holder: VH, position: Int) {
            holder.bind(getItem(position))
        }
    }

}