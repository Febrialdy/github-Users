package com.adityafebrialdy.consumerapp.Adapter

import android.content.Context
import android.os.Bundle
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.adityafebrialdy.consumerapp.FragmentListFollower
import com.adityafebrialdy.consumerapp.FragmentListFollowing
import com.adityafebrialdy.consumerapp.R

class SectionPagerAdapter(private val context: Context, fm: FragmentManager): FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    var url: String = ""
    var listFollowing: String = ""
    fun getUrl(url: String){
        this.url = url
    }

    fun getListFollowing(listFollowing: String){
        this.listFollowing = listFollowing
    }

    override fun getItem(position: Int): Fragment {
        var fragment: Fragment? = null
        when(position){
            0 -> {
                fragment = FragmentListFollower()
                val mBundle = Bundle()
                mBundle.putString(FragmentListFollower.EXTRA_URL, url)
                fragment.arguments = mBundle
            }
            else -> {
                fragment = FragmentListFollower()
                val mBundle = Bundle()
                mBundle.putString(FragmentListFollowing.EXTAR_URL, listFollowing)
                fragment.arguments = mBundle
            }
        }
        return fragment as Fragment
    }

    @Nullable
    override fun getPageTitle(position: Int): CharSequence? {
        when(position){
            0 -> return "${context.resources.getString(R.string.follower)}"
            else -> return "${context.resources.getString(R.string.following)}"
        }
    }

    override fun getCount(): Int {
        return 2
    }
}