import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.ImageView
import com.ddmyb.shalendar.R

class ToggleAnimation {
    companion object {
        fun toggleArrow(view: View, isExpanded: Boolean): Boolean {
            return if (isExpanded) {
                view.animate().setDuration(200).rotation(180f)
                true
            } else {
                view.animate().setDuration(200).rotation(0f)
                false
            }
        }
        fun expand(view: View) {
            if(view.id == R.id.nd_mycalendar_layout){
                view.visibility = View.VISIBLE
            }
            else {
                view.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                val actualHeight = view.measuredHeight

                view.layoutParams.height = 0
                view.visibility = View.VISIBLE

                val animation = object : Animation() {
                    override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
                        view.layoutParams.height =
                            if (interpolatedTime == 1f) ViewGroup.LayoutParams.WRAP_CONTENT
                            else (actualHeight * interpolatedTime).toInt()
                        view.requestLayout()//현재 뷰와 자식 뷰 크기와 위치 변경됐음을 알림
                        /*if(view.id == R.id.nd_mycalendar_layout){
                        val imageView = view.findViewById<ImageView>(R.id.nd_calendar_iv)
                        val imageViewLayoutParams = imageView.layoutParams
                        imageViewLayoutParams.height = (actualHeight * interpolatedTime).toInt()
                        imageView.requestLayout()
                    }*/
                    }
                }
                animation.duration = (actualHeight / view.context.resources.displayMetrics.density).toLong()
                view.startAnimation(animation)
            }
        }


        fun collapse(view: View) {
            if(view.id == R.id.nd_mycalendar_layout){
                view.visibility = View.GONE
            }
            else{
                val actualHeight = view.measuredHeight
                val animation = object : Animation() {
                    override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
                        if (interpolatedTime == 1f) {
                            view.visibility = View.GONE
                        } else {
                            view.layoutParams.height = (actualHeight - (actualHeight * interpolatedTime)).toInt()
                            view.requestLayout()
                        }
                    }
                }

                animation.duration = (actualHeight / view.context.resources.displayMetrics.density).toLong()
                view.startAnimation(animation)
            }
        }
    }
}