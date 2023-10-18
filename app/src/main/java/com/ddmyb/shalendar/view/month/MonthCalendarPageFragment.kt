package com.ddmyb.shalendar.view.month

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat.getColor
import androidx.core.view.get
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.ddmyb.shalendar.R
import com.ddmyb.shalendar.databinding.FragmentMonthCalendarPageBinding
import com.ddmyb.shalendar.databinding.ItemMonthDateBinding
import com.ddmyb.shalendar.util.HttpResult
import com.ddmyb.shalendar.util.Logger
import com.ddmyb.shalendar.util.MutableLiveListData
import com.ddmyb.shalendar.view.holiday.data.HolidayDTO
import com.ddmyb.shalendar.view.month.adapter.MonthCalendarDateScheduleRVAdapter
import com.ddmyb.shalendar.view.month.adapter.MonthDateDetailAdapter
import com.ddmyb.shalendar.view.month.data.MonthCalendarDateData
import com.ddmyb.shalendar.view.month.data.MonthPageData
import com.ddmyb.shalendar.view.month.data.ScheduleData
import com.ddmyb.shalendar.view.month.data.TimeTableScheduleList
import com.ddmyb.shalendar.view.month.presenter.MonthCalendarPagePresenter
import java.util.Calendar

class MonthCalendarPageFragment(private val now: Long) : Fragment(R.layout.fragment_month_calendar_page) {

    private val logger = Logger("MonthCalendarPageFragment", true)

    private lateinit var presenter: MonthCalendarPagePresenter

    private lateinit var binding: FragmentMonthCalendarPageBinding

    private val cal = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        logger.logD("${cal.get(Calendar.YEAR)}, ${cal.get(Calendar.MONTH)+1} created")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cal.timeInMillis = now

        presenter = MonthCalendarPagePresenter(
            MonthPageData(
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH)+1,
                MutableLiveListData()
            )
        )
        binding = FragmentMonthCalendarPageBinding.bind(view)

        binding.data = presenter.pageData
        val itemBindingList = mutableListOf<ItemMonthDateBinding>()

        for (i in 0 until presenter.pageData.calendarDateList.value!!.size) {
            val itemBinding: ItemMonthDateBinding = DataBindingUtil.bind(binding.dateLayout[i])!!
            itemBindingList.add(itemBinding)

            val calendarDate = presenter.pageData.calendarDateList.value!![i]

            itemBinding.schedules.apply {
                layoutManager = object: LinearLayoutManager(requireContext()) {
                    override fun canScrollVertically(): Boolean {
                        return false
                    }
                }
                adapter = MonthCalendarDateScheduleRVAdapter(calendarDate.scheduleList)

            }

            calendarDate.scheduleList.apply {
                this.observeChange {
                    itemBinding.schedules.adapter!!.notifyItemChanged(it)
                }
                this.observeInsert {
                    itemBinding.schedules.adapter!!.notifyItemInserted(it)
                }
                this.observeRemove {
                    itemBinding.schedules.adapter!!.notifyItemRemoved(it)
                }
            }
            presenter.loadSchedule(i)

            itemBinding.root.setOnClickListener {
                val selectedIdx = presenter.selectDate(i)

                logger.logD("$selectedIdx, $i")
                if (selectedIdx != -1)
                    binding.dateLayout[selectedIdx].background = null

                binding.dateLayout[i].background =
                    AppCompatResources.getDrawable(requireContext(), R.drawable.month_date_selected)

                showScheduleDialog(calendarDate)
            }

            itemBinding.data = calendarDate

            if (!presenter.isThisMonth(calendarDate)) {
                itemBinding.root.alpha = 0.3F
            }
        }

        presenter.pageData.calendarDateList.apply {
            this.observeChange {
                logger.logD("change observe $it")
                itemBindingList[it].data = this.value!![it]
            }
        }

        presenter.loadHoliday(object : HttpResult<List<HolidayDTO.HolidayItem>>{
            override fun success(data: List<HolidayDTO.HolidayItem>) {

                for (item in data) {
                    val year = item.locdate/10000
                    val month = (item.locdate%10000)/100
                    val date = item.locdate%100

                    val index = presenter.findDate(year, month, date)
                    if (index != null) {
                        val dateData = presenter.pageData.calendarDateList.value!![index]
                        dateData.isHoliday = true

                        cal.set(year, month-1, date)
                        cal.set(Calendar.HOUR, 0)
                        cal.set(Calendar.MINUTE, 0)
                        cal.set(Calendar.SECOND, 0)
                        cal.set(Calendar.MILLISECOND, 0)
                        val start = cal.timeInMillis

                        cal.add(Calendar.DATE, 1)
                        val end = cal.timeInMillis-1

                        dateData.scheduleList.add(
                            ScheduleData(item.dateName, start, end, true, Color.RED)
                        )

                        presenter.pageData.calendarDateList.replaceAt(index, dateData)
                        logger.logD("holiday $dateData")
                    }

                    logger.logD("$item")
                }
            }

            override fun appFail() {
            }

            override fun fail(throwable: Throwable) {
            }

            override fun finally() {
            }
        })
    }

    private fun showScheduleDialog(date: MonthCalendarDateData) {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(getColor(requireContext(), R.color.transparent)))
        dialog.setContentView(R.layout.dialog_month_date_detail)

        dialog.findViewById<TextView>(R.id.date).text = date.date.toString()

        dialog.findViewById<TextView>(R.id.day_of_week).text = presenter.getWeekOfDay(date)

        dialog.findViewById<ViewPager2>(R.id.pager).apply {
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
            adapter = MonthDateDetailAdapter(
                requireActivity(),
                TimeTableFragment(
                    listOf(
                        TimeTableScheduleList("n1", date.scheduleList.list),
                    ),
                    idxHeight = 50,
                    idxWidthPercentage = 0.5f
                )
            )
            setCurrentItem(0, false)
            offscreenPageLimit = 1
        }

        dialog.show()

    }

}
