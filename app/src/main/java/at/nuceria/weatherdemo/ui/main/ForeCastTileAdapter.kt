package at.nuceria.weatherdemo.ui.main

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.marginStart
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import at.nuceria.weatherdemo.R
import at.nuceria.weatherdemo.data.model.DailyWeatherData
import at.nuceria.weatherdemo.databinding.ItemForecastTileBinding
import at.nuceria.weatherdemo.util.epochToLocalTime
import at.nuceria.weatherdemo.data.local.getDayIcon
import kotlin.math.roundToInt

class ForeCastTileAdapter() :
    ListAdapter<DailyWeatherData, DailyWeatherDataViewHolder>(diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyWeatherDataViewHolder {
        val itemBinding =
            ItemForecastTileBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        val screenWidth = Resources.getSystem().displayMetrics.widthPixels
        itemBinding.root.layoutParams.width = screenWidth / 3;

        return DailyWeatherDataViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: DailyWeatherDataViewHolder, position: Int) {
        val item = currentList[position]
        holder.bind(item)

        holder.itemView.setOnClickListener {
            // show details for this tile
        }
    }
    companion object {
        /**
         * This diff callback informs the PagedListAdapter how to compute list differences when new
         * PagedLists arrive.
         */
        private val diffCallback = object : DiffUtil.ItemCallback<DailyWeatherData>() {
            override fun areItemsTheSame(
                oldItem: DailyWeatherData,
                newItem: DailyWeatherData
            ): Boolean =
                oldItem.timeStamp == oldItem.timeStamp

            /**
             * Note that in kotlin, == checking on data classes compares all contents
             */
            override fun areContentsTheSame(
                oldItem: DailyWeatherData,
                newItem: DailyWeatherData
            ): Boolean =
                oldItem == newItem
        }
    }

}


class DailyWeatherDataViewHolder(
    private val itemBinding: ItemForecastTileBinding,
) :
    RecyclerView.ViewHolder(itemBinding.root) {

    fun bind(item: DailyWeatherData) {
        itemBinding.currentWeatherIcon.setImageDrawable(
            ContextCompat.getDrawable(itemView.context, item.condition.getDayIcon())
        )

        itemBinding.temperature.text = String.format(
            itemView.context.getString(R.string.daily_temp),
            item.minTemperature.roundToInt(),
            item.maxTemperature.roundToInt()
        )
        itemBinding.day.text = item.timeStamp.epochToLocalTime(item.timezoneId)?.dayOfWeek()?.asShortText
    }
}

