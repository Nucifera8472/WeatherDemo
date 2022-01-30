package at.nuceria.weatherdemo.ui.forecastdetail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import at.nuceria.weatherdemo.databinding.ItemGenericWeatherDatapointBinding

class MiniWeatherItemAdapter :
    ListAdapter<MiniWeatherItemAdapter.WeatherItem, MiniWeatherItemAdapter.MiniWeatherDataViewHolder>(
        diffCallback
    ) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MiniWeatherDataViewHolder {
        val itemBinding =
            ItemGenericWeatherDatapointBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return MiniWeatherDataViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: MiniWeatherDataViewHolder, position: Int) {
        val item = currentList[position]
        holder.bind(item)
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<WeatherItem>() {
            override fun areItemsTheSame(oldItem: WeatherItem, newItem: WeatherItem): Boolean =
                oldItem == oldItem

            override fun areContentsTheSame(oldItem: WeatherItem, newItem: WeatherItem): Boolean =
                oldItem == newItem
        }
    }

    data class WeatherItem(@DrawableRes val iconRes: Int, val title: String, val value: String)

    class MiniWeatherDataViewHolder(private val itemBinding: ItemGenericWeatherDatapointBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(item: WeatherItem) {
            itemBinding.icon.setImageDrawable(
                ContextCompat.getDrawable(itemView.context, item.iconRes)
            )
            itemBinding.title.text = item.title
            itemBinding.value.text = item.value
        }
    }
}


