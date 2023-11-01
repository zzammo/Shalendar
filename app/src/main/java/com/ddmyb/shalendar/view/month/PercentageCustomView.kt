package com.ddmyb.shalendar.view.month

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.constraintlayout.widget.Guideline
import com.ddmyb.shalendar.R
import com.ddmyb.shalendar.databinding.CustomViewPercentageBinding

class PercentageCustomView: ConstraintLayout {

    val binding: CustomViewPercentageBinding by lazy {
        CustomViewPercentageBinding.bind(
            LayoutInflater.from(context).inflate(R.layout.custom_view_percentage, this, false)
        )
    }

    constructor(context: Context): super(context) {
        initView()
    }
    constructor(context: Context, attrs: AttributeSet): super(context, attrs) {
        initView()
    }

    private fun initView() {
        addView(binding.root)
    }

    fun addView(view: View, startPercentage: Float) {

        val guideline = Guideline(context)
        guideline.id = View.generateViewId()

        val glp = ConstraintLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0)
        glp.orientation = ConstraintSet.HORIZONTAL
        glp.guidePercent = startPercentage
        guideline.layoutParams = glp

        binding.container.addView(guideline)



        view.id = View.generateViewId()

        val cs = ConstraintSet()
        cs.clone(binding.container)
        cs.connect(view.id, ConstraintSet.TOP, guideline.id, ConstraintSet.BOTTOM)

        val lp = view.layoutParams
        lp.width = LayoutParams.MATCH_PARENT
        view.layoutParams = lp

        binding.container.addView(view)

        cs.applyTo(binding.container)
    }

    fun addView(view: View, startPercentage: Float, heightPercentage: Float) {

        val guideline = Guideline(context)
        guideline.id = View.generateViewId()

        binding.container.addView(guideline)

        val glp = ConstraintLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0)
        glp.orientation = ConstraintSet.HORIZONTAL
        glp.guidePercent = startPercentage
        guideline.layoutParams = glp



        view.id = View.generateViewId()

        binding.container.addView(view)

        val lp = view.layoutParams
        lp.width = LayoutParams.MATCH_PARENT
        lp.height = 0
        view.layoutParams = lp

        val cs = ConstraintSet()
        cs.clone(binding.container)
        cs.connect(view.id, ConstraintSet.TOP, guideline.id, ConstraintSet.BOTTOM)
//        cs.connect(view.id, ConstraintSet.TOP, binding.container.id, ConstraintSet.TOP)
        cs.constrainPercentHeight(view.id, heightPercentage)

        cs.applyTo(binding.container)
    }
}