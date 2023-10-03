package com.ddmyb.shalendar.view.schedules

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.CompoundButton
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.ddmyb.shalendar.R
import com.ddmyb.shalendar.databinding.ActivityScheduleBinding
import com.ddmyb.shalendar.view.programmatic_autocomplete.ProgrammaticAutocompleteGeocodingActivity
import com.ddmyb.shalendar.view.schedules.adapter.NetworkStatusService
import com.ddmyb.shalendar.view.schedules.utils.AlarmInfo.AlarmType.*
import com.ddmyb.shalendar.view.schedules.utils.IterationType.*
import com.ddmyb.shalendar.view.schedules.utils.MeansType
import com.ddmyb.shalendar.view.schedules.utils.Permission
import com.ddmyb.shalendar.view.schedules.utils.Permission.Companion.REQUIRED_PERMISSIONS
import com.ddmyb.shalendar.view.schedules.utils.StartDateTimeDto
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
import java.time.LocalDateTime

private lateinit var binding: ActivityScheduleBinding
private lateinit var getResult: ActivityResultLauncher<Intent>

@RequiresApi(Build.VERSION_CODES.O)
class ScheduleActivity(
    ) : AppCompatActivity(), OnMapReadyCallback{

//    private val scheduleId: String = intent.getStringExtra("id")!!

    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private lateinit var locationRequest: LocationRequest

    private lateinit var resultLatLng: LatLng
    private lateinit var resultTitle: String
    private lateinit var resultLocation: Location
    private var isSrcCallBack = true

    private val presenter: SchedulePresenter = SchedulePresenter(this, StartDateTimeDto(null, LocalDateTime.now().plusHours(14)))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityScheduleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.map.layoutParams.height = resources.displayMetrics.widthPixels - 20
        binding.startDateTextview.text = getDateText(presenter.getSchedule().startLocalDateTime!!.monthValue, presenter.getSchedule().startLocalDateTime!!.dayOfMonth, presenter.getSchedule().startLocalDateTime!!.dayOfWeek.value)
        binding.startTimeTextview.text = getTimeText(presenter.getSchedule().startLocalDateTime!!.hour, presenter.getSchedule().startLocalDateTime!!.minute)
        binding.endDateTextview.text = getDateText(presenter.getSchedule().endLocalDateTime!!.monthValue, presenter.getSchedule().endLocalDateTime!!.dayOfMonth, presenter.getSchedule().endLocalDateTime!!.dayOfWeek.value)
        binding.endTimeTextview.text = getTimeText(presenter.getSchedule().endLocalDateTime!!.hour, presenter.getSchedule().endLocalDateTime!!.minute)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
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

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = LocationRequest.create()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval(10000)
            .setFastestInterval(5000)
        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(locationRequest)

        getResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            if (it.resultCode == RESULT_OK){
                resultTitle = it.data?.getStringExtra("title")!!
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    resultLatLng = it.data?.getParcelableExtra("location", LatLng::class.java)!!
                    resultLocation = Location(LocationManager.GPS_PROVIDER)
                    resultLocation.latitude = resultLatLng.latitude
                    resultLocation.longitude = resultLatLng.longitude
                }
                Log.d("resultLocation", resultLatLng.toString())
                Log.d("resultTitle", resultTitle)
                processGetResultCallBack()
            }
        }
    }

    private fun processGetResultCallBack() {
        when (isSrcCallBack) {
            true -> {
                presenter.getSchedule().srcPosition = resultLatLng
                presenter.getSchedule().srcLocation = resultLocation
                presenter.getSchedule().srcAddress = resultTitle
                setSrcLocation(resultLocation, resultTitle)
                binding.srcAddressText.text = resultTitle
            }
            false -> {
                presenter.getSchedule().dstPosition = resultLatLng
                presenter.getSchedule().dstLocation = resultLocation
                presenter.getSchedule().dstAddress = resultTitle
                setDstLocation(resultLocation, resultTitle)
                binding.dstAddressText.text = resultTitle
            }
        }
    }

    private var datePickerFlag: Int = 0
    private var timePickerFlag: Int = 0
    private val clickedFlags = BooleanArray(4) { false }

    private fun initTimeSelectionListener() {
        binding.startTimeTextview.setOnClickListener(View.OnClickListener {

            Log.d("datePickerFlag", datePickerFlag.toString())
            when (datePickerFlag) {
                1 -> {
                    binding.dateStartLayout.visibility = View.GONE
                    setClicked(0, false)
                }
                2 -> {
                    Log.d("dateEndLayout", datePickerFlag.toString())
                    binding.dateEndLayout.visibility = View.GONE
                    setClicked(2, false)
                    binding.dateEndLayout.visibility = View.GONE
                }
            }
            datePickerFlag = 0

            when (timePickerFlag) {
                0 -> {
                    binding.timeStartLayout.visibility = View.VISIBLE
                    setClicked(1, true)
                    timePickerFlag = 1
                }
                1 -> {
                    binding.timeStartLayout.visibility = View.GONE
                    setClicked(1, false)
                    timePickerFlag = 0
                }
                2 -> {
                    binding.timeEndLayout.visibility = View.GONE
                    setClicked(3, false)
                    binding.timeStartLayout.visibility = View.VISIBLE
                    setClicked(1, true)
                    timePickerFlag = 1
                }
            }
        })

        binding.endTimeTextview.setOnClickListener(View.OnClickListener {
            when (datePickerFlag) {
                1 -> {
                    binding.dateStartLayout.visibility = View.GONE
                    setClicked(0, false)
                }
                2 -> {
                    binding.dateEndLayout.visibility = View.GONE
                    setClicked(2, false)
                }
            }
            datePickerFlag = 0

            when (timePickerFlag) {
                0 -> {
                    binding.timeEndLayout.visibility = View.VISIBLE
                    setClicked(3, true)
                    timePickerFlag = 2
                }
                1 -> {
                    binding.timeStartLayout.visibility = View.GONE
                    setClicked(1, false)
                    binding.timeEndLayout.visibility = View.VISIBLE
                    setClicked(3, true)
                    timePickerFlag = 2
                }
                2 -> {
                    binding.timeEndLayout.visibility = View.GONE
                    setClicked(3, false)
                    timePickerFlag = 0
                }
            }
        })

        binding.startDateTextview.setOnClickListener(View.OnClickListener {
            when (datePickerFlag) {
                0 -> {
                    binding.dateStartLayout.visibility = View.VISIBLE
                    setClicked(0, true)
                    datePickerFlag = 1
                }
                1 -> {
                    binding.dateStartLayout.visibility = View.GONE
                    setClicked(0, false)
                    datePickerFlag = 0
                }
                2 -> {
                    binding.dateEndLayout.visibility = View.GONE
                    setClicked(2, false)
                    binding.dateStartLayout.visibility = View.VISIBLE
                    setClicked(0, true)
                    datePickerFlag = 1
                }
            }

            when (timePickerFlag) {
                1 -> {
                    binding.timeStartLayout.visibility = View.GONE
                    setClicked(1, false)
                }
                2 -> {
                    binding.timeEndLayout.visibility = View.GONE
                    setClicked(3, false)
                }
            }
            timePickerFlag = 0
        })

        binding.endDateTextview.setOnClickListener(View.OnClickListener {
            when (datePickerFlag) {
                0 -> {
                    binding.dateEndLayout.visibility = View.VISIBLE
                    setClicked(2, true)
                    datePickerFlag = 2
                }
                1 -> {
                    binding.dateStartLayout.visibility = View.GONE
                    setClicked(0, false)
                    binding.dateEndLayout.visibility = View.VISIBLE
                    setClicked(2, true)
                    datePickerFlag = 2
                }
                2 -> {
                    binding.dateEndLayout.visibility = View.GONE
                    setClicked(2, false)
                    datePickerFlag = 0
                }
            }

            when (timePickerFlag) {
                1 -> {
                    binding.timeStartLayout.visibility = View.GONE
                    setClicked(1, false)
                }
                2 -> {
                    binding.timeEndLayout.visibility = View.GONE
                    setClicked(3, false)
                }
            }
            timePickerFlag = 0
        })
    }
    private fun setClicked(i: Int, isClicked: Boolean) {
        clickedFlags[i] = isClicked
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

    fun showEndTimeText(endHour: Int, endMinute: Int) {
        binding.endTimeTextview.text = getTimeText(endHour, endMinute)
    }
    fun showStartTimeText(startHour: Int, startMinute: Int) {
        binding.startTimeTextview.text = getTimeText(startHour, startMinute)
    }
    fun showStartDateText(startMonth: Int, startDay: Int, startWeek: Int) {
       binding.startDateTextview.text = getDateText(startMonth, startDay, startWeek)
    }
    fun showEndDateText(endMonth: Int, endDay: Int, endWeek: Int) {
        binding.endDateTextview.text = getDateText(endMonth, endDay, endWeek)
    }


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
            0 -> "일"
            1 -> "월"
            2 -> "화"
            3 -> "수"
            4 -> "목"
            5 -> "금"
            6 -> "토"
            else -> ""
        }
        return "$month 월 $day 일 ($week)"
    }
    private fun getTimeText(hour: Int, minute: Int): String {
        val period = if (hour < 12) {
            if (hour == 0) "오전 12:" else "오전 $hour:"
        } else {
            if (hour == 12) "오후 12:" else "오후 ${hour - 12}:"
        }

        val minuteText = if (minute < 10) "0$minute" else minute.toString()

        return "$period$minuteText"
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
                timePickerFlag = 0
                datePickerFlag = 0
            } else {
                binding.startTimeTextview.visibility = View.VISIBLE
                binding.endTimeTextview.visibility = View.VISIBLE
            }
        }
        /**
         * 알람을 할지 안할지 여부를 선택
         */
        binding.alarmSwitch.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { _, isClicked ->
            if (isClicked) {
                binding.pathPanel.visibility = View.VISIBLE
            } else {
                binding.pathPanel.visibility = View.GONE
                presenter.setAlarmInfo()
            }
        })
    }

    private var customVal = 5
    private var customIndex = 0 //0 -> 분 1 -> 시간 -> 2 -> 일 3 -> 주
    private fun initAlarmTimeListener(){

        binding.alarmTimeLayout.setOnClickListener {

            val isVisible = binding.alarmTimeCheckboxLayout.visibility == View.VISIBLE
            if (isVisible) {
                binding.alarmTimeTextview.background = ContextCompat.getDrawable(this, R.color.bg_white)
                showAlarmTimeText()
            } else {
                binding.alarmTimeTextview.background = ContextCompat.getDrawable(this, R.drawable.ed_text)
            }
            binding.alarmTimeCheckboxLayout.visibility = if (!isVisible) View.VISIBLE else View.GONE
        }

        binding.checkboxOntime.setOnCheckedChangeListener{ _, isChecked ->
            presenter.setAlarmInfo(START_TIME, isChecked)
        }
        binding.checkbox10MinAgo.setOnCheckedChangeListener{ _, isChecked ->
            presenter.setAlarmInfo(TEN_MIN_AGO, isChecked)
        }
        binding.checkboxHourago.setOnCheckedChangeListener { _, isChecked ->
            presenter.setAlarmInfo(HOUR_AGO, isChecked)
        }
        binding.checkboxDayago.setOnCheckedChangeListener { _, isChecked ->
            presenter.setAlarmInfo(DAY_AGO, isChecked)
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

            showCustomCheckBox()

            binding.numpickerLayout.visibility = View.VISIBLE
            binding.customAlramBtn.isClickable = true
            presenter.setAlarmInfo(CUSTOM, true)
        }

        binding.checkboxCustom.setOnCheckedChangeListener{ _, isChecked ->
            if (isChecked) {
                setNumPicker(customIndex)
                binding.numpicker.value = customVal
                binding.charpicker.value = customIndex
                binding.numpickerLayout.visibility = View.VISIBLE
                presenter.setAlarmInfo(customVal, customIndex)
            } else {
                binding.numpickerLayout.visibility = View.GONE
            }
            presenter.setAlarmInfo(CUSTOM, isChecked)
        }

        binding.numpicker.setOnValueChangedListener{ _, _, value ->
            customVal = value
            showCustomCheckBox()
        }

        binding.charpicker.setOnValueChangedListener{ _, _, index ->
            customIndex = index
            showCustomCheckBox()
        }
    }

    private fun showCustomCheckBox() {
        var str = presenter.getAlarmInfo().getCustomText(customVal, customIndex)
        str = str.substring(0, str.length - 2) + " 전"
        binding.checkboxCustom.text = str
    }

    private fun showAlarmTimeText() {
        binding.alarmTimeTextview.text = presenter.getAlarmInfo().toString(customVal, customIndex)
    }

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

        val iterationTypeMap = mapOf(
            R.id.radiobutton_norepeat to NO_REPEAT,
            R.id.radiobutton_everyday to EVERY_DAY,
            R.id.radiobutton_everyweek to EVERY_WEEK,
            R.id.radiobutton_everymonth to EVERY_MONTH,
            R.id.radiobutton_everyyear to EVERY_YEAR
        )

        iteratorRadioGroup.setOnCheckedChangeListener { _, id ->
            iteratorRadioGroup.visibility = View.GONE
            iteratorTextview.text = iterationTypeMap[id].toString()
            iteratorTextview.setBackgroundColor(ContextCompat.getColor(this, R.color.bg_white))

            presenter.setIterationType(iterationTypeMap[id]!!)
        }
    }

    fun showTimeRequired(timeRequired: String, departureTime: LocalDateTime){
        binding.timeRequieredTextview.post {
            binding.timeRequieredTextview.text = timeRequired
        }
        binding.preSrcTimeTextview.post {
            binding.preSrcTimeTextview.text = getTimeText(departureTime.hour, departureTime.minute)
        }
    }

    private fun initExpectedTimeListener(){
        binding.timeRequieredClick.setOnClickListener{
            if (presenter.getSchedule().meansType != MeansType.NULL) {
                presenter.calExpectedTime()
            } else {
                Toast.makeText(applicationContext, "출발지 도착지 이동 수단을 모두 입력 하세요", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        binding.meansRadiogroup.setOnCheckedChangeListener{ _, button ->
            when (button) {
                R.id.radiobutton_walk -> {
                    presenter.getSchedule().meansType = MeansType.WALK
                }
                R.id.radiobutton_public -> {
                    presenter.getSchedule().meansType = MeansType.PUBLIC
                }
                R.id.radiobutton_car -> {
                    presenter.getSchedule().meansType = MeansType.CAR
                }
            }
        }

        fun setClickListener(view: View) {
            view.setOnClickListener {
                val isOnline: Boolean = NetworkStatusService.isOnline(applicationContext)
                if (isOnline) {
                    when (view) {
                        binding.srcAddressText -> {
                            Log.d("srcAddressText", "click")
                            isSrcCallBack = true
                        }
                        binding.dstAddressText -> {
                            Log.i("dstAddressText", "click")
                            isSrcCallBack = false
                        }
                    }
                    val intent = Intent(this, ProgrammaticAutocompleteGeocodingActivity::class.java)
                    getResult.launch(intent)
                } else {
                    Toast.makeText(baseContext, "인터넷 연결 확인", Toast.LENGTH_SHORT).show()
                }
            }
        }
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


    private lateinit var mMap: GoogleMap
    private var srcMarker: Marker? = null
    private var dstMarker: Marker? = null
    private fun addLocationMarker(
        location: Location,
        markerTitle: String,
        isSource: Boolean
    ) {
        val latLng = LatLng(location.latitude, location.longitude)
        val markerOptions = MarkerOptions()
            .position(latLng)
            .title(if (isSource) "출발지 : $markerTitle" else "도착지 : $markerTitle")
            .draggable(true)
        val marker = mMap.addMarker(markerOptions)
        marker?.showInfoWindow()

        val zoomToFitBuilder = LatLngBounds.Builder()
        zoomToFitBuilder.include(latLng)
        var cnt = 1
        if (isSource) {
            presenter.getSchedule().dstPosition?.let {
                zoomToFitBuilder.include(it)
                cnt += 1
            }
        } else {
            presenter.getSchedule().srcPosition?.let {
                zoomToFitBuilder.include(it)
                cnt += 1
            }
        }
        val cameraUpdate =
            if (cnt == 1){
                Log.d("cameraUpdate", "newLatLngZoom")
                CameraUpdateFactory.newLatLngZoom(latLng, 15.0f)
            } else {
                Log.d("cameraUpdate", "newLatLngBounds")
                val zoomToFitBound = zoomToFitBuilder.build()
                val width = resources.displayMetrics.widthPixels
                CameraUpdateFactory.newLatLngBounds(zoomToFitBound, width, width, width/4)
            }
        mMap.animateCamera(cameraUpdate)

        if (isSource) {
            srcMarker = marker!!
        } else {
            dstMarker = marker!!
        }
    }
    fun setSrcLocation(location: Location, markerTitle: String) {
        srcMarker?.remove()
        addLocationMarker(location, markerTitle, isSource = true)
    }
    fun setDstLocation(location: Location, markerTitle: String) {
        dstMarker?.remove()
        addLocationMarker(location, markerTitle, isSource = false)
    }



    // googleMap 관련 함수
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        setDefaultLocation()
        if (hasLocationPermissions()) {
            startLocationUpdates()
        } else {
            requestLocationPermissions()
        }
        configureMapUI()
    }
    private fun hasLocationPermissions(): Boolean {
        val hasFineLocationPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val hasCoarseLocationPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        return hasFineLocationPermission && hasCoarseLocationPermission
    }
    private fun requestLocationPermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                REQUIRED_PERMISSIONS[0]
            )
        ) {
            // 사용자에게 퍼미션 요청 이유를 설명해줄 수 있는 경우
            Snackbar.make(
                findViewById(R.id.map), "이 앱을 실행하려면 위치 접근 권한이 필요합니다.",
                Snackbar.LENGTH_INDEFINITE
            )
                .setAction("확인") {
                    ActivityCompat.requestPermissions(
                        this, REQUIRED_PERMISSIONS,
                        Permission.PERMISSIONS_REQUEST_CODE
                    )
                }.show()
        } else {
            // 사용자에게 퍼미션 요청 이유를 설명할 필요 없는 경우
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS,
                Permission.PERMISSIONS_REQUEST_CODE
            )
        }
    }
    private fun configureMapUI() {
        mMap.uiSettings.isMyLocationButtonEnabled = true
        mMap.setOnMapClickListener {
            Log.d("googleMap Click", "onMapClick")
        }
    }
    private fun setDefaultLocation() {
        val DEFAULT_LOCATION = LatLng(37.56, 126.97)
        val markerTitle = "위치정보 가져올 수 없음"
        val markerSnippet = "위치 퍼미션과 GPS 활성 요부 확인하세요"
        srcMarker?.remove()
        val markerOptions = MarkerOptions()
            .position(DEFAULT_LOCATION)
            .title(markerTitle)
            .snippet(markerSnippet)
            .draggable(true)
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
        srcMarker = mMap.addMarker(markerOptions)!!
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, 15f)
        mMap.moveCamera(cameraUpdate)
    }
    private fun checkLocationServicesStatus(): Boolean {
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        return (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
    }
    private fun startLocationUpdates() {
        if (!checkLocationServicesStatus()) {
            showPermissionDialog()
        } else {
            val hasFineLocationPermission = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            val hasCoarseLocationPermission = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            if (hasFineLocationPermission != PackageManager.PERMISSION_GRANTED ||
                hasCoarseLocationPermission != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            mFusedLocationClient?.requestLocationUpdates(
                locationRequest!!,
                presenter.getLocationCallback(applicationContext),
                Looper.myLooper()
            )
            if (Permission.checkPermission(applicationContext)) {
                mMap.isMyLocationEnabled = true
            }
        }
    }
    private fun showPermissionDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("위치 서비스 비활성화")
        builder.setMessage(
            """
            앱을 사용하기 위해서는 위치 서비스가 필요합니다.
            위치 설정을 수정하실래요?
            """.trimIndent()
        )
        builder.setCancelable(true)
        builder.setPositiveButton("설정") { _, _ ->
            val callGPSSettingIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivityForResult(
                callGPSSettingIntent,
                Permission.GPS_ENABLE_REQUEST_CODE
            )
        }
        builder.setNegativeButton(
            "취소"
        ) { dialog, _ -> dialog.cancel() }
        builder.create().show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Permission.PERMISSIONS_REQUEST_CODE &&
            grantResults.size == REQUIRED_PERMISSIONS.size
        ) {
            val allPermissionsGranted = grantResults.all { it == PackageManager.PERMISSION_GRANTED }

            if (allPermissionsGranted) {
                // 모든 퍼미션을 허용한 경우
                startLocationUpdates()
            } else {
                // 퍼미션 거부한 경우
                val shouldShowRationale = ActivityCompat.shouldShowRequestPermissionRationale(
                    this, REQUIRED_PERMISSIONS[0]
                ) || ActivityCompat.shouldShowRequestPermissionRationale(
                    this, REQUIRED_PERMISSIONS[1]
                )

                val message = if (shouldShowRationale) {
                    // 사용자에게 퍼미션 요청 이유를 설명할 수 있는 경우
                    "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요."
                } else {
                    // "다시 묻지 않음"을 사용자가 체크하고 거부한 경우
                    "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다."
                }

                Snackbar.make(
                    findViewById(R.id.map), message,
                    Snackbar.LENGTH_INDEFINITE
                ).setAction("확인") { finish() }.show()
            }
        }
    }


}