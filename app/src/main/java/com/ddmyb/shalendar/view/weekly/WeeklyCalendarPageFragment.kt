package com.ddmyb.shalendar.view.weekly

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import com.ddmyb.shalendar.R
import com.ddmyb.shalendar.data.Schedule
import com.ddmyb.shalendar.databinding.FragmentWeeklyCalendarPageBinding
import com.ddmyb.shalendar.view.weekly.data.WeeklyDates
import java.util.Calendar

class WeeklyCalendarPageFragment(private val now: Long): Fragment() {

    val TAG = "WeGlonD"
    private lateinit var binding: FragmentWeeklyCalendarPageBinding
    val scheduleContainers = arrayListOf<ConstraintLayout>()
    val hoursId = arrayListOf<ArrayList<Int>>()
    val viewToScheduleMap = HashMap<Int, Schedule>()
    companion object {
        var pixel_1minute = 0f
    }
    lateinit var weeklyDates: WeeklyDates

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWeeklyCalendarPageBinding.inflate(inflater, container, false)
        val cal = Calendar.getInstance()
        cal.timeInMillis = now

        weeklyDates = WeeklyDates(cal.get(Calendar.MONTH)+1, getWeekNums(cal))
        binding.data = weeklyDates

        binding.root.viewTreeObserver.addOnWindowFocusChangeListener { hasFocus ->
            pixel_1minute = binding.clPlanSunday.height / 1440f
            Log.d(TAG, "ConstlaintLayout height: " + binding.clPlanSunday.height)
            Log.d(TAG, "pixel 1minute: " + pixel_1minute)
            onResume()
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        createViewMap()

        clearScheduleViews()

        displaySchedules(weeklyDates)
    }

    private fun clearScheduleViews() {
        for (i in 0..6) {
            while(scheduleContainers[i].childCount > 24)
                scheduleContainers[i].removeViewAt(24)
        }
    }

    private fun getWeekNums(cal: Calendar): Array<Int> {
        val result = Array<Int>(7, {0})

        for (i in 0..6) {
            result[i] = cal.get(Calendar.DATE)
            cal.add(Calendar.DAY_OF_MONTH, 1)
        }

        return result
    }

    fun displaySchedules(weeklyDates: WeeklyDates) {
        // 테스트용 코드
        val cal1 = Calendar.getInstance()
        cal1.set(Calendar.HOUR_OF_DAY, 10)
        cal1.set(Calendar.MINUTE, 30)
        val cal2 = Calendar.getInstance()
        cal2.add(Calendar.DAY_OF_MONTH, 1)
        cal2.set(Calendar.HOUR_OF_DAY, 14)
        cal2.set(Calendar.MINUTE, 10)

        val cal3 = Calendar.getInstance()
        cal3.set(Calendar.HOUR_OF_DAY, 11)
        cal3.set(Calendar.MINUTE, 30)
        val cal4 = Calendar.getInstance()
        cal4.set(Calendar.HOUR_OF_DAY, 15)
        cal4.set(Calendar.MINUTE, 10)

        displaySchedule(Schedule("test", cal1.timeInMillis, cal2.timeInMillis), cal1.timeInMillis)
        displaySchedule(Schedule("text", cal3.timeInMillis, cal4.timeInMillis), cal3.timeInMillis)

        //db 구현 이후 할 일: 반복문 돌면서 일주일 간 각 날짜마다 띄워야할 schedule을 db에서 들고와서 띄우기 (now 변수 활용)
        //기간 쿼리 가능하면 그걸로 들고오자..
    }

    private fun displaySchedule(schedule: Schedule, startMillis:Long) {
        val zeroCal = Calendar.getInstance()
        zeroCal.timeInMillis = startMillis
        zeroCal.set(Calendar.HOUR_OF_DAY, 0)
        zeroCal.set(Calendar.MINUTE, 0)
        zeroCal.set(Calendar.SECOND, 0)
        zeroCal.set(Calendar.MILLISECOND, 0)

        val startCal = Calendar.getInstance()
        startCal.timeInMillis = startMillis
        val endCal = Calendar.getInstance()
        endCal.timeInMillis = schedule.endTime

        var flag = false

        if(startCal.get(Calendar.DAY_OF_MONTH) != endCal.get(Calendar.DAY_OF_MONTH)){
            flag = true
            endCal.set(Calendar.DAY_OF_MONTH, startCal.get(Calendar.DAY_OF_MONTH))
            endCal.set(Calendar.HOUR_OF_DAY, 23)
            endCal.set(Calendar.MINUTE, 59)
        }


        val dayOfWeek = startCal.get(Calendar.DAY_OF_WEEK) - 1
        val scheduleView = LayoutInflater.from(this.requireContext())
            .inflate(R.layout.custom_view_weekly_schedule, null)

        scheduleView.id = ViewCompat.generateViewId()
        viewToScheduleMap.put(scheduleView.id, schedule)

        scheduleView.findViewById<TextView>(R.id.schedule_name).text = schedule.name

        scheduleContainers[dayOfWeek].addView(scheduleView)

        val layoutParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_CONSTRAINT)
        layoutParams.topToTop = hoursId[dayOfWeek][startCal.get(Calendar.HOUR_OF_DAY)]
        layoutParams.bottomToBottom = hoursId[dayOfWeek][endCal.get(Calendar.HOUR_OF_DAY)]
        layoutParams.topMargin = (pixel_1minute * startCal.get(Calendar.MINUTE)).toInt()
        layoutParams.bottomMargin = (pixel_1minute * (60 - endCal.get(Calendar.MINUTE))).toInt()

