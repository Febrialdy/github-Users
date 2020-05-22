package com.adityafebrialdy.consumerapp.ViewModel

import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.adityafebrialdy.consumerapp.Model.Profile
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import org.json.JSONArray

class ListFollowViewModel: ViewModel() {
  val listProfile = MutableLiveData<ArrayList<Profile>>()
  private var listUser = ArrayList<Profile>()
  @RequiresApi(Build.VERSION_CODES.M)
  fun setProfile (url: String, context: Context): Boolean{
    val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val token = "05803c74d257c89ce0a7fdb713a3f3760ba346c9"
    val client = AsyncHttpClient()
    client.addHeader("Authorization", "token $token")
    client.addHeader("User-Agent", "request")
    if(cm.activeNetwork != null){
      client.get(url, object: AsyncHttpResponseHandler(){
        override fun onSuccess(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?) {
          try{
            val result = String(responseBody!!)
            val responseArray = JSONArray(result)

            for(i in 0 until responseArray.length()){
              val responseObject =  responseArray.getJSONObject(i)
              val avatar = responseObject.getString("avatar_url")
              val username = responseObject.getString("login")
              val myUrl = responseObject.getString("url")

              val data = Profile(username, avatar, myUrl)
              listUser.add(data)
            }
            listProfile.postValue(listUser)
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
    else return false
  }

  fun getProfiles(): LiveData<ArrayList<Profile>> {
    return listProfile
  }
}