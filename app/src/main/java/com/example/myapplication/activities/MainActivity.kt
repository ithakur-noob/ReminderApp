package com.example.myapplication.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.fragments.CompletedTasksFragment
import com.example.myapplication.fragments.PendingTasksFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        // Handle the splash screen transition.
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        splashScreen.setKeepOnScreenCondition { false }
        binding.viewPager.adapter = TaskPagerAdapter(supportFragmentManager)
        selectTab(0)
//        Handle Clicks
        binding.tvPending.setOnClickListener { selectTab(0) }
        binding.tvComplete.setOnClickListener { selectTab(1) }

    }

    // TaskPagerAdapter.kt
    inner class TaskPagerAdapter(fm: FragmentManager) :
        FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> PendingTasksFragment()
                else -> CompletedTasksFragment()
            }
        }

        override fun getCount(): Int = 2 // Two tabs: Pending and Completed
    }

    private fun selectTab(position: Int) {
        if (position == 0) {
            binding.tvPending.background =
                ContextCompat.getDrawable(this@MainActivity, R.drawable.bg_seleted_tab)
            binding.tvComplete.background =
                ContextCompat.getDrawable(this@MainActivity, R.drawable.bg_unseleted_tab)
            binding.viewPager.currentItem = 0
        } else {
            binding.tvComplete.background =
                ContextCompat.getDrawable(this@MainActivity, R.drawable.bg_seleted_tab)
            binding.tvPending.background =
                ContextCompat.getDrawable(this@MainActivity, R.drawable.bg_unseleted_tab)
            binding.viewPager.currentItem = 1
        }
    }
}


