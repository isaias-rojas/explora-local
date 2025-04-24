package com.example.exploralocal.presentation.list


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.exploralocal.R
import com.example.exploralocal.domain.model.Place
import java.io.File

class PlaceAdapter(
    private val listener: PlaceClickListener
) : ListAdapter<Place, PlaceAdapter.PlaceViewHolder>(PlaceDiffCallback()) {

    interface PlaceClickListener {
        fun onPlaceClick(place: Place)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_place, parent, false)
        return PlaceViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class PlaceViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        private val imageViewPlace: ImageView = itemView.findViewById(R.id.image_view_place)
        private val textViewName: TextView = itemView.findViewById(R.id.text_view_name)
        private val textViewDescription: TextView = itemView.findViewById(R.id.text_view_description)
        private val ratingBar: RatingBar = itemView.findViewById(R.id.rating_bar)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition  // Use this instead of bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onPlaceClick(getItem(position))
                }
            }
        }

        fun bind(place: Place) {
            textViewName.text = place.name
            textViewDescription.text = place.description
            ratingBar.rating = place.rating

            // Load image if available
            place.photoPath?.let { path ->
                val photoFile = File(path)
                if (photoFile.exists()) {
                    // For now, just use a placeholder
                    // We'll need to add proper image loading library later
                    imageViewPlace.setImageResource(R.drawable.placeholder_image)
                } else {
                    imageViewPlace.setImageResource(R.drawable.placeholder_image)
                }
            } ?: run {
                imageViewPlace.setImageResource(R.drawable.placeholder_image)
            }
        }
    }

    class PlaceDiffCallback : DiffUtil.ItemCallback<Place>() {
        override fun areItemsTheSame(oldItem: Place, newItem: Place): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Place, newItem: Place): Boolean {
            return oldItem == newItem
        }
    }
}