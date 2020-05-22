package com.adityafebrialdy.proyekakhir.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.adityafebrialdy.proyekakhir.Model.Profile
import com.adityafebrialdy.proyekakhir.R
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.profile_items.view.*

class UserAdapter: RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    private val mData = ArrayList<Profile>()

    fun setData(items: ArrayList<Profile>){
        mData.clear()
        mData.addAll(items)
        notifyDataSetChanged()
    }

    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallBack(onItemClickCallback: OnItemClickCallback){
        this.onItemClickCallback = onItemClickCallback
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: Profile)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): UserViewHolder {
        val mView = LayoutInflater.from(viewGroup.context).inflate(R.layout.profile_items, viewGroup, false)
        return UserViewHolder(mView)
    }

    override fun getItemCount(): Int = mData.size

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(mData[position])
        holder.itemView.setOnClickListener { onItemClickCallback.onItemClicked(mData[position]) }
    }

    class UserViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bind(item: Profile){
            with(itemView){
                Glide.with(itemView.context)
                    .load(item.avatar)
                    .into(avatar_user)
                name_user.text = item.username
            }
        }
    }
}