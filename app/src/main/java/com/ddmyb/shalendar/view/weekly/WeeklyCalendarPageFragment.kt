package com.ddmyb.shalendar.view.weekly

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.get
import androidx.core.view.setMargins
import androidx.core.view.setPadding
import androidx.fragment.app.Fragment
import com.ddmyb.shalendar.R
import com.ddmyb.shalendar.databinding.FragmentWeeklyCalendarPageBinding
import com.ddmyb.shalendar.domain.ScheduleDto
import com.ddmyb.shalendar.util.HttpResult
import com.ddmyb.shalendar.util.NewScheduleDto
import com.ddmyb.shalendar.view.holiday.HolidayApi
import com.ddmyb.shalendar.view.holiday.data.HolidayDTO
import com.ddmyb.shalendar.view.home.CalendarFragment
import com.ddmyb.shalendar.view.schedules.ScheduleActivity
import com.ddmyb.shalendar.view.weekly.data.WeeklyDates
import java.util.Calendar


class WeeklyCalendarPageFragment(private val now: Long): Fragment() {
    val TAG = "WeGlonD"
    private val calendarHostTag = "CalendarHostFragment"
    private lateinit var context: Context
    private lateinit var binding: FragmentWeeklyCalendarPageBinding
    private lateinit var drawable_unselect: Drawable
    private lateinit var drawable_onselect: Drawable
    var selected_hour: View? = null
    var weeknum = 0
    val scheduleContainers = arrayListOf<ConstraintLayout>()
    val hours = arrayListOf<ArrayList<View>>()
    val viewToScheduleMap = HashMap<Int, ScheduleDto>() // HashMap<Int,Int> 변경 후 companion으로 Schedule ArrayList를 넣어놓을까..
    val weekCalList = ArrayList<Calendar>()
    val weeknumViews = ArrayList<TextView>()
    val weeknumLayouts = ArrayList<LinearLayout>()
    private lateinit var holidayDrawable: GradientDrawable
    private lateinit var holidayLayoutParams: LinearLayout.LayoutParams

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
        (parentFragmentManager.findFragmentByTag(calendarHostTag) as CalendarFragment).selectedDateCalendar = cal

        drawable_unselect = ContextCompat.getDrawable(context, R.drawable.weekly_boundry)!!
        drawable_onselect = ContextCompat.getDrawable(context, R.drawable.weekly_hour_selector)!!

        weeknum = cal.get(Calendar.WEEK_OF_YEAR)
        weeklyDates = WeeklyDates(cal.get(Calendar.MONTH)+1, getWeekNums(cal), weeknum)
        binding.data = weeklyDates

        createViewMap()
        holiday_setting()

