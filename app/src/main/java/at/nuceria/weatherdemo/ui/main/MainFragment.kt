package at.nuceria.weatherdemo.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import at.nuceria.weatherdemo.data.Result
import at.nuceria.weatherdemo.data.remote.response.WeatherResponse
import at.nuceria.weatherdemo.databinding.MainFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import kotlin.math.roundToInt

@AndroidEntryPoint
class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private val viewModel by viewModels<MainViewModel>()

    private var _binding: MainFragmentBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MainFragmentBinding.inflate(inflater, container, false)
        val view = binding.root


        observeViewModel()
        return view
    }

    private fun observeViewModel() {
        viewModel.weatherResult.observe(viewLifecycleOwner) {
            when (it) {
                is Result.Error -> showError(it.exception)
                is Result.Loading -> showLoading()
                is Result.Success -> showData(it.data)
            }
        }
    }


    private fun showLoading() {
        binding.loadingInfo.visibility = View.VISIBLE
        binding.currentWeatherGroup.visibility = View.GONE


    }

    private fun showData(weatherResult: WeatherResponse) {
        binding.loadingInfo.visibility = View.GONE
        weatherResult.currentWeather.run {
            binding.currentWeatherGroup.visibility = View.VISIBLE
            // we don't want to show decimals for the values
            binding.temperature.text = temp.roundToInt().toString()
            binding.windSpeed.text = windSpeed.roundToInt().toString()
            binding.windDirection.rotation = windDegrees.toFloat()
            weatherConditions.firstOrNull()?.run {
                binding.weatherDescription.text = description.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(Locale.ENGLISH) else it.toString()
                }
                setWeatherConditionIcon(getDayIcon())
            }
        }

    }

    private fun setWeatherConditionIcon(@DrawableRes int: Int) {
        context?.let {
            binding.currentWeatherIcon.setImageDrawable(ContextCompat.getDrawable(it, int))

        }

    }

    private fun showError(exception: Exception) {
        binding.loadingInfo.visibility = View.GONE
        binding.currentWeatherGroup.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
