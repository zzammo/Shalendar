package com.ddmyb.shalendar.view.schedules

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.CompoundButton
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.ddmyb.shalendar.R
import com.ddmyb.shalendar.databinding.ActivitySchedulesBinding
import com.ddmyb.shalendar.view.Address.AddressActivity
import com.ddmyb.shalendar.view.schedules.adapter.NetworkStatusService
import com.ddmyb.shalendar.view.schedules.utils.Permission
import com.ddmyb.shalendar.view.schedules.utils.Permission.Companion.REQUIRED_PERMISSIONS
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import java.net.URLEncoder

private lateinit var binding: ActivitySchedulesBinding
class ScheduleActivity(
    ) : AppCompatActivity(), SchedulesContract.View, OnMapReadyCallback{

//    private val scheduleId: String = intent.getStringExtra("id")!!

    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private var locationRequest: LocationRequest? = null

    private val presenter: SchedulesContract.Presenter = SchedulePresenter(this, "scheduleId")

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_schedules)
        binding = ActivitySchedulesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.google_map_fragment) as SupportMapFragment
        Log.d("mapFragment", mapFragment.toString())
        val callBack = mapFragment.getMapAsync(this)
        Log.d("mapFragment", callBack.toString())


        initTimeSelectionListener()
        initDateTimePicker()
        initSwitchListener()
        initAlarmTimeListener()
        initIteratorListener()
        initExpectedTimeListener()
        initSaveCancelListener()

