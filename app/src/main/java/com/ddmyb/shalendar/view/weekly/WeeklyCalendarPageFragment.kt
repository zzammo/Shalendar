package com.ddmyb.shalendar.view.weekly

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.get
import androidx.fragment.app.Fragment
import com.ddmyb.shalendar.R
import com.ddmyb.shalendar.data.Schedule
import com.ddmyb.shalendar.databinding.FragmentWeeklyCalendarPageBinding
import com.ddmyb.shalendar.view.schedules.ScheduleActivity
import com.ddmyb.shalendar.view.weekly.data.WeeklyDates
import java.util.Calendar

@RequiresApi(Build.VERSION_CODES.O)
class WeeklyCalendarPageFragment(private val now: Long): Fragment() {

    val TAG = "WeGlonD"
    private lateinit var binding: FragmentWeeklyCalendarPageBinding
    val scheduleContainers = arrayListOf<ConstraintLayout>()
    val hours = arrayListOf<ArrayList<TextView>>()
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

        createViewMap()

        binding.root.viewTreeObserver.addOnWindowFocusChangeListener { hasFocus ->
            pixel_1minute = binding.clPlanSunday.height / 1440f
            Log.d(TAG, "ConstlaintLayout height: ${binding.clPlanSunday.height}")
            Log.d(TAG, "pixel 1minute: $pixel_1minute")
            onResume()
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        clearScheduleViews()

        displaySchedules(weeklyDates)
    }

    private fun clearScheduleViews() {
        viewToScheduleMap.clear()
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

        //이번주에 표시할 것이 아니면 리턴
        if(endCal.get(Calendar.DAY_OF_MONTH) < weeklyDates.daynums[0] || weeklyDates.daynums[6] < startCal.get(Calendar.DAY_OF_MONTH))
            return;

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

//        scheduleView.findViewById<TextView>(R.id.schedule_name).text = schedule.name

        scheduleContainers[dayOfWeek].addView(scheduleView)

        val layoutParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_CONSTRAINT)
        layoutParams.topToTop = hours[dayOfWeek][startCal.get(Calendar.HOUR_OF_DAY)].id
        layoutParams.bottomToBottom = hours[dayOfWeek][endCal.get(Calendar.HOUR_OF_DAY)].id
        layoutParams.topMargin = (pixel_1minute * startCal.get(Calendar.MINUTE)).toInt()
        layoutParams.bottomMargin = (pixel_1minute * (60 - endCal.get(Calendar.MINUTE))).toInt()

        scheduleView.layoutParams = layoutParams

        scheduleContainers[dayOfWeek].invalidate()

        Log.d(TAG, "schedule x: ${scheduleView.x}")
        Log.d(TAG, "schedule y: ${scheduleView.y}")

