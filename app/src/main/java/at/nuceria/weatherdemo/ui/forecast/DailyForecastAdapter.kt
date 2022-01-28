package at.nuceria.weatherdemo.ui.forecast

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import at.nuceria.weatherdemo.R
import at.nuceria.weatherdemo.data.model.DailyWeatherData
import at.nuceria.weatherdemo.databinding.DailyForecastBinding
import at.nuceria.weatherdemo.util.epochToLocalTime
import at.nuceria.weatherdemo.util.getDayIcon
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import kotlin.math.roundToInt

class DailyForecastAdapter :
    ListAdapter<DailyWeatherData, DailyWeatherDataViewHolder>(diffCallback),
    DailyWeatherDataViewHolder.DialectInteractionListener {

    private var parentWidth = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyWeatherDataViewHolder {
        val itemBinding =
            DailyForecastBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        parentWidth = parent.width
        return DailyWeatherDataViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: DailyWeatherDataViewHolder, position: Int) {
        val item = currentList[position]
        holder.bind(item)
    }

    override fun onItemClick(itemPosition: Int, viewHolder: DailyWeatherDataViewHolder) {
        // TODO display item select, show details outside of list
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
    private val itemBinding: DailyForecastBinding,
//    var interactionListener: DialectInteractionListener
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

        itemBinding.day.text = item.timeStamp.epochToLocalTime(item.timezoneId)?.dayOfWeek()?.asText

    }

    interface DialectInteractionListener {
        fun onItemClick(
            itemPosition: Int,
            viewHolder: DailyWeatherDataViewHolder
        )
    }
}