//        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
//        locationRequest = LocationRequest.create()
//            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
//            .setInterval(10000)
//            .setFastestInterval(5000)
//        val builder = LocationSettingsRequest.Builder()
//        builder.addLocationRequest(locationRequest)

    }

    fun layoutGone(flag: Int) {
        if (flag == 1){
            binding.dateStartLayout.visibility = View.GONE
//            setOffClicked(0)
        }else if(flag == 2){
            binding.dateEndLayout.visibility = View.GONE
//            setOffClicked(2)
        }
    }

    /**
//    var DatePickerFlag: Int = 0
//    var TimePickerFlag: Int = 0
//    val clicked = BooleanArray(4) { false }
//
//    fun initTimeSelectionListener() {
//        binding.startTimeTextview.setOnClickListener(View.OnClickListener {
//
//            when (DatePickerFlag) {
//                1 -> {
//                    binding.dateStartLayout.visibility = View.GONE
//                    setClicked(0, false)
//                }
//                2 -> {
//                    binding.dateEndLayout.visibility = View.GONE
//                    setClicked(2, false)
//                }
//            }
//            DatePickerFlag = 0
//
//            when (TimePickerFlag) {
//                0 -> {
//                    binding.timeStartLayout.visibility = View.VISIBLE
//                    setClicked(1, true)
//                    TimePickerFlag = 1
//                }
//                1 -> {
//                    binding.timeStartLayout.visibility = View.GONE
//                    setClicked(1, false)
//                    TimePickerFlag = 0
//                }
//                2 -> {
//                    binding.timeEndLayout.visibility = View.GONE
//                    setClicked(3, false)
//                    binding.timeStartLayout.visibility = View.VISIBLE
//                    setClicked(1, true)
//                    TimePickerFlag = 1
//                }
//            }
//        })
//
//        binding.endTimeTextview.setOnClickListener(View.OnClickListener {
//            when (DatePickerFlag) {
//                1 -> {
//                    binding.dateStartLayout.visibility = View.GONE
//                    setClicked(0, false)
//                }
//                2 -> {
//                    binding.dateEndLayout.visibility = View.GONE
//                    setClicked(2, false)
//                }
//            }
//            DatePickerFlag = 0
//
//            when (TimePickerFlag) {
//                0 -> {
//                    binding.timeStartLayout.visibility = View.VISIBLE
//                    setClicked(3, true)
//                    TimePickerFlag = 2
//                }
//                1 -> {
//                    binding.timeEndLayout.visibility = View.GONE
//                    setClicked(1, false)
//                    binding.timeStartLayout.visibility = View.GONE
//                    setClicked(3, true)
//                    TimePickerFlag = 2
//                }
//                2 -> {
//                    binding.timeStartLayout.visibility = View.VISIBLE
//                    setClicked(3, false)
//                    TimePickerFlag = 0
//                }
//            }
//        })
//
//        binding.startDateTextview.setOnClickListener(View.OnClickListener {
//            when (DatePickerFlag) {
//                0 -> {
//                    binding.dateStartLayout.visibility = View.VISIBLE
//                    setClicked(0, true)
//                    DatePickerFlag = 1
//                }
//                1 -> {
//                    binding.dateStartLayout.visibility = View.GONE
//                    setClicked(0, false)
//                    DatePickerFlag = 0
//                }
//                2 -> {
//                    binding.dateEndLayout.visibility = View.GONE
//                    setClicked(2, false)
//                    binding.dateStartLayout.visibility = View.VISIBLE
//                    setClicked(0, true)
//                    DatePickerFlag = 1
//                }
//            }
//
//            when (TimePickerFlag) {
//                1 -> {
//                    binding.timeStartLayout.visibility = View.GONE
//                    setClicked(1, false)
//                }
//                2 -> {
//                    binding.timeEndLayout.visibility = View.GONE
//                    setClicked(3, false)
//                }
//            }
//            TimePickerFlag = 0
//        })
//
//        binding.endDateTextview.setOnClickListener(View.OnClickListener {
//            when (DatePickerFlag) {
//                0 -> {
//                    binding.dateStartLayout.visibility = View.VISIBLE
//                    setClicked(2, true)
//                    DatePickerFlag = 2
//                }
//                1 -> {
//                    binding.dateStartLayout.visibility = View.GONE
//                    setClicked(0, false)
//                    binding.dateEndLayout.visibility = View.VISIBLE
//                    setClicked(2, true)
//                    DatePickerFlag = 2
//                }
//                2 -> {
//                    binding.dateStartLayout.visibility = View.GONE
//                    setClicked(2, false)
//                    DatePickerFlag = 0
//                }
//            }
//
//            when (TimePickerFlag) {
//                1 -> {
//                    binding.timeStartLayout.visibility = View.GONE
//                    setClicked(1, false)
//                }
//                2 -> {
//                    binding.timeEndLayout.visibility = View.GONE
//                    setClicked(3, false)
//                }
//            }
//            TimePickerFlag = 0
//        })
//    }
**/

    private var DatePickerFlag: Int = 0
    private var TimePickerFlag: Int = 0
    val clicked = BooleanArray(4) { false }

    private fun initTimeSelectionListener() {
        binding.startTimeTextview.setOnClickListener {
            handleDateAndTimeClick(1, 2, 0, 1)
        }

        binding.endTimeTextview.setOnClickListener {
            handleDateAndTimeClick(1, 2, 3, 2)
        }

        binding.startDateTextview.setOnClickListener {
            handleDateAndTimeClick(0, 2, 1, 0)
        }

        binding.endDateTextview.setOnClickListener {
            handleDateAndTimeClick(2, 2, 2, 0)
        }
    }
    private fun handleDateAndTimeClick(dateStartVisibility: Int, dateEndVisibility: Int, timeVisibility: Int, newDatePickerFlag: Int) {
        if (DatePickerFlag == 1) {
            binding.dateStartLayout.visibility = View.GONE
            setClicked(0, false)
        } else if (DatePickerFlag == 2) {
            binding.dateEndLayout.visibility = View.GONE
            setClicked(2, false)
        }

        DatePickerFlag = newDatePickerFlag

        if (TimePickerFlag == 0) {
            binding.timeStartLayout.visibility = View.VISIBLE
            setClicked(1, true)
            TimePickerFlag = 1
        } else if (TimePickerFlag == 1) {
            binding.timeStartLayout.visibility = View.GONE
            setClicked(1, false)
            TimePickerFlag = 0
        } else if (TimePickerFlag == 2) {
            binding.timeEndLayout.visibility = View.GONE
            setClicked(3, false)
            binding.timeStartLayout.visibility = View.VISIBLE
            setClicked(1, true)
            TimePickerFlag = 1
        }

        binding.dateStartLayout.visibility = if (DatePickerFlag == 0) View.VISIBLE else View.GONE
        binding.dateEndLayout.visibility = if (DatePickerFlag == 2) View.VISIBLE else View.GONE
        binding.timeEndLayout.visibility = if (TimePickerFlag == 2) View.VISIBLE else View.GONE
        setClicked(dateStartVisibility, true)
        setClicked(dateEndVisibility, true)
        setClicked(timeVisibility, true)
    }
    private fun setClicked(i: Int, isClicked: Boolean) {
        clicked[i] = isClicked
        val textView = when (i) {
            0 -> binding.startDateTextview
            1 -> binding.startTimeTextview
            2 -> binding.endDateTextview
            3 -> binding.endTimeTextview
            else -> null
        }

        textView?.background = if (isClicked) {
            ContextCompat.getDrawable(this, R.drawable.ed_text)
        } else {
            ContextCompat.getDrawable(this, R.color.bg_white)
        }
    }

    override fun showEndTimeText(endHour: Int, endMinute: Int) {
        binding.endTimeTextview.text = getTimeText(endHour, endMinute)
    }
    override fun showStartTimeText(startHour: Int, startMinute: Int) {
        binding.startTimeTextview.text = getTimeText(startHour, startMinute)
    }
    override fun showStartDateText(startMonth: Int, startDay: Int, startWeek: Int) {
       binding.startDateTextview.text = getDateText(startMonth, startDay, startWeek)
    }
    override fun showEndDateText(endMonth: Int, endDay: Int, endWeek: Int) {
        binding.endDateTextview.text = getDateText(endMonth, endDay, endWeek)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun initDateTimePicker(){
        binding.timeStartTimepicker.setOnTimeChangedListener{ _, startHour, startMinute ->
            presenter.setStartTime(startHour, startMinute)
        }
        binding.timeEndTimepicker.setOnTimeChangedListener { _, endHour, endMinute ->
            presenter.setEndTime(endHour, endMinute)
        }
        binding.dateStartDatepicker.setOnDateChangedListener { _, startYear, startMonth, startDay ->
            presenter.setStartDate(startYear, startMonth, startDay)
        }
        binding.dateEndDatepicker.setOnDateChangedListener { _, endYear, endMonth, endDay ->
            presenter.setEndDate(endYear, endMonth, endDay)
        }
    }
    private fun getDateText(month: Int, day: Int, dayOfWeek: Int): String {
        val week = when (dayOfWeek) {
            1 -> "일"
            2 -> "월"
            3 -> "화"
            4 -> "수"
            5 -> "목"
            6 -> "금"
            7 -> "토"
            else -> ""
        }
        return "($month)월 ($day)일 ($week)"
    }
    private fun getTimeText(hour: Int, minute: Int): String? {
        var ret = ""
        ret += if (hour < 12) {
            "오전 $hour:"
        } else {
            "오후 " + (hour - 12).toString() + ":"
        }
        ret += if (minute < 10) {
            "0$minute"
        } else {
            minute.toString()
        }
        return ret
    }
    private fun getCustomText(`val`: Int, index: Int): String? {
        var ret = ""
        ret += `val`.toString()
        when (index) {
            0 -> ret += "분"
            1 -> ret += "시간"
            2 -> ret += "일"
            3 -> ret += "주"
        }
        return ret
    }

    private val maxValues = intArrayOf(360, 99, 365, 52)
    private fun setNumPicker(index: Int) {
        binding.numpicker.maxValue = maxValues.getOrElse(index) { 1 }
        binding.numpicker.minValue = 1
    }

    private fun initSwitchListener(){
        /**
         * 생성 일정이 하루 종일 발생 하는지 여부를 선택
         */
        binding.alldaySwitch.setOnCheckedChangeListener{ _, isChecked ->
            if (isChecked) {
                binding.startTimeTextview.visibility = View.GONE
                binding.endTimeTextview.visibility = View.GONE
                binding.dateEndLayout.visibility = View.GONE
                binding.dateStartLayout.visibility = View.GONE
                binding.timeStartLayout.visibility = View.GONE
                binding.timeEndLayout.visibility = View.GONE
                TimePickerFlag = 0
                DatePickerFlag = 0
            } else {
                binding.startTimeTextview.visibility = View.VISIBLE
                binding.endTimeTextview.visibility = View.VISIBLE
            }
        }
        /**
         * 알람을 할지 안할지 여부를 선택
         */
        binding.alarmSwitch.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { compoundButton, b ->
            if (b) {
                binding.pathPanel.visibility = View.VISIBLE
            } else {
                binding.pathPanel.visibility = View.GONE
            }
        })
    }

    private var alarmTimeChecked = false
    private val agoCheckboxes = booleanArrayOf(false, true, false, false)
    private var customChecked = false
    private var customVal = 5
    private var customIndex = 0 //0 분 1 시간 2 일 3 주
    private var alarm: String? = null
    private fun initAlarmTimeListener(){

        binding.alarmTimeLayout.setOnClickListener {
            if (alarmTimeChecked) {
                binding.alarmTimeCheckboxLayout.visibility = View.VISIBLE
                binding.alarmTimeTextview.setBackground(
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.ed_text
                    )
                )
            } else {
                binding.alarmTimeCheckboxLayout.visibility = View.GONE
                binding.alarmTimeTextview.setBackgroundColor(
                    ContextCompat.getColor(
                        this,
                        R.color.bg_white
                    )
                )
                var text = ""
                val temp = arrayOf("일정 시작시간", "10분", "1시간", "1일")
                for (i in 0..3) {
                    if (agoCheckboxes[i]) {
                        text += temp[i]
                        text += ", "
                    }
                }
                if (customChecked) {
                    text += getCustomText(customVal, customIndex)
                    text += ", "
                }
                if (text === "") {
                    binding.alarmTimeTextview.text = "알람 설정 없음"
                } else {
                    text = text.substring(0, text.length - 2) + " 전"
                    binding.alarmTimeTextview.text = text
                }
                alarm = text
            }
            alarmTimeChecked = !alarmTimeChecked
        }

        binding.checkboxOntime.setOnCheckedChangeListener{ _, isChecked ->
            agoCheckboxes[0] = isChecked
        }
        binding.checkbox10MinAgo.setOnCheckedChangeListener{ _, isChecked ->
            agoCheckboxes[1] = isChecked
        }
        binding.checkboxHourago.setOnCheckedChangeListener { _, isChecked ->
            agoCheckboxes[2] = isChecked
        }
        binding.checkboxDayago.setOnCheckedChangeListener { _, isChecked ->
            agoCheckboxes[3] = isChecked
        }

        binding.charpicker.maxValue = 3
        binding.charpicker.minValue = 0
        binding.charpicker.displayedValues = arrayOf<String>(
            "분", "시간", "일", "주"
        )
        binding.charpicker.wrapSelectorWheel = false

        binding.customAlramBtn.setOnClickListener{
            binding.customAlramLayout.visibility = View.VISIBLE
            setNumPicker(customIndex)
            binding.numpicker.value = customVal
            binding.charpicker.value = customIndex
            binding.checkboxCustom.text = getCustomText(customVal, customIndex)
            binding.numpickerLayout.visibility = View.VISIBLE
            binding.customAlramBtn.isClickable = false
            customChecked = true
        }

        binding.checkboxCustom.setOnCheckedChangeListener{ _, isChecked ->
            if (isChecked) {
                setNumPicker(customIndex)
                binding.numpicker.value = customVal
                binding.charpicker.value = customIndex
                binding.numpickerLayout.visibility = View.VISIBLE
                customChecked = true
            } else {
                binding.numpickerLayout.visibility = View.GONE
                customChecked = false
            }
        }

        binding.numpicker.setOnValueChangedListener{ _, _, value ->
            customVal = value
            binding.checkboxCustom.text = getCustomText(customVal, customIndex)
        }

        binding.charpicker.setOnValueChangedListener{ _, _, index ->
            customIndex = index
            setNumPicker(customIndex)
            binding.checkboxCustom.text = getCustomText(customVal, customIndex)
        }
    }


    private var iteratorFlag = 0
    private fun initIteratorListener() {
        val iteratorTextview = binding.iteratorTextview
        val iteratorRadioGroup = binding.iteratorRadioGroup

        binding.iteratorLayout.setOnClickListener {
            val isVisible = iteratorRadioGroup.visibility == View.VISIBLE
            iteratorRadioGroup.visibility = if (!isVisible) View.VISIBLE else View.GONE
            iteratorTextview.background = if (!isVisible) {
                ContextCompat.getDrawable(this, R.drawable.ed_text)
            } else {
                ContextCompat.getDrawable(this, R.color.bg_white)
            }
        }

        val radioIdToOperatorFlag = mapOf(
            R.id.radiobutton_norepeat to 0,
            R.id.radiobutton_everyday to 1,
            R.id.radiobutton_everyweek to 2,
            R.id.radiobutton_everymonth to 3,
            R.id.radiobutton_everyyear to 4
        )

        iteratorRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            iteratorFlag = radioIdToOperatorFlag[checkedId] ?: 0
            iteratorRadioGroup.visibility = View.GONE
            iteratorTextview.text = when (iteratorFlag) {
                0 -> "반복 안 함"
                1 -> "매일"
                2 -> "매주"
                3 -> "매월"
                4 -> "매년"
                else -> ""
            }
            iteratorTextview.setBackgroundColor(ContextCompat.getColor(this, R.color.bg_white))
        }
    }

    private var meansFlag = -1

    private var srcAddress: String? = null
    private var dstAddress: String? = null
    private fun initExpectedTimeListener(){
        binding.timeRequieredClick.setOnClickListener{
            if (meansFlag >= 0 && srcAddress != null && dstAddress != null) {
                // 소요 시간 계산
            } else {
                Toast.makeText(applicationContext, "출발지 도착지 이동 수단을 모두 입력 하세요", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        binding.meansRadiogroup.setOnCheckedChangeListener{ _, button ->
            when (button) {
                R.id.radiobutton_walk -> {
                    meansFlag = 1
                }
                R.id.radiobutton_public -> {
                    meansFlag = 0
                }
                R.id.radiobutton_car -> {
                    meansFlag = 2
                }
            }
        }

        fun setClickListener(view: View) {
            view.setOnClickListener {
                val isOnline: Boolean = NetworkStatusService.isOnline(applicationContext)
                if (isOnline) {
                    when (view) {
                        binding.srcAddressText -> Log.d("srcAddressText", "click")
                        binding.dstAddressText -> Log.i("dstAddressText", "click")
                    }
                    val intent = Intent(applicationContext, AddressActivity::class.java)
                    // 필요한 경우 intent를 사용하여 다른 작업을 수행
                } else {
                    Toast.makeText(baseContext, "인터넷 연결 확인", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // 클릭 리스너 할당
        binding.srcAddressText.isFocusable = false
        binding.dstAddressText.isFocusable = false

        setClickListener(binding.srcAddressText)
        setClickListener(binding.dstAddressText)
    }
    private fun initSaveCancelListener(){
        binding.saveBtn.setOnClickListener(View.OnClickListener {
            presenter.saveSchedule()
            setResult(RESULT_OK)
            finish()
        })

        binding.cancelBtn.setOnClickListener(View.OnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        })
    }

    private var pressCnt: Long = 0
    override fun onBackPressed() {
        val tempTime = System.currentTimeMillis()
        val interval: Long = tempTime - pressCnt

        if (interval in 0..1000) {
            setResult(RESULT_CANCELED)
            finish()
        } else {
            pressCnt = tempTime
            Toast.makeText(baseContext, "한번 더 누르면 메인 화면", Toast.LENGTH_SHORT).show()
        }
    }


//    private lateinit var mMap: GoogleMap
//    private lateinit var srcMarker: Marker
//    private lateinit var dstMarker: Marker
//    private fun addLocationMarker(
//        location: Location,
//        markerTitle: String,
//        markerSnippet: String?,
//        isSource: Boolean
//    ) {
//        val latlng = LatLng(location.latitude, location.longitude)
//        val markerOptions = MarkerOptions()
//        markerOptions.position(latlng)
//        markerOptions.title(if (isSource) "출발지 : $markerTitle" else "도착지 : $markerTitle")
//        markerOptions.snippet(markerSnippet)
//        markerOptions.draggable(true)
//        val marker = mMap.addMarker(markerOptions)
//        marker?.showInfoWindow()
//
//        val zoomToFitBuilder = LatLngBounds.Builder()
//        zoomToFitBuilder.include(latlng)
//        if (isSource) {
//            presenter.getSchedule().dstPosition?.let { zoomToFitBuilder.include(it) }
//        } else {
//            zoomToFitBuilder.include(presenter.getSchedule().srcPosition!!)
//        }
//        val zoomToFitBound = zoomToFitBuilder.build()
//        val padding = 100
//        val cameraUpdate = CameraUpdateFactory.newLatLngBounds(zoomToFitBound, padding)
//        mMap.animateCamera(cameraUpdate)
//
//        if (isSource) {
//            srcMarker = marker!!
//        } else {
//            dstMarker = marker!!
//        }
//    }
//    override fun setSrcLocation(location: Location, markerTitle: String, markerSnippet: String?) {
//        srcMarker?.remove()
//        addLocationMarker(location, markerTitle, markerSnippet, isSource = true)
//    }
//    override fun setDstLocation(location: Location, markerTitle: String, markerSnippet: String?) {
//        dstMarker?.remove()
//        addLocationMarker(location, markerTitle, markerSnippet, isSource = false)
//    }



    // googleMap 관련 함수
    override fun onMapReady(googleMap: GoogleMap) {
//        mMap = googleMap
//        setDefaultLocation()
//        if (hasLocationPermissions()) {
//            startLocationUpdates()
//        } else {
//            requestLocationPermissions()
//        }
//        configureMapUI()
        googleMap.addMarker(
            MarkerOptions()
                .position(LatLng(0.0, 0.0))
                .title("Marker")
        )
    }
//    private fun hasLocationPermissions(): Boolean {
//        val hasFineLocationPermission = ContextCompat.checkSelfPermission(
//            this,
//            Manifest.permission.ACCESS_FINE_LOCATION
//        ) == PackageManager.PERMISSION_GRANTED
//        val hasCoarseLocationPermission = ContextCompat.checkSelfPermission(
//            this,
//            Manifest.permission.ACCESS_COARSE_LOCATION
//        ) == PackageManager.PERMISSION_GRANTED
//
//        return hasFineLocationPermission && hasCoarseLocationPermission
//    }
//    private fun requestLocationPermissions() {
//        if (ActivityCompat.shouldShowRequestPermissionRationale(
//                this,
//                REQUIRED_PERMISSIONS[0]
//            )
//        ) {
//            // 사용자에게 퍼미션 요청 이유를 설명해줄 수 있는 경우
//            Snackbar.make(
//                findViewById(R.id.map), "이 앱을 실행하려면 위치 접근 권한이 필요합니다.",
//                Snackbar.LENGTH_INDEFINITE
//            )
//                .setAction("확인") {
//                    ActivityCompat.requestPermissions(
//                        this, REQUIRED_PERMISSIONS,
//                        Permission.PERMISSIONS_REQUEST_CODE
//                    )
//                }.show()
//        } else {
//            // 사용자에게 퍼미션 요청 이유를 설명할 필요 없는 경우
//            ActivityCompat.requestPermissions(
//                this, REQUIRED_PERMISSIONS,
//                Permission.PERMISSIONS_REQUEST_CODE
//            )
//        }
//    }
//    private fun configureMapUI() {
//        mMap.uiSettings.isMyLocationButtonEnabled = true
//        mMap.setOnMapClickListener {
//            Log.d("googleMap Click", "onMapClick")
//        }
//    }
//    private fun setDefaultLocation() {
//        val DEFAULT_LOCATION = LatLng(37.56, 126.97)
//        val markerTitle = "위치정보 가져올 수 없음"
//        val markerSnippet = "위치 퍼미션과 GPS 활성 요부 확인하세요"
//        srcMarker?.remove()
//        val markerOptions = MarkerOptions()
//            .position(DEFAULT_LOCATION)
//            .title(markerTitle)
//            .snippet(markerSnippet)
//            .draggable(true)
//            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
//        srcMarker = mMap.addMarker(markerOptions)!!
//        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, 15f)
//        mMap.moveCamera(cameraUpdate)
//    }
//    private fun checkLocationServicesStatus(): Boolean {
//        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
//        return (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
//                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
//    }
//    private fun startLocationUpdates() {
//        if (!checkLocationServicesStatus()) {
//            showDialogForLocationServiceSetting()
//        } else {
//            val hasFineLocationPermission = ContextCompat.checkSelfPermission(
//                this,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            )
//            val hasCoarseLocationPermission = ContextCompat.checkSelfPermission(
//                this,
//                Manifest.permission.ACCESS_COARSE_LOCATION
//            )
//            if (hasFineLocationPermission != PackageManager.PERMISSION_GRANTED ||
//                hasCoarseLocationPermission != PackageManager.PERMISSION_GRANTED
//            ) {
//                return
//            }
//            mFusedLocationClient?.requestLocationUpdates(
//                locationRequest,
//                presenter.getLocationCallback(applicationContext),
//                Looper.myLooper()
//            )
//            if (Permission.checkPermission(applicationContext)) {
//                mMap.isMyLocationEnabled = true
//            }
//        }
//    }
//    private fun showDialogForLocationServiceSetting() {
//        val builder = AlertDialog.Builder(this)
//        builder.setTitle("위치 서비스 비활성화")
//        builder.setMessage(
//            """
//            앱을 사용하기 위해서는 위치 서비스가 필요합니다.
//            위치 설정을 수정하실래요?
//            """.trimIndent()
//        )
//        builder.setCancelable(true)
//        builder.setPositiveButton("설정") { _, _ ->
//            val callGPSSettingIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
//            startActivityForResult(
//                callGPSSettingIntent,
//                Permission.GPS_ENABLE_REQUEST_CODE
//            )
//        }
//        builder.setNegativeButton(
//            "취소"
//        ) { dialog, _ -> dialog.cancel() }
//        builder.create().show()
//    }

//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == Permission.PERMISSIONS_REQUEST_CODE &&
//            grantResults.size == REQUIRED_PERMISSIONS.size
//        ) {
//            val allPermissionsGranted = grantResults.all { it == PackageManager.PERMISSION_GRANTED }
//
//            if (allPermissionsGranted) {
//                // 모든 퍼미션을 허용한 경우
//                startLocationUpdates()
//            } else {
//                // 퍼미션 거부한 경우
//                val shouldShowRationale = ActivityCompat.shouldShowRequestPermissionRationale(
//                    this, REQUIRED_PERMISSIONS[0]
//                ) || ActivityCompat.shouldShowRequestPermissionRationale(
//                    this, REQUIRED_PERMISSIONS[1]
//                )
//
//                val message = if (shouldShowRationale) {
//                    // 사용자에게 퍼미션 요청 이유를 설명할 수 있는 경우
//                    "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요."
//                } else {
//                    // "다시 묻지 않음"을 사용자가 체크하고 거부한 경우
//                    "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다."
//                }
//
//                Snackbar.make(
//                    findViewById(R.id.map), message,
//                    Snackbar.LENGTH_INDEFINITE
//                ).setAction("확인") { finish() }.show()
//            }
//        }
//    }
//
//    private val location: Location? = null
//    val giourl =
//        "http://apis.openapi.sk.com/tmap/geo/fullAddrGeo?addressFlag=F00&coordType=WGS84GEO&version=1&format=json&fullAddr="
//    val key = "l7xxb76eb9ee907444a8b8098322fa488048"
//
//
//    var mStartForResult = registerForActivityResult<Intent, ActivityResult>(
//        ActivityResultContracts.StartActivityForResult()
//    ) { result: ActivityResult ->
//        if (result.resultCode == RESULT_OK) {
//            presenter.getSchedule().srcAddress = result.data!!.extras!!.getString("data")
//            if (presenter.getSchedule().srcAddress != null) {
//                Thread {
//                    try {
//                        Log.d("지오코딩", presenter.getSchedule().srcAddress!!)
//                        val s =
//                            URLEncoder.encode(presenter.getSchedule().srcAddress, "utf-8")
//                        Log.d("encode", "done")
//                        val url =
//                            URL("$giourl$s&appKey=$key")
//                        Log.d("make", url.toString())
//                        Log.d("bfreader", "ready")
//                        val bf: BufferedReader = BufferedReader(
//                            InputStreamReader(
//                                url.openStream(),
//                                "UTF-8"
//                            )
//                        )
//                        Log.d("bfreader", "done")
//                        val temp = bf.readLine()
//                        Log.d("연결완료", temp)
//                        val jsonParser = JSONParser()
//                        val jsonObject: JSONObject = jsonParser.parse(temp) as JSONObject
//                        val coordinateInfo: JSONObject =
//                            jsonObject["coordinateInfo"] as JSONObject
//                        val coordinate: JSONArray =
//                            coordinateInfo["coordinate"] as JSONArray
//                        val pos: JSONObject = coordinate[0] as JSONObject
//                        val newmatchflag: String = pos["newMatchFlag"].toString()
//                        val lat: Double
//                        val lon: Double
//                        if (!newmatchflag.isEmpty()) {
//                            Log.d("fff", "fff")
//                            lat = (pos.get("newLat") as String).toDouble()
//                            lon = (pos.get("newLon") as String).toDouble()
//                        } else {
//                            lat = (pos.get("lat") as String).toDouble()
//                            lon = (pos.get("lon") as String).toDouble()
//                        }
//                        presenter.getSchedule().srcPosition = LatLng(lat, lon)
//                        Log.d("lat", lat.toString())
//                        location?.latitude = lat
//                        Log.d("latdst", "done")
//                        location?.longitude = lon
//                        val markerTitle: String? = presenter.getMarkerTitle(baseContext)
//                        val markerSnippet =
//                            "위도:$lat 경도:$lon"
//                        Log.d(
//                            "googleMap",
//                            "srconLocationResult : $markerSnippet"
//                        )
//                        Log.d("lat", lat.toString())
//                        location?.latitude = lat
//                        Log.d("latdst", "done")
//                        location?.longitude = lon
//                        Log.d("srcmarker", markerTitle!!)
//                        presenter.getSchedule().srcLocation = location
//                        this.runOnUiThread(Runnable {
//                            presenter.getSchedule().srcAddress=markerTitle
//                            setSrcLocation(presenter.getSchedule().srcLocation!!, markerTitle!!, markerSnippet)
//                        })
//                    } catch (e: Exception) {
//                        e.printStackTrace()
//                    }
//                }.start()
//            }
//        }
//    }

}