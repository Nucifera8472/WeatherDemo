package at.nuceria.weatherdemo.ui.forecastdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.SharedElementCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.transition.Transition
import androidx.transition.TransitionInflater
import androidx.transition.TransitionListenerAdapter
import androidx.transition.TransitionSet
import at.nuceria.weatherdemo.R
import at.nuceria.weatherdemo.data.Resource
import at.nuceria.weatherdemo.data.local.getDayIcon
import at.nuceria.weatherdemo.data.model.DailyWeatherData
import at.nuceria.weatherdemo.data.model.WeatherData
import at.nuceria.weatherdemo.databinding.FragmentForecastDetailBinding
import at.nuceria.weatherdemo.ui.WeatherViewModel
import at.nuceria.weatherdemo.util.epochToLocalTime
import at.nuceria.weatherdemo.util.to24hTime
import at.nuceria.weatherdemo.util.toLongDateString
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.math.roundToInt

@AndroidEntryPoint
class ForecastDetailFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prepareSharedElementTransition()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()
    }

    private val viewModel by activityViewModels<WeatherViewModel>()

    private var _binding: FragmentForecastDetailBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentForecastDetailBinding.inflate(inflater, container, false)
        val view = binding.root
        observeViewModel()
        return view
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            // repeatOnLifecycle launches the block in a new coroutine every time the
            // lifecycle is in the STARTED state (or above) and cancels it when it's STOPPED.
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                Timber.d("Collect weather flow")
                // Start listening for values.
                viewModel.weatherData.collect { onNewDataReceived(it) }
            }
        }
    }

    private fun onNewDataReceived(resource: Resource<out WeatherData?>) {
        Timber.d("onNewDataReceived")
        if (resource.data != null) {
            val dailyData =
                resource.data.forecastWeatherData.getOrNull(viewModel.selectedForecastPosition)
                    ?: return
            showData(dailyData)
        }
        // data is ready, start transition
        startPostponedEnterTransition()
    }

    private fun showData(dailyData: DailyWeatherData) {
        context?.let { context ->

            binding.currentWeatherIcon.setImageDrawable(
                ContextCompat.getDrawable(context, dailyData.condition.getDayIcon())
            )

            val timeStamp = dailyData.timeStamp.epochToLocalTime(dailyData.timezoneId)
            binding.day.text = timeStamp?.dayOfWeek()?.asText
            binding.date.text = timeStamp?.toLongDateString()

            val adapter = MiniWeatherItemAdapter()
            binding.list.adapter = adapter

            val list = listOf(
                MiniWeatherItemAdapter.WeatherItem(
                    R.drawable.chevron_double_up,
                    getString(R.string.maximum_temperature),
                    String.format(getString(R.string._c), dailyData.minTemperature.roundToInt())
                ),
                MiniWeatherItemAdapter.WeatherItem(
                    R.drawable.chevron_double_down,
                    getString(R.string.minimum_temperature),
                    String.format(getString(R.string._c), dailyData.minTemperature.roundToInt())
                ),
                MiniWeatherItemAdapter.WeatherItem(
                    R.drawable.water_outline,
                    getString(R.string.precipitation_percentage),
                    getString(
                        R.string.percent,
                        (dailyData.precipitationProbability * 100).roundToInt()
                            .toString()
                    )
                ),
                MiniWeatherItemAdapter.WeatherItem(
                    R.drawable.water_outline, getString(R.string.precipitation_amount),
                    getString(
                        R.string._mm,
                        if (dailyData.precipitationAmount == 0f) "0"
                        else String.format("%.1f", dailyData.precipitationAmount)
                    )
                ),
                MiniWeatherItemAdapter.WeatherItem(
                    R.drawable.weather_sunset_up,
                    getString(R.string.sunrise),
                    dailyData.localSunrise?.to24hTime() ?: ""
                ),
                MiniWeatherItemAdapter.WeatherItem(
                    R.drawable.weather_sunset_down,
                    getString(R.string.sunset),
                    dailyData.localSunset?.to24hTime() ?: ""
                ),
            )

            adapter.submitList(list)
        }
    }

    /**
     * Prepares the shared element transition from and back to the grid fragment.
     */
    private fun prepareSharedElementTransition() {
        val transition =
            TransitionInflater.from(context).inflateTransition(R.transition.shared_image)
        sharedElementEnterTransition = transition
        addSharedElementListener()

        // A similar mapping is set at the GridFragment with a setExitSharedElementCallback.
        setEnterSharedElementCallback(object : SharedElementCallback() {
            override fun onMapSharedElements(
                names: List<String?>,
                sharedElements: MutableMap<String?, View?>
            ) {
                // Map the incoming transition name (including the position) to the image in
                // this view.
                names.forEachIndexed { index, name ->
                    when {
                        name?.startsWith("weatherIcon") == true -> {
                            sharedElements[names[index]] = binding.currentWeatherIcon
                        }
                        name?.startsWith("day") == true -> {
                            sharedElements[names[index]] = binding.day
                        }
                        name?.startsWith("date") == true -> {
                            sharedElements[names[index]] = binding.date
                        }
                    }
                }
            }
        })
    }

    private fun addSharedElementListener() {
        (sharedElementEnterTransition as TransitionSet).addListener((object :
            TransitionListenerAdapter() {
            override fun onTransitionEnd(transition: Transition) {
                super.onTransitionEnd(transition)
                // start reveal animation
                binding.list.visibility = View.VISIBLE
            }
        }))
    }
}
