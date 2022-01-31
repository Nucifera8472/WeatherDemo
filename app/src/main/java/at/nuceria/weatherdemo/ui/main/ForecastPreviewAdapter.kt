package at.nuceria.weatherdemo.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import at.nuceria.weatherdemo.R
import at.nuceria.weatherdemo.data.local.getDayIcon
import at.nuceria.weatherdemo.data.model.DailyWeatherData
import at.nuceria.weatherdemo.databinding.ItemForecastBinding
import at.nuceria.weatherdemo.util.epochToLocalTime
import at.nuceria.weatherdemo.util.to24hTime
import at.nuceria.weatherdemo.util.toLongDateString
import java.util.*
import kotlin.math.roundToInt

class ForecastPreviewAdapter(private val itemClickListener: ItemClickListener) :
    ListAdapter<DailyWeatherData, ForecastPreviewAdapter.DailyWeatherDataViewHolder>(diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyWeatherDataViewHolder {
        val itemBinding =
            ItemForecastBinding.inflate(LayoutInflater.from(parent.context), parent, false)

//        val screenWidth = Resources.getSystem().displayMetrics.widthPixels
//        itemBinding.root.layoutParams.width = screenWidth / 2;

        return DailyWeatherDataViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: DailyWeatherDataViewHolder, position: Int) {
        val item = currentList[position]
        holder.bind(item)

        holder.itemView.setOnClickListener {
            // show details for this tile
            itemClickListener.onForecastPreviewItemClicked(position)
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

    class DailyWeatherDataViewHolder(val itemBinding: ItemForecastBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(item: DailyWeatherData) {
            itemBinding.currentWeatherIcon.setImageDrawable(
                ContextCompat.getDrawable(itemView.context, item.condition.getDayIcon())
            )

            itemBinding.temperatureDetails.minTemperature.text = String.format(
                itemView.context.getString(R.string._c),
                item.minTemperature.roundToInt()
            )
            itemBinding.temperatureDetails.maxTemperature.text = String.format(
                itemView.context.getString(R.string._c),
                item.maxTemperature.roundToInt()
            )

            val timeStamp = item.timeStamp.epochToLocalTime(item.timezoneId)
            itemBinding.day.text = timeStamp?.dayOfWeek()?.asText
            itemBinding.date.text = timeStamp?.toLongDateString()

            itemBinding.precipitationDetails.precipitationProbability.text =
                itemView.context.getString(
                    R.string.percent,
                    (item.precipitationProbability * 100).roundToInt()
                        .toString()
                )
            itemBinding.precipitationDetails.precipitationAmount.text = itemView.context.getString(
                R.string._mm,
                if (item.precipitationAmount == 0f) "0"
                else String.format("%.1f", item.precipitationAmount)
            )
            itemBinding.currentWeatherIcon.transitionName = "weatherIconTransition_$adapterPosition"
        }

    }


    interface ItemClickListener {
        fun onForecastPreviewItemClicked(position: Int)
    }

}



