package com.example.adaproject.adapters

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.adaproject.MainActivity
import com.example.adaproject.R
import com.example.adaproject.models.response.Video
import com.example.adaproject.models.response.VideoResSuccess
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.util.HashMap

class VideoItemAdapter(private val dataSet: ArrayList<Video>, iVideoItemListener: IVideoItemListener) : RecyclerView.Adapter<VideoItemAdapter.VideosViewHolder>()
{
    val mIVideoItemListener:IVideoItemListener

    init {
        this.mIVideoItemListener = iVideoItemListener
    }

    class VideosViewHolder(view: View, iVideoItemListener: IVideoItemListener) : RecyclerView.ViewHolder(view), View.OnClickListener {
        val text_item_week: TextView
        val text_item_status: TextView
        val text_item_date: TextView
        val reCaptureBtn: Button
        val iVideoItemListener: IVideoItemListener
        val pending:Drawable
        val success:Drawable
        val decline:Drawable

        init {
            // Define click listener for the ViewHolder's View.
            text_item_week = view.findViewById(R.id.text_item_week)
            text_item_status = view.findViewById(R.id.text_item_status)
            text_item_date = view.findViewById(R.id.text_item_date)
            reCaptureBtn = view.findViewById(R.id.reCaptureBtn)
            pending = MainActivity.getContext().getDrawable(R.drawable.pending_icon)!!
            success = MainActivity.getContext().getDrawable(R.drawable.available_icon)!!
            decline = MainActivity.getContext().getDrawable(R.drawable.unavailable_icon)!!

            this.iVideoItemListener = iVideoItemListener
            reCaptureBtn.setOnClickListener(this)
            //view.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            iVideoItemListener.onRecapture(adapterPosition)
        }
    }

    interface IVideoItemListener{
        fun onRecapture(position: Int)
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideosViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.video_item, parent, false)

        return VideosViewHolder(view, mIVideoItemListener)
    }

    override fun onBindViewHolder(holder: VideosViewHolder, position: Int) {

        val video:Video = dataSet[position]
        holder.text_item_date.setText(video.updated)
        holder.text_item_status.setText(video.status)


        holder.text_item_week.setText(video.weeks.toString()+". Hafta")

        if(video.status.equals("INAPPROPRIATE")) {
            holder.text_item_status.setCompoundDrawablesRelative(holder.decline,null,null,null)
            holder.reCaptureBtn.isVisible = true
        }
        else if(video.status.equals("PROCESSING"))
        {
            holder.text_item_status.setCompoundDrawablesWithIntrinsicBounds(holder.pending,null,null,null)
            holder.reCaptureBtn.isVisible = false
        }
        else
        {
            holder.text_item_status.setCompoundDrawablesRelative(holder.success,null,null,null)
            holder.reCaptureBtn.isVisible = false
        }
    }

    override fun getItemCount() = dataSet.size

}