        binding.root.viewTreeObserver.addOnGlobalLayoutListener(
            object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    pixel_1minute = binding.clPlanSunday.height / 1440f
                    Log.d(TAG, "ConstlaintLayout height: ${binding.clPlanSunday.height}")
                    Log.d(TAG, "pixel 1minute: $pixel_1minute")
                    onResume()
                    // 1회성을 위해 Listener 제거
                    binding.root.viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
            }
        )

        return binding.root
    }

    inner class HttpResponseImpl: HttpResult<List<HolidayDTO.HolidayItem>> {
        override fun success(data: List<HolidayDTO.HolidayItem>) {
            for (holidayItem in data) {
                Log.d(TAG, "${holidayItem.locdate} ${holidayItem.dateName}")
                val holidayCal = weekCalList[0].clone() as Calendar
                holidayCal.set(Calendar.YEAR, holidayItem.locdate/10000)
                holidayCal.set(Calendar.MONTH, (holidayItem.locdate/100)%100 - 1)
                holidayCal.set(Calendar.DATE, holidayItem.locdate%100)

                val weekdaynum = isThisWeek(holidayCal)
                if (weekdaynum != null) {
                    weeknumViews[weekdaynum].setTextColor(ContextCompat.getColor(context, R.color.red))
                    // 동적으로 TextView 만들고 weeknumLayouts[weekdaynum]에 넣기
                    val textView = TextView(context)
                    textView.text = holidayItem.dateName
                    textView.layoutParams = holidayLayoutParams
                    textView.background = holidayDrawable
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10F)
                    textView.setPadding(5)
                    weeknumLayouts[weekdaynum].addView(textView)
                }
            }
        }

        override fun appFail() { Log.d(TAG, "appFail") }

        override fun fail(throwable: Throwable) { Log.d(TAG, "fail") }

        override fun finally() { Log.d(TAG, "finally") }

    }

    private fun holiday_setting() {
        if (weekCalList[0].get(Calendar.YEAR) == weekCalList[6].get(Calendar.YEAR) && weekCalList[0].get(Calendar.MONTH) == weekCalList[6].get(Calendar.MONTH)) {
            HolidayApi.getHolidays(weekCalList[0].get(Calendar.YEAR), weekCalList[0].get(Calendar.MONTH)+1, HttpResponseImpl())
        }
        else {
            HolidayApi.getHolidays(weekCalList[0].get(Calendar.YEAR), weekCalList[0].get(Calendar.MONTH)+1, HttpResponseImpl())
            HolidayApi.getHolidays(weekCalList[6].get(Calendar.YEAR), weekCalList[6].get(Calendar.MONTH)+1, HttpResponseImpl())
        }
    }

    private fun isThisWeek(cal: Calendar): Int? {
        if ((cal.after(weekCalList[0]) || cal.equals(weekCalList[0])) && (cal.before(weekCalList[6]) || cal.equals(weekCalList[6]))) {
            for (i in 0..6) {
                if (cal.equals(weekCalList[i]))
                    return i
            }
        }
        return null
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.context = context
    }

    override fun onResume() {
        super.onResume()
        val cal = Calendar.getInstance()
        cal.timeInMillis = now
        (parentFragmentManager.findFragmentByTag(calendarHostTag) as CalendarFragment).selectedDateCalendar = cal
    }

    override fun onPause() {
        super.onPause()

        if (selected_hour != null) {
            selected_hour!!.background = drawable_unselect
            selected_hour = null
        }
        val cal = Calendar.getInstance()
        cal.timeInMillis = now
        (parentFragmentManager.findFragmentByTag(calendarHostTag) as CalendarFragment).selectedDateCalendar = cal
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
            weekCalList.add(cal.clone() as Calendar)
            cal.add(Calendar.DAY_OF_MONTH, 1)
        }

        return result
    }

    fun displaySchedules(weeklyDates: WeeklyDates) {
        // 테스트용 코드
        val cal1 = Calendar.getInstance()
        cal1.set(Calendar.MONTH, 11)
        cal1.set(Calendar.DATE,5)
        cal1.set(Calendar.HOUR_OF_DAY, 10)
        cal1.set(Calendar.MINUTE, 30)
        val cal2 = Calendar.getInstance()
        cal2.set(Calendar.MONTH, 11)
        cal2.set(Calendar.DATE,5)
        cal2.set(Calendar.HOUR_OF_DAY, 14)
        cal2.set(Calendar.MINUTE, 10)

        val cal3 = Calendar.getInstance()
        cal3.set(Calendar.HOUR_OF_DAY, 11)
        cal3.set(Calendar.MINUTE, 30)
        val cal4 = Calendar.getInstance()
        cal4.set(Calendar.HOUR_OF_DAY, 15)
        cal4.set(Calendar.MINUTE, 10)

        val scheduleDto1 = ScheduleDto()
        scheduleDto1.title="test";scheduleDto1.startMills=cal1.timeInMillis;scheduleDto1.endMills=cal2.timeInMillis
        val scheduleDto2 = ScheduleDto()
        scheduleDto2.title="text";scheduleDto2.startMills=cal3.timeInMillis;scheduleDto2.endMills=cal4.timeInMillis
        displaySchedule(scheduleDto1, scheduleDto1.startMills)
        displaySchedule(scheduleDto2, scheduleDto2.startMills)

        //db 구현 이후 할 일: 반복문 돌면서 일주일 간 각 날짜마다 띄워야할 schedule을 db에서 들고와서 띄우기 (now 변수 활용)
        //기간 쿼리 가능하면 그걸로 들고오자..
    }

    private fun displaySchedule(schedule: ScheduleDto, startMillis:Long) {
        Log.d(TAG, "displaySchedule Start")
        val zeroCal = Calendar.getInstance()
        zeroCal.timeInMillis = startMillis
        zeroCal.set(Calendar.HOUR_OF_DAY, 0)
        zeroCal.set(Calendar.MINUTE, 0)
        zeroCal.set(Calendar.SECOND, 0)
        zeroCal.set(Calendar.MILLISECOND, 0)

        val startCal = Calendar.getInstance()
        startCal.timeInMillis = startMillis
        val endCal = Calendar.getInstance()
        endCal.timeInMillis = schedule.endMills

        //이번주에 표시할 것이 아니면 리턴
        if(endCal.get(Calendar.WEEK_OF_YEAR) != weeknum || weeknum != startCal.get(Calendar.WEEK_OF_YEAR)) {
            Log.d(TAG, "this schedule is not this week")
            Log.d(TAG, "endCal: ${endCal.get(Calendar.MONTH)+1}.${endCal.get(Calendar.DATE)}, sunday: ${weeklyDates.daynums[0]}, saturday: ${weeklyDates.daynums[6]}")
            return;
        }
        Log.d(TAG, "this schedule is this week")

        var flag = false

        if(startCal.get(Calendar.DAY_OF_MONTH) != endCal.get(Calendar.DAY_OF_MONTH)){
            flag = true
            endCal.set(Calendar.DAY_OF_MONTH, startCal.get(Calendar.DAY_OF_MONTH))
            endCal.set(Calendar.HOUR_OF_DAY, 23)
            endCal.set(Calendar.MINUTE, 59)
        }


        val dayOfWeek = startCal.get(Calendar.DAY_OF_WEEK) - 1
        val scheduleView = LayoutInflater.from(context)
            .inflate(R.layout.custom_view_weekly_schedule, null)
        Log.d(TAG, "custom view created")

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
        Log.d(TAG, "custom view displayed")

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

    fun blankOnClick(dayOfWeek: Int, hour: Int) {
        val listOfSchedule = arrayListOf<ScheduleDto>()
        val blankStartCal = weekCalList[dayOfWeek].clone() as Calendar
        blankStartCal.set(Calendar.HOUR_OF_DAY, hour)
        blankStartCal.set(Calendar.MINUTE, 0)
        blankStartCal.set(Calendar.SECOND, 0)
        blankStartCal.set(Calendar.MILLISECOND, 0)
        val blankEndCal = blankStartCal.clone() as Calendar
        blankEndCal.set(Calendar.HOUR_OF_DAY, hour)
        blankEndCal.set(Calendar.MINUTE, 59)
        blankStartCal.set(Calendar.SECOND, 59)
        blankStartCal.set(Calendar.MILLISECOND, 999)

        for (i in 24 until scheduleContainers[dayOfWeek].childCount) {
            val schedule = viewToScheduleMap.get(scheduleContainers[dayOfWeek].get(i).id) as ScheduleDto

            if ((blankStartCal.timeInMillis <= schedule.endMills && schedule.startMills < blankEndCal.timeInMillis)
                || (blankStartCal.timeInMillis <= schedule.startMills && schedule.endMills <= blankEndCal.timeInMillis)) {
                listOfSchedule.add(schedule)
            }
        }

        for (s in listOfSchedule) {
            Log.d(TAG,"schedule: ${s.title}")
        }

        if (!listOfSchedule.isEmpty()) {
//            .openSlidingUpPanel(blankStartCal, listOfSchedule)
            (parentFragmentManager.findFragmentByTag(calendarHostTag) as CalendarFragment).openSlidingUpPanel(blankStartCal, listOfSchedule)
        }
        else {
            if (selected_hour != null) {
                if (selected_hour == hours[dayOfWeek][hour]) {
                    // 인텐트, 액티비티 이동 ScheduleActivity
                    val intent = Intent(context, ScheduleActivity::class.java)
                    intent.putExtra("NewSchedule", NewScheduleDto("", blankStartCal.timeInMillis))
                    startActivity(intent)
                }
                else {
                    selected_hour!!.background = drawable_unselect
                    selected_hour = hours[dayOfWeek][hour]
                    selected_hour!!.background = drawable_onselect
                    (parentFragmentManager.findFragmentByTag(calendarHostTag) as CalendarFragment).selectedDateCalendar = blankStartCal
                }
            }
            else {
                selected_hour = hours[dayOfWeek][hour]
                selected_hour!!.background = drawable_onselect
                (parentFragmentManager.findFragmentByTag(calendarHostTag) as CalendarFragment).selectedDateCalendar = blankStartCal
            }
        }
    }

    private fun createViewMap() {
        holidayDrawable = ContextCompat.getDrawable(context, R.drawable.weekly_holiday) as GradientDrawable
        holidayDrawable.setColor(ContextCompat.getColor(context,R.color.red__33Alpha))
        holidayLayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        holidayLayoutParams.setMargins(1)
        holidayLayoutParams.gravity = Gravity.CENTER

        weeknumViews.clear()
        weeknumViews.add(binding.tvDaynumSunday); weeknumViews.add(binding.tvDaynumMonday); weeknumViews.add(binding.tvDaynumTuesday); weeknumViews.add(binding.tvDaynumWednesday)
        weeknumViews.add(binding.tvDaynumThursday); weeknumViews.add(binding.tvDaynumFriday); weeknumViews.add(binding.tvDaynumSaturday)

        weeknumLayouts.clear()
        weeknumLayouts.add(binding.llDayconSunday); weeknumLayouts.add(binding.llDayconMonday); weeknumLayouts.add(binding.llDayconTuesday); weeknumLayouts.add(binding.llDayconWednesday)
        weeknumLayouts.add(binding.llDayconThursday); weeknumLayouts.add(binding.llDayconFriday); weeknumLayouts.add(binding.llDayconSaturday)

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
                hours[i][j].setOnClickListener {
                    blankOnClick(i, j)
                }
            }
        }

    }
}