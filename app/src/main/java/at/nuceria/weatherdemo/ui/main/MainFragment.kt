package at.nuceria.weatherdemo.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
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
import at.nuceria.weatherdemo.location.LocationHandler
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

        binding.weatherResultView.more.setOnClickListener {
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

        binding.weatherResultView.forecastTiles.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.weatherResultView.forecastTiles.adapter = adapter
        binding.weatherResultView.forecastTiles.addItemDecoration(
            MarginItemDecoration(resources.getDimensionPixelSize(R.dimen.forecast_tile_spacing))
        )

        binding.weatherResultView.root.visibility = View.GONE
        binding.errorView.root.visibility = View.GONE

        requestPermissionLauncher.launch("android.permission.ACCESS_COARSE_LOCATION")
        return view
    }


    private fun onNewDataReceived(resource: Resource<out WeatherData?>) {
        Timber.d("onNewDataReceived")
        if (resource.data != null) {
            binding.weatherResultView.root.visibility = View.VISIBLE
            showData(resource.data)
        } else {
            binding.weatherResultView.root.visibility = View.GONE
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

        if (resource is Resource.Error && resource.data == null) {
            binding.errorView.root.visibility = View.VISIBLE
            showError(resource.error)
        } else {
            binding.errorView.root.visibility = View.GONE
        }
    }

    private fun showData(weatherData: WeatherData) {
        weatherData.currentWeatherData.let { data ->
            binding.weatherResultView.run {
                root.visibility = View.VISIBLE
                // we don't want to show decimals for the values
                temperature.text = data.temperature.roundToInt().toString()
                wind.windSpeed.text = data.windSpeed.roundToInt().toString()
                wind.windDirection.rotation = data.windDegrees.toFloat() + 180 // the icon is "upside down"
                weatherDescription.text = data.localizedDescription.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(Locale.ENGLISH) else it.toString()
                }
                data.condition.run { setWeatherConditionIcon(getDayIcon()) }
            }
        }

        weatherData.forecastWeatherData.run {
            adapter.submitList(this)


        }
    }

    private fun setWeatherConditionIcon(@DrawableRes int: Int) {
        context?.let {
            binding.weatherResultView.currentWeatherIcon.setImageDrawable(
                ContextCompat.getDrawable(
                    it,
                    int
                )
            )
        }
    }

    private fun showError(exception: Throwable?) {
        val message = when (exception) {
            is LocationHandler.LocationNullError -> getString(R.string.your_location_could_not_be_determined)
            else -> "Something went wrong. Please try again later"
        }

        showError(message)
    }

    private fun showError(message: String) {
        Timber.e("ERROR: $message")
        // TODO create error view or show dialog
        // show "you are offline" in case there is no network and the request failed
        binding.errorView.message.text = message
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Register the permissions callback, which handles the user's response to the
    // system permissions dialog. Save the return value, an instance of
    // ActivityResultLauncher, as an instance variable.
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                Timber.d("Permission Granted")
                lifecycleScope.launch {
                    // repeatOnLifecycle launches the block in a new coroutine every time the
                    // lifecycle is in the STARTED state (or above) and cancels it when it's STOPPED.
                    repeatOnLifecycle(Lifecycle.State.STARTED) {
                        // Trigger the flow and start listening for values.
                        viewModel.retrieveWeatherForCurrentLocation()
                        viewModel.weatherData.collect { onNewDataReceived(it) }
                    }
                }
            } else {
                // Explain to the user that the feature is unavailable because the
                // features requires a permission that the user has denied. At the
                // same time, respect the user's decision. Don't link to system
                // settings in an effort to convince the user to change their
                // decision.
                showError(LocationHandler.MissingLocationPermissionError())
            }
        }

}
