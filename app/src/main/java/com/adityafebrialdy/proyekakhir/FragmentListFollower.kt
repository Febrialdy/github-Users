package com.adityafebrialdy.proyekakhir

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
import com.adityafebrialdy.proyekakhir.Adapter.UserAdapter
import com.adityafebrialdy.proyekakhir.Model.Profile
import com.adityafebrialdy.proyekakhir.ViewModel.ListFollowViewModel
import kotlinx.android.synthetic.main.fragment_list_follower.*

/**
 * A simple [Fragment] subclass.
 */
class FragmentListFollower : Fragment() {
  companion object{
    const val EXTRA_URL = "EXTRA_URL"
  }

  private lateinit var listFollowerViewModel: ListFollowViewModel
  private lateinit var adapter: UserAdapter

  private lateinit var myView: View
  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
    myView = inflater.inflate(R.layout.fragment_list_follower, container, false)
    return myView
  }

  @RequiresApi(Build.VERSION_CODES.M)
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    showLoading(true)
    val bundle = arguments
    if(bundle != null){
      if(bundle.containsKey(EXTRA_URL)){
        adapter = UserAdapter()
        adapter.notifyDataSetChanged()

        val url = bundle.getString(EXTRA_URL)
        listFollowerViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(ListFollowViewModel::class.java)
        listFollowerViewModel.setProfile(url!!, requireContext())

        rv_listFollwers.layoutManager = LinearLayoutManager(context)
        rv_listFollwers.adapter = adapter
        adapter.setOnItemClickCallBack(object: UserAdapter.OnItemClickCallback{
          override fun onItemClicked(data: Profile) {
            Toast.makeText(context, data.username, Toast.LENGTH_LONG).show()
          }
        })

        listFollowerViewModel.getProfiles().observe(viewLifecycleOwner, Observer { user ->
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
