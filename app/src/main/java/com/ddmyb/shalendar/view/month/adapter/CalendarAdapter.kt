//package com.ddmyb.shalendar.view.month.adapter
//
//import androidx.fragment.app.FragmentActivity
//import androidx.viewpager2.adapter.FragmentStateAdapter
//
//class CalendarAdapter(fragmentActivity: FragmentActivity) :
//    FragmentStateAdapter(fragmentActivity) {
//    private lateinit var viewPagerAdapter: ViewPagerAdapter
//    val fragments = listOf<Fragment>(HomeFragment(), EditFragment(), LikeFragment(), UserFragment())
//
//    override fun getItemCount(): Int {
//        return fragments.size
//    }
//
//    override fun createFragment(position: Int): Fragment {
//        return fragments[position]
//    }
//}