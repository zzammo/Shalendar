package com.ddmyb.shalendar.view.schedules

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.ddmyb.shalendar.R
import com.ddmyb.shalendar.databinding.ActivityScheduleBinding
import com.ddmyb.shalendar.view.programmatic_autocomplete.ProgrammaticAutocompleteGeocodingActivity
import com.ddmyb.shalendar.view.schedules.presenter.SchedulePresenter
import com.ddmyb.shalendar.view.schedules.utils.AlarmInfo
import com.ddmyb.shalendar.view.schedules.utils.AlarmInfo.AlarmType.*
import com.ddmyb.shalendar.view.schedules.utils.DateInfo
import com.ddmyb.shalendar.view.schedules.utils.IterationType.*
import com.ddmyb.shalendar.view.schedules.utils.MarkerInfo
import com.ddmyb.shalendar.view.schedules.utils.MeansType
import com.ddmyb.shalendar.view.schedules.utils.Permission
import com.ddmyb.shalendar.view.schedules.utils.Permission.Companion.REQUIRED_PERMISSIONS
import com.ddmyb.shalendar.util.NewScheduleDto
import com.ddmyb.shalendar.view.schedules.utils.TimeInfo
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import java.time.LocalDateTime
import java.time.ZoneId

private lateinit var binding: ActivityScheduleBinding
private lateinit var getResult: ActivityResultLauncher<Intent>