        if (flag) {
            startCal.add(Calendar.DATE, 1)
            startCal.set(Calendar.HOUR_OF_DAY, 0)
            startCal.set(Calendar.MINUTE, 0)
            startCal.set(Calendar.SECOND, 0)
            startCal.set(Calendar.MILLISECOND, 0)
            displaySchedule(schedule, startCal.timeInMillis)
        }
    }

    fun blankOnLongClick(dayOfWeek: Int, hour: Int) {
        val intent = Intent(this.requireContext(), ScheduleActivity::class.java)
        val cal = Calendar.getInstance()
        cal.timeInMillis = now
        cal.add(Calendar.DATE, dayOfWeek)

//        val startLocalDateTime = LocalDateTime.of(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH)+1, cal.get(Calendar.DATE), hour, 0, 0)
//        intent.putExtra("StartDateTimeDto", NewScheduleDto("", startLocalDateTime))
        startActivity(intent)
    }

    fun blankOnClick(dayOfWeek: Int, hour: Int) {
        val listOfSchedule = arrayListOf<Schedule>()
        val blankStartCal = Calendar.getInstance()
        blankStartCal.timeInMillis = now
        blankStartCal.add(Calendar.DAY_OF_MONTH, dayOfWeek)
        blankStartCal.set(Calendar.HOUR_OF_DAY, hour)
        blankStartCal.set(Calendar.MINUTE, 0)
        val blankEndCal = Calendar.getInstance()
        blankEndCal.timeInMillis = now
        blankEndCal.add(Calendar.DAY_OF_MONTH, dayOfWeek)
        blankEndCal.set(Calendar.HOUR_OF_DAY, hour)
        blankEndCal.set(Calendar.MINUTE, 59)

        for (i in 24 until scheduleContainers[dayOfWeek].childCount) {
            val schedule = viewToScheduleMap.get(scheduleContainers[dayOfWeek].get(i).id) as Schedule

            if ((blankStartCal.timeInMillis <= schedule.endTime && schedule.startTime < blankEndCal.timeInMillis)
                || (blankStartCal.timeInMillis <= schedule.startTime && schedule.endTime <= blankEndCal.timeInMillis)) {
                listOfSchedule.add(schedule)
            }
        }

        for (s in listOfSchedule) {
            Log.d(TAG,"schedule: ${s.name}")
        }

        if (!listOfSchedule.isEmpty()) {
//            .openSlidingUpPanel(blankStartCal, listOfSchedule)
            (parentFragmentManager.findFragmentByTag("week") as WeeklyCalendarFragment).openSlidingUpPanel(blankStartCal, listOfSchedule)
        }
    }

    private fun createViewMap() {
        scheduleContainers.clear()
        scheduleContainers.add(binding.clPlanSunday);scheduleContainers.add(binding.clPlanMonday)
        scheduleContainers.add(binding.clPlanTuesday);scheduleContainers.add(binding.clPlanWednesday)
        scheduleContainers.add(binding.clPlanThursday);scheduleContainers.add(binding.clPlanFriday);scheduleContainers.add(binding.clPlanSaturday)

        hours.clear()
        for (i in 1..7)
            hours.add(arrayListOf())

        hours[0].add(binding.blank000);hours[0].add(binding.blank001);hours[0].add(binding.blank002);hours[0].add(binding.blank003);hours[0].add(binding.blank004)
        hours[0].add(binding.blank005);hours[0].add(binding.blank006);hours[0].add(binding.blank007);hours[0].add(binding.blank008);hours[0].add(binding.blank009)
        hours[0].add(binding.blank010);hours[0].add(binding.blank011);hours[0].add(binding.blank012);hours[0].add(binding.blank013);hours[0].add(binding.blank014)
        hours[0].add(binding.blank015);hours[0].add(binding.blank016);hours[0].add(binding.blank017);hours[0].add(binding.blank018);hours[0].add(binding.blank019)
        hours[0].add(binding.blank020);hours[0].add(binding.blank021);hours[0].add(binding.blank022);hours[0].add(binding.blank023)
        hours[1].add(binding.blank100);hours[1].add(binding.blank101);hours[1].add(binding.blank102);hours[1].add(binding.blank103);hours[1].add(binding.blank104)
        hours[1].add(binding.blank105);hours[1].add(binding.blank106);hours[1].add(binding.blank107);hours[1].add(binding.blank108);hours[1].add(binding.blank109)
        hours[1].add(binding.blank110);hours[1].add(binding.blank111);hours[1].add(binding.blank112);hours[1].add(binding.blank113);hours[1].add(binding.blank114)
        hours[1].add(binding.blank115);hours[1].add(binding.blank116);hours[1].add(binding.blank117);hours[1].add(binding.blank118);hours[1].add(binding.blank119)
        hours[1].add(binding.blank120);hours[1].add(binding.blank121);hours[1].add(binding.blank122);hours[1].add(binding.blank123)
        hours[2].add(binding.blank200);hours[2].add(binding.blank201);hours[2].add(binding.blank202);hours[2].add(binding.blank203);hours[2].add(binding.blank204)
        hours[2].add(binding.blank205);hours[2].add(binding.blank206);hours[2].add(binding.blank207);hours[2].add(binding.blank208);hours[2].add(binding.blank209)
        hours[2].add(binding.blank210);hours[2].add(binding.blank211);hours[2].add(binding.blank212);hours[2].add(binding.blank213);hours[2].add(binding.blank214)
        hours[2].add(binding.blank215);hours[2].add(binding.blank216);hours[2].add(binding.blank217);hours[2].add(binding.blank218);hours[2].add(binding.blank219)
        hours[2].add(binding.blank220);hours[2].add(binding.blank221);hours[2].add(binding.blank222);hours[2].add(binding.blank223)
        hours[3].add(binding.blank300);hours[3].add(binding.blank301);hours[3].add(binding.blank302);hours[3].add(binding.blank303);hours[3].add(binding.blank304)
        hours[3].add(binding.blank305);hours[3].add(binding.blank306);hours[3].add(binding.blank307);hours[3].add(binding.blank308);hours[3].add(binding.blank309)
        hours[3].add(binding.blank310);hours[3].add(binding.blank311);hours[3].add(binding.blank312);hours[3].add(binding.blank313);hours[3].add(binding.blank314)
        hours[3].add(binding.blank315);hours[3].add(binding.blank316);hours[3].add(binding.blank317);hours[3].add(binding.blank318);hours[3].add(binding.blank319)
        hours[3].add(binding.blank320);hours[3].add(binding.blank321);hours[3].add(binding.blank322);hours[3].add(binding.blank323)
        hours[4].add(binding.blank400);hours[4].add(binding.blank401);hours[4].add(binding.blank402);hours[4].add(binding.blank403);hours[4].add(binding.blank404)
        hours[4].add(binding.blank405);hours[4].add(binding.blank406);hours[4].add(binding.blank407);hours[4].add(binding.blank408);hours[4].add(binding.blank409)
        hours[4].add(binding.blank410);hours[4].add(binding.blank411);hours[4].add(binding.blank412);hours[4].add(binding.blank413);hours[4].add(binding.blank414)
        hours[4].add(binding.blank415);hours[4].add(binding.blank416);hours[4].add(binding.blank417);hours[4].add(binding.blank418);hours[4].add(binding.blank419)
        hours[4].add(binding.blank420);hours[4].add(binding.blank421);hours[4].add(binding.blank422);hours[4].add(binding.blank423)
        hours[5].add(binding.blank500);hours[5].add(binding.blank501);hours[5].add(binding.blank502);hours[5].add(binding.blank503);hours[5].add(binding.blank504)
        hours[5].add(binding.blank505);hours[5].add(binding.blank506);hours[5].add(binding.blank507);hours[5].add(binding.blank508);hours[5].add(binding.blank509)
        hours[5].add(binding.blank510);hours[5].add(binding.blank511);hours[5].add(binding.blank512);hours[5].add(binding.blank513);hours[5].add(binding.blank514)
        hours[5].add(binding.blank515);hours[5].add(binding.blank516);hours[5].add(binding.blank517);hours[5].add(binding.blank518);hours[5].add(binding.blank519)
        hours[5].add(binding.blank520);hours[5].add(binding.blank521);hours[5].add(binding.blank522);hours[5].add(binding.blank523)
        hours[6].add(binding.blank600);hours[6].add(binding.blank601);hours[6].add(binding.blank602);hours[6].add(binding.blank603);hours[6].add(binding.blank604)
        hours[6].add(binding.blank605);hours[6].add(binding.blank606);hours[6].add(binding.blank607);hours[6].add(binding.blank608);hours[6].add(binding.blank609)
        hours[6].add(binding.blank610);hours[6].add(binding.blank611);hours[6].add(binding.blank612);hours[6].add(binding.blank613);hours[6].add(binding.blank614)
        hours[6].add(binding.blank615);hours[6].add(binding.blank616);hours[6].add(binding.blank617);hours[6].add(binding.blank618);hours[6].add(binding.blank619)
        hours[6].add(binding.blank620);hours[6].add(binding.blank621);hours[6].add(binding.blank622);hours[6].add(binding.blank623)

        for (i in 0..6) {
            for (j in 0..23) {
                hours[i][j].setOnLongClickListener {
                    blankOnLongClick(i, j)
                    true
                }
                hours[i][j].setOnClickListener {
                    blankOnClick(i, j)
                }
            }
        }

    }
}