package com.adityafebrialdy.proyekakhir.ViewModel

import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.adityafebrialdy.proyekakhir.Model.Profile
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import org.json.JSONObject

class MainVM: ViewModel() {
  val listProfile = MutableLiveData<ArrayList<Profile>>()
  private var listUser = ArrayList<String>()
  @RequiresApi(Build.VERSION_CODES.M)
  fun setUsers (users: String, context: Context): Boolean{
    val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val listItems =ArrayList<Profile>()
    val token = "05803c74d257c89ce0a7fdb713a3f3760ba346c9"
    val url = "https://api.github.com/search/users?q=$users"
    val client = AsyncHttpClient()
    client.addHeader("Authorization", "token $token")
    client.addHeader("User-Agent", "request")
    if(cm.activeNetwork != null){
      client.get(url, object: AsyncHttpResponseHandler(){
        override fun onSuccess(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?) {
          try{
            val result = String(responseBody!!)
            val responseObject = JSONObject(result)
            val list = responseObject.getJSONArray("items")
            for(i in 0 until list.length()){
              val user =list.getJSONObject(i)
              val profileUser = user.getString("url")
              val username = user.getString("login")
              val avatar = user.getString("avatar_url")
              val url = user.getString("url")
              val item = Profile(username, avatar, url)
              listItems.add(item)
              listUser.add(profileUser)
            }
            listProfile.postValue(listItems)
          }catch (e: Exception){
            Log.d("Exception", e.message.toString())
          }
        }

        override fun onFailure(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?, error: Throwable?) {
          Log.d("onFailure", error!!.message.toString())
        }
      })
      return true
    }
    else{
      return false
    }
  }

  fun getProfiles(): LiveData<ArrayList<Profile>> {
    return listProfile
  }
}