        scheduleView.layoutParams = layoutParams

        scheduleContainers[dayOfWeek].invalidate()

        Log.d(TAG, "after y: "+ scheduleView.y)

        if (flag) {
            startCal.add(Calendar.DATE, 1)
            startCal.set(Calendar.HOUR_OF_DAY, 0)
            startCal.set(Calendar.MINUTE, 0)
            startCal.set(Calendar.SECOND, 0)
            startCal.set(Calendar.MILLISECOND, 0)
            displaySchedule(schedule, startCal.timeInMillis)
        }
    }
    private fun createViewMap() {
        scheduleContainers.clear()
        scheduleContainers.add(binding.clPlanSunday);scheduleContainers.add(binding.clPlanMonday)
        scheduleContainers.add(binding.clPlanTuesday);scheduleContainers.add(binding.clPlanWednesday)
        scheduleContainers.add(binding.clPlanThursday);scheduleContainers.add(binding.clPlanFriday);scheduleContainers.add(binding.clPlanSaturday)

        hoursId.clear()
        for (i in 1..7)
            hoursId.add(arrayListOf())

        hoursId[0].add(binding.blank000.id);hoursId[0].add(binding.blank001.id);hoursId[0].add(binding.blank002.id);hoursId[0].add(binding.blank003.id);hoursId[0].add(binding.blank004.id)
        hoursId[0].add(binding.blank005.id);hoursId[0].add(binding.blank006.id);hoursId[0].add(binding.blank007.id);hoursId[0].add(binding.blank008.id);hoursId[0].add(binding.blank009.id)
        hoursId[0].add(binding.blank010.id);hoursId[0].add(binding.blank011.id);hoursId[0].add(binding.blank012.id);hoursId[0].add(binding.blank013.id);hoursId[0].add(binding.blank014.id)
        hoursId[0].add(binding.blank015.id);hoursId[0].add(binding.blank016.id);hoursId[0].add(binding.blank017.id);hoursId[0].add(binding.blank018.id);hoursId[0].add(binding.blank019.id)
        hoursId[0].add(binding.blank020.id);hoursId[0].add(binding.blank021.id);hoursId[0].add(binding.blank022.id);hoursId[0].add(binding.blank023.id)
        hoursId[1].add(binding.blank100.id);hoursId[1].add(binding.blank101.id);hoursId[1].add(binding.blank102.id);hoursId[1].add(binding.blank103.id);hoursId[1].add(binding.blank104.id)
        hoursId[1].add(binding.blank105.id);hoursId[1].add(binding.blank106.id);hoursId[1].add(binding.blank107.id);hoursId[1].add(binding.blank108.id);hoursId[1].add(binding.blank109.id)
        hoursId[1].add(binding.blank110.id);hoursId[1].add(binding.blank111.id);hoursId[1].add(binding.blank112.id);hoursId[1].add(binding.blank113.id);hoursId[1].add(binding.blank114.id)
        hoursId[1].add(binding.blank115.id);hoursId[1].add(binding.blank116.id);hoursId[1].add(binding.blank117.id);hoursId[1].add(binding.blank118.id);hoursId[1].add(binding.blank119.id)
        hoursId[1].add(binding.blank120.id);hoursId[1].add(binding.blank121.id);hoursId[1].add(binding.blank122.id);hoursId[1].add(binding.blank123.id)
        hoursId[2].add(binding.blank200.id);hoursId[2].add(binding.blank201.id);hoursId[2].add(binding.blank202.id);hoursId[2].add(binding.blank203.id);hoursId[2].add(binding.blank204.id)
        hoursId[2].add(binding.blank205.id);hoursId[2].add(binding.blank206.id);hoursId[2].add(binding.blank207.id);hoursId[2].add(binding.blank208.id);hoursId[2].add(binding.blank209.id)
        hoursId[2].add(binding.blank210.id);hoursId[2].add(binding.blank211.id);hoursId[2].add(binding.blank212.id);hoursId[2].add(binding.blank213.id);hoursId[2].add(binding.blank214.id)
        hoursId[2].add(binding.blank215.id);hoursId[2].add(binding.blank216.id);hoursId[2].add(binding.blank217.id);hoursId[2].add(binding.blank218.id);hoursId[2].add(binding.blank219.id)
        hoursId[2].add(binding.blank220.id);hoursId[2].add(binding.blank221.id);hoursId[2].add(binding.blank222.id);hoursId[2].add(binding.blank223.id)
        hoursId[3].add(binding.blank300.id);hoursId[3].add(binding.blank301.id);hoursId[3].add(binding.blank302.id);hoursId[3].add(binding.blank303.id);hoursId[3].add(binding.blank304.id)
        hoursId[3].add(binding.blank305.id);hoursId[3].add(binding.blank306.id);hoursId[3].add(binding.blank307.id);hoursId[3].add(binding.blank308.id);hoursId[3].add(binding.blank309.id)
        hoursId[3].add(binding.blank310.id);hoursId[3].add(binding.blank311.id);hoursId[3].add(binding.blank312.id);hoursId[3].add(binding.blank313.id);hoursId[3].add(binding.blank314.id)
        hoursId[3].add(binding.blank315.id);hoursId[3].add(binding.blank316.id);hoursId[3].add(binding.blank317.id);hoursId[3].add(binding.blank318.id);hoursId[3].add(binding.blank319.id)
        hoursId[3].add(binding.blank320.id);hoursId[3].add(binding.blank321.id);hoursId[3].add(binding.blank322.id);hoursId[3].add(binding.blank323.id)
        hoursId[4].add(binding.blank400.id);hoursId[4].add(binding.blank401.id);hoursId[4].add(binding.blank402.id);hoursId[4].add(binding.blank403.id);hoursId[4].add(binding.blank404.id)
        hoursId[4].add(binding.blank405.id);hoursId[4].add(binding.blank406.id);hoursId[4].add(binding.blank407.id);hoursId[4].add(binding.blank408.id);hoursId[4].add(binding.blank409.id)
        hoursId[4].add(binding.blank410.id);hoursId[4].add(binding.blank411.id);hoursId[4].add(binding.blank412.id);hoursId[4].add(binding.blank413.id);hoursId[4].add(binding.blank414.id)
        hoursId[4].add(binding.blank415.id);hoursId[4].add(binding.blank416.id);hoursId[4].add(binding.blank417.id);hoursId[4].add(binding.blank418.id);hoursId[4].add(binding.blank419.id)
        hoursId[4].add(binding.blank420.id);hoursId[4].add(binding.blank421.id);hoursId[4].add(binding.blank422.id);hoursId[4].add(binding.blank423.id)
        hoursId[5].add(binding.blank500.id);hoursId[5].add(binding.blank501.id);hoursId[5].add(binding.blank502.id);hoursId[5].add(binding.blank503.id);hoursId[5].add(binding.blank504.id)
        hoursId[5].add(binding.blank505.id);hoursId[5].add(binding.blank506.id);hoursId[5].add(binding.blank507.id);hoursId[5].add(binding.blank508.id);hoursId[5].add(binding.blank509.id)
        hoursId[5].add(binding.blank510.id);hoursId[5].add(binding.blank511.id);hoursId[5].add(binding.blank512.id);hoursId[5].add(binding.blank513.id);hoursId[5].add(binding.blank514.id)
        hoursId[5].add(binding.blank515.id);hoursId[5].add(binding.blank516.id);hoursId[5].add(binding.blank517.id);hoursId[5].add(binding.blank518.id);hoursId[5].add(binding.blank519.id)
        hoursId[5].add(binding.blank520.id);hoursId[5].add(binding.blank521.id);hoursId[5].add(binding.blank522.id);hoursId[5].add(binding.blank523.id)
        hoursId[6].add(binding.blank600.id);hoursId[6].add(binding.blank601.id);hoursId[6].add(binding.blank602.id);hoursId[6].add(binding.blank603.id);hoursId[6].add(binding.blank604.id)
        hoursId[6].add(binding.blank605.id);hoursId[6].add(binding.blank606.id);hoursId[6].add(binding.blank607.id);hoursId[6].add(binding.blank608.id);hoursId[6].add(binding.blank609.id)
        hoursId[6].add(binding.blank610.id);hoursId[6].add(binding.blank611.id);hoursId[6].add(binding.blank612.id);hoursId[6].add(binding.blank613.id);hoursId[6].add(binding.blank614.id)
        hoursId[6].add(binding.blank615.id);hoursId[6].add(binding.blank616.id);hoursId[6].add(binding.blank617.id);hoursId[6].add(binding.blank618.id);hoursId[6].add(binding.blank619.id)
        hoursId[6].add(binding.blank620.id);hoursId[6].add(binding.blank621.id);hoursId[6].add(binding.blank622.id);hoursId[6].add(binding.blank623.id)

    }
}