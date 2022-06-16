package it.stez78.asai

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.RecyclerView

class CustomAdapter(private val mList: List<Int>, val context: Context, var onItemClick: ((Int) -> Unit)) : RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.adapter_image_element, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itemDrawableId = mList[position]
        val bitmap = ContextCompat.getDrawable(context, itemDrawableId)?.toBitmap();
        holder.imageView.setImageBitmap(bitmap);
        holder.imageView.setOnClickListener{onItemClick(itemDrawableId)}
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val imageView: ImageView = itemView.findViewById(R.id.anteprima)
    }
}