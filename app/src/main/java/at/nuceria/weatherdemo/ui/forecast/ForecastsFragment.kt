package at.nuceria.weatherdemo.ui.forecast

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import at.nuceria.weatherdemo.R
import at.nuceria.weatherdemo.data.model.WeatherData
import at.nuceria.weatherdemo.databinding.ForecastsFragmentBinding
import at.nuceria.weatherdemo.ui.WeatherViewModel
import at.nuceria.weatherdemo.util.MarginItemDecoration
import at.nuceria.weatherdemo.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class ForecastsFragment : Fragment() {

    private val viewModel by activityViewModels<WeatherViewModel>()

    private var _binding: ForecastsFragmentBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var adapter = DailyForecastAdapter()
    private val snapHelper = PagerSnapHelper()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ForecastsFragmentBinding.inflate(inflater, container, false)
        val view = binding.root
        observeViewModel()
        return view
    }


    private fun observeViewModel() {
        binding.forecastsRecycler.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.forecastsRecycler.adapter = adapter
        binding.forecastsRecycler.addItemDecoration(
            MarginItemDecoration(
                resources.getDimensionPixelSize(R.dimen.day_forecast_page_spacing)
            )
        )
        binding.viewOverflowPagerIndicator.attachToRecyclerView(binding.forecastsRecycler)
        snapHelper.attachToRecyclerView(binding.forecastsRecycler)

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

        if (resource.data != null) {
            showData(resource.data)
        } else {
            // TODO show an empty/placeholder view
        }

        if (resource is Resource.Loading && resource.data == null) {
            binding.progressbar.visibility = View.VISIBLE
        } else {
            binding.progressbar.visibility = View.GONE
        }

        if (resource is Resource.Error) {
//            showError(resource.error)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showData(weatherData: WeatherData) {
        adapter.submitList(weatherData.forecastWeatherData)
    }
}