@RequiresApi(Build.VERSION_CODES.O)
class ScheduleActivity(
    ) : AppCompatActivity(), OnMapReadyCallback{

    private lateinit var presenter: SchedulePresenter

    private lateinit var resultLatLng: LatLng
    private lateinit var resultTitle: String
    private lateinit var resultLocation: Location
    private var isSrcCallBack = true
    private lateinit var mMap: GoogleMap
    private var srcMarker: Marker? = null
    private var dstMarker: Marker? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityScheduleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val newScheduleDto = intent.getSerializableExtra("StartDateTimeDto") as? NewScheduleDto
            ?: NewScheduleDto(scheduleId = "", mills = LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond() * 1000)
        Log.d("startDateTimeDto", newScheduleDto.toString())
        presenter = SchedulePresenter(this, newScheduleDto, this)

        binding.map.layoutParams.height = resources.displayMetrics.widthPixels - 100
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
        initTimeRequiredListener()
        initSaveCancelListener()
        initTitleMemoListener(binding.root)
        initColorListener()

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

    private var isVisibleLLPalette = false
    private fun initColorListener(){
        binding.llColorCircleSchedule.setOnClickListener {
            if (!isVisibleLLPalette)binding.llPalette.visibility = View.VISIBLE
            else binding.llPalette.visibility = View.GONE
            isVisibleLLPalette = !isVisibleLLPalette
        }
        val imArray = ArrayList<View>()
        imArray.add(binding.ivCat0)
        imArray.add(binding.ivCat1)
        imArray.add(binding.ivCat2)
        imArray.add(binding.ivCat3)
        imArray.add(binding.ivCat4)
        imArray.add(binding.ivCat5)

        val colorIdList = ArrayList<Int>()
        colorIdList.add(R.color.cat_0)
        colorIdList.add(R.color.cat_1)
        colorIdList.add(R.color.cat_2)
        colorIdList.add(R.color.cat_3)
        colorIdList.add(R.color.cat_4)
        colorIdList.add(R.color.cat_5)

        for ((i, v) in imArray.withIndex()) {
            v.setOnClickListener {
                binding.imColorCircleSchedule.backgroundTintList = ContextCompat.getColorStateList(applicationContext, colorIdList[i])
                presenter.saveColorId(colorIdList[i])
                isVisibleLLPalette = false
                binding.llPalette.visibility = View.GONE
            }
        }
    }
    fun setColor(colorId: Int){
        binding.imColorCircleSchedule.backgroundTintList = ContextCompat.getColorStateList(applicationContext, colorId)
    }

    private fun processGetResultCallBack() {
        when (isSrcCallBack) {
            true -> {
                presenter.getSchedule().srcPosition = resultLatLng
                presenter.getSchedule().srcAddress = resultTitle
                presenter.updateMarker(resultLocation, resultTitle, isSource = true)
                binding.tvSrcAddress.text = resultTitle
            }
            false -> {
                presenter.getSchedule().dstPosition = resultLatLng
                presenter.getSchedule().dstAddress = resultTitle
                presenter.updateMarker(resultLocation, resultTitle, isSource = false)
                binding.dstAddressText.text = resultTitle
            }
        }
    }
    private fun initTitleMemoListener(view: View) {
        view.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                clearFocusAndHideKeyboard(binding.memo, binding.title)
            }
            false
        }

        if (view is ViewGroup) {
            val childCount = view.childCount
            for (i in 0 until childCount) {
                val child = view.getChildAt(i)
                initTitleMemoListener(child) // 재귀적으로 자식 뷰에 대해 처리
            }
        }
    }
    private fun clearFocusAndHideKeyboard(vararg views: View) {
        views.forEach { it.clearFocus() }
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        views.forEach { imm.hideSoftInputFromWindow(it.windowToken, 0) }
    }

    private var datePickerFlag: Int = 0
    private var timePickerFlag: Int = 0
    private val clickedFlags = BooleanArray(4) { false }

    private fun initTimeSelectionListener() {
        binding.startTimeTextview.setOnClickListener{

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
        }

        binding.endTimeTextview.setOnClickListener{

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
        }

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

    fun showEndTimeText(timeInfo: TimeInfo) {
        binding.endTimeTextview.text = timeInfo.toString()
        binding.timeEndTimepicker.hour = timeInfo.hour
        binding.timeEndTimepicker.minute = timeInfo.minute
        Log.d("showEndTimeText", "execute")
    }
    fun showStartTimeText(timeInfo: TimeInfo) {
        binding.startTimeTextview.text = timeInfo.toString()
        binding.timeStartTimepicker.hour = timeInfo.hour
        binding.timeStartTimepicker.minute = timeInfo.minute
    }
    fun showStartDateText(year: Int = 2023, dateInfo: DateInfo, flag: Boolean = false) {
        binding.startDateTextview.text = dateInfo.toString()
        if (flag) {
            binding.dateStartDatepicker.updateDate(year, dateInfo.month - 1, dateInfo.day)
        }
    }
    fun showEndDateText(endYear: Int = 2023, dateInfo: DateInfo, flag: Boolean = false) {
        binding.endDateTextview.text = dateInfo.toString()
        if (flag) {
            binding.dateEndDatepicker.updateDate(endYear, dateInfo.month - 1, dateInfo.day)
        }
    }
    private fun initDateTimePicker(){
        binding.timeStartTimepicker.setOnTimeChangedListener{ _, startHour, startMinute ->
            presenter.setStartTime(startHour, startMinute)
        }
        binding.timeEndTimepicker.setOnTimeChangedListener { _, endHour, endMinute ->
            presenter.setEndTime(endHour, endMinute)
        }
        binding.dateStartDatepicker.setOnDateChangedListener { _, startYear, startMonth, startDay ->
            presenter.setStartDate(startYear, startMonth + 1, startDay)
        }
        binding.dateEndDatepicker.setOnDateChangedListener { _, endYear, endMonth, endDay ->
            presenter.setEndDate(endYear, endMonth + 1, endDay)
        }
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
         * 출발 전 알람을 할지 안할지 여부를 선택
         */
        binding.departureAlarmSwitch.setOnCheckedChangeListener { _, isClicked ->
            if (isClicked) {
                binding.pathPanel.visibility = View.VISIBLE
            } else {
                binding.pathPanel.visibility = View.GONE
            }
        }
    }

    fun isCheckedDepartureAlarmSwitch():Boolean{
        return binding.departureAlarmSwitch.isChecked
    }

    private var customVal = 5
    private var customIndex = 0 //0 -> 분 1 -> 시간 -> 2 -> 일 3 -> 주
    private fun initAlarmTimeListener(){

        val alarmRadioGroup = binding.alarmRadioGroup

        val alarmTypeMap = mapOf(
            R.id.alarm_radio_button_null to NULL,
            R.id.checkbox_ontime to AlarmInfo.AlarmType.START_TIME,
            R.id.checkbox_10_min_ago to AlarmInfo.AlarmType.TEN_MIN_AGO,
            R.id.checkbox_hourago to HOUR_AGO,
            R.id.checkbox_dayago to DAY_AGO,
            R.id.checkbox_custom to CUSTOM
        )

        alarmRadioGroup.setOnCheckedChangeListener { _, id ->
            binding.alarmRadioGroupLayout.visibility = View.GONE
            presenter.setAlarmInfo(alarmTypeMap[id]!!)
            binding.alarmTimeTextview.background = ContextCompat.getDrawable(this, R.color.bg_white)
            showAlarmTimeText()
        }

        binding.alarmTimeLayout.setOnClickListener {

            val isVisible = binding.alarmRadioGroupLayout.visibility == View.VISIBLE
            if (isVisible) {
                binding.alarmTimeTextview.background = ContextCompat.getDrawable(this, R.color.bg_white)
                showAlarmTimeText()
            } else {
                binding.alarmTimeTextview.background = ContextCompat.getDrawable(this, R.drawable.ed_text)
            }
            binding.alarmRadioGroupLayout.visibility = if (!isVisible) View.VISIBLE else View.GONE
        }

        binding.charpicker.maxValue = 3
        binding.charpicker.minValue = 0
        binding.charpicker.displayedValues = arrayOf<String>(
            "분", "시간", "일", "주"
        )
        binding.charpicker.wrapSelectorWheel = false

        binding.customAlarmBtn.setOnClickListener{
            binding.customAlarmLayout.visibility = View.VISIBLE
            setNumPicker(customIndex)
            binding.numpicker.value = customVal
            binding.charpicker.value = customIndex

            showCustomCheckBox()

            binding.numpickerLayout.visibility = View.VISIBLE
            binding.customAlarmBtn.isClickable = true
            presenter.setAlarmInfo(CUSTOM)
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
            presenter.setAlarmInfo(CUSTOM)
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

    fun showTimeRequired(timeRequired: String, timeInfo: TimeInfo){
        binding.timeRequieredTextview.post {
            binding.timeRequieredTextview.text = timeRequired
        }
        binding.preSrcTimeTextview.post {
            binding.preSrcTimeTextview.text = timeInfo.toString()
        }
    }

    private fun handleRadioButtonSelection(){
        if (presenter.getSchedule().meansType != MeansType.NULL && presenter.getSchedule().dstAddress != null) {
            presenter.calTimeRequired()
        } else {
            Toast.makeText(baseContext, "출발지 도착지 이동 수단을 모두 입력 하세요", Toast.LENGTH_SHORT)
                .show()
            binding.meansRadiogroup.clearCheck()
            presenter.getSchedule().meansType = MeansType.NULL
        }
    }

    private fun initTimeRequiredListener(){
        binding.meansRadiogroup.setOnCheckedChangeListener{ _, button ->
            when (button) {
                R.id.radiobutton_walk -> {
                    presenter.getSchedule().meansType = MeansType.WALK
                    handleRadioButtonSelection()
                }
                R.id.radiobutton_public -> {
                    presenter.getSchedule().meansType = MeansType.PUBLIC
                    handleRadioButtonSelection()
                }
                R.id.radiobutton_car -> {
                    presenter.getSchedule().meansType = MeansType.CAR
                    handleRadioButtonSelection()
                }
            }
        }

        fun setAddressClickListener(view: View, isSource: Boolean) {
            view.setOnClickListener {
                val logTag = if (isSource) "srcAddressText" else "dstAddressText"
                Log.d(logTag, "click")
                presenter.loadAddressInfo(isSource, applicationContext)
            }
        }
        setAddressClickListener(binding.tvSrcAddress, true)
        setAddressClickListener(binding.dstAddressText, false)
    }
    fun launchAutocompleteGeocodingActivity(isSource: Boolean){
        isSrcCallBack = isSource
        val intent = Intent(this, ProgrammaticAutocompleteGeocodingActivity::class.java)
        getResult.launch(intent)
    }

    private fun initSaveCancelListener(){
        binding.saveBtn.setOnClickListener(View.OnClickListener {
            presenter.saveSchedule(applicationContext)
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

    fun showUpdatedMarker(
        markerInfo: MarkerInfo
    ){

        val markerOptions = MarkerOptions()
            .position(markerInfo.position)
            .title(markerInfo.title)
            .draggable(true)
        val newMarker = mMap.addMarker(markerOptions)
        newMarker?.showInfoWindow()
        if (markerInfo.isSource){
            srcMarker?.remove()
            srcMarker = newMarker
        }else {
            dstMarker?.remove()
            dstMarker = newMarker
        }


        val cameraUpdate: CameraUpdate = if (srcMarker == null || dstMarker == null){
            Log.d("cameraUpdate", "newLatLngZoom")
            CameraUpdateFactory.newLatLngZoom(newMarker!!.position, 15.0f)
        } else{
            Log.d("cameraUpdate", "newLatLngBounds")
            val zoomToFitBuilder = LatLngBounds.Builder()
            zoomToFitBuilder.include(srcMarker!!.position)
            zoomToFitBuilder.include(dstMarker!!.position)
            val zoomToFitBound = zoomToFitBuilder.build()
            val width = resources.displayMetrics.widthPixels
            CameraUpdateFactory.newLatLngBounds(zoomToFitBound, width, width, width/4)
        }

        mMap.animateCamera(cameraUpdate)
    }
    private fun checkLocationServicesStatus(): Boolean {
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        return (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
    }
    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        presenter.requestLocationUpdate(applicationContext, checkLocationServicesStatus())

        mMap.uiSettings.isMyLocationButtonEnabled = true
        mMap.setOnMapClickListener {
            Log.d("googleMap Click", "onMapClick")
        }
    }
    fun showLocationPermissionsSnackBar(){
        Snackbar.make(
            binding.map, "위치 접근 권한이 필요합니다.",
            Snackbar.LENGTH_INDEFINITE
        ).setAction("확인") {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS,
                Permission.PERMISSIONS_REQUEST_CODE
            )
        }.show()
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
                presenter.requestLocationUpdate(applicationContext, checkLocationServicesStatus())
            } else {
                showLocationPermissionsSnackBar()
            }
        }
    }

    fun readTitle(): String{
        return binding.title.text.toString()
    }
    fun readMemo(): String{
        return binding.title.text.toString()
    }
}