package at.nuceria.weatherdemo.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import at.nuceria.weatherdemo.R
import at.nuceria.weatherdemo.data.model.WeatherData
import at.nuceria.weatherdemo.databinding.MainFragmentBinding
import at.nuceria.weatherdemo.ui.WeatherViewModel
import at.nuceria.weatherdemo.ui.forecast.ForecastsFragment
import at.nuceria.weatherdemo.util.MarginItemDecoration
import at.nuceria.weatherdemo.util.Resource
import at.nuceria.weatherdemo.util.getDayIcon
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*
import kotlin.math.roundToInt

@AndroidEntryPoint
class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private val viewModel by activityViewModels<WeatherViewModel>()

    private var _binding: MainFragmentBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val adapter = ForeCastTileAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MainFragmentBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.more.setOnClickListener {
            if (isAdded) {
                parentFragmentManager.commit {
                    setCustomAnimations(
                        R.anim.slide_in_bottom,
                        R.anim.slide_out_top,
                        R.anim.slide_in_top,
                        R.anim.slide_out_bottom
                    )
                    replace(R.id.container, ForecastsFragment())
                    addToBackStack(null)
                }
            }
        }

        binding.forecastTiles.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.forecastTiles.adapter = adapter
        binding.forecastTiles.addItemDecoration(
            MarginItemDecoration(resources.getDimensionPixelSize(R.dimen.forecast_tile_spacing))
        )

        observeViewModel()
        return view
    }

    private fun observeViewModel() {

        Timber.d("Collect weather flow")

        lifecycleScope.launch {
            // repeatOnLifecycle launches the block in a new coroutine every time the
            // lifecycle is in the STARTED state (or above) and cancels it when it's STOPPED.
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Trigger the flow and start listening for values.
                // Note that this happens when lifecycle is STARTED and stops
                // collecting when the lifecycle is STOPPED
                viewModel.weatherData.collect { onNewDataReceived(it) }
            }
        }
    }

    private fun onNewDataReceived(resource: Resource<out WeatherData?>) {
        if (resource.data != null) {
            showData(resource.data)
        } else {
            // TODO show an empty/placeholder view
        }

        if (resource is Resource.Loading) {
            if (resource.data != null) {
                binding.loadingInfo.visibility = View.VISIBLE
            } else {
                binding.progressbar.visibility = View.VISIBLE
            }
        } else {
            binding.loadingInfo.visibility = View.GONE
            binding.progressbar.visibility = View.GONE
        }

        if (resource is Resource.Error) {
            showError(resource.error)
        }
    }

    private fun showData(weatherData: WeatherData) {
        weatherData.currentWeatherData.run {
            binding.currentWeatherGroup.visibility = View.VISIBLE
            // we don't want to show decimals for the values
            binding.temperature.text = temperature.roundToInt().toString()
            binding.windSpeed.text = windSpeed.roundToInt().toString()
            binding.windDirection.rotation = windDegrees.toFloat()
            binding.weatherDescription.text = localizedDescription.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(Locale.ENGLISH) else it.toString()
            }
            condition.run { setWeatherConditionIcon(getDayIcon()) }
        }

        weatherData.forecastWeatherData.run {
            adapter.submitList(this)


        }
    }

    private fun setWeatherConditionIcon(@DrawableRes int: Int) {
        context?.let {
            binding.currentWeatherIcon.setImageDrawable(ContextCompat.getDrawable(it, int))
        }
    }

    private fun showError(exception: Throwable?) {
        // TODO create error view or show dialog
        // show "you are offline" in case there is no network and the request failed
        binding.currentWeatherGroup.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
