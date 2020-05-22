package com.adityafebrialdy.consumerapp.Adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.adityafebrialdy.consumerapp.Model.Profile
import com.adityafebrialdy.consumerapp.Model.ProfileSQL
import com.adityafebrialdy.consumerapp.R
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.profile_items.view.*

class FavoriteAdapter(private val activity: Activity): RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder>() {

  var listFavorite = ArrayList<ProfileSQL>()
    set(listFavorite){
      this.listFavorite.clear()
      this.listFavorite.addAll(listFavorite)
      notifyDataSetChanged()
    }

  fun addItem(note: ProfileSQL){
    this.listFavorite.add(note)
    notifyItemInserted(this.listFavorite.size)
  }

  fun updateItem(pos: Int, note: ProfileSQL){
    this.listFavorite[pos] = note
    notifyItemChanged(pos, note)
  }

  fun removeItem(pos: Int){
    this.listFavorite.removeAt(pos)
    notifyItemRemoved(pos)
    notifyItemRangeChanged(pos, this.listFavorite.size)
  }

  private lateinit var onItemClickCallback: OnItemClickCallback

  fun setOnItemClickCallBack(onItemClickCallback: OnItemClickCallback){
    this.onItemClickCallback = onItemClickCallback
  }

  interface OnItemClickCallback {
    fun onItemClicked(data: ProfileSQL)
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
    val view = LayoutInflater.from(parent.context).inflate(R.layout.profile_items, parent, false)
    return FavoriteViewHolder(view)
  }

  override fun getItemCount(): Int = this.listFavorite.size

  override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
    holder.bind(listFavorite[position])
    holder.itemView.setOnClickListener{ onItemClickCallback.onItemClicked(listFavorite[position])}
  }

  class FavoriteViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    fun bind(item: ProfileSQL){
      with(itemView){
        Glide.with(itemView.context)
          .load(item.avatar)
          .into(avatar_user)
        name_user.text = item.username
      }
    }
  }
}