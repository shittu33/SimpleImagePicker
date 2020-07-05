package com.example.simpleimagepicker

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.simpleimagepicker.GalleryAdapter.ImageHolder
import java.io.File

class GalleryAdapter(clickListener: ClickListener) :
    ListAdapter<String, ImageHolder>(DIFF_CALLBACK) {
    var mClickListener: ClickListener? = null

    init {
        mClickListener = clickListener
    }

    private var mImageHolder: ImageHolder? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageHolder {
        val item_view =
            LayoutInflater.from(parent.context).inflate(R.layout.image_item, parent, false)
        mImageHolder = ImageHolder(item_view)
        return mImageHolder!!
    }

    override fun onBindViewHolder(holder: ImageHolder, position: Int) {
        holder.populateData(getItem(position))
    }

    fun getImage(pos: Int): String? {
        return getItem(pos)
    }

    inner class ImageHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        private val img: ImageView
        private var mItemView: View? = null
        fun populateData(image_path: String?) {
            Glide.with(mItemView!!.getContext())
                .load(Uri.fromFile(File(image_path)))
                .thumbnail(0.6f)
                .into(img)
            //            img.setImageURI(Uri.fromFile(new File(image_path)));
        }

        init {
            this.mItemView = itemView
            img = itemView.findViewById(R.id.img)
            itemView.setOnClickListener { v ->
                val currentPos = adapterPosition
                mClickListener?.onClick(v, getImage(currentPos), currentPos)
            }
        }
    }


    interface ClickListener {
        fun onClick(v: View?, path: String?, pos: Int)
    }

    companion object {
        private val DIFF_CALLBACK: DiffUtil.ItemCallback<String> =
            object : DiffUtil.ItemCallback<String>() {
                override fun areItemsTheSame(
                    oldItem: String,
                    newItem: String
                ): Boolean {
                    return oldItem == newItem
                }

                override fun areContentsTheSame(
                    oldItem: String,
                    newItem: String
                ): Boolean {
                    return oldItem == newItem
                }
            }
    }

}