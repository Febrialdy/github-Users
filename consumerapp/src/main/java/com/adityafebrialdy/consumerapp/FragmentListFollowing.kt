package com.adityafebrialdy.consumerapp

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.adityafebrialdy.consumerapp.Adapter.UserAdapter
import com.adityafebrialdy.consumerapp.Model.Profile
import com.adityafebrialdy.consumerapp.ViewModel.ListFollowViewModel
import kotlinx.android.synthetic.main.fragment_list_following.*

/**
 * A simple [Fragment] subclass.
 */
class FragmentListFollowing : Fragment() {
  companion object{
    const val EXTAR_URL = "EXTRA_URL"
  }

  private lateinit var listFollowingViewModel: ListFollowViewModel
  private lateinit var adapter: UserAdapter

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_list_following, container, false)
  }

  @RequiresApi(Build.VERSION_CODES.M)
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    showLoading(true)
    val bundle = arguments
    if(bundle != null){
      if(bundle.containsKey(EXTAR_URL)){
        adapter = UserAdapter()
        adapter.notifyDataSetChanged()

        val url = bundle.getString(EXTAR_URL)
        listFollowingViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(ListFollowViewModel::class.java)
        listFollowingViewModel.setProfile(url!!, requireContext())


        rv_listFollowing.layoutManager = LinearLayoutManager(context)
        rv_listFollowing.adapter = adapter
        adapter.setOnItemClickCallBack(object: UserAdapter.OnItemClickCallback{
          override fun onItemClicked(data: Profile) {
            Toast.makeText(context, data.username, Toast.LENGTH_LONG).show()
          }
        })

        listFollowingViewModel.getProfiles().observe(viewLifecycleOwner, Observer { user ->
          if(user != null){
            adapter.setData(user)
            showLoading(false)
          }
        })
      }
    }
  }

  private fun showLoading(state: Boolean){
    if(state){
      progressBar.visibility = View.VISIBLE
    }
    else{
      progressBar.visibility = View.INVISIBLE
    }
  }
}
